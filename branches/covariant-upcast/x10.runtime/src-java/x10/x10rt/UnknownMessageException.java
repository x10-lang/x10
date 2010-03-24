/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.x10rt;

/**
 * An exception raised by the {@link MessageRegistry} when it is unable
 * to find an ActiveMessage that has been registered for an argument Method.
 */
public class UnknownMessageException extends Exception {

  private static final long serialVersionUID = 6184004558021405106L;

  public UnknownMessageException(String message) {
    super(message);
  }

  public UnknownMessageException(Exception cause) {
    super(cause);
  }

}
