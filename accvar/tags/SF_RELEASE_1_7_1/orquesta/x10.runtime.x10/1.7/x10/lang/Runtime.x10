package x10.lang;

/**
    A class with static methods that offer runtime support for X10 programs.
    @author vj 06/12/08
 */
public abstract class Runtime {

    /*package protected*/
    const runtime = new x10.runtime.Runtime();

    /**
       Make an array.
     */
    public def makeArray[T](d: Dist, maker: ((Point) => T)): Array[T]{dist==d, region==d.region, rank==d.rank()};

    public abstract def maxPlaces(): Nat;
    /**
       Make a clock.
     */
    public abstract def makeClock(): Clock;

    /**
       Make a NativeRail[T] of length l, initializing the value at
       index i with init(i).
     */
    public abstract def makeNativeRail[T](l: Nat, init: (Nat(l-1))=>T): NativeRail[T](l);

    /**
       Make a NativeRail[T] of length l, initialized the value at
       index i with init.
     */
    public def makeNativeRail[T](l: Nat, init: T) = makeNativeRail(l, (Nat(l-1))=> init);

    /**
       Create and return a NativeRail[T] of length l, after
       initialized the value at index i with init(i).
     */
    public abstract def makeNativeValRail[T](l: Nat, init: (Nat(l-1))=>T): NativeValRail[T](l);

    /**
       Create and return a NativeValRail[T] of length l, after
       initialized the value at index i with init.
     */
    public def makeNativeValRail[T](l: Nat, init: T):NativeValRail[T](l);

    public static incomplete def makeUnitRegion():Region{rank==0,rect,!rail, !colMajor};

    public static incomplete def makeEmptyRegion(r :nat):Region{rank==r,rect,rail==(r==1), !colMajor};

    public static incomplete def makeRectRegion(r :nat):Region{rank==r,rect,rail==(r==1), !colMajor};

    public static incomplete def makeRangeRegion(min :int, max: int):Region{rank==1,rect,rail==true, !colMajor};

    //********************** Place *****************************
    /**
       Return the place at which the current activity is executing.
     */
    public abstract def here(): Place; 

    /**
       Return the ValRail of all places, with the i'th entry
       containing the place whose id is i.
     */
    public abstract def places():ValRail[Place];


}
