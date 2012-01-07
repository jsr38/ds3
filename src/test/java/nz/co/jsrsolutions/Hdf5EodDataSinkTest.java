/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

package nz.co.jsrsolutions;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for Hdf5EodDataSink.
 */
public class Hdf5EodDataSinkTest 
  extends TestCase
{
  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public Hdf5EodDataSinkTest(String testName)
  {
    super(testName);
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite()
  {
    return new TestSuite( AppTest.class );
  }

  /**
   * Test that when supplied with a known set of exchanges,
   * an identical set of exchanges is written to the HDF5 
   * file.
   */
  public void testUpdateExchanges()
  {
    assertTrue(false);
  }

  /**
   * Test that when supplied with a known set of exchange
   * symbols, an identical set of exchange symbols is 
   * written to the HDF5 file.
   */
  public void testUpdateExchangeSymbolQuotes()
  {
    assertTrue(false);
  }

  /**
   * Test that when supplied with a known set of quotes
   * for a given exchange and symbol, an identical set 
   * of quotes is written to the HDF5 file.
   */
  public void testUpdateQuotes()
  {
    assertTrue(false);
  }

  /**
   * Test that when supplied with a known set of quotes
   * that overlaps existing quotes for a given exchange 
   * and symbol, the existing quotes in the HDF5 file
   * remain unaffected.
   */
  public void testUpdateQuotes_Overlap()
  {
    assertTrue(false);
  }


  /**
   * Test that when supplied with a known set of quotes
   * that partially overlaps existing quotes for a 
   * given exchange and symbol, the existing quotes 
   * in the HDF5 file remain unaffected and the
   * additional quotes are inserted.
   */
  public void testUpdateQuotes_PartialOverlap()
  {
    assertTrue(false);
  }

}
