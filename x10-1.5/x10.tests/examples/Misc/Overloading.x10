/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test that coercions and overloading behave.
 * A non-coerced call should be more specific than a coerced call. 
 *
 * @author nystrom 9/2008
 */
public class Overloading extends x10Test {
        public def f(r: Region) = "region";
        public def f(d: Dist) = "dist";

        public def g(d: Dist) = "dist";
        public def g(r: Region) = "region";

	public def run(): boolean = {
                r: Region = 0..1;
                d: Dist = r->here;

                val r1 = f(r);
                val r2 = f(d);
                val r3 = g(r);
                val r4 = g(d);

                return r1.equals("region")
                    && r2.equals("dist")
                    && r3.equals("region")
                    && r4.equals("dist");
	}

	public static def main(var args: Rail[String]): void = {
		new Overloading().execute();
	}
}

