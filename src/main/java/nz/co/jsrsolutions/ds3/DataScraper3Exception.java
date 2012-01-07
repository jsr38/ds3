
package nz.co.jsrsolutions.ds3;


import java.lang.String;

class DataScraper3Exception extends Exception
{
  private String mistake;
//----------------------------------------------
// Default constructor - initializes instance variable to unknown
  public DataScraper3Exception()
  {
    super();             // call superclass constructor
    mistake = "unknown";
  }
  
//-----------------------------------------------
// Constructor receives some kind of message that is saved in an instance variable.
  public DataScraper3Exception(String err)
  {
    super(err);     // call super class constructor
    mistake = err;  // save message
  }
  
//------------------------------------------------  
// public method, callable by exception catcher. It returns the error message.
  public String getError()
  {
    return mistake;
  }
}
  