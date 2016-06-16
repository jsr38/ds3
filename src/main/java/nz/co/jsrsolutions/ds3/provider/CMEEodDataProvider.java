/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)CMEEodDataProvider.java        
 *
 * Copyright (c) 2013 Argusat Limited
 * 10 Underwood Road, Southampton. UK
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of JSR
 * Solutions Limited. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Argusat Limited.
 */

package nz.co.jsrsolutions.ds3.provider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import nz.co.jsrsolutions.ds3.DataStub.EXCHANGE;
import nz.co.jsrsolutions.ds3.DataStub.QUOTE;
import nz.co.jsrsolutions.ds3.DataStub.SYMBOL;
import nz.co.jsrsolutions.util.Range;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

public class CMEEodDataProvider implements EodDataProvider {

	private static final transient Logger _logger = Logger
			.getLogger(CMEEodDataProvider.class);

	private final String _hostname;

	private final String _basePath;

	private final CMEEodDataProviderExchangeDescriptor[] _descriptors;

	public CMEEodDataProvider(String hostname, String basePath,
			CMEEodDataProviderExchangeDescriptor[] descriptors)
			throws EodDataProviderException {

		_hostname = hostname;
		_basePath = basePath;
		_descriptors = descriptors;

		FTPClient ftp = new FTPClient();
		FTPClientConfig config = new FTPClientConfig();
		ftp.configure(config);
		boolean error = false;
		try {
			int reply;
			ftp.connect(_hostname);
			_logger.info("Connected to " + _hostname);
			_logger.info(ftp.getReplyString());

			// After connection attempt, you should check the reply code to
			// verify
			// success.
			reply = ftp.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				_logger.error("FTP server refused connection.");
				throw new EodDataProviderException(
						"FTP server refused connection.");
			}
			
			boolean result = ftp.login("anonymous", "jsr@argusat.com");

			result = ftp.changeWorkingDirectory(_basePath);
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				_logger.error(reply);
				throw new EodDataProviderException("Failed to cd into "
						+ _basePath);
			}

			for (CMEEodDataProviderExchangeDescriptor descriptor : _descriptors) {

				OutputStream output = new ByteArrayOutputStream();

				String modificationTime = ftp.getModificationTime(descriptor
						.getFilename());

				// 213 20131202235804\r\n
				
				result = ftp.retrieveFile(descriptor.getFilename(), output);

				output.close();
			}

			ftp.logout();
		} catch (IOException e) {
			error = true;
			e.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
					// do nothing
				}
			}
		}
	}

	@Override
	public EXCHANGE[] getExchanges() throws EodDataProviderException {

		for (CMEEodDataProviderExchangeDescriptor descriptor : _descriptors) {

		}
		EXCHANGE[] exchanges = new EXCHANGE[2];
		exchanges[0].setCode("COMEX");
		exchanges[0].setName("COMEX");
		exchanges[1].setCode("NYMEX");
		exchanges[1].setCode("NYMEX");

		return null;
	}

	@Override
	public int getExchangeMonths(String exchange)
			throws EodDataProviderException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SYMBOL[] getSymbols(String exchange) throws EodDataProviderException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QUOTE[] getQuotes(String exchange, String symbol,
			Calendar startCalendar, Calendar endCalendar, String period)
			throws EodDataProviderException {

		QUOTE quote = new QUOTE();
		// quote.

		return null;
	}

	@Override
	public QUOTE[] getQuotes(String exchange) throws EodDataProviderException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Range<Calendar> getExchangeDateRange(String exchange)
			throws EodDataProviderException {
		// TODO Auto-generated method stub
		return null;
	}

}
