/* Copyright (c) <2010>, <Radiological Society of North America>
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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rsna.isn.dao.ConfigurationDao;
import org.rsna.isn.dao.EmailDao;
import org.rsna.isn.dao.JobDao;
import org.rsna.isn.dao.SmsDao;
import org.rsna.isn.domain.Email;
import org.rsna.isn.domain.Job;
import org.rsna.isn.domain.Sms;
import org.rsna.isn.util.EmailUtil;
import org.rsna.isn.util.Environment;
import org.rsna.isn.util.SmsUtil;

/**
 * This class monitors the RSNA database for new jobs. If it finds a new job, it
 * will spawn a worker thread to process the job. Currently a max of five
 * concurrent worker threads are allowed.
 *
 * @author Wyatt Tellis
 * @version 3.2.0
 * @since 1.0.0
 */
class Monitor extends Thread
{
	private static final Logger logger = Logger.getLogger(Monitor.class);

	private final ThreadGroup group = new ThreadGroup("workers");

	private boolean keepRunning;

	Monitor()
	{
		super("monitor");
	}

	@Override
	@SuppressWarnings("SleepWhileInLoop")
	public void run()
	{
		File dcmDir = Environment.getDcmDir();
		File tmpDir = Environment.getTmpDir();


		logger.info("Started monitor thread");
		logger.info("DICOM directory: " + dcmDir);
		logger.info("Temp directory: " + tmpDir);

		int retryDelay;
		try
		{
			ConfigurationDao configDao = new ConfigurationDao();
			String value = configDao.getConfiguration("retry-delay-in-mins");

			int delay = NumberUtils.toInt(value, 10);
			logger.info("Setting retry delay to " + delay + " minute(s).");

			retryDelay = (int) (delay * DateUtils.MILLIS_PER_MINUTE * -1);
		}
		catch (Exception ex)
		{
			logger.fatal("Uncaught exception while loading configuration.", ex);

			return;
		}



		JobDao dao = new JobDao();
		try
		{
			Set<Job> interruptedJobs = new HashSet<Job>();
			interruptedJobs.addAll(dao.getJobsByStatus(Job.RSNA_STARTED_TRANSFER_CONTENT));
                        interruptedJobs.addAll(dao.getJobsByStatus(Job.RSNA_RETRIEVING_GLOBAL_ID));
			interruptedJobs.addAll(dao.getJobsByStatus(Job.RSNA_STARTED_PATIENT_REGISTRATION));
			interruptedJobs.addAll(dao.getJobsByStatus(Job.RSNA_STARTED_DOCUMENT_SUBMISSION));

			for (Job job : interruptedJobs)
			{
				dao.updateStatus(job, Job.RSNA_WAITING_FOR_TRANSFER_CONTENT, "Retrying job");

				logger.info("Retrying " + job);
			}
		}
		catch (Exception ex)
		{
			logger.fatal("Uncaught exception while restarting interrupted jobs.", ex);

			return;
		}




		keepRunning = true;
		while (keepRunning)
		{
			try
			{
				if (group.activeCount() < 5)
				{
					Set<Job> jobs = dao.getJobsByStatus(Job.RSNA_WAITING_FOR_TRANSFER_CONTENT);


					for (Job job : jobs)
					{
						if (group.activeCount() >= 5)
							break;

						dao.updateStatus(job, Job.RSNA_STARTED_TRANSFER_CONTENT);

						Worker worker = new Worker(group, job);
						worker.start();
					}
				}


				logger.debug("Retrieving list of jobs to retry...");
				Date now = new Date();
				Date lastUpdate = DateUtils.addMilliseconds(now, retryDelay);
				Set<Job> jobsToRetry =
						dao.findRetryableJobs(lastUpdate,
						Job.RSNA_FAILED_TO_REGISTER_PATIENT,
						Job.RSNA_FAILED_TO_RETRIEVE_GLOBAL_ID,
						Job.RSNA_FAILED_TO_SUBMIT_DOCUMENTS);

				for (Job job : jobsToRetry)
				{
					int jobId = job.getJobId();
					if (dao.retryJob(jobId))
					{
						logger.warn("Retried job #" + jobId);
					}
				}

                                //Find emails to send
                                EmailDao eDao = new EmailDao();  
                                Set<Email> emailsToSend = eDao.findEmailsToSend();
                                                      
                                for (Email email : emailsToSend) 
                                {                     
                                    EmailUtil.sendInQueue(email);
                                }

                                //Find SMS to send
                                SmsDao smsDao = new SmsDao();  
                                Set<Sms> smsToSend = smsDao.findSmsToSend();
                                                      
                                for (Sms sms : smsToSend) 
                                {     
                                    try
                                    {
                                        SmsUtil.send(sms.getRecipient(),sms.getMessage());
                                        smsDao.updateQueue(sms.getSmslId(), true, false, "Successful");
                                    }
                                    catch (Exception ex)
                                    {
                                        logger.fatal("Uncaught exception while restarting interrupted jobs.", ex);
                                        smsDao.updateQueue(sms.getSmslId(), false, true, ex.getMessage());
                                    }
                                }
                                sleep(1000);
			}
			catch (InterruptedException ex)
			{
				logger.fatal("Monitor thread interrupted", ex);

				LogManager.shutdown();

				System.exit(1);
			}
			catch (Throwable ex)
			{
				logger.fatal("Uncaught exception while processing jobs. "
						+ "Monitor thread shutdown", ex);

				LogManager.shutdown();

				System.exit(1);
			}
		}



		logger.info("Stopped monitor thread");
	}

	public void stopRunning() throws InterruptedException
	{
		keepRunning = false;

		join(10 * 1000);
	}

}
