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
   * Test that when supplied with a valid non-null 
   * ordered pair of Calendar objects, the constructor
   * returns a valid Range object.
   */
  @Test
  public void testConstructRetrieveCalendarRange() {

    Calendar yesterday = Calendar.getInstance();
    yesterday.add(Calendar.DATE, -1);

    Calendar now = Calendar.getInstance();
    
    Range<Calendar> dateRange = new Range<Calendar>(yesterday, now);

    assertEquals(yesterday, dateRange.getLower());
    assertEquals(now, dateRange.getUpper());
  }


  /**
   * Test that when supplied with a invalid non-null 
   * ordered pair of Calendar objects, the constructor
   * throws an IllegalArgumentExcetion.
   */
  @Test(expected=IllegalArgumentException.class)
  public void testExceptionLowerGreaterThanUpperCalendarRange() {

    Calendar yesterday = Calendar.getInstance();
    yesterday.add(Calendar.DATE, -1);

    Calendar now = Calendar.getInstance();
    
    @SuppressWarnings("unused")
    Range<Calendar> dateRange = new Range<Calendar>(now, yesterday);
  }
  
  /**
   * Test that when supplied with a valid non-null Range, that
   * this Range contains, the containsRange() method returns
   * true.
   */
  @Test
  public void testContainsRangeTrue() {

    Calendar yesterday = Calendar.getInstance();
    Calendar dayBeforeYesterday = Calendar.getInstance();
    yesterday.add(Calendar.DATE, -1);
    dayBeforeYesterday.add(Calendar.DATE, -2);
    
    Calendar now = Calendar.getInstance();
    
    Range<Calendar> firstRange = new Range<Calendar>(dayBeforeYesterday, now);
    Range<Calendar> secondRange = new Range<Calendar>(yesterday, now);
    
    assertTrue("containsRange should have returned true", firstRange.containsRange(secondRange));
  }
  
  /**
   * Test that when supplied with a valid non-null Range, that
   * this Range doesn't contain, the containsRange() method returns
   * false.
   */
  @Test
  public void testContainsRangeFalse() {

    Calendar yesterday = Calendar.getInstance();
    Calendar dayBeforeYesterday = Calendar.getInstance();
    Calendar threeDaysAgo = Calendar.getInstance();
    
    yesterday.add(Calendar.DATE, -1);
    dayBeforeYesterday.add(Calendar.DATE, -2);
    threeDaysAgo.add(Calendar.DATE, -3);
    
    Calendar now = Calendar.getInstance();
    
    Range<Calendar> firstRange = new Range<Calendar>(dayBeforeYesterday, now);
    Range<Calendar> secondRange = new Range<Calendar>(threeDaysAgo, dayBeforeYesterday);
    
    assertFalse("containsRange should have returned false", firstRange.containsRange(secondRange));
  }
  
  /**
   * Test that when supplied with a valid non-null Range, that
   * this Range overlaps, the overlapsRange() method returns
   * true.
   */
  @Test
  public void testOverlapsRangeTrue() {

    Calendar tomorrow = Calendar.getInstance();
    Calendar now = Calendar.getInstance();
    Calendar yesterday = Calendar.getInstance();
    Calendar dayBeforeYesterday = Calendar.getInstance();
    
    tomorrow.add(Calendar.DATE, 1);
    yesterday.add(Calendar.DATE, -1);
    dayBeforeYesterday.add(Calendar.DATE, -2);
    
    Range<Calendar> firstRange = new Range<Calendar>(now, tomorrow);
    Range<Calendar> secondRange = new Range<Calendar>(dayBeforeYesterday, now);
    
    assertTrue("overlapsRange should have returned true", firstRange.overlapsRange(secondRange));
  }
  
  /**
   * Test that when supplied with a valid non-null Range, that
   * this Range doesn't overlap, the overlapsRange() method returns
   * false.
   */
  @Test
  public void testOverlapsRangeFalse() {

    
    Calendar tomorrow = Calendar.getInstance();
    Calendar now = Calendar.getInstance();
    Calendar yesterday = Calendar.getInstance();
    Calendar dayBeforeYesterday = Calendar.getInstance();
    Calendar threeDaysAgo = Calendar.getInstance();
    
    tomorrow.add(Calendar.DATE, 1);
    yesterday.add(Calendar.DATE, -1);
    dayBeforeYesterday.add(Calendar.DATE, -2);
    threeDaysAgo.add(Calendar.DATE, -3);
    
    Range<Calendar> firstRange = new Range<Calendar>(now, tomorrow);
    Range<Calendar> secondRange = new Range<Calendar>(threeDaysAgo, dayBeforeYesterday);
    
    assertFalse("overlapsRange should have returned false", firstRange.overlapsRange(secondRange));
  }

}
