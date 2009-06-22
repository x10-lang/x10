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
 * Boxed shorts.
 * 
 * @author Christoph von Praun
 */
public class BoxedShort extends BoxedInteger
{
  public BoxedShort( short value)
  {
    super(value);
  }

  public short shortValue()
  {
    return (short) value;
  }
}
