/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * It is ok to assign a field of a proto receiver.
 * @author vj
 */
public class ProtoAssignField extends x10Test {

    class A {
    	var x: int;
    }
    
    
    /**
     * Cannot read a field of a proto value.
     */
    def m(a: proto A) {
    	a.x = 5;
    }
    
    
    public def run()=true;

    public static def main(Rail[String])  {
	new ProtoAssignField().execute();
    }
}
