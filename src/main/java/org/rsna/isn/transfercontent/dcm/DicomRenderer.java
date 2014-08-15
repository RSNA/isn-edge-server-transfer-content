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
package org.rsna.isn.transfercontent.dcm;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import org.apache.log4j.Logger;
import org.apache.fop.area.PageViewport;
import org.apache.fop.render.java2d.Java2DRenderer;
import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.dcm4che2.data.VR;
import org.dcm4che2.imageio.plugins.dcm.DicomStreamMetaData;
import org.dcm4che2.imageioimpl.plugins.dcm.DicomImageWriterSpi;
import org.dcm4che2.util.UIDUtils;
import org.rsna.isn.domain.DicomStudy;
import org.rsna.isn.domain.Exam;

/**
 *
 * @author Clifton Li
 * @version 3.2.0
 * @since 3.2.0
 */
public class DicomRenderer extends Java2DRenderer {

        private Exam exam;
        private DicomStudy study;
        private File reportSeriesDir;
        private String reportSeriesUID;
        private int seriesNumber;

        private static final Logger logger = Logger.getLogger(DicomRenderer.class);
        
        public DicomRenderer(Exam exam, DicomStudy study, File reportSeriesDir, String reportSeriesUID,int seriesNumber)
        {
                this.exam = exam;
                this.study = study;
                this.reportSeriesDir = reportSeriesDir;
                this.reportSeriesUID = reportSeriesUID;
                this.seriesNumber = seriesNumber;
        }

        @Override
        public void stopRenderer() throws IOException {

            super.stopRenderer();

            for (int pageNumber = 0; pageNumber < pageViewportList.size(); pageNumber++) 
            {
                    // Do the rendering: get the image for this page
                    PageViewport pv = (PageViewport)pageViewportList.get(pageNumber);
                    BufferedImage image = getPageImage(pv);

                    generateDicom(image,pageNumber+1);  
            }
        }

        public void generateDicom(BufferedImage image,int pageNumber)
        {   
                BufferedImage page = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
                page.getGraphics().drawImage(image, 0, 0, null);

                try
                {                
                        ImageWriter writer = new DicomImageWriterSpi().createWriterInstance();

                        FileImageOutputStream output = new FileImageOutputStream(new File(reportSeriesDir, UIDUtils.createUID() + ".dcm"));
                        writer.setOutput(output);

                        DicomObject dicom = createDicomHeader(page,pageNumber);

                        DicomStreamMetaData writeMeta = (DicomStreamMetaData) writer.getDefaultStreamMetadata(null);
                        writeMeta.setDicomObject(dicom);


                        IIOImage iioimage = new IIOImage(page, null, null);
                        writer.write(writeMeta, iioimage, null);
                        
                        writer.dispose();
                        output.close();  
                }
                catch (Exception ex)
                {
                        logger.error("Uncaught exception while genertaing dicom report ",ex);
                }
        }
        
        public DicomObject createDicomHeader(BufferedImage image,int pageNumber) 
        {
                int colorComponents = image.getColorModel().getNumColorComponents();
                int bitsPerPixel = image.getColorModel().getPixelSize();
                int bitsAllocated = (bitsPerPixel / colorComponents);
                int samplesPerPixel = colorComponents;

                DicomObject dicom = new BasicDicomObject();

                // Add patient related information to the DICOM dataset
                dicom.putString(Tag.PatientName, VR.PN, study.getPatientName());
                dicom.putString(Tag.PatientID, VR.LO, study.getPatientId());
                dicom.putString(Tag.PatientSex, VR.CS, study.getSex());
                dicom.putDate(Tag.PatientBirthDate, VR.DA, study.getBirthdate());

                // Add study related information to the DICOM dataset
                dicom.putString(Tag.AccessionNumber, VR.SH, study.getAccessionNumber());
                dicom.putString(Tag.StudyID, VR.SH, study.getStudyId());
                dicom.putString(Tag.StudyDescription, VR.LO, study.getStudyDescription());
                dicom.putDate(Tag.StudyDate, VR.DA, study.getStudyDateTime());
                dicom.putDate(Tag.StudyTime, VR.TM, study.getStudyDateTime());
                dicom.putString(Tag.ReferringPhysicianName, VR.PN, study.getReferringPhysician());

                // Add series related information to the DICOM dataset
                dicom.putInt(Tag.SeriesNumber, VR.IS, seriesNumber);
                dicom.putDate(Tag.SeriesDate, VR.DA, exam.getStatusTimestamp());
                dicom.putDate(Tag.SeriesTime, VR.TM, exam.getStatusTimestamp());
                dicom.putString(Tag.SeriesDescription, null, "REPORT");
                dicom.putString(Tag.Modality, VR.CS, "OT"); // secondary capture

                // Add image related information to the DICOM dataset
                dicom.putInt(Tag.InstanceNumber, VR.IS, pageNumber);
                dicom.putInt(Tag.SamplesPerPixel, VR.US, samplesPerPixel);
                dicom.putString(Tag.PhotometricInterpretation, VR.CS, "MONOCHROME1");
                dicom.putInt(Tag.Rows, VR.US, image.getHeight());
                dicom.putInt(Tag.Columns, VR.US, image.getWidth());
                dicom.putInt(Tag.BitsAllocated, VR.US, bitsAllocated);
                dicom.putInt(Tag.BitsStored, VR.US, bitsAllocated);
                dicom.putInt(Tag.HighBit, VR.US, bitsAllocated - 1);
                dicom.putInt(Tag.PixelRepresentation, VR.US, 0);

                // Add the unique identifiers
                dicom.putString(Tag.SOPClassUID, VR.UI, UID.SecondaryCaptureImageStorage);
                dicom.putString(Tag.StudyInstanceUID, VR.UI, study.getStudyUid());
                dicom.putString(Tag.SeriesInstanceUID, VR.UI, reportSeriesUID);
                dicom.putString(Tag.SOPInstanceUID, VR.UI, UIDUtils.createUID());


                // Add the default character set
                dicom.putString(Tag.SpecificCharacterSet, VR.CS, "ISO_IR 100");

                dicom.initFileMetaInformation(UID.ImplicitVRLittleEndian);

                return dicom;
        }
        @Override
        public String getMimeType() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
}
                