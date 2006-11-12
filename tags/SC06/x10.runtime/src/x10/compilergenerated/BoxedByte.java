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
