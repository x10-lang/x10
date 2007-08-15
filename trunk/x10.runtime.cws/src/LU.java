


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
		public void LU() {
			Thread[] workers = new Thread[px*py];
			for (int i=0; i < px*py; i++) 
				workers[i] = new Thread(new Worker(i));
			
			for (int i=0; i < px*py; i++) workers[i].start();
			// run, run, score many runs.
			for (int i=0; i < px*py; i++) 
				try { 
					workers[i].join();
				} catch (InterruptedException z) {
					System.out.println(z);
				}
			
		}
		static int max(int a, int b) {
			return a > b ? a : b;
		}
		static int min(int a, int b) {
			return a > b ? b : a;
		}
		class Worker implements Runnable {
			final int i;
			public Worker(int i) {
				this.i=i;
			}
			public void run() {
				Block lastBlock = M.getLocal(px, py, nx-1, ny-1);
				int iStart=0;
				while(!lastBlock.ready) {
					if(M.getLocal(px, py, iStart, iStart).ready) {
						iStart += 1;
					}
outer:				for (int i=iStart; i < max(nx,ny); i++) 
						for (int k=0; k <= i; k++) {
							if ( k < ny) {
								Block block = M.getLocal(px, py, i,k);
								if (! block.ready)
									if(block.step()) break outer;
							}
							if ( k < nx) {
								Block block = M.getLocal(px, py, k,i);
								if (! block.ready)
									if(block.step()) break outer;
							}
						}
					}
				}
			}
		
		
		
		
		public static void main(String[] a) {
			if (a.length < 3) {
				System.out.println("Usage: LU n b px py");
				return;
			}
			final int N = java.lang.Integer.parseInt(a[0]);
			final int B= java.lang.Integer.parseInt(a[1]);
			final int px= java.lang.Integer.parseInt(a[2]);
			final int py= java.lang.Integer.parseInt(a[3]);
			int nx = N / px, ny = N/py;
			assert (N % px == 0 && N % py == 0 && N % B == 0);
			LU lu = new LU(px,py,nx,ny,B);
			lu.LU();
			
			// need verification *** TO BE DONE.
			
		}
		/**
		 * A B*B array of doubles, whose top left coordinate is i,j).
		 * get/set operate on the local coordinate system, i.e.
		 * (i,j) is treated as (0,0).
		 * @author VijaySaraswat
		 *
		 */
		class Block {
			double[] A;
			
			volatile boolean ready;
			// counts the number of phases left for this
			// block to finish its processing;
			private int maxCount, count=0;
			final int I,J, B;
			Block(int I, int J, int B) {
				this.I=I; this.J=J;this.B=B;
				A = new double[B*B];
				maxCount = min(I,J);
			}
			void init() {
				for (int i=0; i < B*B; i++)
					A[i] = 10 * Math.random();
				if (I==J) {
					for (int i=0; i < B; i++) 
						A[i*B+i] = 20 * Math.random() + 10;
				}
			}
			boolean step() {
				if (count == maxCount) {
					if (I==J) LU();
					else if (I < J) backSolve(M.get(I,I));
					else lower(M.get(J,J));
					ready = true;
					return true;
				}
				Block IBuddy = M.get(I, count), JBuddy = M.get(count,J);
				if (IBuddy.ready && JBuddy.ready) {
					dgemm(IBuddy, JBuddy);
					count++;
					return true;
				}
				return false;
			}
				
			void lower(Block diag) {
			     for (int i = 1; i < B; i++)
			          for (int k = 0; k < i; k++)
			        	  for(int j=0; j<B; j++) {
			        		  
			        	  }
			}
			void backSolve(Block diag) {
				
			}
			void dgemm(Block left, Block upper) {
				
			}
			void LU() {
				
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
				for (int i=0; i < px*py; i++)
					for (int j=0; j < nx*ny; j++)
						A[i][j].init();
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
			
		}

	
}
