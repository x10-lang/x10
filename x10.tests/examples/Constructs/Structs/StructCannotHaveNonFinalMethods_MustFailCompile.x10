/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * A struct cannot override a method declared in a super-struct.
 * @author vj
 */
public class StructCannotHaveNonFinalMethods_MustFailCompile extends x10Test {

    struct A {
     def m() = 5; // implicitly final
    }
    
    struct B extends A {
    	// compiler must declare an error here.
    	def m() = 5;
    }

    public def run()=true;

    public static def main(Rail[String])  {
	new StructCannotHaveNonFinalMethods_MustFailCompile().execute();
    }
}
