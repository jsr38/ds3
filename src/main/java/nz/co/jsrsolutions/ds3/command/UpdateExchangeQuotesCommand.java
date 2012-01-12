/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)UpdateExchangeQuotesCommand.java        
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

import java.lang.String;
import java.util.Calendar;

import nz.co.jsrsolutions.ds3.EodDataProvider;
import nz.co.jsrsolutions.ds3.EodDataSink;
import nz.co.jsrsolutions.ds3.DataStub.QUOTE;
import nz.co.jsrsolutions.ds3.DataStub.SYMBOL;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

public class UpdateExchangeQuotesCommand implements Command {

  private static final transient Logger logger = Logger.getLogger(UpdateExchangeQuotesCommand.class);

  private static final int HISTORY_YEAR_OFFSET = -3;

  private static final int HISTORY_MONTH_OFFSET = -3;

  private static final String DEFAULT_FREQUENCY = new String("d");

  public boolean execute(Context context) throws Exception {

    logger.info("Executing: updateexchangequotes");

    EodDataProvider eodDataProvider = (EodDataProvider)context.get(CommandContext.EODDATAPROVIDER_KEY);
    EodDataSink eodDataSink = (EodDataSink)context.get(CommandContext.EODDATASINK_KEY);
    String exchange = (String)context.get(CommandContext.EXCHANGE_KEY);

    if (exchange == null) {
      throw new CommandException("Must supply --exchange [exchangecode]");
    }

    Calendar startCalendar = Calendar.getInstance();
    startCalendar.add(Calendar.YEAR, HISTORY_YEAR_OFFSET);
    //    startCalendar.add(Calendar.MONTH, HISTORY_MONTH_OFFSET);

    Calendar endCalendar = Calendar.getInstance();
    

    if (logger.isInfoEnabled()) {

      StringBuffer logMessageBuffer = new StringBuffer();
      logMessageBuffer.append(" Attempting to retrieve quotes on [ ");
      logMessageBuffer.append(exchange);
      logMessageBuffer.append(" ] between [ ");
      logMessageBuffer.append(startCalendar.getTime());
      logMessageBuffer.append(" ] and [ ");
      logMessageBuffer.append(endCalendar.getTime());
      logMessageBuffer.append(" ] ");
      logger.info(logMessageBuffer.toString());

    }

    
    SYMBOL[] symbols = eodDataProvider.getSymbols(exchange);

    for (SYMBOL symbol : symbols) {

      if (logger.isInfoEnabled()) {

        StringBuffer logMessageBuffer = new StringBuffer();
        logMessageBuffer.append(" Attempting to retrieve quotes for symbol [ ");
        logMessageBuffer.append(symbol.getCode());
        logMessageBuffer.append(" ]  ");

        logger.info(logMessageBuffer.toString());

      }

      QUOTE[] quotes = eodDataProvider.getQuotes(exchange,
                                                 symbol.getCode(),
                                                 startCalendar,
                                                 endCalendar,
                                                 DEFAULT_FREQUENCY);

      if (logger.isInfoEnabled()) {
        logger.info("Writing to data sink.");
      }

      eodDataSink.updateExchangeSymbolQuotes(exchange,
                                             symbol.getCode(),
                                             quotes);

    }


    return false;

  }
}

