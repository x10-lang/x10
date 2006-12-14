/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Conditional and is evaluated, well, conditionally. So it must be translated thus:
   c = a && b
   =>
   <stmt-a>
   boolean result = a;
   if (a) {
     <stmt-b>;
     result = b;
     }
     c = result;
     @author vj
 */
 
public class FlattenCondAnd extends x10Test {
    boolean[.] a;
    public FlattenCondAnd() {
      a = new boolean[[1:10,1:10]] (point [i,j]) { return false;};
    }
    boolean m(boolean x) {
      return x;
    }
	public boolean run() {
	    boolean x = m(a[1,1]) && a[0,0]; // the second expression will throw an exception if it is evaluated.
	    
	    return !x;
	}

	public static void main(String[] args) {
		new FlattenCondAnd().execute();
	}
	
}
