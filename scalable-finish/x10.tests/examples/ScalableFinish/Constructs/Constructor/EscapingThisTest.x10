// check that "this" doesn't escape from any ctor
class Test(p:Test) {
	var tt:Test;
	val w:Int;
	global val v1:Int = 1;
	var x1:Int;
	static def foo(t:Test)=2;	
	static def bar(t:Inner)=2;	
	def this() {
		this(null);
	}
	def this(i:Int) {
		this(i,null);
	}
	def this(q:Test!):Test{self.p==q} {
		property(q);
		w = 2;
		val alias = q; 
		val callToString = ""+q;
		val callOp1 = q+q; 
		q.tt = q;
		this.tt = q;
		foo(q);
		q.m();
		q.z(q);
		val inner = q.new Inner();
	}
	def this(i:Int,q:Test!):
			Test{self.p==self} {
		property(this); // ERR
		w = 4;
		val alias = this; // ERR
		val callToString = ""+this; // ERR
		val callOp1 = q+this; // ERR
		val callOp2 = this+q; // ERR
		q.tt = this; // ERR
		this.tt = q;
		q.tt = this.tt;
		foo(this); // ERR
		this.m(); // ERR
		q.z(this);  // ERR
		val inner1 = new Inner(); // ERR
		val inner2 = this.new Inner(); // ERR
	}
	operator this+(that:Test):Test = null;
	def m() {}
	global def g() {}
	global def z(q:Test) {}

	// inner class - this of the inner class cannot escape, but the outer can escape (because you access it's fields via methods, e.g., Outer.this.getHeader())
	class Inner {
		val f:Int;
		global val v2:Int = 4;
		var x2:Int;
		def this() {
			f = 3;
			x2 = v2;
			x2 = v1;
			// Outer "this" can escape
			g();
			Test.this.g();
			z(Test.this);
			// Inner "this" can NOT escape
			f(null); // ERR
			this.f(null); // ERR
			val z:Inner! = null;
			z.f(z);
			z.f(this); // ERR
			bar(this); // ERR
			bar(z);
		}
		def f(inner:Inner) {}
	}
}
