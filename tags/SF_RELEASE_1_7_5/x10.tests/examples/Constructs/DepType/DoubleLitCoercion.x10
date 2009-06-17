/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */

import harness.x10Test;

/**
 * 
 * 1.0 should be coerced to 1.0D in expressions and type expansions.
 * @author vj
 */
public class DoubleLitCoercion extends x10Test {
    def m():double(1.0)=1.0;
    
	public def run()=true;
	public static def main(var args: Rail[String]): void = {
		new DoubleLitCoercion().execute();
	}
}
