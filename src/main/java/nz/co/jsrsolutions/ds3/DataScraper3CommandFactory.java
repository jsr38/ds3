/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

package nz.co.jsrsolutions.ds3;

import java.lang.String;

import org.apache.commons.chain.Command;
import org.apache.commons.configuration.HierarchicalConfiguration;

final class DataScraper3CommandFactory {

  private static final String LISTEXCHANGES_KEY = new String("listexchanges");

  private static final String LISTEXCHANGEMONTHS_KEY = new String("listexchangemonths");

  private static final String UPDATEEXCHANGES_KEY = new String("updateexchanges");

  private static final String UPDATEEXCHANGESYMBOLS_KEY = new String("updateexchangesymbols");

  private static final String UPDATEALLSYMBOLS_KEY = new String("updateallsymbols");

  private static final String UPDATEEXCHANGEQUOTES_KEY = new String("updateexchangequotes");

  private static final String UPDATEEXCHANGESYMBOLQUOTES_KEY = new String("updateexchangesymbolquotes");

  public static Command create(HierarchicalConfiguration appConfig, String type) throws DataScraper3Exception {
    
    if (type.compareTo(LISTEXCHANGES_KEY) == 0) {
      
      return new ListExchangesCommand();

    }
    else if (type.compareTo(LISTEXCHANGEMONTHS_KEY) == 0) {
      
      return new ListExchangeMonthsCommand();

    }
    else if (type.compareTo(UPDATEEXCHANGES_KEY) == 0) {
      
      return new UpdateExchangesCommand();

    }
    else if (type.compareTo(UPDATEEXCHANGESYMBOLS_KEY) == 0) {
      
      return new UpdateExchangeSymbolsCommand();

    }
    else if (type.compareTo(UPDATEALLSYMBOLS_KEY) == 0) {
      
      return new UpdateAllSymbolsCommand();

    }
    else if (type.compareTo(UPDATEEXCHANGEQUOTES_KEY) == 0) {
      
      return new UpdateExchangeQuotesCommand();

    }
    else if (type.compareTo(UPDATEEXCHANGESYMBOLQUOTES_KEY) == 0) {
      
      return new UpdateExchangeSymbolQuotesCommand();

    }
    else {
      
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("Unknown command [ ");
      stringBuffer.append(type);
      stringBuffer.append(" ] ");
      throw new DataScraper3Exception(stringBuffer.toString());

    }

  }

}