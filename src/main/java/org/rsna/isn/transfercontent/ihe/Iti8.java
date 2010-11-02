/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsna.isn.transfercontent.ihe;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.ohf.hl7v2.core.message.MessageManager;
import org.openhealthtools.ihe.atna.auditor.PIXSourceAuditor;
import org.openhealthtools.ihe.common.mllp.MLLPDestination;
import org.openhealthtools.ihe.common.mllp.TCPPort;
import org.openhealthtools.ihe.pix.source.PixMsgRegisterOutpatient;
import org.openhealthtools.ihe.pix.source.PixSource;
import org.openhealthtools.ihe.pix.source.PixSourceResponse;
import org.openhealthtools.ihe.utils.IHEException;
import org.rsna.isn.dao.ConfigurationDao;
import org.rsna.isn.domain.Exam;
import org.rsna.isn.domain.RsnaDemographics;
import org.rsna.isn.util.Constants;

/**
 *
 * @author wtellis
 */
public class Iti8
{
    private static final Logger logger = Logger.getLogger(Iti8.class);

    private static final TCPPort pix;

    private static final TCPPort registry;

    private static final MessageManager manager = MessageManager.getFactory();

    private final Exam exam;

    static
    {
	try
	{
	    ConfigurationDao dao = new ConfigurationDao();

	    String pixHost = dao.getConfiguration("iti8-pix-host");
	    if (StringUtils.isBlank(pixHost))
		throw new ExceptionInInitializerError("iti8-pix-host is blank");

	    int pixPort = Integer.parseInt(dao.getConfiguration("iti8-pix-port"));

	    pix = new TCPPort();
	    pix.setTcpHost(pixHost);
	    pix.setTcpPort(pixPort);





	    String regHost = dao.getConfiguration("iti8-reg-host");
	    if (StringUtils.isBlank(regHost))
		throw new ExceptionInInitializerError("iti8-reg-host is blank");

	    int regPort = Integer.parseInt(dao.getConfiguration("iti8-reg-port"));

	    registry = new TCPPort();
	    registry.setTcpHost(regHost);
	    registry.setTcpPort(regPort);

	    PIXSourceAuditor.getAuditor().getConfig().setAuditorEnabled(false);
	}
	catch (Exception ex)
	{
	    throw new ExceptionInInitializerError(ex);
	}
    }

    public Iti8(Exam exam)
    {
	this.exam = exam;
    }

    public void registerPatient() throws IHEException
    {
	sendIti8Message(pix);

	sendIti8Message(registry);
    }

    private void sendIti8Message(TCPPort dest) throws IHEException
    {
	RsnaDemographics rsna = exam.getRsnaDemographics();
	if (rsna == null)
	    throw new IllegalArgumentException("No RNSA id associated with mrn " + exam.getMrn());

	PixSource feed = new PixSource();

	MLLPDestination mllp = new MLLPDestination(dest);
	MLLPDestination.setUseATNA(false);
	feed.setMLLPDestination(mllp);




	PixMsgRegisterOutpatient msg = new PixMsgRegisterOutpatient(manager,
		null, rsna.getId(),
		Constants.NAMESPACE_ID, Constants.UNIVERSAL_ID,
		Constants.UNIVERSAL_ID_TYPE);
	msg.addOptionalPatientNameFamilyName(rsna.getLastName());
	msg.addOptionalPatientNameGivenName(rsna.getFirstName());


	PixSourceResponse rsp = feed.sendRegistration(msg, false);

	String code = rsp.getResponseAckCode(false);
	String error = rsp.getField("MSA-3");
	String remote = dest.getTcpHost() + ":" + dest.getTcpPort();

	if ("AE".equals(code))
	{
	    throw new IHEException("Remote application " + remote
		    + " responded with error: " + error);
	}
	else if ("AR".equals(code))
	{
	    throw new IHEException("Remote application " + remote
		    + " rejected message with reason: " + error);
	}

    }

}
