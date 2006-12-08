/*
 * Created on Oct 1, 2004
 */
package x10.compilergenerated;



/**
 * Boxed chars.
 * 
 * @author Christoph von Praun
 */
public class BoxedCharacter extends BoxedInteger
{
  public BoxedCharacter( char value)
  {
    super(value);
  }

  public char charValue()
  {
    return (char) value;
  }

  public String toString() {
    return "" + (char) value;
  }
}
