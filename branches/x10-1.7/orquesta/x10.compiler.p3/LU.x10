package lu;
/**
   (C) IBM Corporation 2007.
   
   LU decomposition (with priority) with partial pivoting (shared memory version).
 
   @author Sriram Krishnamoorthy, sriramkr@us.ibm.com
   @author vj vijay@saraswat.org
   
   Working around new Block[p][n] by using Blocks as a wrapper class for Block[].
*/
import java.util.concurrent.atomic.*; 

public class PLU2_C(int px, int py, int nx, int ny, int B) {   
    public static final int NUM_TESTS=10, LOOK_AHEAD=6, RAND_MAX=100;
    final AtomicIntegerArray/*(: in(self[index],N))*/ pivots; 
    final int bx,by,N;
    final boolean C_MODE;
    final Blocks[] A;
    public PLU2_C(int px_, int py_, int nx_, int ny_, int B_, boolean C_MODE_) {
    	property(px_,py_,nx_,ny_,B_);
	bx=nx*px; by=ny*py; N=bx*B; C_MODE=C_MODE_;
	assert bx==by;
	pivots = new AtomicIntegerArray(N);
	for(int i=0; i<pivots.length(); i++) pivots.set(i,i);
	A = new Blocks[px*py];
	for (int i=0; i < px*py; i++) A[i] = new Blocks(nx*ny);
	for(int pi=0; pi<px; ++pi) 
		for(int pj=0; pj<py; ++pj) 
		    for(int i=0; i<nx; ++i) 
			for(int j=0; j<ny; ++j) 
			    A[pord(pi,pj)].z[lord(i,j)] = new Block(i*px+pi, j*py+pj); 
	}
    public PLU2_C(PLU2_C o) {
    	property(o.px,o.py,o.nx,o.ny,o.B);
    	bx=o.bx; by=o.by; N=o.N;C_MODE=o.C_MODE;
    	pivots = o.pivots; // pivots are shared.
    	A = new Blocks[px*py];
    	for (int i=0; i<px*py;++i) A[i] = new Blocks(nx*ny);
    	for(int pi=0; pi<px; ++pi) 
    		for(int pj=0; pj<py; ++pj) 
    			for(int i=0; i<nx; ++i) 
    				for(int j=0; j<ny; ++j) 
    					A[pord(pi,pj)].z[lord(i,j)] = new Block(o.getLocal(pi, pj, i, j));
	}
    static double format(double v, int precision){
	int scale=1;   
	for(int i=0; i<precision; i++) scale *= 10;
	return ((int)(v*scale))*1.0/scale;
    } 
    static int max(int a, int b) { return a>b?a:b; }
    static double max(double a, double b) { return a>b?a:b;}
    static double fabs(double v){ return  v>0?v:-v; }
    static int min(int a, int b) { return a>b?b:a; }
    static double flops(int n) { return ((4.0*n-3.0)*n-1.0)*n/6.0; }
    boolean in(int x, int low, int high) { return low <= x && x < high;}
    int pord(int/*(0,px)*/ i, int/*(0,py)*/ j) { return i*py+j;	}
    int lord(int/*(0,nx)*/ i, int/*(0,ny)*/ j) { return i*ny+j; }
    Block getBlock(int/*(0,bx)*/ i, int/*(0,by)*/ j) { return A[pord(i%px, j%py)].z[lord(i/px,j/py)]; }
    Block getLocal(int/*(0,px)*/ pi, int/*(0,py)*/ pj, int/*(0,nx)*/ ni, int/*(0,ny)*/ nj) { 
    	return A[pord(pi,pj)].z[lord(ni,nj)]; 
    }
    class Blocks {
    	final Block[] z;
    	Blocks(int n) { z=new Block[n]; }
    }
    // TODO: parallelize and optimize ord calculations.
    PLU2_C applyPivots(boolean lowerOnly) {
    	for(int i=0; i<bx; i++) 
    		for(int j=0; j< (lowerOnly? i : by);  j++) 
		    for(int r=i*B; r<(i+1)*B; r++) {
			final int/*(r,N)*/ target = pivots.get(r);
			if(r != target) getBlock(i,j).permute(r, target);	
		    }
	    return this;
    }
    boolean verify(PLU2_C M) { // invoked by a single thread, should be parallelized
	    /* Initialize test. */
	    double max_diff = 0.0; //  record maximum difference between any element of L*U and M. 
	    for (int i = 0; i < N; i++) {
		int iB = i%B;
		for (int j = 0; j < N; j++) {
		    final int I=i/B, J=j/B, jB = j%B;
		    double v = 0.0;
		    int k;
		    for (k=0; k<i && k <= j; k++) {
			final int K=k/B, kB=k%B;
			v += M.getBlock(I,K).get(iB, kB) * M.getBlock(K,J).get(kB, jB);
		    }
		    if (k==i && k <= j) {
			final int K=k/B, kB=k%B;
			v += M.getBlock(K,J).get(kB, jB);
		    }
		    double diff = fabs(getBlock(I,J).get(iB, jB) - v);
		    max_diff = max(diff, max_diff);
		}
	    }
	    /* Check maximum difference against threshold. */
	    return (max_diff <= 0.01);
	}
	public void run() {
	    finish foreach (point [pi,pj] : [0:px-1,0:py-1]) {	
		int startY=0, loopCount=0;
		final Block[] myBlocks=A[pord(pi,pj)].z;
		while(startY < ny) { 
		    int readyCount=0;
		    boolean done=false;
		    for (int j=startY; j < min(startY+LOOK_AHEAD, ny) && !done; ++j) 
			for (int i=0; i <nx; ++i) {
			    final Block b = myBlocks[lord(i,j)];
			    if (b.ready) {
			    	if (j==startY) readyCount++;
			    } else { // step
			    	done |= b.step();
			    }
			    Thread.yield();
			}
		    if (readyCount==nx) startY++;
		    loopCount++;
		    if (loopCount % 100000 == 0) System.err.println("w("+pi+","+pj
		    		+ "startY=" + startY + "):" + loopCount);
		}
	    }
	}
    public static void main(String[] a) { 
	System.out.print("PLU2_C ");
	if (a.length < 4) {
	    System.out.println("Usage: PLU2_C N b px py [C_Mode] [verify] [libraryFileName] ");
	    return;
	}
	final int N = java.lang.Integer.parseInt(a[0]), 
	    B= java.lang.Integer.parseInt(a[1]),
	    px= java.lang.Integer.parseInt(a[2]), 
	    py= java.lang.Integer.parseInt(a[3]);
	boolean VERIFY = true, C_MODE=false;
	String libraryName = "/vol/x10/vj/libPLUInC.so";
	if (a.length > 4) C_MODE=java.lang.Boolean.parseBoolean(a[4]);
	if (a.length > 5) VERIFY=java.lang.Boolean.parseBoolean(a[5]);
	if (a.length > 6) libraryName = a[6];
	final int nx = N / (px*B), ny = N/(py*B);
	assert (N % (px*B) == 0 && N % (py*B) == 0);
	System.out.println("N="+N + " B=" + B + " px=" + px + " py=" + py + " nx=" + nx + " ny="+ny);
	if (C_MODE) System.load(libraryName);
	final PLU2_C orig = new PLU2_C(px,py,nx,ny,B, C_MODE);
	int i=0;
	while (i++ < NUM_TESTS) {
	    PLU2_C plu = new PLU2_C(orig);
	    System.gc();
	    long s = - System.nanoTime();     plu.run();    	    s += System.nanoTime();
	    System.out.print(" Time="+(s)/1000000+"ms"+" Rate="+format(flops(N)/(s)*1000, 3)+" MFLOPS");
	    if (VERIFY) {
		System.out.print(" (Verifying...");
		long v = - System.nanoTime();
		boolean correct = new PLU2_C(orig).applyPivots(false).verify(plu.applyPivots(true));
		v += System.nanoTime();
		System.out.print(((v)/1000000) + " ms " + (correct?" ok)":" fail)"));
	    } 
	    System.out.println();
	} 
    }
	/**
	 * A B*B array of doubles, whose top left coordinate is i,j).
	 * get/set operate on the local coordinate system, i.e.
	 * (i,j) is treated as (0,0).
	 */
	class Block(int/*(0,bx)*/ I, int/*(0,by)*/ J) {
	    final int/*(min(I,J))*/ maxCount; 
	    final double[:zeroBased&&rank==1&&rect] A;
	    @shared boolean ready = false;
	    @shared private int/*(0,maxCount)*/ count=0; 
	    //The column with the block in which LU is being done; start with an invalid value.
	    @shared int/*(-1,B)*/ LUCol=-1;
	    @shared double maxColV; //maximum value in Column LU_col
	    @shared int/*(0, N)*/ maxRow; //Row with that value			
	    int visitCount;
	    /** index of last blocks below this one whose mulsubs are not known to be done.
	     */
	    private int/*(I,nx)*/ readyBelowIndex; 	
	    private int colMaxCount=0; 
	    Block(final int/*(0,bx)*/ I, final int/*(0,by)*/ J) {
		property(I,J);
		A = (double[:zeroBased&&rank==1&&rect]) new double[[0:B*B-1]] (point [i]) { 
		    double d =Math.random();
		    return (I==J && i %B==i)? format(20.0*d+10.0/RAND_MAX,4)
		    : format(10.0*d/RAND_MAX, 4);};			
		maxCount=min(I,J);
		readyBelowIndex=I; visitCount=0;
	    }
	    Block(final Block b) {
		property(b.I,b.J); 
		A = (double[:zeroBased&&rank==1&&rect]) new double[[0:B*B-1]] (point [i]) { return b.A[i];};
		maxCount = min(I,J);
		for(int i=0; i<B*B; i++) A[i] = b.A[i];
		readyBelowIndex = I; visitCount=0;
	    }
	    public String toString() {
		return "Block("+I+","+J+ (ready? " " : " !") + "ready) count=" +count 
		    + "/"+maxCount+" rbc=" + readyBelowIndex + " LUCol=" + LUCol 
		    +  " colMaxCount=" + colMaxCount + " #visits=" + visitCount;
	    }
	   
	    /** Try to step through the next ready operation in given priority order.
		An operation on a block is LU, backSolve, lower or mulSub.
	    */
	    boolean step() {
	    	visitCount++;
	    	if (count==maxCount) {
	    		return I<J ? stepIltJ() : (I==J ? stepIeqJ() : stepIgtJ());
	    	} else {
	    		Block IBuddy=getBlock(I,count),JBuddy;
	    		if (IBuddy.ready && (JBuddy=getBlock(count,J)).ready) {
			    mulsub(IBuddy, JBuddy);
			    count++;
			    return true;
	    		}
	    	}
	    	return false;
	    }
	    boolean stepIltJ(/*:I<J*/) { 
		if(! getBlock(I,I).ready) return false;
		for(;readyBelowIndex<bx && (getBlock(readyBelowIndex, J).count>=I); readyBelowIndex++) ;
		if(readyBelowIndex < bx) return false;
		backSolve(getBlock(I,I));
		return ready = true; 
	    }
	    boolean stepIgtJ(/*:I>J*/) { 
		if(LUCol>=0) {
		    Block diag = getBlock(J,J);
		    if(!diag.ready && !(diag.LUCol > LUCol)) return false;
		    lower(diag, LUCol);
		    if(LUCol==B-1) ready=true; 
		}
		++LUCol;
		if(LUCol <= B-1) computeMax(LUCol);
		return true;
	    }
	    boolean stepIeqJ(/*:I==J*/) { 
		if(LUCol==-1) { 
		    for(;readyBelowIndex<bx && (getBlock(readyBelowIndex, J).count>=J); readyBelowIndex++);
		    if(readyBelowIndex < bx) return false;
		}
		if(LUCol>=0) {
		    if(colMaxCount==0) colMaxCount = I+1;
		    Block block;
		    for(;colMaxCount<bx && ((block=getBlock(colMaxCount,J)).LUCol >=LUCol); colMaxCount++) {
			if(fabs(block.maxColV) > fabs(maxColV)) {
			    maxRow = block.maxRow;
			    maxColV = block.maxColV;
			}
		    }
		    if(colMaxCount < bx) return false;
		    final int/*(0,N)*/ row = I*B+LUCol;
		    pivots.set(row, maxRow);
		    if(row != maxRow) permute(row, maxRow);
		    LU(LUCol);
		    if(LUCol==B-1)  ready=true;
		}
		++LUCol;
		if(LUCol<=B-1)	{
		    computeMax(LUCol, LUCol);
		    colMaxCount=0;
		}
		return true;
	    }
	    void computeMax(int/*(0,B)*/  col) { computeMax(col, 0); }
	    void computeMax(int/*(0,B)*/ col, int/*(0,B)*/ startRow) { 
		int ord=ord(startRow,col); 
		maxColV = A[ord]; 
		maxRow = I*B+startRow;
		for(int i=startRow+1; i<B; i++) {
		    final double a = A[ord++]; 
		    if(fabs(a) > fabs(maxColV)) {
			maxRow = I*B+i; // write Row first to use write-ordering property of volatiles
			maxColV = a;
		    }
		}		
		assert in(maxRow, 0, N);
	    }
	    /**permute, for the columns in this block, 
	       row1 in this block with row2 (in potentially some other block)*/
	    void permute(int/*(I*B,(I+1)*B)*/ row1, int/*(:self!=row1)*/ row2) {  
		final Block b = getBlock(row2/B, J); //the other block
		int ord1=row1%B, ord2=row2%B;
		for(int j=0; j<B; j++){
		    final double v1 = A[ord1], v2 = b.A[ord2];
		    A[ord1]=v2; b.A[ord2]=v1;
		    ord1+=B; ord2+=B;
		}
	    }
	    void lower(final Block diag, final int/*(0,B)*/ col /*:I>J*/) { 
		if (C_MODE) {
		    blockLower(this.A, diag.A, col, B, diag.get(col,col));
		} else {
		    for(int i=0; i<B; i++) {
			double r=0.0; for(int k=0; k<col; k++) r += get(i,k)*diag.get(k,col);
			int ord = ord(i,col);
			A[ord] = (A[ord] -r)/diag.get(col,col);			
		    }
		}
	    }
	    void backSolve(final Block diag /*:I<J*/) { 
		for(int i=I*B; i<(I+1)*B; i++) { // permute first, if necessary
		    final int target = pivots.get(i);
		    if(target != i) permute(i, target);
		}
		if (C_MODE) 
		    blockBackSolve(this.A, diag.A, B);
		 else 
		    for (int i=0; i<B; ++i) { 
			for (int j=0; j<B; ++j) {
			    int iord = i,jord = B*j; 
			    double r=0.0; for (int k=0; k<i; ++k) {
				r += diag.A[iord]*A[jord]; 
				iord +=B; jord++;
			    }
			    A[ord(i,j)] -=r;
			}
		    }
	    } 
	    void mulsub(final Block left, final Block upper) { 
		if (C_MODE) 
		    blockMulSub(this.A, left.A, upper.A, B);
		 else 
		    for(int i=0; i<B; ++i) { 
			for(int j=0; j<B; ++j) {
			    int iord = ord(i,0),jord = ord(0,j);
			    double r=0; for(int k=0; k<B; ++k) { 
				r += left.A[iord]*upper.A[jord+k]; 
				iord +=B;
			    }
			    A[ord(i,j)] -=r; 
			}
		    }
	    }
	    void LU(final int col /*:I==J*/) { 
		for (int i = 0; i < B; ++i) { 
		    int iord=ord(i,0), jord=ord(0,col), m = min(i,col);
		    double r = 0.0; for(int k=0; k<m; ++k) {
			r += A[iord]*A[jord+k]; 
			iord +=B;
		    }
		    A[jord+i] -=r;
		    if(i>col) A[jord+i] /= A[jord+col]; 
		}				
	    } 
	    int ord(int/*(0,B)*/ i, int/*(0,B)*/ j) { return i+j*B; } 
	    double get(int i, int j) { return A[ord(i,j)];	}
	    void set(int i, int j, double v) { 	A[ord(i,j)] = v; }
	    void negAdd(int i, int j, double v) { A[ord(i,j)] -= v; }
	    void posAdd(int i, int j, double v) { A[ord(i,j)] += v; }
	}
    static extern void blockLower(double[.] me, double[.] diag, int col, int B, double diagColCol);
    static extern void blockBackSolve(double[.] me, double[.] diag, int B);
    static extern void blockMulSub(double[.] me, double[.] left, double[.] upper, int B);
}

