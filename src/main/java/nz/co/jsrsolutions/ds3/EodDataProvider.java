/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

package nz.co.jsrsolutions.ds3;

import java.lang.String;
import java.util.Calendar;

import nz.co.jsrsolutions.ds3.DataStub.EXCHANGE;
import nz.co.jsrsolutions.ds3.DataStub.QUOTE;
import nz.co.jsrsolutions.ds3.DataStub.SYMBOL;

interface EodDataProvider {

  public EXCHANGE[] getExchanges() throws EodDataProviderException;

  public int getExchangeMonths(String exchange) throws EodDataProviderException;

  public SYMBOL[] getSymbols(String exchange) throws EodDataProviderException;

  public QUOTE[] getQuotes(String exchange,
                           String symbol,
                           Calendar startCalendar,
                           Calendar endCalendar,
                           String period) throws EodDataProviderException;

  public QUOTE[] getQuotes(String exchange) throws EodDataProviderException;


}