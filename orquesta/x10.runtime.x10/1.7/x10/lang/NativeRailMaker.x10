package x10.lang;

/**
 A factory class that knows how to make native rails of the given type.

 @author vj 06/11/08

*/
import static TypeDefs.*;
public class NativeRailMaker[T] {
	
    /**
    TODO: Figure out how to initialize the array -- prolly need to pass in
    a T. But then need special case initializer for nullables which will
    initialize to null (any performance advantage to this), and a special
    case initializer for int, double etc which will initialize to 0.

    Note: prolly want to allow subtyping of functions so that an D=>R
    can always be passed in for a D' => R' provided that D' is a
    subtype of D and R is a subtype of R'.
    */

    public static native make(l: nat, init: nat(l-1)=>T): NativeRail[T](l);
    public static native make(l: nat, init: nat(l-1)=>T): NativeValRail[T](l); 
	
	
}
