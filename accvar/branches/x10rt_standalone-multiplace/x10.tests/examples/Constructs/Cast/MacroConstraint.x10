/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a float literal can be cast as float.
 */
public class MacroConstraint extends x10Test {
	public def run()  {
	    val v:Object = 0..5;
	    var result:boolean=false;
	    val c = v instanceof Region(1);
	    if (!c) {
		Console.OUT.println("Failed v instanceof Region(1)");
		result=false;
	    }
	    val d = v instanceof Region(2);
	    if (c) {
		Console.OUT.println("Failed v instanceof Region(2)");
		result=false;
	    }
	    try {
		val e = v as Region(1);
	    } catch (ClassCastException) {
		Console.OUT.println("Failed v as  Region(1)");
		result= false;
	    }
	    try {
		val f = v as Region(2);
		Console.OUT.println("Failed v as  Region(2)");
		result=false;
	    } catch (ClassCastException) {
	    }
	    return result;
	}

	public static def main(args: Rail[String]) {
		new MacroConstraint().execute();
	}


}
