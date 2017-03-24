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
package org.rsna.isn.transfercontent.test;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.rsna.isn.dao.JobDao;
import org.rsna.isn.domain.DicomStudy;
import org.rsna.isn.domain.Job;
import org.rsna.isn.transfercontent.dcm.KosGenerator;
import org.rsna.isn.transfercontent.ihe.Iti41;
import org.rsna.isn.transfercontent.ihe.Iti8;
import org.rsna.isn.transfercontent.ihe.Iti9;
import org.rsna.isn.util.Environment;

/**
 *
 * @author wtellis
 * @author Clifton Li
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
		Environment.init("transfer");
                   
                Job job = new JobDao().getJobById(22235);
                JobDao dao = new JobDao();
                
                Iti8 reg = new Iti8(job);
		reg.registerPatient();
                System.out.println("test");
                Iti9 reg9 = new Iti9(job);
		String globalId = reg9.pixQuery();
                


                dao.updateGlobalId(globalId, job);   
                Iti41.init();
                
                File tmpDir = Environment.getTmpDir();

		try
		{
			//KosGenerator gen = new KosGenerator(job);

			File homeDir = new File(System.getProperty("user.home"));
			//File debugFile = new File(homeDir, "submission-set.xml");

                        
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
                                                System.out.println("report submmitted");
						iti41.submitDocuments(debugFile);
                                                
					}
                        
                        /*
			for (DicomStudy study : gen.processFiles().values())
			{
				Iti41 iti41 = new Iti41(study);

				iti41.submitDocuments(debugFile);

				break;
			}
                        */
		}
		finally
		{
			File tmp = Environment.getTmpDir();
			File jobDir = new File(tmp, Integer.toString(job.getJobId()));

			FileUtils.deleteDirectory(jobDir);
		}
	}

}
