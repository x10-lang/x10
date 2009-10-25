/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test type defs in Ctor return type.
 *
 * @author vj 8/2008
 */
public class InCtorRetType extends x10Test {
    static type Testy(i:int) = Test{self.i==i};

    static class Test(i:int) {
       def this(i:int):Testy(i) = {
          property(i);
       }
    }
	public def run()=true;

	public static def main(var args: Rail[String]): void = {
		new InCtorRetType().execute();
	}
}

