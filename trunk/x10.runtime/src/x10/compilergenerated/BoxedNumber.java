/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Oct 1, 2004
 */
package x10.compilergenerated;

import java.io.Serializable;

import x10.lang.Object;
import x10.lang.ValueType;

/**
 * Boxed primitves.
 * 
 * @author Christoph von Praun
 *
 * It is currently only Serializable for supporting futures on distributed
 * VMs using LAPI.
 */
public class BoxedNumber extends Object implements ValueType, Serializable
{
  /** Method used to implement <code>o == p</code> when <code>o</code> or
   * <code>p</code> could be a boxed primitive.  Boxed primitives are compared
   * by their primitive value, not by identity.
   */
  public static boolean equals(Object o, Object p) {
    return o == p || (o instanceof BoxedNumber && ((BoxedNumber) o).equals(p));
  }
}
