
/***************************************************************************************
 *                              Parallel Matrix Transpose
 * This code implements a simple parallel matrix transpose operation incoupled with an 
 * addition:
 *	                    A=A^{T}+B. 
 * The distribution of the matrices is general and its info is not used. The code can be 
 * easily extended to block-distributed matrices built using the encapsulation approach 
 * (arry of arrys) as shown by the X10 examples at our website. The assignment operation 
 * here would be replaced by the array copy method to be implemented in the next version
 * of X10, that is, the [i,j]th subarray is copied to the [j,i]th.
 * 
 * The code takes one command-line argument which is the size of the matrices A and B.
 * For example, n=100 or -n100.
 * Date: Dec 08, 2006
 * Author: Tong Wen @IBM Research
 
 (C) Copyright IBM Corp. 2006
*****************************************************************************************/
public class PTRAN{	
	
    public static void main(String[] argv) {
    	int n=8; // the default size of the matrices A and B
    	if (argv.length==1)
    		if (argv[0].startsWith("-n") || argv[0].startsWith("n=")){
    			try{
    		     	   if( argv[0].length()>2 )
    		     	     n= java.lang.Integer.parseInt(argv[0].substring(2));
    		     	 }catch(java.lang.Exception e){
    		     	     System.out.println( "argument to "+argv[0].substring(0,2)+" must be an int.");
    		     	 }
    		}
    		
    	final int size=n;
    	final region R=[1:size,1:size];
    	final dist D=dist.factory.cyclic(R); // the distribution can be anything
    	final double [.] A=new double [D] (point [i,j]) {return Math.random();};
    	final double [.] B=new double [D] (point [i,j]) {return Math.random();};
    	final double [.] AT=new double [D] (point p) {return 0;};
    	final boolean [.] Flag=new boolean [D] (point [i,j]) {return false;}; // flags to coordinate computations
        
	finish  ateach (point [i,j]:D){
		if (i==j) A[i,i]+= B[i,i];
		else{
			final double temp=A[i,j];
			finish async (D[j,i]){ //remote operations
				AT[j,i]=temp;
				atomic Flag[j,i]=true;
			}
			await(Flag[i,j]); //flag is local
			A[i,j]=AT[i,j]+B[i,j]; // addition  is local
		}
	}
    	
	/* print out the matrix A
	for (int i=1;i<=size; i++){
		for (int j=1;j<=size;j++) System.out.print(" "+A[i,j]);
		System.out.println();
	}
    	*/
    	System.out.println("PTRAN finished! The size of the matrices is "+ size+" by "+size);
    }
	
}
