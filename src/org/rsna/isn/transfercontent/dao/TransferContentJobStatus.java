/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsna.isn.transfercontent.dao;

import java.sql.Timestamp;

/**
 *
 * @author oyesanyf
 */
public class TransferContentJobStatus {

   
    
    public  TransferContentJobStatus() {
        
    }

    public int getDelay_in_hrs() {
        return delay_in_hrs;
    }

    public TransferContentJobStatus(int job_id, int exam_id, int delay_in_hrs, int status, String status_message, Timestamp last_transaction_timestamp) {
        this.job_id = job_id;
        this.exam_id = exam_id;
        this.delay_in_hrs = delay_in_hrs;
        this.status = status;
        this.status_message = status_message;
        this.last_transaction_timestamp = last_transaction_timestamp;
    }

    public void setDelay_in_hrs(int delay_in_hrs) {
        this.delay_in_hrs = delay_in_hrs;
    }

    public int getExam_id() {
        return exam_id;
    }

    public void setExam_id(int exam_id) {
        this.exam_id = exam_id;
    }

    public int getJob_id() {
        return job_id;
    }

    public void setJob_id(int job_id) {
        this.job_id = job_id;
    }

    public Timestamp getlast_transaction_timestamp() {
        return last_transaction_timestamp;
    }

    public void setlast_transaction_timestamp(Timestamp last_transaction_timestamp) {
        this.last_transaction_timestamp = last_transaction_timestamp;
    }

    public String getStatus_message() {
        return status_message;
    }

    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int job_id;
    private int exam_id;
    private int delay_in_hrs;
    private int status ;
    private String status_message  ;
    private java.sql.Timestamp last_transaction_timestamp ;

}
