/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
Test that a deptype for a method arg that depends on a previous arg is handled correctly.
@author vj
**/
public class DepTypeInMethodArgDependsOnArg extends x10Test {

  public static void arraycopy(final double [.] a_dest, 
		  final double [:rank==a_dest.rank] a_src){	
    	 
}
	public boolean run() {
		final double[:rank==2] buffDest = new double[[1:10,1:10]->here];
		double[:rank==buffDest.rank] buffSrc = buffDest;
		arraycopy(buffDest,  buffSrc);
		return true;
	}
	
	public static void main(String args[]) {
		
		new DepTypeInMethodArgDependsOnArg().execute();
	}
}

