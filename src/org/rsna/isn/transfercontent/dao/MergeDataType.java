/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.dao;


/**
 *
 * @author oyesanyf
 */
public class MergeDataType {
     public  MergeDataType() {

    }

    public MergeDataType(String mergeRawData) {
        this.mergeRawData = mergeRawData;
    }

   

    private String oldMrn ;
    private String newMrn;

    

    public String getMergeRawData() {
        return mergeRawData;
    }

    public void setMergeRawData(String mergeRawData) {
        this.mergeRawData = mergeRawData;
    }
    private String mergeRawData;

    public MergeDataType(String oldMrn, String newMrn) {
        this.oldMrn = oldMrn;
        this.newMrn = newMrn;
    }

    public String getNewMrn() {
        return newMrn;
    }

    public void setNewMrn(String newMrn) {
        this.newMrn = newMrn;
    }

    public String getOldMrn() {
        return oldMrn;
    }

    public void setOldMrn(String oldMrn) {
        this.oldMrn = oldMrn;
    }

}
