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
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class UpdateExchangeQuotesCommand implements Command, ApplicationContextAware {

  private static final transient Logger logger = Logger
      .getLogger(UpdateExchangeQuotesCommand.class);

  private final ExecutorService _executorService;
  
  private ApplicationContext _appContext;

  private EodDataSink _eodDataSink;

  private EodDataProvider _eodDataProvider;

  private EmailService _emailService;

  public UpdateExchangeQuotesCommand(ExecutorService executorService,
                                     EodDataProvider eodDataProvider,
                                     EodDataSink eodDataSink,
                                     EmailService emailService) {
    _executorService = executorService;
    _eodDataSink = eodDataSink;
    _eodDataProvider = eodDataProvider;
    _emailService = emailService;
    
  }

  public boolean execute(Context context) throws Exception {

    logger.info("Executing: updateexchangequotes");

    final String exchange = (String) context.get(CommandContext.EXCHANGE_KEY);

    if (exchange == null) {
      throw new CommandException("Must supply --exchange [exchangecode]");
    }

    long nQuotesWritten = 0;

    final int availableMonths = _eodDataProvider.getExchangeMonths(exchange);

    final String[] symbols = _eodDataSink.readExchangeSymbols(exchange);

    if (symbols == null) {
      logger.info("No symbols associated with this exchange...");
      return false;
    }

    final Collection<Future<Long>> futures = new LinkedList<Future<Long>>();
    
    for (String symbol : symbols) {

      final Calendar firstAvailableDateTime = Calendar.getInstance();

      if (availableMonths > 0) {
        firstAvailableDateTime.add(Calendar.MONTH, -1 * availableMonths);
        firstAvailableDateTime.add(Calendar.DATE, 1);
      }

      final Calendar today = Calendar.getInstance();

      final Range<Calendar> sinkRange = _eodDataSink
          .readExchangeSymbolDateRange(exchange, symbol);

      final ArrayList<Range<Calendar>> requestRangesList = new ArrayList<Range<Calendar>>(
          2);

      if (sinkRange != null) {

        if (firstAvailableDateTime.compareTo(sinkRange.getLower()) < 0) {

          // In this implementation we can't ask for this!
          
   /*      
          final Calendar upper = (Calendar) sinkRange.getLower().clone();
          upper.add(Calendar.DATE, -1);
          final Calendar lower = (firstAvailableDateTime.compareTo(upper) < 0) ? firstAvailableDateTime : upper;
          
          requestRangesList.add(new Range<Calendar>(firstAvailableDateTime,
              upper));
             
          // TODO: implement prepend in Hd5 Sink
*/          
        }

        if (today.compareTo(sinkRange.getUpper()) > 0) {

          final Calendar lower = (Calendar) sinkRange.getUpper().clone();
          lower.add(Calendar.DATE, 1);
          // TODO: fix this by observing timezones
          final Calendar upper = (Calendar) today.clone();
          // cheat for now with upper bound on today (anywhere in the world!)
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
          
          futures.add(_executorService.submit(new ReadWriteQuotesTask(_eodDataProvider,
              _eodDataSink, exchange, symbol, requestRange.getLower(),
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
    
    if (_emailService != null) {
      final StringBuffer subjectBuffer = new StringBuffer();
      subjectBuffer.append("Updated quotes on ");
      subjectBuffer.append(exchange);

      final StringBuffer messageBuffer = new StringBuffer();
      messageBuffer.append("Wrote ");
      messageBuffer.append(nQuotesWritten);
      messageBuffer.append(" quotes over ");
      messageBuffer.append(symbols.length);
      messageBuffer.append(" symbols ");

      _emailService.send(subjectBuffer.toString(), messageBuffer.toString());
    }

    return false;

  }

  @Override
  public void setApplicationContext(ApplicationContext appContext)
      throws BeansException {
    // TODO Auto-generated method stub
    _appContext = appContext;
  }
}
