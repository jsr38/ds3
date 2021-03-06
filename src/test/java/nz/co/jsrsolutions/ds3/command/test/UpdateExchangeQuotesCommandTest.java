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

package nz.co.jsrsolutions.ds3.command.test;

import static org.junit.Assert.fail;

import java.util.concurrent.Executors;

import nz.co.jsrsolutions.ds3.sink.EodDataSink;
import nz.co.jsrsolutions.ds3.sink.test.EodDataSinkEmptyMock;
import nz.co.jsrsolutions.ds3.sink.test.EodDataSinkFirstElementLessThanOneDayAfterFirstAvailable;
import nz.co.jsrsolutions.ds3.test.EmailServiceMock;
import nz.co.jsrsolutions.ds3.test.UnitTestData;
import nz.co.jsrsolutions.ds3.command.CommandContext;
import nz.co.jsrsolutions.ds3.command.UpdateExchangeQuotesCommand;
import nz.co.jsrsolutions.ds3.provider.EodDataProvider;
import nz.co.jsrsolutions.ds3.provider.test.EodDataProviderMock;
import nz.co.jsrsolutions.ds3.provider.test.EodDataProviderOneDayAvailableMock;
import nz.co.jsrsolutions.util.EmailService;

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
	public void testUpdateExchangeQuotesCommand_EmptySink() {

		try {

			UnitTestData testData = new UnitTestData();

			EodDataProvider eodDataProvider = new EodDataProviderMock(testData);
			EodDataSink eodDataSink = new EodDataSinkEmptyMock(testData);
			EmailService emailService = new EmailServiceMock();

			CommandContext context = new CommandContext();

			// context.put(CommandContext.EODDATAPROVIDER_KEY, eodDataProvider);
			// context.put(CommandContext.EODDATASINK_KEY, eodDataSink);

			context.put(CommandContext.EXCHANGE_KEY, testData.getTestExchange());
			context.put(CommandContext.SYMBOL_KEY, testData.getTestSymbol());

			Command command = new UpdateExchangeQuotesCommand(
					Executors.newSingleThreadExecutor(), eodDataProvider,
					eodDataSink, emailService);

			command.execute(context);

		} catch (Throwable t) {

			t.printStackTrace();
			fail(t.getCause().toString());

		}

	}

	/**
	 * Test
	 */
	@Test
	public void testUpdateExchangeQuotesCommand_OneDayOrLessAvailable() {

		try {

			UnitTestData testData = new UnitTestData();

			EodDataProvider eodDataProvider = new EodDataProviderOneDayAvailableMock(
					testData);
			EodDataSink eodDataSink = new EodDataSinkFirstElementLessThanOneDayAfterFirstAvailable(
					testData);
			EmailService emailService = new EmailServiceMock();

			CommandContext context = new CommandContext();

			// context.put(CommandContext.EODDATAPROVIDER_KEY, eodDataProvider);
			// context.put(CommandContext.EODDATASINK_KEY, eodDataSink);

			context.put(CommandContext.EXCHANGE_KEY, testData.getTestExchange());
			context.put(CommandContext.SYMBOL_KEY, testData.getTestSymbol());

			Command command = new UpdateExchangeQuotesCommand(
					Executors.newSingleThreadExecutor(), eodDataProvider,
					eodDataSink, emailService);

			command.execute(context);

		} catch (Throwable t) {

			t.printStackTrace();
			fail(t.getCause().getMessage());

		}

	}

}
