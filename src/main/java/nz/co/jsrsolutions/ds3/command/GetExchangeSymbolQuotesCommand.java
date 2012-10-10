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

import java.util.Calendar;

import nz.co.jsrsolutions.ds3.DataStub.QUOTE;
import nz.co.jsrsolutions.ds3.provider.EodDataProvider;
import nz.co.jsrsolutions.ds3.sink.EodDataSink;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

public class GetExchangeSymbolQuotesCommand implements Command {

  private static final String DEFAULT_FREQUENCY = new String("d");

  private static final transient Logger logger = Logger
      .getLogger(GetExchangeSymbolQuotesCommand.class);

  public GetExchangeSymbolQuotesCommand() {
    
  }

  public boolean execute(Context context) throws Exception {

    logger.info("Executing: getexchangesymbolquotes");

    EodDataProvider eodDataProvider = (EodDataProvider) context
        .get(CommandContext.EODDATAPROVIDER_KEY);
    EodDataSink eodDataSink = (EodDataSink) context
        .get(CommandContext.EODDATASINK_KEY);
    String exchange = (String) context.get(CommandContext.EXCHANGE_KEY);
    String symbol = (String) context.get(CommandContext.SYMBOL_KEY);

    if (exchange == null) {
      throw new CommandException("Must supply --exchange [exchangecode]");
    }

    if (symbol == null) {
      throw new CommandException("Must supply --symbol [symbolcode]");
    }

    long nQuotesWritten = 0;

    final int availableMonths = eodDataProvider.getExchangeMonths(exchange);
    // final SYMBOL[] symbols = eodDataProvider.getSymbols(exchange);
    final String[] symbols = eodDataSink.readExchangeSymbols(exchange);

    if (symbols == null) {
      logger.info("No symbols associated with this exchange...");
      return false;
    }

    final Calendar firstAvailableDateTime = Calendar.getInstance();

    if (availableMonths > 0) {
      firstAvailableDateTime.add(Calendar.MONTH, -1 * 1);
      firstAvailableDateTime.add(Calendar.DATE, 1);
    }

    final Calendar plusOneDay = (Calendar) firstAvailableDateTime.clone();
    plusOneDay.add(Calendar.DAY_OF_MONTH, 1);

    if (logger.isInfoEnabled()) {

      StringBuffer logMessageBuffer = new StringBuffer();
      logMessageBuffer.setLength(0);
      logMessageBuffer.append(" Attempting to retrieve quotes on [ ");
      logMessageBuffer.append(exchange);
      logMessageBuffer.append(" ] for [ ");
      logMessageBuffer.append(symbol);
      logMessageBuffer.append(" ] between [ ");
      logMessageBuffer.append(firstAvailableDateTime.getTime().toString());
      logMessageBuffer.append(" ] and [ ");
      logMessageBuffer.append(plusOneDay.getTime().toString());
      logMessageBuffer.append(" ] ");
      logger.info(logMessageBuffer.toString());

    }

    final QUOTE[] quotes = eodDataProvider.getQuotes(exchange, symbol,
        firstAvailableDateTime, plusOneDay, DEFAULT_FREQUENCY);

    if (quotes == null || quotes.length == 0) {
      logger.info("Quote array from provider was empty!");
      return false;
    }
    
    for (QUOTE quote : quotes) {
      StringBuffer logMessageBuffer = new StringBuffer();
      logMessageBuffer.append("Quote date: [ ");
      logMessageBuffer.append(quote.getDateTime().getTime().toString());
      logMessageBuffer.append(" ]");
      logger.info(logMessageBuffer.toString());
      
    }

    return false;

  }
}
