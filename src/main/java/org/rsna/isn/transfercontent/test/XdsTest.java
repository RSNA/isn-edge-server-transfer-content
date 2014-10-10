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
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import org.apache.commons.lang.RandomStringUtils;
import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomElement;
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
import org.rsna.isn.domain.Job;
import org.rsna.isn.transfercontent.ihe.ClearinghouseException;
import org.rsna.isn.transfercontent.ihe.Iti41XdsTest;
import org.rsna.isn.transfercontent.ihe.Iti8;
import org.rsna.isn.util.Environment;

/**
 *
 * @author Clifton Li
 * @version 3.2.0
 * @since 3.2.0
 */

public class XdsTest {

        
        public static void main(String[] args)  
        {       
                Environment.init("transfer");
                System.out.print(submit());
        }
    
        public static String submit()  
        {           
                Job job = new Job();
                String singleUsePatientId = RandomStringUtils.randomAlphanumeric(64);
                job.setSingleUsePatientId(singleUsePatientId);

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

                DicomObject defaults = createDefaults();
                BufferedImage image = createImage();

                DicomObject dcm = createDicomObj(defaults, image);                  
                DicomObject kos = createKosObj(defaults,dcm);

                try
                {
                        byte[] dicomBa = createDicomByteArray(dcm, image);
                        byte[] kosBa = createKosByteArray(kos);

                        Iti41XdsTest.init();
                        Iti41XdsTest test = new Iti41XdsTest(singleUsePatientId);

                        test.submitDocuments(dicomBa, kosBa, dcm, kos);
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
              
        public static DicomObject createKosObj(DicomObject defaults, DicomObject dcm)
        {
                DicomObject kos = new BasicDicomObject();
                defaults.copyTo(kos);

                kos.putString(Tag.Modality, VR.CS, "KO");
                kos.putString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID());
                kos.putNull(Tag.ReferencedPerformedProcedureStepSequence, VR.SQ);

                kos.putDate(Tag.ContentDate, VR.DA, new Date(System.currentTimeMillis()));
                kos.putDate(Tag.ContentTime, VR.TM, new Date(System.currentTimeMillis()));

                DicomObject evidenceSeq = new BasicDicomObject();
                evidenceSeq.putString(Tag.StudyInstanceUID, VR.UI, dcm.getString(Tag.StudyInstanceUID, VR.UI));

                DicomElement refSeriesSeq = evidenceSeq.putSequence(Tag.ReferencedSeriesSequence);
                
                DicomObject seriesItem = new BasicDicomObject();
                seriesItem.putString(Tag.SeriesInstanceUID, VR.UI, dcm.getString(Tag.SeriesInstanceUID, VR.UI));

                DicomElement refSopSeq = seriesItem.putSequence(Tag.ReferencedSOPSequence);
                
                DicomObject objItem = new BasicDicomObject();

                objItem.putString(Tag.ReferencedSOPInstanceUID, VR.UI, dcm.getString(Tag.SOPInstanceUID, VR.UI));
                objItem.putString(Tag.ReferencedSOPClassUID, VR.UI, UID.SecondaryCaptureImageStorage);

                refSopSeq.addDicomObject(objItem);
                refSeriesSeq.addDicomObject(seriesItem);

                kos.putNestedDicomObject(Tag.CurrentRequestedProcedureEvidenceSequence, evidenceSeq);

                //
                // SR document content module
                //
                kos.putString(Tag.ValueType, VR.CS, "CONTAINER");

                DicomElement codeSeq = kos.putSequence(Tag.ConceptNameCodeSequence);
                DicomObject codeItem = new BasicDicomObject();
                codeItem.putString(Tag.CodeValue, VR.SH, "113030");
                codeItem.putString(Tag.CodingSchemeDesignator, VR.SH, "DCM");
                codeItem.putString(Tag.CodeMeaning, VR.LO, "Manifest");
                codeSeq.addDicomObject(codeItem);

                kos.putString(Tag.ContinuityOfContent, VR.CS, "SEPARATE");


                DicomElement contentSeq = kos.putSequence(Tag.ContentSequence);

                DicomObject contentItem = new BasicDicomObject();
                contentItem.putString(Tag.ValueType, VR.CS, "IMAGE");
                contentItem.putString(Tag.RelationshipType, VR.CS, "CONTAINS");

                DicomElement contentSopSeq = contentItem.putSequence(Tag.ReferencedSOPSequence);
                DicomObject contentSopItem = new BasicDicomObject();
                contentSopItem.putString(Tag.ReferencedSOPClassUID, VR.UI, UID.SecondaryCaptureImageStorage);
                contentSopItem.putString(Tag.ReferencedSOPInstanceUID, VR.UI, dcm.getString(Tag.SOPInstanceUID, VR.UI));
                contentSopSeq.addDicomObject(contentSopItem);
                contentSeq.addDicomObject(contentItem);

                //
                // SOP common module
                //
                String kosSopInstanceUid = UIDUtils.createUID();
                kos.putString(Tag.SOPInstanceUID, VR.UI, kosSopInstanceUid);

                String kosSopClassUid = UID.KeyObjectSelectionDocumentStorage;
                kos.putString(Tag.SOPClassUID, VR.UI, kosSopClassUid);

                return kos;           
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
        
        public static DicomObject createDefaults() 
        {
                String patientName = (RandomStringUtils.randomAlphabetic(5) + "^" + RandomStringUtils.randomAlphabetic(5) + "^").toUpperCase();
                String referringPhysician = (RandomStringUtils.randomAlphabetic(5) + "^" + RandomStringUtils.randomAlphabetic(5) + "^").toUpperCase();
                
                String patientId = RandomStringUtils.randomNumeric(8);

                Date currentDate = new Date(System.currentTimeMillis());
                String accession = RandomStringUtils.randomNumeric(8);
                String mrn = RandomStringUtils.randomNumeric(8);

                DicomObject dicom = new BasicDicomObject();

                // Add patient related information to the DICOM dataset
                dicom.putString(Tag.PatientName, VR.PN, patientName);
                dicom.putString(Tag.PatientID, VR.LO, patientId);
                dicom.putString(Tag.PatientSex, VR.CS, "M");
                dicom.putDate(Tag.PatientBirthDate, VR.DA, currentDate);

                // Add study related information to the DICOM dataset
                dicom.putString(Tag.AccessionNumber, VR.SH, RandomStringUtils.randomNumeric(8));
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