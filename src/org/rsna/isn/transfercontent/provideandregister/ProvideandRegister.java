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
    public static int SubmitDocument(int jobID, int examID, String inputFolder) {
        returnResult = 0;
        SubmitAndRegister s = new SubmitAndRegister();

        try {

            //String endPoint = "http://172.20.175.44:9080/tf6/services/xdsrepositoryb";
            String endPoint = "http://ihexds.nist.gov:9080/tf6/services/xdsrepositoryb";
            DocumentDescriptor d = DocumentDescriptor.DICOM;
            String docSetFolder = "/rsna";
            SubmissionSetData inputData = new SubmissionSetData();
            SubmissionSetSqlQueryData ssQueryData = new SubmissionSetSqlQueryData();
            ssQueryData = SQLQueries.GetSubmisionSetData(jobID);

            inputData.setFilename(inputFolder);

            String pName = ssQueryData.getPatientname();
            String[] pInfo = SplitPatientName.find(pName);
            String patientLastName = pInfo[1];
            String patientFirstName = pInfo[2];
            String patientName = patientFirstName + " " + patientLastName;

            inputData.setPatientname(patientName);
            inputData.setFullname(patientName);
            inputData.setGivenname(patientFirstName);
            inputData.setFamilyname(patientLastName);

            String signer = ssQueryData.getSigner();
            String[] signerInfo = SplitDoctorName.find(signer);
            String signerIDNumber = signerInfo[1];
            String signerLastName = signerInfo[2];
            String signerFirstName = signerInfo[3];

            inputData.setAuthorID(signerIDNumber);
            inputData.setAuthorFamilyName(signerLastName);
            inputData.setAuthorGivenName(signerFirstName);
            inputData.setAuthorAssigningAuthorityOID("2.16.840.1.113883.3.933");

            inputData.setPatientid("89bf6068fc0248e");
            inputData.setAssigningauthority("assigningAuthority");
            inputData.setAssigningauthorityOID("1.3.6.1.4.1.21367.2009.1.2.300");
            inputData.setInstitutionname("University Hospital");
            String documentID = UUID.randomUUID().toString();

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
            inputData.setSaveMetadataToFile("fail-meta-XDSb.xml");

//            DocumentEntryGenerator.CreateSubmissionSet(inputData, inputFolder);

            returnResult = s.SendFiles(endPoint, docSetFolder, inputFolder, d, inputData);
            if (returnResult == 0) {
                SQLUpdates.UpdateJobDocumentID(examID, documentID);
            }
            s.initialize();
        } catch (Exception e) {
            //System.out.println("Could not submit document " + e.getMessage());
            e.printStackTrace();
        }
        return returnResult;
    }
}
