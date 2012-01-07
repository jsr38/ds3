/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

package nz.co.jsrsolutions.ds3;

import nz.co.jsrsolutions.ds3.DataStub.EXCHANGE;
import nz.co.jsrsolutions.ds3.DataStub.SYMBOL;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

public class UpdateExchangeSymbolsCommand implements Command {

  private static final transient Logger logger = Logger.getLogger(UpdateExchangeSymbolsCommand.class);

  public boolean execute(Context context) throws Exception {

    logger.info("Executing: updateexchangesymbols");

    EodDataProvider eodDataProvider = (EodDataProvider)context.get(DataScraper3Context.EODDATAPROVIDER_KEY);
    EodDataSink eodDataSink = (EodDataSink)context.get(DataScraper3Context.EODDATASINK_KEY);
    String exchange = (String)context.get(DataScraper3Context.EXCHANGE_KEY);

    if (exchange == null) {
      throw new CommandException("Must supply --exchange [exchangecode]");
    }

    SYMBOL[] symbols = eodDataProvider.getSymbols(exchange);

    eodDataSink.updateExchangeSymbols(exchange, symbols);

    return false;

  }
}

