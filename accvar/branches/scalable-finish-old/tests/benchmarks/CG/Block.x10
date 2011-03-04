package npb2;

/**
  Represents the information stored at a place for the portion of the
  SparseMat allocated to that place.
  (C) Copyright IBM Corp. 2007
*/
public class Block(int I, int J) {
	  final int bx, by, iStart, iEnd, jStart, jEnd;
	  int localSize; // number of data points in this block.
	  // Local data for this block, in compressed row form. 
	  final double[:rail] a; // size=localSize
	  final int[:rail] colIds; // size=localSize
	  final int[:rect&&rank==1] rows; // defined over [iStart: iEnd+1]
	  /**
	  Create a Block at place (I,J) with bx rows and by columns, initializing
	  with data in in (note that CRSparseMatrix represents global data, across
	  all rows and columns).
	  */
	  public Block(int I, int J, int bx, int by, CRSparseMatrix in) {
	      property(I,J);
	      iStart=I*bx; iEnd=iStart+bx-1; jStart=J*by; jEnd=jStart+by-1;
	      this.bx=bx; this.by=by;
	      for (point [i] : [iStart : iEnd]){ // compute localSize
		  for (int j=in.rows[i];j<=in.rows[i+1]-1;++j) {
		      int col = in.colIds[j];
		      if (jStart<=col && col<=jEnd) localSize++;
		  }
	      }
	      a= new double[[0:localSize-1]];
	      colIds=new int[[0:localSize-1]];
	      rows=new int[[0:bx]]; // one more, required by CR format.
	      rows[0]=0;
	      int index=0;
	      for (point [i] : [0 : bx-1]){ // now repeat and copy data into a, colIds, rows
		  int rowCount=0;
		  for (int j=in.rows[i+iStart];j<=in.rows[i+iStart+1]-1;++j) {
		      int col = in.colIds[j];
		      if (jStart<=col && col<=jEnd) {
			  assert localSize > 0;
			  a[index]=in.a[j];
			  colIds[index]=in.colIds[j];
			  rowCount++; index++;
		      }
		  }
		  rows[i+1]=rows[i]+rowCount;
	      }  
	  }
	  /**
	    Multiply the bx * by matrix represented by this block by the
	    by * 1 vector in (represented by a double[]) and return 
	    the resulting bx*1 vector in out. 
	  */
	  public void multiply(double[:rail] in, double[:rail] out) {
	      for (int i=0; i < bx; ++i) {
		  double x=0.0D;
		  for (int z=rows[i]; z<rows[i+1]; ++z) 
		      x +=a[z]*in[colIds[z]-jStart];
		  out[i]=x;
	      }
	  }
	  public String toString() {
	      return super.toString() + "(I="+I+",J="+J
		  +",iStart="+iStart+",iEnd="+iEnd 
		  +",jStart="+jStart+",jEnd="+jEnd+")";
	  }
	  public void print() {
	      for (int i=0; i < bx; ++i) {
		  for (int z=rows[i]; z<rows[i+1]; ++z) 
		      System.err.print(" e["+(iStart+i)+","+colIds[z]+"]="+ a[z]);
		  if (rows[i+1]>rows[i]) System.err.println();
	      }
	  }
	  public static void main(String[] a) {
		  final CRSparseMatrix cr = CRSparseMatrix.makeOne();
		  System.err.println(cr.toString());
		  cr.print();
		  System.out.println();
		  region(:rank==2&&rect) R = CGSolver.makeProcessGrid();
		  final int N=cr.N, 
		            px=R.rank(0).high()+1, 
		            py=R.rank(1).high()+1,
		            bx=N/px,
		            by=N/py;
		  final Block[.] M=new Block[dist.UNIQUE](point [p]) {
			  final int pi=p/py, pj=p%py;
			  return new Block(pi,pj,bx, by, cr);
		  };
		  //M.print();
		  final double[:rail] in = new double[[0:by-1]] (point [i]) { return 1.0D;}; 
		  for (point p : M) {
			  finish async(M.distribution[p]) {
				  final double[:rail] out = new double[[0:bx-1]]; 
				  System.out.println(M[p]);
				  M[p].multiply(in, out);
				  for (point [k] : out) System.out.println("out["+k+"]="+out[k]);
			  }
		  }

	  }
}
