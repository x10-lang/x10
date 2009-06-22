/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a sequential method can be overridden only by a sequential method.
 * @author vj  9/2006
 */
public class SequentialOverride_MustFailCompile extends x10Test {

    class T1 {
      public sequential void m() {
      }
    }
    class T2 extends T1 {
      public void m() { // should give a compile error.
      }
      }
   
	public boolean run() {
		
		return true;
	}

	public static void main(String[] args) {
		new SequentialOverride_MustFailCompile().execute();
	}

	
}
