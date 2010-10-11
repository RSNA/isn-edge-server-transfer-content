/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.generatedocument;

import java.io.IOException;
import org.rsna.isn.transfercontent.logging.LogProvider;

/**
 *
 * @author erackus
 */
public class Main {

    private static LogProvider lp;

    public static void main(String[] args) throws IOException {
        LogProvider.init("/home/erackus/dev/transfer-content-app/src/org/rsna/isn/transfercontent/log4j.properties", "/rsna/logs/rsnatransfercontent.log");
        lp = LogProvider.getInstance();
        System.out.println("About to schedule Transfer Content Task.");
        TimerPoller tPoller = new TimerPoller();
        tPoller.init(60);
        System.out.println("Transfer Content Task scheduled.");
        lp.getLog().info("Transfer Content Task scheduled.");

    }

}
