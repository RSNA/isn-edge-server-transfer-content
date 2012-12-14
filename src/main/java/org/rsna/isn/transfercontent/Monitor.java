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
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.rsna.isn.dao.JobDao;
import org.rsna.isn.domain.Job;
import org.rsna.isn.util.Environment;

/**
 * This class monitors the RSNA database for new jobs. If it finds a
 * new job, it will spawn a worker thread to process the job.  Currently a max
 * of five concurrent worker threads are allowed.
 *
 * @author Wyatt Tellis
 * @version 2.1.0
 *
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
	public void run()
	{
		File dcmDir = Environment.getDcmDir();
		File tmpDir = Environment.getTmpDir();


		logger.info("Started monitor thread");
		logger.info("DICOM directory: " + dcmDir);
		logger.info("Temp directory: " + tmpDir);


		JobDao dao = new JobDao();
		try
		{
			Set<Job> interruptedJobs = new HashSet();
			interruptedJobs.addAll(dao.getJobsByStatus(Job.RSNA_STARTED_TRANSFER_CONTENT));
			interruptedJobs.addAll(dao.getJobsByStatus(Job.RSNA_STARTED_KOS_GENERATION));
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


				sleep(1000);
			}
			catch (InterruptedException ex)
			{
				logger.fatal("Monitor thread interrupted", ex);

				break;
			}
			catch (Throwable ex)
			{
				logger.fatal("Uncaught exception while processing jobs.", ex);

				break;
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
