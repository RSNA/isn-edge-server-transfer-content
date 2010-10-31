/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.test;

import org.rsna.isn.dao.JobDao;
import org.rsna.isn.domain.Exam;
import org.rsna.isn.domain.Job;
import org.rsna.isn.transfercontent.ihe.Iti8;

/**
 *
 * @author wtellis
 */
public class RegistrationTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String argv[]) throws Exception
	{
		Job job = new JobDao().getJobById(50);

		Exam exam = job.getExam();

		Iti8 reg = new Iti8(exam);
		reg.registerPatient();
	}

}
