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
public class LCA1 extends x10Test {
	public def run() {
	  val x:double = 1.2345678;
      val y:int = 2;
	// should succeed. LCA of double and int is Any.
      val z:ValRail[Any] = [x,y];
	  return true;
	}

	public static def main(Rail[String]) {
		new LCA1().execute();
	}
}

