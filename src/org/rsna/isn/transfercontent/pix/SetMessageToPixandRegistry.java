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


import ca.uhn.hl7v2.model.v231.message.ADT_A04;
import ca.uhn.hl7v2.model.v24.message.ADT_A01;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.model.v24.segment.PID;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.NoValidation;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import org.rsna.isn.transfercontent.dao.*;

/**
 *
 * @author oyesanyf
 */
public class SetMessageToPixandRegistry {

    private static String responseString;
    private static String encodedMessage;
    static TimeStamp stamp = new TimeStamp();
    private static int hl7TimeOut;
    private String tstamp;


    public static PixData setAdt01(PixData pixmsg) throws Exception {

        try {

            ADT_A01 adt = new ADT_A01();
            SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmm");
            SimpleDateFormat fs = new SimpleDateFormat("yyyyMMddHHmmSSS");
            Date d = new Date();
            MSH mshSegment = adt.getMSH();
            String FieldSeparator = pixmsg.getMsh1fieldseparator();
            String EncodingCharacters = pixmsg.getMsh2encncodingcharacter();
            String SendingApplication = pixmsg.getMsh3_1sendingapplicationmaespaceID();
            String Sendingfacility = pixmsg.getMsh4_1sendingfacilitynamespaceID();
            String RecevindApplicationNamespaceID = pixmsg.getMsh5_1receivingapplicatnamespaceID();
            String ReceivingFacility = pixmsg.getMsh6_1receivingfacilitynamespaceID();
            String DateTimeOfMessage = pixmsg.getMsh7_1datetimeofevent();
            String MessageType = pixmsg.getMsh9_1messagetype();
            String Triggerevent = pixmsg.getMsh9__2triggerevent();
            String MessageStructure = pixmsg.getMsh93messagestructure();
            String controlID = pixmsg.getMsh10controlID();
            String processingID = pixmsg.getMsh11_1processingID();
            String versionID = pixmsg.getMsg12_1versionID();
            String eventTypecode = pixmsg.getEvn_1eventtypecode();
            String eventdateandtime = pixmsg.getEvn_2dateandtime();
            String patiendid = pixmsg.getPid3_1ID();
            String checkdigits = pixmsg.getPid3_2checkdigits();
            String checkdigitcode = pixmsg.getCheckdigitcode();
            String pidnamespaceID = pixmsg.getPid3_4_1assigninganothioritynamespaceID();
            String piduniversalID = pixmsg.getPid3_4_2universalID();
            String piduniversalIDtype = pixmsg.getPid3_4_3universalidtype();
            String alternatepatientID = pixmsg.getPid4_1patientID();
            String patientFamilyname = pixmsg.getPid5_1patientfamilyname();
            String patientgivenName = pixmsg.getPid5_2patientgivenname();
            String familylastname = pixmsg.getPid6_1_1patientfamilyname();
            String patientdob = pixmsg.getPid7_1patientdob();
            String patientsex = pixmsg.getPid8patientsex();
            String patientvisitID = pixmsg.getPvi1_1patientvisistid();
            String patientVisistClass = pixmsg.getPvi1_2patientvisitclass();
            String checkdigit = pixmsg.getPid3_2checkdigits();
            String pididentifytypecode = pixmsg.getPid_3_5_identifertypecode();
            String pidnamespaceid = pixmsg.getPid_3_6_1namespaceID();
            mshSegment.getFieldSeparator().setValue(FieldSeparator);
            mshSegment.getEncodingCharacters().setValue(EncodingCharacters);
            mshSegment.getSendingApplication().getNamespaceID().setValue(SendingApplication);
            mshSegment.getSendingFacility().getNamespaceID().setValue(Sendingfacility);
            mshSegment.getReceivingApplication().getNamespaceID().setValue(RecevindApplicationNamespaceID);
            mshSegment.getReceivingFacility().getNamespaceID().setValue(ReceivingFacility);
            mshSegment.getDateTimeOfMessage().getTimeOfAnEvent().setValue(DateTimeOfMessage);
            mshSegment.getMessageType().getMessageType().setValue(MessageType);
            mshSegment.getMessageType().getTriggerEvent().setValue(Triggerevent);
            mshSegment.getMessageType().getMessageStructure().setValue(MessageStructure);
            mshSegment.getMessageControlID().setValue(controlID);
            ((AbstractPrimitive) mshSegment.getProcessingID().getComponent(0)).setValue(processingID);
            ((AbstractPrimitive) mshSegment.getVersionID().getComponent(0)).setValue(versionID);

            ca.uhn.hl7v2.model.v24.segment.EVN evn = adt.getEVN();
            evn.getEventTypeCode().setValue(eventTypecode);
            evn.getRecordedDateTime().getTimeOfAnEvent().setValue(ft.format(d));

            PID pid = adt.getPID();
            pid.getPatientIdentifierList(0).getID().setValue(patiendid);

        //    pid.getAlternatePatientIDPID(0).getID().setValue(alternatepatientID);
            pid.getPatientIdentifierList(0).getCheckDigit().setValue(checkdigits);
            pid.getPatientIdentifierList(0).getCodeIdentifyingTheCheckDigitSchemeEmployed().setValue(checkdigitcode);
            pid.getPatientIdentifierList(0).getAssigningAuthority().getNamespaceID().setValue(pidnamespaceID);
            pid.getPatientIdentifierList(0).getAssigningAuthority().getUniversalID().setValue(piduniversalID);
            pid.getPatientIdentifierList(0).getAssigningAuthority().getUniversalIDType().setValue(piduniversalIDtype);

         // pid.getPatientIdentifierList(1).getIdentifierTypeCode().setValue(pididentifytypecode);
        //  pid.getPatientIdentifierL

            pid.getPatientIdentifierList(0).getAssigningFacility().getNamespaceID().setValue(pidnamespaceid);
            pid.getPatientIdentifierList(0).getAssigningFacility().getUniversalIDType().setValue(pididentifytypecode);
            pid.getPatientName(0).getFamilyName().getSurname().setValue(patientFamilyname);
            pid.getPatientName(0).getGivenName().setValue(patientgivenName);

            ca.uhn.hl7v2.model.v24.segment.PV1 pv1 = adt.getPV1();
            pv1.getPatientClass().setValue(patientVisistClass );
            Parser parser = new PipeParser();
            encodedMessage = parser.encode(adt);
            System.out.println(encodedMessage);
            pixmsg.setEncodedMessage(encodedMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pixmsg;

    }


    public static PixData setAdt04(PixData pixmsg) throws Exception {

        try {
            Properties props = new Properties();
            props.load(new FileInputStream("/rsna/properties/rsna.properties"));
            String timeOut = props.getProperty("hl7timeout");
            hl7TimeOut = Integer.parseInt(timeOut.trim());

            ADT_A04 adt = new ADT_A04();
            SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmm");
            SimpleDateFormat fs = new SimpleDateFormat("yyyyMMddHHmmSSS");
            Date d = new Date();
            ca.uhn.hl7v2.model.v231.segment.MSH mshSegment = adt.getMSH();
            String FieldSeparator = pixmsg.getMsh1fieldseparator();

            String EncodingCharacters = pixmsg.getMsh2encncodingcharacter();
            String SendingApplication = pixmsg.getMsh3_1sendingapplicationmaespaceID();
            String Sendingfacility = pixmsg.getMsh4_1sendingfacilitynamespaceID();
            String RecevindApplicationNamespaceID = pixmsg.getMsh5_1receivingapplicatnamespaceID();
            String ReceivingFacility = pixmsg.getMsh6_1receivingfacilitynamespaceID();
            String DateTimeOfMessage = pixmsg.getMsh7_1datetimeofevent();
            String MessageType = pixmsg.getMsh9_1messagetype();
            String Triggerevent = pixmsg.getMsh9__2triggerevent();
            String MessageStructure = pixmsg.getMsh93messagestructure();
            String controlID = pixmsg.getMsh10controlID();
            String processingID = pixmsg.getMsh11_1processingID();
            String versionID = pixmsg.getMsg12_1versionID();
            String eventTypecode = pixmsg.getEvn_1eventtypecode();
            String eventdateandtime = pixmsg.getEvn_2dateandtime();
            String patiendid = pixmsg.getPid3_1ID();
            String checkdigits = pixmsg.getPid3_2checkdigits();
            String checkdigitcode = pixmsg.getCheckdigitcode();
            String pidnamespaceID = pixmsg.getPid3_4_1assigninganothioritynamespaceID();
            String piduniversalID = pixmsg.getPid3_4_2universalID();
            String piduniversalIDtype = pixmsg.getPid3_4_3universalidtype();
            String alternatepatientID = pixmsg.getPid4_1patientID();
            String patientFamilyname = pixmsg.getPid5_1patientfamilyname();
            String patientgivenName = pixmsg.getPid5_2patientgivenname();
            String familylastname = pixmsg.getPid6_1_1patientfamilyname();
            String patientdob = pixmsg.getPid7_1patientdob();
            String patientsex = pixmsg.getPid8patientsex();
            String patientvisitID = pixmsg.getPvi1_1patientvisistid();
            String patientVisistClass = pixmsg.getPvi1_2patientvisitclass();
            String checkdigit = pixmsg.getPid3_2checkdigits();
            String pididentifytypecode = pixmsg.getPid_3_5_identifertypecode();
            String pidnamespaceid = pixmsg.getPid_3_6_1namespaceID();




             if (!FieldSeparator.matches(("\\|"))) {
                throw new Exception ("Field separator character must have a value of |");
            }

            mshSegment.getFieldSeparator().setValue(FieldSeparator);

            mshSegment.getEncodingCharacters().setValue(EncodingCharacters);
            mshSegment.getSendingApplication().getNamespaceID().setValue(SendingApplication);
            mshSegment.getSendingFacility().getNamespaceID().setValue(Sendingfacility);
            mshSegment.getReceivingApplication().getNamespaceID().setValue(RecevindApplicationNamespaceID);
            mshSegment.getReceivingFacility().getNamespaceID().setValue(ReceivingFacility);
            mshSegment.getDateTimeOfMessage().getTimeOfAnEvent().setValue(DateTimeOfMessage);
            mshSegment.getMessageType().getMessageType().setValue(MessageType);
            mshSegment.getMessageType().getTriggerEvent().setValue(Triggerevent);
            mshSegment.getMessageType().getMessageStructure().setValue(MessageStructure);
            mshSegment.getMessageControlID().setValue(controlID);
            ((AbstractPrimitive) mshSegment.getProcessingID().getComponent(0)).setValue(processingID);
            ((AbstractPrimitive) mshSegment.getVersionID().getComponent(0)).setValue(versionID);

            ca.uhn.hl7v2.model.v231.segment.EVN evn = adt.getEVN();
            evn.getEventTypeCode().setValue(eventTypecode);
            evn.getRecordedDateTime().getTimeOfAnEvent().setValue(ft.format(d));

            ca.uhn.hl7v2.model.v231.segment.PID pid = adt.getPID();
            pid.getPatientIdentifierList(0).getID().setValue(patiendid);

        //    pid.getAlternatePatientIDPID(0).getID().setValue(alternatepatientID);
            pid.getPatientIdentifierList(0).getCheckDigit().setValue(checkdigits);
            pid.getPatientIdentifierList(0).getCodeIdentifyingTheCheckDigitSchemeEmployed().setValue(checkdigitcode);
            pid.getPatientIdentifierList(0).getAssigningAuthority().getNamespaceID().setValue(pidnamespaceID);
            pid.getPatientIdentifierList(0).getAssigningAuthority().getUniversalID().setValue(piduniversalID);
            pid.getPatientIdentifierList(0).getAssigningAuthority().getUniversalIDType().setValue(piduniversalIDtype);

         // pid.getPatientIdentifierList(1).getIdentifierTypeCode().setValue(pididentifytypecode);
        //  pid.getPatientIdentifierL

            pid.getPatientIdentifierList(0).getAssigningFacility().getNamespaceID().setValue(pidnamespaceid);
            pid.getPatientIdentifierList(0).getAssigningFacility().getUniversalIDType().setValue(pididentifytypecode);


            pid.getPatientName(0).getFamilyLastName().getFamilyName().setValue(patientFamilyname);




    //        pid.getPatientName(0).getFamilyName().getSurname().setValue(patientFamilyname);
            pid.getPatientName(0).getGivenName().setValue(patientgivenName);

            ca.uhn.hl7v2.model.v231.segment.PV1 pv1 = adt.getPV1();
            pv1.getPatientClass().setValue(patientVisistClass );
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
            Properties props = new Properties();
            props.load(new FileInputStream("/rsna/properties/rsna.properties"));
            String sequencenum = props.getProperty("sequencenum");
            String timeOut = props.getProperty("hl7timeout");
            hl7TimeOut = Integer.parseInt(timeOut.trim());
            ca.uhn.hl7v2.parser.PipeParser parser = new ca.uhn.hl7v2.parser.PipeParser();

            Parser p = new GenericParser();
            //   p.setValidationContext(new NoValidation());

            String msg = pixmsg.getEncodedMessage().toString();

            Message adt = p.parse(msg);


            int port = pixmsg.getPort();
            ConnectionHub connectionHub = ca.uhn.hl7v2.app.ConnectionHub.getInstance();

            String hostname = pixmsg.getHl7ServerName();

            Connection connection = connectionHub.attach(hostname, port, new PipeParser(), MinLowerLayerProtocol.class);
            Initiator initiator = connection.getInitiator();
            initiator.setTimeoutMillis(hl7TimeOut);
            Message response = initiator.sendAndReceive(adt);

            responseString = parser.encode(response);


            connection.close();

        } catch (HL7Exception ex) {
            ex.printStackTrace();
        }


        return responseString;

    }
}
