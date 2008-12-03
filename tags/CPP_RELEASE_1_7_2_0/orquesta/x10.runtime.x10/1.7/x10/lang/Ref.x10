package x10.lang;

/**
   The base class for all reference classes, except x10.lang.Object.
 */
public class Ref(location: Place) {
    public def this() = property(here);
}
