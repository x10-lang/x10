/**
  (C) IBM Corporation 2007.

  @author Sriram Krishnamoorthy, sriramkr@watson.ibm.com
  @author vj vijay@saraswat.org
 */

	public class LU {
	
		final TwoDBlockCyclicArray M;
		
		final int nx,ny,px,py,B;
		/**
		Iterative version, without pivoting.
		*/
		public  LU(int px, int py, int nx, int ny, int B) {
			this.nx=nx; this.ny=ny; this.px=px; this.py=py; this.B=B;
			M = new TwoDBlockCyclicArray(px,py,nx,ny,B);
		}
		public void lu() {
			Thread[] workers = new Thread[px*py];
			for (int i=0; i < px; i++)
				for(int j=0; j<py; j++)
				workers[i*py+j] = new Thread(new Worker(i, j));
			
			for (int i=0; i < px*py; i++) workers[i].start();
			// run, run, score many runs.
			for (int i=0; i < px*py; i++) 
				try { 
					workers[i].join();
				} catch (InterruptedException z) {
					System.out.println(z);
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
		class Worker implements Runnable {
			final int pi, pj;
			public Worker(int pi, int pj) {
				this.pi=pi;
				this.pj=pj;
			}
			public void run() {
				Block lastBlock = M.getLocal(pi, pj, nx-1, ny-1);
				int iStart=0;
				while(!lastBlock.ready) {
					if(iStart+1<nx && iStart+1<ny && M.getLocal(pi, pj, iStart+1, iStart+1).ready) {
						iStart += 1;
					}
outer:				for (int i=iStart; i < max(nx,ny); i++) 
						for (int k=0; k <= i; k++) {
							if ( i < nx && k < ny) {
								Block block = M.getLocal(pi, pj, i,k);
								if(block.step()) break outer;
							}
							if ( k < nx && i < ny) {
								Block block = M.getLocal(pi, pj, k,i);
								if(block.step()) break outer;
							}
						}
					}
				}
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
			LU lu = new LU(px,py,nx,ny,B);
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
			lu.lu();
			long t = System.nanoTime();

//			A.display("Original array");
//			seqlu.M.display("Seq array");
//			lu.M.display("After LU");
//			seqlu.lu();
//			seqlu.M.display("Seq after LU");
			
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
			volatile double[] A;
			TwoDBlockCyclicArray M; //Array of which this block is part
			
			volatile boolean ready = false;
			// counts the number of phases left for this
			// block to finish its processing;
			private int maxCount, count=0;
			final int I,J, B;
			Block(int I, int J, int B, TwoDBlockCyclicArray M) {
				this.I=I; this.J=J;this.B=B; this.M=M;
				A = new double[B*B];
				maxCount = min(I,J);
			}
			Block(final Block b) {
				this.I=b.I; this.J=b.J;this.B=b.B; this.M=b.M;
				A = new double[B*B];
				maxCount = min(I,J);
				for(int i=0; i<B*B; i++)
					A[i] = b.A[i];
			}
			Block copy() {
				return new Block(this);
			}
			void display() {
				//System.out.println("I="+I+" J="+J);
				for(int i=0; i<B; i++) {
					for(int j=0; j<B; j++) {
						System.out.print(format(A[i*B+j],6) + " ");
					}
					//System.out.println("");
				}
			}
			void init() {
				for (int i=0; i < B*B; i++)
					A[i] = format(10 * Math.random(), 4);
				if (I==J) {
					for (int i=0; i < B; i++) 
						A[i*B+i] = format(20 * Math.random() + 10, 4);
				}
			}
			boolean step() {
				if(ready) return false;
				if (count == maxCount) {
					if (I==J) { LU(); ready=true; }
					else if (I < J && M.get(I, I).ready) { backSolve(M.get(I,I)); ready=true; }
					else if(M.get(J, J).ready) { lower(M.get(J,J)); ready=true; }
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
				for(int i=0; i<B; i++)
					for(int j=0; j<B; j++) {
						double r = 0.0;
						for(int k=0; k<j; k++)
							r += get(i,k)*diag.get(k,j);
						negAdd(i,j,r);
						set(i,j,get(i,j)/diag.get(j,j));
					}
			}
			void backSolve(Block diag) {
				  for (int i = 0; i < B; i++) {
				    for (int j = 0; j < B; j++) {
				      double r = 0.0;
				      for (int k = 0; k < i; k++) {
				    	  r += diag.get(i, k) * get(k, j);
				      }
				      negAdd(i, j, r);
				    }
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
			}
		}
		static class TwoDBlockCyclicArray {
			Block[][] A;
			final int px,py, nx,ny,B;
			final int N;
			TwoDBlockCyclicArray(int px, int py, int nx, int ny,int B) {
				this.px=px; this.py=py; this.nx=nx; this.ny=ny; this.B=B;
				assert px*nx==py*ny;
				N = px*nx*B;
				A = new Block[px*py][nx*ny];
				for(int pi=0; pi<px; pi++) {
					for(int pj=0; pj<py; pj++) {
						for(int i=0; i<nx; i++) {
							for(int j=0; j<ny; j++) {
								A[pord(pi,pj)][lord(i,j)] = new Block(i*px+pi, j*py+pj, B, this);								
								A[pord(pi,pj)][lord(i,j)].init();
							}
						}
					}
				}
			}
			TwoDBlockCyclicArray(final TwoDBlockCyclicArray arr) {
				this.px=arr.px; this.py=arr.py; this.nx=arr.nx; this.ny=arr.ny; this.B=arr.B;
				assert px*nx==py*ny;
				N = px*nx*B;
				A = new Block[px*py][nx*ny];
				for(int pi=0; pi<px; pi++) {
					for(int pj=0; pj<py; pj++) {
						for(int i=0; i<nx; i++) {
							for(int j=0; j<ny; j++) {
								A[pord(pi,pj)][lord(i,j)] = arr.getLocal(pi, pj, i, j).copy();
							}
						}
					}
				}
			}

			
			void init() {}
			int pord(int i, int j) {
				return i*py+j;
			}
			int lord(int i, int j) {
				return i*ny+j;
			}
			Block get(int i, int j) {
				return A[pord(i % px, j%py)][lord(i/px,j/py)];
			}

			Block getLocal(int pi, int pj, int i, int j) {
				return A[pord(pi,pj)][lord(i,j)];
			}
			void set(int i, int j, Block v) {
				A[pord(i % px, j%py)][lord(i/px,j/py)] = v;
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
