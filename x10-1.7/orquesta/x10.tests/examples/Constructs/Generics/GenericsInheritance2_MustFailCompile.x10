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
public class GenericsInheritance2_MustFailCompile extends x10Test {
        interface I[T] { def m(): T; }
        class C implements I[int], I[float] {
                /* conflict, also can't implement m anyway */
        }

	public def run(): boolean = {
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new GenericsInheritance2_MustFailCompile().execute();
	}
}

