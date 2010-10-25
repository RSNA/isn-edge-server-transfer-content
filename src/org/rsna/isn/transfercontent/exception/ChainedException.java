/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.exception;

/**
 *
 * @author erackus
 */
public class ChainedException extends Exception{
    private Throwable cause = null;

    public ChainedException() {
        super();
    }

    public ChainedException(String msg) {
        super(msg);
    }

    public ChainedException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }


    @Override
    public void printStackTrace(java.io.PrintStream ps)
    {
        super.printStackTrace(ps);
        if (cause != null) {
            ps.println("Caused by:");
            cause.printStackTrace(ps);
        }
    }

    @Override
    public void printStackTrace(java.io.PrintWriter pw)
    {
        super.printStackTrace(pw);
        if (cause != null) {
            pw.println("Caused by:");
            cause.printStackTrace(pw);
        }
    }
}

