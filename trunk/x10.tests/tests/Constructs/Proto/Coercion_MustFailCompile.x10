/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 *The implicit coercion from proto A to A must fail.
 *@author vj
 */
public class Coercion_MustFailCompile extends x10Test {

    class A {}
    
    
    /**
     * The implicit coercion from proto A to A must fail.
     */
    def m(a: proto A): A = a;
    
    
    public def run()=true;

    public static def main(Rail[String])  {
	new Coercion_MustFailCompile().execute();
    }
}
