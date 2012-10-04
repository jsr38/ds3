/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)DataScraper3Controller.java        
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

import java.util.concurrent.ExecutorService;

import nz.co.jsrsolutions.ds3.command.CommandContext;
import nz.co.jsrsolutions.ds3.command.CommandFactory;
import nz.co.jsrsolutions.ds3.provider.EodDataProvider;
import nz.co.jsrsolutions.ds3.sink.EodDataSink;
import nz.co.jsrsolutions.util.EmailService;

import org.apache.commons.chain.Command;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(objectName = "bean:name=controller", description = "ds3 controller MBean", log = true, logFile = "jmx.log", currencyTimeLimit = 15, persistPolicy = "OnUpdate", persistPeriod = 200, persistLocation = "foo", persistName = "bar")
final public class DataScraper3Controller implements ApplicationContextAware {

  @SuppressWarnings("unused")
  private static final transient Logger logger = Logger
      .getLogger(DataScraper3Controller.class);

//  private static final String THREADPOOL_BEAN_ID = new String("threadPoolExecutor");
  
  private EodDataProvider _eodDataProvider;

  private EodDataSink _eodDataSink;

  private EmailService _emailService;

  private ApplicationContext _appContext;

  private ExecutorService _executorService;
  
  public DataScraper3Controller(EodDataProvider eodDataProvider,
      EodDataSink eodDataSink, EmailService emailService, ExecutorService executorService) {

    _eodDataProvider = eodDataProvider;
    _eodDataSink = eodDataSink;
    _emailService = emailService;
    _executorService = executorService;
  }

  public void executeCommandLine(CommandLine commandLine)
      throws DataScraper3Exception {

    if (!commandLine.hasOption(CommandLineOptions.COMMAND)) {
      throw new DataScraper3Exception("No command supplied.");
    }

    CommandContext context = new CommandContext();
    context.put(CommandContext.EODDATAPROVIDER_KEY, _eodDataProvider);
    context.put(CommandContext.EODDATASINK_KEY, _eodDataSink);
    context.put(CommandContext.EMAILSERVICE_KEY, _emailService);
    //context.put(CommandContext.)
    // place optional argument values into the context
    if (commandLine.hasOption(CommandLineOptions.EXCHANGE)) {
      context.put(CommandContext.EXCHANGE_KEY,
          commandLine.getOptionValue(CommandLineOptions.EXCHANGE));
    }

    if (commandLine.hasOption(CommandLineOptions.SYMBOL)) {
      context.put(CommandContext.SYMBOL_KEY,
          commandLine.getOptionValue(CommandLineOptions.SYMBOL));
    }

    try {
      // (ThreadPoolExecutor)_appContext.getBean(THREADPOOL_BEAN_ID)
      Command command = CommandFactory.create(commandLine
          .getOptionValue(CommandLineOptions.COMMAND), _executorService);
      command.execute(context);
    } catch (Exception e) {
      throw new DataScraper3Exception(e);
    }

  }

  @ManagedOperation(description = "Kill the service")
  public void shutdown() {

    ((ConfigurableApplicationContext) this._appContext).close();
    System.exit(0);
  }

  @Override
  public void setApplicationContext(ApplicationContext ctx)
      throws BeansException {

    this._appContext = ctx;

  }

}
