/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing the ability to assign to the field of an object
 * at place here a reference to an object at place here.next().
 *
 * @author vj
 */
public class AtNext extends x10Test {

	public def run(): boolean = {
		val Other: Place = here.next();
		val t = new T();
		at (Other) {
			val t1: T = new T();
			at (t) t.val = t1;
		}
		return t.val.home == Other;
	}

	public static def main(Rail[String]) {
		new AtNext().execute();
	}

	static class T {
		var val:Object;
	}
}
