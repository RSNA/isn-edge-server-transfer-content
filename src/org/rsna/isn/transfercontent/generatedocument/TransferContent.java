/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.generatedocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.rsna.isn.transfercontent.dao.*;
import org.rsna.isn.transfercontent.generatepayload.*;
import org.rsna.isn.transfercontent.provideandregister.*;

/**
 *
 * @author erackus
 */
public class TransferContent {
    long threadID;
    private static String source;
    private static String destination;
    private static String[] outgoingDir = new String[5];;
    private static String logProvider;
    private static int submitStatus = 0;
    private static boolean delDirSuccess = true;
    private static int updateTransactionStatus = 0;

    public static synchronized void PrepareandTransfer(int jobID) throws InterruptedException, IOException, FileNotFoundException, Exception {
        RunnableThread prepareandTransfer = new RunnableThread("PrepareandTransfer");
        prepareandTransfer.run();

        Properties props = new Properties();
        props.load(new FileInputStream("c:/rsna/rsna.properties"));
        source = props.getProperty("incomingdir");
        destination = props.getProperty("outgoingdir");
        SubmissionSetSqlQueryData ssQueryData = new SubmissionSetSqlQueryData();

        ssQueryData = SQLQueries.GetSubmisionSetData(jobID);
        String mrn = ssQueryData.getPmrn();
        String accessionNumber = ssQueryData.getAccessionnumber();
        System.out.println("Job_ID = " + jobID);
        System.out.println("MRN = " + mrn);
        System.out.println("AccessionNumber = " + accessionNumber);

        outgoingDir = CopyDicomFiles.CopyAllFiles(source, destination, mrn, accessionNumber);
        for (int i=0; i<=outgoingDir.length; i++) {
            WriteKOS.Generate(outgoingDir[i]);
            WriteReport.WritetoFile(outgoingDir[i], jobID);
            SubmissionSetSourceGenerator.CreateSubmissionSet(jobID, outgoingDir[i]);
            DocumentEntryGenerator.CreateSubmissionSet(jobID, outgoingDir[i]);
            DocumentEntrySourceGenerator.CreateSubmissionSet(jobID, outgoingDir[i]);
            submitStatus = ProvideandRegister.SubmitDocument(jobID, outgoingDir[i]);
            if (submitStatus == 0){// Send was OK
                delDirSuccess = DeleteOutputDir.deleteDir(new File(outgoingDir[i]));
                updateTransactionStatus = SQLUpdates.UpdateTransactionStatus(jobID, 4);
            }
        }
    }



}
