/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)HdfObjectEodDataSinkTest.java        
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

import org.apache.commons.configuration.HierarchicalConfiguration;

final class EodDataSinkFactory {

  private static final String HDF5_KEY = new String("hdf5");

  private static final String HDFOBJECT_KEY = new String("hdfobject");

  public static EodDataSink create(HierarchicalConfiguration appConfig, String type) throws EodDataSinkException {
    
    if (type.compareTo(HDF5_KEY) == 0) {
      
      return new Hdf5EodDataSink(appConfig.getString("eodDataSinks.eodDataSink(0).filename"));

    }
    else if (type.compareTo(HDFOBJECT_KEY) == 0) {
      
      return new HdfObjectEodDataSink(appConfig.getString("eodDataSinks.eodDataSink(0).filename"));

    }
    else {
      
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("Unknown data sink type [ ");
      stringBuffer.append(type);
      stringBuffer.append(" ] ");
      throw new EodDataSinkException(stringBuffer.toString());

    }

  }

}