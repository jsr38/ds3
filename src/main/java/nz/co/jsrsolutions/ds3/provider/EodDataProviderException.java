/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)EodDataProviderException.java        
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

package nz.co.jsrsolutions.ds3.provider;

import java.lang.String;

public class EodDataProviderException extends Exception {

	/**
   * 
   */
	private static final long serialVersionUID = -8326565082971956329L;

	private String mistake;

	public EodDataProviderException() {
		super();
		mistake = "unknown";
	}

	public EodDataProviderException(String err) {
		super(err);
		mistake = err;
	}

	public EodDataProviderException(Throwable cause) {
		super(cause);
	}

	public String getError() {
		return mistake;
	}
}
