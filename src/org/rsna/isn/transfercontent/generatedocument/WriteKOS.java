package org.rsna.isn.transfercontent.generatedocument;

import java.io.*;
//import java.util.logging.ConsoleHandler;
//import java.util.logging.FileHandler;
//import java.util.logging.Level;
//import java.util.logging.Logger;


import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomInputStream;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.VR;
import org.dcm4che2.io.DicomOutputStream;
import org.rsna.isn.transfercontent.exception.TransferContentException;
import org.rsna.isn.transfercontent.logging.LogProvider;

public class WriteKOS {

    private static String specificCharacterSet;
    private static String manufacturer;
    private static String patientName;
    private static String SOPInstanceUID;
    private static String SOPClassUID;
    private static String referringPhysician;
    private static String accessionNumber;
    private static String patientSex;
    private static String studyDate;
    private static String studyTime;
    private static String seriesNumber;
    private static String studyDescription;
    private static String studyID;
    private static String CRLF = "\r\n";
    private static String tstamp;
    private static String lmsg;
    private static String errorMsg;
    private static String fileMetaInformationVersion;
    private static String mediaStorageSOPClassUID;
    private static String mediaStorageSOPInstanceUID;
    private static String transferSyntaxUID;
    private static String implementationClassUID;
    private static String implementationVersionName;
    private static String patientID;
    private static String patientBirthDate;
    private static String studyInstanceUID;
    private static String seriesInstanceUID;

    private static boolean firstFlag = true;
    private static String savedSeriesInstanceUID = "";

    private static LogProvider lp;
    private static Tree<String> t = new Tree<String>();
    private static Node<String> root;
    private static Node<String> seriesNode;
    private static Node<String> imageNode;
    private static List<Node<String>> seriesList;
    private static List<Node<String>> imagesList;
    private static int numberofSeries;
    private static int numberofImages;
    private static int index;
    private static FileOutputStream fos;
    private static BufferedOutputStream bos;
    private static DicomOutputStream dos;
    private static File fs;
    private static File fd;


    public WriteKOS() {
    }

    public static void Generate(String source) throws FileNotFoundException, IOException, InterruptedException, Exception, TransferContentException {
//        RunnableThread copyThread = new RunnableThread("WriteKOS");
//        copyThread.run();
        lp =LogProvider.getInstance();

        fs = new File(source);
        fd = new File(source + File.separatorChar + "KOS.dcm");

        try {
            fos = new FileOutputStream(fd);
            bos = new BufferedOutputStream(fos);
            dos = new DicomOutputStream(bos);
        } catch (Exception e) {
            lp.getLog().error("WriteKOS error: " + e.getMessage());
            System.out.println("WriteKOS error: " + e.getMessage());
        }
        int FileNum;
        DicomObject dcmObj = new BasicDicomObject();

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date now = new java.util.Date();
        String strDate = sdfDate.format(now);
        String yr = strDate.substring(0, 4);

        //  check if source directory is there
        if (!fs.exists()) {
            fs.mkdir();
            lp.getLog().info("Source Directory or File doesn't exist:" + fs);
            System.out.println("Source Directory or File doesn't exist:" + fs);
        } else {

            //get list of files in source dir
            String files[] = fs.list();

            FileNum = files.length;
            if (FileNum == 0) {
                return;
            } else {
                for (int i = 0; i < FileNum; i++) {
                    if (files[i].endsWith(".dcm") && !files[i].startsWith("KOS") && !files[i].startsWith("Report")) {
                       OpenDicomFiles(source, files[i], dcmObj);
                    }
                }
                dcmObj.clear();
                dcmObj.putString(Tag.ValueType, VR.CS, "CONTAINER");
                dos.writeDataset(dcmObj, transferSyntaxUID);
                dos.writeHeader(Tag.ConceptNameCodeSequence, VR.SQ, -1);
                dos.writeHeader(Tag.Item, null, -1);
                dcmObj.clear();
                dcmObj.putString(Tag.CodeValue, VR.SH, "113030");
                dcmObj.putString(Tag.CodingSchemeDesignator, VR.SH, "DCM");
                dcmObj.putString(Tag.CodeMeaning, VR.LO, "Manifest");
                dos.writeDataset(dcmObj, transferSyntaxUID);
                dcmObj.clear();
                dos.writeHeader(Tag.ItemDelimitationItem, null, 0);
                dos.writeHeader(Tag.SequenceDelimitationItem, null, 0);
                dcmObj.putString(Tag.ContinuityOfContent, VR.CS, "SEPARATE");
                dos.writeDataset(dcmObj, transferSyntaxUID);
                dcmObj.clear();
                dos.writeHeader(Tag.CurrentRequestedProcedureEvidenceSequence, VR.SQ, -1);
                dos.writeHeader(Tag.Item, null, -1);
                dos.writeHeader(Tag.ReferencedSeriesSequence, VR.SQ, -1);
 
                root = t.getRootElement();
                numberofSeries = root.getNumberOfChildren();
                System.out.println("Number of Series = " + numberofSeries);
                seriesList = root.getChildren();

                for (Iterator<Node<String>> itseries = seriesList.iterator(); itseries.hasNext();) {
                    dos.writeHeader(Tag.Item, null, -1);
                    Node<String> nextSeries  = itseries.next();
                    seriesInstanceUID = nextSeries.getData();
                    System.out.println("Series UID = " + seriesInstanceUID);
                    numberofImages = nextSeries.getNumberOfChildren();
                    System.out.println("Number of Images = " + numberofImages);
                    imagesList = nextSeries.getChildren();
                    dos.writeHeader(Tag.ReferencedSOPSequence, VR.SQ, -1);
                    for (Iterator<Node<String>> itimages = imagesList.iterator(); itimages.hasNext();) {
                        SOPInstanceUID = itimages.next().getData();
                        dos.writeHeader(Tag.Item, null, -1);
                        dcmObj.putString(Tag.ReferencedSOPClassUID, VR.UI, SOPClassUID);
                        dcmObj.putString(Tag.ReferencedSOPInstanceUID, VR.UI, SOPInstanceUID);
                        dos.writeDataset(dcmObj, transferSyntaxUID);
                        dcmObj.clear();
                        dos.writeHeader(Tag.ItemDelimitationItem, null, 0);
                        dos.writeDataset(dcmObj, transferSyntaxUID);
                        dcmObj.clear();
                        System.out.println("SOP Instance UID = " + SOPInstanceUID);
                    }
                    dos.writeHeader(Tag.SequenceDelimitationItem, null, 0);
                    dcmObj.putString(Tag.SeriesInstanceUID, VR.UI, seriesInstanceUID);
                    dos.writeDataset(dcmObj, transferSyntaxUID);
                    dcmObj.clear();
                    dos.writeHeader(Tag.ItemDelimitationItem, null, 0);
                }
                dos.writeHeader(Tag.SequenceDelimitationItem, null, 0);
                dcmObj.putString(Tag.StudyInstanceUID, VR.UI, studyInstanceUID);
                dos.writeDataset(dcmObj, transferSyntaxUID);
                dcmObj.clear();
                dos.writeHeader(Tag.ItemDelimitationItem, null, 0);
                dos.writeHeader(Tag.SequenceDelimitationItem, null, 0);

                dos.writeHeader(Tag.ContentTemplateSequence, VR.SQ, -1);
                dos.writeHeader(Tag.Item, null, -1);
                dcmObj.putString(Tag.MappingResource, VR.CS, "DCMR");
                dcmObj.putString(Tag.TemplateIdentifier, VR.CS, yr);
                dos.writeDataset(dcmObj, transferSyntaxUID);
                dcmObj.clear();
                dos.writeHeader(Tag.ItemDelimitationItem, null, 0);
                dos.writeHeader(Tag.SequenceDelimitationItem, null, 0);

                dos.writeHeader(Tag.ContentSequence, VR.SQ, -1);
                dos.writeHeader(Tag.Item, null, -1);
                dcmObj.putString(Tag.RelationshipType, VR.CS, "HAS CONCEPT MOD");
                dcmObj.putString(Tag.ValueType, VR.CS, "CODE");
                dos.writeDataset(dcmObj, transferSyntaxUID);
                dcmObj.clear();
                dos.writeHeader(Tag.ConceptNameCodeSequence, VR.SQ, -1);
                dos.writeHeader(Tag.Item, null, -1);
                dcmObj.putString(Tag.CodeValue, VR.SH, "121049");
                dcmObj.putString(Tag.CodingSchemeDesignator, VR.SH, "DCM");
                dcmObj.putString(Tag.CodeMeaning, VR.LO, "Language of Content Item and Descendants");
                dos.writeDataset(dcmObj, transferSyntaxUID);
                dcmObj.clear();
                dos.writeHeader(Tag.ItemDelimitationItem, null, 0);
                dos.writeHeader(Tag.SequenceDelimitationItem, null, 0);
                dos.writeHeader(Tag.ConceptCodeSequence, VR.SQ, -1);
                dos.writeHeader(Tag.Item, null, -1);
                dcmObj.putString(Tag.CodeValue, VR.SH, "eng");
                dcmObj.putString(Tag.CodingSchemeDesignator, VR.SH, "RFC3066");
                dcmObj.putString(Tag.CodeMeaning, VR.LO, "English");
                dos.writeDataset(dcmObj, transferSyntaxUID);
                dcmObj.clear();
                dos.writeHeader(Tag.ItemDelimitationItem, null, 0);
                dos.writeHeader(Tag.SequenceDelimitationItem, null, 0);
                dos.writeHeader(Tag.ItemDelimitationItem, null, 0);

                for (Iterator<Node<String>> itseries = seriesList.iterator(); itseries.hasNext();) {
                    dos.writeHeader(Tag.Item, null, -1);
                    Node<String> nextSeries  = itseries.next();
                    numberofImages = nextSeries.getNumberOfChildren();
                    imagesList = nextSeries.getChildren();
                    for (Iterator<Node<String>> itimages = imagesList.iterator(); itimages.hasNext();) {
                        dos.writeHeader(Tag.ReferencedSOPSequence, VR.SQ, -1);
                        SOPInstanceUID = itimages.next().getData();
                        dos.writeHeader(Tag.Item, null, -1);
                        dcmObj.putString(Tag.ReferencedSOPClassUID, VR.UI, SOPClassUID);
                        dcmObj.putString(Tag.ReferencedSOPInstanceUID, VR.UI, SOPInstanceUID);
                        dos.writeDataset(dcmObj, transferSyntaxUID);
                        dcmObj.clear();
                        dos.writeHeader(Tag.ItemDelimitationItem, null, 0);
                        dos.writeHeader(Tag.SequenceDelimitationItem, null, 0);
                        dcmObj.putString(Tag.RelationshipType, VR.CS, "CONTAINS");
                        dcmObj.putString(Tag.ValueType, VR.CS, "IMAGE");
                        dos.writeDataset(dcmObj, transferSyntaxUID);
                        dcmObj.clear();
                        dos.writeHeader(Tag.ItemDelimitationItem, null, 0);
                        System.out.println("(2) SOP Instance UID = " + SOPInstanceUID);
                    }
                }
                dos.writeHeader(Tag.SequenceDelimitationItem, null, 0);
                dos.writeDataset(dcmObj, transferSyntaxUID);
                dos.close();
                lp.getLog().info("Finished writing KOS in subdirectory " + source);
                System.out.println("Finished writing KOS in subdirectory " + source);
            }
        }
    }

    //copy files method
    public static void OpenDicomFiles(String source, String fileName, DicomObject dcmObj) throws FileNotFoundException, IOException, InterruptedException, Exception {
        File dicomFile = new File(source, fileName);
        // Read DICOM file

        try {
            DicomInputStream din = new DicomInputStream(new BufferedInputStream(new FileInputStream(dicomFile)));
            DicomObject inDcmObj = din.readDicomObject();

            fileMetaInformationVersion = inDcmObj.getString(Tag.FileMetaInformationVersion);
            implementationClassUID = inDcmObj.getString(Tag.ImplementationClassUID);
            specificCharacterSet = "ISO_IR 100";
            patientID = inDcmObj.getString(Tag.PatientID);
            studyInstanceUID = inDcmObj.getString(Tag.StudyInstanceUID);
            studyID = inDcmObj.getString(Tag.StudyID);
            patientName = inDcmObj.getString(Tag.PatientName);
            accessionNumber = inDcmObj.getString(Tag.AccessionNumber);
            patientSex = inDcmObj.getString(Tag.PatientSex);
            patientBirthDate = inDcmObj.getString(Tag.PatientBirthDate);
            referringPhysician = inDcmObj.getString(Tag.ReferringPhysicianName);

            studyDescription = inDcmObj.getString(Tag.StudyDescription);
            studyDate = inDcmObj.getString(Tag.StudyDate);
            studyTime = inDcmObj.getString(Tag.StudyTime);

            manufacturer = inDcmObj.getString(Tag.Manufacturer);

            studyInstanceUID = inDcmObj.getString(Tag.StudyInstanceUID);
            studyDescription = inDcmObj.getString(Tag.StudyDescription);
            seriesInstanceUID = inDcmObj.getString(Tag.SeriesInstanceUID);
            seriesNumber = inDcmObj.getString(Tag.SeriesNumber);
            SOPInstanceUID = inDcmObj.getString(Tag.SOPInstanceUID);
            SOPClassUID = inDcmObj.getString(Tag.SOPClassUID);

//            transferSyntaxUID = din.getTransferSyntax().uid();
            transferSyntaxUID = "1.2.840.10008.1.2.1";
            if (firstFlag) {
                 root = new Node<String>(studyInstanceUID);
                 t.setRootElement(root);
                 savedSeriesInstanceUID = seriesInstanceUID;
                 seriesNode = new Node<String>(seriesInstanceUID);
                 root.addChild(seriesNode);
                 imageNode = new Node<String>(SOPInstanceUID);
                 seriesNode.addChild(imageNode);
                 WriteKOSHeader(dcmObj);
                 firstFlag = false;
             } else {
                 if (!seriesInstanceUID.equals(savedSeriesInstanceUID)) {
                    index = 1;
                    savedSeriesInstanceUID = seriesInstanceUID;
                    seriesNode = new Node<String>(seriesInstanceUID);
                    root.addChild(seriesNode);
                    imageNode = new Node<String>(SOPInstanceUID);
                    seriesNode.addChild(imageNode);
                    index++;
                 } else {
                    imageNode = new Node<String>(SOPInstanceUID);
                    seriesNode.addChild(imageNode);
                 }
             }

//             System.out.println("StUID = " + studyUID + " SeUID = " + seriesUID + " SOP UID = " + SOPInstanceUID);

             try {
                din.close();
             } catch (IOException e) {
                System.out.println("Error in closing DICOM input stream");
                System.out.println("WriteKOS error: " + e.getMessage());
                lp.getLog().error("Error in closing DICOM input stream for " + fileName);
                throw new TransferContentException("WriteKOS error: " + e.getMessage(), WriteKOS.class.getName());
             }
        } catch (IOException e) {
            System.out.println("WriteKOS error: " + e.getMessage());
            lp.getLog().error("WriteKOS error: " + e.getMessage());
            throw new TransferContentException("WriteKOS error: " + e.getMessage(), WriteKOS.class.getName());
        } catch (Exception e) {
            System.out.println("WriteKOS error: " + e.getMessage());
            lp.getLog().error("WriteKOS error: " + e.getMessage());
            throw new TransferContentException("WriteKOS error: " + e.getMessage(), WriteKOS.class.getName());
        }
    }

    private static void WriteKOSHeader(DicomObject dcmObj) throws InterruptedException, TransferContentException {

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HHmmss.000");
        Date date = new Date();
        String today = dateFormat.format(date);
        String[] dateTime = today.split(" ");
        String contentDate = dateTime[0];
        String contentTime = dateTime[1];

        // Set basic Meta info
        mediaStorageSOPClassUID = "1.2.840.10008.5.1.4.1.1.88.59";
        mediaStorageSOPInstanceUID = "1.2.528.1.1001.100.25.2645.1933.36321246679.20040824141444562";

        try {
            dcmObj.initFileMetaInformation(mediaStorageSOPClassUID, mediaStorageSOPInstanceUID, transferSyntaxUID);
//            dcmObj.putString(Tag.FileMetaInformationVersion, VR.OB, fileMetaInformationVersion);
            dcmObj.putString(Tag.SpecificCharacterSet, VR.CS, specificCharacterSet);
            dcmObj.putString(Tag.SOPClassUID, VR.UI, mediaStorageSOPClassUID);
            dcmObj.putString(Tag.SOPInstanceUID, VR.UI, mediaStorageSOPInstanceUID);
            dcmObj.putString(Tag.StudyDate, VR.DA, studyDate);
            dcmObj.putString(Tag.ContentDate, VR.DA, contentDate);
            dcmObj.putString(Tag.StudyTime, VR.TM, studyTime);
            dcmObj.putString(Tag.ContentTime, VR.TM, contentTime);
            dcmObj.putString(Tag.AccessionNumber, VR.SH, accessionNumber);
            dcmObj.putString(Tag.Modality, VR.CS, "KO");
            dcmObj.putString(Tag.Manufacturer, VR.LO, manufacturer);
            dcmObj.putString(Tag.ReferringPhysicianName, VR.PN, referringPhysician);
            dcmObj.putString(Tag.StudyDescription, VR.LO, studyDescription);
//                System.out.print(dcmObj);
            dos.writeDicomFile(dcmObj);
            dcmObj.clear();
            dos.writeHeader(Tag.ReferencedPerformedProcedureStepSequence, VR.SQ, -1);
            dos.writeHeader(Tag.SequenceDelimitationItem, null, 0);
            dcmObj.putString(Tag.PatientName, VR.PN, patientName);
            dcmObj.putString(Tag.PatientID, VR.LO, patientID);
            dcmObj.putString(Tag.IssuerOfPatientID, VR.LO, "UCMC");
            dcmObj.putString(Tag.PatientBirthDate, VR.DA, patientBirthDate);
            dcmObj.putString(Tag.PatientSex, VR.CS, patientSex);
            dcmObj.putString(Tag.StudyInstanceUID, VR.UI, studyInstanceUID);
            dcmObj.putString(Tag.SeriesInstanceUID, VR.UI, seriesInstanceUID);
            dcmObj.putString(Tag.StudyID, VR.SH, studyID);
            dcmObj.putString(Tag.SeriesNumber, VR.IS, seriesNumber);
            dcmObj.putString(Tag.InstanceNumber, VR.IS, "1");
            dos.writeDataset(dcmObj, transferSyntaxUID);
        } catch (IOException e) {
            System.out.println("WriteKOS error: " + e.getMessage());
            lp.getLog().error("WriteKOS error: " + e.getMessage());
            throw new TransferContentException("WriteKOS error: " + e.getMessage(), WriteKOS.class.getName());
        } catch (Exception e) {
            System.out.println("WriteKOS error: " + e.getMessage());
            lp.getLog().error("WriteKOS error: " + e.getMessage());
            throw new TransferContentException("WriteKOS error: " + e.getMessage(), WriteKOS.class.getName());
        }
    }

}
