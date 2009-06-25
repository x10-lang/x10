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
 * This tests a code generation bug.  The generated code should NOT be this:
 *
 *     class I<T> { T m(); }
 *     class C { public int m() { return 0; } }
 *     class D extends C implements I<Integer> { }
 *
 * The problem is that, to implement I<Integer>, D needs to have:
 *
 *     public Integer m() { return m(); }
 *
 * But that would have a name conflict with int m().
 *
 * @author nystrom 8/2008
 */
public class GenericsInheritance1 extends x10Test {
        interface I[T] { def m(): T; }
        class C { public def m(): int = { return 0; } }
        class D extends C implements I[int] { }

	public def run(): boolean = {
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new GenericsInheritance1().execute();
	}
}

