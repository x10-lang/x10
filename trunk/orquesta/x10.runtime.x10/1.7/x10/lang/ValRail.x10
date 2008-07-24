package x10.lang;

/**

<p>User-visible X10 val Rails. Interally uses NativeValRail.

<p>Rails are zero-based.

<p>ValRails implement Arithmetic if their base type implements
Arithmetic. Thus, all the operators +, -, /, * etc are available on
ValRail[T] if they are available on T.

// vj: note the conditional implementation of an interface.
// also note that it implements Arithmetic[ValRail[T](length)] and not 
// Arithmetic[ValRail[T]].

// vj: Look into making Arithmetic take two arguments, and then being able to say here:
   value class ValRail[T](length: Nat) implements 
        Arithmetic[ValRail[T](length), ValRail[T](length)],
        Arithmetic[ValRail[T](length), T] ...

//  vj: CHECK: Below, I am using the notation T.add(T) for
//  (x:T,y:T)=>x.add(y)). The notation T.add.(T) should be used (for a
//  class T) to refer to the static method on T with a single arg T.
//  Is this too subtle?

// vj: There is an interesting issue about representing filtered
// ValRails. Suppose I want to apply a filter to obtain a subarray.
// The only reasonable way to do this is to allocate an array A of
// size length and then add to it only those elements which pass the
// filter. Now we have to be able to either (a) "drop" the space for
// the remaining elements of A and return the trimmed A, or (b) if
// there are k live elements, allocate another ValArray of k elements
// and copy the live elements into it, and drop A, or (c) just return
// A but now have the notion that the space allocated for a ValRail is
// actually more than the size of the ValRail.

Implements AbsRail[T, NativeAbsRail[T](length)] rather than AbsRail[T,
NativeValRail[T](length)], permitting thereby the underlying memory to
be a NativeRail[T](length).

 */

import x10.lang.TypeDefs.*;

public value class ValRail extends AbsRail{Mem==NativeValRail{NativeRailT==Base}} { 

    def this[T](x0:T): ValRail{Base==T,length==1} = {
	super[T, NativeValRail[T]](Runtime.runtime.makeNativeValRail[T](1, (i:Nat(0))=> x0));
    }
    def this[T](x0:T, x1:T): ValRail{Base==T,length==2} = {
	super[T, NativeValRail[T]](Runtime.runtime.makeNativeValRail[T](2, (i:Nat(1))=> (i==0? x0:x1)));
    }
    def this[T](x0:T, x1:T, x2: T): ValRail{Base==T,length==3} = {
	super[T, NativeValRail[T]](Runtime.runtime.makeNativeValRail[T](3, (i:Nat(2))=> (i==0? x0: i==1? x1:x2)));
    }

    def this[T]( l: Nat, init: (Nat(l))=>T) = {
	super[T,NativeValRail[T]](makeNativeValRail(l, init));
    }
    def this[T]( r: NativeValRail[T]) = {
	super[T,NativeValRail[T]](r);
    }
    public static def allK[Base](length: Nat, v: Base) = new ValRail[Base](length, (x:Nat(length-1))=>v);
    public static def allZero[Base](length: Nat){Base <: Arithmetic[Base]} = allK(length, T.zero());

    /**
       Create a copy of this object.
     */
    @Override
    public def clone()= new ValRail[Base](length, (i :Nat(length-1))=> r(i));
    @Override
    protected def clone(l: Nat, init: (Nat(l))=>T) =	new ValRail[Base](l, init);
    @Override
    protected def clone[T](_r: NativeValRail[T]) = new ValRail[T](_r);
    @Override
	public def makeNativeRail(n :Nat, f:(Nat(n-1))=>Base):NativeAbsRail[Base](n) 
	= Runtime.runtime.makeNativeValRail[Base](n, f);


}
