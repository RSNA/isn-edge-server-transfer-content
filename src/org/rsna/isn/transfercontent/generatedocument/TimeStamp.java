/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.generatedocument;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author foyesanya
 */
public class TimeStamp {

    private static String timeStamp;

    public  String Date() throws InterruptedException {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date date = new java.util.Date();
        timeStamp = dateFormat.format(date).toString();

        return timeStamp;
    }
}
