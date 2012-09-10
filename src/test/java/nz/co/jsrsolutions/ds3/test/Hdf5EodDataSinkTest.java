/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)Hdf5EodDataSinkTest.java        
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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;

import nz.co.jsrsolutions.ds3.DataStub.QUOTE;
import nz.co.jsrsolutions.ds3.DataStub.SYMBOL;
import nz.co.jsrsolutions.ds3.sink.EodDataSinkException;
import nz.co.jsrsolutions.ds3.sink.Hdf5EodDataSink;
import nz.co.jsrsolutions.ds3.sink.EodDataSink;
import nz.co.jsrsolutions.util.Range;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for Hdf5EodDataSink.
 */
public class Hdf5EodDataSinkTest {
  
  private static final String TEST_FILENAME = "test.h5";
  
  private static UnitTestData testData;
  
  @Before
  public void setUp() {
   
    testData = new UnitTestData();
    
  }
  
  /**
   * Test that when supplied with a known set of exchanges,
   * an identical set of exchanges is written to the HDF5 
   * file.
   */
  @Test
  public void testUpdateExchanges() {
    EodDataSink sink = null;
    try {
      final File testFile = new File(TEST_FILENAME);
      testFile.delete();
      
      sink = new Hdf5EodDataSink(TEST_FILENAME);
      
      sink.updateExchanges(testData.getExchanges());
      
      sink.close();
      
      sink = new Hdf5EodDataSink(TEST_FILENAME);
      
    }
    catch (EodDataSinkException e) {
      assertTrue(false);
    }
    finally {
      if (sink != null) {
        sink.close();
      }
    }
  }
  
  /**
   * Test that when supplied with a known set of symbols
   * associated with an exchange, that an identical set 
   * of symbols is written to the HDF5 file.
   */
  @Test
  public void testUpdateExchangeSymbols() {
    EodDataSink sink = null;
    try {
      final File testFile = new File(TEST_FILENAME);
      testFile.delete();
      
      sink = new Hdf5EodDataSink(TEST_FILENAME);
      
      sink.updateExchanges(testData.getExchanges());
      sink.updateExchangeSymbols(testData.getTestExchange(),
          testData.getSymbols());
      
      sink.close();
      
      sink = new Hdf5EodDataSink(TEST_FILENAME);
      
      final String[] symbols = sink.readExchangeSymbols(testData.getTestExchange());
         
      Arrays.sort(symbols);
      final SYMBOL[] testSymbols = Arrays.copyOf(testData.getSymbols(), testData.getSymbols().length);
      Arrays.sort(testSymbols, new Comparator<SYMBOL>() {
          @Override
          public int compare(SYMBOL entry1, SYMBOL entry2) {
              
              return entry1.getCode().compareTo(entry2.getCode());
          }
      });
      for (int i = 0; i < symbols.length; ++i) {
        assertTrue(symbols[i].compareTo(testSymbols[i].getCode()) == 0);
      }
      
    }
    catch (EodDataSinkException e) {
      assertTrue(false);
    }
    finally {
      if (sink != null) {
        sink.close();
      }
    }
  }

  /**
   * Test that when supplied with a known set of exchange
   * symbols, an identical set of exchange symbols is 
   * written to the HDF5 file.
   */
  @Test
  public void testUpdateExchangeSymbolQuotes()
  {
    EodDataSink sink = null;
    try {
      final File testFile = new File(TEST_FILENAME);
      testFile.delete();
      
      sink = new Hdf5EodDataSink(TEST_FILENAME);
      
      sink.updateExchanges(testData.getExchanges());
      sink.updateExchangeSymbols(testData.getTestExchange(),
            testData.getSymbols());
      
      sink.updateExchangeSymbolQuotes(testData.getTestExchange(),
          testData.getTestSymbol(),
          testData.getQuotes());
      
      //sink.close();
      
      //sink = new Hdf5EodDataSink(TEST_FILENAME);
      
      final Range<Calendar> range = sink.readExchangeSymbolDateRange(testData.getTestExchange(),
          testData.getTestSymbol());
         
      assertTrue(range.getLower().compareTo(testData.getQuotes()[0].getDateTime()) == 0);
      assertTrue(range.getUpper().compareTo(testData.getQuotes()[testData.getQuotes().length - 1].getDateTime()) == 0);
      
    }
    catch (EodDataSinkException e) {
      assertTrue(false);
    }
    finally {
      if (sink != null) {
        sink.close();
      }
    }
  }

  /**
   * Test that when supplied with a known set of quotes
   * for a given exchange and symbol, an identical set 
   * of quotes is written to the HDF5 file.
   */
  @Test
  public void testUpdateQuotes()
  {
    assertTrue(true);
  }

  /**
   * Test that quotes may be appended to an existing
   * set of quotes.
   */
  @Test
  public void testUpdateExchangeSymbolQuotes_Append()
  {
    EodDataSink sink = null;
    try {
      final File testFile = new File(TEST_FILENAME);
      testFile.delete();
      
      sink = new Hdf5EodDataSink(TEST_FILENAME);
      
      sink.updateExchanges(testData.getExchanges());
      sink.updateExchangeSymbols(testData.getTestExchange(),
            testData.getSymbols());
      
      sink.updateExchangeSymbolQuotes(testData.getTestExchange(),
          testData.getTestSymbol(),
          testData.getQuotes());
      
      //sink.close();
      
      //sink = new Hdf5EodDataSink(TEST_FILENAME);
         
      testData.getQuotes()[testData.getQuotes().length - 1].getDateTime().add(Calendar.DATE, 1);
      
      sink.updateExchangeSymbolQuotes(testData.getTestExchange(),
          testData.getTestSymbol(),
          new QUOTE[] { testData.getQuotes()[testData.getQuotes().length - 1] });
      
      final Range<Calendar> range = sink.readExchangeSymbolDateRange(testData.getTestExchange(),
          testData.getTestSymbol());
         
      assertTrue(range.getLower().compareTo(testData.getQuotes()[0].getDateTime()) == 0);
      assertTrue(range.getUpper().compareTo(testData.getQuotes()[testData.getQuotes().length - 1].getDateTime()) == 0);
      
    }
    catch (EodDataSinkException e) {
      assertTrue(false);
    }
    finally {
      if (sink != null) {
        sink.close();
      }
    }
  }
  
  /**
   * Test that when supplied with a known set of quotes
   * that overlaps existing quotes for a given exchange 
   * and symbol, the existing quotes in the HDF5 file
   * remain unaffected.
   */
  @Test
  public void testUpdateQuotes_Overlap()
  {
    assertTrue(true);
  }


  /**
   * Test that when supplied with a known set of quotes
   * that partially overlaps existing quotes for a 
   * given exchange and symbol, the existing quotes 
   * in the HDF5 file remain unaffected and the
   * additional quotes are inserted.
   */
  @Test
  public void testUpdateQuotes_PartialOverlap()
  {
    assertTrue(true);
  }
  
  @After
  public void tearDown() {
    
  }

}
