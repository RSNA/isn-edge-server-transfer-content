/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.dao;

//import org.rsna.isn.transfercontent.pix.*;

/**
 *
 * @author oyesanyf
 */
public class PixData {




    public  PixData () {

    }
    private String msh1fieldseparator ;
    private String msh2encncodingcharacter ;
    private String msh3_1sendingapplicationmaespaceID ;
    private String msh4_1sendingfacilitynamespaceID ;
    private  String msh5_1receivingapplicatnamespaceID ;
    private String msh6_1receivingfacilitynamespaceID ;
    private String msh7_1datetimeofevent;

    public PixData(String msh1fieldseparator, String msh2encncodingcharacter, String msh3_1sendingapplicationmaespaceID, String msh4_1sendingfacilitynamespaceID, String msh5_1receivingapplicatnamespaceID, String msh6_1receivingfacilitynamespaceID, String msh7_1datetimeofevent, String msh9_1messagetype, String msh9__2triggerevent, String msh93messagestructure, String msh10controlID, String msh11_1processingID, String msg12_1versionID, String evn_1eventtypecode, String evn_2dateandtime, String pid3_1ID, String pid3_2checkdigits, String pid3_4_1assigninganothioritynamespaceID, String pid3_4_2universalID, String pid3_4_3universalidtype, String pid4_1patientID, String pid5_1patientfamilyname, String pid5_2patientgivenname, String pid6_1_1patientfamilyname, String pid7_1patientdob, String pid8patientsex, String pvi1_1patientvisistid, String pvi1_2patientvisitclass) {
        this.msh1fieldseparator = msh1fieldseparator;
        this.msh2encncodingcharacter = msh2encncodingcharacter;
        this.msh3_1sendingapplicationmaespaceID = msh3_1sendingapplicationmaespaceID;
        this.msh4_1sendingfacilitynamespaceID = msh4_1sendingfacilitynamespaceID;
        this.msh5_1receivingapplicatnamespaceID = msh5_1receivingapplicatnamespaceID;
        this.msh6_1receivingfacilitynamespaceID = msh6_1receivingfacilitynamespaceID;
        this.msh7_1datetimeofevent = msh7_1datetimeofevent;
        this.msh9_1messagetype = msh9_1messagetype;
        this.msh9__2triggerevent = msh9__2triggerevent;
        this.msh93messagestructure = msh93messagestructure;
        this.msh10controlID = msh10controlID;
        this.msh11_1processingID = msh11_1processingID;
        this.msg12_1versionID = msg12_1versionID;
        this.evn_1eventtypecode = evn_1eventtypecode;
        this.evn_2dateandtime = evn_2dateandtime;
        this.pid3_1ID = pid3_1ID;
        this.pid3_2checkdigits = pid3_2checkdigits;
        this.pid3_4_1assigninganothioritynamespaceID = pid3_4_1assigninganothioritynamespaceID;
        this.pid3_4_2universalID = pid3_4_2universalID;
        this.pid3_4_3universalidtype = pid3_4_3universalidtype;
        this.pid4_1patientID = pid4_1patientID;
        this.pid5_1patientfamilyname = pid5_1patientfamilyname;
        this.pid5_2patientgivenname = pid5_2patientgivenname;
        this.pid6_1_1patientfamilyname = pid6_1_1patientfamilyname;
        this.pid7_1patientdob = pid7_1patientdob;
        this.pid8patientsex = pid8patientsex;
        this.pvi1_1patientvisistid = pvi1_1patientvisistid;
        this.pvi1_2patientvisitclass = pvi1_2patientvisitclass;
    }
    private String msh9_1messagetype ;
    private String msh9__2triggerevent;
    private String msh93messagestructure;
    private String msh10controlID ;
    private String msh11_1processingID ;

    public PixData(String pid_3_5_identifertypecode, String pid_3_6_1namespaceID) {
        this.pid_3_5_identifertypecode = pid_3_5_identifertypecode;
        this.pid_3_6_1namespaceID = pid_3_6_1namespaceID;
    }
    private String msg12_1versionID ;
    private String evn_1eventtypecode ;

    public String getPid_3_5_identifertypecode() {
        return pid_3_5_identifertypecode;
    }

    public void setPid_3_5_identifertypecode(String pid_3_5_identifertypecode) {
        this.pid_3_5_identifertypecode = pid_3_5_identifertypecode;
    }

    public String getPid_3_6_1namespaceID() {
        return pid_3_6_1namespaceID;
    }

    public void setPid_3_6_1namespaceID(String pid_3_6_1namespaceID) {
        this.pid_3_6_1namespaceID = pid_3_6_1namespaceID;
    }
    private String evn_2dateandtime;
    private String pid3_1ID ;
    private String pid3_2checkdigits ;
    private String pid3_4_1assigninganothioritynamespaceID  ;
    private String pid_3_5_identifertypecode ;
    private String pid_3_6_1namespaceID ;
    private String pid3_4_2universalID ;
    private String pid3_4_3universalidtype ;
    private String pid4_1patientID ;
    private String pid5_1patientfamilyname ;
    private String pid5_2patientgivenname ;

    public PixData(String checkdigitcode) {
        this.checkdigitcode = checkdigitcode;
    }


    private String pid6_1_1patientfamilyname ;
    private String pid7_1patientdob ;
    private String pid8patientsex ;
    private String pvi1_1patientvisistid ;


    private String pvi1_2patientvisitclass ;

    private String checkdigitcode ;

    public String getEncodedMessage() {
        return EncodedMessage;
    }

    public void setEncodedMessage(String EncodedMessage) {
        this.EncodedMessage = EncodedMessage;
    }

    private String EncodedMessage ;

    public String getCheckdigitcode() {
        return checkdigitcode;
    }

    public void setCheckdigitcode(String checkdigitcode) {
        this.checkdigitcode = checkdigitcode;
    }

    public String getEvn_1eventtypecode() {
        return evn_1eventtypecode;
    }

    public void setEvn_1eventtypecode(String evn_1eventtypecode) {
        this.evn_1eventtypecode = evn_1eventtypecode;
    }

    public String getEvn_2dateandtime() {
        return evn_2dateandtime;
    }

    public void setEvn_2dateandtime(String evn_2dateandtime) {
        this.evn_2dateandtime = evn_2dateandtime;
    }

    public String getMsg12_1versionID() {
        return msg12_1versionID;
    }

    public void setMsg12_1versionID(String msg12_1versionID) {
        this.msg12_1versionID = msg12_1versionID;
    }

    public String getMsh10controlID() {
        return msh10controlID;
    }

    public void setMsh10controlID(String msh10controlID) {
        this.msh10controlID = msh10controlID;
    }

    public String getMsh11_1processingID() {
        return msh11_1processingID;
    }

    public void setMsh11_1processingID(String msh11_1processingID) {
        this.msh11_1processingID = msh11_1processingID;
    }

    public String getMsh1fieldseparator() {
        return msh1fieldseparator;
    }

    public void setMsh1fieldseparator(String msh1fieldseparator) {
        this.msh1fieldseparator = msh1fieldseparator;
    }

    public String getMsh2encncodingcharacter() {
        return msh2encncodingcharacter;
    }

    public void setMsh2encncodingcharacter(String msh2encncodingcharacter) {
        this.msh2encncodingcharacter = msh2encncodingcharacter;
    }

    public String getMsh3_1sendingapplicationmaespaceID() {
        return msh3_1sendingapplicationmaespaceID;
    }

    public void setMsh3_1sendingapplicationmaespaceID(String msh3_1sendingapplicationmaespaceID) {
        this.msh3_1sendingapplicationmaespaceID = msh3_1sendingapplicationmaespaceID;
    }

    public String getMsh4_1sendingfacilitynamespaceID() {
        return msh4_1sendingfacilitynamespaceID;
    }

    public void setMsh4_1sendingfacilitynamespaceID(String msh4_1sendingfacilitynamespaceID) {
        this.msh4_1sendingfacilitynamespaceID = msh4_1sendingfacilitynamespaceID;
    }

    public String getMsh5_1receivingapplicatnamespaceID() {
        return msh5_1receivingapplicatnamespaceID;
    }

    public void setMsh5_1receivingapplicatnamespaceID(String msh5_1receivingapplicatnamespaceID) {
        this.msh5_1receivingapplicatnamespaceID = msh5_1receivingapplicatnamespaceID;
    }

    public String getMsh6_1receivingfacilitynamespaceID() {
        return msh6_1receivingfacilitynamespaceID;
    }

    public void setMsh6_1receivingfacilitynamespaceID(String msh6_1receivingfacilitynamespaceID) {
        this.msh6_1receivingfacilitynamespaceID = msh6_1receivingfacilitynamespaceID;
    }

    public String getMsh7_1datetimeofevent() {
        return msh7_1datetimeofevent;
    }

    public void setMsh7_1datetimeofevent(String msh7_1datetimeofevent) {
        this.msh7_1datetimeofevent = msh7_1datetimeofevent;
    }

    public String getMsh93messagestructure() {
        return msh93messagestructure;
    }

    public void setMsh93messagestructure(String msh93messagestructure) {
        this.msh93messagestructure = msh93messagestructure;
    }

    public String getMsh9_1messagetype() {
        return msh9_1messagetype;
    }

    public void setMsh9_1messagetype(String msh9_1messagetype) {
        this.msh9_1messagetype = msh9_1messagetype;
    }

    public String getMsh9__2triggerevent() {
        return msh9__2triggerevent;
    }

    public void setMsh9__2triggerevent(String msh9__2triggerevent) {
        this.msh9__2triggerevent = msh9__2triggerevent;
    }

    public String getPid3_1ID() {
        return pid3_1ID;
    }

    public void setPid3_1ID(String pid3_1ID) {
        this.pid3_1ID = pid3_1ID;
    }

    public String getPid3_2checkdigits() {
        return pid3_2checkdigits;
    }

    public void setPid3_2checkdigits(String pid3_2checkdigits) {
        this.pid3_2checkdigits = pid3_2checkdigits;
    }

    public String getPid3_4_3universalidtype() {
        return pid3_4_3universalidtype;
    }

    public void setPid3_4_3universalidtype(String pid3_4_3universalidtype) {
        this.pid3_4_3universalidtype = pid3_4_3universalidtype;
    }

    public String getPid3_4_1assigninganothioritynamespaceID() {
        return pid3_4_1assigninganothioritynamespaceID;
    }

    public void setPid3_4_1assigninganothioritynamespaceID(String pid3_4_1assigninganothioritynamespaceID) {
        this.pid3_4_1assigninganothioritynamespaceID = pid3_4_1assigninganothioritynamespaceID;
    }

    public String getPid3_4_2universalID() {
        return pid3_4_2universalID;
    }

    public void setPid3_4_2universalID(String pid3_4_2universalID) {
        this.pid3_4_2universalID = pid3_4_2universalID;
    }

    public String getPid4_1patientID() {
        return pid4_1patientID;
    }

    public void setPid4_1patientID(String pid4_1patientID) {
        this.pid4_1patientID = pid4_1patientID;
    }

    public String getPid5_1patientfamilyname() {
        return pid5_1patientfamilyname;
    }

    public void setPid5_1patientfamilyname(String pid5_1patientfamilyname) {
        this.pid5_1patientfamilyname = pid5_1patientfamilyname;
    }

    public String getPid5_2patientgivenname() {
        return pid5_2patientgivenname;
    }

    public void setPid5_2patientgivenname(String pid5_2patientgivenname) {
        this.pid5_2patientgivenname = pid5_2patientgivenname;
    }

    public String getPid6_1_1patientfamilyname() {
        return pid6_1_1patientfamilyname;
    }

    public void setPid6_1_1patientfamilyname(String pid6_1_1patientfamilyname) {
        this.pid6_1_1patientfamilyname = pid6_1_1patientfamilyname;
    }

    public String getPid7_1patientdob() {
        return pid7_1patientdob;
    }

    public void setPid7_1patientdob(String pid7_1patientdob) {
        this.pid7_1patientdob = pid7_1patientdob;
    }

    public String getPid8patientsex() {
        return pid8patientsex;
    }

    public void setPid8patientsex(String pid8patientsex) {
        this.pid8patientsex = pid8patientsex;
    }

    public String getPvi1_1patientvisistid() {
        return pvi1_1patientvisistid;
    }

    public void setPvi1_1patientvisistid(String pvi1_1patientvisistid) {
        this.pvi1_1patientvisistid = pvi1_1patientvisistid;
    }

    public String getPvi1_2patientvisitclass() {
        return pvi1_2patientvisitclass;
    }

    public void setPvi1_2patientvisitclass(String pvi1_2patientvisitclass) {
        this.pvi1_2patientvisitclass = pvi1_2patientvisitclass;
    }


















}
