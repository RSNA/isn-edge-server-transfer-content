/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.pix;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import org.rsna.isn.transfercontent.dao.*;

/**
 *
 * @author oyesanyf
 */
public class GetADTMsgToRegistryandPIX {
    private static int controlID;
    private static String ack01;
    private static String ack04;
    private static int hl7TimeOut;

    public static String getAdt01(String fieldseparator, String encncodingcharacter, String sendingfacilitynamespaceID, String receivingapplicatnamespaceID, String receivingfacilitynamespaceID, String messagetype,String triggerevent, String messagestructure,String processingID,String versionID,String rsnaID, String universalID,String universalidtype,String patientfamilyname,String patientgivenname,String patientvisitclass,String pidnamespaceID,String registryhl7Server, String pixhl7Server,int registryport, int pixport ) throws Exception {

        try {

        Properties props = new Properties();

        props.load(new FileInputStream("c:/mtom/rsna.properties"));
        String sequencenum = props.getProperty("sequencenum");
        String timeOut = props.getProperty("hl7timeout");
        hl7TimeOut = Integer.parseInt(timeOut.trim());
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmm");
        SimpleDateFormat fs = new SimpleDateFormat("yyyyMMddHHmmSSS");
        Date d = new Date();

        HL7SequenceNumber seq = new HL7SequenceNumber();
        SetMessageToPixandRegistry pixMsg = new SetMessageToPixandRegistry();
        PixData hl7msg01 = new PixData();
        hl7msg01.setMsh1fieldseparator(fieldseparator);
        hl7msg01.setMsh2encncodingcharacter(encncodingcharacter);


        hl7msg01.setMsh4_1sendingfacilitynamespaceID(sendingfacilitynamespaceID);
        hl7msg01.setMsh5_1receivingapplicatnamespaceID(receivingapplicatnamespaceID);
        hl7msg01.setMsh6_1receivingfacilitynamespaceID(receivingfacilitynamespaceID);
        hl7msg01.setMsh7_1datetimeofevent(ft.format(d));
        hl7msg01.setMsh9_1messagetype(messagetype);
        hl7msg01.setMsh9__2triggerevent(triggerevent);
        hl7msg01.setMsh93messagestructure(messagestructure);
        controlID = HL7SequenceNumber.sequenceNum();
        String controlIDstr = Integer.toString(controlID);

        hl7msg01.setMsh10controlID(controlIDstr);
        hl7msg01.setMsh11_1processingID(processingID);
        hl7msg01.setMsg12_1versionID(versionID);



        //   hl7msg01.setEvn_1eventtypecode();
        hl7msg01.setEvn_2dateandtime(ft.format(d));
        hl7msg01.setPid3_1ID(rsnaID);
        //  hl7msg01.setPid3_4_1assigninganothioritynamespaceID("RSNA");

        hl7msg01.setPid3_4_2universalID(universalID);
        hl7msg01.setPid3_4_3universalidtype(universalidtype);
        //   hl7msg01.setPid3_2checkdigits("1.3.6.1.4.1.21367.2010.1.2.300") ;
        hl7msg01.setPid5_1patientfamilyname(patientfamilyname);
        hl7msg01.setPid5_2patientgivenname(patientgivenname);
        hl7msg01.setPvi1_2patientvisitclass(patientvisitclass);
        hl7msg01.setPid_3_6_1namespaceID(pidnamespaceID);

        PixData encodedMsg = new PixData();

        encodedMsg = SetMessageToPixandRegistry.setAdt01(hl7msg01);
        String encodeOut = encodedMsg.getEncodedMessage();
        PixMessageType send = new PixMessageType();
        send.setHl7ServerName(registryhl7Server);


        int port = registryport;


        send.setPort(port);

        send.setEncodedMessage(encodeOut);
        String response = SendMessageToPix.sendHL7(send,hl7TimeOut);
        System.out.println(response);

        PixMessageType send2 = new PixMessageType();

         send2.setHl7ServerName(pixhl7Server);

        int port2 = pixport;
        send2.setPort(port2);


        send2.setEncodedMessage(encodeOut);
        String response2 = SendMessageToPix.sendHL7(send2,hl7TimeOut);


         ack01 = "Registry:::" + response + "Pix:::" + response2 ;

        } catch (Exception ex) {
            ex.printStackTrace();
        }


       return ack01 ;

    }



     public static String getAdt04(String fieldseparator, String encncodingcharacter, String sendingfacilitynamespaceID, String receivingapplicatnamespaceID, String receivingfacilitynamespaceID, String messagetype,String triggerevent, String messagestructure,String processingID,String versionID,String rsnaID, String universalID,String universalidtype,String patientfamilyname,String patientgivenname,String patientvisitclass,String pidnamespaceID,String registryhl7Server, String pixhl7Server,int registryport, int pixport ) throws Exception {

        try {

        Properties props = new Properties();

        props.load(new FileInputStream("c:/mtom/rsna.properties"));
        String sequencenum = props.getProperty("sequencenum");
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmm");
        SimpleDateFormat fs = new SimpleDateFormat("yyyyMMddHHmmSSS");
        Date d = new Date();

        HL7SequenceNumber seq = new HL7SequenceNumber();
        SetMessageToPixandRegistry pixMsg = new SetMessageToPixandRegistry();
        PixData hl7msg04 = new PixData();
        hl7msg04.setMsh1fieldseparator(fieldseparator);
        hl7msg04.setMsh2encncodingcharacter(encncodingcharacter);


        hl7msg04.setMsh4_1sendingfacilitynamespaceID(sendingfacilitynamespaceID);
        hl7msg04.setMsh5_1receivingapplicatnamespaceID(receivingapplicatnamespaceID);
        hl7msg04.setMsh6_1receivingfacilitynamespaceID(receivingfacilitynamespaceID);
        hl7msg04.setMsh7_1datetimeofevent(ft.format(d));
        hl7msg04.setMsh9_1messagetype(messagetype);
        hl7msg04.setMsh9__2triggerevent(triggerevent);
        hl7msg04.setMsh93messagestructure(messagestructure);
        controlID = HL7SequenceNumber.sequenceNum();
        String controlIDstr = Integer.toString(controlID);

        hl7msg04.setMsh10controlID(controlIDstr);
        hl7msg04.setMsh11_1processingID(processingID);
        hl7msg04.setMsg12_1versionID(versionID);



        //   hl7msg01.setEvn_1eventtypecode();
        hl7msg04.setEvn_2dateandtime(ft.format(d));
        hl7msg04.setPid3_1ID(rsnaID);
        //  hl7msg01.setPid3_4_1assigninganothioritynamespaceID("RSNA");

        hl7msg04.setPid3_4_2universalID(universalID);
        hl7msg04.setPid3_4_3universalidtype(universalidtype);
        //   hl7msg01.setPid3_2checkdigits("1.3.6.1.4.1.21367.2010.1.2.300") ;
        hl7msg04.setPid5_1patientfamilyname(patientfamilyname);
        hl7msg04.setPid5_2patientgivenname(patientgivenname);
        hl7msg04.setPvi1_2patientvisitclass(patientvisitclass);
        hl7msg04.setPid_3_6_1namespaceID(pidnamespaceID);

        PixData encodedMsg = new PixData();

        encodedMsg = SetMessageToPixandRegistry.setAdt01(hl7msg04);
        String encodeOut = encodedMsg.getEncodedMessage();
        PixMessageType send = new PixMessageType();
        send.setHl7ServerName(registryhl7Server);


        int port = registryport;


        send.setPort(port);

        send.setEncodedMessage(encodeOut);
        String response = SendMessageToPix.sendHL7(send, hl7TimeOut);
        System.out.println(response);

        PixMessageType send2 = new PixMessageType();

         send2.setHl7ServerName(pixhl7Server);

        int port2 = pixport;
        send2.setPort(port2);


        send2.setEncodedMessage(encodeOut);
        String response2 = SendMessageToPix.sendHL7(send2, hl7TimeOut);


         ack04 = "Registry:::" + response + "Pix:::" + response2 ;








        } catch (Exception ex) {
            ex.printStackTrace();
        }


       return ack04 ;

    }


}
