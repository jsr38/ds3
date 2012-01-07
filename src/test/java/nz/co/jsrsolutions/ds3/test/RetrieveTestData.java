/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)RetrieveTestData.java        
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

package nz.co.jsrsolutions.ds3.test;

import java.lang.String;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;


final class RetrieveTestData {

  private static final transient Logger logger = Logger.getLogger(RetrieveTestData.class);

  private static final CombinedConfiguration config = new CombinedConfiguration();

  private static final String CONFIG_FILENAME = new String("rtd.config.dev.xml");

  private static final String APPLICATION_CONFIG_ROOT = new String("applicationConfig.rtdConfig");

  private static final String EODDATA_PROVIDER_NAME = new String("eoddata");

  private RetrieveTestData() {

  }

  public static void main(String[] args) {

    logger.info("Starting application [ rtd ] ..."); 

    try {

	    config.addConfiguration(new XMLConfiguration(Class.getResourceAsStream(CONFIG_FILENAME)));

	    HierarchicalConfiguration appConfig = config.configurationAt(APPLICATION_CONFIG_ROOT);

	    EodDataProvider eodDataProvider = null;

	    try {

        eodDataProvider = EodDataProviderFactory.create(appConfig, EODDATA_PROVIDER_NAME);

	    }
	    catch(EodDataProviderException edpe) {

        logger.error(edpe.toString());
        edpe.printStackTrace();

	    }
	    finally {

        if (eodDataProvider != null) {

          eodDataProvider.close();

        }
          
	    }

    }
    catch(ConfigurationException ce) {

	    logger.error(ce.toString()); 
	    ce.printStackTrace();

    }
    catch(Exception e) {

	    logger.error(e.toString()); 
	    e.printStackTrace();

    }

    logger.info("Exiting application [ rtd ] ...");

  }


}

