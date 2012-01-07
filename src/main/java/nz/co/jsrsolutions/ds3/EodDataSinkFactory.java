
package nz.co.jsrsolutions.ds3;

import java.lang.String;

import org.apache.commons.configuration.HierarchicalConfiguration;

final class EodDataSinkFactory {

  private static final String HDF5EODDATASINK_KEY = new String("hdf5");

  public static EodDataSink create(HierarchicalConfiguration appConfig, String type) throws EodDataSinkException {
    
    if (type.compareTo(HDF5EODDATASINK_KEY) == 0) {
      
      return new Hdf5EodDataSink(appConfig.getString("eodDataSinks.eodDataSink(0).filename"));

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