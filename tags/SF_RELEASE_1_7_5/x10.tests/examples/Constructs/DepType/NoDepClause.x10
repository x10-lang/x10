/*
 *
 * (C) Copyright IBM Corporation 2007
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;
 
/**
 * Check that a non-dependent type String is equivalent to String(:)
 *
 * @author igor 6/17/2007
 * @author nystrom 6/17/2007
 */
public class NoDepClause extends x10Test {
	public def run(): boolean = {
	    val r:String = "abcd";
	    return r != "a";
	}
	public static def main(val args: Rail[String]): void = {
	    new NoDepClause().execute();
	}
}
