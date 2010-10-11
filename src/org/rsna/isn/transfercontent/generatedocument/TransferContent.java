/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.generatedocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import org.rsna.isn.transfercontent.dao.*;
import org.rsna.isn.transfercontent.logging.LogProvider;
import org.rsna.isn.transfercontent.provideandregister.*;
import org.rsna.isn.transfercontent.pix.*;

/**
 *
 * @author erackus
 */
public class TransferContent {
    long threadID;
    private static String source;
    private static String destination;
    private static String inDir;
    private static String outDir;
    private static ArrayList<String> outgoingDir = new ArrayList<String>();
    private static LogProvider lp;
    private static int submitStatus = 0;
    private static int updateStatus = 0;
    private static boolean delDirSuccess = true;
    private static int updateTransactionStatus = 0;
    private static boolean success = false;
    private static String hl7Result;
    private static Iterator itr;
    private static String workingDir;

    public static synchronized void PrepareandTransfer(int jobID) throws InterruptedException, IOException, FileNotFoundException, Exception {
        RunnableThread prepareandTransfer = new RunnableThread("PrepareandTransfer");
        prepareandTransfer.run();

        lp = LogProvider.getInstance();

        Properties props = new Properties();
        props.load(new FileInputStream("/rsna/properties/rsna.properties"));
        source = props.getProperty("incomingdir");
        destination = props.getProperty("outgoingdir");
        SubmissionSetSqlQueryData ssQueryData = new SubmissionSetSqlQueryData();

        ssQueryData = SQLQueries.GetSubmisionSetData(jobID);
        String mrn = ssQueryData.getPmrn();
        String accessionNumber = ssQueryData.getAccessionnumber();
        int examID = ssQueryData.getExamid();
        System.out.println("Job_ID = " + jobID);
        System.out.println("MRN = " + mrn);
        System.out.println("AccessionNumber = " + accessionNumber);
        System.out.println("Exam_ID = " + examID);

        PatientRSNAIDs patientRSNAIDs = new PatientRSNAIDs();

        ssQueryData = SQLQueries.GetSubmisionSetData(jobID);
        int patientID = ssQueryData.getPatientid();
        patientRSNAIDs = SQLQueries.GetRSNAIDfromPatientID(patientID);

 //       updateStatus = SQLUpdates.UpdateRegistered(patientID, false);

        boolean isRegistered = patientRSNAIDs.isRegistered();

        if (!patientRSNAIDs.isRegistered()) {
            hl7Result = Pix.RegisterPatient(jobID);
            if (!hl7Result.contains("error")) {
                updateStatus = SQLUpdates.UpdateRegistered(patientID, true);
                lp.getLog().info("Registered patient with patient ID = " + patientID);
            }
        }

        if (mrn != null && accessionNumber != null) {
            inDir = source + File.separatorChar + mrn + File.separatorChar + accessionNumber;
            outDir = destination + File.separatorChar + mrn + File.separatorChar + accessionNumber;
            try {
                success = (new File(outDir)).mkdirs();
                if (success) {
                    System.out.println("Directory: " + outDir + " created");
                }
            } catch (Exception e){//Catch exception if any
                System.err.println("Error: " + e.getMessage());
                return;
            }

            try {
                outgoingDir = CopyDicomFiles.CopyAllFiles(inDir, outDir, mrn, accessionNumber, examID);
                if (outgoingDir != null) {
                    itr = outgoingDir.iterator();
                    while (itr.hasNext()) {
                        lp.getLog().info("Copied files for exam ID = " + examID);
                        workingDir = (String) itr.next();
                        if (workingDir != null) {
                            WriteKOS.Generate(workingDir);
                            WriteReport.WritetoFile(workingDir, jobID);
                            submitStatus = ProvideandRegister.SubmitDocument(jobID, examID, workingDir);
                            if (submitStatus == 0){// Send was OK
                                System.out.println("Successfully uploaded files in " + workingDir);
                                success = true;
                                delDirSuccess = DeleteDir.deleteDir(new File(workingDir));
                                if (!delDirSuccess) {
                                    lp.getLog().error("Could not delete directory " + workingDir);
                                }
                            } else {
                                success = false;
                            }
                        }
                    }
                    java.sql.Timestamp modifiedDateTime = new java.sql.Timestamp(System.currentTimeMillis());
                    if (success) {
                        delDirSuccess = DeleteDir.deleteDir(new File(source + File.separatorChar + mrn));
                        if (!delDirSuccess) {
                            lp.getLog().error("Could not delete directory " + source + File.separatorChar + mrn);
                        }
                        delDirSuccess = DeleteDir.deleteDir(new File(destination + File.separatorChar + mrn));
                        if (!delDirSuccess) {
                            lp.getLog().error("Could not delete directory " + destination + File.separatorChar + mrn);
                        }
                        updateTransactionStatus = SQLUpdates.UpdateTransactionStatus(jobID, 4, "Transferred to clearinghouse", modifiedDateTime);
                        if (updateTransactionStatus == 0) {
                            lp.getLog().error("SQLUpdates: Could not update transaction status to 4");
                        }
                    } else {
                        delDirSuccess = DeleteDir.deleteDir(new File(destination + File.separatorChar + mrn));
                        if (!delDirSuccess) {
                            lp.getLog().error("Could not delete directory " + destination + File.separatorChar + mrn);
                        }
                        updateTransactionStatus = SQLUpdates.UpdateTransactionStatus(jobID, 3, "Document prepared for transfer to clearinghouse", modifiedDateTime);
                        if (updateTransactionStatus == 0) {
                            lp.getLog().error("SQLUpdates: Could not update transaction status to 3");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                lp.getLog().error("Error in Transfer Content ");
                return;
            }
        }
    }

    static void copyFile(String src, String dst) throws IOException {
        InputStream in = new FileInputStream(new File(src));
        OutputStream out = new FileOutputStream(new File(dst));

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;

        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

}
