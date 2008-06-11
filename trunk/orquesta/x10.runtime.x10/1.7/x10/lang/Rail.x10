package x10.lang;


/**
 A rail is an array over the distribution 0..n-1 -> here.
 An Rail is a (mutable) rail of Ts.
 
 This is intended to be implemented natively. 
 
 @author vj 06/09/08
 
*/

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

This is basically a copy of ValRail[T], with ValRail replaced by Rail, and with 
a setter method added.

 */

import static TypeDefs.*;

public value class Rail[T](length: nat) 
    implements Array[T]?rail?, Arithmetic[Rail[T](length)] if T <: Arithmetic[T]  {
	
    val r: NativeRail[T](length);
    def this( _length: nat, init: nat(_length)=>T) {
	property(_length);
	this.r= NativeRailMaker[T].make(_length, init);
    }
    public def apply(p:point(0..length-1)):T = r(p);
    public def set(p:point(0..length-1), x:T):void = { r.set(p,x);}
    public def Rail[T](length) clone() = new Rail[T](length, (i :nat(length-1))=> r(i));

    public def map(b: (x:T)=>T) = new Rail[T](length, (i: nat(length-1))=>b(r(i)));

    public def map(b: (i: nat(length-1), x:T)=>T) = 
	new Rail[T](length, (i: nat(length-1))=>b(i, r(i)));

    public def mapRail(o: Rail[T](length), op:(T,T)=>T) = 
        map((i: nat(length-1), x:T) => op(x,o(i)));

    public def reduce(z: S, op: (T,T)=>S):S = {
	var v = z;
	for (val w in r) v = op(v,w);
	v
    }
    public def andReduce(op: (T)=>boolean):boolean = {
	for (val w in r) if (! op(w)) return false;
	true
    }
    public def andReduceRail(o:Rail[T](length), op: (T,T)=>boolean):boolean = {
	for (val p[i] in r.region) if (! op(r(i),o(i))) return false;
	true
    }

    public def append(o: Rail[T]): Rail[T](length+o.length) = {
	val l = length+o.length;
	new Rail[T](l, (i: nat(l-1)) => i<length-1? r(i) : other.r(i-length))
    }
		       
    public def add(o: Rail[T](length)){T <: Arithmetic[T]}: Rail[T](length) =
        mapRail(o,T.add.(T));
    public def mul(o: Rail[T](length)){T <: Arithmetic[T]}: Rail[T](length) =
        mapRail(o,T.mul.(T));
    public def div(o: Rail[T](length)){T <: Arithmetic[T]}: Rail[T](length) =
        mapRail(o,T.div.(T));
    public def sub(o: Rail[T](length)){T <: Arithmetic[T]}: Rail[T](length) =
        mapRail(o,T.sub.(T));
    public def cosub(o: Rail[T](length)){T <: Arithmetic[T]}: Rail[T](length) =
        mapRail(o,T.cosub.(T));
    public def codiv(o: Rail[T](length)){T <: Arithmetic[T]}: Rail[T](length) =
        mapRail(o,T.codiv.(T));
    public def neginv(){T <: Arithmetic[T]}{T <: Arithmetic[T]}: Rail[T](length) = 
        map(T.neginv.());
    public def mulinv(){T <: Arithmetic[T]}{T <: Arithmetic[T]}: Rail[T](length) =  
        map(T.mulinv.());
    public def eq(o: Rail[T](length)){T <: Arithmetic[T]}:boolean = 
       andReduceRail(o, T.eq.(T));
    public def lt(o: Rail[T](length)){T <: Arithmetic[T]}:boolean = 
       andReduceRail(o, T.lt.(T));
    public def gt(o: Rail[T](length)){T <: Arithmetic[T]}:boolean = 
       andReduceRail(o, T.gt.(T));
    public def le(o: Rail[T](length)){T <: Arithmetic[T]}:boolean = 
       andReduceRail(o, T.le.(T));
    public def ge(o: Rail[T](length)){T <: Arithmetic[T]}:boolean = 
       andReduceRail(o, T.ge.(T));
    public def ne(o: Rail[T](length)){T <: Arithmetic[T]}:boolean = 
       andReduceRail(o, T.ne.(T));

    // custom versions of the above for the case in which the other rail is a constant.
    public def add(c: T){T <: Arithmetic[T]}: Rail[T](length) = map((x:int) => x.add(c));
    public def mul(c: T){T <: Arithmetic[T]}: Rail[T](length) = map((x:int) => x.mul(c));
    public def div(c: T){T <: Arithmetic[T]}: Rail[T](length) = map((x:int) => x.div(c));
    public def codiv(c: T){T <: Arithmetic[T]}: Rail[T](length) = map((x:int) => x.codiv(c));
    public def sub(c: T){T <: Arithmetic[T]}: Rail[T](length) = map((x:int) => x.sub(c));
    public def cosub(c: T){T <: Arithmetic[T]}: Rail[T](length) = map((x:int) => x.cosub(c));
    public def eq(c: T){T <: Arithmetic[T]}:boolean = andReduce(x:T=> x.eq(c));
    public def lt(c: T){T <: Arithmetic[T]}:boolean = andReduce(x:T=> x.lt(c));
    public def gt(c: T){T <: Arithmetic[T]}:boolean = andReduce(x:T=> x.gt(c));
    public def ge(c: T){T <: Arithmetic[T]}:boolean = andReduce(x:T=> x.ge(c));
    public def le(c: T){T <: Arithmetic[T]}:boolean = andReduce(x:T=> x.le(c));
    public def ne(c: T){T <: Arithmetic[T]}:boolean = andReduce(x:T=> x.ne(c));

}

