/**
  (C) IBM Corporation 2007.

  Iterative version, without pivoting.

  @author Sriram Krishnamoorthy, sriramkr@watson.ibm.com
  @author vj vijay@saraswat.org
 */
import java.lang.Integer;
public class LU {
    final TwoDBlockCyclicArray M;
    final int nx,ny,px,py,B;
    public  LU(int px, int py, int nx, int ny, int B) {
	this.nx=nx; this.ny=ny; this.px=px; this.py=py; this.B=B;
	M = new TwoDBlockCyclicArray(px,py,nx,ny,B);
    }
    public void lu() {
	finish foreach (point [pi,pj]: [0:px-1,0:py-1]) {
	    Block lastBlock = M.A[pi, pj][nx-1, ny-1];
	    int iStart=0;
	    while(!lastBlock.ready) {
		if(iStart+1<nx && iStart+1<ny && M.A[pi, pj][iStart+1, iStart+1].ready) iStart ++;
		outer: for (int i=iStart; i < max(nx,ny); i++) 
		    for (int k=0; k <= i; k++) {
			if ( i < nx && k < ny) {
			    Block block = M.A[pi, pj][i,k];
			    if(block.step()) break outer;
			}
			if ( k < nx && i < ny) {
			    Block block = M.A[pi, pj][k,i];
			    if(block.step()) break outer;
			}
		    }
	    }
	}
    }
    static double format(double v, int precision){
	int scale=1;
	for(int i=0; i<precision; i++) scale *= 10;
	return ((int)(v*scale))*1.0/scale;
    }
    static int max(int a, int b) { return a > b ? a : b; }
    static double max(double a, double b) { return a > b ? a : b; }
    static double fabs(double v){ return  v > 0 ? v : -v; }
    static int min(int a, int b) { return a > b ? b : a; }
    static double flops(int n) { return ((4.0 *  n - 3.0) *  n - 1.0) * n / 6.0; }
    boolean verify(TwoDBlockCyclicArray Input) {
	int k;
	/* Initialize test. */
	double max_diff = 0.0;
	
	/* Find maximum difference between any element of LU and M. */
	for (int i = 0; i < nx * px * B; i++)
	    for (int j = 0; j < ny * py * B; j++) {
		final int I = i / B, J = j / B;
		double v = 0.0;
		for (k = 0; k < i && k <= j; k++) {
		    final int K = k / B;
		    v += M.get(I,K).A[i%B, k%B] * M.get(K,J).A[k%B, j%B];
		}
		if (k == i && k <= j) {
		    final int K = k / B;
		    v += M.get(K,J).A[k%B, j%B];
		}
		double diff = fabs(Input.get(I,J).A[i%B, j%B] - v);
		max_diff = max(diff, max_diff);
	    }
	/* Check maximum difference against threshold. */
	return max_diff > 0.01;
    }
    public static void main(String[] a) {
	if (a.length < 4) {
	    System.out.println("Usage: LU N b px py");
	    return;
	}
	final int N = Integer.parseInt(a[0]), B= Integer.parseInt(a[1]), 
	    px= java.lang.Integer.parseInt(a[2]), py= java.lang.Integer.parseInt(a[3]);
	int nx = N / (px*B), ny = N/(py*B);
	assert (N % (px*B) == 0 && N % (py*B) == 0);
	LU lu = new LU(px,py,nx,ny,B);
	TwoDBlockCyclicArray A = lu.M.copy();
	long s = System.nanoTime();
	lu.lu();
	long t = System.nanoTime();
	boolean correct = lu.verify(A);
	System.out.println("N="+N+" px="+px+" py="+py+" B="+B
			   +(correct?" ok":" fail")+" time="+(t-s)/1000000+"ms"
			   +" Rate="+format(flops(N)/(t-s)*1000, 3)+"MFLOPS");
    }
    /**
     * A B*B array of doubles, whose top left coordinate is i,j).
     * get/set operate on the local coordinate system, i.e.
     * (i,j) is treated as (0,0).
     *
     */
    static class Block {
	/*volatile*/ double[.] A;
	TwoDBlockCyclicArray M; //Array of which this block is part
	
	/*volatile*/ boolean ready = false;
	// counts the number of phases left for this
	// block to finish its processing;
	private int maxCount, count=0;
	final int I,J, B;
	final region(:rank==2) R;
	Block(int I, int J, int B, TwoDBlockCyclicArray M) {
	    this.I=I; this.J=J;this.B=B; this.M=M;
	    R = [0:B-1,0:B-1];
	    A = new double[R];
	    maxCount = min(I,J);
	}
	Block(final Block b) {
	    this.I=b.I; this.J=b.J;this.B=b.B; this.M=b.M; this.R=b.R;
	    A = new double[R] (point [i,j]) { return b.A[i,j];};
	    maxCount = min(I,J);
	}
	Block copy() { return new Block(this);}
	void display() {
	    for (point [i,j] : A) System.out.print(format(A[i,j],6) + " "); 
	}
	void init() {
	    for (point [i,j] : A) A[i,j] = format(10 * Math.random(), 4);
	    if (I==J) for (point [i] : [0:B-1])  A[i,i] = format(20 * Math.random() + 10, 4);
	}
	boolean step() {
	    if(ready) return false;
	    if (count == maxCount) {
		if (I==J) { LU(); }
		else if (I < J) { if(M.get(I, I).ready) { backSolve(M.get(I,I));  ready=true;}}
		else if(M.get(J, J).ready) { lower(M.get(J,J));  ready=true; }
		return ready;
	    }
	    Block IBuddy = M.get(I, count), JBuddy = M.get(count,J);
	    if (IBuddy.ready && JBuddy.ready) {
		mulsub(IBuddy, JBuddy);
		count++;
		return true;
	    }
	    return false;
	}
	
	void lower(Block diag) {
	    for(point [i,j] : A) {
		double r = 0.0; for(point [k] : [0:j-1]) r += A[i,k]*diag.A[k,j];
	        A[i,j] -= r;
		A[i,j] = A[i,j]/diag.A[j,j];
	    }
	}

	void backSolve(Block diag) {
	    for (point [i,j] : A) {
		double r = 0.0; for (point [k] : [0:i-1]) r += diag.A[i, k] * A[k, j];
		A[i,j]-= r;
	    }
	}
	void mulsub(Block left, Block upper) {
	    for (point [i,j] : A) {
		double r=0; for(point [k] : [0:B-1]) r += left.A[i, k] * upper.A[k, j];
		A[i,j] -=r;
	    }
	}
	void LU() {
	    for (int k = 0; k < B; k++)
		for (int i = k + 1; i < B; i++) {
		    A[i,k] /= A[k,k];
		    double a = A[i,k];
		    for(int j=k+1; j<B; j++) A[i,j] -= a*A[k,j];
		}                             
	}
    }
    static class TwoDBlockCyclicArray {
	Block[.][.] A;
	final int px,py, nx,ny,B;
	final int N;
	TwoDBlockCyclicArray(int px, int py, int nx, int ny,int B) {
	    this.px=px; this.py=py; this.nx=nx; this.ny=ny; this.B=B;
	    assert px*nx==py*ny;
	    N = px*nx*B;
	    A = new Block[.][0:px-1,0:py-1] (point [pi,pj]) {
		return new Block[0:nx-1,0:ny-1] (point [i,j]) {
		    Block b = new Block(i*px+pi, j*py+pj,B, this);
		    b.init();
		    return b;		    
		};
	    };
	}
	TwoDBlockCyclicArray(final TwoDBlockCyclicArray arr) {
	    this.px=arr.px; this.py=arr.py; this.nx=arr.nx; this.ny=arr.ny; this.B=arr.B; this.N=arr.N;
	    A = new Block[.][0:px-1,0:py-1] (point [pi,pj]) {
		return new Block[0:nx-1,0:ny-1] (point [i,j]) {
		    return arr.A[pi,pj][i,j].copy();
		};
	    };
	}
	Block get(int i, int j) { return A[i % px, j%py][i/px,j/py];}
	void set(int i, int j, Block v) { A[i % px, j%py][i/px,j/py] = v;}
	TwoDBlockCyclicArray copy() { return new TwoDBlockCyclicArray(this); }
	void display(String msg) {
	    System.out.println(msg);
	    System.out.println("px="+px+" py="+py+" nx="+nx+" ny="+ny+" B="+B);
	    
	    for(int I=0; I<px*nx; I++) {
		for(int J=0; J<py*ny; J++) {
		    get(I,J).display();
		}
	    }
	    System.out.println(" ");
	}
    }
}
