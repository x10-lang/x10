/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

import java.util.Iterator;

/**
 * This code (from the PSC experiment) yielded a SEVERE ERROR and then deadlocked,
 * on the code dated 2005/05/25 16:00:00 EDT.
 *
 * A 2D region is being accessed with a 1D point in line 96.
 * But we should not see a severe error.
 */
public class SevereError extends x10Test {
	public boolean run() {
		long startTime = System.currentTimeMillis();
		final int N = 10;
		final int M = 100;
		Random r = new Random(1);
		RandCharStr c1 = new RandCharStr(r,N);
		RandCharStr c2 = new RandCharStr(r,M);
		try {
			EditDistMatrixParallelGetError m = new EditDistMatrixParallelGetError(c1,c2);
			m.printMatrix();
			return false;
		} catch (RankMismatchException e) {
		} catch (MultipleExceptions e) {
			for (Iterator it = e.exceptions.iterator(); it.hasNext(); )
				if (!(it.next() instanceof RankMismatchException))
					return false;
		}
		return true;
	}

	public static void main(String[] args) {
		new SevereError().execute();
	}

	static class EditDistMatrixParallelGetError {
		const int iGapPen = 2;
		const int iMatch = -1 ;
		const int iMisMatch = 1;
		public final int[.] e;
		final RandCharStr c1;
		final RandCharStr c2;
		final int N;
		final int M;
		final int shortSeqLength;
		final int longSeqLength;
		public final int[.] min4Count;
		public EditDistMatrixParallelGetError(RandCharStr cSeq1, RandCharStr cSeq2) {
			c1 = cSeq1;
			c2 = cSeq2;
			N = c1.s.length-1;
			M = c2.s.length-1;
			if (N<M) {
				shortSeqLength = N; longSeqLength = M;
			} else {
				shortSeqLength = M; longSeqLength = N;
				System.out.println("Warning.. #rows < #columns.. performance will be equal to serial");
			}
			final int blockWidth = (int)(Math.ceil(1.5*(double)shortSeqLength));
			final dist D = ([0:N,0:M])->here;
			final dist(:rank==D.rank) D_inner = D | [1:N,1:M];
			final dist D_boundary = D - D_inner;
			e = new int[D];
			for (point[i,j] : D_boundary) e[i,j] = 0;
			min4Count = new int[dist.factory.unique()];
			int[.] localArr;
			finish {
				ateach (point p : dist.factory.unique()) {
					final int p_id = (here).id;
					final int num_loops = (int)(Math.ceil((double)M / ((double)place.MAX_PLACES*(double)blockWidth)));
					for (int loop_id = 0; loop_id<num_loops; loop_id++) {
						int startCol = (p_id*blockWidth+place.MAX_PLACES*blockWidth*loop_id+1);
						int endCol = (p_id*blockWidth+place.MAX_PLACES*blockWidth*loop_id+blockWidth);
						endCol = Math.min(endCol,M);
						if (startCol <= M) {
							if (startCol == 1) {
								final int leftEndPt = 1;
								final int[.] tempArr = new int [ ([1:N, leftEndPt:endCol])->here];
								for (point [i,j] : [1:N, leftEndPt:endCol]) {
									int topCell; int leftCell; int diagCell;
									if (i>1) topCell = tempArr[i-1,j]; else topCell = 0;
									if (j>leftEndPt) leftCell = tempArr[i,j-1]; else leftCell = 0;
									if (i>1 && j>leftEndPt) diagCell = tempArr[i-1,j-1]; else diagCell = 0;
									tempArr[i,j] = min4(0, topCell+iGapPen, leftCell+iGapPen, diagCell + (c1.s[i] == c2.s[j] ? iMatch : iMisMatch));
								}
								final int value[.] tempArr2 = new int [ ([1:N, leftEndPt:endCol])->here] (point[i,j]) {
									return tempArr[i,j];
								};
								finish {
									async (e.distribution.get([0])) {
										e.update(tempArr2);
									}
								}
							}
							else {
								final int leftEndPt = startCol-blockWidth;
								final int[.] tempArr = new int[ [1:N, leftEndPt:endCol]->here];
								for (point [i,j] : [1:N, leftEndPt:endCol]) {
									int topCell; int leftCell; int diagCell;
									if (i>1) topCell = tempArr[i-1,j]; else topCell = 0;
									if (j>leftEndPt) leftCell = tempArr[i,j-1]; else leftCell = 0;
									if (i>1 && j>leftEndPt) diagCell = tempArr[i-1,j-1]; else diagCell = 0;
									tempArr[i,j] = min4(0, topCell+iGapPen, leftCell+iGapPen,
											diagCell + (c1.s[i] == c2.s[j] ? iMatch : iMisMatch));
								}
								final int value[.] tempArr2 = new int[ [1:N, leftEndPt:endCol]->here] (point[i,j]) {
									return tempArr[i,j];
								};
								finish {
									async (e.distribution.get([0])) {
										e.update(tempArr2);
									}
								}
							}
						}
					}
				}
			}
		}
		public void printMatrix() {
			System.out.println("Minimum Matrix EditDistance is: " + e[N,M]);
			System.out.println("Matrix EditDistance is:");
			System.out.print(pad(' '));
			for (point [j]: [0:M]) System.out.print(pad(c2.s[j]));
			System.out.println();
			for (point [i]: [0:N]) {
				System.out.print(pad(c1.s[i]));
				for (point [j]: [0:M]) System.out.print(pad(e[i,j]));
				System.out.println();
			}
		}
		static int min4(int w, int x, int y, int z) {
			return Math.min(Math.min(w,x), Math.min(y,z));
		}
		static String pad(int x) { return pad(x + ""); }
		static String pad(char x) { return pad(x + ""); }
		static String pad(String s) {
			final int n = 3;
			while (s.length() < n) s = " " + s;
			return " " + s + " ";
		}
	}

	static class Random {
		int randomSeed;
		public Random(int x) {
			randomSeed = x;
		}
		public int nextAsciiNumber() {
			randomSeed = (randomSeed * 1103515245 +12345);
			return (int)(unsigned(randomSeed / 65536) % 128L);
		}
		static long unsigned(int x) {
			return ((long)x & 0x00000000ffffffffL);
		}
	}

	static value class RandCharStr {
		public final char [] s;
		public RandCharStr (Random r, int len) {
			s = new char[len+1];
			s[0] = '-';
			int i = 1;
			while (i <= len) {
				int x = r.nextAsciiNumber();
				switch (x) {
					case 65: s[i++] = 'A'; break;
					case 67: s[i++] = 'C'; break;
					case 71: s[i++] = 'G'; break;
					case 84: s[i++] = 'T'; break;
					default:
				}
			}
		}
	}
}

