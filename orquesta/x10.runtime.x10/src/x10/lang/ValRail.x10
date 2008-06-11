package x10.lang;


@ValRail.T
public value class ValRail(int length) implements Array(:rank==1) {
	public static interface T extends Parameter(:x==1) {}
	
	final NativeValRail(:length==this.length)@T r;
	public ValRail(final int _length, NativeRailMaker@T.Initializer init) {
		property(_length);
		this.r= NativeRailMaker@T.make(_length, init);
	}
	public ValRail(:length==this.length)@T clone() { 
		NativeRailMaker@T.Initializer init = new NativeRailMaker@T.Initializer() {
			public T make(int i) {
				return r.get(i);
			}
		};
		return new ValRail(length, init);
	}
	
	/**
	The following method is defined only if T <: Arithmetic. This has to be stated somewhat cumbersomely
	with the @Extends((T) T.zero(), (Arithmetic) T.zero()) clause. 
	
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
	}*/
	
	public ValRail(:length==this.length)@T map(ValRail(:length==this.length)@T other, 
			Rail.BinaryOp b) {
		NativeRailMaker@T.Initializer init = new NativeRailMaker@T.Initializer() {
			public T make(int i) {
				return b.apply(this.r.get(i, other.r.get(i)));
			}
		};
		return new ValRail(length, init);
	}
	public ValRail(:length==this.length)@T map(Rail.UnaryOp@T b) {
		NativeRailMaker@T.Initializer init = new NativeRailMaker@T.Initializer() {
			public T make(int i) {
				return b.apply(r.get(i));
			}
		};
		return new ValRail(length, init);
	}
	public ValRail@T append(final ValRail@T other) {
		final int l1 = length, l2 = other.length;
		NativeRailMaker@T.Initializer init = new NativeRailMaker@T.Initializer() {
			public T make(int i) {
				return i < l1 ? this.r.get(i) : other.r.get(i-l1);
			}
		};
		return new ValRail(l1+l2, init);
	}
	
}