package x10.lang;


/**
 A rail is an array over the distribution 0..n-1 -> here.
 An Rail is a (mutable) rail of Ts.
 
 This is intended to be implemented natively. 
 
 @author vj 06/09/08
 
*/

@Rail.T
public class Rail(int length) { // implements Array {
	public static interface T extends Parameter(:x==1){}
	public Rail(T x0) {property(1); }
	public Rail(T x0, T x1) { property(2); }
	public Rail(T x0, T x1, T x2) { property(3); }
	public Rail(T x0, T x1, T x2, T x3) { property(4);}
	public Rail(T x0, T x1, T x2, T x3, T x4) { property(5);}
	public Rail(T x0, T x1, T x2, T x3, T x4, T x5) {property(6); }
	public Rail(T x0, T x1, T x2, T x3, T x4, T x5, T x6) { property(7); }
	public Rail(T x0, T x1, T x2, T x3, T x4, T x5, T x6, T x7) { property(8);}

	public Rail(:length==this.length) clone() { 
//		 need to clone
		return (Rail(:length==this.length)) this; // placeholder
	}
	public static abstract class BinaryOp {
		public abstract T apply(T x, T y);
		
		@Extends((T) T.zero(), (Arithmetic) T.zero()) 
		const BinaryOp add = new BinaryOp() {
			public T apply(T x, T y) {
				return x.add(y);
			}
		};
		
		@Extends((T) T.zero(), (Arithmetic) T.zero()) 
		const BinaryOp mul= new BinaryOp() {
			public T apply(T x, T y) {
				return x.mul(y);
			}
		};
		
		@Extends((T) T.zero(), (Arithmetic) T.zero()) 
		const BinaryOp sub = new BinaryOp() {
			public T apply(T x, T y) {
				return x.sub(y);
			}
		};
		
		@Extends((T) T.zero(), (Arithmetic) T.zero()) 
		const BinaryOp div= new BinaryOp() {
			public T apply(T x, T y) {
				return x.div(y);
			}
		};
	}
	public static abstract class UnaryOp {
		public abstract T apply(T x);
	
		@Extends((T) T.zero(), (Arithmetic) T.zero()) 
		public	static UnaryOp add(final T c) {
			return new UnaryOp() {
				public T apply(T x) {
					return x.add(c);
				}
			};
		}
		@Extends((T) T.zero(), (Arithmetic) T.zero()) 
		public static UnaryOp sub(final T c) {
			return new UnaryOp() {
				public T apply(T x) {
					return x-c;
				}
			};
		}
		@Extends((T) 0, (Arithmetic) 0) public static UnaryOp mul(final T c) {
			return new UnaryOp() {
				public T apply(T x) {
					return x*c;
				}
			};
		}
		@Extends((T) T.zero(), (Arithmetic) T.zero())  
		public static UnaryOp div(final T c) {
			return new UnaryOp() {
				public T apply(T x) {
					return x/c;
				}
			};
		}
   
		@Extends((T) T.zero(), (Arithmetic) T.zero()) 
		public static UnaryOp cosub(final T c) {
			return new UnaryOp() {
				public T apply(T x) {
					return c-x;
				}
			};
		}
		@Extends((T) T.zero(), (Arithmetic) T.zero()) const UnaryOp negInv = cosub(T.zero());
		@Extends((T) T.one(), (Arithmetic) T.one()) const UnaryOp mulInv = codiv(T.unit());
		@Extends((T) T.zero(), (Arithmetic) T.zero()) public static UnaryOp codiv(final T c) {
			return new UnaryOp() {
				public T apply(T x) {
					return c/x;
				}
			};
		}
	}
	
	@Extends((T) T.zero(), (Arithmetic) T.zero())
	public Rail(:length==this.length) add(Rail(:length==this.length) o) {
		return map(o, BinaryOp.add);
	}
	
	@Extends((T) T.zero(), (Arithmetic) T.zero())
	public Rail(:length==this.length) mul(Rail(:length==this.length) o) {
		return map(o, BinaryOp.mul);
	}
	
	@Extends((T) T.zero(), (Arithmetic) T.zero())
	public Rail(:length==this.length) mulinv() {
		return map(UnaryOp.mulinv);
	}
	
	@Extends((T) T.zero(), (Arithmetic) T.zero())
	public Rail(:length==this.length) sub(Rail(:length==this.length) o) {
		return map(o, BinaryOp.sub);
	}
	
	@Extends((T) T.zero(), (Arithmetic) T.zero())
	public Rail(:length==this.length) neginv() {
		return map(UnaryOp.neginv);
	}
	
	@Extends((T) T.zero(), (Arithmetic) T.zero())
	public Rail(:length==this.length) cosub(Rail(:length==this.length) o) {
		return o.map(this, BinaryOp.cosub);
	}
	
	@Extends((T) T.zero(), (Arithmetic) T.zero())
	public Rail(:length==this.length) codiv(Rail(:length==this.length) o) {
		return o.map(this, BinaryOp.codiv);
	}
	
	@Extends((T) T.zero(), (Arithmetic) T.zero())
	public Rail(:length==this.length) div(Rail(:length==this.length) o) {
		return map(this, BinaryOp.div);
	}
	
	public Rail(:length==this.length) map(Rail(:length==this.length) other, 
			BinaryOp b) {
		final Rail(:length==this.length) r = this.clone();
		for (int i=0; i < this.length; i++) {
			r.set(i, b.apply(get(i), other.get(i)));
		}
		return r; 
	}
	public Rail(:length==this.length) map(UnaryOp b) {
		final Rail(:length==this.length) r = this.clone();
		for (int i=0; i < this.length; i++) {
			r.set(i, b.apply(get(i)));
		}
		return r; 
	}
	
	public  T get(int i) { 
		// get the right value.
		return T.zero();
	}
	public  void set(int i, T value) {
		// update.
	}
	
	public Rail@T append(Rail@T other) {
		// concatenate this with other.
		return other;
	}
	
	
}