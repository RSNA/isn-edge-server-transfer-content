/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.dao;

import java.sql.Timestamp;

/**
 *
 * @author erackus
 */
public class Transaction {

    private int transaction_id;
    private int job_id;
    private int status;
    private String status_message;
    private java.sql.Timestamp modified_date;

    public Transaction() {
    }

    public Transaction(int job_id, int status, String status_message, Timestamp modified_date) {
        this.job_id = job_id;
        this.status = status;
        this.status_message = status_message;
        this.modified_date = modified_date;
    }

    public Timestamp getModified_date() {
        return modified_date;
    }

    public void setModified_date(Timestamp modified_date) {
        this.modified_date = modified_date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatus_message() {
        return status_message;
    }

    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }

    public int getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

}
