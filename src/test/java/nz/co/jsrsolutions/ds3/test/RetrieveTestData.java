/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)RetrieveTestData.java        
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

import nz.co.jsrsolutions.ds3.*;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.String;
import java.util.Calendar;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axis2.databinding.ADBBean;
import org.apache.axis2.databinding.ADBException;
import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;


final class RetrieveTestData {

  private static final transient Logger logger = Logger.getLogger(RetrieveTestData.class);

  private static final CombinedConfiguration config = new CombinedConfiguration();

  private static final String CONFIG_FILENAME = new String("rtd.config.dev.xml");

  private static final String OUTPUT_FILENAME = new String("testdata.xml");

  private static final String APPLICATION_CONFIG_ROOT = new String("applicationConfig.rtdConfig");

  private static final String EODDATA_PROVIDER_NAME = new String("eoddata");

  private static final String TEST_EXCHANGE = new String("WCE");

  private static final String TEST_SYMBOL = new String("RSF12");

  private static final String XML_NAMESPACE_URI = new String("http://www.jsrsolutions.co.nz/ds3/testdata");

  private static final String XML_PREFIX = new String("td");

  private static final QName EXCHANGE_QNAME = new QName(XML_NAMESPACE_URI, "exchange", XML_PREFIX);

  private static final QName SYMBOL_QNAME = new QName(XML_NAMESPACE_URI, "symbol", XML_PREFIX);

  private static final QName QUOTE_QNAME = new QName(XML_NAMESPACE_URI, "quote", XML_PREFIX);

  private static final String TESTDATA_LOCALNAME = new String("testdata");

  private static final String EXCHANGES_LOCALNAME = new String("exchanges");

  private static final String SYMBOLS_LOCALNAME = new String("symbols");

  private static final String QUOTES_LOCALNAME = new String("quotes");

  private static final String NEWLINE = System.getProperty("line.separator");

  private static final int HISTORY_YEAR_OFFSET = -3;

  private static final int HISTORY_MONTH_OFFSET = -3;

  private static final String DEFAULT_FREQUENCY = new String("d");

  private RetrieveTestData() {

  }

  public static void main(String[] args) {

    logger.info("Starting application [ rtd ] ..."); 

    try {

	    config.addConfiguration(new XMLConfiguration(RetrieveTestData.class.getClassLoader().getResource(CONFIG_FILENAME)));

	    HierarchicalConfiguration appConfig = config.configurationAt(APPLICATION_CONFIG_ROOT);

	    EodDataProvider eodDataProvider = null;

      OutputStream out = new FileOutputStream(OUTPUT_FILENAME);
      XMLOutputFactory factory = XMLOutputFactory.newInstance();
      XMLStreamWriter writer = factory.createXMLStreamWriter(out);

      eodDataProvider = EodDataProviderFactory.create(appConfig, EODDATA_PROVIDER_NAME);

      writer.writeStartDocument("utf-8", "1.0");
      writer.writeCharacters(NEWLINE);
      writer.writeComment("Test data for running DataScraper unit tests against");
      writer.writeCharacters(NEWLINE);

      writer.setPrefix(XML_PREFIX, XML_NAMESPACE_URI);

      writer.writeStartElement(XML_NAMESPACE_URI, TESTDATA_LOCALNAME);
      writer.writeNamespace(XML_PREFIX, XML_NAMESPACE_URI);
      writer.writeCharacters(NEWLINE);

      serializeExchanges(eodDataProvider, writer);
      serializeSymbols(eodDataProvider, writer);
      serializeQuotes(eodDataProvider, writer);

      writer.writeEndElement();
      writer.writeCharacters(NEWLINE);
      writer.writeEndDocument();
      writer.flush();
      writer.close();


    }
    catch(ConfigurationException ce) {

	    logger.error(ce.toString()); 
	    ce.printStackTrace();

    }
    catch(Exception e) {

	    logger.error(e.toString()); 
	    e.printStackTrace();

    }

    logger.info("Exiting application [ rtd ] ...");

  }


  private static void serialize(ADBBean[] beanArray, QName qname, XMLStreamWriter writer) {

    for (ADBBean bean : beanArray) {

      try {
        bean.serialize(qname, writer);
        writer.writeCharacters(NEWLINE);
      }
      catch (XMLStreamException xse) {
        logger.error(xse.toString());
        xse.printStackTrace();
      }

    }

  }

  private static void serializeExchanges(EodDataProvider eodDataProvider, XMLStreamWriter writer) {

    try {
      writer.writeStartElement(XML_NAMESPACE_URI, EXCHANGES_LOCALNAME);
      writer.writeCharacters(NEWLINE);
      serialize(eodDataProvider.getExchanges(), EXCHANGE_QNAME, writer);
      writer.writeEndElement();
      writer.writeCharacters(NEWLINE);
    }
    catch (XMLStreamException xse) {
      logger.error(xse.toString());
      xse.printStackTrace();
    }
    catch(EodDataProviderException edpe) {

      logger.error(edpe.toString());
      edpe.printStackTrace();

    }
    finally {

      if (eodDataProvider != null) {

        // eodDataProvider.close();

      }
          
    }


  }

  private static void serializeSymbols(EodDataProvider eodDataProvider, XMLStreamWriter writer) {

    try {
      writer.writeStartElement(XML_NAMESPACE_URI, SYMBOLS_LOCALNAME);
      writer.writeCharacters(NEWLINE);
      serialize(eodDataProvider.getSymbols(TEST_EXCHANGE), SYMBOL_QNAME, writer);
      writer.writeEndElement();
      writer.writeCharacters(NEWLINE);
    }
    catch (XMLStreamException xse) {
      logger.error(xse.toString());
      xse.printStackTrace();
    }
    catch(EodDataProviderException edpe) {

      logger.error(edpe.toString());
      edpe.printStackTrace();

    }
    finally {

      if (eodDataProvider != null) {

        // eodDataProvider.close();

      }
          
    }


  }

  private static void serializeQuotes(EodDataProvider eodDataProvider, XMLStreamWriter writer) {

    try {
      writer.writeStartElement(XML_NAMESPACE_URI, QUOTES_LOCALNAME);
      writer.writeCharacters(NEWLINE);

      Calendar startCalendar = Calendar.getInstance();
      startCalendar.add(Calendar.YEAR, HISTORY_YEAR_OFFSET);
      //    startCalendar.add(Calendar.MONTH, HISTORY_MONTH_OFFSET);

      Calendar endCalendar = Calendar.getInstance();

      serialize(eodDataProvider.getQuotes(TEST_EXCHANGE, TEST_SYMBOL, startCalendar, endCalendar, DEFAULT_FREQUENCY), QUOTE_QNAME, writer);
      writer.writeEndElement();
      writer.writeCharacters(NEWLINE);
    }
    catch (XMLStreamException xse) {
      logger.error(xse.toString());
      xse.printStackTrace();
    }
    catch(EodDataProviderException edpe) {

      logger.error(edpe.toString());
      edpe.printStackTrace();

    }
    finally {

      if (eodDataProvider != null) {

        // eodDataProvider.close();

      }
          
    }


  }

}

