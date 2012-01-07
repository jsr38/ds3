
package nz.co.jsrsolutions.ds3;

import java.lang.String;

import org.apache.commons.configuration.HierarchicalConfiguration;

final class EodDataProviderFactory {

  private static final String EODDATADATAPROVIDER_KEY = new String("eoddata");

  public static EodDataProvider create(HierarchicalConfiguration appConfig, String type) throws EodDataProviderException {
    
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