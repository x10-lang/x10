public class FieldReferencesExample1 {
	
	interface I {
		def m(): void;
	}
	
	class A implements I {
		public def m() {
			this.f = 12;
		}
		
		var f : Int;
	}
	
	class B extends A {
		public def m() {
			super.f = 24;
			if (this instanceof A) {
			  Console.OUT.println("I'm A");
			}
		}
	}
	
	public def run() {
		var a : A = new B();
		a.m();
		a.f = 56;
	}
	
}