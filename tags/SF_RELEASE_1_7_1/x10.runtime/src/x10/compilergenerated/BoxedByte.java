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
 * Boxed bytes.
 * 
 * @author Christoph von Praun
 */
public class BoxedByte extends BoxedInteger
{
  public BoxedByte( byte value)
  {
    super(value);
  }

  public byte byteValue()
  {
    return (byte) value;
  }
}
