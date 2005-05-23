import x10.lang.*;
 /** 
  * Jacobi iteration
  * 
  * At each step of the iteration, replace the value of a cell with
  * the average of its adjacent cells in the (i,j) dimensions. 
  * Compute the error at each iteration as the sum of the changes
  * in value across the whole array. Continue the iteration until
  * the error falls below a given bound.
  *
  * @author vj, cvp, kemal
  */


 public class JacobiSmall  {

     static final int N=7;
     static final double epsilon=0.002;
     static final double epsilon2=0.000000001;
     final region R1 = region.factory.region(0, N+1);
     final region R2 = region.factory.region(1, N);
     final region R= region.factory.region(R1,R1);
     final region R_inner=region.factory.region(R2, R2);
     final dist D = dist.factory.block(R);
     final dist D_inner = D.restriction(R_inner);
     final dist D_Boundary = D.difference(D_inner.region);
     static final int EXPECTED_ITERS=131;
     static final double EXPECTED_ERR=0.0019977310907846046;

     
     public boolean run() {
     	
     	int iters = 0;
     	
     	final double[D] a= new double[D];
     	finish ateach(final point p:D_inner) {a[p]=(double)(p.get(0)-1)*N+(p.get(1)-1);}
     	finish ateach(final point p:D_Boundary) {a[p]=(N-1)/2;}
	double err;
     	double[D] x = a;
     	while(true) {
	    final double[D] b = x;
     		final double[D_inner] temp = new double[D_inner];
     		finish ateach(final point p:D_inner) {
     			int i = p.get(0);
     			int j = p.get(1);
     			temp[i,j]=(b[i+1,j]+b[i-1,j]+b[i,j-1]+b[i,j+1])/4.0;}
     		
     		if((err=(b.restriction(D_inner)
     				.lift(doubleArray.sub,temp)
					.lift(doubleArray.abs)
					.reduce(doubleArray.add,0.0))) < epsilon)
     			break;
	     		
     		x = x.overlay(temp);
     		iters++; 
     	}
     	System.out.println(err);
        System.out.println(iters);

     	return Math.abs(err-EXPECTED_ERR)<epsilon2 && iters==EXPECTED_ITERS;
     }
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new JacobiSmall()).run();
        } catch (Throwable e) {
                e.printStackTrace();
                b.val=false;
        }
        System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
        x10.lang.Runtime.setExitCode(b.val?0:1);
    }
    static class boxedBoolean {
        boolean val=false;
    }

     
 }
