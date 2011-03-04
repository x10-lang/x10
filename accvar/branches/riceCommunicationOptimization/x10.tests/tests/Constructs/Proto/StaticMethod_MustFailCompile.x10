/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Cant declare a static method proto.
 * @author vj
 */
public class StaticMethod_MustFailCompile extends x10Test {

    class A {}
    
    
    /**
     * Cant declare a static method proto.
     */
    static proto def m(a: A) = a;
    
    
    public def run()=true;

    public static def main(Rail[String])  {
	new StaticMethod_MustFailCompile().execute();
    }
}
