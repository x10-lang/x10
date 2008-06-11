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
   value class ValRail[T](length: nat) implements 
        Arithmetic[ValRail[T](length), ValRail[T](length)],
        Arithmetic[ValRail[T](length), T] ...

 */

import static TypeDefs.*;

public value class ValRail[T](length: nat) 
    implements Array[T](0..length-1->here),
	       Arithmetic[ValRail[T](length)] if T <: Arithmetic[T]  {
    val r: NativeValRail[T](length);
    def this(x0:T) {
	property(1);
	this.r = NativeRalMaker[T].make(1, (i:nat(0)=> x0));
    }
    def this(x0:T, x1:T) {
	property(2);
	this.r = NativeRalMaker[T].make(2, (i:nat(0)=> i==0? x0:x1));
    }
    def this(x0:T, x1:T, x2: T) {
	property(3);
	this.r = NativeRalMaker[T].make(3, (i:nat(0)=> i==0? x0: i==1? x1:x2));
    }

    def this( _length: nat, init: nat(_length)=>T) {
	property(_length);
	this.r= NativeRailMaker[T].make(_length, init);
    }
    public static def allK(length : nat, v:T) = new ValRail[T](length, x:nat(length-1)=>v);
    public static def allZero(length : nat){T <: Arithmetic[T]} = allK(length, T.zero());

    public def apply(p:point(0..length-1)):T = r(p);
    public def ValRail[T](length) clone() = new ValRail[T](length, (i :nat(length-1))=> r(i));
    public def map(b: (x:T)=>T) = new ValRail[T](length, (i: nat(length-1))=>b(r(i)));

    public def map(b: (i: nat(length-1), x:T)=>T) = 
	new ValRail[T](length, (i: nat(length-1))=>b(i, r(i)));

    public def mapRail(o: ValRail[T](length), op:(T,T)=>T) = 
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
    public def andReduce(op: (nat(0,length-1))=>boolean):boolean = {
	for (val p[i] in r.region) if (! op(i)) return false;
	true
    }
    public def andReduceRail(o:ValRail[T](length), op: (T,T)=>boolean):boolean = {
	for (val p[i] in r.region) if (! op(r(i),o(i))) return false;
	true
    }

    public def append(o: ValRail[T]): ValRail[T](length+o.length) = {
	val l = length+o.length;
	new ValRail[T](l, (i: nat(l-1)) => i<length-1? r(i) : other.r(i-length))
    }

    public def isK(k:T):boolean = andReduce(x:T => x==k);
    public def add(o: ValRail[T](length)){T <: Arithmetic[T]}: ValRail[T](length) =
        mapRail(o,T.add.(T));
    public def mul(o: ValRail[T](length)){T <: Arithmetic[T]}: ValRail[T](length) =
        mapRail(o,T.mul.(T));
    public def div(o: ValRail[T](length)){T <: Arithmetic[T]}: ValRail[T](length) =
        mapRail(o,T.div.(T));
    public def sub(o: ValRail[T](length)){T <: Arithmetic[T]}: ValRail[T](length) =
        mapRail(o,T.sub.(T));
    public def cosub(o: ValRail[T](length)){T <: Arithmetic[T]}: ValRail[T](length) =
        mapRail(o,T.cosub.(T));
    public def codiv(o: ValRail[T](length)){T <: Arithmetic[T]}: ValRail[T](length) =
        mapRail(o,T.codiv.(T));
    public def neginv(){T <: Arithmetic[T]}{T <: Arithmetic[T]}: ValRail[T](length) = 
        map(T.neginv.());
    public def mulinv(){T <: Arithmetic[T]}{T <: Arithmetic[T]}: ValRail[T](length) =  
        map(T.mulinv.());
    public def eq(o: ValRail[T](length)){T <: Arithmetic[T]}:boolean = 
       andReduceRail(o, T.eq.(T));
    public def lt(o: ValRail[T](length)){T <: Arithmetic[T]}:boolean = 
       andReduceRail(o, T.lt.(T));
    public def gt(o: ValRail[T](length)){T <: Arithmetic[T]}:boolean = 
       andReduceRail(o, T.gt.(T));
    public def le(o: ValRail[T](length)){T <: Arithmetic[T]}:boolean = 
       andReduceRail(o, T.le.(T));
    public def ge(o: ValRail[T](length)){T <: Arithmetic[T]}:boolean = 
       andReduceRail(o, T.ge.(T));
    public def ne(o: ValRail[T](length)){T <: Arithmetic[T]}:boolean = 
       andReduceRail(o, T.ne.(T));

    // custom versions of the above for the case in which the other rail is a constant.
    public def add(c: T){T <: Arithmetic[T]}: ValRail[T](length) = map((x:int) => x.add(c));
    public def mul(c: T){T <: Arithmetic[T]}: ValRail[T](length) = map((x:int) => x.mul(c));
    public def div(c: T){T <: Arithmetic[T]}: ValRail[T](length) = map((x:int) => x.div(c));
    public def codiv(c: T){T <: Arithmetic[T]}: ValRail[T](length) = map((x:int) => x.codiv(c));
    public def sub(c: T){T <: Arithmetic[T]}: ValRail[T](length) = map((x:int) => x.sub(c));
    public def cosub(c: T){T <: Arithmetic[T]}: ValRail[T](length) = map((x:int) => x.cosub(c));
    public def eq(c: T){T <: Arithmetic[T]}:boolean = andReduce(x:T=> x.eq(c));
    public def lt(c: T){T <: Arithmetic[T]}:boolean = andReduce(x:T=> x.lt(c));
    public def gt(c: T){T <: Arithmetic[T]}:boolean = andReduce(x:T=> x.gt(c));
    public def ge(c: T){T <: Arithmetic[T]}:boolean = andReduce(x:T=> x.ge(c));
    public def le(c: T){T <: Arithmetic[T]}:boolean = andReduce(x:T=> x.le(c));
    public def ne(c: T){T <: Arithmetic[T]}:boolean = andReduce(x:T=> x.ne(c));
}
