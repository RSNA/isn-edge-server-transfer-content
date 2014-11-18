/* Copyright (c) <2014>, <Radiological Society of North America>
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
 */

package org.rsna.isn.transfercontent.ihe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.axis2.client.Options;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.dcm4che2.data.UID;
import org.dcm4che2.util.UIDUtils;
import org.eclipse.emf.common.util.EList;
import org.openhealthtools.ihe.atna.auditor.XDSAuditor;
import org.openhealthtools.ihe.common.hl7v2.CX;
import org.openhealthtools.ihe.common.hl7v2.Hl7v2Factory;
import org.openhealthtools.ihe.common.hl7v2.SourcePatientInfoType;
import org.openhealthtools.ihe.common.hl7v2.XCN;
import org.openhealthtools.ihe.common.hl7v2.XON;
import org.openhealthtools.ihe.common.hl7v2.XPN;
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
import org.openhealthtools.ihe.xds.response.XDSErrorListType;
import org.openhealthtools.ihe.xds.response.XDSErrorType;
import org.openhealthtools.ihe.xds.response.XDSResponseType;
import org.openhealthtools.ihe.xds.response.XDSStatusType;
import org.openhealthtools.ihe.xds.source.B_Source;
import org.openhealthtools.ihe.xds.source.SubmitTransactionData;
import org.rsna.isn.dao.ConfigurationDao;
import org.rsna.isn.domain.Author;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.VR;
import org.rsna.isn.util.Constants;
import org.rsna.isn.util.Environment;

/**
 * This class is a copy of the ITI-41 class with 
 * modifications to support the XdsTest class. 
 * This class supports the ability to send DICOM 
 * objects in memory instead of a file.
 *
 * @author Clifton Li
 * @version 3.2.0
 *
 */
public class Iti41XdsTest
{
	private static final Logger logger = Logger.getLogger(Iti41.class);

	private static final MetadataFactory xdsFactory = MetadataFactory.eINSTANCE;

	private static final Hl7v2Factory hl7Factory = Hl7v2Factory.eINSTANCE;

	private static final String DICOM_UID_REG_UID = "1.2.840.10008.2.6.1";

	private static final DocumentDescriptor KOS_DESCRIPTOR =
			new DocumentDescriptor("KOS", "application/dicom-kos");

	private static final DocumentDescriptor TEXT_DESCRIPTOR =
			new DocumentDescriptor("TEXT", "text/plain");

	private static String sourceId;

	private static URI endpoint;

	private static long timeout;

	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

        private final String singleUsePatientId;
        
        public Iti41XdsTest(String  singleUsePatientId)
	{
		this.singleUsePatientId = singleUsePatientId;
	}
        
        
	public static void init() throws Exception
	{
		ConfigurationDao dao = new ConfigurationDao();

		sourceId = dao.getConfiguration("iti41-source-id");
		if (StringUtils.isBlank(sourceId))
			throw new ExceptionInInitializerError("iti41-source-id is blank");

		logger.info("Source id set to: " + sourceId);




		String uri = dao.getConfiguration("iti41-endpoint-uri");
		if (StringUtils.isBlank(uri))
			throw new ExceptionInInitializerError("iti41-endpoint-uri");
		endpoint = new URI(uri);

		logger.info("Endpoint URI set to: " + endpoint);


		String t = dao.getConfiguration("iti41-socket-timeout");
		timeout = NumberUtils.toLong(t, 120) * 1000;

		logger.info("Timeout set to: " + timeout + " ms");



		XDSAuditor.getAuditor().getConfig().setAuditorEnabled(false);

		//
		// Load Axis 2 configuration (there has got be a better way)
		//
		Class<Iti41XdsTest> cls = Iti41XdsTest.class;
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
	 * Perform the actual submission to the document repository.
	 *
	 */
	public void submitDocuments(byte[] dcmBa,byte[] kosBa, DicomObject dcm, DicomObject kos) throws Exception
	{
		SubmitTransactionData tx = new SubmitTransactionData();

                String report = "This is a test report.";

                XDSDocument reportDoc =
                                new XDSDocumentFromByteArray(TEXT_DESCRIPTOR, report.getBytes("UTF-8"));

                    String reportUuid = tx.addDocument(reportDoc);
                DocumentEntryType reportEntry = tx.getDocumentEntry(reportUuid);
                initDocEntry(reportEntry);


                CodedMetadataType reportFmt = xdsFactory.createCodedMetadataType();
                reportFmt.setCode("TEXT");
                reportFmt.setDisplayName(inStr("TEXT"));
                reportFmt.setSchemeName("RSNA-ISN");
                reportEntry.setFormatCode(reportFmt);

                reportEntry.setMimeType(TEXT_DESCRIPTOR.getMimeType());

                reportEntry.setUniqueId(UIDUtils.createUID());

		//
		// Add entry for KOS
		//

		XDSDocument kosDoc = new XDSDocumentFromByteArray(KOS_DESCRIPTOR, kosBa);
		String kosUuid = tx.addDocument(kosDoc);
		DocumentEntryType kosEntry = tx.getDocumentEntry(kosUuid);
		initDocEntry(kosEntry);

		CodedMetadataType kosFmt = xdsFactory.createCodedMetadataType();
		kosFmt.setCode(UID.KeyObjectSelectionDocumentStorage);
		kosFmt.setDisplayName(inStr(UID.KeyObjectSelectionDocumentStorage));
		kosFmt.setSchemeName(DICOM_UID_REG_UID);
		kosFmt.setSchemeUUID(DICOM_UID_REG_UID);
		kosEntry.setFormatCode(kosFmt);

		kosEntry.setMimeType(KOS_DESCRIPTOR.getMimeType());

		//kosEntry.setUniqueId(kos.getSopInstanceUid());
                kosEntry.setUniqueId(kos.getString(Tag.SOPInstanceUID, VR.UI));

		//
		// Add entries for images
		//

                XDSDocument dcmDoc = new XDSDocumentFromByteArray(DocumentDescriptor.DICOM, dcmBa);
                String dcmUuid = tx.addDocument(dcmDoc);
                DocumentEntryType dcmEntry = tx.getDocumentEntry(dcmUuid);
                initDocEntry(dcmEntry);

                CodedMetadataType dcmFmt = xdsFactory.createCodedMetadataType();
 
                String sopClass = dcm.getString(Tag.SOPClassUID, VR.UI);
                dcmFmt.setCode(sopClass);
                dcmFmt.setDisplayName(inStr(sopClass));
                dcmFmt.setSchemeName(DICOM_UID_REG_UID);
                dcmFmt.setSchemeUUID(DICOM_UID_REG_UID);
                dcmEntry.setFormatCode(dcmFmt);

                dcmEntry.setMimeType(DocumentDescriptor.DICOM.getMimeType());

                dcmEntry.setUniqueId(dcm.getString(Tag.SOPInstanceUID, VR.UI));

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
		contentType.setSchemeName("RSNA-ISN");
		subSet.setContentTypeCode(contentType);

		subSet.setPatientId(getRsnaId());
		subSet.setSourceId(sourceId);
		subSet.setSubmissionTime(getGmt(new Date()));
		subSet.setUniqueId(UIDUtils.createUID());
                
		B_Source reg = new B_Source(endpoint);
		IHESOAP12Sender sender = (IHESOAP12Sender) reg.getSenderClient().getSender();

		Options options = sender.getAxisServiceClient().getOptions();
		options.setTimeOutInMilliSeconds(timeout);

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
			legalAuthenticator.setAssigningAuthorityUniversalIdType("ISO");

			return legalAuthenticator;
		}
		else
		{
			XCN legalAuthenticator = hl7Factory.createXCN();

			legalAuthenticator.setFamilyName("RSNA ISN");
			legalAuthenticator.setGivenName("RSNA ISN");
			legalAuthenticator.setIdNumber("RSNA ISN");
			legalAuthenticator.setAssigningAuthorityUniversalIdType("ISO");

			return legalAuthenticator;
		}
	}

	private CX getRsnaId()
	{
		CX rsnaId = hl7Factory.createCX();
		rsnaId.setIdNumber(this.singleUsePatientId);
		rsnaId.setAssigningAuthorityUniversalId(Constants.RSNA_UNIVERSAL_ID);
		rsnaId.setAssigningAuthorityUniversalIdType(Constants.RSNA_UNIVERSAL_ID_TYPE);

		return rsnaId;
	}

	@SuppressWarnings("unchecked")
	private SourcePatientInfoType getSrcPatInfo()
	{
		XPN rsnaPatName = hl7Factory.createXPN();
		rsnaPatName.setFamilyName("RSNA ISN");
		rsnaPatName.setGivenName("RSNA ISN");

		SourcePatientInfoType srcPatInfo = hl7Factory.createSourcePatientInfoType();
		srcPatInfo.getPatientIdentifier().add(getRsnaId());
		srcPatInfo.getPatientName().add(rsnaPatName);

		return srcPatInfo;
	}

	private CodedMetadataType getClassCode()
	{
		CodedMetadataType classCode = xdsFactory.createCodedMetadataType();
		classCode.setCode("Imaging Exam");
		classCode.setDisplayName(inStr("Imaging Exam"));
		classCode.setSchemeName("RSNA ISN");

		return classCode;
	}

	private CodedMetadataType getConfidentialityCode()
	{
		CodedMetadataType confidentialityCode = xdsFactory.createCodedMetadataType();
		confidentialityCode.setCode("GRANT");
		confidentialityCode.setSchemeName("RSNA ISN");

		return confidentialityCode;
	}

	private String getGmt(Date date)
	{
		return sdf.format(date);
	}

	private CodedMetadataType getHealthcareFacilityTypeCode()
	{
		CodedMetadataType healthcareFacilityTypeCode = xdsFactory.createCodedMetadataType();
		healthcareFacilityTypeCode.setCode("GENERAL HOSPITAL");
		healthcareFacilityTypeCode.setDisplayName(inStr("GENERAL HOSPITAL"));
		healthcareFacilityTypeCode.setSchemeName("RSNA-ISN");

		return healthcareFacilityTypeCode;
	}

	private CodedMetadataType getPracticeSettingCode()
	{
		CodedMetadataType practiceSettingCode = xdsFactory.createCodedMetadataType();
		practiceSettingCode.setCode("Radiology");
		practiceSettingCode.setDisplayName(inStr("Radiology"));
		practiceSettingCode.setSchemeName("RSNA-ISN");

		return practiceSettingCode;
	}

	private CodedMetadataType getTypeCode()
	{
		CodedMetadataType typeCode = xdsFactory.createCodedMetadataType();
		//typeCode.setCode(study.getStudyDescription());
		//typeCode.setDisplayName(inStr(study.getStudyDescription()));
		//typeCode.setSchemeName("RSNA-ISN");

		typeCode.setCode("18748-4");
		typeCode.setDisplayName(inStr("Diagnostic Imaging Report"));
		typeCode.setSchemeName("LOINC");

		return typeCode;
	}

	@SuppressWarnings("unchecked")
	private void initDocEntry(DocumentEntryType docEntry)
	{
		AuthorType author = getAuthor();
		if (author != null)
			docEntry.setAuthor(author);

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
		docEntry.setServiceStartTime(getGmt(new Date(System.currentTimeMillis())));
		docEntry.setServiceStopTime(getGmt(new Date(System.currentTimeMillis())));
		docEntry.setSourcePatientId(getRsnaId());
		docEntry.setSourcePatientInfo(getSrcPatInfo());
		//docEntry.setTitle(inStr(study.getStudyDescription()));
		docEntry.setTypeCode(getTypeCode());
	}

	@SuppressWarnings("unchecked")
	private static InternationalStringType inStr(String value)
	{
		LocalizedStringType lzStr = xdsFactory.createLocalizedStringType();
		lzStr.setCharset("UTF-8");
		lzStr.setLang("en-US");
		lzStr.setValue(value);

		InternationalStringType inStr = xdsFactory.createInternationalStringType();
		inStr.getLocalizedString().add(lzStr);

		return inStr;
	}

}