/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)UpdateExchangeQuotesCommandTest.java        
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

import static org.junit.Assert.fail;
import nz.co.jsrsolutions.ds3.EodDataProvider;
import nz.co.jsrsolutions.ds3.EodDataSink;
import nz.co.jsrsolutions.ds3.command.CommandContext;
import nz.co.jsrsolutions.ds3.command.UpdateExchangeQuotesCommand;

import org.apache.commons.chain.Command;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for UpdateExchangeQuotesCommand.
 */
public class UpdateExchangeQuotesCommandTest {

  /**
   * Test
   */
  @Before
  public void setUp() {

    

  }

  /**
   * Test
   */
  @Test
  public void testUpdateExchangeQuotesCommandEmptySink() {

    try {

      UnitTestData testData = new UnitTestData();
      
      EodDataProvider eodDataProvider = new EodDataProviderMock(testData);
      EodDataSink eodDataSink = new EodDataSinkEmptyMock(testData);

      CommandContext context = new CommandContext();
    
      context.put(CommandContext.EODDATAPROVIDER_KEY, eodDataProvider);
      context.put(CommandContext.EODDATASINK_KEY, eodDataSink);

      context.put(CommandContext.EXCHANGE_KEY, testData.getTestExchange());
      context.put(CommandContext.SYMBOL_KEY, testData.getTestSymbol());


      Command command = new UpdateExchangeQuotesCommand();

      command.execute(context);

    }
    catch (Throwable t) {

      fail(t.toString());

    }

  }

}