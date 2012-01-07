/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

package nz.co.jsrsolutions.ds3;

import org.apache.commons.chain.impl.ContextBase;


public class DataScraper3Context extends ContextBase {

  public static final String EODDATAPROVIDER_KEY = new String("eoddataprovider");

  public static final String EODDATASINK_KEY = new String("eoddatasink");

  public static final String EXCHANGE_KEY = new String("exchange");

  public static final String SYMBOL_KEY = new String("symbol");

  private EodDataProvider eodDataProvider;

  private EodDataSink eodDataSink;

  public EodDataProvider getEodDataProvider() {
    return eodDataProvider;
  }

  public void setEodDataProvider(EodDataProvider eodDataProvider) {
    this.eodDataProvider = eodDataProvider;
  }

  public EodDataSink getEodDataSink() {
    return eodDataSink;
  }

  public void setEodDataSink(EodDataSink eodDataSink) {
    this.eodDataSink = eodDataSink;
  }

}