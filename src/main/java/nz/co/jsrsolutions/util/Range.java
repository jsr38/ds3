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
      throw new IllegalArgumentException("lower element of range must be less than or equal to upper element");
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

  // TODO: add some proper range functionality

}