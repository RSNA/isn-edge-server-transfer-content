/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.exception;

/**
 *
 * @author erackus
 */
public class WhoCalled {
    private static String methodName;

    static String GetName() {
    // Generate an exception to fill in the stack trace
    try {
        throw new Exception();
    } catch (Exception e) {
      for(StackTraceElement ste : e.getStackTrace())
        methodName = ste.getMethodName();
    }
    return methodName;
  }

}
