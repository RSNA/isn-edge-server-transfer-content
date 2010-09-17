package org.rsna.isn.transfercontent.provideandregister;

import java.io.*;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import org.apache.log4j.Logger;
import org.openhealthtools.ihe.utils.OID;
import org.openhealthtools.ihe.xds.document.DocumentDescriptor;
import org.openhealthtools.ihe.xds.document.XDSDocument;
import org.openhealthtools.ihe.xds.document.XDSDocumentFromFile;
import org.openhealthtools.ihe.xds.response.XDSResponseType;
import org.openhealthtools.ihe.xds.response.XDSErrorType;
import org.openhealthtools.ihe.xds.source.SubmitTransactionData;
import org.openhealthtools.ihe.xds.source.B_Source;

public class SubmitAndRegister {

    static final Logger logger = Logger.getLogger(SubmitAndRegister.class);
    private static ArrayList<String> sendFilesList;
    private int i;
    private int returnValue;
    private File dicomDocument;

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

    public int SendFiles(String endPoint, String configFolder, String outputFolder, DocumentDescriptor documentDescriptor, SubmissionSetData InputData) throws Exception {
        returnValue = 0;
        System.out.println(endPoint);
        sendFilesList = new ArrayList<String>();


        String fname = InputData.getFilename();
        sendFilesList = ListSubmissionSetFiles.listSetFiles(fname);


        int size = sendFilesList.size();

        for (i = 0; i < size; ++i) {

            String dicomFileName = sendFilesList.get(i);
            dicomDocument = new File(outputFolder + File.separatorChar + dicomFileName);
            System.out.println("XXXXXXXXXXXXXXXXXXXXX" + dicomDocument);

            File f = new File(outputFolder);
            java.io.File temp = getTempDir(f);
            System.out.println(outputFolder);

            //   String kosname = InputData.getkosDcmReportFile name();
            //    File kosDcmReportFile  = new File(folderName+File.separator+kosname);

            //  System.out.println("XXXXXXXXXXXXXXXXXXXXX" + kosDcmReportFile );

            String assigningAuthority = InputData.getAssigningauthority();
            String assigningAuthorityOID = InputData.getAssigningauthorityOID();
            String uuid = InputData.getUuid();
            String uid1 = InputData.getUid1();
            String uid2 = InputData.getUid2();
            String uid3 = InputData.getUid3();
            String uid4 = InputData.getUid4();
            String path = InputData.getPath();
            String patientName = InputData.getPatientname();
            String fullName = InputData.getFullname();
            String givenName = InputData.getGivenname();
            String familyName = InputData.getFamilyname();
            String patientID = InputData.getPatientid();
            String InstitutionName = InputData.getInstitutionname();
            String documentID = InputData.getDocumentid();
            String title = InputData.getTitle();
            String date = InputData.getDate();
            String time = InputData.getTime();
            String street = InputData.getStreet();
            String city = InputData.getCity();
            String state = InputData.getState();
            String zipCode = InputData.getZip();
            String country = InputData.getCountry();
            String sex = InputData.getSex();
            String birthDate = InputData.getBirthdate();
            String pdf = InputData.getPdf();




            String[] params = new String[]{
                //   "path",         path,
                "patient-name", patientName,
                "full-name", fullName,
                "given-name", givenName,
                "family-name", familyName,
                "patient-id", patientID,
                "assigning-authority", assigningAuthority,
                "assigning-authority-OID", assigningAuthorityOID,
                "institution-name", InstitutionName,
                "document-id", documentID,
                "title", title,
                "date", date,
                "time", time,
                "street", street,
                "city", city,
                "state", state,
                "zip", zipCode,
                "country", country,
                "sex", sex,
                "birth-date", birthDate,
                "uuid", uuid,
                "uid1", uid1,
                "uid2", uid2,
                "uid3", uid3,
                "uid4", uid4,
                "pdf", pdf //This param must be last in the array (see *** below).
            };


            String docEntrySourceFileName = outputFolder + File.separatorChar + InputData.getDocEntrySourceFileName();
            String docEntryFileName = outputFolder + File.separatorChar + InputData.getDocEntryFileName();


            File docEntrySource = new File(docEntrySourceFileName);
            File docEntry = new File(docEntryFileName);

            String docxslpath = InputData.getDocxslpath();
            String docEntrySourceToDocEntryFileName = InputData.getDocEntrySourceToDocEntryFileName();
            File docEntrySourceToDocEntry = new File(
                    configFolder, docxslpath + File.separatorChar + docEntrySourceToDocEntryFileName);
            FileUtil.setFileText(
                    docEntry,
                    XmlUtil.toString(
                    XmlUtil.getTransformedDocument(
                    docEntrySource, docEntrySourceToDocEntry, params)));


            String submissionSetSourceFilename = outputFolder + File.separatorChar + InputData.getSubmissionSetSourceFileName();
            File submissionSetSource = new File(submissionSetSourceFilename);
            String submissionSetFileName = InputData.getSubmissionSetFileName();
            File submissionSet = new File(temp, submissionSetFileName);

            String submissionSetSourceToSubmissionSetFileName = InputData.getSubmissionSetSourceToSubmissionSetFileName();


            File submissionSetSourceToSubmissionSet = new File(configFolder, docxslpath + File.separatorChar + submissionSetSourceToSubmissionSetFileName);
            System.out.println("Submission set source XSL " + submissionSetSourceToSubmissionSet.getAbsolutePath());
            if (!submissionSetSourceToSubmissionSet.exists() || !submissionSetSourceToSubmissionSet.isFile()) {
                return 1; // Return 1 if no Submission Set
            }
            FileUtil.setFileText(
                    submissionSet,
                    XmlUtil.toString(
                    XmlUtil.getTransformedDocument(
                    submissionSetSource, submissionSetSourceToSubmissionSet, params)));

            String responseText = "transmission disabled";
            B_Source source = null;

            responseText = "XDS.b transmission attempted";
            System.out.println("Make a new txnData");
            String organizationalOID = InputData.getOrganizationalOID();
            SubmitTransactionData txnData = new SubmitTransactionData();
            XDSDocument clinicalDocument = new XDSDocumentFromFile(
                    documentDescriptor,
                    dicomDocument);

            FileInputStream fis = new FileInputStream(docEntry);
            String docEntryUUID = txnData.loadDocumentWithMetadata(clinicalDocument, fis);
            fis.close();
            System.out.println("loadDocumentWithMetadata " + docEntryUUID);

            String uniqueID = OID.createOIDGivenRoot(organizationalOID, 64);
            System.out.println("Doc Entry UUID: " + uniqueID);
            txnData.getDocumentEntry(docEntryUUID).setUniqueId(uniqueID);

            fis = new FileInputStream(submissionSet);
            txnData.loadSubmissionSet(fis);
            fis.close();

            logger.debug("txtData.loadSubmissionSet");
            uniqueID = OID.createOIDGivenRoot(organizationalOID, 64);
            System.out.println("Submission Set Unique ID: " + uniqueID);
            txnData.getSubmissionSet().setUniqueId(uniqueID);
            txnData.getSubmissionSet().setSubmissionTime(SubmitAndRegister.formGMT_DTM());

            String saveMetadataToFileName = InputData.getSaveMetadataToFile();

            txnData.saveMetadataToFile(saveMetadataToFileName);
            txnData.getSubmissionSet().setSourceId(organizationalOID);

            logger.debug("SOAP URL: " + endPoint);
            logger.debug("Make a new B_Source");
            source = new org.openhealthtools.ihe.xds.source.B_Source(
                    new URI(endPoint));
            System.out.println("Submitting document " + endPoint);
            XDSResponseType response = null;
            response = source.submit(txnData);
            System.out.println("Doc submitted, check for errors");
            responseText = "XDS.b transmission completed";
            if (response == null) {
                // System.out.println("Null response back from submitting doc");
                returnValue = 2; //Null response may indicate a problem
            } else if (response.getErrorList() == null) {
                // System.out.println("getErrorList is null");
                returnValue = 0;
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
                returnValue = 3;
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
