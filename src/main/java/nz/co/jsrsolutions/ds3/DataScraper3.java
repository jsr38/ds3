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

import java.lang.String;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;


final class DataScraper3 {

  private static final transient Logger logger = Logger.getLogger(DataScraper3.class);

  private static final CombinedConfiguration config = new CombinedConfiguration();

  private static final String APPLICATION_CONFIG_ROOT = new String("applicationConfig.ds3Config");

  private DataScraper3() {

  }

  public static void main(String[] args) {

    logger.info("Starting application [ ds3 ] ..."); 

    try {

      CommandLineParser parser = new GnuParser();

      CommandLine commandLine = parser.parse(CommandLineOptions.Options, args);

      if (commandLine.getOptions().length > 0 && !commandLine.hasOption(CommandLineOptions.HELP)) {

        config.addConfiguration(new XMLConfiguration(commandLine.getOptionValue(CommandLineOptions.CONFIGFILE)));

        HierarchicalConfiguration appConfig = config.configurationAt(APPLICATION_CONFIG_ROOT);

        EodDataProvider eodDataProvider = null;
        EodDataSink eodDataSink = null;

        try {

          eodDataProvider = EodDataProviderFactory.create(appConfig, commandLine.getOptionValue(CommandLineOptions.PROVIDER));
          eodDataSink = EodDataSinkFactory.create(appConfig, commandLine.getOptionValue(CommandLineOptions.SINK));

          Context context = new DataScraper3Context();
          context.put(DataScraper3Context.EODDATAPROVIDER_KEY, eodDataProvider);
          context.put(DataScraper3Context.EODDATASINK_KEY, eodDataSink);

          // place optional argument values into the context
          if (commandLine.hasOption(CommandLineOptions.EXCHANGE)) {
            context.put(DataScraper3Context.EXCHANGE_KEY, commandLine.getOptionValue(CommandLineOptions.EXCHANGE));
          }

          if (commandLine.hasOption(CommandLineOptions.SYMBOL)) {
            context.put(DataScraper3Context.SYMBOL_KEY, commandLine.getOptionValue(CommandLineOptions.SYMBOL));
          }

          Command command = DataScraper3CommandFactory.create(appConfig, commandLine.getOptionValue(CommandLineOptions.COMMAND));
          command.execute(context);

        }
        catch(EodDataProviderException edpe) {

          logger.error(edpe.toString());
          edpe.printStackTrace();

        }
        catch(EodDataSinkException edse) {

          logger.error(edse.toString()); 
          edse.printStackTrace();

        }
        finally {

          if (eodDataProvider != null) {

            //eodDataProvider.close();

          }
          
          if (eodDataSink != null) {

            eodDataSink.close();

          }
        }

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
    catch(ConfigurationException ce) {

      logger.error(ce.toString()); 
      ce.printStackTrace();

    }
    catch(Exception e) {

      logger.error(e.toString()); 
      e.printStackTrace();

    }

    logger.info("Exiting application [ ds3 ] ...");

  }


}

