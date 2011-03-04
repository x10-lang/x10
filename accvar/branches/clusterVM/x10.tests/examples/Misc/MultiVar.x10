/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test declaration of multiple variables in the same var decl, separated by commas. Fails currently.
 *
 * @author vj
 */
public class MultiVar extends x10Test {
    var a:Float,b:Float;
	public def run() = true;
    

	public static def main(var args: Rail[String]): void = {
		new MultiVar().execute();
	}
}

