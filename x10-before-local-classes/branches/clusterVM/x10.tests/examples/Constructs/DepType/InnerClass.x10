//LIMITATION:
//Anonymous inner classes with new methods defined on them are not correctly handled.
/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The test checks that final fields can be accessed in a depclause.
 *
 * @author vcave, vj
 */
public class InnerClass extends x10Test {
	interface I  {
		 def test(): void;
		 def whatever():I;
	}
	
	public def run(): boolean = {
	   x10.io.Console.OUT.println((new I() {
		   public def test(): void = {
			   x10.io.Console.OUT.println("Inner Class test invoked.");
		   }
		   public def whatever() = this;
	   }).whatever() + " hmm.. this worked");
	    return true;
	}
	public static def main(var args: Rail[String]): void = {
		new InnerClass().execute();
	}
}
