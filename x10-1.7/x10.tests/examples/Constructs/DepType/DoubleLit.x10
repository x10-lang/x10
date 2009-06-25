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
 *
 * @author vj
 */
public class DoubleLit extends x10Test {
    def m():double(1.0D)=1.0D;
    
	public def run()=true;
	public static def main(var args: Rail[String]): void = {
		new DoubleLit().execute();
	}
}
