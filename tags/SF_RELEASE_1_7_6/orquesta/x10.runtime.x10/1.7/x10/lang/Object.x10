package x10.lang;

/** The top of the type hierarchy.

    It is important that this class has no mutable fields. Hence it
    can be subclassed by value classes, such as value.

  @author vj 06/12/08
 */
public class Object {

    // should be overridden by subclasses.
    public def toString() = "x10.lang.Object";
}
