/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

package nz.co.jsrsolutions.ds3;

import nz.co.jsrsolutions.ds3.DataStub.EXCHANGE;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

public class UpdateExchangesCommand implements Command {

  private static final transient Logger logger = Logger.getLogger(UpdateExchangesCommand.class);

  public boolean execute(Context context) throws Exception {

    logger.info("Executing: updateexchanges");

    EodDataProvider eodDataProvider = (EodDataProvider)context.get(DataScraper3Context.EODDATAPROVIDER_KEY);
    EodDataSink eodDataSink = (EodDataSink)context.get(DataScraper3Context.EODDATASINK_KEY);

    EXCHANGE[] exchanges = eodDataProvider.getExchanges();
    eodDataSink.updateExchanges(exchanges);

    return false;

  }
}