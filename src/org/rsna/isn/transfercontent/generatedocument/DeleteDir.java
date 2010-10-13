/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.generatedocument;

import java.io.File;

/**
 *
 * @author erackus
 */
public class DeleteDir {
    public DeleteDir () {
        
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
                for (int i=0; i<children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }
}
