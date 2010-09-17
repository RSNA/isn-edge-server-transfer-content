 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.generatedocument;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author erackus
 */
public class SplitDoctorName {

    public static String[] find(String input) {
        String patternStr = "(\\d*)\\^([^\\^]*)\\^([^\\^]*)\\^*";
        String[] comps = new String[4];

        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(input);
        boolean matchFound = matcher.find();
        if (matchFound) {
            for (int i=0; i<=matcher.groupCount(); i++) {
                 comps[i] = matcher.group(i);
            }
        }
        return comps;
    }

}
