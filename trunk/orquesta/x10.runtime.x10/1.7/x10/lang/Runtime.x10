package x10.lang;

/**
    A class with static methods that offer runtime support for X10 programs.
    @author vj 06/12/08
 */
public class Runtime {

    /*package protected*/
    static val runtime = new x10.runtime.Runtime();

    /**
       Make a clock.
     */
    public static def makeClock()=runtime.makeClock();


    /**
       Make a NativeRail[T] of length l, initializing the value at
       index i with init(i).
     */
    public static def [T] makeNativeRail(l:nat, init: nat(l-1)=>T)=runtime.makeNativeRail(l,init);

    /**
       Make a NativeRail[T] of length l, initialized the value at
       index i with init.
     */
    public static def [T] makeNativeRail(l:nat, init: T)=runtime.makeNativeRail(l,init);

    /**
       Create and return a NativeRail[T] of length l, after
       initialized the value at index i with init(i).
     */
    public static def [T] makeNativeValRail(l:nat, init: nat(l-1)=>T)=
	runtime.makeNativeValRail(l, init);

    /**
       Create and return a NativeValRail[T] of length l, after
       initialized the value at index i with init.
     */
    public static def [T] makeNativeValRail(l:nat, init: T)=
	runtime.makeNativeValRail(l, init);


    public static def makeUnitRegion() = runtime.makeUnitRegion();
}
