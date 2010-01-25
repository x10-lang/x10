/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Inference of least common ancestor type.
 *
 * @author vj 2/29/2010
 */
public class LCAClassAClassB extends x10Test {
	class A {}
		
	class B {}
	public def run() {
	  val x = new A();
      val y = new B();
	// should succeed. LCA of A and B is Object.
      val z:ValRail[Object] = [x,y];
	  return true;
	}

	public static def main(Rail[String]) {
		new LCAClassAClassB().execute();
	}
}

