/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)QuoteHelper.java
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

package nz.co.jsrsolutions.ds3;

import java.lang.String;

import nz.co.jsrsolutions.ds3.DataStub.QUOTE;

public class QuoteHelper {

  private static final String NEWLINE = System.getProperty("line.separator");

  public static String toString(QUOTE quote) {

    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(NEWLINE);

    stringBuffer.append(" Symbol:      [ ");
    stringBuffer.append(quote.getSymbol());
    stringBuffer.append(" ]");
    stringBuffer.append(NEWLINE);

    stringBuffer.append(" Description: [ ");
    stringBuffer.append(quote.getDescription());
    stringBuffer.append(" ]");
    stringBuffer.append(NEWLINE);

    stringBuffer.append(" Name:        [ ");
    stringBuffer.append(quote.getName());
    stringBuffer.append(" ] ");
    stringBuffer.append(NEWLINE);

    stringBuffer.append(" DateTime:    [ ");
    stringBuffer.append(quote.getDateTime().getTime());
    stringBuffer.append(" ] ");
    stringBuffer.append(NEWLINE);

    stringBuffer.append(" Open:        [ ");
    stringBuffer.append(quote.getOpen());
    stringBuffer.append(" ] ");
    stringBuffer.append(NEWLINE);

    stringBuffer.append(" High:        [ ");
    stringBuffer.append(quote.getHigh());
    stringBuffer.append(" ] ");
    stringBuffer.append(NEWLINE);

    stringBuffer.append(" Low:         [ ");
    stringBuffer.append(quote.getLow());
    stringBuffer.append(" ] ");
    stringBuffer.append(NEWLINE);

    stringBuffer.append(" Close:       [ ");
    stringBuffer.append(quote.getClose());
    stringBuffer.append(" ] ");
    stringBuffer.append(NEWLINE);

    stringBuffer.append(" Volume:      [ ");
    stringBuffer.append(quote.getVolume());
    stringBuffer.append(" ] ");
    stringBuffer.append(NEWLINE);

    stringBuffer.append(" Bid:         [ ");
    stringBuffer.append(quote.getBid());
    stringBuffer.append(" ] ");
    stringBuffer.append(NEWLINE);

    stringBuffer.append(" Ask:         [ ");
    stringBuffer.append(quote.getAsk());
    stringBuffer.append(" ] ");
    stringBuffer.append(NEWLINE);

    return stringBuffer.toString();

  }

}
