/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)CommandLineOptions.java        
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

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;


final class CommandLineOptions {

  private static final Logger logger = Logger.getLogger(CommandLineOptions.class);

  public static final String HELP = new String("help");

  public static final String ENVIRONMENT = new String("environment");

  public static final String COMMAND = new String("command");

  public static final String EXCHANGE = new String("exchange");

  public static final String SYMBOL = new String("symbol");

  public static final Options Options = new Options();

  static {
    
    Option help = OptionBuilder
      .withDescription("display help")
      .create(HELP);

    Option environment = OptionBuilder
      .withArgName("env")
      .hasArg()
      .isRequired(true)
      .withDescription("specify environment {dev,qa,prod}")
      .create(ENVIRONMENT);

    Option command = OptionBuilder
      .withArgName("command")
      .hasArg()
      .withDescription("specify command to execute")
      .create(COMMAND);

    Option exchange = OptionBuilder
      .withArgName("exchange")
      .hasArg()
      .isRequired(false)
      .withDescription("specify exchange")
      .create(EXCHANGE);
    
    Option symbol = OptionBuilder
      .withArgName("symbol")
      .hasArg()
      .isRequired(false)
      .withDescription("specify symbol")
      .create(SYMBOL);

    logger.debug("Registering command line options:");
    logger.debug(help.toString());
    logger.debug(environment.toString());
    logger.debug(command.toString());
    logger.debug(exchange.toString());
    logger.debug(symbol.toString());
        
    Options.addOption(help);
    Options.addOption(environment);
    Options.addOption(command);
    Options.addOption(exchange);
    Options.addOption(symbol);

  }

  private CommandLineOptions() {

  }

}