package org.rsna.isn.transfercontent.provideandregister;

import java.io.*;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.log4j.Logger;
import org.openhealthtools.ihe.utils.OID;
import org.openhealthtools.ihe.xds.document.DocumentDescriptor;
import org.openhealthtools.ihe.xds.document.XDSDocument;
import org.openhealthtools.ihe.xds.document.XDSDocumentFromFile;
import org.openhealthtools.ihe.xds.metadata.extract.MetadataExtractionException;
import org.openhealthtools.ihe.xds.response.XDSResponseType;
import org.openhealthtools.ihe.xds.response.XDSErrorType;
import org.openhealthtools.ihe.xds.source.SubmitTransactionData;
import org.openhealthtools.ihe.xds.source.B_Source;
import org.openhealthtools.ihe.xds.source.SubmitTransactionCompositionException;
import org.rsna.isn.transfercontent.dao.SQLUpdates;
import org.rsna.isn.transfercontent.exception.TransferContentException;
import org.rsna.isn.transfercontent.generatepayload.*;
import org.rsna.isn.transfercontent.logging.LogProvider;

public class SubmitAndRegister {

    static final Logger logger = Logger.getLogger(SubmitAndRegister.class);
    private static ArrayList<String> sendFilesList;
    private int i;
    private int ind;
    private int returnValue;
    private File dicomDocument;
    private static String dcm = ".dcm";
    private String sopClassUID;
    private DocumentDescriptor docDescriptor;
    public static final DocumentDescriptor KOS = new DocumentDescriptor("KOS", "application/dicom-kos");
    private String mimeType;
    private FileInputStream fis;
    private String docEntryUUID;
    private LogProvider lp;


    public void SubmitAndRegister() {
    }

    public void initialize() {
    }

    private synchronized File getTempDir(File dir) throws Exception {
        File temp = File.createTempFile("temp-", "", dir);
        temp.delete();
        temp.mkdirs();
        return temp;
    }

    public int SendFiles(int jobID, String endPoint, String configFolder, String outputFolder, DocumentDescriptor documentDescriptor, SubmissionSetData inputData) throws Exception {
        returnValue = 0;
        System.out.println(endPoint);
        sendFilesList = new ArrayList<String>();
        lp = LogProvider.getInstance();


        String fname = inputData.getFilename();
        sendFilesList = ListSubmissionSetFiles.listSetFiles(fname);


        int size = sendFilesList.size();

        for (i = 0; i < size; ++i) {

            String submissionItemFileName = sendFilesList.get(i);
            if (submissionItemFileName.startsWith("KOS")) {
                docDescriptor = DocumentDescriptor.MIME_TYPE_MAP.put("application/dicom-kos",KOS);
                docDescriptor = DocumentDescriptor.MIME_TYPE_MAP.get(KOS.getMimeType());
//                docDescriptor = DocumentDescriptor.DICOM;
                sopClassUID = "1.2.840.10008.5.1.4.1.1.88.59";
            } else if (submissionItemFileName.endsWith(dcm)) {
                sopClassUID = OpenDicom.GetSopClassUID(fname + File.separatorChar + submissionItemFileName);
                docDescriptor = DocumentDescriptor.DICOM;
            } else if (submissionItemFileName.endsWith(".txt")) {
                docDescriptor = DocumentDescriptor.XML;
                sopClassUID = "TEXT";
            }
            inputData.setSopInstanceUID(sopClassUID);
            dicomDocument = new File(outputFolder + File.separatorChar + submissionItemFileName);
            System.out.println("XXXXXXXXXXXXXXXXXXXXX" + dicomDocument);

            File f = new File(outputFolder);
            java.io.File temp = getTempDir(f);
            System.out.println(outputFolder);

            //   String kosname = InputData.getkosDcmReportFile name();
            //    File kosDcmReportFile  = new File(folderName+File.separator+kosname);

            //  System.out.println("XXXXXXXXXXXXXXXXXXXXX" + kosDcmReportFile );

            String assigningAuthority = inputData.getAssigningauthority();
            String assigningAuthorityOID = inputData.getAssigningauthorityOID();
            String uuid = inputData.getUuid();
            String uid1 = inputData.getUid1();
            String uid2 = inputData.getUid2();
            String uid3 = inputData.getUid3();
            String uid4 = inputData.getUid4();
            String path = inputData.getPath();
            String patientName = inputData.getPatientname();
            String fullName = inputData.getFullname();
            String givenName = inputData.getGivenname();
            String familyName = inputData.getFamilyname();
            String patientID = inputData.getPatientid();
            String authorID = inputData.getAuthorID();
            String authorFamilyName = inputData.getAuthorFamilyName();
            String authorGivenName = inputData.getAuthorGivenName();
            String authorAssigningAuthorityOID = inputData.getAuthorAssigningAuthorityOID();
            String institutionName = inputData.getInstitutionname();
            String examDescription = inputData.getExamDescription();
            String sopInstanceUID = inputData.getSopInstanceUID();
            String documentID = inputData.getDocumentid();
            String title = inputData.getTitle();
            String date = inputData.getDate();
            String time = inputData.getTime();
            String street = inputData.getStreet();
            String city = inputData.getCity();
            String state = inputData.getState();
            String zipCode = inputData.getZip();
            String country = inputData.getCountry();
            String sex = inputData.getSex();
            String birthDate = inputData.getBirthdate();
            String pdf = inputData.getPdf();


//            String docEntrySourceFileName = outputFolder + File.separatorChar + inputData.getDocEntrySourceFileName();
            String docEntryFileName = inputData.getDocEntryFileName();


//            File docEntrySource = new File(docEntrySourceFileName);
            File docEntry = new File(temp,docEntryFileName);

//            String docxslpath = inputData.getDocxslpath();
//            String docEntrySourceToDocEntryFileName = inputData.getDocEntrySourceToDocEntryFileName();
//            File docEntrySourceToDocEntry = new File(
//                    configFolder, docxslpath + File.separatorChar + docEntrySourceToDocEntryFileName);
//            FileUtil.setFileText(
//                    docEntry,
//                    XmlUtil.toString(
//                    XmlUtil.getTransformedDocument(
//                    docEntrySource, docEntrySourceToDocEntry, params)));

            DocumentEntryGenerator.CreateSubmissionSet(jobID, examDescription, sopClassUID, docEntry);

//            String submissionSetSourceFilename = outputFolder + File.separatorChar + inputData.getSubmissionSetSourceFileName();
//            File submissionSetSource = new File(submissionSetSourceFilename);
            String submissionSetFileName = inputData.getSubmissionSetFileName();
            File submissionSet = new File(temp, submissionSetFileName);
//
//            String submissionSetSourceToSubmissionSetFileName = inputData.getSubmissionSetSourceToSubmissionSetFileName();


//            File submissionSetSourceToSubmissionSet = new File(configFolder, docxslpath + File.separatorChar + submissionSetSourceToSubmissionSetFileName);
//            System.out.println("Submission set source XSL " + submissionSetSourceToSubmissionSet.getAbsolutePath());
//            if (!submissionSetSourceToSubmissionSet.exists() || !submissionSetSourceToSubmissionSet.isFile()) {
//                return 1; // Return 1 if no Submission Set
//            }
//            FileUtil.setFileText(
//                    submissionSet,
//                    XmlUtil.toString(
//                    XmlUtil.getTransformedDocument(
//                    submissionSetSource, submissionSetSourceToSubmissionSet, params)));

            SubmissionSetGenerator.CreateSubmissionSet(jobID, submissionSet);

            String responseText = "transmission disabled";
            B_Source source = null;

            responseText = "XDS.b transmission attempted";
            System.out.println("Make a new txnData");
            String organizationalOID = inputData.getOrganizationalOID();
            SubmitTransactionData txnData = new SubmitTransactionData();
            XDSDocument clinicalDocument = new XDSDocumentFromFile(
                    documentDescriptor,
                    dicomDocument);

            try {
                fis = new FileInputStream(docEntry);
                docEntryUUID = txnData.loadDocumentWithMetadata(clinicalDocument, fis);
                fis.close();
                System.out.println("loadDocumentWithMetadata " + docEntryUUID);
            } catch (MetadataExtractionException mee) {
                lp.getLog().error("SubmitAndRegister: Metada Extraction Error");
                mee.printStackTrace();
                throw new TransferContentException("SubmitAndRegister: Metada Extraction Error", SubmitAndRegister.class.getName());
            } catch (SubmitTransactionCompositionException sce) {
                lp.getLog().error("SubmitAndRegister: Submit Transaction Composition Error");
                sce.printStackTrace();
                throw new TransferContentException("SubmitAndRegister: Submit Transaction Composition Error", SubmitAndRegister.class.getName());
            } catch (Exception e) {
                lp.getLog().error("SubmitAndRegister: File Error");
                e.printStackTrace();
                throw new TransferContentException("SubmitAndRegister: File Error", SubmitAndRegister.class.getName());
            }

            String documentUniqueID = OID.createOIDGivenRoot(organizationalOID, 64);
            System.out.println("Doc Entry UUID: " + documentUniqueID);
            txnData.getDocumentEntry(docEntryUUID).setUniqueId(documentUniqueID);

            fis = new FileInputStream(submissionSet);
            txnData.loadSubmissionSet(fis);
            fis.close();

            ServiceClient serviceClient = new ServiceClient();
            Options options = new Options();
            options.setTo(new EndpointReference(endPoint));
            options.setProperty(org.apache.axis2.Constants.Configuration.ENABLE_MTOM, org.apache.axis2.Constants.VALUE_TRUE);
            serviceClient .setOptions(options);

            logger.debug("txtData.loadSubmissionSet");
            String submissionSetUniqueID = OID.createOIDGivenRoot(organizationalOID, 64);
            System.out.println("Submission Set Unique ID: " + submissionSetUniqueID);
            txnData.getSubmissionSet().setUniqueId(submissionSetUniqueID);
            txnData.getSubmissionSet().setSubmissionTime(SubmitAndRegister.formGMT_DTM());

            String saveMetadataToFileName = inputData.getSaveMetadataToFile();

            txnData.saveMetadataToFile(saveMetadataToFileName);
            txnData.getSubmissionSet().setSourceId(organizationalOID);

            logger.debug("SOAP URL: " + endPoint);
            logger.debug("Make a new B_Source");
            source = new org.openhealthtools.ihe.xds.source.B_Source(
                    new URI(endPoint));
            System.out.println("Submitting document " + endPoint);
            XDSResponseType response = null;
            try {
                response = source.submit(txnData);
            } catch (Exception e) {
                lp.getLog().error("SubmitAndRegister: Submit Metadata Error");
                e.printStackTrace();
                throw new TransferContentException("SubmitAndRegister: Submit Metadata Error", SubmitAndRegister.class.getName());
            }
            System.out.println("Doc submitted, check for errors");
            responseText = "XDS.b transmission completed";
            if (response == null) {
                // System.out.println("Null response back from submitting doc");
                returnValue = 1; //Null response may indicate a problem
            } else if (response.getErrorList() == null) {
                // System.out.println("getErrorList is null");
                returnValue = 0;
                SQLUpdates.UpdateJobDocumentID(jobID, documentUniqueID);
            } else if (response.getErrorList().getError() == null) {
                // System.out.println("getErrorList.getError is null");
                returnValue = 0;
            } else {
                System.out.println("Returned " + response.getErrorList().getError().size() + " errors.");
                responseText = "XDS.b transmission encountered errors:";
                java.util.List<XDSErrorType> lst = response.getErrorList().getError();
                Iterator<XDSErrorType> it = lst.iterator();
                int ix = 1;
                while (it.hasNext()) {
                    responseText += " (" + ix + ")" + it.next().getValue();
                    ix++;
                }
                responseText += " (End of XDS.b xmit errors)";
                returnValue = 2;
            }
        }
        return returnValue;
    }

    public static String formGMT_DTM() {
        String timeInGMT = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        // first, set up time in current time zone (where program is running
        sdf.setTimeZone(TimeZone.getDefault());
        String tm = sdf.format(new Date());

        // convert (though there is probably is some circular logic here, oh well
        Date specifiedTime;
        //System.out.println("Specified time is: " + tm);
        //System.out.println("time zone is:GMT" + offset);
        try {
            // switch timezone
            specifiedTime = sdf.parse(tm);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            timeInGMT = sdf.format(specifiedTime);
            //System.out.println("Specified time post conversion: "+ tm);
            //System.exit(0);
        } catch (ParseException e) {
            // FIXME just skip the conversion, bad time stamp, hence bad
            // CDA!
            // Maybe this should be more robust?? An Exception?
        }
        return timeInGMT;
    }
}
