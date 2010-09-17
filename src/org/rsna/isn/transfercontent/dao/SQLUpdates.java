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

    public static synchronized int UpdateJobDocumentID(int jobID, String documentID) throws InterruptedException, SQLException, FileNotFoundException, IOException, Exception {
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
            String sql = "update jobs set document_id='" + documentID +  "'  where job_id='" + jobID + "'";
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

    public static synchronized int UpdateTransactionStatus(int jobID, int status) throws InterruptedException, SQLException, FileNotFoundException, IOException, Exception {
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
            String sql = "update transactions set status='" + status +  "'  where job_id='" + jobID + "'";
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
