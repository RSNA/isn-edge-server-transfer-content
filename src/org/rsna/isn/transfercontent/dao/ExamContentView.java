/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsna.isn.transfercontent.dao;

import java.sql.Timestamp;


public class ExamContentView {

    public ExamContentView() {
    }
    private int patient_id;
    private String mrn;
    private String patient_name;
    private java.sql.Timestamp dob;
    private String sex;
    private String street;

    public String getAccession_number() {
        return accession_number;
    }

    public void setAccession_number(String accession_number) {
        this.accession_number = accession_number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public ExamContentView(int patient_id, String mrn, String patient_name, Timestamp dob, String sex, String street, String city, String state, String zip_code, int exam_id, String accession_number, String exam_description, String report_id, String status, Timestamp status_timestamp, String report_text, String dictator, String transcriber, String signer) {
        this.patient_id = patient_id;
        this.mrn = mrn;
        this.patient_name = patient_name;
        this.dob = dob;
        this.sex = sex;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip_code = zip_code;
        this.exam_id = exam_id;
        this.accession_number = accession_number;
        this.exam_description = exam_description;
        this.report_id = report_id;
        this.status = status;
        this.status_timestamp = status_timestamp;
        this.report_text = report_text;
        this.dictator = dictator;
        this.transcriber = transcriber;
        this.signer = signer;
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

    public String getExam_description() {
        return exam_description;
    }

    public void setExam_description(String exam_description) {
        this.exam_description = exam_description;
    }

    public int getExam_id() {
        return exam_id;
    }

    public void setExam_id(int exam_id) {
        this.exam_id = exam_id;
    }

    public String getMrn() {
        return mrn;
    }

    public void setMrn(String mrn) {
        this.mrn = mrn;
    }

    public int getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public String getReport_id() {
        return report_id;
    }

    public void setReport_id(String report_id) {
        this.report_id = report_id;
    }

    public String getReport_text() {
        return report_text;
    }

    public void setReport_text(String report_text) {
        this.report_text = report_text;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getStatus_timestamp() {
        return status_timestamp;
    }

    public void setStatus_timestamp(Timestamp status_timestamp) {
        this.status_timestamp = status_timestamp;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getTranscriber() {
        return transcriber;
    }

    public void setTranscriber(String transcriber) {
        this.transcriber = transcriber;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }
    private String city;
    private String state;
    private String zip_code;
    private int exam_id;
    private String accession_number;
    private String exam_description;
    private String report_id;
    private String status;
    private java.sql.Timestamp status_timestamp;
    private String report_text;
    private String dictator;
    private String transcriber;
    private String signer;
}
