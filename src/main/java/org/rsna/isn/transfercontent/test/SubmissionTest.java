/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsna.isn.transfercontent.test;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.rsna.isn.dao.JobDao;
import org.rsna.isn.domain.DicomStudy;
import org.rsna.isn.domain.Job;
import org.rsna.isn.transfercontent.dcm.KosGenerator;
import org.rsna.isn.transfercontent.ihe.Iti41;
import org.rsna.isn.util.Environment;

/**
 *
 * @author wtellis
 */
public class SubmissionTest
{
	/**
	 * @param args the command line arguments
	 */
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws Exception
	{
		Job job = new JobDao().getJobById(50);
		try
		{
			KosGenerator gen = new KosGenerator(job);



			for (DicomStudy study : gen.processFiles().values())
			{
				Iti41 iti41 = new Iti41(study);

				iti41.submitDocuments();
			}

		}
		finally
		{
			File tmp = Environment.getTmpDir();
			File jobDir = new File(tmp, Integer.toString(job.getJobId()));

			FileUtils.deleteDirectory(jobDir);
		}
	}

}
