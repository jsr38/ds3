/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)ListExchangesCommand.java        
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

package nz.co.jsrsolutions.ds3.command;

import nz.co.jsrsolutions.ds3.DataStub.EXCHANGE;
import nz.co.jsrsolutions.ds3.provider.EodDataProvider;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

public class ListExchangesCommand implements Command {

  private static final transient Logger logger = Logger.getLogger(ListExchangesCommand.class);

  private static final String NEWLINE = System.getProperty("line.separator");

  public boolean execute(Context context) throws Exception {

    logger.info("Executing: listexchanges");

    EodDataProvider eodDataProvider = (EodDataProvider)context.get(CommandContext.EODDATAPROVIDER_KEY);

    EXCHANGE[] exchanges = eodDataProvider.getExchanges();

    for (EXCHANGE exchange : exchanges) {

      if (logger.isInfoEnabled()) {

        StringBuffer logMessageBuffer = new StringBuffer();
        logMessageBuffer.append(NEWLINE);

        logMessageBuffer.append(" Code:               [ ");
        logMessageBuffer.append(exchange.getCode());
        logMessageBuffer.append(" ]");
        logMessageBuffer.append(NEWLINE);

        logMessageBuffer.append(" Name:               [ ");
        logMessageBuffer.append(exchange.getName());
        logMessageBuffer.append(" ]");
        logMessageBuffer.append(NEWLINE);

        logMessageBuffer.append(" Country:            [ ");
        logMessageBuffer.append(exchange.getCountry());
        logMessageBuffer.append(" ] ");
        logMessageBuffer.append(NEWLINE);

        logMessageBuffer.append(" TimeZone:           [ ");
        logMessageBuffer.append(exchange.getTimeZone());
        logMessageBuffer.append(" ] ");
        logMessageBuffer.append(NEWLINE);

        logMessageBuffer.append(" IsIntraday:         [ ");
        logMessageBuffer.append(exchange.getIsIntraday());
        logMessageBuffer.append(" ] ");
        logMessageBuffer.append(NEWLINE);

        logMessageBuffer.append(" IntradayStartDate:  [ ");
        logMessageBuffer.append(exchange.getIntradayStartDate().getTime());
        logMessageBuffer.append(" ] ");
        logMessageBuffer.append(NEWLINE);

        logMessageBuffer.append(" HasIntradayProduct: [ ");
        logMessageBuffer.append(exchange.getHasIntradayProduct());
        logMessageBuffer.append(" ] ");
        logMessageBuffer.append(NEWLINE);

        logger.info(logMessageBuffer.toString());

      }
    }


    return false;

  }
}