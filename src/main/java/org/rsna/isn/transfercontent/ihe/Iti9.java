/* Copyright (c) <2016>, <Radiological Society of North America>
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
package org.rsna.isn.transfercontent.ihe;

import java.net.URI;
import java.sql.SQLException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.ohf.hl7v2.core.message.MessageManager;
import org.openhealthtools.ihe.atna.auditor.PIXSourceAuditor;
import org.openhealthtools.ihe.common.mllp.MLLPDestination;
import org.openhealthtools.ihe.pix.consumer.PixConsumer;
import org.openhealthtools.ihe.pix.consumer.PixConsumerQuery;
import org.openhealthtools.ihe.pix.consumer.PixConsumerResponse;
import org.openhealthtools.ihe.utils.IHEException;
import org.rsna.isn.dao.ConfigurationDao;
import org.rsna.isn.dao.JobDao;
import org.rsna.isn.domain.Job;
import org.rsna.isn.util.Constants;

/**
 * This class implements the ITI-9 (Patient ID Cross Reference Query) transaction.
 *
 *
 * @author Clifton Li
 * @version 5.0.0
 */
public class Iti9
{
	private static final Logger logger = Logger.getLogger(Iti9.class);

	private static final URI pix;

	private static final URI registry;

	private static final MessageManager manager = MessageManager.getFactory();

	private final Job job;
        
        private static String sourceId;

	static
	{
		try
		{
			ConfigurationDao dao = new ConfigurationDao();

			String pixUri = dao.getConfiguration("iti8-pix-uri");
			if (StringUtils.isBlank(pixUri))
				throw new ExceptionInInitializerError("iti8-pix-uri is blank");

			pix = new URI(pixUri);





			String regUri = dao.getConfiguration("iti8-reg-uri");
			if (StringUtils.isBlank(regUri))
				throw new ExceptionInInitializerError("iti8-reg-uri is blank");

			registry = new URI(regUri);

			PIXSourceAuditor.getAuditor().getConfig().setAuditorEnabled(false);
                        
                        sourceId = dao.getConfiguration("iti41-source-id");
                        
		}
		catch (Exception ex)
		{
			throw new ExceptionInInitializerError(ex);
		}
	}

	/**
	 * Create an instance of this class.
	 *
	 * @param job An job instance. Not be null.  
	 */
	public Iti9(Job job)
	{
		this.job = job;
	}

	/**
	 * Implementation of Patient Identifier Cross-Reference.
	 *
	 * @throws IHEException If there was an uncaught exception while attempting
	 * to register the patient.
	 * @throws ClearinghouseException  If the clearinghouse returned an error while
	 * attempting to register the patient.
         * @throws SQLException  If the database can not be updated.
	 */
	public void retrieveGlobalPatientId() throws IHEException, ClearinghouseException, SQLException
	{
		pixQuery(pix, "PIX");
	}

	private void pixQuery(URI uri, String remoteType) throws IHEException, ClearinghouseException, SQLException
	{
		PixConsumer pixQuery = new PixConsumer();

		MLLPDestination mllp = new MLLPDestination(uri);
		MLLPDestination.setUseATNA(false);
		pixQuery.setMLLPDestination(mllp);

                
                //PixConsumerQuery msg = pixQuery.createQuery(job.getSingleUsePatientId(), null, 
                //                "1.3.6.1.4.1.21367.13.20.1000", Constants.RSNA_UNIVERSAL_ID_TYPE); 
                PixConsumerQuery msg = pixQuery.createQuery("IHERED-10000", null, 
                                "1.3.6.1.4.1.21367.13.20.1000", Constants.RSNA_UNIVERSAL_ID_TYPE); 
                
                msg.changeDefaultCharacterSet("UNICODE");
                
                /*
		PixMsgRegisterOutpatient msg2 = new PixMsgRegisterOutpatient(manager,
				null, job.getSingleUsePatientId(),
				null, Constants.RSNA_UNIVERSAL_ID,
				Constants.RSNA_UNIVERSAL_ID_TYPE);
                
		msg2.addOptionalPatientNameFamilyName("RSNA ISN");
		msg2.addOptionalPatientNameGivenName("RSNA ISN");
                */
                
		PixConsumerResponse rsp = pixQuery.sendQuery(msg, false);

		String code = rsp.getResponseAckCode(false);

                String error = rsp.getField("MSA-3");

		if ("AE".equals(code))
		{
			String chMsg = "Clearinghouse " + remoteType
					+ " failed to process ITI-8 message.  Error returned was: " + error;

			throw new ClearinghouseException(chMsg);
		}
		else if ("AR".equals(code))
		{

			if (error.startsWith("PIX-10000:"))
			{
				logger.info("Clearinghouse " + remoteType
						+ " reports patient id " + job.getSingleUsePatientId()
						+ " has already been registered.");
			}
			else
			{
				String chMsg = "Clearinghouse " + remoteType
						+ " rejected ITI-8 message. Error returned was: " + error;

				throw new ClearinghouseException(chMsg);
			}
		}
                else
                {

                        String globalId = rsp.getField("PID-3-1");

                        if (!globalId.isEmpty())
                        {
                                JobDao dao = new JobDao();
                                dao.updateGlobalId(globalId, job);   
                                
                                logger.info("Received Globlal ID " + globalId);
                        }
                        else
                        {
                                String chMsg = "Clearinghouse " + remoteType
						+ " did not return a global ID.";

				throw new ClearinghouseException(chMsg);
                        }
                       
                            
                }
	}

}
