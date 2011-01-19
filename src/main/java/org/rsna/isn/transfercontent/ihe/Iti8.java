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
package org.rsna.isn.transfercontent.ihe;

import java.net.URI;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.ohf.hl7v2.core.message.MessageManager;
import org.openhealthtools.ihe.atna.auditor.PIXSourceAuditor;
import org.openhealthtools.ihe.common.mllp.MLLPDestination;
import org.openhealthtools.ihe.pix.source.PixMsgRegisterOutpatient;
import org.openhealthtools.ihe.pix.source.PixSource;
import org.openhealthtools.ihe.pix.source.PixSourceResponse;
import org.openhealthtools.ihe.utils.IHEException;
import org.rsna.isn.dao.ConfigurationDao;
import org.rsna.isn.domain.Job;
import org.rsna.isn.util.Constants;

/**
 * This class implements the ITI-8 (Patient identity feed) transaction.
 *
 * Note: the "iti8-pix-host", "iti8-pix-port", "iti8-reg-host" and "iti8-reg-port"
 * properties must be set in the configurations table of the RSNA database.
 *
 * @author Wyatt Tellis
 * @version 1.2.0
 */
public class Iti8
{

    private static final Logger logger = Logger.getLogger(Iti8.class);

    private static final URI pix;

    private static final URI registry;

    private static final MessageManager manager = MessageManager.getFactory();

    private final Job job;

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
        }
        catch (Exception ex)
        {
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Create an instance of this class.
     *
     * @param exam An exam instance
     * @throws IllegalArgumentException If there is no RSNA id associated with
     * the exam
     */
    public Iti8(Job job)
    {
        this.job = job;
    }

    /**
     * Register the patient with the PIX and registry.
     *
     * @throws IHEException If there was an uncaught exception while attempting
     * to register the patient.
     */
    public void registerPatient() throws IHEException
    {
        sendIti8Message(pix);

        sendIti8Message(registry);
    }

    private void sendIti8Message(URI uri) throws IHEException
    {
        PixSource feed = new PixSource();

        MLLPDestination mllp = new MLLPDestination(uri);
        MLLPDestination.setUseATNA(false);
        feed.setMLLPDestination(mllp);


        PixMsgRegisterOutpatient msg = new PixMsgRegisterOutpatient(manager,
                null, job.getSingleUsePatientId(),
                null, Constants.RSNA_UNIVERSAL_ID,
                Constants.RSNA_UNIVERSAL_ID_TYPE);
        msg.addOptionalPatientNameFamilyName("RSNA ISN");
        msg.addOptionalPatientNameGivenName("RSNA ISN");


        PixSourceResponse rsp = feed.sendRegistration(msg, false);

        String code = rsp.getResponseAckCode(false);
        String error = rsp.getField("MSA-3");
        String remote = uri.getHost() + ":" + uri.getPort();

        if ("AE".equals(code))
        {
            throw new IHEException("Remote application " + remote
                    + " responded with error: " + error);
        }
        else if ("AR".equals(code))
        {

            if (error.startsWith("PIX-10000:"))
            {
                logger.info("Remote application " + remote
                        + " reports patient id " + job.getSingleUsePatientId()
                        + " has already been registered.");
            }
            else
            {
                throw new IHEException("Remote application " + remote
                        + " rejected message with reason: " + error);
            }
        }
    }

}
