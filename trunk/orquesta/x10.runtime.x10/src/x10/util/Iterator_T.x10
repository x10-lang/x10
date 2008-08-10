package x10.util;

// XXX temp hack - don't belong here but this lets us generate
// Iterator_Scanner and Iterator_Halfspace... Iterator_T is
// eventually going away anyway...
import x10.lang.Region.Scanner;
import x10.array.Halfspace;
import x10.array.PolyRegion;

public interface Iterator_T {
    boolean hasNext();
    T next();
}
