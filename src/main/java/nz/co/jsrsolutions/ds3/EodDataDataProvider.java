/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)EodDataDataProvider.java        
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

import java.lang.String;
import java.util.Calendar;

import nz.co.jsrsolutions.ds3.DataStub.EXCHANGE;
import nz.co.jsrsolutions.ds3.DataStub.QUOTE;
import nz.co.jsrsolutions.ds3.DataStub.SYMBOL;

import org.apache.log4j.Logger;



class EodDataDataProvider extends EodDataProviderBase implements EodDataProvider {

  private static final transient Logger logger = Logger.getLogger(EodDataDataProvider.class);

  @SuppressWarnings("unused")
  private final String url;
  
  @SuppressWarnings("unused")
  private final String username;

  @SuppressWarnings("unused")
  private final String password;

  @SuppressWarnings("unused")
  private final long timeout;

  private final transient DataStub eodDataStub;

  private final transient String token;


  public EodDataDataProvider(String url,
                             String username,
                             String password,
                             long timeout) throws EodDataProviderException {

    this.url = url;
    this.username = username;
    this.password = password;
    this.timeout = timeout;

    try {

      eodDataStub = new DataStub(url);
      eodDataStub._getServiceClient().getOptions().setTimeOutInMilliSeconds(timeout);

      DataStub.Login loginRequest = new DataStub.Login();
      loginRequest.setUsername(username);
      loginRequest.setPassword(password);

      // Login
      DataStub.LoginResponse0 loginResponse0 = eodDataStub.login(loginRequest);
      DataStub.LOGINRESPONSE loginResponse = loginResponse0.getLoginResult();
        
      if (loginResponse == null) {
        throw new EodDataProviderException("Failed to authenticate with EOD Data web service.");
      }
          
      token = loginResponse.getToken();

      logger.info(loginResponse.getMessage());
      logger.info(token);
      logger.info(loginResponse.getDataFormat());
    }
    catch(org.apache.axis2.AxisFault afe) {

      logger.error(afe.toString());
      EodDataProviderException edpe = new EodDataProviderException("Unable to construct EodDataDataProvider");
      edpe.initCause(afe);

      throw edpe;

    }
    catch(java.rmi.RemoteException re) {

      logger.error(re.toString());
      EodDataProviderException edpe = new EodDataProviderException("Unable to construct EodDataDataProvider");
      edpe.initCause(re);

      throw edpe;

    }

  }


  public EXCHANGE[] getExchanges() throws EodDataProviderException {

    try {

      // exchangeList
      DataStub.ExchangeList exchangeListRequest = new DataStub.ExchangeList();
      exchangeListRequest.setToken(token);

      DataStub.ExchangeListResponse exchangeListResponse = eodDataStub.exchangeList(exchangeListRequest);
      DataStub.EXCHANGE[] exchanges = exchangeListResponse.getExchangeListResult().getEXCHANGES().getEXCHANGE();

      for (DataStub.EXCHANGE exchange : exchanges) {

        if (logger.isDebugEnabled()) {

          StringBuffer logMessageBuffer = new StringBuffer();
          logMessageBuffer.append(" [ ");
          logMessageBuffer.append(exchange.getCode());
          logMessageBuffer.append(" ]  [ ");
          logMessageBuffer.append(exchange.getName());
          logMessageBuffer.append(" ] [ ");
          logMessageBuffer.append(exchange.getCountry());
          logMessageBuffer.append(" ]");
          logger.debug(logMessageBuffer.toString());

        }
      }

      // exchangeGet
      //      DataStub.ExchangeGet exchangeGetRequest = new DataStub.ExchangeGet();
      //      exchangeGetRequest.setToken(token);
      //      exchangeGetRequest.setExchange("WCE");

      //      DataStub.ExchangeGetResponse exchangeGetResponse = eodDataStub.exchangeGet(exchangeGetRequest);
      //      DataStub.ArrayOfExchange exchanges = exchangeGetResponse.getExchangeGetResult().getEXCHANGES();
      //      EXCHANGE[] exchanges = exchanges.getEXCHANGE();
        
      //      logger.debug(exchangeGetResponse.getExchangeGetResult().getMessage());

      return exchanges;
      
    }
    catch(java.rmi.RemoteException re) {

      logger.error(re.toString());
      EodDataProviderException edpe = new EodDataProviderException("Unable to get exchanges");
      edpe.initCause(re);

      throw edpe;

    }
      
  }

  public int getExchangeMonths(String exchange) throws EodDataProviderException {

    try {

      DataStub.ExchangeMonths exchangeMonthsRequest = new DataStub.ExchangeMonths();
      exchangeMonthsRequest.setToken(token);

      DataStub.ExchangeMonthsResponse exchangeMonthsResponse = eodDataStub.exchangeMonths(exchangeMonthsRequest);
      String monthsString = exchangeMonthsResponse.getExchangeMonthsResult().getMONTHS();

      int months = Integer.parseInt(monthsString);

      if (logger.isDebugEnabled()) {

        StringBuffer logMessageBuffer = new StringBuffer();
        logMessageBuffer.append(" Exchange [ ");
        logMessageBuffer.append(exchange);
        logMessageBuffer.append(" ] has [ ");
        logMessageBuffer.append(months);
        logMessageBuffer.append(" ] months of history available. ");
        logger.debug(logMessageBuffer.toString());

      }

      return months;
      
    }
    catch(java.rmi.RemoteException re) {

      logger.error(re.toString());
      EodDataProviderException edpe = new EodDataProviderException("Unable to get exchanges");
      edpe.initCause(re);

      throw edpe;

    }
      
  }


  public SYMBOL[] getSymbols(String exchange) throws EodDataProviderException {

    try {

      if (logger.isInfoEnabled()) {  
        logger.info("Retrieving symbols available on [ " + exchange + " ] ");
      }

      // symbolList
      DataStub.SymbolList symbolListRequest = new DataStub.SymbolList();
      symbolListRequest.setToken(token);
      symbolListRequest.setExchange(exchange);

      DataStub.SymbolListResponse symbolListResponse = eodDataStub.symbolList(symbolListRequest);
      DataStub.SYMBOL[] symbolArray = symbolListResponse.getSymbolListResult().getSYMBOLS().getSYMBOL();

      for (DataStub.SYMBOL symbol : symbolArray) {

        if (logger.isDebugEnabled()) {

          StringBuffer logMessageBuffer = new StringBuffer();
          logMessageBuffer.append(" [ ");
          logMessageBuffer.append(symbol.getCode());
          logMessageBuffer.append(" ]  [ ");
          logMessageBuffer.append(symbol.getName());
          logMessageBuffer.append(" ] [ ");
          logMessageBuffer.append(symbol.getLongName());
          logMessageBuffer.append(" ]");
          logger.debug(logMessageBuffer.toString());

        }
      }
      return symbolArray;

    }
    catch(java.rmi.RemoteException re) {

      logger.error(re.toString());
      EodDataProviderException edpe = new EodDataProviderException("Unable to get symbols");
      edpe.initCause(re);

      throw edpe;

    }
  }

  public QUOTE[] getQuotes(String exchange,
                           String symbol,
                           Calendar startCalendar,
                           Calendar endCalendar,
                           String period) throws EodDataProviderException {

    try {

      StringBuffer startDate = new StringBuffer();
      startDate.append(Integer.toString(startCalendar.get(Calendar.YEAR)));
      startDate.append(String.format("%02d", startCalendar.get(Calendar.MONTH) + 1));
      startDate.append(String.format("%02d", startCalendar.get(Calendar.DAY_OF_MONTH)));

      StringBuffer endDate = new StringBuffer();
      endDate.append(Integer.toString(endCalendar.get(Calendar.YEAR)));
      endDate.append(String.format("%02d", endCalendar.get(Calendar.MONTH) + 1));
      endDate.append(String.format("%02d", endCalendar.get(Calendar.DAY_OF_MONTH)));


      DataStub.SymbolHistoryPeriodByDateRange symbolHistoryPeriodByDateRangeRequest = new DataStub.SymbolHistoryPeriodByDateRange();
      symbolHistoryPeriodByDateRangeRequest.setToken(token);
      symbolHistoryPeriodByDateRangeRequest.setExchange(exchange);
      symbolHistoryPeriodByDateRangeRequest.setSymbol(symbol);
      symbolHistoryPeriodByDateRangeRequest.setStartDate(startDate.toString());
      symbolHistoryPeriodByDateRangeRequest.setEndDate(endDate.toString());
      symbolHistoryPeriodByDateRangeRequest.setPeriod(period);

      DataStub.SymbolHistoryPeriodByDateRangeResponse symbolHistoryPeriodByDateRangeResponse = eodDataStub.symbolHistoryPeriodByDateRange(symbolHistoryPeriodByDateRangeRequest);

      DataStub.RESPONSE response = symbolHistoryPeriodByDateRangeResponse.getSymbolHistoryPeriodByDateRangeResult();

      if (logger.isDebugEnabled()) {
        logger.debug(response.getMessage());
      }

      if (response.getQUOTES() == null) {
        throw new EodDataProviderException("Failed to retrieve quotes");
      }        

      DataStub.QUOTE[] quotes = response.getQUOTES().getQUOTE();
 
      if (quotes == null) {
        throw new EodDataProviderException("Failed to retrieve quotes");
      }

      // This is a fix to cope with the web service occasionally returning 
      // null values for a quote symbol.  It is not understood why this occurs.
      for (QUOTE quote : quotes) {
        if (quote.getSymbol() == null) {
          quote.setSymbol(symbol);
        }
      }

      return quotes;

    }
    catch(java.rmi.RemoteException re) {

      logger.error(re.toString());
      EodDataProviderException edpe = new EodDataProviderException("Unable to get symbol history");
      edpe.initCause(re);

      throw edpe;

    }

  }

  public QUOTE[] getQuotes(String exchange) throws EodDataProviderException {

    try {

      DataStub.QuoteList quoteListRequest = new DataStub.QuoteList();
      quoteListRequest.setToken(token);
      quoteListRequest.setExchange(exchange);

      DataStub.QuoteListResponse quoteListResponse = eodDataStub.quoteList(quoteListRequest);
      DataStub.RESPONSE response = quoteListResponse.getQuoteListResult();

      logger.info(response.getMessage());

      if (response.getQUOTES() == null) {
        throw new EodDataProviderException("Failed to retrieve quotes");
      }        

      DataStub.QUOTE[] quotes = response.getQUOTES().getQUOTE();
 
      if (quotes == null) {
        throw new EodDataProviderException("Failed to retrieve quotes");
      }

      if (logger.isDebugEnabled()) {
        for (QUOTE quote : quotes) {
          StringBuffer messageBuffer = new StringBuffer();
          messageBuffer.append(exchange);
          messageBuffer.append(".");
          messageBuffer.append(quote.getSymbol());
          messageBuffer.append(".");

          Calendar calendar = quote.getDateTime();
          messageBuffer.append(Integer.toString(calendar.get(Calendar.YEAR)));
          messageBuffer.append(String.format("%02d", calendar.get(Calendar.MONTH)));
          messageBuffer.append(String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)));
          messageBuffer.append(",");
          messageBuffer.append(Double.toString(quote.getClose()));

          //          messageBuffer.append(quote.getDateTime());
          messageBuffer.append(" [ ");
          messageBuffer.append(quote.getBid());
          messageBuffer.append(",");
          messageBuffer.append(quote.getAsk());
          messageBuffer.append(" ] ");
          logger.debug(messageBuffer.toString());
        }
      }

      return quotes;

    }
    catch(java.rmi.RemoteException re) {

      logger.error(re.toString());
      EodDataProviderException edpe = new EodDataProviderException("Unable to get symbol history");
      edpe.initCause(re);

      throw edpe;

    }

  }

}
