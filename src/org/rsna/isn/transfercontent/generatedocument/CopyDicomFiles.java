package org.rsna.isn.transfercontent.generatedocument;

import java.io.*;
//import java.util.logging.ConsoleHandler;
//import java.util.logging.FileHandler;
//import java.util.logging.Level;
//import java.util.logging.Logger;

import java.nio.channels.FileChannel;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.DicomOutputStream;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;
import org.apache.commons.io.FileUtils;

import java.nio.channels.FileLock;
import java.text.Format;


public class CopyDicomFiles {

    private static FileLock lock;
    private static boolean lockfile;
    private static String pID;
    private static String an;
    private static String studyUID;
    private static String studyDate;
    private static String studyTime;
    private static String studyDateTime;
    private static String key;
    private static String newDirPath;
    private static String newsUIDDirPath;
    private static int index = 0;
    private static String[] uidDirs = new String[5];
    private static long threadID = 0;
    private static String CRLF = "\r\n";
    private static String tstamp;
    private static String lmsg;
    private static Format formatter;
    private static String dateTime;
    private static boolean success = false ;
    private static boolean success2 = false;
    private static boolean success3  = false;

    static LogService log = new LogService();
    static TimeStamp stamp = new TimeStamp();

    public CopyDicomFiles() {
    }

    public static String[] CopyAllFiles(String source, String destination, String mrn, String accessionNumber) throws FileNotFoundException, IOException, InterruptedException, Exception {
        RunnableThread copyThread = new RunnableThread("CopyDirectory");
        copyThread.run();

        File fs = new File(source);
        File fd = new File(destination);

        newDirPath = destination;
        newsUIDDirPath = destination;

        int FileNum;

        //  check if source directory is there
        if (!fs.exists()) {
            fs.mkdir();
            System.out.println("Source Directory or File doesn't exist:" + fs);
        } else if (!fd.exists()) {
            fd.mkdir();
            System.out.println("Destination Directory or File doesn't exist:" + fd);
        // create destination Directory
        } else if (fs.exists() && fd.exists()) {

            //get list of files in source dir
            String files[] = fs.list();

            FileNum = files.length;
            if (FileNum == 0) {
                return null;
            } else {
                threadID = Thread.currentThread().getId();
                for (int i = 0; i < FileNum; i++) {
                       CopyFile(source, destination, files[i], mrn, accessionNumber);
                }
            }

        } else if (fs.isFile()) {
            try {
                copyFile(fs, fd);
            } catch (IOException e) {
                System.out.println(e.getStackTrace());
            }
        } else {
            return null;
        }
        return uidDirs;
    }

    //copy files method
    public static void CopyFile(String source, String destination, String fileName, String mrn, String accessionNumber) throws FileNotFoundException, IOException, InterruptedException, Exception {
        Properties props = new Properties();
        props.load(new FileInputStream("c:/rsna/rsna.properties"));

        String newFname;

        File fd = null;
        File fs = new File(source, fileName);

        // Do the anonymization and conversion to part10 format, if necessary
        DicomObject dcmObj = null;
        DicomInputStream din = null;

        try {
            din = new DicomInputStream(new BufferedInputStream(new FileInputStream(fs)));
            dcmObj = din.readDicomObject();
            java.util.Date today = new java.util.Date();
            
            dateTime = dcmObj.getString(Tag.StudyDate);
            formatter = new SimpleDateFormat("yyyyMMdd");

            if (dateTime == null) {
                studyDate = formatter.format(today);
            } else {
                studyDate = formatter.format(formatter.parseObject(dateTime));
            }

            dateTime = dcmObj.getString(Tag.StudyTime);
            formatter = new SimpleDateFormat("HHmmss");
            if (dateTime == null) {
                studyTime = formatter.format(today);
            } else {
                studyTime = formatter.format(formatter.parseObject(dateTime));
            }

            studyDateTime = studyDate + studyTime;

            pID = dcmObj.getString(Tag.PatientID);
            if (!pID.equals(mrn)) {
                return;
            }

            an = dcmObj.getString(Tag.AccessionNumber);
            if (!an.equals(accessionNumber)) {
                return;
            }

            studyUID = dcmObj.getString(Tag.StudyInstanceUID);

            newDirPath = destination + pID;
            File newDir = new File(newDirPath);
            if (!newDir.exists()) {
                success = newDir.mkdir();
                if (success) {
                    tstamp = stamp.Date();
                    lmsg = "CopyFile: Successfully created subdirectory for  " + pID;
                    log.logger("LogTime is " + "  " + tstamp + ": " + "::     " + lmsg);
                    log.logger(CRLF);
                    System.out.println("Successfully created subdirectory for " + pID);
                } else {
                    tstamp = stamp.Date();
                    lmsg = "Error in CopyDirFile: Unsuccessfully created subdirectory for " + pID;
                    log.logger("LogTime is " + "  " + tstamp + ": " + "::     " + lmsg);
                    log.logger(CRLF);
                    System.out.println("Unsuccessfully created subdirectory for " + pID);
                }
            }

            try {
                din.close();
            } catch (IOException e) {
                System.out.println("Error in closing DICOM input stream");
                tstamp = stamp.Date();
                lmsg = "Error in closing DICOM input stream for " + fileName;
                log.logger("LogTime is " + "  " + tstamp + ": " + "::     " + lmsg);
                log.logger(CRLF);
            }

            newsUIDDirPath = newDirPath + File.separatorChar + studyUID;
            File newsUIDDir = new File(newsUIDDirPath);
            if (!newsUIDDir.exists()) {
                uidDirs[index] = newsUIDDirPath;
                index++;
                success2 = newsUIDDir.mkdir();
                if (success2) {
                    System.out.println("Created filefolder " + newsUIDDirPath);
                    tstamp = stamp.Date();
                    lmsg = "CopyFile: Created filefolder " + newsUIDDirPath;
                    log.logger("LogTime is " + "  " + tstamp + ": " + "::     " + lmsg);
                    log.logger(CRLF);
                } else {
                    System.out.println("Error creating filefolder " + newsUIDDirPath);
                    tstamp = stamp.Date();
                    lmsg = "Exception in CopyFile: Error creating filefolder " + newsUIDDirPath;
                    log.logger("LogTime is " + "  " + tstamp + ": " + "::     " + lmsg);
                    log.logger(CRLF);
                }
            }

            String fPath = newsUIDDirPath + File.separatorChar + fileName;
            if(new File(fPath).exists()) {
                tstamp = stamp.Date();
                lmsg = "CopyFile: File already exists in finalcopydir for MRN = " + pID;
                log.logger("LogTime is " + "  " + tstamp + ": " + "::     " + lmsg);
                log.logger(CRLF);
                String oldFPath = source + fileName;
                success3 = new File(oldFPath).delete();
                if (!success3) {
                    System.out.println("Delete of " + fileName + " in CopyDirFile failed!");
                    lmsg = "Exception in CopyDir File: Delete of Duplicate file " + fileName+ "failed";
                    log.logger("LogTime is " + "  " + tstamp + ": " + "::     " + lmsg);
                    log.logger(CRLF);
                }
                return;
            }

            newFname = newsUIDDirPath + File.separatorChar + fileName;
            fs = new File(source + File.separatorChar + fileName);
            fd = new File(newFname);
            try {
                FileUtils.moveFile(fs, fd);
            } catch (Exception e) {
                System.out.println("CopyDicomFiles Error" + e.getMessage());
                e.printStackTrace();
            }

              System.out.println("Moved " + fileName + "to directory " + newsUIDDirPath);
             tstamp = stamp.Date();
             lmsg = "CopyDicomFile: Wrote " + fileName + "to directory " + newsUIDDirPath;
             log.logger("LogTime is " + "  " + tstamp + ": " + "::     " + lmsg);
             log.logger(CRLF);


            if (fs.exists() && fs.length() > 0) {
                if (fs.delete()) {
                    tstamp = stamp.Date();
                    lmsg = "CopyDir File: Deleted " + fileName + " in incomingdir";
                    log.logger("LogTime is " + "  " + tstamp + ": " + "::     " + lmsg);
                    log.logger(CRLF);
                } else {
                    tstamp = stamp.Date();
                    lmsg = "Exception in CopyDir File: " + fileName + " not deleted in incomingdir";
                    log.logger("LogTime is " + "  " + tstamp + ": " + "::     " + lmsg);
                    log.logger(CRLF);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void FileOperator(String from, String to, String name) throws FileNotFoundException, IOException, InterruptedException {
        final File in = new File(from, name);
        final File out = new File(to, name);
            try {
                copyFile(in, out);
            } catch (IOException e) {
                System.out.println(e.getStackTrace());
            }

    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if(source != null) {
                source.close();
            }

            if(destination != null) {
                destination.close();
            }
        }
    }


}
