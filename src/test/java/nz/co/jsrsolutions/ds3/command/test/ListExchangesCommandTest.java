/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)ListExchangesCommandTest.java        
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

package nz.co.jsrsolutions.ds3.command.test;

import static org.junit.Assert.fail;
import nz.co.jsrsolutions.ds3.sink.EodDataSink;
import nz.co.jsrsolutions.ds3.sink.test.EodDataSinkEmptyMock;
import nz.co.jsrsolutions.ds3.test.UnitTestData;
import nz.co.jsrsolutions.ds3.command.CommandContext;
import nz.co.jsrsolutions.ds3.command.ListExchangesCommand;
import nz.co.jsrsolutions.ds3.provider.EodDataProvider;
import nz.co.jsrsolutions.ds3.provider.test.EodDataProviderMock;

import org.apache.commons.chain.Command;
import org.junit.Test;

/**
 * Unit test for ListExchangesCommand.
 */
public class ListExchangesCommandTest {

  /**
   * Test that when supplied with a known set of exchanges,
   * an identical set of exchanges is written to the HDF5 
   * file.
   */
  @Test
  public void testListExchangesCommand() {

    try {
      
      UnitTestData testData = new UnitTestData();
      
      EodDataProvider eodDataProvider = new EodDataProviderMock(testData);
      EodDataSink eodDataSink = new EodDataSinkEmptyMock(testData);

      CommandContext context = new CommandContext();
    
      context.put(CommandContext.EODDATAPROVIDER_KEY, eodDataProvider);
      context.put(CommandContext.EODDATASINK_KEY, eodDataSink);

      context.put(CommandContext.EXCHANGE_KEY, testData.getTestExchange());
      context.put(CommandContext.SYMBOL_KEY, testData.getTestSymbol());

      Command command = new ListExchangesCommand();
      command.execute(context);

    } catch (Exception e) {

      fail(e.getMessage());

    }

  }

}