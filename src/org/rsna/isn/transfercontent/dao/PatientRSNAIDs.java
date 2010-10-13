/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.dao;

/**
 *
 * @author erackus
 */

import java.sql.Timestamp;

public class PatientRSNAIDs {

    private String rsnaID;
    private int patientID;
    private Timestamp modifiedDate;
    private String patientAliasLastName;
    private String patientAliasFirstName;
    private boolean registered;

    public PatientRSNAIDs() {
    }

    public PatientRSNAIDs(String rsnaID, int patientID, Timestamp modifiedDate, String patientAliasLastName, String patientAliasFirstName, boolean registered) {
        this.rsnaID = rsnaID;
        this.patientID = patientID;
        this.modifiedDate = modifiedDate;
        this.patientAliasLastName = patientAliasLastName;
        this.patientAliasFirstName = patientAliasFirstName;
        this.registered = registered;
    }

    public Timestamp getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Timestamp modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    public String getPatientAliasFirstName() {
        return patientAliasFirstName;
    }

    public void setPatientAliasFirstName(String patientAliasFirstName) {
        this.patientAliasFirstName = patientAliasFirstName;
    }

    public String getPatientAliasLastName() {
        return patientAliasLastName;
    }

    public void setPatientAliasLastName(String patientAliasLastName) {
        this.patientAliasLastName = patientAliasLastName;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public String getRsnaID() {
        return rsnaID;
    }

    public void setRsnaID(String rsnaID) {
        this.rsnaID = rsnaID;
    }

}
