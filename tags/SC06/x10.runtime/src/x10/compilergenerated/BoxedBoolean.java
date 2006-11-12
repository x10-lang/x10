/*
 * Created on Oct 1, 2004
 */
package x10.compilergenerated;


/**
 * Boxed booleans.
 * 
 * @author Christoph von Praun
 */
public class BoxedBoolean extends BoxedNumber
{
  private boolean value;

  public BoxedBoolean( boolean value)
  {
    this.value = value;
  }

  public boolean booleanValue()
  {
    return value;
  }

  public int hashCode()
  {
    return (value ? 1 : 0);
  }

  public boolean equals( Object o)
  {
    if( o instanceof BoxedBoolean) {
      return ((BoxedBoolean)o).value == value;
    }
    else {
      return false;
    }
  }

  public String toString() {
    return "" + value;
  }
}
