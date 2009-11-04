/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test type parameter bounds.
 *
 * @author nystrom 8/2008
 */
public class Bounds7 extends x10Test {
      
        interface Sum {
          def sum():int;
        }

        public class Test[T]{T <: Sum} {
            def sum(a:T) = a.sum();

        }

	public def run() = true;
	public static def main( Rail[String]) {
		new Bounds7().execute();
	}
}


