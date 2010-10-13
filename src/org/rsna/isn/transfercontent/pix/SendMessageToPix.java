/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsna.isn.transfercontent.pix;

/**
 *
 * @author oyesanyf
 */
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.ConnectionHub;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.parser.GenericParser;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractPrimitive;
import ca.uhn.hl7v2.model.v21.segment.EVN;
import ca.uhn.hl7v2.model.v21.segment.PV1;
import ca.uhn.hl7v2.model.v24.message.ADT_A01;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.model.v24.segment.PID;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.NoValidation;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.rsna.isn.transfercontent.dao.*;

/**
 *
 * @author oyesanyf
 */
public class SendMessageToPix {

    private static String responseString;
    private static String encodedMessage;
    static TimeStamp stamp = new TimeStamp();
    private String tstamp;

    public static PixMessageType createAdt01(PixMessageType pixmsg) throws Exception {

        try {


            ADT_A01 adt = new ADT_A01();
            SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmm");
            SimpleDateFormat fs = new SimpleDateFormat("yyyyMMddHHmmSSS");
            Date d = new Date();

            MSH mshSegment = adt.getMSH();

            String FieldSeparator = pixmsg.getFieldSeparator();
            String EncodingCharacters = pixmsg.getEncodingCharacters();
            String DateTimeOfMessage = pixmsg.getDateTimeOfMessage();
            String SendingApplication = pixmsg.getSendingApplication();
            String SequenceNumber = pixmsg.getSequenceNumber();
            String MessageType = pixmsg.getMessageType();
            String TriggerEvent = pixmsg.getTriggerEvent();
            String MessageStructure = pixmsg.getMessageStructure();
            String FamilyName = pixmsg.getFamilyName();
            String GivenName = pixmsg.getFirstName();
            String PatientID = pixmsg.getPatientID();
            String versionID = pixmsg.getVersionID();


             ((AbstractPrimitive)mshSegment.getProcessingID().getComponent(0) ).setValue( "P" );

            ((AbstractPrimitive) mshSegment.getVersionID().getComponent(0)).setValue(versionID);
            mshSegment.getFieldSeparator().setValue(FieldSeparator);
            mshSegment.getEncodingCharacters().setValue(EncodingCharacters);
            mshSegment.getDateTimeOfMessage().getTimeOfAnEvent().setValue(DateTimeOfMessage);
            mshSegment.getSendingApplication().getNamespaceID().setValue(SendingApplication);
            //  ((AbstractPrimitive)mshSegment.getSendingApplication().getComponent(0) ).setValue(SendingApplication );

            //        mshSegment.getCharacterSet(0).setValue("ASCII");

            stamp = new TimeStamp();

            String controlID = "RSNA" + stamp.Date();

            mshSegment.getMessageControlID().setValue(controlID);

            mshSegment.getSequenceNumber().setValue(SequenceNumber);
            mshSegment.getMessageType().getMessageType().setValue(MessageType);
            mshSegment.getMessageType().getTriggerEvent().setValue(TriggerEvent);
            mshSegment.getMessageType().getMessageStructure().setValue(MessageStructure);




             
            PID pid = adt.getPID();
            pid.getPatientName(0).getFamilyName().getSurname().setValue(FamilyName);
            pid.getPatientName(0).getGivenName().setValue(GivenName);
            pid.getPatientIdentifierList(0).getID().setValue(PatientID);



           


            ca.uhn.hl7v2.model.v24.segment.EVN evn = adt.getEVN();
            evn.getEventTypeCode().setValue("A01");
            evn.getRecordedDateTime().getTimeOfAnEvent().setValue(ft.format(d));




            ca.uhn.hl7v2.model.v24.segment.PV1 pv1 = adt.getPV1();
            pv1.getPatientClass().setValue("O");
            pv1.getReferringDoctor(0).getFamilyName().getOwnSurname().setValue("Ogun") ;



            ca.uhn.hl7v2.parser.Parser parser = new ca.uhn.hl7v2.parser.PipeParser();
            encodedMessage = parser.encode(adt);
            System.out.println(encodedMessage);

            pixmsg.setEncodedMessage(encodedMessage);








        } catch (Exception e) {
            e.printStackTrace();
        }
        return pixmsg;

    }

    public static String sendHL7(PixMessageType pixmsg) throws Exception {

        try {
            ca.uhn.hl7v2.parser.PipeParser parser = new ca.uhn.hl7v2.parser.PipeParser();

            Parser p = new GenericParser();

//            Parser p = new Parser();
            //   p.setValidationContext(new NoValidation());

            String msg = pixmsg.getEncodedMessage().toString();

            Message adt = p.parse(msg);


            int port = pixmsg.getPort();
            ConnectionHub connectionHub = ca.uhn.hl7v2.app.ConnectionHub.getInstance();

            String hostname = pixmsg.getHl7ServerName();

            Connection connection = connectionHub.attach(hostname, port, new PipeParser(), MinLowerLayerProtocol.class);
            Initiator initiator = connection.getInitiator();
            //  initiator.setTimeoutMillis(100000000);
            Message response = initiator.sendAndReceive(adt);

            responseString = parser.encode(response);


            connection.close();

        } catch (HL7Exception ex) {
            ex.printStackTrace();
        }


        return responseString;

    }
}
