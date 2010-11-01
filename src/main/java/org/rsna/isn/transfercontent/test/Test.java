/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsna.isn.transfercontent.test;

import java.util.Map;
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
public class Test
{
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws Exception
	{
		Job job = new JobDao().getJobById(50);
		Exam exam = job.getExam();

		KosGenerator gen = new KosGenerator(job);
		Map<String, DicomStudy> studies = gen.processFiles();

		Iti8 iti8 = new Iti8(exam);
		iti8.registerPatient();

		for (DicomStudy study : studies.values())
		{
			Iti41 iti41 = new Iti41(study);
			iti41.submitDocuments(null);
		}
	}

}
