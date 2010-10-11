/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.dao;

//import org.rsna.isn.transfercontent.pix.*;
import java.sql.Timestamp;

/**
 *
 * @author oyesanyf
 */
public class RSNAPixMessageDataType {
    
    public  RSNAPixMessageDataType() {
        
    }


private int map_id ;
private String  rsna_id ;
private int  patient_id ;
private boolean registered;

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public int getMap_id() {
        return map_id;
    }

    public void setMap_id(int map_id) {
        this.map_id = map_id;
    }

    public Timestamp getModified_date() {
        return modified_date;
    }

    public void setModified_date(Timestamp modified_date) {
        this.modified_date = modified_date;
    }

    public String getPatient_alias() {
        return patient_alias;
    }

    public void setPatient_alias(String patient_alias) {
        this.patient_alias = patient_alias;
    }

    public String getPatient_alias_firstname() {
        return patient_alias_firstname;
    }

    public void setPatient_alias_firstname(String patient_alias_firstname) {
        this.patient_alias_firstname = patient_alias_firstname;
    }

    public String getPatient_alias_lastname() {
        return patient_alias_lastname;
    }

    public RSNAPixMessageDataType(int map_id, String rsna_id, int patient_id, Timestamp modified_date, String patient_alias, String patient_alias_lastname, String patient_alias_firstname) {
        this.map_id = map_id;
        this.rsna_id = rsna_id;
        this.patient_id = patient_id;
        this.modified_date = modified_date;
        this.patient_alias = patient_alias;
        this.patient_alias_lastname = patient_alias_lastname;
        this.patient_alias_firstname = patient_alias_firstname;
    }

    public void setPatient_alias_lastname(String patient_alias_lastname) {
        this.patient_alias_lastname = patient_alias_lastname;
    }

    public int getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }

    public String getRsna_id() {
        return rsna_id;
    }

    public void setRsna_id(String rsna_id) {
        this.rsna_id = rsna_id;
    }
private java.sql.Timestamp  modified_date ;
private String patient_alias ;
private String  patient_alias_lastname ;
private String   patient_alias_firstname  ;


}
