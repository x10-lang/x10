/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test inheritance and generics.
 *
 * @author nystrom 8/2008
 */
public class GenericsInheritance3 extends x10Test {
        class C implements (int) => int, (String) => int {
                public def apply(x: String): int = { return 0; }
                public def apply(x: int): int = { return 0; }
        }

	public def run(): boolean = {
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new GenericsInheritance3().execute();
	}
}

