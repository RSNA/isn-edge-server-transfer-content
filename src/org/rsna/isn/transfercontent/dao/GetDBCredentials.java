/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rsna.isn.transfercontent.dao;

import java.io.FileInputStream;
import java.util.Properties;

/**
 *
 * @author oyesanyf
 */
public class GetDBCredentials {

    private static DBCredentials db;

    public static DBCredentials Credentials() throws Exception {

        RunnableThread GetCredentials = new RunnableThread("GetCredentials");
        GetCredentials.run();


        try {

            Properties props = new Properties();
            props.load(new FileInputStream("/rsna/properties/rsna.properties"));
            db = new DBCredentials();
            db.setUsername(props.getProperty("postgresuser"));
            db.setPassword(props.getProperty("postgrespassword"));
            db.setSqlhost(props.getProperty("postgreshost"));
            db.setRsnadb(props.getProperty("rsnadbpostgres"));
           

        } catch (Exception e) {
            e.printStackTrace();
        }
       

        return db;
    }
}
