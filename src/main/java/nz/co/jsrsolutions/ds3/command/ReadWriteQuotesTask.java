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

import java.lang.Runnable;
import java.util.Calendar;
import nz.co.jsrsolutions.ds3.DataStub.QUOTE;
import nz.co.jsrsolutions.ds3.provider.EodDataProvider;
import nz.co.jsrsolutions.ds3.sink.EodDataSink;


public class ReadWriteQuotesTask implements Runnable {

    public ReadWriteQuotesTask(String exchange,
			       String symbol,
			       Calendar lower,
			       Calendar upper) {
    }

              public void run() {

                // simulate a long-running task
                try {

          final QUOTE[] quotes = eodDataProvider.getQuotes(exchange,
              symbol,
              requestRange.getLower(),
              requestRange.getUpper(),
              DEFAULT_FREQUENCY);

          if (quotes.length == 0) {
	      //            logger.info("Quote array from provider was empty!");
          }

          eodDataSink.updateExchangeSymbolQuotes(exchange,
              symbol,
              quotes);

          nQuotesWritten += quotes.length;

                } catch (InterruptedException ex) {
                }
              }
            

}