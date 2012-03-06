/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)Util.java        
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

import java.util.Arrays;

class ArrayUtil {

    public static <T> T[] concat(T[] first, T[] second) {

      T[] result = Arrays.copyOf(first, first.length + second.length);
      System.arraycopy(second, 0, result, first.length, second.length);

      return result;

    }

}