/**
  (C) IBM Corporation 2007.

	LU decomposition (with priority) with partial pivoting.
  @author Sriram Krishnamoorthy, sriramkr@us.ibm.com
  @author vj vijay@saraswat.org
 */

import java.util.concurrent.atomic.*;

	public class PLU {
	
		final TwoDBlockCyclicArray M;
		AtomicIntegerArray pivots; //row permutation for pivoting
		
		final int nx,ny,px,py,B;
		/**
		Iterative version, with pivoting.
		*/
		public  PLU(int px, int py, int nx, int ny, int B) {
			this.nx=nx; this.ny=ny; this.px=px; this.py=py; this.B=B;
			M = new TwoDBlockCyclicArray(px,py,nx,ny,B);
			pivots = new AtomicIntegerArray(nx*px*B);
			for(int i=0; i<nx*px*B; i++) pivots.set(i,i);
		}
		public void plu() {
			finish foreach (point [pi,pj]: [0:px-1,0:py-1]) {
				int starty = 0;
			    int readyCount=0;

			    while(starty<ny) {
			      assert ny-1>=starty;
			      readyCount=0;

				  for(int j=starty; j<min(starty+3,ny); j++) {
					for(int i=0; i<nx; i++) {
					  Block *block = M->getLocal(pi, pj, i,j);
					  bool rval = block->step(pivots, prof);
					  if(j==starty && block->ready) readyCount += 1;
					}
				  }
				  if(readyCount == nx) starty+=1;
			    }
			}
		}
		static double format(double v, int precision){
			int scale=1;
			for(int i=0; i<precision; i++)
				scale *= 10;
			return ((int)(v*scale))*1.0/scale;
		}
		static int max(int a, int b) {
			return a > b ? a : b;
		}
		static double max(double a, double b) {
			return a > b ? a : b;
		}
		static double fabs(double v){
			return  v > 0 ? v : -v;
		}
		static int min(int a, int b) {
			return a > b ? b : a;
		}
		static double flops(int n) {
		     return ((4.0 *  n - 3.0) *  n - 1.0) * n / 6.0;
		}
		boolean verify(TwoDBlockCyclicArray Input) {
			int k;
			/* Initialize test. */
		     double max_diff = 0.0;

		     /* Find maximum difference between any element of LU and M. */
		     for (int i = 0; i < nx * px * B; i++)
		          for (int j = 0; j < ny * py * B; j++) {
		               final int I = i / B;
		               final int J = j / B;
		               double v = 0.0;
		               for (k = 0; k < i && k <= j; k++) {
		                    final int K = k / B;
		                    v += M.get(I,K).get(i%B, k%B) * M.get(K,J).get(k%B, j%B);
		               }
		               if (k == i && k <= j) {
		                    final int K = k / B;
		                    v += M.get(K,J).get(k%B, j%B);
		               }
		               double diff = fabs(Input.get(I,J).get(i%B, j%B) - v);
		               max_diff = max(diff, max_diff);
		          }

		     /* Check maximum difference against threshold. */
		     if (max_diff > 0.01)
		          return false;
		     else
		          return true;
		}
		
		public static void main(String[] a) {
			if (a.length < 4) {
				System.out.println("Usage: LU N b px py");
				return;
			}
			
			final int N = java.lang.Integer.parseInt(a[0]);
			final int B= java.lang.Integer.parseInt(a[1]);
			final int px= java.lang.Integer.parseInt(a[2]);
			final int py= java.lang.Integer.parseInt(a[3]);
			int nx = N / (px*B), ny = N/(py*B);
			assert (N % (px*B) == 0 && N % (py*B) == 0);
			PLU lu = new PLU(px,py,nx,ny,B);
			//lu.M.display("Before LU");

			TwoDBlockCyclicArray A = lu.M.copy();

//			LU seqlu = new LU(1,1,1,1,N);
//			for(int i=0; i<N; i++) {
//				for(int j=0; j<N; j++) {
//					int I = i/B, J = j/B;
//					seqlu.M.get(0,0).set(i,j,lu.M.get(I,J).get(i%B,j%B));
//				}
//			}
			
			long s = System.nanoTime();
			lu.plu();
			long t = System.nanoTime();

//			A.display("Original array");
//			seqlu.M.display("Seq array");
//			lu.M.display("After LU");
//			seqlu.lu();
//			seqlu.M.display("Seq after LU");

			lu.M.applyLowerPivots(lu.pivots);
//			System.out.println("After back-propagating pivots");
//			lu.M.display("After pivoting L as well");
//			A.display("Original array (again)");
			A.applyPivots(lu.pivots);
//			System.out.println("Done applying pivots to copy");
//			A.display("Original array after pivoting");
			
			boolean correct = lu.verify(A);

			System.out.print("Pivots: ");
			for(int i=0; i<lu.pivots.length(); i++) {
				System.out.print(lu.pivots.get(i)+" ");
			}
			System.out.println(" ");
			
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
			private int maxCount;
			private /*volatile*/ int count=0; //in PLU other threads read the count
			final int I,J, B;
			final region(:rank==2) R;
			Block(int I, int J, int B, TwoDBlockCyclicArray M) {
				this.I=I; this.J=J;this.B=B; this.M=M;
				R = [0:B-1,0:B-1];
				A = new double[R];
				maxCount = min(I,J);
				readyBelowCount = I;
			}
			Block(final Block b) {
				this.I=b.I; this.J=b.J;this.B=b.B; this.M=b.M, this.R=b.R;
			    A = new double[R] (point [i,j]) { return b.A[i,j];};
				maxCount = min(I,J);
				readyBelowCount = I;
			}
			Block copy() {
				return new Block(this);
			}
			void display() {
			    for (point [i,j] : A) System.out.print(format(A[i,j],6) + " "); 
			}
			void init() {
			    for (point [i,j] : A) A[i,j] = format(10 * Math.random(), 4);
	    		//if (I==J) for (point [i] : [0:B-1])  A[i,i] = format(20 * Math.random() + 10, 4);
			}
			//#blocks below this one, that have all mulsubs done so that 
			//we can permute and do backSolve on this block
			private int readyBelowCount; 	
			boolean step(AtomicIntegerArray pivots) {
				if(ready) return false;
				if (count == maxCount) {
					if(I<J && M.get(I,I).ready) {
						if(readyBelowCount==0) readyBelowCount=I;
						for(;readyBelowCount<M.px*M.nx && 
							(M.get(readyBelowCount, J).count==I);
							readyBelowCount++);
						if(readyBelowCount==M.px*M.nx) {
							bSolve(M.get(I,I), pivots);
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
				Block IBuddy = M.get(I, count), JBuddy = M.get(count,J);
				if (IBuddy.ready && JBuddy.ready) {
					mulsub(IBuddy, JBuddy);
					count++;
					return true;
				}
				return false;
			}

			//The column with the block in which LU is being done
			//start with an invalid value
			private /*volatile*/ int LU_col=-1;

			private /*volatile*/ double maxColV; //maximum value in Column LU_col
			private /*volatile*/ int maxRow; //Row with that value			

			void computeMax(int col) {
				computeMax(col, 0);
			}

			void computeMax(int col, int start_row) {
				assert B > 0;
				assert col>=0 && col<B;
				assert start_row>=0 && start_row<B;
				maxColV = get(start_row,col);
				maxRow = I*B+start_row;
				for(int i=start_row; i<B; i++) {
					if(fabs(get(i,col)) > fabs(maxColV)) {
						maxColV = get(i,col);
						maxRow = I*B+i;
					}
				}				
			}
			
			private int colMaxCount=0; //#maxes ready for this column
			//stepping through to perform panel factorization
			boolean stepLU(AtomicIntegerArray pivots) {
				assert I >= J;
				assert count == maxCount;
				assert ready == false;
				assert LU_col < B;
				
				if(LU_col==-1 && (I==J)) { 
					for(;readyBelowCount<M.px*M.nx && 
					(M.get(readyBelowCount, J).count==J);
					readyBelowCount++);
					
					if(readyBelowCount < M.px*M.nx) {
						return false;
					}
				}

				if(I == J) {
					if(LU_col>=0) {
						if(colMaxCount==0) colMaxCount = I+1;
						for(;colMaxCount<M.px*M.nx &&
							(M.get(colMaxCount,J).LU_col==LU_col);
							colMaxCount++) {
							if(fabs(M.get(colMaxCount, J).maxColV) > fabs(maxColV)) {
								maxColV = M.get(colMaxCount, J).maxColV;
								maxRow = M.get(colMaxCount, J).maxRow;
							}
						}
						if(colMaxCount < M.px*M.nx)
								return false;
						pivots.set(I*B+LU_col, maxRow);
						if(I*B+LU_col != pivots.get(I*B+LU_col))
							permute(I*B+LU_col, pivots.get(I*B+LU_col));
						LU(LU_col);
						if(LU_col==B-1)  ready=true;
					}
					LU_col = (LU_col==-1? 0 : LU_col+1);
					if(LU_col<=B-1)	{
						computeMax(LU_col, LU_col);
						colMaxCount=0;
					}
				}
				else {
					if(LU_col>=0) {
						Block diag = M.get(J,J);
						if(!(diag.LU_col > LU_col) && !diag.ready) 
							return false;
						lower(diag, LU_col);
						if(LU_col==B-1)  ready = true; 
					}
					if(LU_col+1 <= B-1) computeMax((LU_col==-1?0:LU_col+1));
					//A store barrier?
					LU_col = (LU_col==-1 ? 0 : LU_col+1);
				}

				return true;
			}

			void lower(Block diag, int col) {
				for(int i=0; i<B; i++) {
					double r = 0.0;
					for(int k=0; k<col; k++)
						r += A[i,k] * diag.A[k,col]; 
					A[i,col] -= r;
					A[i,col] /= diag.A[col,col];
				}
			}
			
			void lower(Block diag) {
				for(int i=0; i<B; i++)
					for(int j=0; j<B; j++) {
						double r = 0.0;
						for(int k=0; k<j; k++)
							r += A[i,k]*diag.A[k,j];
						A[i,j] -= r;
						A[i,j] /= diag.A[j,j];
					}
			}

			//permute, for the columns in this block, 
			//row1 in this block with row2 (in potentially some other block)*/
			void permute(int row1, int row2) {
				assert row1 != row2; //why was this called then?
				assert row1>=I*B && row1<(I+1)*B; //should be a row in this block
				
				Block b = M.get(row2/B, J); //the other block
				
				for(int j=0; j<B; j++){
					double v1 = A[row1%B, j];
					double v2 = b.A[row2%B, j];
					A[row1%B, j] =  v2;
					b.A[row1%B, j] = v1;
				}
			}
			void bSolve(Block diag, AtomicIntegerArray pivots) {
				for(int i=I*B; i<(I+1)*B; i++) {
					if(pivots.get(i) != i)
						permute(i, pivots.get(i));
				}
				for (int i = 0; i < B; i++) {
				    for (int j = 0; j < B; j++) {
				      double r = 0.0;
				      for (int k = 0; k < i; k++) {
				    	  r += diag.A[i, k] * A[k, j];
				      }
				      A[i, j] -= r;
				    }
				  }
			}
			void mulsub(Block left, Block upper) {
				for(int i=0; i<B; i++)
					for(int j=0; j<B; j++) {
						double r=0;
						for(int k=0; k<B; k++)
							r += left.A[i, k] * upper.A[k, j];
						A[i,j] -= r;
					}
			}

			void LU(int col) {
				for (int i = 0; i < B; i++) {
					double r = 0.0;
					for(int k=0; k<min(i,col); k++)
						r += A[i,k] * A[k,col];
					A[i,col] -=  r;
					if(i>col) A[i,col] /= A[col,col];
				}				
			}
			
			void LU() {
			     for (int k = 0; k < B; k++)
			          for (int i = k + 1; i < B; i++) {
			        	 A[i,k] /= A[k,k];
			        	  double a = A[i,k];
			        	  for(int j=k+1; j<B; j++)
			        		  A[i,j] -= a*A[k,j];
			          }				
			}
/*			int ord(int i, int j) {
				assert j>=0 && j<B;
				assert i>=0 && i < B;
				return i*B+j;
			}
			double get(int i, int j) {
				return A[ord(i,j)];
			}
			void set(int i, int j, double v) {
				A[ord(i,j)] = v;
			}
			void negAdd(int i, int j, double v) {
				A[ord(i,j)] -= v;
			}
			void posAdd(int i, int j, double v) {
				A[ord(i,j)] += v;
			}*/
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
				}.M = this;
			    };
			}

			void applyLowerPivots(AtomicIntegerArray pivots) {
				for(int i=0; i<px*nx; i++) {
					for(int j=0; j<i;  j++) {
						for(int r=i*B; r<(i+1)*B; r++) {
							if(r != pivots.get(r))
								get(i,j).permute(r, pivots.get(r));						
						}
					}
				}
			}

			void applyPivots(AtomicIntegerArray pivots) {
				for(int i=0; i<px*nx; i++) {
					for(int j=0; j<py*ny;  j++) {
						for(int r=i*B; r<(i+1)*B; r++) {
							assert pivots.get(r)>=r;
							assert pivots.get(r)<px*nx*B;
							if(r != pivots.get(r))
								get(i,j).permute(r, pivots.get(r));						}
					}
				}				
			}
			
			void init() {}
/*			int pord(int i, int j) {				return i*py+j;			}
			int lord(int i, int j) {				return i*ny+j;			}
			Block get(int i, int j) {
							return A[pord(i % px, j%py)][lord(i/px,j/py)];	
			}
			void set(int i, int j, Block v) {
				A[pord(i % px, j%py)][lord(i/px,j/py)] = v;
			}
			Block getLocal(int pi, int pj, int i, int j) {
				return A[pord(pi,pj)][lord(i,j)];
			}*/

			Block get(int i, int j) { return A[i % px, j%py][i/px,j/py];}
			void set(int i, int j, Block v) { A[i % px, j%py][i/px,j/py] = v;}
			Block getLocal(int pi, int pj, int i, int j) {
				return A[pi,pj][i,j];
			}
			

			TwoDBlockCyclicArray copy() {
				return new TwoDBlockCyclicArray(this);
			}
			
			void display(String msg) {
				System.out.println(msg);
				System.out.println("px="+px+" py="+py+" nx="+nx+" ny="+ny+" B="+B);

				for(int I=0; I<px*nx; I++) {
					for(int J=0; J<py*ny; J++) {
						get(I,J).display();
					}
					//System.out.println(" ");
				}
				System.out.println(" ");
			}
		}
}
