/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * A proto method must be called on a proto receiver.
 * @author vj
 */
public class ProtoCall_MustFailCompile extends x10Test {

    class A {}
    val a:A;
    
    /**
     * This should be declared proto since it is called on this from within a receiver.
     */
    def makeA():A = new A();
    
    public def this() {
    	a = makeA();
    }
    public def run()=true;

    public static def main(Rail[String])  {
	new ProtoCall_MustFailCompile().execute();
    }
}
