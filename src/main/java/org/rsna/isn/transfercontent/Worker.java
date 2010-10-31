/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsna.isn.transfercontent;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.openhealthtools.ihe.utils.IHEException;
import org.rsna.isn.dao.JobDao;
import org.rsna.isn.domain.DicomStudy;
import org.rsna.isn.domain.Exam;
import org.rsna.isn.domain.Job;
import org.rsna.isn.transfercontent.dcm.KosGenerator;
import org.rsna.isn.transfercontent.ihe.Iti41;
import org.rsna.isn.transfercontent.ihe.Iti8;

/**
 *
 * @author wtellis
 */
public class Worker extends Thread
{
	private static final Logger logger = Logger.getLogger(Worker.class);

	private final Job job;

	private final Exam exam;

	public Worker(ThreadGroup group, Job job)
	{
		super(group, "worker-" + job.getJobId());

		this.job = job;

		this.exam = job.getExam();
	}

	@Override
	public void run()
	{
		logger.info("Started worker thread");

		try
		{
			JobDao dao = new JobDao();
			try
			{
				//
				// Generate KOS objects
				//
				Map<String, DicomStudy> studies = Collections.EMPTY_MAP;
				try
				{
					dao.updateStatus(job, Job.STARTED_KOS_GENERATION, "");

					KosGenerator gen = new KosGenerator(job);
					studies = gen.processFiles();
				}
				catch (IOException ex)
				{
					logger.error("Unable to generate KOS for " + job + ". ", ex);

					dao.updateStatus(job, Job.FAILED_TO_GENERATE_KOS, ex);

					return;
				}




				//
				// Register patient
				//
				try
				{
					dao.updateStatus(job, Job.STARTED_PATIENT_REGISTRATION, "");

					Iti8 iti8 = new Iti8(exam);
					iti8.registerPatient();
				}
				catch (IHEException ex)
				{
					logger.error("Unable to register patient for " + job + ". ", ex);

					dao.updateStatus(job, Job.FAILED_TO_REGISTER_PATIENT, ex);

					return;
				}

				//
				// Submit documents to registry
				//
				try
				{
					dao.updateStatus(job, Job.STARTED_DOCUMENT_SUBMISSION, "");

					for(DicomStudy study : studies.values())
					{
						Iti41 iti41 = new Iti41(study);
						iti41.submitDocuments();
					}
				}
				catch (Exception ex)
				{
					logger.error("Unable to register patient for " + job + ". ", ex);

					dao.updateStatus(job, Job.FAILED_TO_REGISTER_PATIENT, ex);

					return;
				}



				dao.updateStatus(job, Job.COMPLETED_TRANSFER_TO_CLEARINGHOUSE, "");

				logger.info("Successfully transfer content to clearinghouse for " + job);
			}
			catch (Exception ex)
			{
				logger.error("Uncaught exception while processing job " + job, ex);

				dao.updateStatus(job, Job.FAILED_TO_TRANSFER_TO_CLEARINGHOUSE, ex);
			}




		}
		catch (Exception ex)
		{
			logger.error("Uncaught exception while updating job " + job, ex);
		}
		finally
		{
			logger.info("Stopped worker thread");
		}
	}

}
