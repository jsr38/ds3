/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)CommandFactory.java        
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

import org.apache.commons.chain.Command;

public final class CommandFactory {

  private static final String LISTEXCHANGES_KEY = new String("listexchanges");

  private static final String LISTEXCHANGEMONTHS_KEY = new String("listexchangemonths");

  private static final String UPDATEEXCHANGES_KEY = new String("updateexchanges");

  private static final String UPDATEEXCHANGESYMBOLS_KEY = new String("updateexchangesymbols");

  private static final String UPDATEALLSYMBOLS_KEY = new String("updateallsymbols");

  private static final String UPDATEEXCHANGEQUOTES_KEY = new String("updateexchangequotes");

  private static final String UPDATEEXCHANGESYMBOLQUOTES_KEY = new String("updateexchangesymbolquotes");

  public static Command create(String type) throws CommandException {
    
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
      throw new CommandException(stringBuffer.toString());

    }

  }

}