/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsna.isn.transfercontent.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

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
    private static ExamContentView examContentView;
    private static int PatientInsertStatus = 0;
    private static String pID;
    private static int pupMRNStatus = 0;
    private static int reportInsertStatus = 0;
    private static int examInsertStatus = 0;
    private static int mergeInsertStatus = 0;
    private static int mergeStatus = 0;
    private static ArrayList<String> jobList;
    private static int examReady;
    private static String examReadyString;
    private static ArrayList<String> examList;
    private static int tranUp;
    private static ArrayList<String> accessNumList;
    private static String accessReady;
    private static ArrayList<String> mrnList;
    private static String mrnReady;
    private static String examID;
    private static int jobReady;
    private static int tranInsertStatus;
    private static int existexamID;
    private static int examReportID;
    private static int reportUp;
    private static RSNAPixMessageDataType pixMsg;

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

    public static PatientRSNAIDs GetRSNAIDfromPatientID(int patientid) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread GetPatientByPatientID = new RunnableThread("GetPatientByPatientID");
        GetPatientByPatientID.run();

        GetDBCredentials db = new GetDBCredentials();
        UserName = GetDBCredentials.Credentials().getUsername();
        PassWord = GetDBCredentials.Credentials().getPassword();
        SqlHost = GetDBCredentials.Credentials().getSqlhost();
        RSNADB = GetDBCredentials.Credentials().getRsnadb();
        URL = "jdbc:postgresql://" + SqlHost + "/" + RSNADB;

        DBConnection conn = new DBConnection();
        PatientRSNAIDs patientRSNAIDs = new PatientRSNAIDs();
        con = conn.runConnect(SqlHost, RSNADB, UserName, PassWord);
        System.out.println("Database connection established");

        try {
            con.setAutoCommit(true);
            Statement s = con.createStatement();
            s.executeQuery("SELECT * from public.patient_rsna_ids where patient_id = '" + patientid + "' ");
            ResultSet rs = s.getResultSet();

            while (rs.next()) {
                patientRSNAIDs.setRsnaID(rs.getString("rsna_id"));
                patientRSNAIDs.setPatientID(rs.getInt("patient_id"));
                patientRSNAIDs.setModifiedDate(rs.getTimestamp("modified_date"));
                patientRSNAIDs.setPatientAliasLastName(rs.getString("patient_alias_lastname"));
                patientRSNAIDs.setPatientAliasFirstName(rs.getString("patient_alias_firstname"));
                patientRSNAIDs.setRegistered(rs.getBoolean("registered"));
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
        return patientRSNAIDs;
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
    public static ArrayList<TransferContentJobStatus> GetTransferContentJobStatus(int status) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

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
        ArrayList<TransferContentJobStatus> readyJobs = new ArrayList<TransferContentJobStatus>();
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
                readyJobs.add(tcjs);
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
        return readyJobs;
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
            String qString = "SELECT v_exam_status.patient_id, v_exam_status.mrn, v_exam_status.patient_name, v_exam_status.dob, v_exam_status.sex, v_exam_status.street, " +
                    "v_exam_status.city, v_exam_status.state, v_exam_status.zip_code, v_exam_status.exam_id, v_exam_status.accession_number, v_exam_status.exam_description, " +
                    "v_exam_status.report_id, v_exam_status.report_text, v_exam_status.dictator, v_exam_status.signer FROM " +
                    "public.v_exam_status, public.v_job_status WHERE v_job_status.exam_id = v_exam_status.exam_id AND v_job_status.job_id = '" + jobid + "'";
            s.executeQuery(qString);
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

    public static ExamContentView GetTransferContentExamByStatus(int status) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread GetTransferContentExamByStatus = new RunnableThread("GetTransferContentExamByStatus");
        GetTransferContentExamByStatus.run();
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
            examContentView = new ExamContentView();

            while (rs.next()) {

                examContentView.setPatient_id(rs.getInt("patient_id"));
                examContentView.setMrn(rs.getString("mrn"));
                examContentView.setPatient_name(rs.getString("mrn"));
                examContentView.setDob(rs.getTimestamp("dob"));
                examContentView.setSex(rs.getString("sex"));
                examContentView.setStreet(rs.getString("street"));
                examContentView.setCity(rs.getString("city"));
                examContentView.setState(rs.getString("state"));
                examContentView.setZip_code(rs.getString("zip_code"));
                examContentView.setExam_id(rs.getInt("exam_id"));
                examContentView.setAccession_number(rs.getString("accession_number"));
                examContentView.setExam_description(rs.getString("exam_description"));
                examContentView.setReport_id(rs.getString("report_id"));
                examContentView.setStatus(rs.getString("status"));
                examContentView.setStatus_timestamp(rs.getTimestamp("status_timestamp"));
                examContentView.setReport_text(rs.getString("report_text"));
                examContentView.setDictator(rs.getString("dictator"));
                examContentView.setTranscriber(rs.getString("transcriber"));
                examContentView.setSigner(rs.getString("signer"));

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
        return examContentView;
    }

    public static ExamContentView GetTransferContentExamByMRN(String MRN) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread GetTransferContentExamByExamID = new RunnableThread("GetTransferContentExamByExamID");
        GetTransferContentExamByExamID.run();
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
            s.executeQuery("SELECT * from v_exam_status where status = '" + MRN + "' ");
            ResultSet rs = s.getResultSet();
            examContentView = new ExamContentView();

            while (rs.next()) {

                examContentView.setPatient_id(rs.getInt("patient_id"));
                examContentView.setMrn(rs.getString("mrn"));
                examContentView.setPatient_name(rs.getString("mrn"));
                examContentView.setDob(rs.getTimestamp("dob"));
                examContentView.setSex(rs.getString("sex"));
                examContentView.setStreet(rs.getString("street"));
                examContentView.setCity(rs.getString("city"));
                examContentView.setState(rs.getString("state"));
                examContentView.setZip_code(rs.getString("zip_code"));
                examContentView.setExam_id(rs.getInt("exam_id"));
                examContentView.setAccession_number(rs.getString("accession_number"));
                examContentView.setExam_description(rs.getString("exam_description"));
                examContentView.setReport_id(rs.getString("report_id"));
                examContentView.setStatus(rs.getString("status"));
                examContentView.setStatus_timestamp(rs.getTimestamp("status_timestamp"));
                examContentView.setReport_text(rs.getString("report_text"));
                examContentView.setDictator(rs.getString("dictator"));
                examContentView.setTranscriber(rs.getString("transcriber"));
                examContentView.setSigner(rs.getString("signer"));

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
        return examContentView;
    }

    public static int InsertPatient(String mrn, String patient_name, java.sql.Timestamp dob, String sex, String street, String city, String state, String zip_code, java.sql.Timestamp modified_date) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread InsertPatient = new RunnableThread("InsertPatient");
        InsertPatient.run();


        GetDBCredentials db = new GetDBCredentials();
        UserName = GetDBCredentials.Credentials().getUsername();
        PassWord = GetDBCredentials.Credentials().getPassword();
        SqlHost = GetDBCredentials.Credentials().getSqlhost();
        RSNADB = GetDBCredentials.Credentials().getRsnadb();
        URL = "jdbc:postgresql://" + SqlHost + "/" + RSNADB;
        DBConnection conn = new DBConnection();
        System.out.println("Database connection established");

        String PatientDemo = "insert into patients( mrn, patient_name, dob, sex, street, city, state, zip_code, modified_date) values ( ?, ?, ?, ?, ? , ?, ? , ? , ?  ) ";

        //  String PatientDemo = "insert into patients(patient_id, mrn, patient_name, dob, sex, street, city, state, zip_code, modified_date) values (?, ?, ?, ?, ?, ? , ?, ? , ? , ?  ) ";
        PreparedStatement ps = null;


        try {


            con = DriverManager.getConnection(URL, UserName, PassWord);
            System.out.println("Database connection established");
            con.setAutoCommit(false);

            ps = con.prepareStatement(PatientDemo);



            // ps.setInt(1, patient_id);
            ps.setString(1, mrn);
            ps.setString(2, patient_name);
            ps.setTimestamp(3, dob);
            ps.setString(4, sex);
            ps.setString(5, street);
            ps.setString(6, city);
            ps.setString(7, state);
            ps.setString(8, zip_code);
            ps.setTimestamp(9, modified_date);


            PatientInsertStatus = ps.executeUpdate();

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {

                    e.printStackTrace();
                }

                ps = null;
            }

            if (con != null) {
                try {
                    con.commit();
                    con.rollback();
                    con.close();
                    System.out.println("Database connection terminated");
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }
        return PatientInsertStatus;

    }

    public static String FindPatientMRN(String mrn) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread FindPatientID = new RunnableThread("FindPatientID");
        FindPatientID.run();


        GetDBCredentials db = new GetDBCredentials();

        UserName = GetDBCredentials.Credentials().getUsername();
        PassWord = GetDBCredentials.Credentials().getPassword();
        SqlHost = GetDBCredentials.Credentials().getSqlhost();
        RSNADB = GetDBCredentials.Credentials().getRsnadb();
        URL = "jdbc:postgresql://" + SqlHost + "/" + RSNADB;



        DBConnection conn = new DBConnection();
        System.out.println("Database connection established");
        con = conn.runConnect(SqlHost, RSNADB, UserName, PassWord);

        try {



            System.out.println("Database connection established");
            con.setAutoCommit(true);
            Statement s = con.createStatement();
            s.executeQuery("SELECT mrn from patients where mrn = '" + mrn + "' ");
            ResultSet rs = s.getResultSet();
            pID = null;

            while (rs.next()) {
                pID = rs.getString(1);
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
        return pID;
    }

    public static int PatientUpdate(String mrn, String patient_name, java.sql.Timestamp dob, String sex, String street, String city, String state, String zip_code, java.sql.Timestamp modified_date) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread InsertPatient = new RunnableThread("InsertPatient");
        InsertPatient.run();


        GetDBCredentials db = new GetDBCredentials();
        UserName = GetDBCredentials.Credentials().getUsername();
        PassWord = GetDBCredentials.Credentials().getPassword();
        SqlHost = GetDBCredentials.Credentials().getSqlhost();
        RSNADB = GetDBCredentials.Credentials().getRsnadb();
        URL = "jdbc:postgresql://" + SqlHost + "/" + RSNADB;
        DBConnection conn = new DBConnection();
        System.out.println("Database connection established");
        con = conn.runConnect(SqlHost, RSNADB, UserName, PassWord);


        try {



            System.out.println("Database connection established");
            con.setAutoCommit(false);
            Statement s = con.createStatement();


            String sql = "update patients set mrn='" + mrn + "',patient_name='" + patient_name + "', dob='" + dob + "', sex='" + sex + "',street='" + street + "',city='" + city + "', state='" + state + "', zip_code='" + zip_code + "', modified_date='" + modified_date + "'  where mrn='" + mrn + "'";


            pupMRNStatus = 0;
            pupMRNStatus = s.executeUpdate(sql);
            if (pupMRNStatus == 1) {

                con.commit();
                System.out.println("Row is updated.");
            } else {
                System.out.println("Row is not updated.");
            }

            con.close();


        } catch (SQLException e) {


            e.printStackTrace();

        } finally {

            con.close();
            if (con != null) {
                try {
                    con.close();
                    System.out.println("update operation complete");
                    System.out.println("Database connection terminated");
                } catch (SQLException e) {

                    e.printStackTrace();
                }
            }

        }
        return pupMRNStatus;
    }

    public static int insertReport(int exam_id, String proc_code, String status, Timestamp status_timestamp, String report_text, String signer, String dictator, String transcriber, Timestamp modified_date) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread InsertReport = new RunnableThread("InsertReport");
        InsertReport.run();
        GetDBCredentials db = new GetDBCredentials();
        UserName = GetDBCredentials.Credentials().getUsername();
        PassWord = GetDBCredentials.Credentials().getPassword();
        SqlHost = GetDBCredentials.Credentials().getSqlhost();
        RSNADB = GetDBCredentials.Credentials().getRsnadb();
        URL = "jdbc:postgresql://" + SqlHost + "/" + RSNADB;
        DBConnection conn = new DBConnection();
        System.out.println("Database connection established");
        String insertreport = "insert into reports(exam_id,  proc_code,  status, status_timestamp, report_text,  signer,  dictator, transcriber, modified_date) values ( ?, ?, ?, ?, ? , ?, ? , ? , ?  ) ";
        PreparedStatement ps = null;


        try {
            con = DriverManager.getConnection(URL, UserName, PassWord);
            System.out.println("Database connection established");
            con.setAutoCommit(false);
            ps = con.prepareStatement(insertreport);



            // ps.setInt(1, patient_id);
            // ps.setInt(1, report_id);
            ps.setInt(1, exam_id);
            ps.setString(2, proc_code);
            ps.setString(3, status);
            ps.setTimestamp(4, status_timestamp);
            ps.setString(5, report_text);
            ps.setString(6, signer);
            ps.setString(7, dictator);
            ps.setString(8, transcriber);
            ps.setTimestamp(9, modified_date);


            reportInsertStatus = ps.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {

                    e.printStackTrace();
                }

                ps = null;
            }

            if (con != null) {
                try {
                    con.commit();
                    con.rollback();
                    con.close();
                    System.out.println("Database connection terminated");
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }
        return reportInsertStatus;

    }

    public static int updateReport(int exam_id, String proc_code, String status, Timestamp status_timestamp, String report_text, String signer, String dictator, String transcriber, Timestamp modified_date) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread updateReport = new RunnableThread("updateReport");
        updateReport.run();


        GetDBCredentials db = new GetDBCredentials();
        UserName = GetDBCredentials.Credentials().getUsername();
        PassWord = GetDBCredentials.Credentials().getPassword();
        SqlHost = GetDBCredentials.Credentials().getSqlhost();
        RSNADB = GetDBCredentials.Credentials().getRsnadb();
        URL = "jdbc:postgresql://" + SqlHost + "/" + RSNADB;

        DBConnection conn = new DBConnection();
        System.out.println("Database connection established");
        con = conn.runConnect(SqlHost, RSNADB, UserName, PassWord);

        try {

            System.out.println("Database connection established");
            con.setAutoCommit(true);
            Statement s = con.createStatement();

            String sql = "update reports set exam_id='" + exam_id + "',status='" + status + "', status_timestamp='" + status_timestamp + "', report_text='" + report_text + "', signer='" + signer + "',dictator='" + dictator + "',transcriber='" + transcriber + "', modified_date='" + modified_date + "'  where exam_id='" + exam_id + "'";


            reportUp = s.executeUpdate(sql);

            if (reportUp == 1) {
                con.commit();
                System.out.println("Row is updated.");
            } else {
                System.out.println("Row is not updated.");
            }



            s.close(); // close result set
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
        return tranUp;
    }

    public static int insertTransaction(int jobID, int status, String statusMessage, java.sql.Timestamp modifiedDate) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread InsertReport = new RunnableThread("InsertReport");
        InsertReport.run();
        GetDBCredentials db = new GetDBCredentials();
        UserName = GetDBCredentials.Credentials().getUsername();
        PassWord = GetDBCredentials.Credentials().getPassword();
        SqlHost = GetDBCredentials.Credentials().getSqlhost();
        RSNADB = GetDBCredentials.Credentials().getRsnadb();
        URL = "jdbc:postgresql://" + SqlHost + "/" + RSNADB;
        DBConnection conn = new DBConnection();
        System.out.println("Database connection established");
        String insertTransaction = "insert into transactions(job_id ,status ,status_message, modified_date) values ( ?, ?, ?, ? ) ";
        PreparedStatement ps = null;


        try {
            con = DriverManager.getConnection(URL, UserName, PassWord);
            System.out.println("Database connection established");
            con.setAutoCommit(false);
            ps = con.prepareStatement(insertTransaction);



            ps.setInt(1, jobID);
            ps.setInt(2, status);
            ps.setString(3, statusMessage);
            ps.setTimestamp(4, modifiedDate);


            tranInsertStatus = ps.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {

                    e.printStackTrace();
                }

                ps = null;
            }

            if (con != null) {
                try {
                    con.commit();
                    con.rollback();
                    con.close();
                    System.out.println("Database connection terminated");
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }
        return tranInsertStatus;

    }

    public static Exam findExamID(int patientid, String accessNUM) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread GetPatientByPatientID = new RunnableThread("GetPatientByPatientID");
        GetPatientByPatientID.run();

        GetDBCredentials db = new GetDBCredentials();
        UserName = GetDBCredentials.Credentials().getUsername();
        PassWord = GetDBCredentials.Credentials().getPassword();
        SqlHost = GetDBCredentials.Credentials().getSqlhost();
        RSNADB = GetDBCredentials.Credentials().getRsnadb();
        URL = "jdbc:postgresql://" + SqlHost + "/" + RSNADB;

        DBConnection conn = new DBConnection();
        Exam exam = new Exam();
        con = conn.runConnect(SqlHost, RSNADB, UserName, PassWord);
        System.out.println("Database connection established");

        try {
            con.setAutoCommit(true);
            Statement s = con.createStatement();
            s.executeQuery("SELECT distinct exams.exam_id, exams.accession_number FROM public.patients, public.exams WHERE   exams.patient_id = '" + patientid + "' and exams.accession_number = '" + accessNUM + "' ;");
            ResultSet rs = s.getResultSet();

            while (rs.next()) {
                exam.setExam_id(rs.getInt("exam_id"));
                exam.setAccession_number(rs.getString("accession_number"));

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
    private int foundID;

    public static PatientDemographics GetPatientIDByPatientMrn(String mrn) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread GetPatientByPatientMrn = new RunnableThread("GetPatientByPatientMrn");
        GetPatientByPatientMrn.run();

        GetDBCredentials db = new GetDBCredentials();
        UserName = GetDBCredentials.Credentials().getUsername();
        PassWord = GetDBCredentials.Credentials().getPassword();
        SqlHost = GetDBCredentials.Credentials().getSqlhost();
        RSNADB = GetDBCredentials.Credentials().getRsnadb();
        URL = "jdbc:postgresql://" + SqlHost + "/" + RSNADB;

        DBConnection conn = new DBConnection();
        PatientDemographics patient = new PatientDemographics();
        con = conn.runConnect(SqlHost, RSNADB, UserName, PassWord);
        System.out.println("Database connection established");

        try {
            con.setAutoCommit(true);
            Statement s = con.createStatement();
            ResultSet idFound = s.executeQuery("SELECT distinct patient_id  from public.patients where mrn = '" + mrn + "' ");
            ResultSet rs = s.getResultSet();





            while (rs.next()) {
                patient.setPatient_id(rs.getInt("patient_id"));

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

    public static int insertExam(String accessNum, int patiendID, String examDesc, Timestamp modified_date) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread insertExam = new RunnableThread("insertExam");
        insertExam.run();
        GetDBCredentials db = new GetDBCredentials();
        UserName = GetDBCredentials.Credentials().getUsername();
        PassWord = GetDBCredentials.Credentials().getPassword();
        SqlHost = GetDBCredentials.Credentials().getSqlhost();
        RSNADB = GetDBCredentials.Credentials().getRsnadb();
        URL = "jdbc:postgresql://" + SqlHost + "/" + RSNADB;
        System.out.println("Database connection established");
        String insertreport = "insert into exams(accession_number, patient_id, exam_description, modified_date) values ( ?, ?, ?, ?) ";
        PreparedStatement ps = null;


        try {
            con = DriverManager.getConnection(URL, UserName, PassWord);
            System.out.println("Database connection established");
            con.setAutoCommit(false);
            ps = con.prepareStatement(insertreport);




            ps.setString(1, accessNum);
            ps.setInt(2, patiendID);
            ps.setString(3, examDesc);
            ps.setTimestamp(4, modified_date);


            examInsertStatus = ps.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {

                    e.printStackTrace();
                }

                ps = null;
            }

            if (con != null) {
                try {
                    con.commit();
                    con.rollback();
                    con.close();
                    System.out.println("Database connection terminated");
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }
        return examInsertStatus;

    }

    public static int insertMrnMergeUpdateEvent(String oldMrn, String newMrn, int oldPID, int newPID, int status, java.sql.Timestamp modifieddate) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {


        RunnableThread mrnMergeUpdate = new RunnableThread("mrnMergeUpdate");
        mrnMergeUpdate.run();
        GetDBCredentials db = new GetDBCredentials();
        UserName = GetDBCredentials.Credentials().getUsername();
        PassWord = GetDBCredentials.Credentials().getPassword();
        SqlHost = GetDBCredentials.Credentials().getSqlhost();
        RSNADB = GetDBCredentials.Credentials().getRsnadb();
        URL = "jdbc:postgresql://" + SqlHost + "/" + RSNADB;



        String insertMerge = "insert into patient_merge_events( old_mrn, new_mrn, old_patient_id, new_patient_id, status,  modified_date) values ( ?, ?, ?, ?, ? , ? ) ";

        //  String PatientDemo = "insert into patients(patient_id, mrn, patient_name, dob, sex, street, city, state, zip_code, modified_date) values (?, ?, ?, ?, ?, ? , ?, ? , ? , ?  ) ";
        PreparedStatement ps = null;


        try {


            con = DriverManager.getConnection(URL, UserName, PassWord);
            System.out.println("Database connection established");
            con.setAutoCommit(false);

            ps = con.prepareStatement(insertMerge);



            // ps.setInt(1, patient_id);
            ps.setString(1, oldMrn);
            ps.setString(2, newMrn);
            ps.setInt(3, oldPID);
            ps.setInt(4, oldPID);
            ps.setInt(5, status);
            ps.setTimestamp(6, modifieddate);


            mergeInsertStatus = ps.executeUpdate();

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {

                    e.printStackTrace();
                }

                ps = null;
            }

            if (con != null) {
                try {
                    con.commit();
                    con.rollback();
                    con.close();
                    System.out.println("Database connection terminated");
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }
        return mergeInsertStatus;

    }

    public static int mrnMergePatientUpdate(String oldMrn, String newMrn) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread mrnMergePatientUpdate = new RunnableThread("mrnMergePatientUpdate");
        mrnMergePatientUpdate.run();

        GetDBCredentials db = new GetDBCredentials();
        UserName = GetDBCredentials.Credentials().getUsername();
        PassWord = GetDBCredentials.Credentials().getPassword();
        SqlHost = GetDBCredentials.Credentials().getSqlhost();
        RSNADB = GetDBCredentials.Credentials().getRsnadb();
        URL = "jdbc:postgresql://" + SqlHost + "/" + RSNADB;
        DBConnection conn = new DBConnection();
        MergeDataType merge = new MergeDataType();
        con = conn.runConnect(SqlHost, RSNADB, UserName, PassWord);
        System.out.println("Database connection established");

        try {
            con.setAutoCommit(true);
            Statement s = con.createStatement();
            String sql = "update patients set mrn ='" + newMrn + "'  where mrn ='" + oldMrn + "'";


            mergeStatus = 0;
            mergeStatus = s.executeUpdate(sql);
            if (mergeStatus == 1) {

                con.commit();
                System.out.println("Row is updated.");
            } else {
                System.out.println("Row is not updated.");
            }

            con.close();


        } catch (SQLException e) {


            e.printStackTrace();

        } finally {

            con.close();
            if (con != null) {
                try {
                    con.close();
                    System.out.println("update operation complete");
                    System.out.println("Database connection terminated");
                } catch (SQLException e) {

                    e.printStackTrace();
                }
            }

        }
        return mergeStatus;
    }

    public static ExamContentView GetFinalizedExams(String status) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread GetTransferContentExamByStatus = new RunnableThread("GetTransferContentExamByStatus");
        GetTransferContentExamByStatus.run();
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
            examContentView = new ExamContentView();

            while (rs.next()) {

                examContentView.setPatient_id(rs.getInt("patient_id"));
                examContentView.setMrn(rs.getString("mrn"));
                examContentView.setPatient_name(rs.getString("mrn"));
                examContentView.setDob(rs.getTimestamp("dob"));
                examContentView.setSex(rs.getString("sex"));
                examContentView.setStreet(rs.getString("street"));
                examContentView.setCity(rs.getString("city"));
                examContentView.setState(rs.getString("state"));
                examContentView.setZip_code(rs.getString("zip_code"));
                examContentView.setExam_id(rs.getInt("exam_id"));
                examContentView.setAccession_number(rs.getString("accession_number"));
                examContentView.setExam_description(rs.getString("exam_description"));
                examContentView.setReport_id(rs.getString("report_id"));
                examContentView.setStatus(rs.getString("status"));
                examContentView.setStatus_timestamp(rs.getTimestamp("status_timestamp"));
                examContentView.setReport_text(rs.getString("report_text"));
                examContentView.setDictator(rs.getString("dictator"));
                examContentView.setTranscriber(rs.getString("transcriber"));
                examContentView.setSigner(rs.getString("signer"));

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
        return examContentView;
    }

    public static ArrayList getReadyExamID(int jobID) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread getReadyExamID = new RunnableThread("getReadyExamID");
        getReadyExamID.run();
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
            s.executeQuery("SELECT exam_id from jobs where job_id = '" + jobID + "' ");
            ResultSet rs = s.getResultSet();
            examList = new ArrayList<String>();

            while (rs.next()) {

                examReady = rs.getInt(1);
                examReadyString = Integer.toString(examReady);
                examList.add(examReadyString);



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
        return examList;
    }

    public static ArrayList getReadyAccessNum(int examID) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread getReadyAccessNum = new RunnableThread("getReadyAccessNum");
        getReadyAccessNum.run();
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
            s.executeQuery("SELECT accession_number FROM exams  where exam_id='" + examID + "'");
            ResultSet rs = s.getResultSet();
            accessNumList = new ArrayList<String>();

            while (rs.next()) {

                accessReady = rs.getString(1);

                accessNumList.add(accessReady);



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
        return accessNumList;
    }

    public static int getReadyJobID(String accessNum) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread getReadyAccessNum = new RunnableThread("getReadyAccessNum");
        getReadyAccessNum.run();
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
            s.executeQuery("SELECT jobs.job_id FROM  public.exams,  public.jobs WHERE accession_number ='" + accessNum + "'");
            ResultSet rs = s.getResultSet();
            accessNumList = new ArrayList<String>();

            while (rs.next()) {

                jobReady = rs.getInt(1);





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
        return jobReady;
    }

    public static String getReadyMrn(String accessNum) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread getReadyMrn = new RunnableThread("getReadyMrn");
        getReadyMrn.run();
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
            s.executeQuery("SELECT distinct mrn FROM   v_exam_status where accession_number ='" + accessNum + "'");
            ResultSet rs = s.getResultSet();


            while (rs.next()) {
                mrnReady = rs.getString(1);

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
        return mrnReady;
    }

    public static int getReportExamID(String accessNum) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread getRepordExamID = new RunnableThread("getRepordExamID");
        getRepordExamID.run();
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
            s.executeQuery("SELECT distinct  exams.exam_id FROM public.reports,  public.exams WHERE  exams.exam_id = reports.exam_id  and exams.accession_number = '" + accessNum + "'");
            ResultSet rs = s.getResultSet();


            while (rs.next()) {
                examReportID = rs.getInt(1);

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
        return examReportID;
    }

    public static int getExamID(String accessNum) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread getRepordExamID = new RunnableThread("getRepordExamID");
        getRepordExamID.run();
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
            s.executeQuery("SELECT exams.exam_id FROM  public.exams,  public.patients WHERE exams.patient_id = patients.patient_id and accession_number =  '" + accessNum + "'");
            ResultSet rs = s.getResultSet();


            while (rs.next()) {
                existexamID = rs.getInt(1);

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
        return existexamID;
    }

    public static int getExsitingExamID(int examID) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread getExsitingExamID = new RunnableThread("getExsitingExamID");
        getExsitingExamID.run();
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
            s.executeQuery("SELECT distinct reports.exam_id FROM   public.reports where exam_id = '" + examID + "'");
            ResultSet rs = s.getResultSet();


            while (rs.next()) {
                examID = rs.getInt(1);

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
        return examID;
    }

    public static ArrayList getReadyJobs() throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread getReadyJobs = new RunnableThread("getReadyJobs ");
        getReadyJobs.run();
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
            s.executeQuery("SELECT job_id FROM jobs WHERE jobs.job_id NOT IN (SELECT job_id FROM transactions)");
            ResultSet rs = s.getResultSet();
            jobList = new ArrayList<String>();

            while (rs.next()) {

                examReady = rs.getInt(1);
                examReadyString = Integer.toString(examReady);
                jobList.add(examReadyString);



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
        return jobList;
    }

    public static int updateTransaction(int status, int jobid) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread FindPatientID = new RunnableThread("FindPatientID");
        FindPatientID.run();


        GetDBCredentials db = new GetDBCredentials();
        UserName = GetDBCredentials.Credentials().getUsername();
        PassWord = GetDBCredentials.Credentials().getPassword();
        SqlHost = GetDBCredentials.Credentials().getSqlhost();
        RSNADB = GetDBCredentials.Credentials().getRsnadb();
        URL = "jdbc:postgresql://" + SqlHost + "/" + RSNADB;

        DBConnection conn = new DBConnection();
        System.out.println("Database connection established");
        con = conn.runConnect(SqlHost, RSNADB, UserName, PassWord);

        try {



            System.out.println("Database connection established");
            con.setAutoCommit(true);
            Statement s = con.createStatement();
            String sql = "update transactions set status= ='" + status + "'  where job_id='" + jobid + "'";
            tranUp = s.executeUpdate(sql);

            if (tranUp == 1) {
                con.commit();
                System.out.println("Row is updated.");
            } else {
                System.out.println("Row is not updated.");
            }



            s.close(); // close result set
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
        return tranUp;
    }
    private int jobUP;

    public int updateJobDocID(int jobid, String documentID) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread updateJobDocID = new RunnableThread("updateJobDocID");
        updateJobDocID.run();


        GetDBCredentials db = new GetDBCredentials();
        UserName = GetDBCredentials.Credentials().getUsername();
        PassWord = GetDBCredentials.Credentials().getPassword();
        SqlHost = GetDBCredentials.Credentials().getSqlhost();
        RSNADB = GetDBCredentials.Credentials().getRsnadb();
        URL = "jdbc:postgresql://" + SqlHost + "/" + RSNADB;

        DBConnection conn = new DBConnection();
        System.out.println("Database connection established");
        con = conn.runConnect(SqlHost, RSNADB, UserName, PassWord);

        try {

            System.out.println("Database connection established");
            con.setAutoCommit(true);
            Statement s = con.createStatement();
            String sql = "update jobs set document_id= ='" + documentID + "'  where job_id='" + jobid + "'";
            jobUP = s.executeUpdate(sql);

            if (jobUP == 1) {

                con.commit();
                System.out.println("Row is updated.");
            } else {
                System.out.println("Row is not updated.");
            }

            s.close(); // close result set
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
        return jobUP;
    }


     public static RSNAPixMessageDataType getMessageForPix (int patientid) throws SQLException, InterruptedException, FileNotFoundException, IOException, Exception {

        RunnableThread getMessageForPix  = new RunnableThread("getMessageForPix ");
        getMessageForPix.run();
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
            s.executeQuery("SELECT * patient_rsna_ids where patient_id = '" + patientid + "' ");
            ResultSet rs = s.getResultSet();
            pixMsg = new RSNAPixMessageDataType();

            while (rs.next()) {

                pixMsg.setRsna_id(rs.getString("rsna_id"));
                pixMsg.setPatient_id(rs.getInt("patient_id"));
                pixMsg.setModified_date(rs.getTimestamp("modified_date"));
                pixMsg.setPatient_alias(rs.getString("patient_alias"));
                pixMsg.setPatient_alias_lastname(rs.getString("patient_alias_lastname"));
                pixMsg.setPatient_alias_firstname(rs.getString("patient_alias_firstname"));


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
        return  pixMsg;
    }

}






    