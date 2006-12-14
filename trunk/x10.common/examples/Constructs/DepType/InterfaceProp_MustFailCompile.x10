/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Classes implementing interfaces with properties must also 
 * define the same properties, or else an error is thrown
 *
 * @author raj
 */
public class InterfaceProp_MustFailCompile extends x10Test {
  
	interface  I(int i) {
      public void  a();
	}
	interface  J(int k) extends I{
      public void  a();
	}
	class E(int k) implements J{
      public E(int kk) { k = kk;}
      public void a() {
        int x;
      }
	}
	
	public boolean run() {
        new E(1);
	    return true;
	}
	public static void main(String[] args) {
		new InterfaceProp_MustFailCompile().execute();
	}
}


