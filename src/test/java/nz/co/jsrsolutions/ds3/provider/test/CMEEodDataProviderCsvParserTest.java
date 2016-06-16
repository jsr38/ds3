/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)CMEEodDataProviderCsvParserTest.java        
 *
 * Copyright (c) 2012 Argusat Limited
 * 10 Underwood Road, Southampton. UK
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Argusat Limited. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Argusat Limited.
 */

package nz.co.jsrsolutions.ds3.provider.test;

import static org.junit.Assert.*;

import java.io.InputStream;

import nz.co.jsrsolutions.ds3.provider.CMEEodDataProviderCsvParser;
import nz.co.jsrsolutions.ds3.provider.EodDataProviderException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CMEEodDataProviderCsvParserTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testParseCsv() throws EodDataProviderException {

		CMEEodDataProviderCsvParser parser = new CMEEodDataProviderCsvParser();

		InputStream inputStream = CMEEodDataProviderCsvParserTest.class
				.getClassLoader().getResourceAsStream("nymex_future.csv");
		
		parser.parse(inputStream);
		
		assertEquals(11504, parser.getSymbols().size());
		assertEquals(11443, parser.getQuotes().size());

	}

}
