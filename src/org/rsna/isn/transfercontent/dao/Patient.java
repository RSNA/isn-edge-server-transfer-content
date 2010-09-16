/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsna.isn.transfercontent.dao;

/**
 *
 * @author oyesanyf
 */
public class Patient {

    private int patient_id;
    private String mrn;
    private String patient_name;

    public Patient() {

    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public java.sql.Timestamp getDob() {
        return dob;
    }

    public void setDob(java.sql.Timestamp dob) {
        this.dob = dob;
    }

    public java.sql.Timestamp getModified_date() {
        return modified_date;
    }

    public void setModified_date(java.sql.Timestamp modified_date) {
        this.modified_date = modified_date;
    }

    public String getMrn() {
        return mrn;
    }

    public Patient(int patient_id, String mrn, String patient_name, java.sql.Timestamp dob, String sex, String street, String city, String state, String zip_code) {
        this.patient_id = patient_id;
        this.mrn = mrn;
        this.patient_name = patient_name;
        this.dob = dob;
        this.sex = sex;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip_code = zip_code;
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
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
    private java.sql.Timestamp dob;
    private String sex;
    private String street;
    private String city;
    private String state;
    private String zip_code;
    private java.sql.Timestamp modified_date;
}
