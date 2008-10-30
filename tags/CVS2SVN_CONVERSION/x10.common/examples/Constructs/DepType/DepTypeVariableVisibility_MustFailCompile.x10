/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * A local variable referenced in a deptype in a method declaration must have greater or equal accessibility/visibility than the declaration.
 *
 * @author pvarma
 */
public class DepTypeVariableVisibility_MustFailCompile extends x10Test {
   private final boolean traceOn = true;
   public boolean(:self == traceOn) m(boolean(:self == traceOn) t) { 
      return t;
    }
	public boolean run() {
	   m((boolean(:self == traceOn))traceOn); 
	   return true;
	}
	public static void main(String[] args) {
		new DepTypeVariableVisibility_MustFailCompile().execute();
	}
}