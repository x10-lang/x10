/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * A method with a proto argument must return a proto type or be a void method.
 * @author vj
 */
public class MethodMustReturnProto_MustFailCompile extends x10Test {

    class A {}
    
    
    /**
     * This should be declared proto: a method with a proto argument must return
     * a proto type or be a void method.
     */
    def m(a: proto A): A = new A();
    
    
    public def run()=true;

    public static def main(Rail[String])  {
	new MethodMustReturnProto_MustFailCompile().execute();
    }
}
