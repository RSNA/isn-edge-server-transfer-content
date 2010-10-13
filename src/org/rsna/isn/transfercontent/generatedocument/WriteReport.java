/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.generatedocument;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import org.rsna.isn.transfercontent.dao.*;
import org.rsna.isn.transfercontent.exception.TransferContentException;
import org.rsna.isn.transfercontent.logging.LogProvider;

/**
 *
 * @author erackus
 */
public class WriteReport {

    static LogProvider lp;

    public WriteReport() {
    }

    public static void WritetoFile(String destination, int jobID) throws TransferContentException {
        lp = LogProvider.getInstance();

        try{
            FileWriter fstream = new FileWriter(destination + File.separatorChar + "Report.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            SubmissionSetSqlQueryData ssQueryData = new SubmissionSetSqlQueryData();

            ssQueryData = SQLQueries.GetSubmisionSetData(jobID);
            out.write(ssQueryData.getReporttxt());
            out.close();
            System.out.println("Successfully wrote report");
            lp.getLog().error("Successfully wrote report");
        }catch (Exception e){//Catch exception if any
            lp.getLog().error("WritetoFile Error: " + e.getMessage());
            System.err.println("WritetoFile Error: " + e.getMessage());
            throw new TransferContentException("WritetoFile Error: " + e.getMessage(), WriteReport.class.getName());
        }
      }

}
