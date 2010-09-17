/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsna.isn.transfercontent.provideandregister;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.openhealthtools.ihe.xds.document.DocumentDescriptor;
import org.rsna.isn.transfercontent.dao.*;
import org.rsna.isn.transfercontent.generatedocument.*;
import java.util.UUID;

/**
 *
 * @author foyesanya
 */
public class ProvideandRegister {

    private static int returnResult;

    /**
     * @param args the command line arguments
     */
    public static int SubmitDocument(int jobID, String inputFolder) {
        returnResult = 0;
        SubmitAndRegister s = new SubmitAndRegister();

        try {

            //String endPoint = "http://172.20.175.44:9080/tf6/services/xdsrepositoryb";
            String endPoint = "http://ihexds.nist.gov:9080/tf6/services/xdsrepositoryb";
            DocumentDescriptor d = DocumentDescriptor.DICOM;
            String docSetFolder = "C:\\rsna";
            SubmissionSetData InputData = new SubmissionSetData();
            SubmissionSetSqlQueryData ssQueryData = new SubmissionSetSqlQueryData();
            ssQueryData = SQLQueries.GetSubmisionSetData(jobID);

            InputData.setFilename(inputFolder);

            String pName = ssQueryData.getPatientname();
            String[] pInfo = SplitPatientName.find(pName);
            String patientLastName = pInfo[1];
            String patientFirstName = pInfo[2];
            String patientName = patientFirstName + " " + patientLastName;

            InputData.setPatientname(patientName);
            InputData.setFullname(patientName);
            InputData.setGivenname(patientFirstName);
            InputData.setFamilyname(patientLastName);
            InputData.setPatientid(ssQueryData.getPmrn());
            InputData.setAssigningauthority("assigningAuthority");
            InputData.setAssigningauthorityOID("1.3.6.1.4.1.21367.2009.1.2.300");
            InputData.setInstitutionname("University Hospital");
            String documentID = UUID.randomUUID().toString();

            InputData.setDocumentid(documentID);
            InputData.setTitle("Submission Set" + documentID);
            Date date = new Date();
            Format formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String cDate = formatter.format(date);

            InputData.setDate(cDate.substring(0, 8));
            InputData.setTime(cDate.substring(8, cDate.length()));
            InputData.setStreet(ssQueryData.getStreet());
            InputData.setCity(ssQueryData.getCity());
            InputData.setState(ssQueryData.getState());
            InputData.setZip(ssQueryData.getZip_code());
            InputData.setCountry("US");
            InputData.setSex(ssQueryData.getSex());

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
            java.sql.Timestamp dateofBirth = ssQueryData.getDob();
            String dob = dateFormatter.format(dateofBirth);

            InputData.setBirthdate(dob);
            InputData.setUuid("1.2.3");
            InputData.setUid1("1.2.3.1");
            InputData.setUid2("1.2.3.2");
            InputData.setUid3("1.2.3.3");
            InputData.setUid4("1.2.3.4");
            InputData.setPdf("");
            InputData.setDocEntrySourceFileName("DocumentEntrySource.xml");
            InputData.setDocEntrySourceToDocEntryFileName("docEntrySourceToDocEntry.xsl");
            InputData.setDocEntryFileName("DocumentEntry.xml");
            InputData.setDocxslpath("docxsl");
            InputData.setSubmissionSetFileName("SubmissionSet.xml");
            InputData.setSubmissionSetSourceFileName("SubmissionSetSource.xml");
            InputData.setSubmissionSetSourceToSubmissionSetFileName("submissionSetSourceToSubmissionSet.xsl");
            InputData.setOrganizationalOID("1.3.6.1.4.1.21367.100");
            InputData.setSaveMetadataToFile("fail-meta-XDSb.xml");

            returnResult = s.SendFiles(endPoint, docSetFolder, inputFolder, d, InputData);
            if (returnResult == 0) {
                SQLUpdates.UpdateJobDocumentID(jobID, documentID);
            }
            s.initialize();
        } catch (Exception e) {
            //System.out.println("Could not submit document " + e.getMessage());
            e.printStackTrace();
        }
        return returnResult;
    }
}
