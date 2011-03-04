package x10.lang;

/**

<p>User-visible X10 Rails. Interally uses NativeRail.

<p>Rails are zero-based.

<p>Rails implement Arithmetic if their base type implements
Arithmetic. Thus, all the operators +, -, /, * etc are available on
ValRail[T] if they are available on T.

 */

import x10.lang.TypeDefs.*;

public value class Rail extends AbsRail{Mem==NativeRail[Base]} { 

    def this[T](x0:T): Rail{Base==T,length==1} = {
	super[T, NativeRail[T]](Runtime.runtime.makeNativeRail[T](1, (i:Nat(0))=> x0));
    }
    def this[T](x0:T, x1:T): Rail{Base==T,length==2} = {
	super[T, NativeRail[T]](Runtime.runtime.makeNativeRail[T](2, (i:Nat(1))=> i==0? x0:x1));
    }
    def this[T](x0:T, x1:T, x2: T): Rail{Base==T,length==3} = {
	super[T, NativeRail[T]](Runtime.runtime.makeNativeRail[T](3, (i:Nat(2))=> i==0? x0: i==1? x1:x2));
    }

    def this[T]( l: Nat, init: (Nat(l))=>T) = {
	super[T,NativeRail[T]](makeNativeRail(l, init));
    }
    def this[T]( r: NativeRail[T]) = {
	super[T,NativeRail[T]](r);
    }
    
    public static def allK[Base](length: Nat, v: Base) = new Rail[Base](length, (x:Nat(length-1))=>v);
    public static def allZero[Base](length: Nat){Base <: Arithmetic[Base]} = allK(length, T.zero());

    /**
       Create a copy of this object.
     */
    @Override
    public def clone()  = new Rail[Base](length, (i :Nat(length-1))=> r(i));
    @Override
    protected def clone(l: Nat, init: (Nat(l))=>T) =	new Rail[Base](l, init);
    @Override
    protected def clone[T](_r: NativeRail[T]) = new Rail[T](_r);
    @Override
	public static def makeNativeRail(n :Nat, f:(Nat(n-1))=>Base):NativeAbsRail[Base](n) 
	= Runtime.runtime.makeNativeRail[Base](n, f);



}
