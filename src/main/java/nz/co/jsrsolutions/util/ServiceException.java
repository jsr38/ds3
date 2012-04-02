/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)ServiceException.java        
 *
 * Copyright (c) 2012 JSR Solutions Limited
 * 4 Viridian Lane, Auckland, 0632.  New Zealand
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of JSR
 * Solutions Limited. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with JSR Solutions Limited.
 */

package nz.co.jsrsolutions.util;


import java.lang.String;

class ServiceException extends Exception {
  /**
   * 
   */
  private static final long serialVersionUID = 8938865020947322939L;
  
  private String error;

  public ServiceException() {
    super();
    error = "unknown";
  }

  public ServiceException(String err) {
    super(err);
    error = err;
  }
  
  public ServiceException(Throwable cause) {
    super(cause);
  }  

  public String getError() {
    return error;
  }
}
  