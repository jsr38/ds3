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

package nz.co.jsrsolutions.ds3.provider;

import java.lang.String;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;

import nz.co.jsrsolutions.ds3.DataStub;
import nz.co.jsrsolutions.ds3.DataStub.EXCHANGE;
import nz.co.jsrsolutions.ds3.DataStub.QUOTE;
import nz.co.jsrsolutions.ds3.DataStub.SYMBOL;
import nz.co.jsrsolutions.ds3.XigniteEnergyStub;
import nz.co.jsrsolutions.ds3.XigniteEnergyStub.ArrayOfFutureQuote;
import nz.co.jsrsolutions.ds3.XigniteEnergyStub.Future;
import nz.co.jsrsolutions.ds3.XigniteEnergyStub.FutureQuote;
import nz.co.jsrsolutions.ds3.XigniteEnergyStub.GetAllHistoricalEnergyFutures;
import nz.co.jsrsolutions.ds3.XigniteEnergyStub.GetAllHistoricalEnergyFuturesResponse;
import nz.co.jsrsolutions.ds3.XigniteEnergyStub.GetAllHistoricalEnergySwaps;
import nz.co.jsrsolutions.ds3.XigniteEnergyStub.GetAllHistoricalEnergySwapsResponse;
import nz.co.jsrsolutions.ds3.XigniteEnergyStub.Header;
import nz.co.jsrsolutions.ds3.XigniteEnergyStub.HeaderE;
import nz.co.jsrsolutions.ds3.XigniteEnergyStub.ListEnergySwaps;
import nz.co.jsrsolutions.ds3.XigniteEnergyStub.ListEnergySwapsResponse;
import nz.co.jsrsolutions.ds3.XigniteEnergyStub.OutcomeTypes;
import nz.co.jsrsolutions.util.Range;

import org.apache.log4j.Logger;

class XigniteEodDataProvider extends EodDataProviderBase implements
    EodDataProvider {

  private static final transient Logger logger = Logger
      .getLogger(XigniteEodDataProvider.class);

  private final String url;

  private final String username;

  private final String password;

  private final long timeout;

  private final transient HeaderE headerE;

  private final transient XigniteEnergyStub eodDataStub;

  private static final int DEFAULT_MONTHS_HISTORY = 36;

  public XigniteEodDataProvider(String url, String username, String password,
      long timeout) throws EodDataProviderException {

    this.url = url;
    this.username = username;
    this.password = password;
    this.timeout = timeout;

    try {
      Header header = new Header();
      header.setUsername(username);
      header.setPassword(password);
      headerE = new HeaderE();
      headerE.setHeader(header);

      eodDataStub = new XigniteEnergyStub(url);
      eodDataStub._getServiceClient().getOptions()
          .setTimeOutInMilliSeconds(timeout);
      logger
          .info(String
              .format(
                  "Constructed [ %s ] with: \nusername = [ %s ]\npassword = [ %s ]\n timeout = [ %d ]",
                  XigniteEodDataProvider.class, username, password, timeout));
    } catch (org.apache.axis2.AxisFault afe) {

      logger.error(afe.toString());
      EodDataProviderException edpe = new EodDataProviderException(
          "Unable to construct XigniteEodDataProvider");
      edpe.initCause(afe);

      throw edpe;

    } catch (java.rmi.RemoteException re) {

      logger.error(re.toString());
      EodDataProviderException edpe = new EodDataProviderException(
          "Unable to construct XigniteEodDataProvider");
      edpe.initCause(re);

      throw edpe;

    }

  }

  public EXCHANGE[] getExchanges() throws EodDataProviderException {

    try {

      // Instantiate the return class from the operation
      // This class was autogenerated by the WSDL2Java tool
      ListEnergySwaps listEnergySwapsArguments = new ListEnergySwaps();

      ListEnergySwapsResponse response = eodDataStub.listEnergySwaps(
          listEnergySwapsArguments, null);
      Future[] objFuture = response.getListEnergySwapsResult().getFuture();
      // objFuture is pointing to the result of the operation
      if (objFuture[0].getOutcome() == OutcomeTypes.RegistrationError) {
        // code to handle registration errors
        String msg = (OutcomeTypes.RegistrationError.toString() + ": " + objFuture[0].getMessage());
        logger.error(msg);
        throw new EodDataProviderException(msg);
      } else if (objFuture[0].getOutcome() == OutcomeTypes.RequestError) {
        // code to handle request errors
        String msg = (OutcomeTypes.RequestError.toString() + ": "
            + objFuture[0].getMessage());
        logger.error(msg);
        throw new EodDataProviderException(msg);
      } else if (objFuture[0].getOutcome() == OutcomeTypes.SystemError) {
        // code to handle system errors
        String msg = (OutcomeTypes.SystemError.toString() + ": "
            + objFuture[0].getMessage());
        logger.error(msg);
        throw new EodDataProviderException(msg);
      } else // Success
      {
        Set<String> exchanges = new LinkedHashSet<String>(objFuture.length);
        for (int i = 0; i < objFuture.length; ++i) {
          
          String exchange = objFuture[i].getExchange();
          
          if (exchange == null  || exchange == "")
          {
            exchange = "(null)";
          }
          else if (!exchanges.contains(exchange))
          {
            exchanges.add(exchange);
            if (logger.isInfoEnabled()) {
              logger.info(exchange);
            }  
          }
          
        }
        
        EXCHANGE[] exchangeArray = new EXCHANGE[exchanges.size()];
        int i = 0;
        for (String exchange : exchanges) {

          EXCHANGE ex = new EXCHANGE();
          ex.setCode(exchange);
          ex.setName("");
          ex.setCountry("");
          exchangeArray[i] = ex;
          ++i;
          
          if (logger.isDebugEnabled()) {

            StringBuffer logMessageBuffer = new StringBuffer();
            logMessageBuffer.append(" [ ");
            logMessageBuffer.append(ex.getCode());
            logMessageBuffer.append(" ]  [ ");
            logMessageBuffer.append(ex.getName());
            logMessageBuffer.append(" ] [ ");
            logMessageBuffer.append(ex.getCountry());
            logMessageBuffer.append(" ]");
            logger.debug(logMessageBuffer.toString());

          }
        }
        return exchangeArray;
      }

    } catch (Exception re) {

      logger.error(re.toString());
      EodDataProviderException edpe = new EodDataProviderException(
          "Unable to get exchanges");
      edpe.initCause(re);

      throw edpe;

    }

  }

  public int getExchangeMonths(String exchange) throws EodDataProviderException {

    try {

      int months = 0;// Integer.parseInt(monthsString);

      if (months == 0) {
        months = DEFAULT_MONTHS_HISTORY;
      }

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

    } catch (Exception re) {

      logger.error(re.toString());
      EodDataProviderException edpe = new EodDataProviderException(
          "Unable to get exchanges");
      edpe.initCause(re);

      throw edpe;

    }

  }

  public SYMBOL[] getSymbols(String exchange) throws EodDataProviderException {

    try {

      if (logger.isInfoEnabled()) {
        logger.info("Retrieving symbols available on [ " + exchange + " ] ");
      }

      // Instantiate the return class from the operation
      // This class was autogenerated by the WSDL2Java tool
      ListEnergySwaps listEnergySwapsArguments = new ListEnergySwaps();

      ListEnergySwapsResponse response = eodDataStub.listEnergySwaps(
          listEnergySwapsArguments, null);
      Future[] objFuture = response.getListEnergySwapsResult().getFuture();
      // objFuture is pointing to the result of the operation
      if (objFuture[0].getOutcome() == OutcomeTypes.RegistrationError) {
        // code to handle registration errors
        String msg = (OutcomeTypes.RegistrationError.toString() + ": " + objFuture[0]
            .getMessage());
        logger.error(msg);
        throw new EodDataProviderException(msg);
      } else if (objFuture[0].getOutcome() == OutcomeTypes.RequestError) {
        // code to handle request errors
        String msg = (OutcomeTypes.RequestError.toString() + ": "
            + objFuture[0].getMessage());
        logger.error(msg);
        throw new EodDataProviderException(msg);
      } else if (objFuture[0].getOutcome() == OutcomeTypes.SystemError) {
        // code to handle system errors
        String msg = (OutcomeTypes.SystemError.toString() + ": "
            + objFuture[0].getMessage());
        logger.error(msg);
        throw new EodDataProviderException(msg);
      } else // Success
      {
        SYMBOL[] symbols = new SYMBOL[objFuture.length];
        for (int i = 0; i < objFuture.length; ++i) {
          
          SYMBOL symbol = new SYMBOL();
          symbol.setCode(objFuture[i].getExchangeSymbol());
          symbols[i] = symbol;
          if (logger.isInfoEnabled()) {
            
            logger.info(objFuture[i].getSymbol());
          
          }
        }
        for (Future symbol : objFuture) {

          if (logger.isDebugEnabled()) {

            StringBuffer logMessageBuffer = new StringBuffer();
            logMessageBuffer.append(" [ ");
            logMessageBuffer.append(symbol.getSymbol());
            logMessageBuffer.append(" ]  [ ");
            logMessageBuffer.append(symbol.getName());
            logMessageBuffer.append(" ] [ ");
            logMessageBuffer.append(symbol.getType());
            logMessageBuffer.append(" ]");
            logger.debug(logMessageBuffer.toString());

          }
        }
        return symbols;
      }

    } catch (java.rmi.RemoteException re) {

      logger.error(re.toString());
      EodDataProviderException edpe = new EodDataProviderException(
          "Unable to get symbols");
      edpe.initCause(re);

      throw edpe;

    }
  }

  public QUOTE[] getQuotes(String exchange, String symbol,
      Calendar startCalendar, Calendar endCalendar, String period)
      throws EodDataProviderException {

    try {

      StringBuffer startDate = new StringBuffer();
      startDate.append(Integer.toString(startCalendar.get(Calendar.YEAR)));
      startDate.append(String.format("%02d",
          startCalendar.get(Calendar.MONTH) + 1));
      startDate.append(String.format("%02d",
          startCalendar.get(Calendar.DAY_OF_MONTH)));

      StringBuffer endDate = new StringBuffer();
      endDate.append(Integer.toString(endCalendar.get(Calendar.YEAR)));
      endDate
          .append(String.format("%02d", endCalendar.get(Calendar.MONTH) + 1));
      endDate.append(String.format("%02d",
          endCalendar.get(Calendar.DAY_OF_MONTH)));

      GetAllHistoricalEnergyFutures request = new GetAllHistoricalEnergyFutures();
      request.setSymbol(symbol);

      GetAllHistoricalEnergyFuturesResponse response = eodDataStub
          .getAllHistoricalEnergyFutures(request, headerE);

      if (logger.isDebugEnabled()) {
        logger.debug(response.toString());
      }

      if (response.getGetAllHistoricalEnergyFuturesResult() == null) {
        throw new EodDataProviderException("Failed to retrieve quotes");
      }

      ArrayOfFutureQuote array = response.getGetAllHistoricalEnergyFuturesResult();
      
      DataStub.QUOTE[] quotes = new DataStub.QUOTE[array.getFutureQuote().length];
      
      int i = 0;
      for (FutureQuote quote : array.getFutureQuote())
      {
        quotes[i] = new QUOTE();
        quotes[i].setOpen(quote.getOpen());
        quotes[i].setHigh(quote.getHigh());
        quotes[i].setLow(quote.getLow());
        quotes[i].setClose(quote.getLast());
        quotes[i].setVolume(Long.parseLong(String.format("%f0", quote.getVolume())));
        
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
        
        calendar.setTime(sdf.parse(quote.getDate()));
       
        
        quotes[i].setDateTime(calendar);
      }

      return quotes;

    } catch (java.rmi.RemoteException re) {

      logger.error(re.toString());
      EodDataProviderException edpe = new EodDataProviderException(
          "Unable to get symbol history");
      edpe.initCause(re);

      throw edpe;

    } catch (ParseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        logger.error("Couldn't parse date from quote");
        EodDataProviderException edpe = new EodDataProviderException(
            "Unable to get symbol history");
        edpe.initCause(e);
        throw edpe;
    }

  }

  public QUOTE[] getQuotes(String exchange) throws EodDataProviderException {

    try {

      GetAllHistoricalEnergySwaps request = new GetAllHistoricalEnergySwaps();
      GetAllHistoricalEnergySwapsResponse response = eodDataStub
          .getAllHistoricalEnergySwaps(request, headerE);

      if (response.getGetAllHistoricalEnergySwapsResult() == null) {
        throw new EodDataProviderException("Failed to retrieve quotes");
      }

      DataStub.QUOTE[] quotes = new DataStub.QUOTE[2];

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
          messageBuffer.append(String.format("%02d",
              calendar.get(Calendar.MONTH)));
          messageBuffer.append(String.format("%02d",
              calendar.get(Calendar.DAY_OF_MONTH)));
          messageBuffer.append(",");
          messageBuffer.append(Double.toString(quote.getClose()));

          // messageBuffer.append(quote.getDateTime());
          messageBuffer.append(" [ ");
          messageBuffer.append(quote.getBid());
          messageBuffer.append(",");
          messageBuffer.append(quote.getAsk());
          messageBuffer.append(" ] ");
          logger.debug(messageBuffer.toString());
        }
      }

      return quotes;

    } catch (java.rmi.RemoteException re) {

      logger.error(re.toString());
      EodDataProviderException edpe = new EodDataProviderException(
          "Unable to get symbol history");
      edpe.initCause(re);

      throw edpe;

    }

  }

  @Override
  public Range<Calendar> getExchangeDateRange(String exchange)
      throws EodDataProviderException {

    return null;
  }

}
