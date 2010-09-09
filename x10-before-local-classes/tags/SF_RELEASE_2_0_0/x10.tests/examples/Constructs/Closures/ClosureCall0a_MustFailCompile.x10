// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *



import harness.x10Test;


/**
 * Closures are no longer permitted to take type parameters.
 */

public class ClosureCall0a_MustFailCompile extends x10Test {

    public def run(): boolean = {
	val s= "hi";
        val a = ([T](x:T)=>x)(s);
        return a.equals(s);
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureCall0a_MustFailCompile().execute();
    }
}
