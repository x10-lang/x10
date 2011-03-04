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
 * Boxed floats.
 * 
 * @author Christoph von Praun
 */
public class BoxedFloat extends BoxedDouble
{
  public BoxedFloat(float value)
  {
    super(value);
  }

  public float floatValue()
  {
    return (float) value;
  }
}
