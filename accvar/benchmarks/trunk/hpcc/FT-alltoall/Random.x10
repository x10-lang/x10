package FT;

import x10.compiler.Native;

class Random {
	@Native("c++", "srandom(#1)")
	static native def srandom(seed:Int):void;

	@Native("c++", "random()")
	native def random():Long;
	
	def this(I:Int) {
		srandom(I);
	}
	
	def nextDouble() = random() / ((1L<<31) as Double);
}
