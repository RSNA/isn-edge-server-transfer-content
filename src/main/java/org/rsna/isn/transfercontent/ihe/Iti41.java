/* Copyright (c) <2017>, <Radiological Society of North America>
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the <RSNA> nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 * 
 * 
 * 3.1.0
 *      03/04/2013: Wyatt Tellis
 *           * Added check for report status as part of "send_on_complete" feature
 * 
 */
package org.rsna.isn.transfercontent.ihe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.client.Options;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.dcm4che2.util.UIDUtils;
import org.eclipse.emf.common.util.EList;
import org.openhealthtools.ihe.atna.auditor.XDSAuditor;
import org.openhealthtools.ihe.common.hl7v2.CX;
import org.openhealthtools.ihe.common.hl7v2.CXi;
import org.openhealthtools.ihe.common.hl7v2.Hl7v2Factory;
import org.openhealthtools.ihe.common.hl7v2.XCN;
import org.openhealthtools.ihe.common.hl7v2.XON;
import org.openhealthtools.ihe.common.ws.IHESOAP12Sender;
import org.openhealthtools.ihe.xds.document.DocumentDescriptor;
import org.openhealthtools.ihe.xds.document.XDSDocument;
import org.openhealthtools.ihe.xds.document.XDSDocumentFromByteArray;
import org.openhealthtools.ihe.xds.metadata.AuthorType;
import org.openhealthtools.ihe.xds.metadata.CodedMetadataType;
import org.openhealthtools.ihe.xds.metadata.DocumentEntryType;
import org.openhealthtools.ihe.xds.metadata.InternationalStringType;
import org.openhealthtools.ihe.xds.metadata.LocalizedStringType;
import org.openhealthtools.ihe.xds.metadata.MetadataFactory;
import org.openhealthtools.ihe.xds.metadata.SubmissionSetType;
import org.openhealthtools.ihe.xds.metadata.constants.IdentifierTypeCodeConstants;
import org.openhealthtools.ihe.xds.metadata.transform.ByteArrayProvideAndRegisterDocumentSetTransformer;
import org.openhealthtools.ihe.xds.response.XDSErrorListType;
import org.openhealthtools.ihe.xds.response.XDSErrorType;
import org.openhealthtools.ihe.xds.response.XDSResponseType;
import org.openhealthtools.ihe.xds.response.XDSStatusType;
import org.openhealthtools.ihe.xds.source.B_Source;
import org.openhealthtools.ihe.xds.source.SubmitTransactionData;
import org.rsna.isn.dao.ConfigurationDao;
import org.rsna.isn.domain.Author;
import org.rsna.isn.domain.DicomObject;
import org.rsna.isn.domain.DicomSeries;
import org.rsna.isn.domain.DicomStudy;
import org.rsna.isn.domain.Exam;
import static org.rsna.isn.domain.Exam.*;
import org.rsna.isn.domain.Job;
import org.rsna.isn.util.Constants;
import org.rsna.isn.util.Environment;
import org.rsna.isn.util.Reports;

/**
 * This class implements the ITI-41 (Submit and register document set)
 * transaction. Note: the "iti41-repository-unique-id", "iti41-source-id", and
 * "iti41-endpoint-url" properties must be set in the configurations table of
 * the RSNA database.
 *
 * @author Wyatt Tellis
 * @author Clifton Li
 * @version 5.0.0
 * @since 3.1.0
 *
 */
public class Iti41
{
	private static final Logger logger = Logger.getLogger(Iti41.class);

	private static final MetadataFactory xdsFactory = MetadataFactory.eINSTANCE;

	private static final Hl7v2Factory hl7Factory = Hl7v2Factory.eINSTANCE;

	private static final String DICOM_UID_REG_UID = "1.2.840.10008.2.6.1";
        
	private static final DocumentDescriptor PDF_DESCRIPTOR =
			new DocumentDescriptor("PDF", "application/pdf");


	private static String sourceId;

        private static String siteAssigningAuthority;
        
	private static URI imgEndpoint;

        private static URI docEndpoint;
        
	private static long timeout;

	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	private final DicomStudy study;

	private final Exam exam;

	private final Job job;
        
        private static String TEMPLATE = "pdf-template.pdf";
        
	public static void init() throws Exception
	{
		ConfigurationDao dao = new ConfigurationDao();

		sourceId = dao.getConfiguration("iti41-source-id");
                
                //Update sourceID so all ISN instances have unique IDs
                if (!sourceId.startsWith(Constants.RSNA_ISN_ROOT_SOURCE_ID))    
                {       
                        //randomUID() shortens the UID to less than 64 characters per OID spec
                        sourceId = randomUID(Constants.RSNA_ISN_ROOT_SOURCE_ID);
                        dao.updateSourceId(sourceId);
                        logger.info("Source id set to: " + sourceId);
                }

                siteAssigningAuthority = dao.getConfiguration("site-assigning-authority");
 		if (!siteAssigningAuthority.startsWith(Constants.SITE_ASSIGNING_AUTHORITY))
                {       
                        //randomUID() shortens the UID to less than 64 characters per OID spec
                        siteAssigningAuthority = randomUID(Constants.SITE_ASSIGNING_AUTHORITY);
                        
                        dao.updateConfiguration("site-assigning-authority",siteAssigningAuthority);
                        logger.info("siteAssigningAuthority set to: " + siteAssigningAuthority);
                }
                
		String imgUri = dao.getConfiguration("iti41-img-endpoint-uri");
		if (StringUtils.isBlank(imgUri))
			throw new ExceptionInInitializerError("iti41-img-endpoint-uri");
		imgEndpoint = new URI(imgUri);
		logger.info("Image Document Source Endpoint URI set to: " + imgEndpoint);

		String docUri = dao.getConfiguration("iti41-doc-endpoint-uri");
		if (StringUtils.isBlank(docUri))
			throw new ExceptionInInitializerError("iti41-doc-endpoint-uri");
		docEndpoint = new URI(docUri);
		logger.info("Document Source Endpoint URI set to: " + docEndpoint);
		String t = dao.getConfiguration("iti41-socket-timeout");
		timeout = NumberUtils.toLong(t, 120) * 1000;

		logger.info("Timeout set to: " + timeout + " ms");



		XDSAuditor.getAuditor().getConfig().setAuditorEnabled(false);

		//
		// Load Axis 2 configuration (there has got be a better way)
		//
		Class<Iti41> cls = Iti41.class;
		String pkg = cls.getPackage().getName().replace('.', '/');
		String path = "/" + pkg + "/axis2.xml";
		InputStream in = cls.getResourceAsStream(path);


		File tmpDir = Environment.getTmpDir();
		File axis2Xml = new File(tmpDir, "axis2.xml");
		FileOutputStream out = new FileOutputStream(axis2Xml);
		IOUtils.copy(in, out);
		in.close();
		out.close();

		System.setProperty("axis2.xml", axis2Xml.getCanonicalPath());

		File soapDir = new File(tmpDir, "soap");
		soapDir.mkdirs();
		System.setProperty("ihe.soap.tmpdir", soapDir.getCanonicalPath());

		System.setProperty("use.http.chunking", "true");
	}

	/**
	 * Create an instance of this class.
	 *
	 * @param study The DICOM study that will comprise the submission set. It is
	 * assumed the files for this study are stored at:
	 * ${rsna.root}/tmp/${jobId}/studies/${studyUid}
	 *
	 * @throws IllegalArgumentException If the patient associated with this
	 * study does not have an RSNA ID.
	 */
	public Iti41(DicomStudy study)
	{
		this.study = study;

		this.job = study.getJob();

		this.exam = job.getExam();
	}

	/**
	 * Perform the actual submission to the document repository.
	 *
	 * @param debugFile An optional file to which to dump the submission set
	 * metadata
	 * @throws Exception If there was an error processing the submission set.
	 */
        
        public void submitReport(File debugFile) throws Exception
        {
                SubmitTransactionData tx = new SubmitTransactionData();

		//
		// Add entry for report
		//
		String examStatus = exam.getStatus();
		if (FINALIZED.equals(examStatus))
		{
			String report = exam.getReport();
			if (StringUtils.isNotBlank(report))
			{
                                ConfigurationDao dao = new ConfigurationDao();

                                String pfdTemplate = dao.getConfiguration("pdf-template");
                                String templatePath = Environment.getConfDir().getAbsolutePath() + "/" + TEMPLATE;
                                
                                byte[] reportBa = null;

                                if (Boolean.parseBoolean(pfdTemplate) && new File(templatePath).isFile())
                                {
                                        reportBa = Reports.useTemplate(exam);
                                }
                                else
                                {
                                        //simple formatting
                                        reportBa = Reports.generate(exam);
                                }
                                   
                                XDSDocument reportDoc = new XDSDocumentFromByteArray(PDF_DESCRIPTOR, reportBa);
                                
				String reportUuid = tx.addDocument(reportDoc);
				DocumentEntryType docEntry = tx.getDocumentEntry(reportUuid);
				initDocEntry(docEntry);

                                docEntryReports(docEntry);
                                
                                submitTransaction(tx,debugFile,"report");
			}
			else
			{
				logger.warn("No report text associated with " + exam + " for " + job);
			}
		}
		else if(job.isSendOnComplete())
		{
			logger.info("Sending " + exam + " for " + job + " without a report");
		}
		else if(NON_REPORTABLE.equals(examStatus))
		{
			logger.info("No report sent for " + exam + " for " + job 
					+ " because the exam has a status of " + NON_REPORTABLE);
		}
		else
		{
			throw new RuntimeException("Invalid exam status: " + examStatus);
		}
        }
        
	public void submitDocuments(File debugFile) throws Exception
	{
		SubmitTransactionData tx = new SubmitTransactionData();
                
		// Add entries for images
		//
		for (DicomSeries series : study.getSeries().values())
		{
			for (DicomObject object : series.getObjects().values())
			{
				File dcmFile = object.getFile();

				XDSDocument dcmDoc = new LazyLoadedXdsDocument(DocumentDescriptor.DICOM, dcmFile);
				String dcmUuid = tx.addDocument(dcmDoc);
				DocumentEntryType dcmEntry = tx.getDocumentEntry(dcmUuid);
				initDocEntry(dcmEntry);

				docEntryImages(dcmEntry,series);
			}
		}
                
                submitTransaction(tx,debugFile,"image");
        }


        protected void submitTransaction(SubmitTransactionData tx, File debugFile, String srcType) throws Exception
        {
            	//
		// Initialize submission set metadata
		//
		SubmissionSetType subSet = tx.getSubmissionSet();
		AuthorType author = getAuthor();
		if (author != null)
			subSet.setAuthor(author);

		CodedMetadataType contentType = xdsFactory.createCodedMetadataType();
		contentType.setCode("Imaging Exam");
		contentType.setDisplayName(inStr("Imaging Exam"));
                contentType.setSchemeName("1.3.6.1.4.1.19376.3.840.1.1.3");
		subSet.setContentTypeCode(contentType);

		subSet.setPatientId(getRsnaId());
		subSet.setSourceId(sourceId);
                subSet.setSubmissionTime(getGmt(new Date()));
                
		subSet.setUniqueId(UIDUtils.createUID());

		if (debugFile != null)
		{
			// tx.saveMetadataToFile does not close the FOS, which means you
			// can't delete the file when you're done processing the job
			// tx.saveMetadataToFile(debugFile.getCanonicalPath());

			ByteArrayProvideAndRegisterDocumentSetTransformer setTransformer =
					new ByteArrayProvideAndRegisterDocumentSetTransformer();
			setTransformer.transform(tx.getMetadata());

			FileOutputStream fos = null;
			try
			{
				fos = new FileOutputStream(debugFile);
				fos.write(setTransformer.getMetadataByteArray());
			}
			finally
			{
				IOUtils.closeQuietly(fos);
			}
		}
                
                B_Source reg;
                if (srcType.equals("image"))
                {
                         reg = new B_Source(imgEndpoint);
                }
                else if (srcType.equals("report"))
                {
                         reg = new B_Source(docEndpoint);
                }
                else
                {
			throw new IllegalArgumentException("Invalid srcType value in submitTransaction.");                       
                }
		IHESOAP12Sender sender = (IHESOAP12Sender) reg.getSenderClient().getSender();

		Options options = sender.getAxisServiceClient().getOptions();
		options.setTimeOutInMilliSeconds(timeout);
                options.setProperty(AddressingConstants.ADD_MUST_UNDERSTAND_TO_ADDRESSING_HEADERS, Boolean.TRUE); 
                                
		XDSResponseType resp = reg.submit(tx);

		XDSStatusType status = resp.getStatus();
		int code = status.getValue();

		if (code != XDSStatusType.SUCCESS)
		{
			XDSErrorListType errors = resp.getErrorList();
			List<XDSErrorType> errorList = errors.getError();

			String chMsg = status.getLiteral();
			for (XDSErrorType error : errorList)
			{
				chMsg = error.getCodeContext();
				chMsg = StringUtils.removeStart(chMsg,
						"com.axonmed.xds.registry.exceptions.RegistryException: ");

				break;
			}


			throw new ClearinghouseException("Submission of study "
					+ study.getStudyUid()
					+ " failed. Clearinghouse returned error: " + chMsg);

		}
        }
	@SuppressWarnings(
	{
		"unchecked", "rawtypes"
	})
	private AuthorType getAuthor()
	{
		XCN legalAuthenticator = getLegalAuthenticator();
		if (legalAuthenticator != null)
		{
			AuthorType author = xdsFactory.createAuthorType();
			author.setAuthorPerson(legalAuthenticator);
                        


			XON institution = Hl7v2Factory.eINSTANCE.createXON();
			institution.setOrganizationName("RSNA ISN");

			EList institutions = author.getAuthorInstitution();
			institutions.add(institution);
                       

			return author;
		}
		else
		{
			return null;
		}

	}

	private XCN getLegalAuthenticator()
	{
		Author signer = null; //exam.getSigner();
		if (signer != null)
		{
			XCN legalAuthenticator = hl7Factory.createXCN();

			legalAuthenticator.setFamilyName(signer.getLastName());
			legalAuthenticator.setGivenName(signer.getFirstName());
			legalAuthenticator.setIdNumber(signer.getId());
                        legalAuthenticator.setAssigningAuthorityUniversalId(Constants.RSNA_ISN_ASSIGNING_AUTHORITY);
			legalAuthenticator.setAssigningAuthorityUniversalIdType("ISO");

			return legalAuthenticator;
		}
		else
		{
			XCN legalAuthenticator = hl7Factory.createXCN();

			legalAuthenticator.setFamilyName("RSNA ISN");
			legalAuthenticator.setGivenName("RSNA ISN");
			legalAuthenticator.setIdNumber("RSNA ISN");
                        legalAuthenticator.setAssigningAuthorityUniversalId(Constants.RSNA_ISN_ASSIGNING_AUTHORITY);
			legalAuthenticator.setAssigningAuthorityUniversalIdType("ISO");

			return legalAuthenticator;
		}
	}

	private CX getRsnaId()
	{
		CX rsnaId = hl7Factory.createCX();
		
                rsnaId.setIdNumber(job.getGlobalId());
		rsnaId.setAssigningAuthorityUniversalId(job.getGlobalAA());
                rsnaId.setAssigningAuthorityUniversalIdType(Constants.RSNA_UNIVERSAL_ID_TYPE);

		return rsnaId;
	}

	private CodedMetadataType getClassCode()
	{
		CodedMetadataType classCode = xdsFactory.createCodedMetadataType();
		classCode.setCode("IMAGES");
		classCode.setDisplayName(inStr("Images"));
		classCode.setSchemeName("1.3.6.1.4.1.19376.1.2.6.1");

		return classCode;
	}

	protected CodedMetadataType getConfidentialityCode()
	{
		CodedMetadataType confidentialityCode = xdsFactory.createCodedMetadataType();
		confidentialityCode.setCode("V");
		confidentialityCode.setSchemeName("2.16.840.1.113883.5.25");
                confidentialityCode.setDisplayName(inStr("very restricted"));
                
		return confidentialityCode;
	}

	private String getGmt(Date date)
	{
		return sdf.format(date);
	}

	private CodedMetadataType getHealthcareFacilityTypeCode()
	{
		CodedMetadataType healthcareFacilityTypeCode = xdsFactory.createCodedMetadataType();
		healthcareFacilityTypeCode.setCode("33022008");
		healthcareFacilityTypeCode.setDisplayName(inStr("Hospital-based outpatient clinic or department--OTHER-NOT LISTED"));
		healthcareFacilityTypeCode.setSchemeName("2.16.840.1.113883.3.88.12.80.67");

		return healthcareFacilityTypeCode;
	}

	private CodedMetadataType getPracticeSettingCode()
	{
		CodedMetadataType practiceSettingCode = xdsFactory.createCodedMetadataType();
		practiceSettingCode.setCode("R-3027B");
		practiceSettingCode.setDisplayName(inStr("Radiology"));
                practiceSettingCode.setSchemeName("2.16.840.1.113883.6.96");
                
		return practiceSettingCode;
	}

	private CodedMetadataType getTypeCode()
	{
		CodedMetadataType typeCode = xdsFactory.createCodedMetadataType();
                
                typeCode.setCode("Imaging Exam");
		typeCode.setDisplayName(inStr("Imaging Exam"));
                typeCode.setSchemeName("1.3.6.1.4.1.19376.3.840.1.1.2");
                
		return typeCode;
	}

	@SuppressWarnings("unchecked")
	protected void initDocEntry(DocumentEntryType docEntry)
	{
		AuthorType author = getAuthor();
		if (author != null)
                {
			docEntry.getAuthors().add(author);
                }
                
		docEntry.setClassCode(getClassCode());
		docEntry.getConfidentialityCode().add(getConfidentialityCode());
		docEntry.setCreationTime(getGmt(new Date()));
		docEntry.setHealthCareFacilityTypeCode(getHealthcareFacilityTypeCode());
		docEntry.setLanguageCode("en-US");

		//XCN legalAuthenticator = getLegalAuthenticator();
		//if (legalAuthenticator != null)
		//    docEntry.setLegalAuthenticator(legalAuthenticator);

		docEntry.setPatientId(getRsnaId());
		docEntry.setPracticeSettingCode(getPracticeSettingCode());
		docEntry.setServiceStartTime(getGmt(study.getStudyDateTime()));
		docEntry.setServiceStopTime(getGmt(study.getStudyDateTime()));
		docEntry.setSourcePatientId(getRsnaId());
		docEntry.setTitle(inStr(study.getStudyDescription()));
		docEntry.setTypeCode(getTypeCode());
                
                CXi accNum = hl7Factory.createCXi();
                accNum.setIdNumber(exam.getAccNum());
                accNum.setAssigningAuthorityUniversalId(siteAssigningAuthority);
                //accNum.setAssigningAuthorityName("RSNA-ISN");
                accNum.setIdentifierTypeCode(IdentifierTypeCodeConstants.ACCESSION_NUMBER);
                accNum.setAssigningAuthorityUniversalIdType("ISO^");

                docEntry.getReferenceIdList().add(accNum);
                accNum.toString();
                
                
	}

        protected void docEntryImages(DocumentEntryType dcmEntry, DicomSeries series)
        {
            	for (DicomObject object : series.getObjects().values())
		{
                        series.getObjects();
                        CodedMetadataType dcmFmt = xdsFactory.createCodedMetadataType();
                        String sopClass = object.getSopClassUid();                   
                        dcmFmt.setCode(sopClass);
                        dcmFmt.setDisplayName(inStr(sopClass));
                        dcmFmt.setSchemeName(DICOM_UID_REG_UID);             
                        //dcmFmt.setSchemeUUID(DICOM_UID_REG_UID);
                        dcmEntry.setFormatCode(dcmFmt);
                   
                        CodedMetadataType dcmEventCode = xdsFactory.createCodedMetadataType();

                        dcmEventCode.setCode("OT");
                        dcmEventCode.setSchemeName("DCM");
                        dcmEventCode.setDisplayName(inStr("Other"));                
                        dcmEntry.getEventCode().add(dcmEventCode);
                        
                        dcmEntry.setMimeType(DocumentDescriptor.DICOM.getMimeType());

                        dcmEntry.setUniqueId(object.getSopInstanceUid());  
                }
        }
        
        protected void docEntryReports(DocumentEntryType docEntry)
        {
                CodedMetadataType classCode = xdsFactory.createCodedMetadataType();
                classCode.setCode("REPORTS");
                classCode.setDisplayName(inStr("Report"));
                classCode.setSchemeName("1.3.6.1.4.1.19376.1.2.6.1");
                docEntry.setClassCode(classCode);

                CodedMetadataType formatCode = xdsFactory.createCodedMetadataType();
                formatCode.setCode("urn:ihe:rad:PDF");
                formatCode.setDisplayName(inStr("urn:ihe:rad:PDF"));
                formatCode.setSchemeName("1.3.6.1.4.1.19376.1.2.3");
                docEntry.setFormatCode(formatCode);

                CodedMetadataType dcmEventCode = xdsFactory.createCodedMetadataType();

                dcmEventCode.setCode("OT");
                dcmEventCode.setSchemeName("DCM");
                dcmEventCode.setDisplayName(inStr("Other"));
                docEntry.getEventCode().add(dcmEventCode);

                docEntry.setMimeType(PDF_DESCRIPTOR.getMimeType());

                docEntry.setUniqueId(UIDUtils.createUID());            
        }
	@SuppressWarnings("unchecked")
	protected static InternationalStringType inStr(String value)
	{
		LocalizedStringType lzStr = xdsFactory.createLocalizedStringType();
		lzStr.setCharset("UTF-8");
		lzStr.setLang("en-US");
		lzStr.setValue(value);

		InternationalStringType inStr = xdsFactory.createInternationalStringType();
		inStr.getLocalizedString().add(lzStr);

		return inStr;
	}
        
        private static String randomUID(String baseUID)
        {
                String randomUID = UIDUtils.createUID();
                int UIDlength = randomUID.length();

                String uidTail = randomUID.substring(15, UIDlength);
                
                return baseUID + "." + uidTail;
        }

}
