/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.core.utils;

import java.util.Random;

/**
 * Utility methods for development.
 * 
 * @author egeay
 */
public final class CodingUtils {
  
  /**
   * Test if two object are equals considering also possible null values.
   * 
   * @param lhs The left hand side object.
   * @param rhs The right hand side object.
   * @return True if the two elements are equals, false otherwise.
   */
  public static boolean equals(final Object lhs, final Object rhs) {
    return (lhs == null) ? (rhs == null) : lhs.equals(rhs);
  }
  
  /**
   * Generates a unique hash code from the list of fields provided. If some elements are <b>null</b> a random integer is
   * used.
   * 
   * @param seed The seed to use for the random generator in case of null values.
   * @param elements The list of fields to consider.
   * @return A unique hash code.
   */
  public static int generateHashCode(final long seed, final Object ... elements) {
    int hashCode = 0;
    final Random randomGen = new Random(seed);
    for (final Object element : elements) {
      if (element == null) {
        hashCode += randomGen.nextInt();
      } else {
        hashCode += element.hashCode();
      }
    }
    return hashCode;
  }
  
  // --- Private code
  
  private CodingUtils() {}

}
