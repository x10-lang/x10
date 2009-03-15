/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * This code (from the PSC experiment) yielded a SEVERE ERROR and then deadlocked,
 * on the code dated 2005/05/25 16:00:00 EDT.
 *
 * A 2D region is being accessed with a 1D point in line 96.
 * But we should not see a severe error.
 */
public class SevereError extends x10Test {
	public def run(): boolean {
		var startTime: long = System.currentTimeMillis();
		val N: int = 10;
		val M: int = 100;
		var r: Random = new Random(1);
		var c1: RandCharStr = new RandCharStr(r,N);
		var c2: RandCharStr = new RandCharStr(r,M);
		try {
			var m: EditDistMatrixParallelGetError = new EditDistMatrixParallelGetError(c1,c2);
			m.printMatrix();
			return false;
		} catch (var e: RankMismatchException) {
		} catch (var e: MultipleExceptions) {
			for (var it: Iterator[Throwable] = e.exceptions.iterator(); it.hasNext(); )
				if (!(it.next() instanceof RankMismatchException))
					return false;
		}
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new SevereError().execute();
	}

	static class EditDistMatrixParallelGetError {
		public const iGapPen: int = 2;
		public const iMatch: int = -1 ;
		public const iMisMatch: int = 1;
		public val e: Array[int];
		val c1: RandCharStr;
		val c2: RandCharStr;
		val N: int;
		val M: int;
		val shortSeqLength: int;
		val longSeqLength: int;
		public val min4Count: Array[int];
		public def this(var cSeq1: RandCharStr, var cSeq2: RandCharStr): EditDistMatrixParallelGetError = {
			c1 = cSeq1;
			c2 = cSeq2;
			N = c1.s.length-1;
			M = c2.s.length-1;
			if (N<M) {
				shortSeqLength = N; longSeqLength = M;
			} else {
				shortSeqLength = M; longSeqLength = N;
				x10.io.Console.OUT.println("Warning.. #rows < #columns.. performance will be equal to serial");
			}
			val blockWidth: int = Math.ceil(1.5*(shortSeqLength as double)) as int;
			val D: Dist = Dist.makeConstant(([0..N, 0..M]), here);
			val D_inner: Dist{rank==D.rank} = D | [1..N, 1..M];
			val D_boundary: Dist = D - D_inner;
			e = new Array[int](D);
			for (val (i,j): Point in D_boundary) e(i, j) = 0;
			min4Count = new Array[int](Dist.makeUnique());
			var localArr: Array[int];
			finish {
				ateach (val p: Point in Dist.makeUnique()) {
					val p_id: int = (here).id;
					val num_loops: int = Math.ceil((M as double) / ((Place.MAX_PLACES*blockWidth) as double)) as int;
					for (var loop_id: int = 0; loop_id<num_loops; loop_id++) {
						var startCol: int = (p_id*blockWidth+Place.MAX_PLACES*blockWidth*loop_id+1);
						var endCol: int = (p_id*blockWidth+Place.MAX_PLACES*blockWidth*loop_id+blockWidth);
						endCol = Math.min(endCol,M);
						if (startCol <= M) {
							if (startCol == 1) {
								val leftEndPt: int = 1;
								val tempArr: Array[int] = new Array[int](Dist.makeConstant(([1..N, leftEndPt..endCol]), here));
								for (val (i,j): Point in [1..N, leftEndPt..endCol]) {
									var topCell: int; var leftCell: int; var diagCell: int;
									if (i>1) topCell = tempArr(i-1, j); else topCell = 0;
									if (j>leftEndPt) leftCell = tempArr(i, j-1); else leftCell = 0;
									if (i>1 && j>leftEndPt) diagCell = tempArr(i-1, j-1); else diagCell = 0;
									tempArr(i, j) = min4(0, topCell+iGapPen, leftCell+iGapPen, diagCell + (c1.s(i) == c2.s(j) ? iMatch : iMisMatch));
								}
								val tempArr2: Array[int] = new Array[int](Dist.makeConstant(([1..N, leftEndPt..endCol]), here), (var (i,j): Point): int => {
									return tempArr(i, j);
								});
								finish {
									async (e.dist.get([0])) {
										e.update(tempArr2);
									}
								}
							}
							else {
								val leftEndPt: int = startCol-blockWidth;
								val tempArr: Array[int] = new Array[int](Dist.makeConstant([1..N, leftEndPt..endCol], here));
								for (val (i,j): Point in [1..N, leftEndPt..endCol]) {
									var topCell: int; var leftCell: int; var diagCell: int;
									if (i>1) topCell = tempArr(i-1, j); else topCell = 0;
									if (j>leftEndPt) leftCell = tempArr(i, j-1); else leftCell = 0;
									if (i>1 && j>leftEndPt) diagCell = tempArr(i-1, j-1); else diagCell = 0;
									tempArr(i, j) = min4(0, topCell+iGapPen, leftCell+iGapPen,
											diagCell + (c1.s(i) == c2.s(j) ? iMatch : iMisMatch));
								}
								val tempArr2: Array[int] = new Array[int](Dist.makeConstant([1..N, leftEndPt..endCol], here), (var (i,j): Point): int => {
									return tempArr(i, j);
								});
								finish {
									async (e.dist.get([0])) {
										e.update(tempArr2);
									}
								}
							}
						}
					}
				}
			}
		}
		public def printMatrix(): void = {
			x10.io.Console.OUT.println("Minimum Matrix EditDistance is: " + e(N, M));
			x10.io.Console.OUT.println("Matrix EditDistance is:");
			x10.io.Console.OUT.print(pad(' '));
			for (val (j): Point in [0..M]) x10.io.Console.OUT.print(pad(c2.s(j)));
			x10.io.Console.OUT.println();
			for (val (i): Point in [0..N]) {
				x10.io.Console.OUT.print(pad(c1.s(i)));
				for (val (j): Point in [0..M]) x10.io.Console.OUT.print(pad(e(i, j)));
				x10.io.Console.OUT.println();
			}
		}
		static def min4(var w: int, var x: int, var y: int, var z: int): int = {
			return Math.min(Math.min(w,x), Math.min(y,z));
		}
		static def pad(var x: int): String = { return pad(x + ""); }
		static def pad(var x: char): String = { return pad(x + ""); }
		static def pad(var s: String): String = {
			val n: int = 3;
			while (s.length() < n) s = " " + s;
			return " " + s + " ";
		}
	}

	static class Random {
		var randomSeed: int;
		public def this(var x: int): Random = {
			randomSeed = x;
		}
		public def nextAsciiNumber(): int = {
			randomSeed = (randomSeed * 1103515245 +12345);
			return (unsigned(randomSeed / 65536) % 128L) as int;
		}
		static def unsigned(var x: int): long = {
			return ((x as long) & 0x00000000ffffffffL);
		}
	}

	static value class RandCharStr {
		public val s: Array[char];
		public def this(var r: Random, var len: int): RandCharStr = {
			s = new Array[char](len+1);
			s(0) = '-';
			var i: int = 1;
			while (i <= len) {
				var x: int = r.nextAsciiNumber();
				switch (x) {
					case 65:case 65: s(i++) = 'A'; break;
					case 67:case 67: s(i++) = 'C'; break;
					case 71:case 71: s(i++) = 'G'; break;
					case 84:case 84: s(i++) = 'T'; break;
					default:
				}
			}
		}
	}
}
