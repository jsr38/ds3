/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)ArrayConverter.java        
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

import java.lang.reflect.Array;


public class ArrayConverter {

  @SuppressWarnings("unchecked")
  public static <T> T[] convert(Object[] objects, Class<T> type) throws ClassCastException {
    
    T[] convertedObjects =  (T[]) Array.newInstance(type, objects.length);
 
    for(int i = 0; i < objects.length; ++i) {
      convertedObjects[i] = (T)objects[i];
    }
 
    return convertedObjects;
  }

  
}

