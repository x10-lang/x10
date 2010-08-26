package npb2;

//package NPB3_0_X10;

/*****************************************************************************
              An X10 implementation of the NPB3.0 CG Benchmark 

This class implementes the sparse matrix distributed across places. 

It assumes that methods are called from the master thread at place 0.

Related classes: CGDriver, CGSolver, Vector

Author: Tong Wen, IBM Research
        vj
Date:   06/28/06
        11/08/06 clean up comments
        11/09/16 -- vj Moved most of the local functionality to Block.
(C) Copyright IBM Corp. 2007
******************************************************************************/

public value SparseMat{	
	public final int 
	   N, // The matrix is a sparse N x N matrix.
	   px, // The place grid is [0:px-1,0:py-1] grid.
	   py, 
	   bx, // N/px
	   by; // N/py
	
	public final region(:rect&&rank==2&&zeroBased) R;
	final CRSparseMatrix crSparseMatrix;
	final Block[:rail] M; // The matrix itself
	final Vector temp; //used for internal work.
	
	/* Matrices and vectors here are zero based. The sparse matrix is stored 
	in the compact row format.*/                    
	public SparseMat(final region(:rect&&rank==2&&zeroBased) R, 
			final CRSparseMatrix in) {
		this.R=R;
	        this.crSparseMatrix=in;
		N=in.N;
		px=R.rank(0).high()+1;
		py=R.rank(1).high()+1;
		bx=N/px;
		by=N/py;
		if (N%px != 0 || N%py !=0) {
			System.err.println("N=(" + in.N+") must be a multiple of px=("+px
					+ " and py(=" + py + ").");
			throw new UnsupportedOperationException();
			
		}
		M= (Block[:rail]) new Block[dist.UNIQUE](point [p]) {
			int pi = p/py, pj=p%py;
			return new Block(pi,pj,bx, by, in);
		};
		temp = new Vector(R, bx, 0.0D);
	}
	
	public void print(){
		 for (point p : M) 
			  finish async(M.distribution[p]) {
				  System.err.println(M[p].toString());
				  M[p].print();
			  }
	}
	public final void multiply(final Vector in, final Vector out){
		finish ateach(point p : M)	
		    M[p].multiply((double[:rail]) in.a[p].e, (double[:rail]) temp.a[p].e);
		temp.sumReducePieces();
		temp.transpose(out);
	}
}
