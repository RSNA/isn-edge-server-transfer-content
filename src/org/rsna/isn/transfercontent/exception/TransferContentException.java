/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rsna.isn.transfercontent.exception;

/**
 *
 * @author erackus
 */
public class TransferContentException extends Exception{
  private String className = null;

  public TransferContentException() {
  }

  public TransferContentException(String msg) { 
      super(msg);
  }

  public TransferContentException(String msg, String className) {
      super(msg);
      this.className = className;
  }

}

