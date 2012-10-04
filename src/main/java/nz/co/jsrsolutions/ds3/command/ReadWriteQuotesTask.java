/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)ReadWriteQuotesTask.java
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
import java.util.concurrent.Callable;
import org.apache.log4j.Logger;

import nz.co.jsrsolutions.ds3.DataStub.QUOTE;
import nz.co.jsrsolutions.ds3.provider.EodDataProvider;
import nz.co.jsrsolutions.ds3.provider.EodDataProviderException;
import nz.co.jsrsolutions.ds3.sink.EodDataSink;
import nz.co.jsrsolutions.ds3.sink.EodDataSinkException;

public class ReadWriteQuotesTask implements Callable<Long> {

  private static final transient Logger logger = Logger
      .getLogger(ReadWriteQuotesTask.class);

  private final EodDataProvider _eodDataProvider;
  private final EodDataSink _eodDataSink;

  private final String _exchange;
  private final String _symbol;
  private final Calendar _lower;
  private final Calendar _upper;
  private long _nQuotesWritten;

  private static final String DEFAULT_FREQUENCY = new String("d");

  public ReadWriteQuotesTask(EodDataProvider eodDataProvider,
      EodDataSink eodDataSink, String exchange, String symbol, Calendar lower,
      Calendar upper) {
    _eodDataProvider = eodDataProvider;
    _eodDataSink = eodDataSink;
    _exchange = exchange;
    _symbol = symbol;
    _lower = lower;
    _upper = upper;
    _nQuotesWritten = 0;
  }

  public Long call() {

    final StringBuffer logMessageBuffer = new StringBuffer();
    logMessageBuffer.append(" Attempting to retrieve quotes on [ ");
    logMessageBuffer.append(_exchange);
    logMessageBuffer.append(" ] for [ ");
    logMessageBuffer.append(_symbol);
    logMessageBuffer.append(" ] ");
    logger.info(logMessageBuffer.toString());

    try {

      final QUOTE[] quotes = _eodDataProvider.getQuotes(_exchange, _symbol,
          _lower, _upper, DEFAULT_FREQUENCY);

      if (quotes.length == 0) {
        // logger.info("Quote array from provider was empty!");
      }

      _eodDataSink.updateExchangeSymbolQuotes(_exchange, _symbol, quotes);

      _nQuotesWritten = quotes.length;

    } catch (EodDataProviderException e) {
      logger.error("Provider exception: ", e);
    } catch (EodDataSinkException e) {
      // TODO Auto-generated catch block
      logger.error("Sink exception: ", e);
    }
    return new Long(_nQuotesWritten);
  }

  public long getQuotesWritten() {
    return _nQuotesWritten;
  }
}
