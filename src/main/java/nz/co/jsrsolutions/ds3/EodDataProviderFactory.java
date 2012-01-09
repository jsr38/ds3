/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)EodDataProviderFactory.java        
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

public final class EodDataProviderFactory {

  private static final String EODDATADATAPROVIDER_KEY = new String("eoddata");

  public static EodDataProvider create(HierarchicalConfiguration appConfig, String type) throws EodDataProviderException {
    
    // TODO: Use reflection here.  Probably need a Builder to abstract away different construction arguments
    if (type.compareTo(EODDATADATAPROVIDER_KEY) == 0) {

      String serviceUrl = appConfig.getString("eodDataProviders.eodDataProvider(0).serviceUrl");
      String username = appConfig.getString("eodDataProviders.eodDataProvider(0).username");
      String password = appConfig.getString("eodDataProviders.eodDataProvider(0).password");
      long timeout = appConfig.getLong("eodDataProviders.eodDataProvider(0).timeout");
      
      return new EodDataDataProvider(serviceUrl,
				     username,
				     password,
				     timeout);

    }
    else {
      
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("Unknown data provider type [ ");
      stringBuffer.append(type);
      stringBuffer.append(" ] ");
      throw new EodDataProviderException(stringBuffer.toString());

    }

  }

}