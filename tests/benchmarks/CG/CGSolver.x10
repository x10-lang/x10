package npb2;
/*************************************************************************
                An X10 implementation of NPB3.0 CG Benchmark
                
This class implements  the CG algorithm for solving the linear system.

Related class: CGDriver, SparseMat, Vector

Author: Tong Wen @IBM Research
        vj

Date:   06/23/06
        11/08/06 clean up comments
        11/09/07 Completely redone.

(C) Copyright IBM Corp. 2007
**************************************************************************/

public value CGSolver{	
    const int N_ITER=10;
    public static region(:rect&&zeroBased&&rank==2) makeProcessGrid() {
    	System.out.println("place.MAX_PLACES=" + place.MAX_PLACES);
	final int P = place.MAX_PLACES;
	assert Util.powerOf2(P);
	int numCuts=Util.log2(P);
	int a=numCuts/2, b=numCuts%2;
	if (b > 0) {
	    System.err.println("Limitation: This code currently works only for square process grids.");
	    System.err.println("Please run with even log2(place.NUM_PLACES).");
	    throw new UnsupportedOperationException();
	}
	assert b==0;
	int py=1<<(a+(b>0?1:0)); int px=1 << (a);
	System.err.println("numCuts=" + numCuts +",px="+px+",py="+py);
	region(:rect&&zeroBased&&rank==2) R = [0:px-1,0:py-1];
	return R;
    }
    /* the CG algorithm for solving the linear system */
    public static Vector(:localSize==x.localSize&& R==x.R) cg(SparseMat A, final Vector x, 
    		final Vector(:localSize==x.localSize&& R==x.R) r){
	final Vector(:localSize==x.localSize&& R==x.R) p=new Vector(x), q=new Vector(x), z=new Vector(x);
	r.copyFrom((Vector(:localSize==r.localSize&& R==r.R)) x); // TODO: compiler -- cast not needed.
	p.copyFrom((Vector(:localSize==p.localSize&& R==p.R)) x);
	double rho=r.dot(r);	
	for (int i=0;i<N_ITER;i++){
	    A.multiply(p,q); 
	    double d=p.dot(q);
	    double alpha=rho/d;
	    z.axpy(alpha,1.0D,p,z); 
	    double rho0=rho;
	    r.axpy(-alpha,1.0D,q,r); 
	    rho=r.dot(r); 
	    double beta=rho/rho0;
	    p.axpy(beta,1.0D,p,r); 
	}
	return z;
    }

}
