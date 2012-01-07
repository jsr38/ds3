/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)ListExchangesCommandTest.java        
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


import org.junit.* ;
import static org.junit.Assert.* ;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;

/**
 * Unit test for ListExchangesCommand.
 */
public class ListExchangesCommandTest {

  /**
   * Test that when supplied with a known set of exchanges,
   * an identical set of exchanges is written to the HDF5 
   * file.
   */
  public void testListExchangesCommand() {

    Context context = new ContextBase();

    Command command = new ListExchangesCommand();

    try {

      //      command.execute(context);

    } catch (Exception e) {

      fail(e.getMessage());

    }

  }

}