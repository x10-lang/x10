package npb2;
//package NPB3_0_X10;

/*****************************************************************************************
               An X10 implementation of the NPB3.0 CG Benchmark 

In this implementation, the matrix and vectors are partitioned across places. 

Note that the mater thread is at the default place 0.

The standard NPB test problems are not defined here yet. Instead, we use a small test problem.

Related class: CGSolver, SparseMat, Vector, Util

Date:   07/14/06
        11/08/06 clean up comments

Author: Tong Wen @IBM Research
        vj
(C) Copyright IBM Corp. 2007
********************************************************************************************/

final public class CGDriver(Vector x) {
	final SparseMat A;
	
	final Vector(:localSize==this.x.localSize&&R==this.x.R) r;
	final int niter; // the number of outer iterations
	final double zeta_verify_value; // the reference result
	final double shift;
	
	public CGDriver(){
		niter=10;
		CRSparseMatrix cr = CRSparseMatrix.makeSS();
		region(:rect&&zeroBased&&rank==2) R = CGSolver.makeProcessGrid();
		A =new SparseMat(R,cr);
		zeta_verify_value=cr.zeta;
		shift =0D;
		property(new Vector((region(:rect&&rank==2)) A.R,A.bx,1.0D));
		r=new Vector(x.R, x.localSize, 0.0D);
		System.out.println("CGDriver SS created.");

	}
	/*added by Tong 11/10/2007*/
	public CGDriver(char CLASS){
		CRSparseMatrix cr = CRSparseMatrix.make(CLASS);
		niter=cr.niter;
		zeta_verify_value=cr.zeta;
		shift = cr.shift;
		region(:rect&&zeroBased&&rank==2) R = CGSolver.makeProcessGrid();
		A =new SparseMat(R,cr);
		property(new Vector((region(:rect&&rank==2)) A.R,A.bx,1.0D));
		r=new Vector(x.R,x.localSize,0.0D);
		System.out.println("CGDriver created.");

	}
    public double run(double zeta) {
	double lambda =this.shift;
	x.set(1);
	/*the inverse power method*/
	for(int i=0;i<niter;i++){
		final Vector xx = x;
		final Vector(:localSize==xx.localSize && R==xx.R) rr  = (Vector(:localSize==xx.localSize && R==xx.R)) r;
	    final Vector(:localSize==xx.localSize && R==xx.R) z=CGSolver.cg(A,xx,rr);
	    		 // TODO -- compiler cast not needed.
	    zeta=lambda+1.0/(x.dot(z));
	    System.out.println(" Checking Step "+i+": ||r||="+r.norm2()+" zeta="+zeta);
	    z.multByScalar(1/z.norm2());
	    xx.copyFrom(z);
	}
	return zeta;
    }

   /*public void test1() {
		double lambda=0.0D;
		x.set(1);
		final Vector z=CGSolver.cg(A,x,r);
		System.out.println(""+z.norm2());
	}*/
	
    public static void main(String[] args) {
    	char CLASS = 'S';
    	for (int q = 0; q < args.length; ++q) {
			if (args[q].equals("-s") || args[q].equals("-S")) {
				CLASS = 'S';
			}
			if (args[q].equals("-w") || args[q].equals("-W")) {
				CLASS = 'W';
			}
			if (args[q].equals("-a") || args[q].equals("-A")) {
				CLASS = 'A';
			}
			if (args[q].equals("-b") || args[q].equals("-B")) {
				CLASS = 'B';
			}
			if (args[q].equals("-c") || args[q].equals("-C")) {
				CLASS = 'C';
			}
	}
    	
		Timer tmr=new Timer();
		//CGDriver cg=new CGDriver();
		CGDriver cg = new CGDriver(CLASS);
		System.out.println("CLASS=" + CLASS);
		int counter=0;
		tmr.start(counter);
		double zeta=cg.run(0D);
		//cg.test1();
		tmr.stop(counter);
		System.out.println("The reference value is "+cg.zeta_verify_value+" vs the computed one "+zeta);
		System.out.println("The difference between zeta and its reference value: "+(zeta-cg.zeta_verify_value));
		System.out.println("wall time:"+tmr.readTimer(counter)+"secs");
	}
}
