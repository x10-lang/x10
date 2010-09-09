import harness.x10Test;

/**
Test that methods whose return types are deptypes are handled correctly when
they are defined in another class, in a different source unit from the class
which references them. This program should not give a compile-time error -- the
compiler should be able to establish that the arguments for - are regions of
rank==3.
@author vj
**/
public class DepTypeInMethodArgDependsOnArg extends x10Test {

  public static void arraycopy(final double [.] a_dest, 
		  final double [:rank==a_dest.rank] a_src){	
    	 
}
	public boolean run() {
		final double[:rank==2] buffDest = new double[[1:10,1:10]->here];
		double[:rank==2] buffSrc = buffDest;
		arraycopy(buffDest,  buffSrc);
		return true;
	}
	
	public static void main(String args[]) {
		
		new DepTypeInMethodArgDependsOnArg().execute();
	}
}

