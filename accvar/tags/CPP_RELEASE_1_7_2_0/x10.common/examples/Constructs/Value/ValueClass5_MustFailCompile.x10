/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Value class test:
 *
 * assigning to a value class field must be
 * flagged as an error by the compiler.
 *
 * @author Mandana Vaziri, kemal 4/2005
 */
public class ValueClass5_MustFailCompile extends x10Test {

	public boolean run() {
		Test1 x = new Test1(1);
		x.modifier();
		return x.f == 2;
	}

	public static void main(String[] args) {
		new ValueClass5_MustFailCompile().execute();
	}

	static value Test1 {
		int f;

		public Test1(int f) {
			this.f = f;
		}

		public void modifier() {
			//=== > compiler error should occur here
			f++;
		}
	}
}

