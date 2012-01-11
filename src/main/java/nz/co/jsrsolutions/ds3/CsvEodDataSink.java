/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

package nz.co.jsrsolutions.ds3;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Calendar;

import nz.co.jsrsolutions.ds3.DataStub.EXCHANGE;
import nz.co.jsrsolutions.ds3.DataStub.QUOTE;
import nz.co.jsrsolutions.ds3.DataStub.SYMBOL;
import nz.co.jsrsolutions.util.Range;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;

class CsvEodDataSink implements EodDataSink {

  private static final transient Logger logger = Logger.getLogger(CsvEodDataSink.class);

  CsvEodDataSink() {

  }

  public void updateExchanges(EXCHANGE[] exchanges) throws EodDataSinkException {

    throw new NotImplementedException();

  }

  public void updateExchangeSymbols(String exchange,
                                    SYMBOL[] symbols) throws EodDataSinkException {

    throw new NotImplementedException();

  }

  public void updateQuotes(String exchange,
                           String symbol,
                           String startDate,
                           String endDate,
                           String period,
                           QUOTE[] quoteArray) throws EodDataSinkException {

    try {

      StringBuffer filenameBuf = new StringBuffer();
      filenameBuf.append("/data/eoddata/");
      filenameBuf.append(exchange);
      filenameBuf.append("/");
      filenameBuf.append(symbol);
      filenameBuf.append("/");
      filenameBuf.append(exchange);
      filenameBuf.append(".");
      filenameBuf.append(symbol);
      filenameBuf.append(".");
      filenameBuf.append(startDate);
      filenameBuf.append("-");
      filenameBuf.append(endDate);
      filenameBuf.append(".");
      filenameBuf.append(period);
      filenameBuf.append(".csv");


      String filename = new String(filenameBuf.toString());

      StringBuffer infoMessage = new StringBuffer();
      infoMessage.append("Writing output to: [ ");
      infoMessage.append(filename);
      infoMessage.append(" ] ");
      logger.info(infoMessage.toString());

      FileWriter fileWriter = new FileWriter(filenameBuf.toString());
      BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

      for (DataStub.QUOTE quote : quoteArray) {

        Calendar calendar = quote.getDateTime();
        bufferedWriter.append(Integer.toString(calendar.get(Calendar.YEAR)));
        bufferedWriter.append(String.format("%02d", calendar.get(Calendar.MONTH)));
        bufferedWriter.append(String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)));
        bufferedWriter.append(",");
        bufferedWriter.append(Double.toString(quote.getClose()));
        bufferedWriter.newLine();

      }

      bufferedWriter.close();

    }
    catch(java.io.IOException ioe) {

      logger.error(ioe.toString());
      EodDataSinkException edpe = new EodDataSinkException("Unable to set symbols");
      edpe.initCause(ioe);

      throw edpe;

    }

  }

  public void updateExchangeSymbolQuotes(String exchange,
                                         String symbol,
                                         QUOTE[] quotes) throws EodDataSinkException {
    throw new NotImplementedException();
  }

  public void close() {
  
  }

  public Range<Calendar> getRange(String exchange, String symbol) {
    throw new NotImplementedException();
  }


}
