/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)EodDataProvider.java        
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

/** 
 * 
 * Interface implemented by providers of EOD Data
 * 
 */ 
public interface EodDataProvider {

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
  public EXCHANGE[] getExchanges() throws EodDataProviderException;

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
  public int getExchangeMonths(String exchange) throws EodDataProviderException;

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
  public SYMBOL[] getSymbols(String exchange) throws EodDataProviderException;

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
  public QUOTE[] getQuotes(String exchange,
                           String symbol,
                           Calendar startCalendar,
                           Calendar endCalendar,
                           String period) throws EodDataProviderException;

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
  public QUOTE[] getQuotes(String exchange) throws EodDataProviderException;


}