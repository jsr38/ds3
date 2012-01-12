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

import nz.co.jsrsolutions.ds3.command.CommandContext;
import nz.co.jsrsolutions.ds3.command.CommandFactory;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;


final class DataScraper3Controller {

  @SuppressWarnings("unused")
  private static final transient Logger logger = Logger.getLogger(DataScraper3Controller.class);

  private EodDataProvider eodDataProvider;

  private EodDataSink eodDataSink;

  public DataScraper3Controller(EodDataProvider eodDataProvider,
                                EodDataSink eodDataSink) {

    this.eodDataProvider = eodDataProvider;
    this.eodDataSink = eodDataSink;
  }

  public void executeCommandLine(CommandLine commandLine) throws DataScraper3Exception {

    if (!commandLine.hasOption(CommandLineOptions.COMMAND)) {
      throw new DataScraper3Exception("No command supplied.");
    }

    CommandContext context = new CommandContext();
    context.put(CommandContext.EODDATAPROVIDER_KEY, eodDataProvider);
    context.put(CommandContext.EODDATASINK_KEY, eodDataSink);


    // place optional argument values into the context
    if (commandLine.hasOption(CommandLineOptions.EXCHANGE)) {
      context.put(CommandContext.EXCHANGE_KEY, commandLine.getOptionValue(CommandLineOptions.EXCHANGE));
    }

    if (commandLine.hasOption(CommandLineOptions.SYMBOL)) {
      context.put(CommandContext.SYMBOL_KEY, commandLine.getOptionValue(CommandLineOptions.SYMBOL));
    }

    try {
      Command command = CommandFactory.create(commandLine.getOptionValue(CommandLineOptions.COMMAND));
      command.execute(context);
    }
    catch (Exception e) {
      throw new DataScraper3Exception(e);
    }
    finally {
      eodDataSink.close();
    }




  }

}

