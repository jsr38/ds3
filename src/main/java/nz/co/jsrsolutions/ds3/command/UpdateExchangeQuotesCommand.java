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

import java.lang.InterruptedException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import nz.co.jsrsolutions.ds3.provider.EodDataProvider;
import nz.co.jsrsolutions.ds3.sink.EodDataSink;
import nz.co.jsrsolutions.util.EmailService;
import nz.co.jsrsolutions.util.Range;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

public class UpdateExchangeQuotesCommand implements Command {

  private static final transient Logger logger = Logger
      .getLogger(UpdateExchangeQuotesCommand.class);

  private final ExecutorService _executorService;

  public UpdateExchangeQuotesCommand(ExecutorService executorService) {
    _executorService = executorService;
  }

  public boolean execute(Context context) throws Exception {

    logger.info("Executing: updateexchangequotes");

    final EodDataProvider eodDataProvider = (EodDataProvider) context
        .get(CommandContext.EODDATAPROVIDER_KEY);
    final EodDataSink eodDataSink = (EodDataSink) context
        .get(CommandContext.EODDATASINK_KEY);
    final EmailService emailService = (EmailService) context
        .get(CommandContext.EMAILSERVICE_KEY);
    final String exchange = (String) context.get(CommandContext.EXCHANGE_KEY);

    if (exchange == null) {
      throw new CommandException("Must supply --exchange [exchangecode]");
    }

    long nQuotesWritten = 0;

    final int availableMonths = eodDataProvider.getExchangeMonths(exchange);
    // final SYMBOL[] symbols = eodDataProvider.getSymbols(exchange);
    final String[] symbols = eodDataSink.readExchangeSymbols(exchange);

    if (symbols == null) {
      logger.info("No symbols associated with this exchange...");
      return false;
    }

    Collection<Future<Long>> futures = new LinkedList<Future<Long>>();
    
    for (String symbol : symbols) {

      final Calendar firstAvailableDateTime = Calendar.getInstance();

      if (availableMonths > 0) {
        firstAvailableDateTime.add(Calendar.MONTH, -1 * availableMonths);
        firstAvailableDateTime.add(Calendar.DATE, 1);
      }

      final Calendar today = Calendar.getInstance();

      final Range<Calendar> sinkRange = eodDataSink
          .readExchangeSymbolDateRange(exchange, symbol);

      final ArrayList<Range<Calendar>> requestRangesList = new ArrayList<Range<Calendar>>(
          2);

      if (sinkRange != null) {

        if (firstAvailableDateTime.compareTo(sinkRange.getLower()) < 0) {

          final Calendar upper = (Calendar) sinkRange.getLower().clone();
          upper.add(Calendar.DATE, -1);

          requestRangesList.add(new Range<Calendar>(firstAvailableDateTime,
              upper));
        }

        if (today.compareTo(sinkRange.getUpper()) > 0) {

          final Calendar lower = (Calendar) sinkRange.getUpper().clone();
          lower.add(Calendar.DATE, 1);
          // TODO: fix this by observing timezones
          final Calendar upper = (Calendar) today.clone();
          upper.add(Calendar.DATE, 1);

          requestRangesList.add(new Range<Calendar>(lower, upper));

        }

      } else {
        requestRangesList
            .add(new Range<Calendar>(firstAvailableDateTime, today));
      }

      for (Range<Calendar> requestRange : requestRangesList) {

        if (logger.isInfoEnabled()) {

          StringBuffer logMessageBuffer = new StringBuffer();
          logMessageBuffer.setLength(0);
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

        try {
          
          futures.add(_executorService.submit(new ReadWriteQuotesTask(eodDataProvider,
              eodDataSink, exchange, symbol, requestRange.getLower(),
              requestRange.getUpper())));
 
        } catch (Exception e) {
          logger.error("Task submission failed", e);
        }

      }

    }

    for (Future<Long> future : futures) {
      try {
        nQuotesWritten += future.get();
      } catch (ExecutionException e) {
        logger.error("Execution exception: ", e);
      } catch (InterruptedException e) {
        logger.error("Interrupted exception: ", e);
      }
    }
    
    if (emailService != null) {
      final StringBuffer subjectBuffer = new StringBuffer();
      subjectBuffer.append("Updated quotes on ");
      subjectBuffer.append(exchange);

      final StringBuffer messageBuffer = new StringBuffer();
      messageBuffer.append("Wrote ");
      messageBuffer.append(nQuotesWritten);
      messageBuffer.append(" quotes over ");
      messageBuffer.append(symbols.length);
      messageBuffer.append(" symbols ");

      emailService.send(subjectBuffer.toString(), messageBuffer.toString());
    }

    return false;

  }
}
