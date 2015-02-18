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

package apgas;

/**
 * A {@link DeadPlaceException} is thrown when attempting to interact with a
 * dead {@link Place}.
 */
public class DeadPlaceException extends RuntimeException {
  private static final long serialVersionUID = -4113514316492737844L;

  /**
   * The dead place.
   */
  public final Place place;

  /**
   * Constructs a new {@link DeadPlaceException}.
   *
   * @param place
   *          the dead place
   */
  public DeadPlaceException(Place place) {
    this.place = place;
  }
}
