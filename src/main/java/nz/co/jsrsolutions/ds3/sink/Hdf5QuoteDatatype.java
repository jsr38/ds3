/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)Hdf5QuoteDataType.java        
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

import java.util.Calendar;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.HDFNativeData;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;

import org.apache.log4j.Logger;

class Hdf5QuoteDatatype {

  @SuppressWarnings("unused")
  private static final transient Logger logger = Logger.getLogger(Hdf5QuoteDatatype.class);

  public static final int QUOTE_DATATYPE_SIZE = 48;

  public static final String DATETIME = new String("datetime");

  public static final String OPEN = new String("open");

  public static final String HIGH = new String("high");

  public static final String LOW = new String("low");

  public static final String CLOSE = new String("close");

  public static final String VOLUME = new String("volume");

  private static final String[] fieldNames = new String[] {
    DATETIME,
    OPEN,
    HIGH,
    LOW,
    CLOSE,
    VOLUME
  };

  private static final int[] fileDatatypes = new int[] {
    HDF5Constants.H5T_UNIX_D64LE,
    HDF5Constants.H5T_IEEE_F64LE,
    HDF5Constants.H5T_IEEE_F64LE,
    HDF5Constants.H5T_IEEE_F64LE,
    HDF5Constants.H5T_IEEE_F64LE,
    HDF5Constants.H5T_IEEE_F64LE
  };

  private static final int[] memoryDatatypes = new int[] {
    HDF5Constants.H5T_UNIX_D64LE,
    HDF5Constants.H5T_NATIVE_DOUBLE,
    HDF5Constants.H5T_NATIVE_DOUBLE,
    HDF5Constants.H5T_NATIVE_DOUBLE,
    HDF5Constants.H5T_NATIVE_DOUBLE,
    HDF5Constants.H5T_NATIVE_DOUBLE
};

  // The offsets and sizes must eventually be computed from the
  // 'field' data types.  For another day.
  private static final int[] offsets = new int[] { 0, 8, 16, 24, 32, 40 };

  private static final int[] sizes = new int[] { 8, 8, 8, 8, 8, 8 };

  private static int[] fileDatatypeHandles = null;

  private static int[] memoryDatatypeHandles = null;

  private static int quoteFileDatatypeHandle = -1;

  private static int quoteMemoryDatatypeHandle = -1;
  
  private static boolean active = false;

  private long[] dateTime = new long[1];

  // Fix all this up - for convenience in
  // type conversion with HDFNative
  private double[] open = new double[1];

  private double[] high = new double[1];

  private double[] low = new double[1];

  private double[] close = new double[1];

  private double[] volume = new double[1];

  Hdf5QuoteDatatype() {
  }

  Hdf5QuoteDatatype(Calendar dateTime,
                    double open,
                    double high,
                    double low,
                    double close,
                    double volume) throws EodDataSinkException {

    this.dateTime[0] = dateTime.getTimeInMillis();
    this.open[0] = open;
    this.high[0] = high;
    this.low[0] = low;
    this.close[0] = close;
    this.volume[0] = volume;
    
  }

  static void initDatatypeHandles() {

    try {
      
      fileDatatypeHandles = new int[fileDatatypes.length];
      quoteFileDatatypeHandle = H5.H5Tcreate(HDF5Constants.H5T_COMPOUND,
          QUOTE_DATATYPE_SIZE);

      int i = 0;

      for (int type : fileDatatypes) {

        fileDatatypeHandles[i] = H5.H5Tcopy(type);
        H5.H5Tinsert(quoteFileDatatypeHandle, fieldNames[i], offsets[i],
            fileDatatypeHandles[i]);
        ++i;

      }

      memoryDatatypeHandles = new int[memoryDatatypes.length];
      quoteMemoryDatatypeHandle = H5.H5Tcreate(HDF5Constants.H5T_COMPOUND,
          QUOTE_DATATYPE_SIZE);

      i = 0;

      for (int type : memoryDatatypes) {

        memoryDatatypeHandles[i] = H5.H5Tcopy(type);
        H5.H5Tinsert(quoteMemoryDatatypeHandle, fieldNames[i], offsets[i],
            memoryDatatypeHandles[i]);
        ++i;

      }

      active = true;
    }
    catch (HDF5Exception ex) {
      ex.printStackTrace();
    }
 
  }
  
  Calendar getDateTime() {

    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(dateTime[0]);

    return calendar;
  }

  double getOpen() {
    return open[0];
  }

  double getHigh() {
    return high[0];
  }

  double getLow() {
    return low[0];
  }

  double getClose() {
    return close[0];
  }

  double getVolume() {
    return volume[0];
  }

  void setDateTime(Calendar dateTime) {
    this.dateTime[0] = dateTime.getTimeInMillis();
  }

  void setOpen(double open) {
    this.open[0] = open;
  }

  void setHigh(double high) {
    this.high[0] = high;
  }

  void setLow(double low) {
    this.low[0] = low;
  }

  void setClose(double close) {
    this.close[0] = close;
  }

  void setVolume(double volume) {
    this.volume[0] = volume;
  }

  static int getFileDatatypeHandle() {
    
    if (!active) {
      initDatatypeHandles();
    }
    return quoteFileDatatypeHandle;

  }

  static int getMemoryDatatypeHandle() {
    
    if (!active) {
      initDatatypeHandles();
    }
    return quoteMemoryDatatypeHandle;

  }

  byte[] getByteArray() {
    
    byte[] buffer = new byte[QUOTE_DATATYPE_SIZE];

    byte[] dateTimeRecord = HDFNativeData.longToByte(0, 1, dateTime);
    byte[] openRecord = HDFNativeData.doubleToByte(0, 1, open);
    byte[] highRecord = HDFNativeData.doubleToByte(0, 1, high);
    byte[] lowRecord = HDFNativeData.doubleToByte(0, 1, low);
    byte[] closeRecord = HDFNativeData.doubleToByte(0, 1, close);
    byte[] volumeRecord = HDFNativeData.doubleToByte(0, 1, volume);
    
    System.arraycopy(dateTimeRecord, 0, buffer, offsets[0], sizes[0]);
    System.arraycopy(openRecord, 0, buffer, offsets[1], sizes[1]);
    System.arraycopy(highRecord, 0, buffer, offsets[2], sizes[2]);
    System.arraycopy(lowRecord, 0, buffer, offsets[3], sizes[3]);
    System.arraycopy(closeRecord, 0, buffer, offsets[4], sizes[4]);
    System.arraycopy(volumeRecord, 0, buffer, offsets[5], sizes[5]);

    return buffer;

  }

  static Hdf5QuoteDatatype create(byte[] buffer) {
    
    if (buffer.length != QUOTE_DATATYPE_SIZE) {
      throw new IllegalArgumentException("supplied buffer must be exactly the dimension of the Hdf5QuoteDatatype size");
    }
    
    final Hdf5QuoteDatatype quoteData = new Hdf5QuoteDatatype();
    
    quoteData.dateTime[0] = HDFNativeData.byteToLong(buffer, offsets[0]);
    quoteData.open[0] = HDFNativeData.byteToDouble(buffer, offsets[1]);
    quoteData.high[0] = HDFNativeData.byteToDouble(buffer, offsets[2]);
    quoteData.low[0] = HDFNativeData.byteToDouble(buffer, offsets[3]);
    quoteData.close[0] = HDFNativeData.byteToDouble(buffer, offsets[4]);
    quoteData.volume[0] = HDFNativeData.byteToDouble(buffer, offsets[5]);

    return quoteData;

  }
  
  static Hdf5QuoteDatatype[] createArray(byte[] buffer) {
    
    if (buffer.length % QUOTE_DATATYPE_SIZE != 0) {
      throw new IllegalArgumentException("supplied buffer must be exactly an integer multiple of the Hdf5QuoteDatatype size");
    }
    
    final int numQuotes = buffer.length / QUOTE_DATATYPE_SIZE;
    final Hdf5QuoteDatatype[] quotes = new Hdf5QuoteDatatype[numQuotes];
    final byte[] quoteDataBuffer = new byte[QUOTE_DATATYPE_SIZE];
    
    for (int i = 0; i < numQuotes; ++i) {
      
      System.arraycopy(buffer, i * QUOTE_DATATYPE_SIZE, quoteDataBuffer, 0, QUOTE_DATATYPE_SIZE);
      quotes[i] = create(quoteDataBuffer);
    
    }
    
    return quotes;

  }

  static void close() throws EodDataSinkException {

    if (active) {
      for (int fileDatatypeHandle : fileDatatypeHandles) {
        try {

          H5.H5Tclose(fileDatatypeHandle);

        } catch (HDF5Exception ex) {
          ex.printStackTrace();
          throw new EodDataSinkException("Failed to close file data type.");
        }
      }

      for (int memoryDatatypeHandle : memoryDatatypeHandles) {
        try {

          H5.H5Tclose(memoryDatatypeHandle);

        } catch (HDF5Exception ex) {
          ex.printStackTrace();
          throw new EodDataSinkException("Failed to close memory data type.");
        }
      }

      try {

        H5.H5Tclose(quoteFileDatatypeHandle);

      } catch (HDF5Exception ex) {
        ex.printStackTrace();
        throw new EodDataSinkException("Failed to close quote file datatype.");
      }

      try {

        H5.H5Tclose(quoteMemoryDatatypeHandle);

      } catch (HDF5Exception ex) {
        ex.printStackTrace();
        throw new EodDataSinkException("Failed to close quote memory datatype.");
      }
    }
    
    active = false;
    
  }

}