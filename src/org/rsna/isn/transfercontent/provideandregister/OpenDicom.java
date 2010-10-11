/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.provideandregister;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomInputStream;
import org.rsna.isn.transfercontent.logging.LogProvider;

/**
 *
 * @author erackus
 */
public class OpenDicom  {
    
    private static String sopClassUID;
    private static LogProvider lp;

    public OpenDicom(){
    }

    public static String GetSopClassUID(String source) {
       File fs = new File(source);
        // Do the anonymization and conversion to part10 format, if necessary

        DicomObject dcmObj = null;
        DicomInputStream din = null;

        try {
            din = new DicomInputStream(new BufferedInputStream(new FileInputStream(fs)));
            dcmObj = din.readDicomObject();

            sopClassUID = dcmObj.getString(Tag.SOPClassUID);

            try {
                din.close();
            } catch (IOException e) {
                e.printStackTrace() ;
                System.out.println("Error in closing DICOM input stream");
            }

        } catch (IOException e) {
             e.printStackTrace();

        } catch (Exception e) {
           e.printStackTrace();
        }

        return sopClassUID;
    }

}
