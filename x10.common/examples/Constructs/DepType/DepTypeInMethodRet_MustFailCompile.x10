/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Cannot return a value of type boolean from a method whose return type is boolean(:self==true).
 *
 * @author vj
 */
public class DepTypeInMethodRet_MustFailCompile extends x10Test {
    
   public boolean(:self==true) m(boolean t) { 
      return t;
    }
	public boolean run() {
	   return m(false);
	}
	public static void main(String[] args) {
		new DepTypeInMethodRet_MustFailCompile().execute();
	}
}