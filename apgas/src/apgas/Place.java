/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package apgas;

import java.io.Serializable;

/**
 * The {@link Place} class represents an APGAS place.
 */
public class Place implements Serializable, Comparable<Place> {
  private static final long serialVersionUID = -7210312031258955537L;

  /**
   * The integer ID of this place.
   */
  public final int id;

  /**
   * Constructs a {@link Place} with the specified ID.
   * <p>
   * Prefer {@link Constructs#place(int)} to avoid constructing unnecessary
   * {@link Place} objects.
   *
   * @param id
   *          the desired place ID
   */
  public Place(int id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "place(" + id + ")";
  }

  @Override
  public boolean equals(Object that) {
    return that instanceof Place ? id == ((Place) that).id : false;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(id);
  }

  @Override
  public int compareTo(Place o) {
    return Integer.compare(id, o.id);
  }
}
