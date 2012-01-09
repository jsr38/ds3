/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)EodDataSink.java        
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

import nz.co.jsrsolutions.ds3.DataStub.EXCHANGE;
import nz.co.jsrsolutions.ds3.DataStub.SYMBOL;
import nz.co.jsrsolutions.ds3.DataStub.QUOTE;

/** 
 * 
 * Interface that allows EOD Data to be written to.
 * 
 */ 
public interface EodDataSink {

  /** 
   * 
   * Writes the supplied exchange data to the target where possible.
   * 
   * 
   * @param       the array of exchange data
   * @return      
   * @exception   EodDataSinkException
   *              If the exchange data is unable to be written 
   */ 
  public void updateExchanges(EXCHANGE[] exchanges) throws EodDataSinkException;

  /** 
   * 
   * Writes the supplied exchange data to the target where possible.
   * 
   * 
   * @param       the array of exchange data
   * @return      
   * @exception   EodDataSinkException
   *              If the exchange data is unable to be written 
   */ 
  public void updateExchangeSymbols(String exchange,
                                    SYMBOL[] symbols) throws EodDataSinkException;

  /** 
   * 
   * Writes the supplied exchange data to the target where possible.
   * 
   * 
   * @param       the array of exchange data
   * @return      
   * @exception   EodDataSinkException
   *              If the exchange data is unable to be written 
   */
  public void updateQuotes(String exchange,
                           String symbol,
                           String startDate,
                           String endDate,
                           String period,
                           QUOTE[] quotes) throws EodDataSinkException;

  /** 
   * 
   * Writes the supplied exchange data to the target where possible.
   * 
   * 
   * @param       the array of exchange data
   * @return      
   * @exception   EodDataSinkException
   *              If the exchange data is unable to be written 
   */ 
  public void updateExchangeSymbolQuotes(String exchange,
                                         String symbol,
                                         QUOTE[] quotes) throws EodDataSinkException;

  /** 
   * 
   * Writes the supplied exchange data to the target where possible.
   * 
   * 
   * @param       the array of exchange data
   * @return      
   * @exception   EodDataSinkException
   *              If the exchange data is unable to be written 
   */
  public void close() throws EodDataSinkException;

}