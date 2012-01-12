/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)EodDataSinkException.java        
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

package nz.co.jsrsolutions.ds3;


import java.lang.String;

public class EodDataSinkException extends Exception {
  /**
   * 
   */
  private static final long serialVersionUID = 2538235337093051298L;
  
  private String mistake;

  public EodDataSinkException() {
    super();
    mistake = "unknown";
  }
  
  public EodDataSinkException(String err) {
    super(err);
    mistake = err;
  }
  
  public String getError() {
    return mistake;
  }
}
  