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
public class Job {
    private int job_id;
    private int job_set_id;
    private int exam_id;
    private int report_id;
    private int delay_in_hours;
    private String document_id;
    private java.sql.Timestamp modified_date;

    public Job() {
    }

    public Job(int job_set_id, int exam_id, int report_id, int delay_in_hours, String document_id, Timestamp modified_date) {
        this.job_set_id = job_set_id;
        this.exam_id = exam_id;
        this.report_id = report_id;
        this.delay_in_hours = delay_in_hours;
        this.document_id = document_id;
        this.modified_date = modified_date;
    }
    public int getDelay_in_hours() {
        return delay_in_hours;
    }

    public void setDelay_in_hours(int delay_in_hours) {
        this.delay_in_hours = delay_in_hours;
    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
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

    public int getJob_set_id() {
        return job_set_id;
    }

    public void setJob_set_id(int job_set_id) {
        this.job_set_id = job_set_id;
    }

    public Timestamp getModified_date() {
        return modified_date;
    }

    public void setModified_date(Timestamp modified_date) {
        this.modified_date = modified_date;
    }

    public int getReport_id() {
        return report_id;
    }

    public void setReport_id(int report_id) {
        this.report_id = report_id;
    }


}
