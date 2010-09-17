/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsna.isn.transfercontent.provideandregister;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author oyesanyf
 */
public class ListSubmissionSetFiles {

    private static ArrayList<String> sendFilesList;

    public static ArrayList listSetFiles(String DirName) throws Exception {

        sendFilesList = new ArrayList<String>();
        try {
            File folder = new File(DirName);
            File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
                String fileName = listOfFiles[i].getName().toString();

                if (fileName.endsWith(".dcm") || fileName.endsWith(".txt")) {
                    if (listOfFiles[i].isFile()) {
                        System.out.println("File " + listOfFiles[i].getAbsolutePath());
                        sendFilesList.add(listOfFiles[i].getName());

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }




        return sendFilesList;
    }
}
