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
import java.util.AbstractMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
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

	private static final MessageManager manager = MessageManager.getFactory();

	private final Job job;
        
        private static String rsnaAssigningAuthority;

	static
	{
		try
		{
			ConfigurationDao dao = new ConfigurationDao();

			String pixUri = dao.getConfiguration("iti9-pix-uri");
			if (StringUtils.isBlank(pixUri))
				throw new ExceptionInInitializerError("iti9-pix-uri is blank");

			pix = new URI(pixUri);

			rsnaAssigningAuthority = dao.getConfiguration("rsna-assigning-authority");
			if (StringUtils.isBlank(rsnaAssigningAuthority))
				throw new ExceptionInInitializerError("rsna-assigning-authority");                  
                        
			PIXSourceAuditor.getAuditor().getConfig().setAuditorEnabled(false);                
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
         * @return globalId
	 */
        /*
	public String retrieveGlobalPatientId() throws IHEException, ClearinghouseException, SQLException
	{
		return pixQuery(pix, "PIX");
	}
        */
	public Map.Entry<String, String> pixQuery() throws IHEException, ClearinghouseException, SQLException
	{
		PixConsumer pixQuery = new PixConsumer();

		MLLPDestination mllp = new MLLPDestination(pix);
		MLLPDestination.setUseATNA(false);
		pixQuery.setMLLPDestination(mllp);

                String globalId = "";
                String assigningAuthority = "";

                PixConsumerQuery msg = pixQuery.createQuery(job.getSingleUsePatientId(), null, 
                                rsnaAssigningAuthority,Constants.RSNA_UNIVERSAL_ID_TYPE);  
                
                msg.changeDefaultCharacterSet("UNICODE");
                
		PixConsumerResponse rsp = pixQuery.sendQuery(msg, false);

		String code = rsp.getResponseAckCode(false);

                String error = rsp.getField("MSA-3");

		if ("AE".equals(code))
		{
			String chMsg = "Clearinghouse PIX" +
                                        " failed to process ITI-8 message.  Error returned was: " + error;

			throw new ClearinghouseException(chMsg);
		}
		else if ("AR".equals(code))
		{

			if (error.startsWith("PIX-10000:"))
			{
				logger.info("Clearinghouse PIX"
						+ " reports patient id " + job.getSingleUsePatientId()
						+ " has already been registered.");
			}
			else
			{
				String chMsg = "Clearinghouse PIX"
						+ " rejected ITI-8 message. Error returned was: " + error;

				throw new ClearinghouseException(chMsg);
			}
		}
                else
                {
                        globalId = rsp.getField("PID-3-1");
                        assigningAuthority = rsp.getField("PID-3-4-2");
                        //AA PID-3-4-2
                        if (globalId.isEmpty() || assigningAuthority.isEmpty())
                        {
                                String chMsg = "Clearinghouse did not return a global ID / Assigning Authority";
				throw new ClearinghouseException(chMsg);
                        }
                        else
                        {
                                logger.info("Received Globlal ID " + globalId);
                                logger.info("Received Assigning Authority " + globalId);
                        }                               
                }

                return new AbstractMap.SimpleImmutableEntry(globalId, assigningAuthority);
	}
}