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
 * Boxed ints.
 * 
 * @author Christoph von Praun
 */
public class BoxedInteger extends BoxedLong
{
  public BoxedInteger(int value)
  {
    super(value);
  }

  public int intValue()
  {
    return (int) value;
  } 
}
