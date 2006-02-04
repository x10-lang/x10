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
