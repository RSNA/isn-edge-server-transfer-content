/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.generatedocument;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import org.rsna.isn.transfercontent.dao.*;

/**
 *
 * @author erackus
 */
public class TimerPoller {
    Timer timer;
    int job_id;

    public TimerPoller() {
    }

    public void init(int seconds) {
        timer = new Timer();
        timer.schedule(new PollDatabase(), 0, seconds * 1000);
    }

    class PollDatabase extends TimerTask {
        public void run() {
            System.out.println("Doing Transfer Content task now!");
            TransferContentJobStatus tcJobStatus;// = new TransferContentJobStatus();
            ArrayList<TransferContentJobStatus> readyJobs = new ArrayList<TransferContentJobStatus>();
            Iterator itr;
            try {
                readyJobs =  SQLQueries.GetTransferContentJobStatus(2);
                itr = readyJobs.iterator();
                while(itr.hasNext()) {
                    tcJobStatus = (TransferContentJobStatus)itr.next();
                    job_id = tcJobStatus.getJob_id();
                    if (job_id != 0) {
                        TransferContent.PrepareandTransfer(job_id);
                    }
                }
            } catch (Exception e) {
                System.out.println("Poll Database: " + e.getMessage());
            }
            //timer.cancel(); //Not necessary because we call System.exit
            //System.exit(0); //Stops the AWT thread (and everything else)
        }
    }
}