/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Cannot read a field of a proto value.
 * @author vj
 */
public class ProtoReadField_MustFailCompile extends x10Test {

    class A {
    	var x: int;
    }
    
    
    /**
     * Cannot read a field of a proto value.
     */
    def m(a: proto A):int = a.x;
    
    
    public def run()=true;

    public static def main(Rail[String])  {
	new ProtoReadField_MustFailCompile().execute();
    }
}
