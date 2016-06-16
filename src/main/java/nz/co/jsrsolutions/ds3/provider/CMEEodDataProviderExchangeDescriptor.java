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

public class CMEEodDataProviderExchangeDescriptor {

	private final String _exchange;

	private final String _filename;
	
	public CMEEodDataProviderExchangeDescriptor(final String exchange, final String filename) {
		_exchange = exchange;
		_filename = filename;
	}
	
	public String getExchange() {
		return _exchange;
	}

	public String getFilename() {
		return _filename;
	}
	
}
