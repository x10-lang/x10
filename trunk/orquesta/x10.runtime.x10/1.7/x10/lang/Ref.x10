package x10.lang;

/**
   The base class for all reference classes, except x10.lang.Object.
 */
public class Ref {
    val loc: place;
    public def this() {
	loc=here;
    }
    public def loc()=loc;
}
