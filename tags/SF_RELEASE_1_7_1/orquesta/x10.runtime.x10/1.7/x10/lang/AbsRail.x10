package x10.lang;

/**
   An abstract Rail, intended to be subclassed by ValRail and Rail. It
   gathers in one class definitions that are used by both.  This class
   is package protected. Users are only able to see the public
   subclasses ValRail[T] and Rail[T].

   The type parameter T represents the type of elements of the array,
   and R represents the type of the memory used to store the elements
   of array. R will be instantiated to either NativeRail[T], or
   NativeValRail[T].

   AbsRail implements Arithmetic (provided that T implements
   Arithmetic), thereby permitting arithmetic operators (e.g. a + b)
   to be used in case the first argument is of type AbsRail[T,R](n),
   and the second is also of type AbsRail[T,R](n). Thus ValRail[T](n)
   or Rail[T](n) can be be added to ValRail[T](n) or Rail[T](n), with
   the type of the result being determined by the type of the first
   argument.

   @author vj 06/14/08

 */

// Note that R <: NativeAbsRail[T] and not R <: NativeAbsRail[T]
// We get a form of partial type specification. 
// From NativeAbsRail, we can form several types:
// NativeAbsRail
// NativeAbsRail[T]
// NativeAbsRail[T]{length==self.length}

abstract value AbsRail[Base,Mem](length: nat){Mem <: NativeAbsRail{NativeRailT==Base}} 
    extends Array[Base](0..length-1-> here)
    implements
	(Nat(length-1))=> Base, // permit indexing by nats in the given range.
	Arithmetic[AbsRail[Base,Mem](length)] /* if Base <: Arithmetic[Base] */ {

	    val r: Box[Mem{length==this.length}]; // augmented type variables must be boxed.

    protected def this[B,M](_r: M){M <: NativeAbsRail[B]} : AbsRail[B,M](_r.length) = {
	property[B,M](_r.length);
	r = _r;
    }

    /**
       Create a new Rail that is a copy of this rail.
     */
    public abstract def clone(): AbsRail[Base,Mem](length); 
    
    /**
       Create a new Rail of length l with Base=this.Base and
       Mem=this.Mem, initialized with init.

       <p>We need this method because X10 does not yet support
       thisType.

       <p>Under the convention that Mem is always instantiated with a
       type whose length is unspecified, we can construct the types
       this.Mem{length==k} for any k. (This will be the type of the r
       field in AbsRail[Base, Mem](l).) The type checker will
       complain if it has to process a constraint of the form
       Mem{length==k, length==l}, where k and l are independent
       variables not already constrained to be equal to each other.

     */
    protected abstract def clone(l: Nat, init: (Nat(l))=>Base): AbsRail{self.Base==this.Baes,self.Mem==this.Mem,self.length==l};
    protected def clone[B,M](_r: M): AbsRail{self.Base==B,self.Mem==M,self.length==_r.length};

    public def apply(p: Point(0..length-1)):Base = r(p);
    public def apply(p: Nat(0..length-1)):Base = r(p);
    public def filter(f: (Base)=>Boolean) = filter((Nat(length), t: Base)=>f(t));

    public def map(b: (x:Base)=>Base) = 
	clone(length, (i: Nat(length-1))=>b(r(i)));
    public def map(b: (i: Nat(length-1), x:Base)=>Base) = 
	clone(length, (i: Nat(length-1))=>b(i, r(i)));

    public def andReduceRail(o:AbsRail[Base](length), op: (Base,Base)=>boolean) = {
	for (val p(i) in r.region) if (! op(r(i),o(i))) return false;
	true
    }

    // you want the return type to be this.class{length+o.length}
    public def append(o: AbsRail{self.Base==this.Base}): AbsRail{self.Base==this.Base,self.Mem==this.Mem,self.length==this.length+o.length} = {
	val l = length+o.length;
	clone(l, (i: nat(l-1)) => i<length-1? r(i) : other.r(i-length));
    }

    public abstract def makeNativeRail(n :Nat, f:(Nat(n-1))=>Base):NativeAbsRail{NativeRailT==Base,length==n};
    /**
       Return the Rail obtained from this by removing those elements that do not satisfy the
       given condition.
     */
    public def filter(f: (Nat(length), Base)=>Boolean) = {
	shared var count: Nat=0;
	val temp = new ValRail[Base](length, (i:Nat(length)) => {
		if (f(r(i))) { count++; return r(i); } else return null; });
	clone(Runtime.runtime.makeNativeRail(count, (i:Nat(count))=> temp(i)))
    }

    public def mapRail(o: AbsRail[Base](length), op:(Base, Base)=>Base) = 
        map((i: Nat(length-1), x:Base) => op(x,o(i)));

    public def reduce[S](z: S, op: (Base, Base)=>S) = {
	var v: S = z;
	for (val w in r) v = op(v,w);
	v
    }
    public def andReduce(op: (Base)=>Boolean) = {
	for (val w in r) if (! op(w)) return false;
	true
    }
    public def andReduce(op: (Nat(0,length-1))=>Boolean) = {
	for (val p(i) in r.region) if (! op(i)) return false;
	true
    }
    public def isK(k:Base): Boolean = andReduce((x:Base) => x==k);
    public def add(o: AbsRail[Base,Mem](length)){Base <: Arithmetic[Base]}  = mapRail(o,(x:Base,y:Base)=>x.add(y));
    public def sub(o: AbsRail[Base,Mem](length)){Base <: Arithmetic[Base]}  = mapRail(o,Base.sub.(Base));
    public def mul(o: AbsRail[Base,Mem](length)){Base <: Arithmetic[Base]}  = mapRail(o,Base.mul.(Base));
    public def div(o: AbsRail[Base,Mem](length)){Base <: Arithmetic[Base]}  = mapRail(o,Base.div.(Base));
    public def cosub(o: AbsRail[Base,Mem](length)){Base <: Arithmetic[Base]}= mapRail(o,Base.cosub.(Base));
    public def codiv(o: AbsRail[Base,Mem](length)){Base <: Arithmetic[Base]}= mapRail(o,Base.codiv.(Base));
    public def neginv(){Base <: Arithmetic[Base]}                    = map(Base.neginv.());
    public def mulinv(){Base <: Arithmetic[Base]}                    = map(Base.mulinv.());
    public def eq(o: AbsRail[Base,Mem](length)){Base <: Arithmetic[Base]}   = andReduceRail(o, Base.eq.(Base));
    public def lt(o: AbsRail[Base,Mem](length)){Base <: Arithmetic[Base]}   = andReduceRail(o, Base.lt.(Base));
    public def gt(o: AbsRail[Base,Mem](length)){Base <: Arithmetic[Base]}   = andReduceRail(o, Base.gt.(Base));
    public def le(o: AbsRail[Base,Mem](length)){Base <: Arithmetic[Base]}   = andReduceRail(o, Base.le.(Base));
    public def ge(o: AbsRail[Base,Mem](length)){Base <: Arithmetic[Base]}   = andReduceRail(o, Base.ge.(Base));
    public def ne(o: AbsRail[Base,Mem](length)){Base <: Arithmetic[Base]}   = andReduceRail(o, Base.ne.(Base));

    // custom versions of the above for the case in which the other rail is a constant.
    public def add(c: Base){Base <: Arithmetic[Base]}   = map((x:int) => x.add(c));
    public def mul(c: Base){Base <: Arithmetic[Base]}   = map((x:int) => x.mul(c));
    public def div(c: Base){Base <: Arithmetic[Base]}   = map((x:int) => x.div(c));
    public def codiv(c: Base){Base <: Arithmetic[Base]} = map((x:int) => x.codiv(c));
    public def sub(c: Base){Base <: Arithmetic[Base]}   = map((x:int) => x.sub(c));
    public def cosub(c: Base){Base <: Arithmetic[Base]} = map((x:int) => x.cosub(c));
    public def eq(c: Base){Base <: Arithmetic[Base]}    = andReduce((x:Base)=> x.eq(c));
    public def lt(c: Base){Base <: Arithmetic[Base]}    = andReduce((x:Base)=> x.lt(c));
    public def gt(c: Base){Base <: Arithmetic[Base]}    = andReduce((x:Base)=> x.gt(c));
    public def ge(c: Base){Base <: Arithmetic[Base]}    = andReduce((x:Base)=> x.ge(c));
    public def le(c: Base){Base <: Arithmetic[Base]}    = andReduce((x:Base)=> x.le(c));
    public def ne(c: Base){Base <: Arithmetic[Base]}    = andReduce((x:Base)=> x.ne(c));
}
