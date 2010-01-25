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
public class LCAClassAStructB extends x10Test {
	class A {}
		
	// static because currently non-static struct members are not permitted.
	// XTENLANG-929 
	static struct B { } 
	public def run() {
	  val x = new A();
      val y = B();
	// should succeed. LCA of A and B is Any
      val z:ValRail[Any] = [x,y];
	  return true;
	}

	public static def main(Rail[String]) {
		new LCAClassAStructB().execute();
	}
}

