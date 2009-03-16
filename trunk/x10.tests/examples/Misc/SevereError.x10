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
	val N = 10;
	val M = 100;
	val r = new Random(1);
	val c1 = new RandCharStr(r,N);
	val c2 = new RandCharStr(r,M);
	try {
	    val m = new EditDistMatrixParallelGetError(c1,c2);
	    m.printMatrix();
	    return false;
	} catch (e: MultipleExceptions) {
	    for (x in e.exceptions)
		if (!(x instanceof RankMismatchException))
		    return false;
	}
	return true;
    }

    public static def main(Rail[String]) {
	new SevereError().execute();
    }

    static class EditDistMatrixParallelGetError {
        public const ZERO = (Point)=>0;
	public const iGapPen = 2;
	public const iMatch = -1 ;
	public const iMisMatch = 1;
	public val e: Array[int];
	val c1: RandCharStr;
	val c2: RandCharStr;
	val N: int;
	val M: int;
	val shortSeqLength: int;
	val longSeqLength: int;
	public val min4Count: Array[int];
	public def this(var cSeq1: RandCharStr, var cSeq2: RandCharStr) {
	    c1 = cSeq1;
	    c2 = cSeq2;
	    N = c1.s.length-1;
	    M = c2.s.length-1;
	    shortSeqLength = (N<M)? N : M;
	    longSeqLength = (N<M)? M: N;
	    if (M>N)
		x10.io.Console.OUT.println("Warning.. #rows < #columns.. performance will be equal to serial");

	    val blockWidth = (Math.ceil(1.5*(shortSeqLength as Double))) as int;
	    val D = Dist.makeConstant(([0..N, 0..M]), here);
	    val D_inner: Dist{rank==2} = D.restriction([1..N, 1..M] as Region);
	    val D_boundary: Dist{rank==2} = D.difference(D_inner.region);
	    e = Array.make[int](D, ZERO);
	    val U = Dist.makeUnique();
	    for ((i,j) in D_boundary) e(i, j) = 0;
	    min4Count = Array.make[int](U, ZERO);
	    var localArr: Array[int];
	    finish {
		ateach (val p: Point in U) {
		    val P = Place.MAX_PLACES;
		    val p_id = here.id;
		    val num_loops = (Math.ceil(M as Double) /
				     ((P as Double)*(blockWidth as Double))) as Int;
		    for (var loop_id: int = 0; loop_id<num_loops; loop_id++) {
			val startCol = (p_id*blockWidth+P*blockWidth*loop_id+1);
			val endCol1 = (p_id*blockWidth+P*blockWidth*loop_id+blockWidth);
			val endCol = Math.min(endCol1,M);
			if (startCol <= M) {
			    val leftEndPt = (startCol==1)?1 : startCol-blockWidth;
			    val R:Region = [1..N, leftEndPt..endCol];
			    val tempArr = Array.make[int](R, ZERO);
			    for ((i,j) in R) {
				val topCell = (i>1)? tempArr(i-1,j):0, 
				    leftCell = (j>leftEndPt)? tempArr(i,j-1) : 0,
				    diagCell = (i>1 && j>leftEndPt) ? tempArr(i-1, j-1) : 0;
				tempArr(i, j) = min4(0, topCell+iGapPen, leftCell+iGapPen, 
						     diagCell + (c1.s(i) == c2.s(j) ? iMatch : iMisMatch));
			    }
			    val tempArr2 = Array.make[int]([1..N, leftEndPt..endCol], 
							   (k: Point): int => tempArr(k(0), k(1)));
			    finish {
				async (e.dist(0)) {
				    for ((i,j) in R)
					e(i,j) = tempArr2(i,j);
				}
			    }
			}
		    }
		}
	    }
	}
	def println(x:String) = x10.io.Console.OUT.println(x);
	def println() = x10.io.Console.OUT.println();
	def print(x:String) = x10.io.Console.OUT.print(x);
	public def printMatrix(): void = {
	    println("Minimum Matrix EditDistance is: " + e(N, M));
	    println("Matrix EditDistance is:");
	    print(pad(' '));
	    for ((j): Point in 0..M) print(pad(c2.s(j)));
	    println();
	    for ((i): Point in 0..N) {
		print(pad(c1.s(i)));
		for (val (j): Point in 0..M) print(pad(e(i, j)));
		println();
	    }
	}
	static def min4(var w: int, var x: int, var y: int, var z: int): int = {
	    return Math.min(Math.min(w,x), Math.min(y,z));
	}
	static def pad(var x: int) = pad(x + "");
	static def pad(var x: char) = pad(x + ""); 
	static def pad(var s: String): String = {
	    val n = 3;
	    while (s.length() < n) s = " " + s;
	    return " " + s + " ";
	}
    }

    static class Random {
	var randomSeed: int;
	public def this(var x: int)  {
	    randomSeed = x;
	}
	public def nextAsciiNumber(): int {
	    randomSeed = (randomSeed * 1103515245 +12345);
	    return (unsigned(randomSeed / 65536) % 128L) as int;
	}
	static def unsigned(var x: int) = (x as long) & 0x00000000ffffffffL;
    }

    static value class RandCharStr {
	public val s: Rail[char];
	public def this(r: Random, len: int) = {
	    s = Rail.makeVar[char](len+1);
	    s(0) = '-';
	    var i: int = 1;
	    while (i <= len) {
		val x = r.nextAsciiNumber();
		switch (x) {
		case 65: s(i++) = 'A'; break;
		case 67: s(i++) = 'C'; break;
		case 71:s(i++) = 'G'; break;
		case 84:s(i++) = 'T'; break;
		default:
		}
	    }
	}
    }
}
