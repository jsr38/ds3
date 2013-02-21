/* -*- mode: java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */

/*
 * @(#)DefaultEmailService.java
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

package nz.co.jsrsolutions.util;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;

public class DefaultEmailService implements EmailService {

  private static final transient Logger logger = Logger.getLogger(EmailService.class);

  private Email email = new SimpleEmail();
  
  public DefaultEmailService(String smtphost,
                             int    smtpport,
                             String from,
                             String recipients) throws ServiceException {
    

    try {
      email.setHostName(smtphost);
      email.setSmtpPort(smtpport);
      //          email.setAuthenticator(new DefaultAuthenticator("username", "password"));
      //          email.setTLS(true);
      email.setFrom(from);
      email.addTo(recipients);
    }
    catch (EmailException e) {
      ServiceException se = new ServiceException("Failed to initialise Apache Commons email subsystem.");
      se.initCause(e);
      throw se;
    }
  }
  
  public void send(String subject, String message) {
    
    try {
      email.setSubject(subject);
      email.setMsg(message);
      email.send();

      final StringBuffer logMessage = new StringBuffer();
      logMessage.append("Sent mail to: ");

      for(Object address : email.getToAddresses()) {
        logMessage.append(((javax.mail.internet.InternetAddress)address).toString());
        logMessage.append(";");
      }

      logMessage.append(" with subject: ");
      logMessage.append(subject);
      logMessage.append(" [ ");
      logMessage.append(message);
      logMessage.append(" ] ");
      logger.info(logMessage.toString());
      
    }
    catch (EmailException e) {
      
      final StringBuffer logMessage = new StringBuffer();
      logMessage.append("Failed sending message to: ");

      for(Object address : email.getToAddresses()) {
        logMessage.append(((javax.mail.internet.InternetAddress)address).toString());
        logMessage.append(";");
      }

      logMessage.append(" with subject: ");
      logMessage.append(subject);
      logMessage.append(" [ ");
      logMessage.append(message);
      logMessage.append(" ] ");
      logger.error(logMessage.toString());

      logger.error(e);

    }

    
  }
  
}
