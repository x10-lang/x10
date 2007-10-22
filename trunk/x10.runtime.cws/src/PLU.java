/**
  (C) IBM Corporation 2007.

	LU decomposition (with priority) with partial pivoting.
  @author Sriram Krishnamoorthy, sriramkr@us.ibm.com
  @author vj vijay@saraswat.org
 */

import java.util.concurrent.atomic.*;
/**
 * Iterative, shared memory LU with pivoting.
 * @author Sriram Krishnamoorthy
 * @author vj
 *
 */
public class PLU {
	final TwoDBCA M;
	/**
	 * Row permutation for pivoting
	 */
	AtomicIntegerArray pivots; 

	final int nx,ny,px,py,B;
	/**
		Iterative version, with pivoting.
	 */
	public  PLU(int px, int py, int nx, int ny, int B) {
		this.nx=nx; this.ny=ny; this.px=px; this.py=py; this.B=B;
		M = new TwoDBCA(px,py,nx,ny,B);
		pivots = new AtomicIntegerArray(nx*px*B);
		for(int i=0; i<nx*px*B; i++) pivots.set(i,i);
	}

	static double format(double v, int precision){
		int scale=1;
		for(int i=0; i<precision; i++)
			scale *= 10;
		return ((int)(v*scale))*1.0/scale;
	}
	static int max(int a, int b) { return a > b ? a : b; }
	static double max(double a, double b) { return a > b ? a : b;}
	static double fabs(double v){ return  v > 0 ? v : -v; }
	static int min(int a, int b) { return a > b ? b : a; }
	static double flops(int n) { return ((4.0 *  n - 3.0) *  n - 1.0) * n / 6.0; }

	public static void main(String[] a) {
		if (a.length < 4) {
			System.out.println("Usage: PLU N b px py");
			return;
		}

		final int N = java.lang.Integer.parseInt(a[0]), 
		B= java.lang.Integer.parseInt(a[1]),
		px= java.lang.Integer.parseInt(a[2]), 
		py= java.lang.Integer.parseInt(a[3]);

		final int nx = N / (px*B), ny = N/(py*B);
		assert (N % (px*B) == 0 && N % (py*B) == 0);
		System.out.println("PLU " + N + " " + B + " " + px + " " + py + ":");
		PLU lu = new PLU(px,py,nx,ny,B);
		//lu.M.display("Before LU");

		TwoDBCA A = lu.M.copy();
		long s = - System.nanoTime();
		lu.M.plu();
		s += System.nanoTime();
		lu.M.applyLowerPivots();
		A.applyPivots();

		System.out.println("N="+N+" px="+px+" py="+py+" B="+B
				+" time="+(s)/1000000+"ms"
				+" Rate="+format(flops(N)/(s)*1000, 3)+" MFLOPS");
		
		System.out.print("Verifying... ");
		long v = - System.nanoTime();
		boolean correct = A.verify(lu.M);
		v += System.nanoTime();
		
		System.out.print("Pivots: ");
		for(int i=0; i<lu.pivots.length(); i++) {
			System.out.print(lu.pivots.get(i)+" ");
			if (i % 10==0) { 
				System.out.println();
				System.out.print(" " );
			}
		}
		System.out.println(" ");

		
		System.out.println("Verification " + ((v)/1000000) + " ms " + (correct?" ok":" fail"));
	}
	class TwoDBCA {
		/**
		 * A B*B array of doubles, whose top left coordinate is i,j).
		 * get/set operate on the local coordinate system, i.e.
		 * (i,j) is treated as (0,0).
		 *
		 */
		class Block {
			volatile double[] A;
			volatile boolean ready = false;
			// counts the number of phases left for this
			// block to finish its processing;
			private int maxCount;
			private volatile int count=0; //in PLU other threads read the count
			final int I,J, B;
			Block(int I, int J, int B) {
				assert B > 0 && in(I, 0,px*nx) && in(J, 0, py*ny);
				this.I=I; this.J=J;this.B=B; //this.MMM=TwoDBCA.this;
				A = new double[B*B];
				maxCount = min(I,J);
				readyBelowCount = I;
			}
			Block(final Block b) {
				this.I=b.I; this.J=b.J;this.B=b.B; //this.MMM=b.MMM;
				A = new double[B*B];
				maxCount = min(I,J);
				for(int i=0; i<B*B; i++) A[i] = b.A[i];
				readyBelowCount = I;
			}
			void display() {
				//System.out.println("I="+I+" J="+J);
				for(int i=0; i<B; i++) 
					for(int j=0; j<B; j++) 
						System.out.print(format(A[i*B+j],6) + " ");
			}
			void init() {
				for (int i=0; i < B*B; i++)
					A[i] = format(10 * Math.random(), 4);
				if (I==J) {
//					for (int i=0; i < B; i++) 
//					A[i*B+i] = format(20 * Math.random() + 10, 4);
				}
			}
			//#blocks below this one, that have all mulsubs done so that 
			//we can permute and do backSolve on this block
			private int readyBelowCount; 	
			boolean step() {
				if(ready) return false;
				if (count == maxCount) {
					if(I<J && TwoDBCA.this.get(I,I).ready) {
						if(readyBelowCount==0) readyBelowCount=I; 
						for(;readyBelowCount<px*nx && (TwoDBCA.this.get(readyBelowCount, J).count==I); readyBelowCount++)
							;
						if(readyBelowCount==px*nx) {
							bSolve(TwoDBCA.this.get(I,I), pivots);
							ready = true;
							return true;
						}
						return false;
					}
					else if (I >=J)
						return stepLU(pivots);
					else
						return false;
				}
				Block IBuddy = TwoDBCA.this.get(I, count), JBuddy = TwoDBCA.this.get(count,J);
				if (IBuddy.ready && JBuddy.ready) {
					mulsub(IBuddy, JBuddy);
					count++;
					return true;
				}
				return false;
			}

			//The column with the block in which LU is being done
			//start with an invalid value
			private volatile int LUCol=-1;
			private volatile double maxColV; //maximum value in Column LU_col
			private volatile int maxRow; //Row with that value			

			void computeMax(int col) { computeMax(col, 0); }

			void computeMax(int col, int startRow) {
					assert in(col, 0, B);
					assert in(startRow, 0, B);
					maxColV = get(startRow,col);
					maxRow = I*B+startRow;
					
					for(int i=startRow; i<B; i++) {
						if(fabs(get(i,col)) > fabs(maxColV)) {
							maxColV = get(i,col);
							maxRow = I*B+i;
						}
					}		
					assert in(maxRow, 0, N);
			}

			private int colMaxCount=0; //#maxes ready for this column
			//stepping through to perform panel factorization
			boolean stepLU(AtomicIntegerArray pivots) {
				assert I >= J;
					assert count == maxCount;
					assert ready == false;
					assert LUCol < B;

					if(LUCol==-1 && (I==J)) { 
						for(;readyBelowCount<px*nx && (TwoDBCA.this.get(readyBelowCount, J).count==J); readyBelowCount++);
						if(readyBelowCount < px*nx) return false;
						
					}

					if(I == J) {
						if(LUCol>=0) {
							if(colMaxCount==0) colMaxCount = I+1;
							for(;colMaxCount<px*nx && (TwoDBCA.this.get(colMaxCount,J).LUCol==LUCol); colMaxCount++) {
								if(fabs(TwoDBCA.this.get(colMaxCount, J).maxColV) > fabs(maxColV)) {
									maxColV = TwoDBCA.this.get(colMaxCount, J).maxColV;
									maxRow = TwoDBCA.this.get(colMaxCount, J).maxRow;
								}
							}
							if(colMaxCount < px*nx) return false;
							final int row = I*B+LUCol;
							assert in(row, 0, N);
							pivots.set(row, maxRow);
							if(row != maxRow) {
							//	System.out.println("StepLU: Invoking permute " + row + " --> " + maxRow);
								permute(row, maxRow);
							}
							LU(LUCol);
							if(LUCol==B-1)  ready=true;
						}
						LUCol = (LUCol==-1? 0 : LUCol+1);
						if(LUCol<=B-1)	{
							computeMax(LUCol, LUCol);
							colMaxCount=0;
						}
					}
					else {
						if(LUCol>=0) {
							Block diag = TwoDBCA.this.get(J,J);
							if(!(diag.LUCol > LUCol) && !diag.ready) 
								return false;
							lower(diag, LUCol);
							if(LUCol==B-1)  ready = true; 
						}
						if(LUCol+1 <= B-1) computeMax((LUCol==-1?0:LUCol+1));
						//A store barrier?
						LUCol = (LUCol==-1 ? 0 : LUCol+1);
					}

					return true;
			}

			void lower(Block diag, int col) {
				for(int i=0; i<B; i++) {
					double r = 0.0;
					for(int k=0; k<col; k++)
						r += get(i,k)*diag.get(k,col);
					negAdd(i,col,r);
					set(i,col,get(i,col)/diag.get(col,col));				
				}
			}

			void lower(Block diag) {
				for(int i=0; i<B; i++)
					for(int j=0; j<B; j++) {
						double r = 0.0;
						for(int k=0; k<j; k++)
							r += get(i,k)*diag.get(k,j);
						negAdd(i,j,r);
						set(i,j,get(i,j)/diag.get(j,j));
					}
			}

			//permute, for the columns in this block, 
			//row1 in this block with row2 (in potentially some other block)*/
			void permute(int row1, int row2) {
				assert row1 != row2; //why was this called then?
				assert row1>=I*B && row1<(I+1)*B; //should be a row in this block

				Block b = TwoDBCA.this.get(row2/B, J); //the other block

				for(int j=0; j<B; j++){
					double v1 = get(row1%B, j);
					double v2 = b.get(row2%B, j);
					set(row1%B, j, v2);
					b.set(row1%B, j, v1);
				}
			}
			void bSolve(Block diag, AtomicIntegerArray pivots) {
				for(int i=I*B; i<(I+1)*B; i++) 
					if(pivots.get(i) != i)
						permute(i, pivots.get(i));
				for (int i = 0; i < B; i++) 
					for (int j = 0; j < B; j++) {
						double r = 0.0;
						for (int k = 0; k < i; k++) 
							r += diag.get(i,k)*get(k, j);
						negAdd(i, j, r);
					}
			}
			void mulsub(Block left, Block upper) {
				for(int i=0; i<B; i++)
					for(int j=0; j<B; j++) {
						double r=0;
						for(int k=0; k<B; k++)
							r += left.get(i, k) * upper.get(k, j);
						negAdd(i,j,r);
					}
			}

			void LU(int col) {
				for (int i = 0; i < B; i++) {
					double r = 0.0;
					for(int k=0; k<min(i,col); k++)
						r += get(i,k) * get(k,col);
					negAdd(i,col, r);
					if(i>col) set(i,col, get(i,col)/get(col,col));
				}				
			}

			void LU() {
				for (int k = 0; k < B; k++)
					for (int i = k + 1; i < B; i++) {
						set(i,k, get(i,k)/get(k,k));
						double a = get(i,k);
						for(int j=k+1; j<B; j++)
							negAdd(i,j, a*get(k,j));
					}				
			}
			int ord(int i, int j) {
				assert j>=0 && j<B;
				assert i>=0 && i < B;
				return i*B+j;
			}
			double get(int i, int j) { return A[ord(i,j)];	}
			void set(int i, int j, double v) { 	A[ord(i,j)] = v; }
			void negAdd(int i, int j, double v) { A[ord(i,j)] -= v; }
			void posAdd(int i, int j, double v) { A[ord(i,j)] += v; }
		}


		class Worker implements Runnable {
			final int pi, pj;
			public Worker(int pi, int pj) { this.pi=pi; this.pj=pj; }
			public void run() {
				final Block lastBlock = getLocal(pi, pj, nx-1, ny-1);
				int starty=0, ct=0;

				while(!lastBlock.ready) {
					assert ny-1>=starty;

					if(getLocal(pi, pj, nx-1, starty).ready) ++starty;
					outer:	for(int j=starty; j<ny; j++) {
						for(int i=0; i<nx; i++) {
							ct++;
							if (ct % 10000000 == 0) {
								System.out.println( "Worker(" + pi + ","+pj+"): count=" + ct);
							}
							Block block = getLocal(pi, pj, i,j);
							if(block.step()) break outer;
						}
					}
					Thread.currentThread().yield();
				}
				System.out.println( "Worker(" + pi + ","+pj+"): count=" + ct);
			}
		} // Worker
		Block[][] A;
		final int px,py, nx,ny,B, N;
		void init() {}
		boolean in(int x, int low, int high) { return low <= x && x < high;}
		int pord(int i, int j) { assert in(i, 0, px) && in(j, 0, py); return i*py+j;	}
		int lord(int i, int j) { assert in(i, 0, nx) && in(j,0, ny); return i*ny+j; }
		Block get(int i, int j) { return A[pord(i%px, j%py)][lord(i/px,j/py)]; }
		Block getLocal(int pi, int pj, int i, int j) { 
			assert in(pi, 0, px) && in(pj, 0, py) && in(i, 0, nx) && in(j, 0, ny);
			return A[pord(pi,pj)][lord(i,j)]; 
		}
		void set(int i, int j, Block v) { A[pord(i % px, j%py)][lord(i/px,j/py)] = v; }
		TwoDBCA(int px, int py, int nx, int ny,int B) {
			this.px=px; this.py=py; this.nx=nx; this.ny=ny; this.B=B;
			assert px*nx==py*ny;
			N = px*nx*B;
			A = new Block[px*py][nx*ny];
			for(int pi=0; pi<px; ++pi) 
				for(int pj=0; pj<py; ++pj) 
					for(int i=0; i<nx; ++i) 
						for(int j=0; j<ny; ++j) {
							int q = pord(pi,pj), k = lord(i,j);
							A[q][k] = new Block(i*px+pi, j*py+pj, B);								
							A[q][k].init();
						}

		}
		TwoDBCA(final TwoDBCA arr) {
			this.px=arr.px; this.py=arr.py; this.nx=arr.nx; this.ny=arr.ny; this.B=arr.B;
			assert px*nx==py*ny;
			N = px*nx*B;
			A = new Block[px*py][nx*ny];
			for(int pi=0; pi<px; pi++) 
				for(int pj=0; pj<py; pj++) 
					for(int i=0; i<nx; i++) 
						for(int j=0; j<ny; j++) {
							A[pord(pi,pj)][lord(i,j)] = new Block(arr.getLocal(pi, pj, i, j));
							//A[pord(pi,pj)][lord(i,j)].MMM = this;
						}
		}

		public void plu() {
			Thread[] workers = new Thread[px*py];
			for (int i=0; i < px; i++)
				for(int j=0; j<py; j++)
					workers[i*py+j] = new Thread(new Worker(i, j));

			for (int i=0; i < px*py; i++) workers[i].start();
			for (int i=0; i < px*py; i++) // run, run, score many runs.
				try { 
					workers[i].join();
				} catch (InterruptedException z) {
					System.out.println(z);
				}
		}
		// invoked by a single thread, should be parallelized
		void applyLowerPivots() {
			for(int i=0; i<px*nx; i++) 
				for(int j=0; j<i;  j++) 
					for(int r=i*B; r<(i+1)*B; r++) 
						if(r != pivots.get(r))
							get(i,j).permute(r, pivots.get(r));						
		}
		
		// invoked by a single thread, should be parallelized
		void applyPivots() {
			for(int i=0; i<px*nx; i++) 
				for(int j=0; j<py*ny;  j++) 
					for(int r=i*B; r<(i+1)*B; r++) {
						assert pivots.get(r)>=r;
						assert pivots.get(r)<px*nx*B;
						if(r != pivots.get(r))
							get(i,j).permute(r, pivots.get(r));		
					}						
		}


		TwoDBCA copy() { return new TwoDBCA(this); }

		void display(String msg) {
			System.out.println(msg);
			System.out.println("px="+px+" py="+py+" nx="+nx+" ny="+ny+" B="+B);
			for(int I=0; I<px*nx; I++) 
				for(int J=0; J<py*ny; J++) 
					get(I,J).display();
			System.out.println(" ");
		}
		
		// invoked by a single thread, should be parallelized
		boolean verify(TwoDBCA M) {
			/* Initialize test. */
			double max_diff = 0.0;

			/* Find maximum difference between any element of LU and M. */
			for (int i = 0; i < nx * px * B; i++)
				for (int j = 0; j < ny * py * B; j++) {
					final int I = i / B, J = j / B;
					double v = 0.0;
					int k;
					for (k = 0; k < i && k <= j; k++) {
						final int K = k / B;
						v += M.get(I,K).get(i%B, k%B) * M.get(K,J).get(k%B, j%B);
					}
					if (k == i && k <= j) {
						final int K = k / B;
						v += M.get(K,J).get(k%B, j%B);
					}
					double diff = fabs(get(I,J).get(i%B, j%B) - v);
					max_diff = max(diff, max_diff);
				}

			/* Check maximum difference against threshold. */
			return (max_diff <= 0.01);
		}
		
	} // TwoDBlockCyclicArray
	
}
