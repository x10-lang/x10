/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.IPath;
import org.eclipse.imp.pdb.facts.ISourceLocation;
import org.eclipse.imp.pdb.facts.IString;
import org.eclipse.imp.pdb.facts.IValue;

import x10dt.ui.launch.core.utils.IFunctor;

/**
 * Utility methods for testing purposes.
 * 
 * @author egeay
 */
@SuppressWarnings("nls")
public final class TestUtils {
  
  /**
   * Checks that a given element in an array is present.
   * 
   * @param <T> The type of the array elements.
   * @param array The array to consider.
   * @param element The element to look for in the array.
   */
  public static <T> void assertHasElement(final T[] array, final T element) {
    for (final T t : array) {
      if ((t != null) && t.equals(element)) {
        return;
      }
    }
    assertTrue(String.format("Element ''%s'' is not present in array.", element.toString()), false);
  }
  
  /**
   * Checks that a given element in an array is present after a conversion occurred.
   * 
   * @param <T1> The original type of the array elements.
   * @param <T2> The expected type of the array elements.
   * @param array The array to consider.
   * @param element The element to look for.
   * @param functor The functor to use for the conversion.
   */
  public static <T1,T2> void assertHasElement(final T1[] array, final T2 element, final IFunctor<T1,T2> functor) {
    for (final T1 t : array) {
      if ((t != null) && functor.apply(t).equals(element)) {
        return;
      }
    }
    assertTrue(String.format("Element ''%s'' is not present in array.", element.toString()), false);
  }
  
  /**
   * Checks some key source location parameters.
   * 
   * @param location The source location to test.
   * @param endPath The end path to test against the source location URI path.
   * @param beginLine The beginning of the line to verify.
   * @param endLine The end of the line to verify.
   */
  public static void assertLocation(final IValue location, final String endPath, final int beginLine, final int endLine) {
    assertTrue(location instanceof ISourceLocation);
    final ISourceLocation sourceLocation = (ISourceLocation) location;
    assertTrue("Condition failed for path: " + sourceLocation.getURI().getPath(), 
               sourceLocation.getURI().getPath().endsWith(endPath));
    assertEquals(beginLine, sourceLocation.getBeginLine());
    assertEquals(endLine, sourceLocation.getEndLine());
  }
  
  public static void assertPath(final String projectName, final String resourceLocation, final IPath path) {
    assertEquals(String.format("/%s/%s", projectName, resourceLocation), path.toString());
  }
  
  /**
   * Returns a fact string value without the surrounding quotes.
   * 
   * @param value The string value to consider.
   * @return The string value without the quotes.
   */
  public static String getString(final IValue value) {
    assertTrue(value instanceof IString);
    final String str = value.toString();
    return str.substring(1, str.length() - 1);
  }
  
  // --- Private code
  
  private TestUtils() {}

}
