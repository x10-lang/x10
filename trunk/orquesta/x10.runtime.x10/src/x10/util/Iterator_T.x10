package x10.util;

// XXX temp hack - don't belong here but this lets us generate
// Iterator_Scanner and Iterator_Constraint... Iterator_T is
// eventually going away anyway...
import x10.lang.Region.Scanner;
import x10.array.Constraint;

public interface Iterator_T {
    boolean hasNext();
    T next();
}
