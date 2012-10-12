/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)Hdf5EodDataSink.java        
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

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.callbacks.H5L_iterate_cb;
import ncsa.hdf.hdf5lib.callbacks.H5L_iterate_t;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.hdf5lib.exceptions.HDF5LibraryException;
import ncsa.hdf.hdf5lib.structs.H5L_info_t;
import ncsa.hdf.hdf5lib.structs.H5O_info_t;
import nz.co.jsrsolutions.ds3.DataStub.EXCHANGE;
import nz.co.jsrsolutions.ds3.DataStub.QUOTE;
import nz.co.jsrsolutions.ds3.DataStub.SYMBOL;
import nz.co.jsrsolutions.ds3.QuoteHelper;
import nz.co.jsrsolutions.util.ExchangeSymbolKey;
import nz.co.jsrsolutions.util.Range;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;

class H5O {
  enum H5O_type {
    H5O_TYPE_UNKNOWN(-1), // Unknown object type
    H5O_TYPE_GROUP(0), // Object is a group
    H5O_TYPE_DATASET(1), // Object is a dataset
    H5O_TYPE_NAMED_DATATYPE(2), // Object is a named data type
    H5O_TYPE_NTYPES(3); // Number of different object types
    private static final Map<Integer, H5O_type> lookup = new HashMap<Integer, H5O_type>();

    static {
      for (H5O_type s : EnumSet.allOf(H5O_type.class))
        lookup.put(s.getCode(), s);
    }

    private int code;

    H5O_type(int layout_type) {
      this.code = layout_type;
    }

    public int getCode() {
      return this.code;
    }

    public static H5O_type get(int code) {
      return lookup.get(code);
    }
  }
}

class opdata implements H5L_iterate_t {
  int recurs;
  opdata prev;
  long addr;
}

class H5L_iter_callbackT implements H5L_iterate_cb {

  private static final transient Logger logger = Logger
      .getLogger(H5L_iter_callbackT.class);
  
  private List<String> symbols = new Vector<String>();

  public int callback(int group, String name, H5L_info_t info,
      H5L_iterate_t op_data) {

    H5O_info_t infobuf;
    int return_val = 0;
    opdata od = (opdata) op_data; // Type conversion
    int spaces = 2 * (od.recurs + 1); // Number of white spaces to prepend to
                                      // output.

    // Get type of the object and display its name and type.
    // The name of the object is passed to this function by the Library.
    try {
      infobuf = H5.H5Oget_info_by_name(group, name, HDF5Constants.H5P_DEFAULT);

      for (int i = 0; i < spaces; i++)
        System.out.print(" "); // Format output.
      switch (H5O.H5O_type.get(infobuf.type)) {
      case H5O_TYPE_GROUP:
        System.out.println("Group: " + name + " { ");
        // Check group address against linked list of operator
        // data structures. We will always run the check, as the
        // reference count cannot be relied upon if there are
        // symbolic links, and H5Oget_info_by_name always follows
        // symbolic links. Alternatively we could use H5Lget_info
        // and never recurse on groups discovered by symbolic
        // links, however it could still fail if an object's
        // reference count was manually manipulated with
        // H5Odecr_refcount.
        if (group_check(od, infobuf.addr)) {
          for (int i = 0; i < spaces; i++)
            System.out.print(" ");
          System.out.println("  Warning: Loop detected!");
        } else {
          // Initialize new object of type opdata and begin
          // recursive iteration on the discovered
          // group. The new opdata is given a pointer to the
          // current one.
          symbols.add(name);
          opdata nextod = new opdata();
          nextod.recurs = od.recurs + 1;
          nextod.prev = od;
          nextod.addr = infobuf.addr;
          //H5L_iterate_cb iter_cb2 = new H5L_iter_callbackT();
          // return_val = H5.H5Literate_by_name (group, name,
          // HDF5Constants.H5_INDEX_NAME,
          // HDF5Constants.H5_ITER_NATIVE, 0L, iter_cb2, nextod,
          // HDF5Constants.H5P_DEFAULT);
        }
        for (int i = 0; i < spaces; i++)
          System.out.print(" ");
        System.out.println("}");
        break;
      case H5O_TYPE_DATASET:
        System.out.println("Dataset: " + name);
        break;
      case H5O_TYPE_NAMED_DATATYPE:
        System.out.println("Datatype: " + name);
        break;
      default:
        System.out.println("Unknown: " + name);
      }
    } catch (Exception e) {
      logger.error(e);
    }

    return return_val;
  }

  public boolean group_check(opdata od, long target_addr) {
    if (od.addr == target_addr)
      return true; // Addresses match
    else if (od.recurs == 0)
      return false; // Root group reached with no matches
    else
      return group_check(od.prev, target_addr); // Recursively examine the next
                                                // node
  }

  public String[] getSymbols() {
    return symbols.<String> toArray(new String[0]);
  }

}

public class Hdf5EodDataSink implements EodDataSink, DisposableBean {

  private static final transient Logger logger = Logger
      .getLogger(Hdf5EodDataSink.class);

  private final int fileHandle;

  private int rootGroupHandle = -1;

  private int exchangeDatasetHandle = -1;

  private int exchangeDatatypeHandle = -1;

  private int quoteDatasetHandle = -1;

  private int quoteFileDatatypeHandle = -1;

  private int quoteMemoryDatatypeHandle = -1;

  private final String filename;

  private static final String EXCHANGE_DATASET_NAME = new String("exchange");

  private static final int EXCHANGE_DATASET_RANK = 1;

  private static final String QUOTE_DATASET_NAME = new String("eod");

  private static final int QUOTE_DATASET_RANK = 1;

  // TODO: Investigate this further
  private static final long[] QUOTEDATASET_CHUNK_DIMENSIONS = new long[] { 350 };

  private final HashMap<String, Integer> exchangeGroupHandleMap = new HashMap<String, Integer>();

  private final HashMap<String, Integer> symbolGroupHandleMap = new HashMap<String, Integer>();

  private boolean isOpen;
  
  public Hdf5EodDataSink(String filename) throws EodDataSinkException {

    this.isOpen = false;
    this.filename = filename;

    try {

      H5.H5open();

      File file = new File(filename);
      int flags = HDF5Constants.H5F_ACC_RDWR;
      int access = HDF5Constants.H5P_DEFAULT;

      if (file.exists()) {

        fileHandle = H5.H5Fopen(filename, flags, access);

        if (logger.isInfoEnabled()) {
          StringBuffer logMessageBuffer = new StringBuffer();
          logMessageBuffer.append("Opened existing HDF5 file: ");
          logMessageBuffer.append(filename);
          logger.info(logMessageBuffer.toString());
        }

      } else {

        int create = HDF5Constants.H5P_DEFAULT;
        flags = HDF5Constants.H5F_ACC_EXCL;
        fileHandle = H5.H5Fcreate(filename, flags, create, access);

        if (logger.isInfoEnabled()) {
          StringBuffer logMessageBuffer = new StringBuffer();
          logMessageBuffer.append("Created HDF5 file: ");
          logMessageBuffer.append(filename);
          logger.info(logMessageBuffer.toString());
        }

      }

    } catch (HDF5LibraryException ex) {

      logger.error(ex);
      throw new EodDataSinkException(ex.toString());

    }

    if (fileHandle < 0) {
      StringBuffer messageBuffer = new StringBuffer();
      messageBuffer.append("Unable to open HDF5 file: ");
      messageBuffer.append(filename);
      throw new EodDataSinkException(messageBuffer.toString());
    } else {
      this.isOpen = true;
    }

  }

  public synchronized void updateExchanges(EXCHANGE[] exchanges)
      throws EodDataSinkException {

    if (!isOpen) {
      throw new EodDataSinkException("HDF5 File data sink closed!");
    }
    
    try {

      exchangeDatasetHandle = H5.H5Dopen(fileHandle, "/"
          + EXCHANGE_DATASET_NAME, HDF5Constants.H5P_DEFAULT);

      if (exchangeDatasetHandle < 0) {

        if (logger.isInfoEnabled()) {
          logger
              .info("Failed to open exchange dataset... Attempting to create exchange dataset from scratch.");
        }

        try {
          createExchangeDataset(exchanges.length);
        } catch (HDF5Exception ex) {
          logger.error(ex);
          throw new EodDataSinkException(
              "Failed to create exchange dataset from scratch.");
        }
      }

    } catch (HDF5LibraryException lex) {

      // lex.printStackTrace();

      if (logger.isInfoEnabled()) {
        logger
            .info("Failed to open exchange dataset... Attempting to create exchange dataset from scratch.");
      }

      try {
        createExchangeDataset(exchanges.length);
      } catch (HDF5Exception ex) {
        logger.error(ex);
        throw new EodDataSinkException(
            "Failed to create exchange dataset from scratch.");
      }

    }

    if (exchangeDatasetHandle >= 0) {
      // If we managed to create the exchange dataset OK then
      // create the groups.
      
      Set<String> exchangeSet = readExchanges();
      
      try {
        for (EXCHANGE exchange : exchanges) {
          if (exchangeSet.contains(exchange.getCode())) {
            continue;
          }
          int groupHandle = H5.H5Gcreate(fileHandle, exchange.getCode(),
              HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT,
              HDF5Constants.H5P_DEFAULT);

          if (logger.isDebugEnabled()) {
            StringBuffer messageBuffer = new StringBuffer();
            messageBuffer.append("Created exchange group [ ");
            messageBuffer.append(exchange.getCode());
            messageBuffer.append(" ] ");
            logger.debug(messageBuffer.toString());
          }

          @SuppressWarnings("unused")
          int status = H5.H5Gclose(groupHandle);
        }
      } catch (HDF5LibraryException lex) {
        logger.error(lex);
        throw new EodDataSinkException("Failed to create group");
      }

    } else {
      throw new EodDataSinkException(
          "Unable to either open an existing exchange dataset or create a new exchange dataset.");
    }

  }

  public synchronized void updateExchangeSymbols(String exchange,
      SYMBOL[] symbols) throws EodDataSinkException {

    if (!isOpen) {
      throw new EodDataSinkException("HDF5 File data sink closed!");
    }
    
    int exchangeGroupHandle = -1;
    long createdSymbols = 0;

    try {

      StringBuffer groupNameBuffer = new StringBuffer();
      groupNameBuffer.append("/");
      groupNameBuffer.append(exchange);

      exchangeGroupHandle = H5.H5Gopen(fileHandle, groupNameBuffer.toString(),
          HDF5Constants.H5P_DEFAULT);

    } catch (HDF5LibraryException lex) {

      logger.error(lex);
      StringBuffer messageBuffer = new StringBuffer();
      messageBuffer.append("Failed to open existing exchange group [ ");
      messageBuffer.append(exchange);
      messageBuffer.append(" ] .  Try running updateexchanges command first.");
      throw new EodDataSinkException(messageBuffer.toString());

    }

    for (SYMBOL symbol : symbols) {

      int symbolGroupHandle = -1;
      try {

        symbolGroupHandle = H5.H5Gopen(exchangeGroupHandle, symbol.getCode(),
            HDF5Constants.H5P_DEFAULT);

      } catch (HDF5LibraryException lex) {

        // lex.printStackTrace();
        if (logger.isDebugEnabled()) {
          StringBuffer messageBuffer = new StringBuffer();
          messageBuffer.append("Failed to open existing symbol group [ ");
          messageBuffer.append(symbol.getCode());
          messageBuffer.append(" ] on exchange group [ ");
          messageBuffer.append(exchange);
          messageBuffer.append(" ]. Attempting to create new group.");
          logger.debug(messageBuffer.toString());
        }

        try {
          symbolGroupHandle = H5.H5Gcreate(exchangeGroupHandle,
              symbol.getCode(), HDF5Constants.H5P_DEFAULT,
              HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
          ++createdSymbols;

          if (logger.isDebugEnabled()) {
            StringBuffer messageBuffer = new StringBuffer();
            messageBuffer.append("Created symbol group [ ");
            messageBuffer.append(symbol.getCode());
            messageBuffer.append(" ] on exchange [ ");
            messageBuffer.append(exchange);
            messageBuffer.append(" ] ");

            logger.debug(messageBuffer.toString());
          }
        } catch (HDF5LibraryException ex) {
          logger.error(ex);

          StringBuffer messageBuffer = new StringBuffer();
          messageBuffer.append("Failed to create symbol group [ ");
          messageBuffer.append(symbol.getCode());
          messageBuffer.append(" ] on exchange [ ");
          messageBuffer.append(exchange);
          messageBuffer.append(" ] ");

          throw new EodDataSinkException(messageBuffer.toString());
        }

      }

      if (symbolGroupHandle >= 0) {
        try {
          @SuppressWarnings("unused")
          int status = H5.H5Gclose(symbolGroupHandle);
        } catch (HDF5LibraryException lex) {
          logger.error(lex);
        }
      }
    }

    if (exchangeGroupHandle >= 0) {
      try {
        @SuppressWarnings("unused")
        int status = H5.H5Gclose(exchangeGroupHandle);
      } catch (HDF5LibraryException lex) {
        logger.error(lex);
      }
    }

    if (logger.isInfoEnabled()) {

      StringBuffer messageBuffer = new StringBuffer();
      messageBuffer.append("Created ( ");
      messageBuffer.append(createdSymbols);
      messageBuffer.append(" / ");
      messageBuffer.append(symbols.length);
      messageBuffer.append(" ) new symbol groups. ");

      logger.info(messageBuffer.toString());
    }

  }

  private synchronized void createExchangeDataset(long dimension)
      throws HDF5Exception, EodDataSinkException {

    if (!isOpen) {
      throw new EodDataSinkException("HDF5 File data sink closed!");
    }
    
    long dimensions[] = { dimension };
    long maxDimensions[] = { HDF5Constants.H5S_UNLIMITED };
    int exchangeDataspaceHandle = H5.H5Screate_simple(EXCHANGE_DATASET_RANK,
        dimensions, maxDimensions);

    Hdf5ExchangeDatatype exchangeDatatype = new Hdf5ExchangeDatatype();
    exchangeDatatypeHandle = exchangeDatatype.getFileDatatypeHandle();

    int createProperties = H5.H5Pcreate(HDF5Constants.H5P_DATASET_CREATE);
    @SuppressWarnings("unused")
    int status = H5.H5Pset_chunk(createProperties, EXCHANGE_DATASET_RANK,
        dimensions);

    if ((fileHandle >= 0) && (exchangeDataspaceHandle >= 0)
        && (exchangeDatatypeHandle >= 0)) {

      exchangeDatasetHandle = H5.H5Dcreate(fileHandle, EXCHANGE_DATASET_NAME,
          exchangeDatatypeHandle, exchangeDataspaceHandle,
          HDF5Constants.H5P_DEFAULT, createProperties,
          HDF5Constants.H5P_DEFAULT);
    } else {
      throw new EodDataSinkException(
          "Failed to create exchange dataset from scratch.");
    }

    logger.info("Sucessfully created new exchange dataset.");

  }

  public synchronized void updateExchangeSymbolQuotes(String exchange,
      String symbol, QUOTE[] quotes) throws EodDataSinkException {

    if (!isOpen) {
      throw new EodDataSinkException("HDF5 File data sink closed!");
    }
    
    if (quotes == null) {
      throw new EodDataSinkException("Invalid quote vector.");
    }
    
    if (quotes.length == 0) {
      return;
    }

    final Range<Calendar> newRange = new Range<Calendar>(
        quotes[0].getDateTime(), quotes[quotes.length - 1].getDateTime());
    final Range<Calendar> existingRange = readExchangeSymbolDateRange(exchange,
        symbol);

    if (existingRange != null && newRange.overlapsRange(existingRange)) {
      throw new EodDataSinkException(
          "Supplied quote vector overlaps existing quotes");
    }

    if (existingRange != null
        && newRange.getUpper().compareTo(existingRange.getLower()) < 0) {
      throw new EodDataSinkException(
          "Prepending quotes not supported by this EodDataSink implementation");
    }

    // TODO: write unit tests for this and include a test to ensure
    // contiguousness

    final Hdf5QuoteDatatype quoteData = new Hdf5QuoteDatatype();
    final ExchangeSymbolKey key = new ExchangeSymbolKey(exchange, symbol);
    boolean newlyCreatedDataset = false;

    if (quoteDatasetHandle < 0) {

      int symbolGroupHandle = (Integer) symbolGroupHandleMap.get(key.getKey());
      // then create a new dataset
      createQuoteDataset(quotes.length, symbolGroupHandle);
      newlyCreatedDataset = true;
    }

    if (quoteDatasetHandle >= 0) {

      // We now have a valid dataset handle.
      // Figure out where we should be writing to.
      int memoryDataspaceHandle = -1;
      int fileDataspaceHandle = -1;

      try {

        if (quoteMemoryDatatypeHandle == -1) {
          quoteMemoryDatatypeHandle = Hdf5QuoteDatatype
              .getMemoryDatatypeHandle();
        }

        fileDataspaceHandle = H5.H5Dget_space(quoteDatasetHandle);
        long dimensions[] = new long[1];
        long maxDimensions[] = new long[1];
        long fileWriteOffset = 0;
        @SuppressWarnings("unused")
        int status;

        if (!newlyCreatedDataset) {

          status = H5.H5Sget_simple_extent_dims(fileDataspaceHandle,
              dimensions, maxDimensions);

          // we need to extend the non-empty dataset
          H5.H5Dset_extent(quoteDatasetHandle, new long[] { quotes.length
              + dimensions[0] });

          H5.H5Sclose(fileDataspaceHandle);
          fileDataspaceHandle = H5.H5Dget_space(quoteDatasetHandle);
          fileWriteOffset = dimensions[0];
        }

        final byte[] writeBuffer = new byte[Hdf5QuoteDatatype.QUOTE_DATATYPE_SIZE
            * quotes.length];
        int i = 0;

        for (QUOTE quote : quotes) {

          if (logger.isDebugEnabled()) {
            StringBuffer messageBuffer = new StringBuffer();
            messageBuffer.append("Attempting to write quote: ");
            messageBuffer.append(QuoteHelper.toString(quote));
            logger.debug(messageBuffer.toString());
          }

          quoteData.setDateTime(quote.getDateTime());
          quoteData.setOpen(quote.getOpen());
          quoteData.setHigh(quote.getHigh());
          quoteData.setLow(quote.getLow());
          quoteData.setClose(quote.getClose());
          quoteData.setVolume(quote.getVolume());

          System.arraycopy(quoteData.getByteArray(), 0, writeBuffer,
              Hdf5QuoteDatatype.QUOTE_DATATYPE_SIZE * i,
              Hdf5QuoteDatatype.QUOTE_DATATYPE_SIZE);

          ++i;
        }

        memoryDataspaceHandle = H5.H5Screate_simple(QUOTE_DATASET_RANK,
            new long[] { quotes.length }, new long[] { quotes.length });

        // select a hyperslab for the new data to be written to
        status = H5.H5Sselect_hyperslab(fileDataspaceHandle,
            HDF5Constants.H5S_SELECT_SET, new long[] { fileWriteOffset }, null,
            new long[] { quotes.length }, null);

        // write the data
        status = H5.H5Dwrite(quoteDatasetHandle, quoteMemoryDatatypeHandle,
            memoryDataspaceHandle,
            // HDF5Constants.H5S_ALL,
            fileDataspaceHandle,
            // HDF5Constants.H5S_ALL,
            // HDF5Constants.H5S_ALL,
            HDF5Constants.H5P_DEFAULT, writeBuffer);

        if (logger.isInfoEnabled()) {
          final StringBuffer messageBuffer = new StringBuffer();
          messageBuffer.append("Wrote [ ");
          messageBuffer.append(i);
          messageBuffer.append(" ] quotes to dataset.");
          logger.info(messageBuffer.toString());
        }
      } catch (HDF5Exception ex) {
        logger.error(ex);
        EodDataSinkException e = new EodDataSinkException(
            "Failed to write to file dataset.");
        e.initCause(ex);
        throw e;
      } finally {

        if (logger.isDebugEnabled()) {
          logger
              .debug("Closing memory dataspace, file dataspace and quote dataset...");
        }

        if (memoryDataspaceHandle >= 0) {
          try {
            H5.H5Sclose(memoryDataspaceHandle);
          } catch (Exception ex) {
            logger.error(ex);
          }
        }

        if (fileDataspaceHandle >= 0) {
          try {
            H5.H5Sclose(fileDataspaceHandle);
          } catch (Exception ex) {
            logger.error(ex);
          }
        }

        closeQuoteDataset();

      }
    }

  }

  private void createQuoteDataset(final long dimension, final int locationHandle)
      throws EodDataSinkException {

    final long dimensions[] = { dimension };
    final long maxDimensions[] = { HDF5Constants.H5S_UNLIMITED };
    int quoteDataspaceHandle;

    try {

      quoteDataspaceHandle = H5.H5Screate_simple(QUOTE_DATASET_RANK,
          dimensions, maxDimensions);

    } catch (HDF5Exception e) {
      logger.error(e.toString());
      throw new EodDataSinkException(e);
    }

    quoteFileDatatypeHandle = Hdf5QuoteDatatype.getFileDatatypeHandle();

    int createProperties;

    try {

      createProperties = H5.H5Pcreate(HDF5Constants.H5P_DATASET_CREATE);

    } catch (HDF5Exception e) {
      logger.error(e);
      throw new EodDataSinkException(e);
    }

    try {

      @SuppressWarnings("unused")
      int status = H5.H5Pset_chunk(createProperties, QUOTE_DATASET_RANK,
          QUOTEDATASET_CHUNK_DIMENSIONS);

    } catch (HDF5Exception e) {
      logger.error(e);
      throw new EodDataSinkException(e);
    }

    if ((fileHandle >= 0) && (quoteDataspaceHandle >= 0)
        && (quoteFileDatatypeHandle >= 0)) {

      try {
        quoteDatasetHandle = H5.H5Dcreate(locationHandle, QUOTE_DATASET_NAME,
            quoteFileDatatypeHandle, quoteDataspaceHandle,
            HDF5Constants.H5P_DEFAULT, createProperties,
            HDF5Constants.H5P_DEFAULT);
      } catch (HDF5Exception e) {
        logger.error(e);
      } finally {

        try {

          H5.H5Sclose(quoteDataspaceHandle);

        } catch (HDF5Exception e) {
          logger.error(e);
          throw new EodDataSinkException(e);
        }

      }

    } else {
      throw new EodDataSinkException(
          "Failed to create quote dataset from scratch.");
    }

    logger.info("Sucessfully created new quote dataset.");

  }

  private void openQuoteDataset(String exchange, String symbol) {

    closeQuoteDataset();

    Integer exchangeGroupHandle = (Integer) exchangeGroupHandleMap
        .get(exchange);
    if (exchangeGroupHandle == null) {
      try {
        exchangeGroupHandle = H5.H5Gopen(fileHandle, exchange,
            HDF5Constants.H5P_DEFAULT);
        exchangeGroupHandleMap.put(exchange, exchangeGroupHandle);
      } catch (HDF5LibraryException lex) {
        StringBuffer messageBuffer = new StringBuffer();
        messageBuffer.append("Failed to open exchange group for [ ");
        messageBuffer.append(exchange);
        messageBuffer.append(" ]");
        logger.info(messageBuffer.toString());
        return;
      }
    }

    final ExchangeSymbolKey key = new ExchangeSymbolKey(exchange, symbol);

    Integer symbolGroupHandle = (Integer) symbolGroupHandleMap
        .get(key.getKey());
    if (symbolGroupHandle == null) {
      try {
        symbolGroupHandle = H5.H5Gopen(exchangeGroupHandle, symbol,
            HDF5Constants.H5P_DEFAULT);
        symbolGroupHandleMap.put(key.getKey(), symbolGroupHandle);
      } catch (HDF5LibraryException lex) {
        StringBuffer messageBuffer = new StringBuffer();
        messageBuffer.append("Failed to open symbol group for [ ");
        messageBuffer.append(symbol);
        messageBuffer.append(" ]");
        logger.info(messageBuffer.toString());
        return;
      }
    }

    try {

      quoteDatasetHandle = H5.H5Dopen(symbolGroupHandle, QUOTE_DATASET_NAME,
          HDF5Constants.H5P_DEFAULT);

      if (quoteDatasetHandle < 0) {

        if (logger.isInfoEnabled()) {
          logger.info("Failed to open quote dataset...");
        }

      }

    } catch (HDF5LibraryException lex) {

      // lex.printStackTrace();

      if (logger.isInfoEnabled()) {
        logger.info("Failed to open quote dataset...");
      }

    }

  }

  private void closeQuoteDataset() {

    if (quoteDatasetHandle >= 0) {
      try {
        H5.H5Dclose(quoteDatasetHandle);
        quoteDatasetHandle = -1;
      } catch (Exception ex) {
        logger.error(ex);
      }
    }
  }

  public synchronized void close() {

    if (!isOpen) {
      return;
    }
    
    Iterator<Entry<String, Integer>> it = symbolGroupHandleMap.entrySet()
        .iterator();
    while (it.hasNext()) {
      Map.Entry<String, Integer> kvp = (Map.Entry<String, Integer>) it.next();
      try {
        H5.H5Gclose((Integer) kvp.getValue());
      } catch (HDF5LibraryException ex) {
        logger.error(ex);
      }
    }

    it = exchangeGroupHandleMap.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String, Integer> kvp = (Map.Entry<String, Integer>) it.next();
      try {
        H5.H5Gclose((Integer) kvp.getValue());
      } catch (HDF5LibraryException ex) {
        logger.error(ex);
      }
    }

    if (exchangeDatasetHandle >= 0) {
      try {
        H5.H5Dclose(exchangeDatasetHandle);
      } catch (HDF5LibraryException ex) {
        logger.error(ex);
      }
    }

    if (rootGroupHandle >= 0) {
      try {
        H5.H5Gclose(rootGroupHandle);
      } catch (HDF5LibraryException ex) {
        logger.error(ex);
      }
    }

    try {
      Hdf5QuoteDatatype.close();
    } catch (EodDataSinkException e) {
      logger.error(e);
    }

    if (fileHandle >= 0) {
      try {
        H5.H5Fclose(fileHandle);
        if (logger.isInfoEnabled()) {
          StringBuffer logMessageBuffer = new StringBuffer();
          logMessageBuffer.append("Closed HDF5 file: ");
          logMessageBuffer.append(filename);
          logger.info(logMessageBuffer.toString());
        }

      } catch (HDF5LibraryException ex) {
        logger.error(ex);
      }
    }

    try {
      H5.H5close();

      if (logger.isDebugEnabled()) {
        logger.debug("Closed HDF5 library");
      }
    } catch (HDF5LibraryException ex) {
      logger.error(ex);
    }

    this.isOpen = false; 
  }

  public Range<Calendar> readExchangeSymbolDateRange(String exchange,
      String symbol) throws EodDataSinkException {

    if (!isOpen) {
      throw new EodDataSinkException("HDF5 File data sink closed!");
    }
    
    openQuoteDataset(exchange, symbol);
    if (quoteDatasetHandle < 0) {
      return null;
    }

    try {
      int fileDataspaceHandle = H5.H5Dget_space(quoteDatasetHandle);
      long dimensions[] = new long[1];
      long maxDimensions[] = new long[1];

      @SuppressWarnings("unused")
      int status = H5.H5Sget_simple_extent_dims(fileDataspaceHandle,
          dimensions, maxDimensions);

      // TODO: handle the case where we only have a single element
      if (dimensions[0] > 1) {

        final byte[] readBuffer = new byte[Hdf5QuoteDatatype.QUOTE_DATATYPE_SIZE];

        int memoryDataspace = H5.H5Screate_simple(1, new long[] { 1 },
            new long[] { 1 });

        // selects the first element
        status = H5.H5Sselect_hyperslab(fileDataspaceHandle,
            HDF5Constants.H5S_SELECT_SET, new long[] { 0 }, null,
            new long[] { 1 }, null);

        H5.H5Dread(quoteDatasetHandle,
            Hdf5QuoteDatatype.getMemoryDatatypeHandle(), memoryDataspace,
            fileDataspaceHandle, HDF5Constants.H5P_DEFAULT, readBuffer);

        Hdf5QuoteDatatype[] quotes = Hdf5QuoteDatatype.createArray(readBuffer);

        Hdf5QuoteDatatype quote1 = quotes[0];

        // selects the final element
        status = H5.H5Sselect_hyperslab(fileDataspaceHandle,
            HDF5Constants.H5S_SELECT_SET, new long[] { dimensions[0] - 1 },
            null, new long[] { 1 }, null);

        H5.H5Dread(quoteDatasetHandle,
            Hdf5QuoteDatatype.getMemoryDatatypeHandle(), memoryDataspace,
            fileDataspaceHandle, HDF5Constants.H5P_DEFAULT, readBuffer);

        quotes = Hdf5QuoteDatatype.createArray(readBuffer);

        Hdf5QuoteDatatype quote2 = quotes[0];

        H5.H5Sclose(memoryDataspace);

        final StringBuffer messageBuffer = new StringBuffer();
        messageBuffer.append("Sink contains data from [ ");
        messageBuffer.append(quote1.getDateTime().getTime().toString());
        messageBuffer.append(" ] to [ ");
        messageBuffer.append(quote2.getDateTime().getTime().toString());
        messageBuffer.append(" ]");
        logger.info(messageBuffer.toString());

        return new Range<Calendar>(quote1.getDateTime(), quote2.getDateTime());
      } else {

        return null;

      }

    } catch (HDF5LibraryException e) {
      logger.error(e);
      throw new EodDataSinkException("Failed to read from the quote dataset");
    } catch (HDF5Exception e) {
      logger.error(e);
      throw new EodDataSinkException("Failed to read from the quote dataset");
    }

  }

  public String[] readExchangeSymbols(String exchange)
      throws EodDataSinkException {

    if (!isOpen) {
      throw new EodDataSinkException("HDF5 File data sink closed!");
    }
    
    Integer exchangeGroupHandle = (Integer) exchangeGroupHandleMap
        .get(exchange);

    String[] symbols = null;

    try {
      if (exchangeGroupHandle == null) {
        exchangeGroupHandle = H5.H5Gopen(fileHandle, exchange,
            HDF5Constants.H5P_DEFAULT);
        exchangeGroupHandleMap.put(exchange, exchangeGroupHandle);
      }

      final H5L_iterate_cb iter_cb = new H5L_iter_callbackT();
      final opdata od = new opdata();
      @SuppressWarnings("unused")
      int status = H5.H5Literate(exchangeGroupHandle,
          HDF5Constants.H5_INDEX_NAME, HDF5Constants.H5_ITER_NATIVE, 0L,
          iter_cb, od);
      symbols = ((H5L_iter_callbackT) iter_cb).getSymbols();
      if (symbols == null || symbols.length <= 0) {
        throw new EodDataSinkException(
            "Couldn't find any symbols for this exchange.");
      }
    } catch (HDF5LibraryException lex) {
      StringBuffer messageBuffer = new StringBuffer();
      messageBuffer.append("Failed to iterate over exchanges  ");
      messageBuffer.append(exchange);
      messageBuffer.append(" ]");
      EodDataSinkException e = new EodDataSinkException(
          messageBuffer.toString());
      e.initCause(lex);
      throw e;
    }

    return symbols;
  }

  public Set<String> readExchanges() throws EodDataSinkException {

    if (!isOpen) {
      throw new EodDataSinkException("HDF5 File data sink closed!");
    }
    
    String[] exchanges = null;

    try {
      rootGroupHandle = H5.H5Gopen(fileHandle, "/", HDF5Constants.H5P_DEFAULT);

      final H5L_iterate_cb iter_cb = new H5L_iter_callbackT();
      final opdata od = new opdata();
      @SuppressWarnings("unused")
      int status = H5.H5Literate(rootGroupHandle, HDF5Constants.H5_INDEX_NAME,
          HDF5Constants.H5_ITER_NATIVE, 0L, iter_cb, od);
      exchanges = ((H5L_iter_callbackT) iter_cb).getSymbols();
      
    } catch (HDF5LibraryException lex) {
      StringBuffer messageBuffer = new StringBuffer();
      messageBuffer.append("Failed to iterate over exchanges!");
      EodDataSinkException e = new EodDataSinkException(
          messageBuffer.toString());
      e.initCause(lex);
      throw e;
    }

    return new HashSet<String>(Arrays.asList(exchanges));
  }

  @Override
  public void destroy() throws Exception {

    close();

  }

}
