/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsna.isn.transfercontent.generatedocument;

/**
 *
 * @author foyesanya
 */
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class LogService {

    public synchronized void logger(String msg) throws IOException {
        DataOutputStream dos = null;
        Properties props = new Properties();
        props.load(new FileInputStream("/rsna/properties/rsna.properties"));
        
        String logfile;
        logfile = props.getProperty("transfercontentlog");

        try {
            dos = new DataOutputStream(new FileOutputStream(logfile, true));
            dos.writeBytes(msg);
            dos.close();
        } catch (FileNotFoundException ex) {
            //
        } catch (IOException ex) {
            //
        }
    }
}
