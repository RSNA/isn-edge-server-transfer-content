/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.generatedocument;

import java.util.Timer;
import java.util.TimerTask;
import org.rsna.isn.transfercontent.dao.*;

/**
 *
 * @author erackus
 */
public class TimerPoller {
    Timer timer;

    public TimerPoller() {
    }

    public void init(int seconds) {
        timer = new Timer();
        timer.schedule(new PollDatabase(), 0, seconds * 1000);
    }

    class PollDatabase extends TimerTask {
        public void run() {
            System.out.println("Doing Transfer Content task now!");
            TransferContentJobStatus tcJobStatus = new TransferContentJobStatus();

            try {
                tcJobStatus =  SQLQueries.GetTransferContentJobStatus(3);
                if (tcJobStatus != null) {
                    int job_id = tcJobStatus.getJob_id();
                    TransferContent.PrepareandTransfer(job_id);
                }
            } catch (Exception e) {
                String error = e.getMessage();
                System.out.println("Poll Database: " + error);
            }
            //timer.cancel(); //Not necessary because we call System.exit
            //System.exit(0); //Stops the AWT thread (and everything else)
        }
    }
}