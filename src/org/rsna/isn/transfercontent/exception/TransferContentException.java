/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.exception;

/**
 *
 * @author erackus
 */
public class TransferContentException extends ChainedException{

    public TransferContentException(String msg) {
        super(msg);
    }

    public TransferContentException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
