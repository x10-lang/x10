/*(C) Copyright IBM Corp. 2007*/
package npb2;
import java.util.*;
public class CRSparseMatrix {
	final int N, NZ;
	final double[] a;
	final int[] colIds;
	final int[] rows;
	final double zeta;
	final int niter;
	final double shift;
	
	CRSparseMatrix(double[] a, int[] colIds, int[] rows, double zeta) {
		assert a.length==colIds.length;
		NZ = a.length;
		N = rows.length-1;
		niter = 1; //not used in this case
		shift = 0;
		this.a=a; this.colIds=colIds; this.rows=rows; this.zeta=zeta;
	}
	
	CRSparseMatrix(double[] a, int[] colIds, int[] rows, double zeta, int niter, double shift) {
		assert a.length==colIds.length;
		NZ = a.length;
		N = rows.length-1;
		this.a=a; this.colIds=colIds; this.rows=rows; this.zeta=zeta;
		this.niter = niter;
		this.shift = shift;
	}
	public void print() {
		int index=0;
		for (int i=0; i < rows.length-1; i++) {
			int count=rows[i+1]-rows[i];
			for (int z=index; z < index+count; z++) {
				System.err.print(" e["+i+","+colIds[z]+"]="+a[z]);
			}
			if (count>0) System.err.println();
			index +=count;
		}
	}
	public String toString() {
		return super.toString() + "(N="+N + ",NZ="+NZ+")";
	}
	
	/* The standard NPB test problems (S, W, A, B, C) haven't been defined here yet. 
	   Instead, we use a small test problem. The reason is that the dimension of the
	   matrix and vectores is assumed to be a power of 2 in our implementation. But 
	   the general case can be easily implemented.*/
	public static CRSparseMatrix makeSS() {
		final double [] a={2,1,1,2,1,1,2,1,1,2,1,1,2,1,1,2,1,1,2,1,1,2,1,1,
				   2,1,1,2,1,1,2,1,1,2,1,1,2,1,1,2,1,1,2,1,1,2};
		final int [] colIds={0,1,0,1,2,1,2,3,2,3,4,3,4,5,4,5,6,5,6,7,6,7,8,
				     7,8,9,8,9,10,9,10,11,10,11,12,11,12,13,12,13,14,
				     13,14,15,14,15};
		final int [] rows= {0,2,5,8,11,14,17,20,23,26,29,32,35,38,41,44,46};
		/*A's first two smallest eigenvalues are 0.03405380063220 and 0.13505554119129.
		  Since initially vector x is set to be the ones which happens to be the eigenvector of 
		  the smallest eigenvalue, if the niter is small then the algorithm will converge to 
		  the second smallest eigenvalue of the test matrix. But if niter is large enough, it 
		  will eventually converges to the smallest one.*/
		double zeta=0.13505554119129; //if niter is large, then set it to be 0.03405380063220
		return new CRSparseMatrix(a, colIds, rows,zeta);
	}
	
	public static CRSparseMatrix makeOne() {
		final double [] a={11, 22, 44, 45, 46, 47, 413, 81, 87,912,121,127,1210,1414};
		final int [] colIds={1, 2, 4, 5, 6, 7, 13, 1, 7, 12, 1, 7, 10,14};
		final int [] rows= {0, 0, 1, 2, 2, 7, 7,7,7,9,10,10,10,13,13,14,14};
		return new CRSparseMatrix(a,colIds, rows,0.0D/*fake*/);
	}
	
	public static CRSparseMatrix make(char CLASS) {
		int na =0, nonzer=0, niter=1;
		double shift=0,rcond=0,zeta_verify_value=0;
		int nz =0;
		final int firstrow, lastrow, firstcol, lastcol;
		Random rng = new Random();
		double zeta = rng.randlc(CGGen.amult);
		int colidx[], rowstr[];
		double a[];
		
		switch(CLASS){
		case 'S':
			na=1400; 
			nonzer=7;
			shift=10;
			niter=15;
			rcond=.1;
			zeta_verify_value = 8.5971775078648;
			break;
		case 'W':
			na=7000; 
			nonzer=8;
			shift=12;
			niter=15;
			rcond=.1;	
			zeta_verify_value = 10.362595087124;
			break;   
		case 'A':
			na=14000;
			nonzer=11;
			shift=20;
			niter=15;
			rcond=.1;
			zeta_verify_value = 17.130235054029;
			break;
		case 'B':
			na=75000;
			nonzer=13;
			shift=60;
			niter=75;
			rcond=.1;	
			zeta_verify_value = 22.712745482631;
			break;
		case 'C':
			na=150000; 
			nonzer=15;
			shift=110;
			niter=75;
			rcond=.1;
			zeta_verify_value = 28.973605592845;
			break;
		}
		nz = (na*(nonzer+1)*(nonzer+1)+ na*(nonzer+2) );
		colidx = new int[nz+1]; 
		rowstr = new int[na+2]; 
		a = new double[nz+1];
		firstrow = 1;
		lastrow  = na;
		firstcol = 1;
		lastcol  = na;
		
		CGGen cggen = new CGGen(firstrow, lastrow, firstcol, lastcol, rng);
		cggen.makea(na, nz, a, colidx, rowstr, nonzer, rcond, shift);
		
		/*switch to zero-based*/
		final int computedNZ = rowstr[na+1]-1; 
		//System.out.println("computed zeros = "+computedNZ);
		final double [] A= new double [computedNZ];
		final int [] colIds= new int [computedNZ];
		final int [] rows= new int [na+1];
		for (int i=0;i<computedNZ;i++){
			A[i] = a[i+1];
			colIds[i] = colidx[i+1]-1;
		}
		for (int i=0;i<na+1;i++) rows[i]=rowstr[i+1]-1;
		return new CRSparseMatrix(A,colIds, rows,zeta_verify_value, niter, shift);
	}
}
