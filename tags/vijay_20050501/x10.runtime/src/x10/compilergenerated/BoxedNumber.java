/*
 * Created on Oct 1, 2004
 */
package x10.compilergenerated;

import x10.lang.Object;

/**
 * Boxed primitves.
 * 
 * @author Christoph von Praun
 */
public class BoxedNumber extends Object
{
  /** Method used to implement <code>o == p</code> when <code>o</code> or
   * <code>p</code> could be a boxed primitive.  Boxed primitives are compared
   * by their primitive value, not by identity.
   */
  public static boolean equals(Object o, Object p) {
    return o == p || (o instanceof BoxedNumber && ((BoxedNumber) o).equals(p));
  }
}
