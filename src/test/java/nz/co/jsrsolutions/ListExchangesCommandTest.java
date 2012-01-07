
package nz.co.jsrsolutions.ds3;

import junit.framework.TestCase;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;

public class ListExchangesCommandTest extends TestCase {

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