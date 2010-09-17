/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsna.isn.transfercontent.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;

public class SQLQueries {

    private static Connection con = null;
    private static String UserName = null;
    private static String PassWord = null;
    private static String SqlHost = null;
    private static String URL = null;
    private static String RSNADB = null;
    private static Configuration config;
    private static TransferContentJobStatus tcjs;
    private static Exam exam;
    private static SubmissionSetSqlQueryData submitData;

    /**
     * Method finds  Patient data using mrn as input
     * argument mrn
     * @author oyesanyf
     * <p>
     * @param  String SetMrnInput
     * @return int patient_id, String mrn,String patient_name,java.sql.Timestamp dob,String sex,String street,String city,String state,String zip_code,java.sql.Timestamp modified_date,
     */
    public static Patient GetPatientByPatientMrn(String SetMRNInput) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread GetPatientByPatientMrn = new RunnableThread("GetPatientByPatientMrn");
        GetPatientByPatientMrn.run();

        GetDBCredentials db = new GetDBCredentials();
        UserName = GetDBCredentials.Credentials().getUsername();
        PassWord = GetDBCredentials.Credentials().getPassword();
        SqlHost = GetDBCredentials.Credentials().getSqlhost();
        RSNADB = GetDBCredentials.Credentials().getRsnadb();
        URL = "jdbc:postgresql://" + SqlHost + "/" + RSNADB;

        DBConnection conn = new DBConnection();
        Patient patient = new Patient();
        con = conn.runConnect(SqlHost, RSNADB, UserName, PassWord);
        System.out.println("Database connection established");

        try {
            con.setAutoCommit(true);
            Statement s = con.createStatement();
            s.executeQuery("SELECT * from public.patients where mrn = '" + SetMRNInput + "' ");
            ResultSet rs = s.getResultSet();

            while (rs.next()) {
                patient.setPatient_id(rs.getInt("patient_id"));
                patient.setMrn(rs.getString("mrn"));
                patient.setPatient_name(rs.getString("patient_name"));
                patient.setDob(rs.getTimestamp("dob"));
                patient.setSex(rs.getString("sex"));
                patient.setStreet(rs.getString("street"));
                patient.setCity(rs.getString("city"));
                patient.setState(rs.getString("state"));
                patient.setZip_code(rs.getString("zip_code"));
                patient.setModified_date(rs.getTimestamp("modified_date"));
            }
            rs.close(); // close result set
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            con.close();
            if (con != null) {
                try {
                    con.close();
                    System.out.println("Select operation complete");
                    System.out.println("Database connection terminated");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return patient;
    }

    /**
     * Method finds  Patient data using patientid as input
     * argument mrn
     * @author oyesanyf
     * <p>
     * @param  String SetMrnInput
     * @return int patient_id, String mrn,String patient_name,java.sql.Timestamp dob,String sex,String street,String city,String state,String zip_code,java.sql.Timestamp modified_date,
     */
    public static Patient GetPatientByPatientID(String patientid) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread GetPatientByPatientID = new RunnableThread("GetPatientByPatientID");
        GetPatientByPatientID.run();

        GetDBCredentials db = new GetDBCredentials();
        UserName = GetDBCredentials.Credentials().getUsername();
        PassWord = GetDBCredentials.Credentials().getPassword();
        SqlHost = GetDBCredentials.Credentials().getSqlhost();
        RSNADB = GetDBCredentials.Credentials().getRsnadb();
        URL = "jdbc:postgresql://" + SqlHost + "/" + RSNADB;

        DBConnection conn = new DBConnection();
        Patient patient = new Patient();
        con = conn.runConnect(SqlHost, RSNADB, UserName, PassWord);
        System.out.println("Database connection established");

        try {
            con.setAutoCommit(true);
            Statement s = con.createStatement();
            s.executeQuery("SELECT * from public.patients where patient_id = '" + patientid + "' ");
            ResultSet rs = s.getResultSet();

            while (rs.next()) {
                patient.setPatient_id(rs.getInt("patient_id"));
                patient.setMrn(rs.getString("mrn"));
                patient.setPatient_name(rs.getString("patient_name"));
                patient.setDob(rs.getTimestamp("dob"));
                patient.setSex(rs.getString("sex"));
                patient.setStreet(rs.getString("street"));
                patient.setCity(rs.getString("city"));
                patient.setState(rs.getString("state"));
                patient.setZip_code(rs.getString("zip_code"));
                patient.setModified_date(rs.getTimestamp("modified_date"));
            }
            rs.close(); // close result set
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            con.close();
            if (con != null) {
                try {
                    con.close();
                    System.out.println("Select operation complete");
                    System.out.println("Database connection terminated");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return patient;
    }

    /**
     * Method gets configuration values fron configuration tablet
     * argument key
     * @author oyesanyf
     * <p>
     * @param  String key
     * @return String value
     */
    public static Configuration GetConfiguration(String key) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread GetConfiguration = new RunnableThread("GetConfiguration");
        GetConfiguration.run();

        GetDBCredentials db = new GetDBCredentials();
        UserName = GetDBCredentials.Credentials().getUsername();
        PassWord = GetDBCredentials.Credentials().getPassword();
        SqlHost = GetDBCredentials.Credentials().getSqlhost();
        RSNADB = GetDBCredentials.Credentials().getRsnadb();
        URL = "jdbc:postgresql://" + SqlHost + "/" + RSNADB;



        DBConnection conn = new DBConnection();
        System.out.println("Database connection established");

        try {
            con = conn.runConnect(SqlHost, RSNADB, UserName, PassWord);
            con.setAutoCommit(true);
            Statement s = con.createStatement();
            s.executeQuery("SELECT * from public.configurations where key = '" + key + "' ");
            ResultSet rs = s.getResultSet();
            config = new Configuration();
            while (rs.next()) {
                config.setKey(rs.getString("key"));
                config.setValue(rs.getString("value"));
                config.setModified_date(rs.getTimestamp("modified_date"));
            }
            rs.close(); // close result set
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            con.close();
            if (con != null) {
                try {
                    con.close();
                    System.out.println("Select operation complete");
                    System.out.println("Database connection terminated");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return config;
    }

    /**
     * Method selects  patient_id, mrn ,patient_name ,dob ,sex, street, city , state,zip_code , exam_id, accession_number ,exam_description , report_id, status, status_timestamp , report_text , dictator , transcriber ,signer   from exam table
     * @author oyesanyf
     * <p>
     * @param  sql  select *
     * @return intpatient_id , String mrn ,String patient_name ,java.sql.Timestamp dob ,String sex ,String street ,String city ,String state ,String zip_code ,int exam_id ,String accession_number ,String exam_description ,String report_id ,int status ,java.sql.Timestamp status_timestamp ,String report_text ,String dictator ,String transcriber ,String signer  */
    public static Exam GetTransferContentExam(int status) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread GetConfiguration = new RunnableThread("GetConfiguration");
        GetConfiguration.run();
        GetDBCredentials db = new GetDBCredentials();
        UserName = GetDBCredentials.Credentials().getUsername();
        PassWord = GetDBCredentials.Credentials().getPassword();
        SqlHost = GetDBCredentials.Credentials().getSqlhost();
        RSNADB = GetDBCredentials.Credentials().getRsnadb();
        URL = "jdbc:postgresql://" + SqlHost + "/" + RSNADB;

        DBConnection conn = new DBConnection();
        System.out.println("Database connection established");

        try {
            con = conn.runConnect(SqlHost, RSNADB, UserName, PassWord);
            con.setAutoCommit(true);
            Statement s = con.createStatement();
            s.executeQuery("SELECT * from v_exam_status where status = '" + status + "' ");
            ResultSet rs = s.getResultSet();
             exam = new Exam() ;

             while (rs.next()) {

                exam.setPatient_id(rs.getInt("patient_id"));
                
            }
            rs.close(); // close result set
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            con.close();
            if (con != null) {
                try {
                    con.close();
                    System.out.println("Select operation complete");
                    System.out.println("Database connection terminated");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return exam;
    }

    /**
     * Method gets configuration values fron configuration tablet
     * argument key
     * @author oyesanyf
     * <p>
     * @param  String key
     * @return String value
     */
    public static TransferContentJobStatus GetTransferContentJobStatus(int status) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread GetTransferContentJobStatus = new RunnableThread("GetTransferContentJobStatus");
        GetTransferContentJobStatus.run();

        GetDBCredentials db = new GetDBCredentials();
        UserName = GetDBCredentials.Credentials().getUsername();
        PassWord = GetDBCredentials.Credentials().getPassword();
        SqlHost = GetDBCredentials.Credentials().getSqlhost();
        RSNADB = GetDBCredentials.Credentials().getRsnadb();
        URL = "jdbc:postgresql://" + SqlHost + "/" + RSNADB;
        DBConnection conn = new DBConnection();
        System.out.println("Database connection established");
        try {
            con = conn.runConnect(SqlHost, RSNADB, UserName, PassWord);
            con.setAutoCommit(true);
            Statement s = con.createStatement();
            s.executeQuery("SELECT * from v_job_status where status = '" + status + "' ");
            ResultSet rs = s.getResultSet();
            tcjs = new TransferContentJobStatus();

            while (rs.next()) {
                tcjs.setJob_id(rs.getInt("job_id"));
                tcjs.setExam_id(rs.getInt("exam_id"));
                tcjs.setDelay_in_hrs(rs.getInt("delay_in_hrs"));
                tcjs.setStatus(rs.getInt("status"));
                tcjs.setStatus_message(rs.getString("status_message"));
                tcjs.setlast_transaction_timestamp(rs.getTimestamp("last_transaction_timestamp"));

            }
            rs.close();
            // close result set
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            con.close();
            if (con != null) {
                try {
                    con.close();
                    System.out.println("Select operation complete");
                    System.out.println("Database connection terminated");
                } catch (Exception e) {
                    String msg = e.getMessage();
                    System.out.println("SQLQueries: " + msg);
                }
            }
        }
        return tcjs;
    }

    public static SubmissionSetSqlQueryData GetSubmisionSetData(int jobid) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {
        RunnableThread GetTransferContentJobStatus = new RunnableThread("GetTransferContentJobStatus");
        GetTransferContentJobStatus.run();
        GetDBCredentials db = new GetDBCredentials();
        UserName = GetDBCredentials.Credentials().getUsername();
        PassWord = GetDBCredentials.Credentials().getPassword();
        SqlHost = GetDBCredentials.Credentials().getSqlhost();
        RSNADB = GetDBCredentials.Credentials().getRsnadb();
        URL = "jdbc:postgresql://" + SqlHost + "/" + RSNADB;
        DBConnection conn = new DBConnection();
        System.out.println("Database connection established");
        try {
            con = conn.runConnect(SqlHost, RSNADB, UserName, PassWord);
            con.setAutoCommit(true);
            Statement s = con.createStatement();
            s.executeQuery("SELECT j.exam_id,  e.patient_id,  p.mrn,  p.patient_name,  p.dob,  p.sex,  p.street,  p.city,  p.state,  p.zip_code,  r.report_text,  r.signer,  r.dictator,  e.exam_description,  e.accession_number,  j.report_id FROM public.jobs j, public.exams e,  public.patients p,  public.reports r " + " WHERE j.exam_id = e.exam_id AND j.report_id = r.report_id AND e.patient_id = p.patient_id AND job_id = '" + jobid + "' ");
            ResultSet rs = s.getResultSet();
            submitData = new SubmissionSetSqlQueryData();
            while (rs.next()) {
                submitData.setExamid(rs.getInt("exam_id"));
                submitData.setPatientid(rs.getInt("patient_id"));
                submitData.setPmrn(rs.getString("mrn"));
                submitData.setPatientname(rs.getString("patient_name"));
                submitData.setDob(rs.getTimestamp("dob"));
                submitData.setSex(rs.getString("sex"));
                submitData.setStreet(rs.getString("street"));
                submitData.setCity(rs.getString("city"));
                submitData.setState(rs.getString("state"));
                submitData.setZip_code(rs.getString("zip_code"));
                submitData.setReporttxt(rs.getString("report_text"));
                submitData.setSigner(rs.getString("signer"));
                submitData.setDictator(rs.getString("dictator"));
                submitData.setExamdescription(rs.getString("exam_description"));
                submitData.setAccessionnumber(rs.getString("accession_number"));
                submitData.setReportid(rs.getInt("report_id"));
            }
            rs.close();
            // close result set
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            con.close();
            if (con != null) {
                try {
                    con.close();
                    System.out.println("Select operation complete");
                    System.out.println("Database connection terminated");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return submitData;
    }
}






    