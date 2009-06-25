/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that info from the real clause of a type is propagated down
 * to the use of the type.
 */
public class FieldPropFromThis extends x10Test {
	class  Foo(i:int(0)) {
		public def this():Foo {
			property(0);
		}
	}
	
	                   
	public def run(): boolean = {
		var f: Foo = new Foo();
		var s: int(0) = f.i;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new FieldPropFromThis().execute();
	}


}
