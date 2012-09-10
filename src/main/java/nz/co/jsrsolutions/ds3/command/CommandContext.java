/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)CommandContext.java        
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


package nz.co.jsrsolutions.ds3.command;

import nz.co.jsrsolutions.ds3.sink.EodDataSink;
import nz.co.jsrsolutions.ds3.provider.EodDataProvider;
import nz.co.jsrsolutions.util.EmailService;

import org.apache.commons.chain.impl.ContextBase;


public class CommandContext extends ContextBase {

  /**
  	 * 
  	 */
  private static final long serialVersionUID = 4691923958838858461L;

  public static final String EODDATAPROVIDER_KEY = new String("eoddataprovider");

  public static final String EODDATASINK_KEY = new String("eoddatasink");

  public static final String EMAILSERVICE_KEY = new String("emailservice");

  public static final String EXCHANGE_KEY = new String("exchange");

  public static final String SYMBOL_KEY = new String("symbol");

  private EodDataProvider eodDataProvider;

  private EodDataSink eodDataSink;

  private EmailService emailService;

  public EodDataProvider getEodDataProvider() {
    return eodDataProvider;
  }

  public void setEodDataProvider(EodDataProvider eodDataProvider) {
    this.eodDataProvider = eodDataProvider;
  }

  public EodDataSink getEodDataSink() {
    return eodDataSink;
  }

  public void setEodDataSink(EodDataSink eodDataSink) {
    this.eodDataSink = eodDataSink;
  }

  public EmailService getEmailService() {
    return emailService;
  }

  public void setEmailService(EmailService emailService) {
    this.emailService = emailService;
  }

}