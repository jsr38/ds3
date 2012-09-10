/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)EodDataSinkMock.java        
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

package nz.co.jsrsolutions.ds3.test;

import java.util.Calendar;

import nz.co.jsrsolutions.ds3.DataStub.EXCHANGE;
import nz.co.jsrsolutions.ds3.DataStub.QUOTE;
import nz.co.jsrsolutions.ds3.DataStub.SYMBOL;
import nz.co.jsrsolutions.ds3.sink.EodDataSinkException;
import nz.co.jsrsolutions.ds3.sink.EodDataSink;
import nz.co.jsrsolutions.util.Range;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;

class EodDataSinkMock implements EodDataSink {

  @SuppressWarnings("unused")
  private static final transient Logger logger = Logger.getLogger(EodDataSinkMock.class);

  protected UnitTestData testData = null;
  
  EodDataSinkMock(UnitTestData testData) throws EodDataSinkException {
    
    this.testData = testData;
  
  }

  public void updateExchanges(EXCHANGE[] exchanges) throws EodDataSinkException {

    throw new NotImplementedException();
  }

  public void updateExchangeSymbols(String exchange, SYMBOL[] symbols) throws EodDataSinkException {

    throw new NotImplementedException();

  }
  
  public void updateExchangeSymbolQuotes(String exchange,
                                         String symbol,
                                         QUOTE[] quotes) throws EodDataSinkException {

    if (quotes == null) {
      throw new EodDataSinkException("Invalid quote vector.");
    }

    openQuoteDataset(exchange, symbol);


  }

  protected void openQuoteDataset(String exchange, String symbol) throws EodDataSinkException {

    throw new NotImplementedException();
  }

  @SuppressWarnings("unused")
  private void readExchangeSymbolQuotes(String exchange, String symbol) throws EodDataSinkException {
    

    openQuoteDataset(exchange, symbol);


  }

  public void close() {
    throw new NotImplementedException();
  }

  @Override
  public Range<Calendar> readExchangeSymbolDateRange(String exchange,
      String symbol) throws EodDataSinkException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String[] readExchangeSymbols(String exchange)
      throws EodDataSinkException {
    // TODO Auto-generated method stub
    return null;
  }

}
