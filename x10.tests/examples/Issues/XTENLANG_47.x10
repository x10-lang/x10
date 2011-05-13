/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_47 extends x10Test {
	static class C(rank:Int) {
		def this(r:Int) = property(r);
	}
	public class CL(rank2:Int) {

		// this reference to rank2 should succeed.
		private class It implements Iterator[C{self.rank==rank2}] {  
			public def hasNext()=true;
			public def next(): C{self.rank==rank2}=null;
			public def remove() {}
		}

		def this(r:Int) = property(r);
	}

	public def run()=true;

	public static def main(Array[String](1)) {
		new XTENLANG_47().execute();
	}
}


class XXXXXXXXXX {
	class B(rank2:Int) {
		class C {}
		class D {
			class A {
				abstract class It implements Iterator[C{1==rank2}] {}
			}
		}
	}
}