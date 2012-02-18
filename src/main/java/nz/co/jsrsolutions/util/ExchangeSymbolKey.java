/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)ExchangeSymbolKey.java        
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


public final class ExchangeSymbolKey {

  private final String key;
  
  public ExchangeSymbolKey(final String exchange, final String symbol) {
    
    final StringBuffer symbolKeyBuffer = new StringBuffer();
    symbolKeyBuffer.append(exchange);
    symbolKeyBuffer.append("-");
    symbolKeyBuffer.append(symbol);
    
    key = symbolKeyBuffer.toString();
  
  }

  public String getKey() {
    return key;
  }
  
}

