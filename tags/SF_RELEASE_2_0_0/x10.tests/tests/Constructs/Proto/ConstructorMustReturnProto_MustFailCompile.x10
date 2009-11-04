/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * A constructor with a proto argument must return a proto type.
 * @author vj
 */
public class ConstructorMustReturnProto_MustFailCompile extends x10Test {

    class A {
    	val x:A;
    	def this() { x=null;}
    	def this(x: proto A) {
    		this.x=x;
    	}
    }
    
    public def run()=true;

    public static def main(Rail[String])  {
	new ConstructorMustReturnProto_MustFailCompile().execute();
    }
}
