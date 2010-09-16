/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsna.isn.transfercontent.dao;

//import org.rsna.isc.transfercontent.dao.*;
import java.sql.Timestamp;

/**
 *
 * @author oyesanyf
 */
public class SubmissionSetSqlQueryData {

    public SubmissionSetSqlQueryData(int examid, int patientid, String pmrn, String patientname, Timestamp dob, String sex, String street, String city, String state, String zip_code, String reporttxt, String signer, String dictator, String examdescription, String accessionnumber, int reportid) {
        this.examid = examid;
        this.patientid = patientid;
        this.pmrn = pmrn;
        this.patientname = patientname;
        this.dob = dob;
        this.sex = sex;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip_code = zip_code;
        this.reporttxt = reporttxt;
        this.signer = signer;
        this.dictator = dictator;
        this.examdescription = examdescription;
        this.accessionnumber = accessionnumber;
        this.reportid = reportid;
    }
    
    public SubmissionSetSqlQueryData()   {
        
    }

    private int examid;
    private int patientid;

    public String getAccessionnumber() {
        return accessionnumber;
    }

    public void setAccessionnumber(String accessionnumber) {
        this.accessionnumber = accessionnumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDictator() {
        return dictator;
    }

    public void setDictator(String dictator) {
        this.dictator = dictator;
    }

    public Timestamp getDob() {
        return dob;
    }

    public void setDob(Timestamp dob) {
        this.dob = dob;
    }

    public String getExamdescription() {
        return examdescription;
    }

    public void setExamdescription(String examdescription) {
        this.examdescription = examdescription;
    }

    public int getExamid() {
        return examid;
    }

    public void setExamid(int examid) {
        this.examid = examid;
    }

    public int getPatientid() {
        return patientid;
    }

    public void setPatientid(int patientid) {
        this.patientid = patientid;
    }

    public String getPatientname() {
        return patientname;
    }

    public void setPatientname(String patientname) {
        this.patientname = patientname;
    }

    public String getPmrn() {
        return pmrn;
    }

    public void setPmrn(String pmrn) {
        this.pmrn = pmrn;
    }

    public int getReportid() {
        return reportid;
    }

    public void setReportid(int reportid) {
        this.reportid = reportid;
    }

    public String getReporttxt() {
        return reporttxt;
    }

    public void setReporttxt(String reporttxt) {
        this.reporttxt = reporttxt;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSigner() {
        return signer;
    }

    public void setSigner(String signer) {
        this.signer = signer;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }
    private String pmrn;
    private String patientname ;
    private java.sql.Timestamp  dob ;
    private String sex  ;
    private String street;
    private String city;
    private String state;
    private String zip_code;
    private String reporttxt ;
    private String signer ;
    private String dictator ;
    private String examdescription ;
    private String  accessionnumber ;
    private int reportid ;




















}
