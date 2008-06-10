package x10.lang;


@ValRail.T
public class ValRail(int length) implements Array(:rank==1) {
	public static interface T extends Parameter(:x==1) {}
	public ValRail(T x0) {
		this(new Rail(x0));
	}
	public ValRail(T x0, T x1) { 
		this(new Rail(x0, x1));
	}
	public ValRail(T x0, T x1, T x2) { 
		this(new Rail(x0, x1,x2));
	}
	public ValRail(T x0, T x1, T x2, T x3) { 
		this(new Rail(x0, x1,x2,x3));
	}
	public ValRail(T x0, T x1, T x2, T x3, T x4) { 
		this(new Rail(x0, x1,x2,x3,x4));
	}
	public ValRail(T x0, T x1, T x2, T x3, T x4, T x5) {
		this(new Rail(x0, x1,x2,x3,x4,x5));
	}
	public ValRail(T x0, T x1, T x2, T x3, T x4, T x5, T x6) { 
		this(new Rail(x0, x1,x2,x3,x4,x5,x6));
	}
	public ValRail(T x0, T x1, T x2, T x3, T x4, T x5, T x6, T x7) {
		this(new Rail(x0, x1,x2,x3,x4,x5,x6,x7));
	}

	final Rail(:length==this.length)@T r;
	public ValRail(Rail@T r) {
		property(r.length);
		this.r=r;
	}
	public Rail(:length==this.length)@T clone() { 
		return r.clone(); 
	}
	
	/**
	The following method is defined only if T <: Arithmetic. This has to be stated somewhat cumbersomely
	with the @Extends((T) T.zero(), (Arithmetic) T.zero()) clause. 
	*/
	@Extends((T) T.zero(), (Arithmetic) T.zero())
	public ValRail(:length==this.length)@T add(ValRail(:length==this.length)@T o) {
		return new ValRail(r.add(o.r));
	}
	
	@Extends((T) T.zero(), (Arithmetic) T.zero())
	public ValRail(:length==this.length)@T mul(ValRail(:length==this.length)@T o) {
		return new ValRail(r.mul(o.r));
	}
	
	@Extends((T) T.zero(), (Arithmetic) T.zero())
	public ValRail(:length==this.length)@T mulinv() {
		return new ValRail(r.mulinv());
	}
	
	@Extends((T) T.zero(), (Arithmetic) T.zero())
	public ValRail(:length==this.length)@T sub(ValRail(:length==this.length) o) {
		return new ValRail(r.sub(o.r));
	}
	
	@Extends((T) T.zero(), (Arithmetic) T.zero())
	public ValRail(:length==this.length)@T neginv() {
		return new ValRail(r.neginv());
	}
	
	@Extends((T) T.zero(), (Arithmetic) T.zero())
	public ValRail(:length==this.length)@T cosub(ValRail(:length==this.length)@T o) {
		return new ValRail(r.cosub(o.r));
	}
	
	@Extends((T) T.zero(), (Arithmetic) T.zero())
	public ValRail(:length==this.length)@T codiv(ValRail(:length==this.length)@T o) {
		return new ValRail(r.codiv(o.r));
	}
	
	@Extends((T) T.zero(), (Arithmetic) T.zero())
	public ValRail(:length==this.length)@T div(ValRail(:length==this.length)@T o) {
		return new ValRail(r.div(o.r));
	}
	
	public ValRail(:length==this.length)@T map(ValRail(:length==this.length)@T other, 
			Rail.BinaryOp b) {
		return new ValRail(r.map(other.r, b));
	}
	public ValRail(:length==this.length)@T map(Rail.UnaryOp@T b) {
		return new ValRail(r.map(b));
	}
	public ValRail@T append(ValRail@T other) {
		return new ValRail(r.append(other.r));
	}
	
}