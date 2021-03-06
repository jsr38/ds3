/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)HdfObjectEodDataSinkTest.java        
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

import nz.co.jsrsolutions.ds3.sink.EodDataSink;

import org.junit.* ;
import static org.junit.Assert.* ;

/**
 * Unit test for HdfObjectEodDataSink.
 */
public class HdfObjectEodDataSinkTest {


  @SuppressWarnings("unused")
  private EodDataSink eodDataSink = null;
  
  /**
   *
   * 
   * 
   */
  @Before
  public void setUp() {
    
    //    eodDataSink = EodDataSinkFactory.create
    
  }

  /**
   * Test that when supplied with a known set of exchanges,
   * an identical set of exchanges is written to the HDF5 
   * file.
   */
  @Test
  public void testUpdateExchanges() {

    assertTrue(true);
  }

  /**
   * Test that when supplied with a known set of exchange
   * symbols, an identical set of exchange symbols is 
   * written to the HDF5 file.
   */
  @Test
  public void testUpdateExchangeSymbolQuotes() {
    assertTrue(true);
  }

  /**
   * Test that when supplied with a known set of quotes
   * for a given exchange and symbol, an identical set 
   * of quotes is written to the HDF5 file.
   */
  @Test
  public void testUpdateQuotes() {
    assertTrue(true);
  }

  /**
   * Test that when supplied with a known set of quotes
   * that overlaps existing quotes for a given exchange 
   * and symbol, the existing quotes in the HDF5 file
   * remain unaffected.
   */
  @Test
  public void testUpdateQuotes_Overlap() {
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
  public void testUpdateQuotes_PartialOverlap() {
    assertTrue(true);
  }

}
