/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsna.isn.transfercontent.pix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Properties;

/**
 *
 * @author oyesanyf
 */
public class HL7SequenceNumber {

    public static int sequenceNum() throws FileNotFoundException, IOException {
        Properties props = new Properties();
        props.load(new FileInputStream("c:/mtom/rsna.properties"));
        String sequencenum = props.getProperty("sequencenum");
        String seq = readFromFile(sequencenum);
        System.out.println(seq  +  "Femi") ;
        int aInt = Integer.parseInt(seq);
        Integer N =    aInt;
         N = new Integer(N.intValue() + 1);
        String newNum = Integer.toString(N);
        writeFile(sequencenum, newNum);
      





      
        return N;
    }
    private DataOutputStream dos;

    public static void writeFile(String fileName, String aContents) throws FileNotFoundException, IOException {

        File aFile = new File(fileName);

        if (aFile == null) {
            throw new IllegalArgumentException("File should not be null.");
        }
        if (!aFile.exists()) {
            throw new FileNotFoundException("File does not exist: " + aFile);
        }
        if (!aFile.isFile()) {
            throw new IllegalArgumentException("Should not be a directory: " + aFile);
        }
        if (!aFile.canWrite()) {
            throw new IllegalArgumentException("File cannot be written: " + aFile);
        }

        //use buffering
        Writer output = new BufferedWriter(new FileWriter(aFile));
        try {
            //FileWriter always assumes default encoding is OK!
            output.write(aContents);
        } finally {
            output.close();
        }
    }

    /*
     * Reads data from a given file
     */
    public static String readFromFile(String fileName) {
    String DataLine  = "" ;
        try {
            File inFile = new File(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(inFile)));

            DataLine = br.readLine();
            br.close();
        } catch (FileNotFoundException ex) {
            return (null);
        } catch (IOException ex) {
            return (null);
        }
        return (DataLine);

    }
}




