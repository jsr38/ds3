/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)QuartzJobBeanCommandProxy.java        
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

import java.util.concurrent.ExecutorService;

import nz.co.jsrsolutions.ds3.sink.EodDataSink;
import nz.co.jsrsolutions.ds3.provider.EodDataProvider;
import nz.co.jsrsolutions.util.EmailService;

import org.apache.commons.chain.Command;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class QuartzJobBeanCommandProxy extends QuartzJobBean {

  private String command;
  
  private String exchange;
  
  private EodDataSink eodDataSink;
  
  private EodDataProvider eodDataProvider;
  
  private EmailService emailService;
  
  private ExecutorService _executorService;
  
  public void setExecutorService(ExecutorService executorService) {
    _executorService = executorService;
  }
  
  public void setEmailService(EmailService emailService) {
    this.emailService = emailService;
  }

  public void setEodDataSink(EodDataSink eodDataSink) {
    this.eodDataSink = eodDataSink;
  }

  public void setEodDataProvider(EodDataProvider eodDataProvider) {
    this.eodDataProvider = eodDataProvider;
  }

  public void setCommand(String command) {
    this.command = command;
  }
  
  public void setExchange(String exchange) {
    this.exchange = exchange;
  }

  protected void executeInternal(JobExecutionContext ctx)
      throws JobExecutionException {

    if (command == null) {
      throw new JobExecutionException("No command supplied.");
    }

    CommandContext context = new CommandContext();
    context.put(CommandContext.EODDATAPROVIDER_KEY, eodDataProvider);
    context.put(CommandContext.EODDATASINK_KEY, eodDataSink);
    context.put(CommandContext.EMAILSERVICE_KEY, emailService);

    context.put(CommandContext.EXCHANGE_KEY, exchange);

    try {
      Command cmd = CommandFactory.create(command, _executorService);
      cmd.execute(context);
    }
    catch (Exception e) {
      throw new JobExecutionException(e);
    }

  }
}