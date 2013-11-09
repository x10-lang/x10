/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */


/**
 * A struct cannot override a method declared in a super-struct.
 * @author vj
 */
public class FinalMethod  {

   class A {
     final int m() { return 5;} // implicitly final
    }
    
    class B extends A {
    	// compiler must declare an error here.
    	int m() { return 6;}
    }

   
}
