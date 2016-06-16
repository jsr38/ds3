/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)CMEEodDataProvider.java        
 *
 * Copyright (c) 2013 Argusat Limited
 * 10 Underwood Road, Southampton. UK
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * Argusat Limited. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Argusat Limited
 */

package nz.co.jsrsolutions.ds3.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import nz.co.jsrsolutions.ds3.DataStub.QUOTE;
import nz.co.jsrsolutions.ds3.DataStub.SYMBOL;

public class CMEEodDataProviderCsvParser {

	private Vector<SYMBOL> _symbols;

	private Vector<QUOTE> _quotes;

	public CMEEodDataProviderCsvParser() {

		_symbols = new Vector<SYMBOL>();
		_quotes = new Vector<QUOTE>();

	}

	public void parse(InputStream inputStream) throws EodDataProviderException {

		String line;
		BufferedReader br;
		@SuppressWarnings("unused")
		int lineNumber = 1;  // skip the header
		try {
			br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

			line = br.readLine(); // skip the first line (header)
			
			while ((line = br.readLine()) != null) {
				++lineNumber;
				// use comma as separator
				String[] columns = line.split(",");
				
				SYMBOL symbol = new SYMBOL();
				symbol.setCode(columns[4]); // CONTRACT
				symbol.setName(columns[4]); // CONTRACT
				symbol.setLongName(columns[5]); // PRODUCT DESCRIPTION
				
				_symbols.add(symbol);

				// if there is no settlement for this contract
				// then ignore this quote
				if (columns[13].isEmpty()) {
					continue;
				}
				
				QUOTE quote = new QUOTE();
				quote.setSymbol(columns[4]); // CONTRACT
				quote.setName(columns[4]); // CONTRACT
				quote.setDescription(columns[5]); // PRODUCT DESCRIPTION

				if (columns[6].isEmpty()) {
					quote.setOpen(Double.parseDouble(columns[13])); // SETTLE(?)
				} else {
					quote.setOpen(Double.parseDouble(columns[6])); // OPEN
				}

				if (columns[11].isEmpty()) {
					quote.setClose(Double.parseDouble(columns[13])); // SETTLE(?)
				} else {
					quote.setClose(Double.parseDouble(columns[11])); // CLOSE
				}

				String high = columns[7];
				String low = columns[9];
				if (!high.isEmpty()) {
					quote.setHigh(Double.parseDouble(high));
				}
				if (!low.isEmpty()) {
					quote.setLow(Double.parseDouble(low));
				}

				quote.setVolume(Long.parseLong(columns[15]));
				if (columns[14].equals("UNCH")) {
					quote.setChange(0.0);
				} else {
					quote.setChange(Double.parseDouble(columns[14]));
				}

				quote.setOpenInterest(Long.parseLong(columns[15]));

				DateTimeFormatter formatter = DateTimeFormat
						.forPattern("MM/dd/yyyy");
				DateTime dt = formatter.parseDateTime(columns[19]);
				quote.setDateTime(dt.toGregorianCalendar());

				
				_quotes.add(quote);

			}
		} catch (UnsupportedEncodingException e) {
			throw new EodDataProviderException(e);
		} catch (IOException e) {
			throw new EodDataProviderException(e);
		}

	}

	public Vector<SYMBOL> getSymbols() {
		return _symbols;
	}

	public Vector<QUOTE> getQuotes() {
		return _quotes;
	}

}
