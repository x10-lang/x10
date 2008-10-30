/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a method arg can have a deptype and it is propagated into the body.
 *
 * @author vj
 */
public class MethodArgDepTypes2 extends x10Test {
	public static void arraycopy(final double [.] a_dest, final double [:rank==a_dest.rank] a_src){	
    	  final region R = a_src.region&& (region(:self.rank==a_dest.rank)) a_dest.region; 
    	  finish foreach( point p : R){
    	  //finish for( point p : R){	  
    	    a_dest[p]= future(a_src.distribution[p]) {a_src[p]}.force();
    	  }	  
    	  //for( point p : R) a_dest[p]=a_src[p]; //implicit syntax
	}
  
	public void m(final double[.] a) {
		double [.] result=a;
		final double [.] buffDest=a;
		
		double [:rank==buffDest.rank] buffSrc= (double [:rank==buffDest.rank])  buffDest;
		arraycopy(buffDest, buffSrc);
	
	}
	public boolean run() {
	   return true;
	}
	public static void main(String[] args) {
		new MethodArgDepTypes2().execute();
	}
}