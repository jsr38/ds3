/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)EodDataSinkContainedByAvailableRangeMock.java        
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.Calendar;

import nz.co.jsrsolutions.ds3.DataStub.QUOTE;
import nz.co.jsrsolutions.ds3.sink.EodDataSinkException;
import nz.co.jsrsolutions.ds3.test.UnitTestData;
import nz.co.jsrsolutions.util.Range;

public class EodDataSinkFirstElementLessThanOneDayAfterFirstAvailable extends
		EodDataSinkMock {

	private static final int LOWER_INDEX_OFFSET = 10;

	private static final int UPPER_INDEX_OFFSET = -11;

	private final int minElements = LOWER_INDEX_OFFSET - UPPER_INDEX_OFFSET;

	public EodDataSinkFirstElementLessThanOneDayAfterFirstAvailable(
			UnitTestData testData) throws EodDataSinkException {
		super(testData);

	}

	public void updateExchangeSymbolQuotes(String exchange, String symbol,
			QUOTE[] quotes) throws EodDataSinkException {

		if (quotes == null) {
			throw new EodDataSinkException("Invalid quote vector.");
		}

		final QUOTE[] testQuotes = this.testData.getQuotes();
		final Range<Calendar> existingRange = new Range<Calendar>(
				testQuotes[LOWER_INDEX_OFFSET].getDateTime(),
				testQuotes[testQuotes.length + UPPER_INDEX_OFFSET]
						.getDateTime());

		final Range<Calendar> newRange = new Range<Calendar>(
				quotes[0].getDateTime(),
				quotes[quotes.length - 1].getDateTime());

		assertFalse("Quote vector overlaps existing quotes",
				existingRange.overlapsRange(newRange));

	}

	@Override
	public Range<Calendar> readExchangeSymbolDateRange(String exchange,
			String symbol) {

		final QUOTE[] quotes = this.testData.getQuotes();

		if (quotes.length < minElements) {

			final StringBuffer message = new StringBuffer();
			message.append("Test data quote vector must contain at least ");
			message.append(minElements);
			message.append(" elements");
			fail(message.toString());

		}

		final Calendar lower = quotes[LOWER_INDEX_OFFSET].getDateTime();
		final Calendar upper = quotes[quotes.length + UPPER_INDEX_OFFSET]
				.getDateTime();

		final Range<Calendar> range = new Range<Calendar>(lower, upper);

		return range;

	}

}
