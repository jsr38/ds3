/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)RangeTest.java        
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

package nz.co.jsrsolutions.util.test;

import java.util.Calendar;

import nz.co.jsrsolutions.util.Range;

import org.junit.* ;
import static org.junit.Assert.* ;



/**
 * Unit test for Range class
 */
public class RangeTest {

  
  /**
   *
   * 
   * 
   */
  @Before
  public void setUp() {
    
  }

  /**
   * Test that when supplied with a known set of exchanges,
   * an identical set of exchanges is written to the HDF5 
   * file.
   */
  @Test
  public void testConstructRetrieveRange() {

    Calendar yesterday = Calendar.getInstance();
    yesterday.add(Calendar.DATE, -1);

    Calendar now = Calendar.getInstance();
    
    Range<Calendar> dateRange = new Range<Calendar>(yesterday, now);

    assertEquals(yesterday, dateRange.getLower());
    assertEquals(now, dateRange.getUpper());
  }


  /**
   * Test that when supplied with a known set of exchanges,
   * an identical set of exchanges is written to the HDF5 
   * file.
   */
  @Test(expected=IllegalArgumentException.class)
  public void testExceptionLowerGreaterThanUpper() {

    Calendar yesterday = Calendar.getInstance();
    yesterday.add(Calendar.DATE, -1);

    Calendar now = Calendar.getInstance();
    
    @SuppressWarnings("unused")
    Range<Calendar> dateRange = new Range<Calendar>(now, yesterday);
  }

}
