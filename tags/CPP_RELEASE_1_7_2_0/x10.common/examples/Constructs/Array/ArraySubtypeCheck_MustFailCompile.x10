/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 */
public class ArraySubtypeCheck_MustFailCompile extends x10Test {
	class Sup { }
	class Sub extends Sup { }
	
	public boolean run() {
		Sub[.] subarr00 = new Sub[[0:3]];
		Sup[.] suparr00 = subarr00;
		return true;
	}

	public static void main(String[] args) {
		new ArraySubtypeCheck_MustFailCompile().execute();
	}
}

