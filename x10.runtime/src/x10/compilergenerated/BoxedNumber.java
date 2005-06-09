/*
 * Created on Oct 1, 2004
 */
package x10.compilergenerated;

import x10.lang.Object;
import x10.lang.ValueType;

/**
 * Boxed primitves.
 * 
 * @author Christoph von Praun
 */
public class BoxedNumber extends Object implements ValueType
{
  /** Method used to implement <code>o == p</code> when <code>o</code> or
   * <code>p</code> could be a boxed primitive.  Boxed primitives are compared
   * by their primitive value, not by identity.
   */
  public static boolean equals(Object o, Object p) {
    return o == p || (o instanceof BoxedNumber && ((BoxedNumber) o).equals(p));
  }
}
