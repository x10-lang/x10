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
public class GenericsInheritance2 extends x10Test {
        interface I[T] { def m(x:T): void; }
        class C implements I[int], I[float] {
                public def m(x: int): void = { } 
                public def m(x: float): void = { }
        }

	public def run(): boolean = {
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new GenericsInheritance2().execute();
	}
}

