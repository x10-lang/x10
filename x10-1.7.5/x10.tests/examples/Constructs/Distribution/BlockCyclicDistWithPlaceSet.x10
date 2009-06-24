/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import x10.util.Set;
import x10.util.HashSet;
import harness.x10Test;

/**
 * Testing block-cyclic dist.
 *
 * Randomly generate block-cyclic dists and check
 * index-to-place mapping for conformance with x10 0.41 spec:
 *
 * The dist blockCyclic(R, N, Q) distributes the elements of
 * R cyclically over the set of places Q in blocks of size N.
 *
 * This version actually uses a place set Q.
 *
 * @author kemal 5/2005
 */
public class BlockCyclicDistWithPlaceSet extends x10Test {

	const P = Dist.makeUnique();
	const COUNT = 200;
    const L = 5;
	const K = 1;

	public def run(): boolean = {
		for (val (tries): Point in 1..COUNT) {
			val lb1: int = ranInt(-L, L);
			val lb2: int = ranInt(-L, L);
			val ub1: int = ranInt(lb1, L);
			val ub2: int = ranInt(lb2, L);
			val R = [lb1..ub1, lb2..ub2] as Region;
			val totalPoints: int = (ub1-lb1+1)*(ub2-lb2+1);
			val bSize: int = ranInt(1, totalPoints+1);
			val r = createRandPlaceSet();
			val np = r.np;
			val placeNums = r.placeNums;
			val placeSet  = r.placeSet;

			val DBlockCyclic = Dist.makeBlockCyclic(R, bSize, placeSet);
			val offsWithinPlace = Array.makeUnique[int](np);
			var pn: int = 0;
			var offsWithinBlock: int = 0;
			//x10.io.Console.OUT.println("lb1 = "+lb1+" ub1 = "+ub1+" lb2 = "+lb2+" ub2 = "+ub2+" totalPoints = "+totalPoints+" bSize = "+bSize);

			for (val (i,j): Point(2) in R) {
				//x10.io.Console.OUT.println("placeNum = "+placeNums[pn]+" offsWithinPlace[pn] = "+offsWithinPlace[pn]+" offsWithinBlock = "+offsWithinBlock+" i = "+i+" j = "+j+" DBlockCyclic[i,j] = "+DBlockCyclic[i,j].id);
				chk(DBlockCyclic(i, j) == P(placeNums(pn)));
				offsWithinPlace(pn)++;
				offsWithinBlock++;
				if (offsWithinBlock == bSize) {
					//time to go to next placeNum
					offsWithinBlock = 0;
					pn++;
					if (pn == np) {
						pn = 0;
					}
				}
			}
		}
		return true;
	}

	/**
	 * emulating multiple return values
	 */
	static class randPlaceSet {
		val np: int;
		val placeSet: ValRail[Place];
		val placeNums: Array[Int](1);
		def this(n: int, a: Array[Int](1), s: ValRail[Place]): randPlaceSet = {
			np = n;
			placeNums = a;
			placeSet = s;
		}
	}
    static class Cell {
      val p:Place;
      var next:Cell;
      var length:Int;
      def this(p:Place) = this(p, null);
        
      def this(p:Place, n:Cell):Cell {
        this.p=p;
        this.next=n;
        this.length=(n==null? 0: n.length+1);
      }
      var temp:Cell;
      def add(p:Place) = new Cell(p, this);
      def toValRail():ValRail[Place] = {
         temp = this;
         new ValRail[Place](length, (Nat)=> 
           { val pp = this.temp.p; 
             this.temp = this.temp.next;
             pp})
      }
    }
   
	/**
	 * Create a random, non-empty subset of the places
	 */
	def createRandPlaceSet(): randPlaceSet = {
		var placeSet: Cell = null;
		var np: int;
		val placeNums  = Array.makeUnique[int](Place.MAX_PLACES);
		do {
			np = 0;
			
			val THRESH: int = ranInt(10, 90);
			for (val (i): Point(1) in P) {
				val x: int = ranInt(0, 99);
				if (x >= THRESH) {
				    placeSet = new Cell(P(i), placeSet);
					placeNums(np++) = i;
				}
			}
		} while (np == 0);
		return new randPlaceSet(np, placeNums, placeSet.toValRail());
	}

	public static def main(var args: Rail[String]): void = {
		new BlockCyclicDistWithPlaceSet().execute();
	}
}
