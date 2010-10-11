/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.dao;

/**
 *
 * @author oyesanyf
 */
//import org.rsna.isn.transfercontent.pix.*;
import java.sql.Timestamp;

/**
 *
 * @author oyesanyf
 */
public class PixMessageType {
    public  PixMessageType() {

    }

    public PixMessageType(String hl7message, int port, String hl7ServerName, String FieldSeparator, String EncodingCharacters,String DateTimeOfMessage, String SendingApplication, String MessageType, String TriggerEvent, String MessageStructure, String FamilyName, String FirstName, String patientID) {
        this.hl7message = hl7message;
        this.port = port;
        this.hl7ServerName = hl7ServerName;
        this.FieldSeparator = FieldSeparator;
        this.EncodingCharacters = EncodingCharacters;
        this.DateTimeOfMessage = DateTimeOfMessage;
        this.SendingApplication = SendingApplication;
        this.MessageType = MessageType;
        this.TriggerEvent = TriggerEvent;
        this.MessageStructure = MessageStructure;
        this.FamilyName = FamilyName;
        this.FirstName = FirstName;
        this.patientID = patientID;
    }
    private String hl7message ;
    private int port ;
    private String hl7ServerName ;
    private String FieldSeparator ;

    public String getDateTimeOfMessage() {
        return DateTimeOfMessage;
    }

    public void setDateTimeOfMessage(String DateTimeOfMessage) {
        this.DateTimeOfMessage = DateTimeOfMessage;
    }

    public String getEncodingCharacters() {
        return EncodingCharacters;
    }

    public void setEncodingCharacters(String EncodingCharacters) {
        this.EncodingCharacters = EncodingCharacters;
    }

    public String getFamilyName() {
        return FamilyName;
    }

    public void setFamilyName(String FamilyName) {
        this.FamilyName = FamilyName;
    }

    public String getFieldSeparator() {
        return FieldSeparator;
    }

    public void setFieldSeparator(String FieldSeparator) {
        this.FieldSeparator = FieldSeparator;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String FirstName) {
        this.FirstName = FirstName;
    }

    public String getMessageStructure() {
        return MessageStructure;
    }

    public void setMessageStructure(String MessageStructure) {
        this.MessageStructure = MessageStructure;
    }

    public String getMessageType() {
        return MessageType;
    }

    public void setMessageType(String MessageType) {
        this.MessageType = MessageType;
    }

    public String getSendingApplication() {
        return SendingApplication;
    }

    public void setSendingApplication(String SendingApplication) {
        this.SendingApplication = SendingApplication;
    }

    public String getTriggerEvent() {
        return TriggerEvent;
    }

    public void setTriggerEvent(String TriggerEvent) {
        this.TriggerEvent = TriggerEvent;
    }

    public String getHl7ServerName() {
        return hl7ServerName;
    }

    public void setHl7ServerName(String hl7ServerName) {
        this.hl7ServerName = hl7ServerName;
    }

    public String getHl7message() {
        return hl7message;
    }

    public void setHl7message(String hl7message) {
        this.hl7message = hl7message;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public PixMessageType(String SequenceNumber) {
        this.SequenceNumber = SequenceNumber;
    }
    private String EncodingCharacters ;
    private  String DateTimeOfMessage ;

    public String getSequenceNumber() {
        return SequenceNumber;
    }

    public void setSequenceNumber(String SequenceNumber) {
        this.SequenceNumber = SequenceNumber;
    }
    private String SendingApplication;
    private String MessageType ;
    private String TriggerEvent ;
    private String MessageStructure ;
    private String FamilyName ;
    private String FirstName ;
    private String patientID ;
    private String SequenceNumber ;
    private String encodedMessage ;
    private String versionID ;

    public String getVersionID() {
        return versionID;
    }



    public void setVersionID(String versionID) {
        this.versionID = versionID;
    }

    public String getEncodedMessage() {
        return encodedMessage;
    }

   

    public void setEncodedMessage(String encodedMessage) {
        this.encodedMessage = encodedMessage;
    }










}

