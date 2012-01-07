

package nz.co.jsrsolutions.ds3;

import java.lang.String;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;


final class CommandLineOptions {

  private static final Logger logger = Logger.getLogger(CommandLineOptions.class);

  public static final String HELP = new String("help");

  public static final String CONFIGFILE = new String("configfile");

  public static final String PROVIDER = new String("provider");

  public static final String SINK = new String("sink");

  public static final String COMMAND = new String("command");

  public static final String EXCHANGE = new String("exchange");

  public static final String SYMBOL = new String("symbol");

  public static final Options Options = new Options();

  static {
    
      Option help = OptionBuilder
	  .withDescription("display help")
	  .create(HELP);

      Option configFile = OptionBuilder
	  .withArgName("file")
	  .hasArg()
	  .isRequired()
	  .withDescription("specify configuration file")
	  .create(CONFIGFILE);

      Option provider =  OptionBuilder
	  .withArgName("provider")
	  .hasArg()
	  .isRequired()
	  .withDescription("specify data provider")
	  .create(PROVIDER);

      Option sink = OptionBuilder
	  .withArgName("sink")
	  .hasArg()
	  .isRequired()
	  .withDescription("specify data sink")
	  .create(SINK);

      Option command = OptionBuilder
	  .withArgName("command")
	  .hasArg()
	  .withDescription("specify command to execute")
	  .create(COMMAND);

    Option exchange = OptionBuilder
	.withArgName("exchange")
	.hasArg()
	.isRequired(false)
	.withDescription("specify exchange")
	.create(EXCHANGE);
    
    Option symbol = OptionBuilder
	.withArgName("symbol")
	.hasArg()
	.isRequired(false)
	.withDescription("specify symbol")
	.create(SYMBOL);

    logger.debug("Registering command line options:");
    logger.debug(help.toString());
    logger.debug(configFile.toString());
    logger.debug(provider.toString());
    logger.debug(sink.toString());
    logger.debug(command.toString());
    logger.debug(exchange.toString());
    logger.debug(symbol.toString());
        
    Options.addOption(help);
    Options.addOption(configFile);
    Options.addOption(provider);
    Options.addOption(sink);
    Options.addOption(command);
    Options.addOption(exchange);
    Options.addOption(symbol);

  }

  private CommandLineOptions() {

  }

}