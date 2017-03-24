/* Copyright (c) <2017>, <Radiological Society of North America>
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
package org.rsna.isn.transfercontent;

import java.io.File;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openhealthtools.ihe.utils.IHEException;
import org.rsna.isn.dao.JobDao;
import org.rsna.isn.dao.SmsDao;
import org.rsna.isn.domain.DicomStudy;
import org.rsna.isn.domain.Exam;
import org.rsna.isn.domain.Job;
import org.rsna.isn.domain.MsgTemplate;
import org.rsna.isn.domain.Sms;
import org.rsna.isn.transfercontent.dcm.KosGenerator;
import org.rsna.isn.transfercontent.ihe.ClearinghouseException;
import org.rsna.isn.transfercontent.ihe.Iti41;
import org.rsna.isn.transfercontent.ihe.Iti8;
import org.rsna.isn.transfercontent.ihe.Iti9;
import org.rsna.isn.util.Environment;

/**
 * Worker thread that processes jobs.
 *
 * @author Wyatt Tellis
 * @author Clifton Li
   @version 5.0.0
 * @since 2.1.0
 */
class Worker extends Thread
{
	private static final Logger logger = Logger.getLogger(Worker.class);

	private static final File dcmDir = Environment.getDcmDir();

	private static final File tmpDir = Environment.getTmpDir();

	private final Job job;

	private final Exam exam;

	Worker(ThreadGroup group, Job job)
	{
		super(group, "worker-" + job.getJobId());

		this.job = job;

		this.exam = job.getExam();
	}

	@Override
	@SuppressWarnings("UnusedAssignment")
	public void run()
	{
		logger.info("Started worker thread for " + job);

		try
		{
			JobDao dao = new JobDao();
			try
			{
				//
				// Register patient
				//
                            
				try
				{
					dao.updateStatus(job, Job.RSNA_STARTED_PATIENT_REGISTRATION);

					logger.info("Started patient registration for " + job);

					Iti8 iti8 = new Iti8(job);
					iti8.registerPatient();

					logger.info("Completed patient registration for " + job);
				}
				catch (ClearinghouseException ex)
				{
					String chMsg = ex.getMessage();
					String errorMsg = "Unable to register patient for "
							+ job + ". " + chMsg;

					logger.error(errorMsg);

					dao.updateStatus(job, Job.RSNA_FAILED_TO_REGISTER_PATIENT, chMsg);

					return;
				}
				catch (IHEException ex)
				{
					logger.error("Unable to register patient for " + job + ". ", ex);

					dao.updateStatus(job, Job.RSNA_FAILED_TO_REGISTER_PATIENT, ex);

					return;
				}
                                
                                //
				// Retrieve Global ID 
				//
				try
				{
					dao.updateStatus(job, Job.RSNA_RETRIEVING_GLOBAL_ID);

					logger.info("Retrieving global ID " + job);

					Iti9 iti9 = new Iti9(job);
					String globalId = iti9.pixQuery();
                                        
                                        dao.updateGlobalId(globalId, this.job);   

					logger.info("Received global ID: " + job);
				}
				catch (ClearinghouseException ex)
				{
					String chMsg = ex.getMessage();
					String errorMsg = "Unable to retreive global ID for "
							+ job + ". " + chMsg;

					logger.error(errorMsg);

					dao.updateStatus(job, Job.RSNA_FAILED_TO_RETRIEVE_GLOBAL_ID, chMsg);

					return;
				}
				catch (IHEException ex)
				{
					logger.error("Unable to retreive global ID for " + job + ". ", ex);

					dao.updateStatus(job, Job.RSNA_FAILED_TO_RETRIEVE_GLOBAL_ID, ex);

					return;
				}
                                catch (SQLException ex)
				{
					logger.error("Can not update database. Unable to retreive global ID for " + job + ". ", ex);

					dao.updateStatus(job, Job.RSNA_FAILED_TO_RETRIEVE_GLOBAL_ID, ex);

					return;
				}
				//
				// Submit documents to registry
				//
				try
				{
					dao.updateStatus(job, Job.RSNA_STARTED_DOCUMENT_SUBMISSION);

					logger.info("Started document submission for " + job);

					File jobDir = new File(tmpDir, Integer.toString(job.getJobId()));
					File studiesDir = new File(jobDir, "studies");
                                        
                                        
                                        Map<String, DicomStudy> studies =
						Collections.<String, DicomStudy>emptyMap();
                                        
                                        KosGenerator gen = new KosGenerator(job);
					studies = gen.processFiles();
                                        
					for (DicomStudy study : studies.values())
					{
						File studyDir = new File(studiesDir, study.getStudyUid());
						File debugFile = new File(studyDir, "submission-set.xml");

						Iti41 iti41 = new Iti41(study);
                                                iti41.submitReport(debugFile);
						iti41.submitDocuments(debugFile);
                                                
					}

					logger.info("Completed document submission for " + job);
				}
				catch (ClearinghouseException ex)
				{
					String chMsg = ex.getMessage();
					String errorMsg = "Unable to submit documents for "
							+ job + ". " + chMsg;

					logger.error(errorMsg);

					dao.updateStatus(job, Job.RSNA_FAILED_TO_SUBMIT_DOCUMENTS, chMsg);

					return;
				}
				catch (Exception ex)
				{
					logger.error("Unable to submit documents for " + job + ". ", ex);

					dao.updateStatus(job, Job.RSNA_FAILED_TO_SUBMIT_DOCUMENTS, ex);

					return;
				}

				File jobDir = new File(tmpDir, Integer.toString(job.getJobId()));
				FileUtils.deleteDirectory(jobDir);


				File jobDcmDir = new File(dcmDir, Integer.toString(job.getJobId()));
				FileUtils.deleteDirectory(jobDcmDir);

				dao.updateStatus(job, Job.RSNA_COMPLETED_TRANSFER_TO_CLEARINGHOUSE);

				logger.info("Successfully transferred content to clearinghouse for " + job);
                                
                                SmsDao smsDao = new SmsDao();
                                Sms sms = smsDao.getSms();
                                
                                if (dao.isJobSetComplete(job.getJobSetId()) && smsDao.isSmsEnabled() && !job.getPhoneNumber().isEmpty())
                                {      
                                        MsgTemplate smsMessage = new MsgTemplate(); 
                                
                                        
                                        smsMessage.setTemplate(sms.getMessage());
                                        smsMessage.setAccessCode(job.getAccessCode()); 
                                        sms.setMessage(smsMessage.compose());
                                        sms.setRecipient(job.getPhoneNumber());
     
                                        smsDao.addToQueue(sms);
                                }
			}
			catch (Throwable ex)
			{
				logger.error("Uncaught exception while processing job " + job, ex);

				dao.updateStatus(job, Job.RSNA_FAILED_TO_TRANSFER_TO_CLEARINGHOUSE, ex);
			}




		}
		catch (Throwable ex)
		{
			logger.error("Uncaught exception while updating job " + job, ex);
		}
		finally
		{
			logger.info("Stopped worker thread");
		}
	}

}
