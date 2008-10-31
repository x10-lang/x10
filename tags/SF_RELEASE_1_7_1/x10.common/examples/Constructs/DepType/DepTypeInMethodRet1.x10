/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a method can have a deptype argument and it is checked properly.
 *
 * @author vj
 */
public class DepTypeInMethodRet1  extends x10Test {
   
   public boolean(:self==t) m(final boolean t) { 
      return t;
    }
	 public boolean run() {
	   return m(true);
	}
	public static void main(String[] args) {
		new DepTypeInMethodRet1().execute();
	}
}