/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Tests that the constraint d of an extended type D(:d) is entailed by the
 * type returned by the constructor of the subtype.
 *
 * @author pvarma
 */
public class SuperExtendsRule_MustFailCompile extends x10Test {

	static class Test(i:int, j:int) {
		public def this(ii:int, jj:int):Test{self.i==ii,self.j==jj} {
			property(ii, jj);
		}
	}

	static class Test2(k:int) extends Test {
		def this(k:int):Test2{i==j} = {
			// the call to super below violates the constraint i == j
			super(0,1);
			property(k);
		}
	}

	public def run(): boolean = {
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new SuperExtendsRule_MustFailCompile().execute();
	}
}
