/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Casts to proto types are not permitted.
 * @author vj
 */
public class Cast_MustFailCompile extends x10Test {

    class A {}
    
    
    /**
     * Casts to proto types are not permitted.
     */
    def m(a: A): proto A = a as proto A;
    
    
    public def run()=true;

    public static def main(Rail[String])  {
	new Cast_MustFailCompile().execute();
    }
}
