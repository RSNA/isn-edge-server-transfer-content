/* Copyright (c) <2014>, <Radiological Society of North America>
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the <RSNA> nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

package org.rsna.isn.transfercontent.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.RandomStringUtils;
import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.dcm4che2.data.VR;
import org.dcm4che2.imageio.plugins.dcm.DicomStreamMetaData;
import org.dcm4che2.imageioimpl.plugins.dcm.DicomImageWriterSpi;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.media.FileMetaInformation;
import org.dcm4che2.util.UIDUtils;
import org.openhealthtools.ihe.utils.IHEException;
import org.rsna.isn.domain.DicomSeries;
import org.rsna.isn.domain.DicomStudy;
import org.rsna.isn.domain.Exam;
import org.rsna.isn.domain.Job;
import org.rsna.isn.transfercontent.ihe.ClearinghouseException;
import org.rsna.isn.transfercontent.ihe.Iti41XdsTest;
import org.rsna.isn.transfercontent.ihe.Iti8;
import org.rsna.isn.transfercontent.ihe.Iti9;
import org.rsna.isn.util.Environment;

/**
 *
 * @author Clifton Li
 * @version 5.0.0
 * @since 3.2.0
 */

public class XdsTest {

        private final static Exam exam = new Exam();
        private static Job job = new Job();
        
        public static void main(String[] args)  
        {       
                Environment.init("transfer");
               
                System.out.print(submit());
        }
    
        public static String submit()  
        {           
                try 
                {   
                    String singleUsePatientId = RandomStringUtils.randomAlphanumeric(64);
                    MessageDigest md;
                    md = MessageDigest.getInstance("SHA-256");
                    md.update(singleUsePatientId.getBytes());
                    byte[] shaDig = md.digest();

                    singleUsePatientId = new String(Hex.encodeHex(shaDig));
                    
                    job.setSingleUsePatientId(singleUsePatientId);
                } 
                catch (NoSuchAlgorithmException ex) 
                {
                    return ex.getMessage();
                }


                try
                {
                        Iti8 iti8 = new Iti8(job);
                        iti8.registerPatient();
                }
                catch (ClearinghouseException ex)
                {
                        return "Unable to register patient. " + ex;
                }
                catch (IHEException ex)
                {
                        return "Unable to register patient. " + ex;
                }

                String globalId;
                String globalAA;
                try
                {
                        Iti9 iti9 = new Iti9(job);
                        Map.Entry response = iti9.pixQuery();
                        
                        globalId = response.getKey().toString();
                        job.setglobalId(globalId);
                        
                        globalAA = response.getValue().toString();
                        job.setglobalAA(globalAA);
                        
                        if (globalId.isEmpty() && globalAA.isEmpty())
                            return "Unable to retreive global ID";         
                }
                catch (ClearinghouseException ex)
                {
                        return "Unable to retreive globalID" + ex;
                }
                catch (IHEException ex)
                {
                        return "Unable to retreive globalID" + ex;
                }
                catch (SQLException ex)
                {
                        return "Unable to retreive globalID" + ex;
                }                
                
                DicomObject defaults = createDefaults();
                BufferedImage image = createImage();

                DicomObject dcm = createDicomObj(defaults, image);                  
                
                exam.setStatus("FINALIZED");
                exam.setReport("This is a test from RSNA ISN");
                exam.setAccNum(RandomStringUtils.randomNumeric(8));
                
                DicomSeries series = new DicomSeries();
                series.setModality("OT");
                
                //series.getObjects().put(UIDUtils.createUID(),dcm);
                DicomStudy study = new DicomStudy();
                study.setStudyUid(UIDUtils.createUID());
                study.setStudyDateTime(new Date(System.currentTimeMillis()));
                study.setStudyDescription("TEST STUDY");
                study.getSeries().put(UIDUtils.createUID(),series);
                
                job.setExam(exam);
                study.setJob(job);
                try
                {
                        byte[] dicomBa = createDicomByteArray(dcm, image);

                        Iti41XdsTest.init();
                        Iti41XdsTest test = new Iti41XdsTest(study, job.getSingleUsePatientId());   
                        File debugFile = new File("/tmp", "submission-set.xml");
                        test.submitReport(debugFile);
                        test.submitTestDocuments(dicomBa);
                }

                catch (ClearinghouseException ex)
                {
                        return "Unable to submit documents. " + ex;
                }
                catch (Exception ex)
                {
                        return "Unable to submit documents. " + ex;
                }
            
                return "Successfully transferred content to clearinghouse.";
        }   
        
        public static byte[] createKosByteArray(DicomObject kos) throws IOException
        {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DicomOutputStream out = new DicomOutputStream(baos);

                String kosSopInstanceUid = kos.getString(Tag.SOPInstanceUID, VR.UI);
                String kosSopClassUid = kos.getString(Tag.SOPClassUID, VR.UI);
                
                String txuid = UID.ImplicitVRLittleEndian;
		FileMetaInformation kosFmi = new FileMetaInformation(kosSopClassUid, kosSopInstanceUid, txuid);
		out.writeFileMetaInformation(kosFmi.getDicomObject());

                out.writeDataset(kos, txuid);
                out.close();

                return baos.toByteArray();
        }
        
        public static DicomObject createDicomObj(DicomObject defaults, BufferedImage image) 
        {
                DicomObject dcm = new BasicDicomObject();
                defaults.copyTo(dcm);
                
                int colorComponents = image.getColorModel().getNumColorComponents();
                int bitsPerPixel = image.getColorModel().getPixelSize();
                int bitsAllocated = (bitsPerPixel / colorComponents);
                int samplesPerPixel = colorComponents;
                
                // Add image related information to the DICOM dataset
                dcm.putString(Tag.SOPInstanceUID, VR.UI, UIDUtils.createUID());
                dcm.putString(Tag.SOPClassUID, VR.UI, UID.SecondaryCaptureImageStorage);
                dcm.putString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID());
                        
                dcm.putInt(Tag.SamplesPerPixel, VR.US, samplesPerPixel);
                dcm.putString(Tag.PhotometricInterpretation, VR.CS, "MONOCHROME1");
                dcm.putInt(Tag.Rows, VR.US, image.getHeight());
                dcm.putInt(Tag.Columns, VR.US, image.getWidth());
                dcm.putInt(Tag.BitsAllocated, VR.US, bitsAllocated);
                dcm.putInt(Tag.BitsStored, VR.US, bitsAllocated);
                dcm.putInt(Tag.HighBit, VR.US, bitsAllocated - 1);
                dcm.putInt(Tag.PixelRepresentation, VR.US, 0);
                dcm.putString(Tag.BurnedInAnnotation, VR.CS, "YES");
                dcm.putString(Tag.ConversionType, VR.CS, "SYN");
                
                // Add the default character set
                dcm.putString(Tag.SpecificCharacterSet, VR.CS, "ISO_IR 100");

                dcm.initFileMetaInformation(UID.ImplicitVRLittleEndian);
                  
                return dcm;
        }
        
        private static DicomObject createDefaults() 
        {
                String patientName = (RandomStringUtils.randomAlphabetic(5) + "^" + RandomStringUtils.randomAlphabetic(5) + "^").toUpperCase();
                String referringPhysician = (RandomStringUtils.randomAlphabetic(5) + "^" + RandomStringUtils.randomAlphabetic(5) + "^").toUpperCase();
                
                String patientId = RandomStringUtils.randomNumeric(8);

                Date currentDate = new Date(System.currentTimeMillis());
                String mrn = RandomStringUtils.randomNumeric(8);

                DicomObject dicom = new BasicDicomObject();

                // Add patient related information to the DICOM dataset
                dicom.putString(Tag.PatientName, VR.PN, patientName);
                dicom.putString(Tag.PatientID, VR.LO, patientId);
                dicom.putString(Tag.PatientSex, VR.CS, "M");
                dicom.putDate(Tag.PatientBirthDate, VR.DA, currentDate);

                // Add study related information to the DICOM dataset
                dicom.putString(Tag.AccessionNumber, VR.SH, exam.getAccNum());
                dicom.putString(Tag.StudyID, VR.SH, RandomStringUtils.randomNumeric(8));
                dicom.putString(Tag.StudyDescription, VR.LO, "TEST STUDY");
                dicom.putDate(Tag.StudyDate, VR.DA, currentDate);
                dicom.putDate(Tag.StudyTime, VR.TM, currentDate);
                dicom.putString(Tag.ReferringPhysicianName, VR.PN, referringPhysician);

                // Add series related information to the DICOM dataset
                dicom.putString(Tag.SeriesNumber, VR.IS, "1");
                dicom.putDate(Tag.SeriesDate, VR.DA, currentDate);
                dicom.putDate(Tag.SeriesTime, VR.TM, currentDate);
                dicom.putString(Tag.SeriesDescription, VR.LO, "TEST SERIES");
                dicom.putString(Tag.Modality, VR.CS, "OT");
                dicom.putInt(Tag.InstanceNumber, VR.IS, 1);
                
                // Add the unique identifiers
                dicom.putString(Tag.StudyInstanceUID, VR.UI, UIDUtils.createUID());

                // General equipment module
                dicom.putString(Tag.Manufacturer, VR.LO, "RSNA");

                return dicom;
        }
        
        
        public  static BufferedImage createImage()
        {
                BufferedImage image = new BufferedImage(512, 512, BufferedImage.TYPE_BYTE_GRAY);

                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z");
                String text = "Test DICOM created on \n" + dateFormat.format(date);

                //write text on image
                Graphics2D g2d = image.createGraphics();
                g2d.setColor(Color.white);
                g2d.drawString(text, 10,40);

                return image;
        }
        
        public  static byte[] createDicomByteArray(DicomObject dicom, BufferedImage image) throws IOException
        {     
                ImageWriter writer = new DicomImageWriterSpi().createWriterInstance();
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                MemoryCacheImageOutputStream memOut = new MemoryCacheImageOutputStream(baos);
                
                writer.setOutput(memOut);
                
                DicomStreamMetaData writeMeta = (DicomStreamMetaData) writer.getDefaultStreamMetadata(null);
                writeMeta.setDicomObject(dicom);

                IIOImage iioimage = new IIOImage(image, null, null);
                writer.write(writeMeta, iioimage, null);

                writer.dispose();  
                    
                return baos.toByteArray();
        }
}