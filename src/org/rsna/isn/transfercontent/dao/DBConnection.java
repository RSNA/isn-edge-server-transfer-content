/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsna.isn.transfercontent.dao;

import java.sql.*;

public class DBConnection {

    private static String url;
    private static Connection conMe;

    public  Connection  runConnect(String host, String database, String user, String password) throws Exception {

        try {

         driverTest();
         conMe = makeCon(host, database, user, password);

        } catch (java.sql.SQLException e) {
           e.printStackTrace();
        }
        
        return conMe ;




    }

    protected static void driverTest() throws Exception {

        try {
            Class.forName("org.postgresql.Driver").newInstance();
            System.out.println("MySQL Driver Found");
        } catch (java.lang.ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found ... ");
            throw (e);
        }
    }

    
    protected static Connection makeCon(String host, String database, String user, String password) throws Exception {

        

        try {
            url = "jdbc:postgresql://" + host + "/" + database;
            Connection con = DriverManager.getConnection(url, user, password);
            System.out.println("Connection established to " + url + "...");
            return con;
        } catch (java.sql.SQLException e) {
            System.out.println("Connection couldn't be established to " + url);
            throw (e);
        }
    }
}
