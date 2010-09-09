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
 * Return depclause cannot reference a non-final parameter.
 *
 * @author vj
 */
public class DepTypeInMethodRet1_MustFailCompile extends x10Test {
   
   public boolean(:self==t) m( boolean t) { 
      return t;
    }
	public boolean run() {
	   
	   return m(true);
	}
	public static void main(String[] args) {
		new DepTypeInMethodRet1_MustFailCompile().execute();
	}
}