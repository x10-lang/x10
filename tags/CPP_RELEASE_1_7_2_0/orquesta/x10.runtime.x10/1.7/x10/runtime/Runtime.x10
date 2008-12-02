package x10.runtime;

/**
   A reference implementation of the runtime for X10.

   @author vj 06/12/08
 */
public class Runtime {
    /**
       Make a clock.
     */
    public incomplete def makeClock():clock;

    /**
       Make a NativeRail[T] of length l, initializing the value at
       index i with init(i).
     */
    public incomplete def makeNativeRail[T](l:nat, init: (nat(l-1))=>T):NativeRail[T](l);

    /**
       Make a NativeRail[T] of length l, initialized the value at
       index i with init.
     */
    public incomplete def makeNativeRail[T](l:nat, init: T):NativeRail[T](l);

    /**
       Create and return a NativeRail[T] of length l, after
       initialized the value at index i with init(i).
     */
    public incomplete def makeNativeValRail[T](l:nat, init: (nat(l-1))=>T):NativeValRail[T](l);


    /**
       Create and return a NativeValRail[T] of length l, after
       initialized the value at index i with init.
     */
    public incomplete def makeNativeValRail[T](l:nat, init: T):NativeValRail[T](l);


    public def makeUnitRegion():region{rank==0,rect,zeroBased,!rail, !colMajor}=UnitRegion.unit;
}
