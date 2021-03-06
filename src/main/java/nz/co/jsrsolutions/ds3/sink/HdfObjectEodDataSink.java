/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)HdfObjectEodDataSink.java        
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.hdf5lib.exceptions.HDF5LibraryException;
import nz.co.jsrsolutions.ds3.DataStub.EXCHANGE;
import nz.co.jsrsolutions.ds3.DataStub.QUOTE;
import nz.co.jsrsolutions.ds3.DataStub.SYMBOL;
import nz.co.jsrsolutions.ds3.QuoteHelper;
import nz.co.jsrsolutions.util.Range;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;

class HdfObjectEodDataSink implements EodDataSink {

  private static final transient Logger logger = Logger.getLogger(Hdf5EodDataSink.class);

  private final int fileHandle;

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
  private static final long[] QUOTEDATASET_CHUNK_DIMENSIONS = new long[] { 300 };

  private final HashMap<String, Integer> exchangeGroupHandleMap = new HashMap<String, Integer>(); 

  private final HashMap<String, Integer> symbolGroupHandleMap = new HashMap<String, Integer>(); 

  public HdfObjectEodDataSink(String filename) throws EodDataSinkException {

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

      }
      else {

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

    }
    catch (HDF5LibraryException ex) {

      logger.error(ex);
      throw new EodDataSinkException(ex.toString());

    }

    if (fileHandle < 0) {
      StringBuffer messageBuffer = new StringBuffer();
      messageBuffer.append("Unable to open HDF5 file: ");
      messageBuffer.append(filename);
      throw new EodDataSinkException(messageBuffer.toString());
    }

  }

  public void updateExchanges(EXCHANGE[] exchanges) throws EodDataSinkException {

    try {

      exchangeDatasetHandle = H5.H5Dopen(fileHandle, "/" + EXCHANGE_DATASET_NAME, HDF5Constants.H5P_DEFAULT);

      if (exchangeDatasetHandle < 0) {

        if (logger.isInfoEnabled()) {
          logger.info("Failed to open exchange dataset... Attempting to create exchange dataset from scratch.");
        }

        try {
          createExchangeDataset(exchanges.length);
        }
        catch (HDF5Exception ex) {
          logger.error(ex);
          throw new EodDataSinkException("Failed to create exchange dataset from scratch.");
        }
      }


    }
    catch (HDF5LibraryException lex) {
    
      //      lex.printStackTrace();

      if (logger.isInfoEnabled()) {
        logger.info("Failed to open exchange dataset... Attempting to create exchange dataset from scratch.");
      }

      try {
        createExchangeDataset(exchanges.length);
      }
      catch (HDF5Exception ex) {
        logger.error(ex);
        throw new EodDataSinkException("Failed to create exchange dataset from scratch.");
      }

    }

    if (exchangeDatasetHandle >= 0) {
      // If we managed to create the exchange dataset OK then
      // create the groups.
      try {
        for (EXCHANGE exchange : exchanges) {
          int groupHandle = H5.H5Gcreate(fileHandle,
                                         exchange.getCode(),
                                         HDF5Constants.H5P_DEFAULT,
                                         HDF5Constants.H5P_DEFAULT,
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
      }
      catch (HDF5LibraryException lex) {
        logger.error(lex);
        throw new EodDataSinkException("Failed to create group");
      }

    }
    else {
      throw new EodDataSinkException("Unable to either open an existing exchange dataset or create a new exchange dataset.");
    }

  }

  public void updateExchangeSymbols(String exchange, SYMBOL[] symbols) throws EodDataSinkException {

    int exchangeGroupHandle = -1;
    long createdSymbols = 0;

    try {

      StringBuffer groupNameBuffer = new StringBuffer();
      groupNameBuffer.append("/");
      groupNameBuffer.append(exchange);

      exchangeGroupHandle = H5.H5Gopen(fileHandle, groupNameBuffer.toString(), HDF5Constants.H5P_DEFAULT);

    }
    catch (HDF5LibraryException lex) {
    
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

        symbolGroupHandle = H5.H5Gopen(exchangeGroupHandle, symbol.getCode(), HDF5Constants.H5P_DEFAULT);

      }
      catch (HDF5LibraryException lex) {
    
        //        lex.printStackTrace();
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
                                           symbol.getCode(),
                                           HDF5Constants.H5P_DEFAULT,
                                           HDF5Constants.H5P_DEFAULT,
                                           HDF5Constants.H5P_DEFAULT);
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
        }
        catch (HDF5LibraryException ex) {
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
        }
        catch (HDF5LibraryException lex) {
          logger.error(lex);
        }
      }
    }

    if (exchangeGroupHandle >= 0) {
      try {
        @SuppressWarnings("unused")
        int status = H5.H5Gclose(exchangeGroupHandle);
      }
      catch (HDF5LibraryException lex) {
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


  private void createExchangeDataset(long dimension) throws HDF5Exception, EodDataSinkException {
  
    long dimensions[] = { dimension };
    long maxDimensions[] = { HDF5Constants.H5S_UNLIMITED };
    int exchangeDataspaceHandle = H5.H5Screate_simple(EXCHANGE_DATASET_RANK, dimensions, maxDimensions);

    Hdf5ExchangeDatatype exchangeDatatype = new Hdf5ExchangeDatatype();
    exchangeDatatypeHandle = exchangeDatatype.getFileDatatypeHandle();

    int createProperties = H5.H5Pcreate(HDF5Constants.H5P_DATASET_CREATE);
    @SuppressWarnings("unused")
    int status = H5.H5Pset_chunk(createProperties, EXCHANGE_DATASET_RANK, dimensions);

    if ((fileHandle >= 0)
        && (exchangeDataspaceHandle >= 0)
        && (exchangeDatatypeHandle >= 0)) {

      exchangeDatasetHandle = H5.H5Dcreate(fileHandle,
                                           EXCHANGE_DATASET_NAME,
                                           exchangeDatatypeHandle,
                                           exchangeDataspaceHandle,
                                           HDF5Constants.H5P_DEFAULT,
                                           createProperties,
                                           HDF5Constants.H5P_DEFAULT);
    }
    else {
      throw new EodDataSinkException("Failed to create exchange dataset from scratch.");
    }

    logger.info("Sucessfully created new exchange dataset.");

  }

  public void updateExchangeSymbolQuotes(String exchange,
                                         String symbol,
                                         QUOTE[] quotes) throws EodDataSinkException {

    if (quotes == null) {
      throw new EodDataSinkException("Invalid quote vector.");
    }

    @SuppressWarnings("unused")
    long nWrittenToDataset = 0;
    Hdf5QuoteDatatype quoteData = new Hdf5QuoteDatatype();

    openQuoteDataset(exchange, symbol);

    if (quoteDatasetHandle >= 0) {
          
      // We now have a valid dataset handle.
      // Figure out where we should be writing to.
      int memoryDataspaceHandle = -1;
      int fileDataspaceHandle = -1;

      try {

        if (quoteMemoryDatatypeHandle == -1) {
          quoteMemoryDatatypeHandle = Hdf5QuoteDatatype.getMemoryDatatypeHandle();
        }
        
        fileDataspaceHandle = H5.H5Dget_space(quoteDatasetHandle);
        long dimensions[] = new long[1];
        long maxDimensions[] = new long[1];
        @SuppressWarnings("unused")
        int status = H5.H5Sget_simple_extent_dims(fileDataspaceHandle, dimensions, maxDimensions);
        long fileWriteOffset = 0;

        if (dimensions[0] == 0) {
          // we have a new, empty dataset
          H5.H5Dset_extent(quoteDatasetHandle,
                           new long[] { quotes.length });

          fileDataspaceHandle = H5.H5Dget_space(quoteDatasetHandle);

        }
        else {

          /*          
          scalarQuoteDataspaceHandle = H5.H5Screate(HDF5Constants.H5S_SCALAR);
          long fileEndOffset = dimensions[0] - 1;
          H5.H5Soffset_simple(scalarQuoteDataspaceHandle, new long[] { fileEndOffset });
          fileWriteOffset = fileEndOffset;
          
          final byte[] readBuffer = new byte[Hdf5QuoteDatatype.QUOTE_DATATYPE_SIZE];

          H5.H5Dread(quoteDatasetHandle,
                     quoteMemoryDatatypeHandle,
                     scalarQuoteDataspaceHandle,
                     scalarQuoteDataspaceHandle,
                     HDF5Constants.H5P_DEFAULT,
                     readBuffer);

          H5.H5Sclose(scalarQuoteDataspaceHandle);
          */
          logger.info("whoop!");

          readExchangeSymbolQuotes(exchange, symbol);

        }

        // extend the dataset to accomodate the new data

        // select a hyperslab for the new data to be written to
        if (fileWriteOffset == 0) {

          final byte[] writeBuffer = new byte[Hdf5QuoteDatatype.QUOTE_DATATYPE_SIZE * quotes.length];
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

            System.arraycopy(quoteData.getByteArray(),
                             0,
                             writeBuffer,
                             Hdf5QuoteDatatype.QUOTE_DATATYPE_SIZE * i,
                             Hdf5QuoteDatatype.QUOTE_DATATYPE_SIZE);

            ++i;
          }

          memoryDataspaceHandle = H5.H5Screate_simple(QUOTE_DATASET_RANK,
                                                      new long[] { quotes.length },
                                                      new long[] { quotes.length });


          status = H5.H5Sselect_hyperslab(fileDataspaceHandle,
                                          HDF5Constants.H5S_SELECT_SET,
                                          new long[] { 0 },
                                          null,
                                          new long[] { quotes.length },
                                          null);

          // write the data
          status  = H5.H5Dwrite(quoteDatasetHandle,
                                quoteMemoryDatatypeHandle,
                                //                                memoryDataspaceHandle,
                                HDF5Constants.H5S_ALL,
                                //                                fileDataspaceHandle,
                                HDF5Constants.H5S_ALL,
                                //                                HDF5Constants.H5S_ALL,
                                HDF5Constants.H5P_DEFAULT,
                                writeBuffer);


        }
      }
      catch(HDF5Exception ex) {
        logger.error(ex);
        EodDataSinkException e = new EodDataSinkException("Failed to write to file dataset.");
        e.initCause(ex);
        throw e;
      }
      finally {

        if (logger.isDebugEnabled()) {
          logger.debug("Closing memory dataspace, file dataspace and quote dataset...");
        }

        if (memoryDataspaceHandle >= 0) {
          try {
            H5.H5Sclose(memoryDataspaceHandle);
          }
          catch(Exception ex) {
            logger.error(ex);
          }
        }

        if (fileDataspaceHandle >= 0) {
          try {
            H5.H5Sclose(fileDataspaceHandle);
          }
          catch(Exception ex) {
            logger.error(ex);
          }
        }

        if (quoteDatasetHandle >= 0) {
          try {
            H5.H5Dclose(quoteDatasetHandle);
          }
          catch(Exception ex) {
            logger.error(ex);
          }
        }

      }
    }

  }

  private void createQuoteDataset(long dimension, int locationHandle) throws HDF5Exception, EodDataSinkException {
  
    long dimensions[] = { dimension };
    long maxDimensions[] = { HDF5Constants.H5S_UNLIMITED };
    
    int quoteDataspaceHandle = H5.H5Screate_simple(QUOTE_DATASET_RANK, dimensions, maxDimensions);
    quoteFileDatatypeHandle = Hdf5QuoteDatatype.getFileDatatypeHandle();
    quoteMemoryDatatypeHandle = Hdf5QuoteDatatype.getMemoryDatatypeHandle();

    int createProperties = H5.H5Pcreate(HDF5Constants.H5P_DATASET_CREATE);
    @SuppressWarnings("unused")
    int status = H5.H5Pset_chunk(createProperties, QUOTE_DATASET_RANK, QUOTEDATASET_CHUNK_DIMENSIONS);

    if ((fileHandle >= 0)
        && (quoteDataspaceHandle >= 0)
        && (quoteFileDatatypeHandle >= 0)) {

      try {
        quoteDatasetHandle = H5.H5Dcreate(locationHandle,
                                          QUOTE_DATASET_NAME,
                                          quoteFileDatatypeHandle,
                                          quoteDataspaceHandle,
                                          HDF5Constants.H5P_DEFAULT,
                                          createProperties,
                                          HDF5Constants.H5P_DEFAULT);
      }
      catch (HDF5Exception e) {
        throw e;
      }
      finally {
        H5.H5Sclose(quoteDataspaceHandle);
      }
      
    }
    else {
      throw new EodDataSinkException("Failed to create exchange dataset from scratch.");
    }

    logger.info("Sucessfully created new quote dataset.");


  }

  private void openQuoteDataset(String exchange, String symbol) throws EodDataSinkException {

    Integer exchangeGroupHandle = (Integer)exchangeGroupHandleMap.get(exchange);
    if (exchangeGroupHandle == null) {
      try {
        exchangeGroupHandle = H5.H5Gopen(fileHandle, exchange, HDF5Constants.H5P_DEFAULT);
        exchangeGroupHandleMap.put(exchange, exchangeGroupHandle);
      }
      catch (HDF5LibraryException lex) {
        StringBuffer messageBuffer = new StringBuffer();
        messageBuffer.append("Failed to open exchange group for [ ");
        messageBuffer.append(exchange);
        messageBuffer.append(" ]");
        EodDataSinkException e = new EodDataSinkException(messageBuffer.toString());
        e.initCause(lex);
        throw e;
      }
    }
        
    StringBuffer symbolKeyBuffer = new StringBuffer();
    symbolKeyBuffer.append(exchange);
    symbolKeyBuffer.append("-");
    symbolKeyBuffer.append(symbol);

    Integer symbolGroupHandle = (Integer)symbolGroupHandleMap.get(symbolKeyBuffer.toString());
    if (symbolGroupHandle == null) {
      try {
        symbolGroupHandle = H5.H5Gopen(exchangeGroupHandle, symbol, HDF5Constants.H5P_DEFAULT);
        symbolGroupHandleMap.put(symbolKeyBuffer.toString(), symbolGroupHandle);
      }
      catch (HDF5LibraryException lex) {
        StringBuffer messageBuffer = new StringBuffer();
        messageBuffer.append("Failed to open symbol group for [ ");
        messageBuffer.append(symbol);
        messageBuffer.append(" ]");
        EodDataSinkException e = new EodDataSinkException(messageBuffer.toString());
        e.initCause(lex);
        throw e;
      }
    }
      
    try {

      quoteDatasetHandle = H5.H5Dopen(symbolGroupHandle, QUOTE_DATASET_NAME, HDF5Constants.H5P_DEFAULT);

      if (quoteDatasetHandle < 0) {

        if (logger.isInfoEnabled()) {
          logger.info("Failed to open quote dataset... Attempting to create quote dataset from scratch.");
        }

        try {
          createQuoteDataset(0, symbolGroupHandle);
        }
        catch (HDF5Exception ex) {
          logger.error(ex);
          throw new EodDataSinkException("Failed to create quote dataset from scratch.");
        }
      }


    }
    catch (HDF5LibraryException lex) {
    
      //      lex.printStackTrace();

      if (logger.isInfoEnabled()) {
        logger.info("Failed to open quote dataset... Attempting to create quote dataset from scratch.");
      }

      try {
        createQuoteDataset(0, symbolGroupHandle);
      }
      catch (HDF5Exception ex) {
        logger.error(ex);
        throw new EodDataSinkException("Failed to create quote dataset from scratch.");
      }

    }

  }

  private void readExchangeSymbolQuotes(String exchange, String symbol) throws EodDataSinkException {
    

    openQuoteDataset(exchange, symbol);

    try {
    int fileDataspaceHandle = H5.H5Dget_space(quoteDatasetHandle);
    long dimensions[] = new long[1];
    long maxDimensions[] = new long[1];
    @SuppressWarnings("unused")
    int status = H5.H5Sget_simple_extent_dims(fileDataspaceHandle, dimensions, maxDimensions);
    
    final byte[] readBuffer = new byte[Hdf5QuoteDatatype.QUOTE_DATATYPE_SIZE * (int)dimensions[0]];

    H5.H5Dread(quoteDatasetHandle,
               HDF5Constants.H5T_NATIVE_INT,
               HDF5Constants.H5S_ALL,
               HDF5Constants.H5S_ALL,
               HDF5Constants.H5P_DEFAULT,
               readBuffer);
    }
    catch (HDF5Exception e) {
      throw new EodDataSinkException();
    }

  }

  public void close() {

    Iterator<Entry<String, Integer>> it = symbolGroupHandleMap.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String,Integer> kvp = (Map.Entry<String,Integer>)it.next();
      try {
        H5.H5Gclose((Integer)kvp.getValue());
      }
      catch (HDF5LibraryException ex) {
        logger.error(ex);
      }
    }

    it = exchangeGroupHandleMap.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String,Integer> kvp = (Map.Entry<String,Integer>)it.next();
      try {
        H5.H5Gclose((Integer)kvp.getValue());
      }
      catch (HDF5LibraryException ex) {
        logger.error(ex);
      }
    }

    if (exchangeDatasetHandle >= 0) {
      try {
        H5.H5Dclose(exchangeDatasetHandle);
      }
      catch (HDF5LibraryException ex) {
        logger.error(ex);
      }
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

      }
      catch (HDF5LibraryException ex) {
        logger.error(ex);
      }
    }


    try {
      H5.H5close();

      if (logger.isDebugEnabled()) {
        logger.debug("Closed HDF5 library");
      }
    }
    catch (HDF5LibraryException ex) {
      logger.error(ex);
    }

  }

  public String[] readExchangeSymbols(String exchange) throws EodDataSinkException {
    throw new NotImplementedException();
  }

  public Range<Calendar> readExchangeSymbolDateRange(String exchange, String symbol) {
    throw new NotImplementedException();
  }


}
