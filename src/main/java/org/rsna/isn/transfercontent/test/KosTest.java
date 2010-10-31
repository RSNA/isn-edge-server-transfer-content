/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsna.isn.transfercontent.test;

import org.rsna.isn.dao.JobDao;
import org.rsna.isn.domain.Job;
import org.rsna.isn.transfercontent.dcm.KosGenerator;

/**
 *
 * @author wtellis
 */
public class KosTest
{
	/**
	 * @param args the command line arguments
	 */
	public static void main(String argv[]) throws Exception
	{
		Job job = new JobDao().getJobById(50);

		KosGenerator gen = new KosGenerator(job);
		gen.processFiles();

		System.out.println("Done");
	}

}
