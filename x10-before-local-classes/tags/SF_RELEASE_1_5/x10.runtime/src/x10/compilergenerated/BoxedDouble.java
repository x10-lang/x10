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


/**
 * Boxed doubles.
 * 
 * @author Christoph von Praun
 */
public class BoxedDouble extends BoxedNumber
{
  protected double value;

  public BoxedDouble(double value)
  {
    this.value = value;
  }

  public double doubleValue()
  {
    return value;
  }
  
  public int hashCode()
  {
    return (int)value;
  }

  public boolean equals(Object o)
  {
    if( o instanceof BoxedDouble) {
      return ((BoxedDouble)o).value == value;
    }
    if( o instanceof BoxedLong) {
      return ((BoxedLong)o).value == value;
    }
    return false;
  }

  public String toString() {
    return "" + value;
  }
}
