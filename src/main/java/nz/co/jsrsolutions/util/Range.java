/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)Range.java        
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


public class Range<T extends Comparable<T>> {

  protected T lower;

  protected T upper;

  public Range(T lower, T upper) {

    if (lower.compareTo(upper) > 0) {
      final StringBuffer messageBuffer = new StringBuffer();
      messageBuffer.append("lower element of range [ ");
      messageBuffer.append(lower.toString());
      messageBuffer.append(" ] must be less than or equal to upper element [ ");
      messageBuffer.append(upper.toString());
      messageBuffer.append(" ]");
      throw new IllegalArgumentException(messageBuffer.toString());
    }

    this.lower = lower;
    this.upper = upper;
  }

  public T getLower() {
    return lower;
  }

  public T getUpper() {
    return upper;
  }

  public boolean containsRange(Range<T> range) {
    
    boolean containsRange = false;
    
    if ((range.getLower().compareTo(this.lower) >= 0) 
        && (range.getUpper().compareTo(this.upper) <= 0)) {
      
      containsRange = true;
    
    }
    
    return containsRange;
  }
  
  public boolean overlapsRange(Range<T> range) {
    
    boolean overlapsRange = true;
    
    if ((range.getLower().compareTo(this.lower) < 0) 
        && (range.getUpper().compareTo(this.lower) < 0)) {
      
      overlapsRange = false;
    
    }
    else if ((range.getLower().compareTo(this.upper) > 0)) {
    
      overlapsRange = false;
      
    }
      
      
    return overlapsRange;
  }

}