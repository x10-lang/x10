/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that properties may be declared on interfaces.
 *
 * @author raj
 */
public class InterfaceProp extends x10Test {
	interface  I(int i) {
      public void  a();
	}
	interface  J(int k) {
      public void  a();
	}
	class E(int m, int k) extends D implements J{
      public E(int mm, int nn, int ii, int kk) { super(nn,ii); m = mm; k = kk;}
      public void a() {
        int x;
      }
	}
	class D(int n, int i) implements I {
      public D(int nn, int ii) { n = nn; i=ii; }
      public void a() {
        int x;
      }
	}
	public boolean run() {
        new E(1,2,3,4);
	    return true;
	}
	public static void main(String[] args) {
		new InterfaceProp().execute();
	}
}


