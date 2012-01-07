/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

package nz.co.jsrsolutions.ds3;


import java.lang.String;

class CommandException extends Exception
{
  private String mistake;
//----------------------------------------------
// Default constructor - initializes instance variable to unknown
  public CommandException()
  {
    super();             // call superclass constructor
    mistake = "unknown";
  }
  
//-----------------------------------------------
// Constructor receives some kind of message that is saved in an instance variable.
  public CommandException(String err)
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
  