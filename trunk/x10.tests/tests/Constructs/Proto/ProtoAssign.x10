/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Checks that it is ok to assign a proto A to a field of type A, 
 * where the receiver is proto.
 * @author vj
 */
public class ProtoAssign extends x10Test {

    class A {
    	val x:A;
    	def this() { x=null;}
    	def this(x: proto A):proto A {
    		this.x=x;
    	}
    }
    
    public def run()=true;

    public static def main(Rail[String])  {
	new ProtoAssign().execute();
    }
}
