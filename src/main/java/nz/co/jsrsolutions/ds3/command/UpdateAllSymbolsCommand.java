/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)UpdateAllSymbolsCommand.java        
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

import nz.co.jsrsolutions.ds3.EodDataProvider;
import nz.co.jsrsolutions.ds3.EodDataSink;
import nz.co.jsrsolutions.ds3.DataStub.EXCHANGE;
import nz.co.jsrsolutions.ds3.DataStub.SYMBOL;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

public class UpdateAllSymbolsCommand implements Command {

  private static final transient Logger logger = Logger.getLogger(UpdateAllSymbolsCommand.class);

  public boolean execute(Context context) throws Exception {

    logger.info("Executing: updateallsymbols");

    EodDataProvider eodDataProvider = (EodDataProvider)context.get(CommandContext.EODDATAPROVIDER_KEY);
    EodDataSink eodDataSink = (EodDataSink)context.get(CommandContext.EODDATASINK_KEY);

    EXCHANGE[] exchanges = eodDataProvider.getExchanges();
    for (EXCHANGE exchange : exchanges) {

      SYMBOL[] symbols = eodDataProvider.getSymbols(exchange.getCode());

      eodDataSink.updateExchangeSymbols(exchange.getCode(), symbols);

    }

    return false;

  }
}

