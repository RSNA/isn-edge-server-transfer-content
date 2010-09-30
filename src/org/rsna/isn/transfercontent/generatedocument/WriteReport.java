/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.generatedocument;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import org.rsna.isn.transfercontent.dao.*;

/**
 *
 * @author erackus
 */
public class WriteReport {

    public WriteReport() {
    }

    public static void WritetoFile(String destination, int jobID) {
        try{
            FileWriter fstream = new FileWriter(destination + File.separatorChar + "Report");
            BufferedWriter out = new BufferedWriter(fstream);
            SubmissionSetSqlQueryData ssQueryData = new SubmissionSetSqlQueryData();

            ssQueryData = SQLQueries.GetSubmisionSetData(jobID);
            out.write(ssQueryData.getReporttxt());
            out.close();
            System.out.println("Successfully wrote report");
        }catch (Exception e){//Catch exception if any
            System.err.println("WritetoFile Error: " + e.getMessage());
        }
      }

}
