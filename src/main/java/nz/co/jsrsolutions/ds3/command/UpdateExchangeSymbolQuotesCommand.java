/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)UpdateExchangeSymbolQuotesCommand.java        
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

import java.util.ArrayList;
import java.util.Calendar;

import nz.co.jsrsolutions.ds3.DataStub.QUOTE;
import nz.co.jsrsolutions.ds3.provider.EodDataProvider;
import nz.co.jsrsolutions.ds3.sink.EodDataSink;
import nz.co.jsrsolutions.util.Range;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

public class UpdateExchangeSymbolQuotesCommand implements Command {

  private static final transient Logger logger = Logger.getLogger(UpdateExchangeSymbolQuotesCommand.class);

  private static final String DEFAULT_FREQUENCY = new String("d");

  public boolean execute(Context context) throws Exception {

    logger.info("Executing: updateexchangesymbolquotes");

    EodDataProvider eodDataProvider = (EodDataProvider)context.get(CommandContext.EODDATAPROVIDER_KEY);
    EodDataSink eodDataSink = (EodDataSink)context.get(CommandContext.EODDATASINK_KEY);
    String exchange = (String)context.get(CommandContext.EXCHANGE_KEY);
    String symbol = (String)context.get(CommandContext.SYMBOL_KEY);

    if (exchange == null) {
      throw new CommandException("Must supply --exchange [exchangecode]");
    }

    if (symbol == null) {
      throw new CommandException("Must supply --symbol [symbolcode]");
    }

    final int availableMonths = eodDataProvider.getExchangeMonths(exchange);  
    final Calendar firstAvailableDateTime = Calendar.getInstance();
    
    if (availableMonths > 0) {
      firstAvailableDateTime.add(Calendar.MONTH, -1 * availableMonths);
      firstAvailableDateTime.add(Calendar.DATE, 1);
    }

    final Calendar today = Calendar.getInstance();
    final Range<Calendar> sinkRange = eodDataSink.readExchangeSymbolDateRange(exchange, symbol);
    final ArrayList<Range<Calendar>> requestRangesList = new ArrayList<Range<Calendar>>(2);
    
    if (sinkRange != null) {
      
      if (firstAvailableDateTime.compareTo(sinkRange.getLower()) < 0) {
        
        final Calendar upper = (Calendar)sinkRange.getLower().clone();
        upper.add(Calendar.DATE, -1);
        
        requestRangesList.add(new Range<Calendar>(firstAvailableDateTime, upper));
      }
      
      if (today.compareTo(sinkRange.getUpper()) > 0) {
        
        final Calendar lower = (Calendar)sinkRange.getUpper().clone();
        lower.add(Calendar.DATE, 1);
        
        requestRangesList.add(new Range<Calendar>(lower, today));
        
      }
      
    }
    else {
      requestRangesList.add(new Range<Calendar>(firstAvailableDateTime, today));
    }
    
    for(Range<Calendar> requestRange : requestRangesList) {
      
      if (logger.isInfoEnabled()) {

        final StringBuffer logMessageBuffer = new StringBuffer();
        logMessageBuffer.append(" Attempting to retrieve quotes on [ ");
        logMessageBuffer.append(exchange);
        logMessageBuffer.append(" ] for [ ");
        logMessageBuffer.append(symbol);
        logMessageBuffer.append(" ] between [ ");
        logMessageBuffer.append(requestRange.getLower().getTime().toString());
        logMessageBuffer.append(" ] and [ ");
        logMessageBuffer.append(requestRange.getUpper().getTime().toString());
        logMessageBuffer.append(" ] ");
        logger.info(logMessageBuffer.toString());

      }
      
      final QUOTE[] quotes = eodDataProvider.getQuotes(exchange,
          symbol,
          requestRange.getLower(),
          requestRange.getUpper(),
          DEFAULT_FREQUENCY);

      if (quotes == null || quotes.length == 0) {
        logger.info("Quote array from provider was empty!");
        return false;
      }
      
      eodDataSink.updateExchangeSymbolQuotes(exchange,
          symbol,
          quotes);
      
    }


    return false;

  }
}

