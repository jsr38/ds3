/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)EodDataProviderMock.java        
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

import java.lang.String;
import java.util.Calendar;

import nz.co.jsrsolutions.ds3.EodDataProvider;
import nz.co.jsrsolutions.ds3.EodDataProviderBase;
import nz.co.jsrsolutions.ds3.EodDataProviderException;
import nz.co.jsrsolutions.ds3.DataStub.EXCHANGE;
import nz.co.jsrsolutions.ds3.DataStub.QUOTE;
import nz.co.jsrsolutions.ds3.DataStub.SYMBOL;
import nz.co.jsrsolutions.util.Range;

import org.apache.log4j.Logger;


class EodDataProviderMock extends EodDataProviderBase implements EodDataProvider {

  @SuppressWarnings("unused")
  private static final transient Logger logger = Logger.getLogger(EodDataProviderMock.class);

  private UnitTestData testData = null;

  public EodDataProviderMock(UnitTestData testData) throws EodDataProviderException {

    this.testData = testData;
    

  }


  public EXCHANGE[] getExchanges() throws EodDataProviderException {

    return testData.getExchanges();
      
  }

  public int getExchangeMonths(String exchange) throws EodDataProviderException {

    return 3;
  }


  public SYMBOL[] getSymbols(String exchange) throws EodDataProviderException {

    return testData.getSymbols();

  }

  public QUOTE[] getQuotes(String exchange,
                           String symbol,
                           Calendar startCalendar,
                           Calendar endCalendar,
                           String period) throws EodDataProviderException {

    return testData.getQuotes();

  }

  public QUOTE[] getQuotes(String exchange) throws EodDataProviderException {

    return testData.getQuotes();

  }


  @Override
  public Range<Calendar> getExchangeDateRange(String exchange)
      throws EodDataProviderException {
    // TODO Auto-generated method stub
    return null;
  }

}
