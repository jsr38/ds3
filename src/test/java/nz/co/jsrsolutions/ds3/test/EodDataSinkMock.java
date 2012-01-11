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

package nz.co.jsrsolutions.ds3;

import java.lang.Integer;
import java.lang.String;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nz.co.jsrsolutions.ds3.DataStub.EXCHANGE;
import nz.co.jsrsolutions.ds3.DataStub.QUOTE;
import nz.co.jsrsolutions.ds3.DataStub.SYMBOL;
import nz.co.jsrsolutions.util.Range;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;

class EodDataSinkMock implements EodDataSink {

  private static final transient Logger logger = Logger.getLogger(EodDataSinkMock.class);

  public Hdf5EodDataSink(String filename) throws EodDataSinkException {
    throw new NotImplementedException();
  }

  public void updateExchanges(EXCHANGE[] exchanges) throws EodDataSinkException {

    throw new NotImplementedException();
  }

  public void updateExchangeSymbols(String exchange, SYMBOL[] symbols) throws EodDataSinkException {

    throw new NotImplementedException();

  }


  private void createExchangeDataset(long dimension) throws HDF5Exception, EodDataSinkException {
  
    throw new NotImplementedException();
  }

  public void updateQuotes(String exchange,
                           String symbol,
                           String startDate,
                           String endDate,
                           String period,
                           QUOTE[] quotes) throws EodDataSinkException {

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

  private void openQuoteDataset(String exchange, String symbol) throws EodDataSinkException {

    throw new NotImplementedException();
  }

  private void readExchangeSymbolQuotes(String exchange, String symbol) throws EodDataSinkException {
    

    openQuoteDataset(exchange, symbol);


  }

  public void close() {

    throw new NotImplementedException();
  }

  public Range<Calendar> getRange(String exchange, String symbol) {
    throw new NotImplementedException();
  }

}
