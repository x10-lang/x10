/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package apgas.impl;

/**
 * A {@link DeadPlaceError} is thrown when attempting a finish state change from
 * a place that is considered dead by the finish.
 */
public class DeadPlaceError extends Error {
  private static final long serialVersionUID = -6291716310951978192L;

  /**
   * Constructs a new {@link DeadPlaceError}.
   */
  public DeadPlaceError() {
  }
}
