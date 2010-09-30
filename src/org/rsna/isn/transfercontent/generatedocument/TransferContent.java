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
import java.util.Properties;
import org.rsna.isn.transfercontent.dao.*;
import org.rsna.isn.transfercontent.provideandregister.*;

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
    private static String[] outgoingDir = new String[10];
    private static String logProvider;
    private static int submitStatus = 0;
    private static boolean delDirSuccess = true;
    private static int updateTransactionStatus = 0;

    public static synchronized void PrepareandTransfer(int jobID) throws InterruptedException, IOException, FileNotFoundException, Exception {
        RunnableThread prepareandTransfer = new RunnableThread("PrepareandTransfer");
        prepareandTransfer.run();

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

        if (mrn != null && accessionNumber != null) {
            inDir = source + File.separatorChar + mrn + File.separatorChar + accessionNumber;
            outDir = destination + File.separatorChar + mrn + File.separatorChar + accessionNumber;
            try {
                Boolean success = (new File(outDir)).mkdirs();
                if (success) {
                    System.out.println("Directory: " + outDir + " created");
                }
            } catch (Exception e){//Catch exception if any
                System.err.println("Error: " + e.getMessage());
                return;
            }

            try {
                outgoingDir = CopyDicomFiles.CopyAllFiles(inDir, outDir, mrn, accessionNumber, examID);
                if (outgoingDir.length != 0) {
                    for (int i=0; i< outgoingDir.length; i++) {
                        WriteKOS.Generate(outgoingDir[i]);
                        WriteReport.WritetoFile(outgoingDir[i], jobID);
                        copyFile("/rsna/docxsl/docEntrySource.xml", outgoingDir[i] + File.separatorChar + "docEntrySource.xml");
                        copyFile("/rsna/docxsl/submissionSetSource.xml", outgoingDir[i] + File.separatorChar + "submissionSetSource.xml");
                        submitStatus = ProvideandRegister.SubmitDocument(jobID, examID, outgoingDir[i]);
                        if (submitStatus == 0){// Send was OK
                           delDirSuccess = DeleteDir.deleteDir(new File(outgoingDir[i]));
                           System.out.println("Successfully uploaded files in " + outgoingDir[i]);
                        }
                        java.sql.Timestamp modifiedDateTime = new java.sql.Timestamp(System.currentTimeMillis());
                        delDirSuccess = DeleteDir.deleteDir(new File(source + File.separatorChar + mrn));
                        updateTransactionStatus = SQLUpdates.UpdateTransactionStatus(jobID, 3, "Transferred to clearinghouse", modifiedDateTime);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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
