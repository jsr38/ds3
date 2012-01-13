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

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import nz.co.jsrsolutions.ds3.DataStub.EXCHANGE;
import nz.co.jsrsolutions.ds3.DataStub.QUOTE;
import nz.co.jsrsolutions.ds3.DataStub.SYMBOL;
import nz.co.jsrsolutions.ds3.EodDataSink;
import nz.co.jsrsolutions.ds3.EodDataSinkException;
import nz.co.jsrsolutions.util.ArrayConverter;
import nz.co.jsrsolutions.util.Range;

import org.apache.axis2.databinding.ADBBean;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;

class EodDataSinkMock implements EodDataSink {

  private static final transient Logger logger = Logger.getLogger(EodDataSinkMock.class);

  private static final String TESTDATA_FILENAME = new String("testdata.xml");

  private static final String XML_NAMESPACE_URI = new String("http://www.jsrsolutions.co.nz/ds3/testdata");

  private static final String XML_PREFIX = new String("td");

  @SuppressWarnings("unused")
  private static final QName EXCHANGE_QNAME = new QName(XML_NAMESPACE_URI, "exchange", XML_PREFIX);

  @SuppressWarnings("unused")
  private static final QName SYMBOL_QNAME = new QName(XML_NAMESPACE_URI, "symbol", XML_PREFIX);

  @SuppressWarnings("unused")
  private static final QName QUOTE_QNAME = new QName(XML_NAMESPACE_URI, "quote", XML_PREFIX);

  private static final String TESTDATA_LOCALNAME = new String("testdata");

  private static final String EXCHANGES_LOCALNAME = new String("exchanges");

  private static final String SYMBOLS_LOCALNAME = new String("symbols");

  private static final String QUOTES_LOCALNAME = new String("quotes");

  @SuppressWarnings("unused")
  private static final QName TESTDATA_QNAME = new QName(XML_NAMESPACE_URI, TESTDATA_LOCALNAME, XML_PREFIX);

  private static final QName EXCHANGES_QNAME = new QName(XML_NAMESPACE_URI, EXCHANGES_LOCALNAME, XML_PREFIX);

  private static final QName SYMBOLS_QNAME = new QName(XML_NAMESPACE_URI, SYMBOLS_LOCALNAME, XML_PREFIX);

  private static final QName QUOTES_QNAME = new QName(XML_NAMESPACE_URI, QUOTES_LOCALNAME, XML_PREFIX);

  @SuppressWarnings("unused")
  private static final String TESTEXCHANGE_ATTRNAME = new String("testExchange");

  @SuppressWarnings("unused")
  private static final String TESTSYMBOL_ATTRNAME = new String("testSymbol");

  private String testExchange;

  private String testSymbol;

  private EXCHANGE[] exchanges;

  private SYMBOL[] symbols;

  private QUOTE[] quotes;

  EodDataSinkMock() throws EodDataSinkException {

    deserialiseTestData();

  }

  private void deserialiseTestData() {

    try {

      InputStream testDataStream = EodDataSinkMock.class.getClassLoader().getResourceAsStream(TESTDATA_FILENAME);
      XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(testDataStream);
    
      while(reader.next() != XMLStreamReader.END_DOCUMENT) {
      
        if (reader.getEventType() == XMLStreamReader.START_ELEMENT) {

          if (reader.getName().equals(EXCHANGES_QNAME)) {
            exchanges = this.<EXCHANGE, EXCHANGE.Factory>deserialiseTypeArray(reader, EXCHANGE.class, EXCHANGE.Factory.class);
          }

          if (reader.getName().equals(SYMBOLS_QNAME)) {
            testExchange = reader.getAttributeValue(XML_NAMESPACE_URI, TESTEXCHANGE_ATTRNAME);
            symbols = this.<SYMBOL, SYMBOL.Factory>deserialiseTypeArray(reader, SYMBOL.class, SYMBOL.Factory.class);
          }

          if (reader.getName().equals(QUOTES_QNAME)) {
            testSymbol = reader.getAttributeValue(XML_NAMESPACE_URI, TESTSYMBOL_ATTRNAME);
            quotes = this.<QUOTE, QUOTE.Factory>deserialiseTypeArray(reader, QUOTE.class, QUOTE.Factory.class);
          }
        }
      }

    }
    catch (XMLStreamException e) {
      e.printStackTrace();
      logger.error("Unable to deserialise test data");
    }

  }

  private <T extends ADBBean, F> T[] deserialiseTypeArray(XMLStreamReader reader, Class<T> beanClass, Class<F> factoryClass) {

    Method factoryMethod;
    ArrayList<T> beanArrayList;

    try {

      
      factoryMethod = factoryClass.getMethod("parse", XMLStreamReader.class);

      beanArrayList = new ArrayList<T>();

      int eventType = reader.nextTag();
      do {
        
        Object object = factoryMethod.invoke(null, reader);
        if (object != null) {
          @SuppressWarnings("unchecked")
          final T bean = (T)object;
          beanArrayList.add(bean);
        }
        else {
          System.out.println("Cast failed!");
        }

      } while (reader.nextTag() != XMLStreamReader.END_ELEMENT);

    }
    catch (NoSuchMethodException e) {
      e.printStackTrace();
      logger.error(e.toString());
      return null;
    }
    catch (SecurityException e) {
      e.printStackTrace();
      logger.error(e.toString());
      return null;
    }
    catch (XMLStreamException e) {
      e.printStackTrace();
      logger.error(e.toString());
      return null;
    }
    catch (IllegalAccessException e) {
      e.printStackTrace();
      logger.error(e.toString());
      return null;
    }
    catch (InvocationTargetException e) {
      e.printStackTrace();
      logger.error(e.toString());
      return null;
    }
    
    return ArrayConverter.convert(beanArrayList.toArray(), beanClass);

  }

  public void updateExchanges(EXCHANGE[] exchanges) throws EodDataSinkException {

    throw new NotImplementedException();
  }

  public void updateExchangeSymbols(String exchange, SYMBOL[] symbols) throws EodDataSinkException {

    throw new NotImplementedException();

  }

  @SuppressWarnings("unused")
private void createExchangeDataset(long dimension) throws EodDataSinkException {
  
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

  @SuppressWarnings("unused")
  private void readExchangeSymbolQuotes(String exchange, String symbol) throws EodDataSinkException {
    

    openQuoteDataset(exchange, symbol);


  }

  public void close() {
    throw new NotImplementedException();
  }

  public Range<Calendar> getRange(String exchange, String symbol) {
    throw new NotImplementedException();
  }

  public String getTestExchange() {
    return testExchange;
  }

  public String getTestSymbol() {
    return testSymbol;
  }

}
