/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

package nz.co.jsrsolutions.ds3;

import java.lang.String;

import nz.co.jsrsolutions.ds3.DataStub.EXCHANGE;
import nz.co.jsrsolutions.ds3.DataStub.SYMBOL;
import nz.co.jsrsolutions.ds3.DataStub.QUOTE;

interface EodDataSink {

  public void updateExchanges(EXCHANGE[] exchanges) throws EodDataSinkException;

  public void updateExchangeSymbols(String exchange,
                                    SYMBOL[] symbols) throws EodDataSinkException;

  public void updateQuotes(String exchange,
                           String symbol,
                           String startDate,
                           String endDate,
                           String period,
                           QUOTE[] quotes) throws EodDataSinkException;

  public void updateExchangeSymbolQuotes(String exchange,
                                         String symbol,
                                         QUOTE[] quotes) throws EodDataSinkException;

  public void close() throws EodDataSinkException;

}