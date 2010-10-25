/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsna.isn.transfercontent.pix;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import org.rsna.isn.transfercontent.dao.*;
import org.rsna.isn.transfercontent.logging.LogProvider;

/**
 *
 * @author oyesanyf
 */
public class Pix {

  
    private static PixData encodedMsg2;
    private static LogProvider lp;
    private static int hl7TimeOut;
   

    /**
     * @param args the command line arguments
     */
    public static String RegisterPatient(int jobID) throws IOException, Exception {
        Properties props = new Properties();
        props.load(new FileInputStream("/rsna/properties/rsna.properties"));
        String timeOut = props.getProperty("hl7timeout");
        hl7TimeOut = Integer.parseInt(timeOut.trim());
        String registryHL7Host = props.getProperty("registryHL7host");
        Integer ix = new Integer( props.getProperty("registryHL7port") );
        int    registryHL7Port = ix.intValue();

        lp = LogProvider.getInstance();
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmm");
        SimpleDateFormat fs = new SimpleDateFormat("yyyyMMddHHmmSSS");
        Date d = new Date();

        SubmissionSetSqlQueryData ssQueryData = new SubmissionSetSqlQueryData();
        PatientRSNAIDs patientRSNAIDs = new PatientRSNAIDs();

        ssQueryData = SQLQueries.GetSubmisionSetData(jobID);
        int patientID = ssQueryData.getPatientid();
        patientRSNAIDs = SQLQueries.GetRSNAIDfromPatientID(patientID);

        HL7SequenceNumber seq = new HL7SequenceNumber();
        SetMessageToPixandRegistry pixMsg = new SetMessageToPixandRegistry();
        PixData hl7msg01 = new PixData();
        hl7msg01.setMsh1fieldseparator("|");
        hl7msg01.setMsh2encncodingcharacter("^~\\&");

        //   hl7msg01.setEvn_2dateandtime("2010100102");
        //    hl7msg01.setMsh3_1sendingapplicationmaespaceID("EncodedMessage");
        hl7msg01.setMsh4_1sendingfacilitynamespaceID("RSNA");
        hl7msg01.setMsh5_1receivingapplicatnamespaceID("ClearingHouse");
        hl7msg01.setMsh6_1receivingfacilitynamespaceID("RSNA");
        hl7msg01.setMsh7_1datetimeofevent(ft.format(d));
        hl7msg01.setMsh9_1messagetype("ADT");
        hl7msg01.setMsh9__2triggerevent("A01");
        hl7msg01.setMsh93messagestructure("ADT_A01");
        Random rand = new Random();
        long  controlID = rand.nextInt();
       // controlID = HL7SequenceNumber.sequenceNum();
        String controlIDstr = Integer.toString((int) controlID);

        hl7msg01.setMsh10controlID(controlIDstr);
        hl7msg01.setMsh11_1processingID("P");
        hl7msg01.setMsg12_1versionID("2.3.1");



        //   hl7msg01.setEvn_1eventtypecode();
        hl7msg01.setEvn_2dateandtime(ft.format(d));
        hl7msg01.setPid3_1ID(patientRSNAIDs.getRsnaID());
        //  hl7msg01.setPid3_4_1assigninganothioritynamespaceID("RSNA");

        hl7msg01.setPid3_4_2universalID("1.3.6.1.4.1.21367.2010.1.2.300");
        hl7msg01.setPid3_4_3universalidtype("ISO");
        //   hl7msg01.setPid3_2checkdigits("1.3.6.1.4.1.21367.2010.1.2.300") ;
        hl7msg01.setPid5_1patientfamilyname(patientRSNAIDs.getPatientAliasLastName());
        hl7msg01.setPid5_2patientgivenname(patientRSNAIDs.getPatientAliasFirstName());
        hl7msg01.setPvi1_2patientvisitclass("I");
        //hl7msg01.setPid_3_6_1namespaceID("RSNA");

        PixData encodedMsg = new PixData();

        encodedMsg = SetMessageToPixandRegistry.setAdt01(hl7msg01);
        String encodeOut = encodedMsg.getEncodedMessage();
        PixMessageType send = new PixMessageType();
        send.setHl7ServerName(registryHL7Host);
        send.setPort(registryHL7Port);

        send.setEncodedMessage(encodeOut);
        String response = SendMessageToPix.sendHL7(send);
        System.out.println(response);
        lp.getLog().info(response);
	return response;

/*
        int port2 = 8888;
        send.setPort(port2);


        send.setEncodedMessage(encodeOut);
        String response2 = SendMessageToPix.sendHL7(send);
        System.out.println(response2);

        String out = response + "*****" + response2;
        lp.getLog().info(out);

        return out;
*/
       ///adt04 message begins here

      //  setMessageToPixandResigtry pixMsgAdt04 = new setMessageToPixandResigtry();
      //  pixData hl7msg04 = new pixData();
     //   hl7msg04.setMsh1fieldseparator("|");
     //   hl7msg04.setMsh2encncodingcharacter("^~\\&");

        //   hl7msg01.setEvn_2dateandtime("2010100102");
        //    hl7msg01.setMsh3_1sendingapplicationmaespaceID("EncodedMessage");
     // hl7msg04.setMsh4_1sendingfacilitynamespaceID("FemiDesktop");
     //   hl7msg04.setMsh5_1receivingapplicatnamespaceID("ClearingHouse");
    //    hl7msg04.setMsh6_1receivingfacilitynamespaceID("FromFemi ");
   //     hl7msg04.setMsh7_1datetimeofevent(ft.format(d));
   //     hl7msg04.setMsh9_1messagetype("ADT");
    //    hl7msg04.setMsh9__2triggerevent("A04");
     //   hl7msg04.setMsh93messagestructure("ADT_A04");

      //  controlID04 = HL7SequenceNumber.sequenceNum();
      //   long  controlID04 = rand.nextInt();
      //  String controlIDstr04 = Integer.toString((int) controlID04);


     //   hl7msg04.setMsh10controlID(controlIDstr04);
     //   hl7msg04.setMsh11_1processingID("P");
     //   hl7msg04.setMsg12_1versionID("2.3.1");



        //   hl7msg01.setEvn_1eventtypecode();
      //  hl7msg04.setEvn_2dateandtime(ft.format(d));
      //  hl7msg04.setPid3_1ID("2000");
        //  hl7msg01.setPid3_4_1assigninganothioritynamespaceID("RSNA");

     //   hl7msg04.setPid3_4_2universalID("1.3.6.1.4.1.21367.2010.1.2.300");
    //    hl7msg04.setPid3_4_3universalidtype("ISO");
        //   hl7msg01.setPid3_2checkdigits("1.3.6.1.4.1.21367.2010.1.2.300") ;
     //   hl7msg04.setPid5_1patientfamilyname("Oyesanya");
     //   hl7msg04.setPid5_2patientgivenname("Femi");
     //   hl7msg04.setPvi1_2patientvisitclass("I");
      //  hl7msg04.setPid_3_6_1namespaceID("RSNA");

    //    pixData encodedMsg04 = new pixData();

    //    encodedMsg04 = setMessageToPixandResigtry.setAdt04(hl7msg04);
    //    String encodeOut04 = encodedMsg04.getEncodedMessage();
    //    pixMessageType send04 = new pixMessageType();
     //   send04.setHl7ServerName("216.185.79.26");

     //   send04.setHl7ServerName("216.185.79.26");


        // int port = 8888 ;
     //   send04.setPort(port);

      //  send04.setEncodedMessage(encodeOut04);
   //     String response04 = sendMessageToPix.sendHL7(send04);
  //      System.out.println("Femi" + response04);


      //  send04.setPort(port2);


      //  send04.setEncodedMessage(encodeOut04);
     //   String response04b = sendMessageToPix.sendHL7(send04);
    //    System.out.println("Femi" + response04b);
    }
}
