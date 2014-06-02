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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.RandomStringUtils;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.StopTagInputHandler;
import org.dcm4che2.util.UIDUtils;
import org.openhealthtools.ihe.utils.IHEException;
import org.rsna.isn.domain.DicomKos;
import org.rsna.isn.domain.DicomObject;
import org.rsna.isn.domain.DicomSeries;
import org.rsna.isn.domain.DicomStudy;
import org.rsna.isn.domain.Exam;
import org.rsna.isn.domain.Job;
import org.rsna.isn.transfercontent.ihe.ClearinghouseException;
import org.rsna.isn.transfercontent.ihe.Iti41;
import org.rsna.isn.transfercontent.ihe.Iti8;
import org.rsna.isn.util.Environment;

/**
 *
 * @author Clifton Li
 * @version 3.2.0
 * @since 3.2.0
 */

public class XDSTest {
    
            public static void main(String[] args) throws Exception 
            {
                    System.out.print(submit());
            }
            
            public static String submit()
            {
                    Environment.init("transfer");
                    
                    //Manually create exam object
                    Exam exam = new Exam();                    
                    exam.setStatus(Exam.FINALIZED);
                    exam.setMrn("1");
                    exam.setPatientName("TEST^TEST^");
                    exam.setReport("This is a test submission.");
                    
                    //Manually create job obj
                    Job job = new Job();
                    job.setSingleUsePatientId(RandomStringUtils.randomAlphanumeric(64));
                    job.setJobId(1);
                    job.setJobSetId(1);
                    job.setRemainingRetries(1);
                    job.setExam(exam);
                    job.setSendOnComplete(true);
                    
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

                    //File debugFile = new File("/tmp", "submission-set.xml");
                        
                    DicomInputStream in = null;
                    DicomStudy dicomStudy = new DicomStudy();
                    
                    try
                    {
                            URL kosPath = XDSTest.class.getResource("resources/xds/kos.dcm");
                            URL dcmPath = XDSTest.class.getResource("resources/xds/dcm");
                            File dcmDir = new File(dcmPath.toURI());

                            //Manually create DicomKos obj
                            DicomKos dicomKos = new DicomKos();
                            dicomKos.setFile(new File(kosPath.toURI()));
                            dicomKos.setSopInstanceUid(UIDUtils.createUID());
                            
                            String SeriesInstanceUID = String.valueOf(Tag.SeriesInstanceUID);
                            
                            //Manually create DicomSeries obj
                            DicomSeries dicomSeries = new DicomSeries();
                            dicomSeries.setSeriesUid(SeriesInstanceUID);
                            
                            //Manually create DicomStudy obj
                            dicomStudy.setKos(dicomKos);
                            dicomStudy.getSeries().put(SeriesInstanceUID,dicomSeries);
                            dicomStudy.setJob(job);
                            dicomStudy.setStudyDateTime(new Date());

                           
                            StopTagInputHandler stop = new StopTagInputHandler(Tag.PixelData);
                            Iterator<File> it = FileUtils.iterateFiles(dcmDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

                            while (it.hasNext())
                            {
                                    File srcFile = it.next();
                                    in = new DicomInputStream(srcFile);
                                    in.setHandler(stop);

                                    org.dcm4che2.data.DicomObject fmi = in.readFileMetaInformation();
                                    if (fmi == null)
                                            throw new IOException(srcFile + " is not a DICOM part-10 file");

                                    org.dcm4che2.data.DicomObject header = in.readDicomObject();

                                    String transferSyntaxUid = in.getTransferSyntax().uid();

                                    in.close();
                                    in = null;

                                    String studyUid = header.getString(Tag.StudyInstanceUID);
                                    String seriesUid = header.getString(Tag.SeriesInstanceUID);
                                    String sopInstanceUid = header.getString(Tag.SOPInstanceUID);
                                    String sopClassUid = header.getString(Tag.SOPClassUID);

                                    DicomObject obj = new DicomObject();
                                    obj.setSopClassUid(sopClassUid);
                                    obj.setSopInstanceUid(sopInstanceUid);
                                    obj.setTransferSyntaxUid(transferSyntaxUid);
                                    obj.setFile(srcFile);

                                    dicomSeries.getObjects().put(sopInstanceUid, obj);
                            }
                    }
                    
                    catch (URISyntaxException ex)
                    {
                            return "Can not find resource files" + ex;
                    }
                    catch (IOException ex)
                    {
                            return "Can not open resource files" + ex;

                    }
                    catch (Exception ex)
                    {
                            return ex.getMessage();
                    }
                    finally
                    {
                            IOUtils.closeQuietly(in);
                    }

                    try
                    {
                            Iti41.init();
                            Iti41 iti41 = new Iti41(dicomStudy);
                            iti41.submitDocuments(null);
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
}
