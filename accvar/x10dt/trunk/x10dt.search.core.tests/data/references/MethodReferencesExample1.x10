public class MethodReferencesExample1 {
	
	interface I {
		def m(): int;
	}
	
	class A implements I {
		public def m() {
			return Math.max(10,11);
		}
	}
	
	class B extends A {
	    def this() {}
	    
		public def m() {
			return Math.max(0,1);
		}
	}
	
	public def run() {
		var a : A = new B();
		a.m();
	}
	
}