/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsna.isn.transfercontent;

import java.io.File;
import java.util.Set;
import org.apache.log4j.Logger;
import org.rsna.isn.dao.JobDao;
import org.rsna.isn.domain.Job;
import org.rsna.isn.util.Environment;

/**
 *
 * @author wtellis
 */
public class Monitor extends Thread
{
	private static final Logger logger = Logger.getLogger(Monitor.class);

	private final ThreadGroup group = new ThreadGroup("workers");

	private boolean keepRunning;

	public Monitor()
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
		keepRunning = true;
		while (keepRunning)
		{
			try
			{
				if (group.activeCount() < 5)
				{
					Set<Job> jobs = dao.getJobsByStatus(Job.WAITING_FOR_TRANSFER_CONTENT);


					for (Job job : jobs)
					{
						if (group.activeCount() >= 5)
							break;

						dao.updateStatus(job, Job.STARTED_TRANSFER_CONTENT);

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
			catch (Exception ex)
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
