/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)QuoteDateComparator.java        
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

package nz.co.jsrsolutions.util;

import java.util.Comparator;

import nz.co.jsrsolutions.ds3.DataStub.QUOTE;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class QuoteDateComparator implements Comparator<QUOTE> {

  @Override
  public int compare(QUOTE o1, QUOTE o2) {
    
    final DateTime first = new DateTime(o1.getDateTime());
    final DateTime second = new DateTime(o2.getDateTime());
    
    final LocalDate firstDate = first.toLocalDate();
    final LocalDate secondDate = second.toLocalDate();
    
    return firstDate.compareTo(secondDate);
  
  }

}
