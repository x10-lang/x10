/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//Anonymous inner classes with new methods defined on them are not correctly handled.
import harness.x10Test;

/**
 * The test checks that final fields can be accessed in a depclause.
 *
 * @author vcave, vj
 */
public class InnerClass extends x10Test {
	public interface I {
		 void test();
	}
	
	public boolean run() {
	   System.out.println((new I() {
		   public void test() {
			   System.out.println("Inner Class test invoked.");
		   }
		   public java.lang.Object whatever() {
			   return this;
		   }
	   }).whatever() + " hmm.. this worked");
	    return true;
	}
	public static void main(String[] args) {
		new InnerClass().execute();
	}
}

