/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsna.isn.transfercontent.provideandregister;

import java.io.FileInputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import org.openhealthtools.ihe.xds.document.DocumentDescriptor;
import org.rsna.isn.transfercontent.dao.*;
import org.rsna.isn.transfercontent.generatedocument.*;
import java.util.UUID;
import org.rsna.isn.transfercontent.exception.*;
import org.rsna.isn.transfercontent.logging.LogProvider;
import org.rsna.isn.transfercontent.runnable.RunnableThread;

/**
 *
 * @author foyesanya
 */
public class ProvideandRegister {

    private static int returnResult;
    private static LogProvider lp;
    private static String metaDataFile;
    private static String endPoint;

    /**
     * @param args the command line arguments
     */
    public static int SubmitDocument(int jobID, int examID, String inputFolder) throws ChainedException{
        returnResult = 0;
        SubmitDocumentSet s = new SubmitDocumentSet();
        lp = LogProvider.getInstance();

        try {
            Properties props = new Properties();
            props.load(new FileInputStream("/rsna/properties/rsna.properties"));
            metaDataFile = props.getProperty("metadatafile");
            endPoint = props.getProperty("endpoint");

            DocumentDescriptor d = DocumentDescriptor.DICOM;
            SubmissionSetData inputData = new SubmissionSetData();
            SubmissionSetSqlQueryData ssQueryData = new SubmissionSetSqlQueryData();
            ssQueryData = SQLQueries.GetSubmisionSetData(jobID);

            inputData.setFilename(inputFolder);

            inputData.setPatientname("PatientName");
            inputData.setFullname("PatientName");
            inputData.setGivenname("PatientFirstName");
            inputData.setFamilyname("PatientLastName");


            inputData.setAuthorID("SignerIDNumber");
            inputData.setAuthorFamilyName("SignerLastName");
            inputData.setAuthorGivenName("SignerFirstName");
            inputData.setAuthorAssigningAuthorityOID("2.16.840.1.113883.3.933");

            inputData.setPatientid("89bf6068fc0248e");
            inputData.setAssigningauthority("assigningAuthority");
            inputData.setAssigningauthorityOID("1.3.6.1.4.1.21367.2009.1.2.300");
            inputData.setInstitutionname("University Hospital");
            String documentID = UUID.randomUUID().toString();
            inputData.setExamDescription(ssQueryData.getExamdescription());
            inputData.setSopInstanceUID("1.2");

            inputData.setDocumentid(documentID);
            inputData.setTitle("SubmissionSet" + documentID);
            Date date = new Date();
            Format formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String cDate = formatter.format(date);

            inputData.setDate(cDate.substring(0, 8));
            inputData.setTime(cDate.substring(8, cDate.length()));
            inputData.setStreet(ssQueryData.getStreet());
            inputData.setCity(ssQueryData.getCity());
            inputData.setState(ssQueryData.getState());
            inputData.setZip(ssQueryData.getZip_code());
            inputData.setCountry("US");
            inputData.setSex(ssQueryData.getSex());

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
            java.sql.Timestamp dateofBirth = ssQueryData.getDob();
            String dob = dateFormatter.format(dateofBirth);

            inputData.setBirthdate(dob);
            inputData.setUuid("1.2.3");
            inputData.setUid1("1.2.3.1");
            inputData.setUid2("1.2.3.2");
            inputData.setUid3("1.2.3.3");
            inputData.setUid4("1.2.3.4");
            inputData.setPdf("");
            inputData.setDocEntrySourceFileName("docEntrySource.xml");
            inputData.setDocEntrySourceToDocEntryFileName("docEntrySourceToDocEntry.xsl");
            inputData.setDocEntryFileName("DocumentEntry.xml");
            inputData.setDocxslpath("docxsl");
            inputData.setSubmissionSetFileName("SubmissionSet.xml");
            inputData.setSubmissionSetSourceFileName("submissionSetSource.xml");
            inputData.setSubmissionSetSourceToSubmissionSetFileName("submissionSetSourceToSubmissionSet.xsl");
            inputData.setOrganizationalOID("1.3.6.1.4.1.21367.100");
            inputData.setSaveMetadataToFile(metaDataFile);

            returnResult = s.SendFiles(jobID, endPoint, inputFolder, inputData);
            if (returnResult == 0) {
                SQLUpdates.UpdateJobDocumentID(examID, documentID);
            }
            s.initialize();
        } catch (Exception e) {
            System.out.println("Error in Submit Document " + e.getMessage());
            e.printStackTrace();
            lp.getLog().error("Error in Submit Document ", e);
            throw new TransferContentException("Error in in Submit Document", e);
        }
        return returnResult;
    }
}
