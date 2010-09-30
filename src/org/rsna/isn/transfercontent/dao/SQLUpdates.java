/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.dao;

/**
 *
 * @author erackus
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;

public class SQLUpdates {

    public SQLUpdates() {

    }

    private static Connection con = null;
    private static String UserName = null;
    private static String PassWord = null;
    private static String SqlHost = null;
    private static String URL = null;
    private static String RSNADB = null;
    private static Configuration config;
    private static int updateStatus;

    public static synchronized int UpdateJobDocumentID(int examID, String documentID) throws InterruptedException, SQLException, FileNotFoundException, IOException, Exception {
        RunnableThread insertPatientTable = new RunnableThread("UpdateStatusinJobTable");
        insertPatientTable.run();

        GetDBCredentials db = new GetDBCredentials();
        UserName = GetDBCredentials.Credentials().getUsername();
        PassWord = GetDBCredentials.Credentials().getPassword();
        SqlHost = GetDBCredentials.Credentials().getSqlhost();
        RSNADB = GetDBCredentials.Credentials().getRsnadb();
        URL = "jdbc:postgresql://" + SqlHost + "/" + RSNADB;

        DBConnection conn = new DBConnection();
        con = conn.runConnect(SqlHost, RSNADB, UserName, PassWord);
        System.out.println("Database connection established");
        try {

            System.out.println("Database connection established");
            con.setAutoCommit(true);
            Statement s = con.createStatement();
            String sql = "update jobs set document_id='" + documentID +  "'  where exam_id='" + examID + "'";
            updateStatus = s.executeUpdate(sql);

              if (updateStatus  == 1) {

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
                    System.out.println("JOBS: Upate DocumentID operation complete");
                    System.out.println("Database connection terminated");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return updateStatus;
    }

    public static synchronized int UpdateTransactionStatus(int jobID, int status, String statusMessage, java.sql.Timestamp modifiedDate) throws InterruptedException, SQLException, FileNotFoundException, IOException, Exception {
        RunnableThread insertPatientTable = new RunnableThread("UpdateTransactionStatus");
        insertPatientTable.run();

        GetDBCredentials db = new GetDBCredentials();
        UserName = GetDBCredentials.Credentials().getUsername();
        PassWord = GetDBCredentials.Credentials().getPassword();
        SqlHost = GetDBCredentials.Credentials().getSqlhost();
        RSNADB = GetDBCredentials.Credentials().getRsnadb();
        URL = "jdbc:postgresql://" + SqlHost + "/" + RSNADB;

        DBConnection conn = new DBConnection();
        con = conn.runConnect(SqlHost, RSNADB, UserName, PassWord);
        System.out.println("Database connection established");
        PreparedStatement ps = null;

        try {

            System.out.println("Database connection established");
            con.setAutoCommit(true);
            String sql = "insert into transactions (job_id, status, status_message, modified_date) VALUES (?,?,?,?)";
            ps = con.prepareStatement(sql);
            ps.setInt(1, jobID);
            ps.setInt(2, status);
            ps.setString(3, statusMessage);
            ps.setTimestamp(4, modifiedDate);
            updateStatus = ps.executeUpdate();

            if (updateStatus  == 1) {
                con.commit();
                System.out.println("Row is inserted.");
            } else {
                System.out.println("Row is not inserted.");
            }

             ps.close(); // close result set
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            con.close();
            if (con != null) {
                try {
                    con.close();
                    System.out.println("TRANSACTIONS: update status operation complete");
                    System.out.println("Database connection terminated");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return updateStatus;
    }

    public static synchronized int InsertStudyRow(String studyUID, int examID, String studyDesc, java.sql.Timestamp studyDate, java.sql.Timestamp modifiedDate) throws InterruptedException, SQLException, FileNotFoundException, IOException, Exception {
        RunnableThread insertPatientTable = new RunnableThread("UpdateTransactionStatus");
        insertPatientTable.run();

        GetDBCredentials db = new GetDBCredentials();
        UserName = GetDBCredentials.Credentials().getUsername();
        PassWord = GetDBCredentials.Credentials().getPassword();
        SqlHost = GetDBCredentials.Credentials().getSqlhost();
        RSNADB = GetDBCredentials.Credentials().getRsnadb();
        URL = "jdbc:postgresql://" + SqlHost + "/" + RSNADB;

        DBConnection conn = new DBConnection();
        con = conn.runConnect(SqlHost, RSNADB, UserName, PassWord);
        System.out.println("Database connection established");
        PreparedStatement ps = null;
        try {

            System.out.println("Database connection established");
            con.setAutoCommit(true);
            String sql = "insert into studies (study_uid, exam_id, study_description, study_date, modified_date) VALUES (?,?,?,?,?)";
            ps = con.prepareStatement(sql);
            ps.setString(1, studyUID);
            ps.setInt(2, examID);
            ps.setString(3, studyDesc);
            ps.setTimestamp(4, studyDate);
            ps.setTimestamp(5, modifiedDate);
            updateStatus = ps.executeUpdate();

            if (updateStatus  == 1) {
                con.commit();
                System.out.println("Row is inserted.");
            } else {
                System.out.println("Row is not inserted.");
            }

            ps.close(); // close result set
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            con.close();
            if (con != null) {
                try {
                    con.close();
                    System.out.println("TRANSACTIONS: update status operation complete");
                    System.out.println("Database connection terminated");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return updateStatus;
    }
}
