/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.dao;

/**
 *
 * @author oyesanyf
 */
public class DBCredentials {

    public DBCredentials() {

    }


    private String username;

    public DBCredentials(String username, String password, String sqlhost, String rsnadb, String url) {
        this.username = username;
        this.password = password;
        this.sqlhost = sqlhost;
        this.rsnadb = rsnadb;
       
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRsnadb() {
        return rsnadb;
    }

    public void setRsnadb(String rsnadb) {
        this.rsnadb = rsnadb;
    }

    public String getSqlhost() {
        return sqlhost;
    }

    public void setSqlhost(String sqlhost) {
        this.sqlhost = sqlhost;
    }

    

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    private String password ;
    private String sqlhost ;
    private String rsnadb;
    private String url ;

}
