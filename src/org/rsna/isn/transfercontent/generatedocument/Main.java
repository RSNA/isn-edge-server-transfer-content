/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.generatedocument;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.rsna.isn.transfercontent.logging.LogProvider;

/**
 *
 * @author erackus
 */
public class Main {

    private static String logProperties;
    private static String logFile;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream("/rsna/properties/rsna.properties"));
        logProperties = props.getProperty("transfercontentlogproperties");
        logFile = props.getProperty("transfercontentlog");
        LogProvider.init(logProperties, logFile);
        System.out.println("About to schedule Transfer Content Task.");
        TimerPoller tPoller = new TimerPoller();
        tPoller.init(60);
        System.out.println("Transfer Content Task scheduled.");
    }

}
