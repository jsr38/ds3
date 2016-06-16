/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)EodDataSinkEmptyMock.java        
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


package nz.co.jsrsolutions.ds3.sink.test;

import java.util.Calendar;

import nz.co.jsrsolutions.ds3.DataStub.QUOTE;
import nz.co.jsrsolutions.ds3.sink.EodDataSinkException;
import nz.co.jsrsolutions.ds3.test.UnitTestData;
import nz.co.jsrsolutions.util.Range;

public class EodDataSinkEmptyMock extends EodDataSinkMock {

  public EodDataSinkEmptyMock(UnitTestData testData) throws EodDataSinkException {
    super(testData);
    
  }

  public void updateExchangeSymbolQuotes(String exchange,
                                        String symbol,
                                        QUOTE[] quotes) throws EodDataSinkException {

    if (quotes == null) {
      throw new EodDataSinkException("Invalid quote vector.");
    }

    // basically this does nothing because whatever contiguous
    // block of quotes we pass in should be able to be persisted
    // as we are an 'empty' data sink.


  }
  
  @Override
  public Range<Calendar> readExchangeSymbolDateRange(String exchange, String symbol) {
    return null;
  }
  
  
}
