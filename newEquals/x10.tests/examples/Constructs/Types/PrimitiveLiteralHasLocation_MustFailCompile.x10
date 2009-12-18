/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * 3 is an int, hence a struct, hence has no home.
 *
 * @author vj, igor 09/06
 */
public class PrimitiveLiteralHasLocation_MustFailCompile extends x10Test {

    public def run(): boolean = {
	return 3.home==null;
    }

	public static def main(Rail[String]) {
	    new PrimitiveLiteralHasLocation_MustFailCompile().execute();
	}
}
