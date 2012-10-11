/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)Hdf5ExchangeDataType.java        
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

package nz.co.jsrsolutions.ds3.sink;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.hdf5lib.exceptions.HDF5LibraryException;
import nz.co.jsrsolutions.ds3.DataStub.EXCHANGE;

import org.apache.log4j.Logger;

class Hdf5ExchangeDatatype {

  @SuppressWarnings("unused")
  private static final transient Logger logger = Logger.getLogger(Hdf5ExchangeDatatype.class);

  private static final int EXCHANGE_DATATYPE_SIZE = 137;

  private static final int EXCHANGE_DATATYPE_OFFSET_CODE = 0;

  private static final int EXCHANGE_DATATYPE_OFFSET_NAME = 6;

  private static final int EXCHANGE_DATATYPE_OFFSET_COUNTRY = 106;

  private static final int EXCHANGE_DATATYPE_OFFSET_CURRENCY = 108;

  private static final int EXCHANGE_DATATYPE_OFFSET_SUFFIX = 111;

  private static final int EXCHANGE_DATATYPE_OFFSET_TIMEZONE = 113;

  private static final int EXCHANGE_DATATYPE_OFFSET_ISINTRADAY = 121;

  private static final int EXCHANGE_DATATYPE_OFFSET_INTRADAYSTARTDATE = 125;

  private static final int EXCHANGE_DATATYPE_OFFSET_HASINTRADAYPRODUCT = 133;

  public static final String EXCHANGE_DATATYPE_NAME_CODE = new String("code");

  public static final String EXCHANGE_DATATYPE_NAME_NAME = new String("name");

  public static final String EXCHANGE_DATATYPE_NAME_COUNTRY = new String("country");

  public static final String EXCHANGE_DATATYPE_NAME_CURRENCY = new String("currency");

  public static final String EXCHANGE_DATATYPE_NAME_SUFFIX = new String("suffix");

  public static final String EXCHANGE_DATATYPE_NAME_TIMEZONE = new String("timezone");

  public static final String EXCHANGE_DATATYPE_NAME_ISINTRADAY = new String("isintraday");

  public static final String EXCHANGE_DATATYPE_NAME_INTRADAYSTARTDATE = new String("intradaystartdate");

  public static final String EXCHANGE_DATATYPE_NAME_HASINTRADAYPRODUCT = new String("hasintradayproduct");

  private static final int EXCHANGE_DATATYPE_SIZE_CODE = 6;

  private static final int EXCHANGE_DATATYPE_SIZE_NAME = 100;

  private static final int EXCHANGE_DATATYPE_SIZE_COUNTRY = 2;

  private static final int EXCHANGE_DATATYPE_SIZE_CURRENCY = 3;

  private static final int EXCHANGE_DATATYPE_SIZE_SUFFIX = 2;

  private static final int EXCHANGE_DATATYPE_SIZE_TIMEZONE = 8;

  private static final int EXCHANGE_DATATYPE_SIZE_ISINTRADAY = 4;

  private static final int EXCHANGE_DATATYPE_SIZE_INTRADAYSTARTDATE = 2;

  private static final int EXCHANGE_DATATYPE_SIZE_HASINTRADAYPRODUCT = 4;

  private int codeDatatypeHandle = -1;

  private int nameDatatypeHandle = -1;

  private int countryDatatypeHandle = -1;

  private int currencyDatatypeHandle = -1;

  private int suffixDatatypeHandle = -1;

  private int timezoneDatatypeHandle = -1;

  private int isIntradayDatatypeHandle = -1;

  private int intradayStartDateDatatypeHandle = -1;

  private int hasIntradayProductDatatypeHandle = -1;

  private int exchangeDatatypeHandle = -1;

  Hdf5ExchangeDatatype() throws EodDataSinkException {
    
    try {

      codeDatatypeHandle = H5.H5Tcopy(HDF5Constants.H5T_C_S1);
      H5.H5Tset_size(codeDatatypeHandle, EXCHANGE_DATATYPE_SIZE_CODE);

      nameDatatypeHandle = H5.H5Tcopy(HDF5Constants.H5T_C_S1);
      H5.H5Tset_size(nameDatatypeHandle, EXCHANGE_DATATYPE_SIZE_NAME);
    
      countryDatatypeHandle = H5.H5Tcopy(HDF5Constants.H5T_C_S1);
      H5.H5Tset_size(countryDatatypeHandle, EXCHANGE_DATATYPE_SIZE_COUNTRY);

      currencyDatatypeHandle = H5.H5Tcopy(HDF5Constants.H5T_C_S1);
      H5.H5Tset_size(currencyDatatypeHandle, EXCHANGE_DATATYPE_SIZE_CURRENCY);

      suffixDatatypeHandle = H5.H5Tcopy(HDF5Constants.H5T_C_S1);
      H5.H5Tset_size(suffixDatatypeHandle, EXCHANGE_DATATYPE_SIZE_SUFFIX);

      timezoneDatatypeHandle = H5.H5Tcopy(HDF5Constants.H5T_C_S1);
      H5.H5Tset_size(timezoneDatatypeHandle, EXCHANGE_DATATYPE_SIZE_TIMEZONE);

      isIntradayDatatypeHandle = H5.H5Tcopy(HDF5Constants.H5T_STD_I32LE);

      //intradayStartDateDatatypeHandle = H5.H5Tcopy(HDF5Constants.H5T_STD_U64BE);
      intradayStartDateDatatypeHandle = H5.H5Tcopy(HDF5Constants.H5T_UNIX_D64LE);

      hasIntradayProductDatatypeHandle = H5.H5Tcopy(HDF5Constants.H5T_STD_I32LE);

      exchangeDatatypeHandle = H5.H5Tcreate(HDF5Constants.H5T_COMPOUND, EXCHANGE_DATATYPE_SIZE);

      if (exchangeDatatypeHandle >= 0) {
      
        H5.H5Tinsert(exchangeDatatypeHandle, EXCHANGE_DATATYPE_NAME_CODE, EXCHANGE_DATATYPE_OFFSET_CODE, codeDatatypeHandle);
        H5.H5Tinsert(exchangeDatatypeHandle, EXCHANGE_DATATYPE_NAME_NAME, EXCHANGE_DATATYPE_OFFSET_NAME, nameDatatypeHandle);
        H5.H5Tinsert(exchangeDatatypeHandle, EXCHANGE_DATATYPE_NAME_COUNTRY, EXCHANGE_DATATYPE_OFFSET_COUNTRY, countryDatatypeHandle);
        H5.H5Tinsert(exchangeDatatypeHandle, EXCHANGE_DATATYPE_NAME_CURRENCY, EXCHANGE_DATATYPE_OFFSET_CURRENCY, currencyDatatypeHandle);
        H5.H5Tinsert(exchangeDatatypeHandle, EXCHANGE_DATATYPE_NAME_SUFFIX, EXCHANGE_DATATYPE_OFFSET_SUFFIX, suffixDatatypeHandle);
        H5.H5Tinsert(exchangeDatatypeHandle, EXCHANGE_DATATYPE_NAME_TIMEZONE, EXCHANGE_DATATYPE_OFFSET_TIMEZONE, timezoneDatatypeHandle);
        H5.H5Tinsert(exchangeDatatypeHandle, EXCHANGE_DATATYPE_NAME_ISINTRADAY, EXCHANGE_DATATYPE_OFFSET_ISINTRADAY, isIntradayDatatypeHandle);
        H5.H5Tinsert(exchangeDatatypeHandle, EXCHANGE_DATATYPE_NAME_INTRADAYSTARTDATE, EXCHANGE_DATATYPE_OFFSET_INTRADAYSTARTDATE, intradayStartDateDatatypeHandle);
        H5.H5Tinsert(exchangeDatatypeHandle, EXCHANGE_DATATYPE_NAME_HASINTRADAYPRODUCT, EXCHANGE_DATATYPE_OFFSET_HASINTRADAYPRODUCT, hasIntradayProductDatatypeHandle);

      }
      else {
        throw new EodDataSinkException("Unable to create exchange datatype.");
      }

    }
    catch (HDF5LibraryException ex) {
      ex.printStackTrace();
      throw new EodDataSinkException("Unable to create exchange datatype.");
    }



  }

  int getFileDatatypeHandle() {
    
    return exchangeDatatypeHandle;

  }

  int getMemoryDatatypeHandle() {
    
    return exchangeDatatypeHandle;

  }

  @SuppressWarnings("unused")
  byte[] toByteArray(EXCHANGE[] exchanges) {
    
    byte[] buffer = new byte[exchanges.length * EXCHANGE_DATATYPE_SIZE];

    byte[] codeByteArray = null;
    byte[] nameByteArray = null;
    byte[] countryByteArray = null;
    byte[] currencyByteArray = null;
    byte[] suffixByteArray = null;
    byte[] timezoneByteArray = null;
    byte[] isIntradayByteArray = null;
    byte[] intradayStartDateByteArray = null;
    byte[] hasIntradayProductByteArray = null;

    for (long i = 0; i < exchanges.length; ++i) {

      //codeByteArray = HDFNativeData.;
      nameByteArray = new byte[EXCHANGE_DATATYPE_SIZE_NAME];
      countryByteArray = new byte[EXCHANGE_DATATYPE_SIZE_COUNTRY];
      currencyByteArray = new byte[EXCHANGE_DATATYPE_SIZE_CURRENCY];
      suffixByteArray = new byte[EXCHANGE_DATATYPE_SIZE_SUFFIX];
      timezoneByteArray = new byte[EXCHANGE_DATATYPE_SIZE_TIMEZONE];
      isIntradayByteArray = new byte[EXCHANGE_DATATYPE_SIZE_ISINTRADAY];
      intradayStartDateByteArray = new byte[EXCHANGE_DATATYPE_SIZE_INTRADAYSTARTDATE];
      hasIntradayProductByteArray = new byte[EXCHANGE_DATATYPE_SIZE_HASINTRADAYPRODUCT];
      
      //      System.arrayCopy(buffer[(i * EXCHANGE_DATATYPE_SIZE) + EXCHANGE_DATATYPE_OFFSET_CODE]);

    }
    
    return buffer;

  }

  void close() throws EodDataSinkException {

    try {

      // TODO: this is really quite unsatisfactory as
      // an exception thrown from any of these calls
      // will mean any subsequent calls will not
      // be executed.
      H5.H5Tclose(codeDatatypeHandle);
      H5.H5Tclose(nameDatatypeHandle);
      H5.H5Tclose(countryDatatypeHandle);
      H5.H5Tclose(currencyDatatypeHandle);
      H5.H5Tclose(suffixDatatypeHandle);
      H5.H5Tclose(timezoneDatatypeHandle);
      H5.H5Tclose(isIntradayDatatypeHandle);
      H5.H5Tclose(intradayStartDateDatatypeHandle);
      H5.H5Tclose(hasIntradayProductDatatypeHandle);
      H5.H5Tclose(exchangeDatatypeHandle);

    }
    catch (HDF5Exception ex) {
      logger.error(ex);
      throw new EodDataSinkException("Encountered problem while attempting to close exchange data types.");
    }
    
  }

}