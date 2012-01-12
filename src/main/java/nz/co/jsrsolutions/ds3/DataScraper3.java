/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)DataScraper3.java        
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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


final class DataScraper3 {

  private static final transient Logger logger = Logger.getLogger(DataScraper3.class);

  private static final String SPRING_CONFIG_PREFIX = new String("service.");

  private static final String SPRING_CONFIG_SUFFIX = new String(".xml");

  private DataScraper3() {

  }

  public static void main(String[] args) {

    logger.info("Starting application [ ds3 ] ..."); 

    try {

      CommandLineParser parser = new GnuParser();

      CommandLine commandLine = parser.parse(CommandLineOptions.Options, args);

      if (commandLine.getOptions().length > 0 && !commandLine.hasOption(CommandLineOptions.HELP)) {

        StringBuffer environment = new StringBuffer();
        environment.append(SPRING_CONFIG_PREFIX);
        environment.append(commandLine.getOptionValue(CommandLineOptions.ENVIRONMENT));
        environment.append(SPRING_CONFIG_SUFFIX);

        ApplicationContext context = new ClassPathXmlApplicationContext(environment.toString());

        DataScraper3Controller controller = context.getBean("controller", DataScraper3Controller.class);

        controller.executeCommandLine(commandLine);
      }
      else {

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("ds3", CommandLineOptions.Options);

      }


    }
    catch(ParseException pe) {

      logger.error(pe.toString()); 
      pe.printStackTrace();

    }
    catch(Exception e) {

      logger.error(e.toString()); 
      e.printStackTrace();

    }

    logger.info("Exiting application [ ds3 ] ...");

  }


}

