
// Yoav: testing the safe modifier on methods, ctors, classes and closures
// (I hope these modifiers will be removed:  pinned  nonblocking sequential)
class TestSafeModifier {
	var p:Place = here.next();
	var i:Int;
	var safeClosure: safe ()=>void;
	var nonSafeClosure: ()=>void;
	def nonSafe() {}
	safe def m() {
		new Inner(5);
		safeClosure();
	}
	safe def q() { atomic m(); }
	def err() { 
		atomic nonSafe(); // ERR
	} 
	// todo: the following should be an error
	safe def m1() { at (p) {} } // ERR: not pinned
	safe def m2() { when (i==3) {} } // ERR: not nonblocking
	safe def m3() { async {} } // ERR: not sequential
	safe def m4() { nonSafe(); } // ERR: calling non-safe
	safe def m5() { nonSafeClosure(); } // ERR: calling non-safe closure
	safe def m6() { new Inner(); } // ERR: calling ctor without "safe"

	class Inner {
		def this() { }
		safe def this(i:int) { }
	}
	static class NonSafeFieldInit {
		var i:Int;
		var j:Int = at (here.next()) 2*i;
		safe def this() {} // ERR: a field initializer has "at"
	}

	static class NonSafeOperator {
		safe def this(a:NonSafeOperator) { 
			val q = a+a; // ERR: calling non-safe operator
		}
		operator this+(that:NonSafeOperator):NonSafeOperator = null;
	}

	safe static class Bar {
		def m() {} // it is implicitly safe
		def ok() { atomic m(); }
		static class Foo {
			def m() {} // it is implicitly safe
			def ok() { atomic m(); }
		}
	}

	// check legal overriding with safe
	static interface I {
		safe def safeI():void;
		def nonSafeI():void;
	}
	static class Impl1 implements I {
		public safe def safeI() {}
		public safe def nonSafeI() {}
	}
	static class Impl2 implements I {
		public def safeI() {} // ERR: public safeI(): x10.lang.Void in TestSafeModifier.Impl2 cannot override abstract public safe safeI(): x10.lang.Void in TestSafeModifier.I; attempting to assign weaker behavioral annotations
		public def nonSafeI() {}
	}
	static class Super {
		def this() {}
		safe def this(i:Int) {}

		safe def m() {}
		nonblocking def q() {}
	}
	static class Sub extends Super {
		safe def this() {
			super(); // ERR: calling non-safe super ctor
		}
		def this(i:Int) {
			super(i);
		}
		safe def this(i:Boolean) {
			super(4);
		}

		def m() {} // ERR: Semantic Error:  m(): x10.lang.Void in TestSafeModifier.Sub cannot override safe m(): x10.lang.Void in TestSafeModifier.Super; attempting to assign weaker behavioral annotations
		safe def q() {}
	}
}