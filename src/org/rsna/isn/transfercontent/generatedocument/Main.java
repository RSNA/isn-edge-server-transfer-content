/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.generatedocument;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import org.rsna.isn.transfercontent.dao.SQLQueries;
import org.rsna.isn.transfercontent.dao.TransferContentJobStatus;
import org.rsna.isn.transfercontent.logging.LogProvider;

/**
 *
 * @author erackus
 */
public class Main {

    private static LogProvider lp;
    private static String log4jpropertiesfile;
    private static String transfercontentlog;
    private static int job_id;
    private static int seconds = 10;

    public static void main(String[] args) throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream("/rsna/properties/rsna.properties"));
        log4jpropertiesfile=props.getProperty("log4jpropertiesfile");
        transfercontentlog=props.getProperty("transfercontentlog");
        LogProvider.init(log4jpropertiesfile, transfercontentlog);
        lp = LogProvider.getInstance();
        TransferContentJobStatus tcJobStatus;// = new TransferContentJobStatus();
        ArrayList<TransferContentJobStatus> readyJobs = new ArrayList<TransferContentJobStatus>();

        while (true) {
            Iterator itr;
            try {
                readyJobs =  SQLQueries.GetTransferContentJobStatus(2);
                if (!readyJobs.isEmpty()) {
                    synchronized (Main.class) {
                        itr = readyJobs.iterator();
                            while(itr.hasNext()) {
                                System.out.println("Transfer Content Task scheduled.");
                                lp.getLog().info("Transfer Content Task scheduled.");
                                tcJobStatus = (TransferContentJobStatus)itr.next();
                                job_id = tcJobStatus.getJob_id();
                                if (job_id != 0) {
                                    TransferContent.PrepareandTransfer(job_id);
                                }
                            }
                            readyJobs.clear();
                            Thread.sleep(seconds*1000);
                    }
                }
                Thread.sleep(seconds*1000);
            } catch (Exception e) {
                System.out.println("Main: " + e.getMessage());
                lp.getLog().error("Main",e);
            }

        }

    }

}
