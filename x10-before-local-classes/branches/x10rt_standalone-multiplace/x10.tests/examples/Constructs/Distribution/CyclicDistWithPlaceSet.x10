/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

import x10.util.Set;
import x10.util.HashSet;

/**
 * Testing cyclic dist.
 *
 * Randomly generate cyclic dists and check
 * index-to-place mapping for conformance with x10 0.41 spec:
 *
 * The dist cyclic(R, Q) distributes the points in R
 * cyclically across places in Q in order.
 *
 * This test version incorporates actual place sets
 *
 * @author kemal 5/2005
 */
public class CyclicDistWithPlaceSet extends x10Test {

	public const P = Dist.makeUnique();
	public const COUNT = 200;
	public const L = 5;

	public def run(): boolean = {
		for (val (tries): Point in 1..COUNT) {
			val lb1: int = ranInt(-L, L);
			val lb2: int = ranInt(-L, L);
			val ub1: int = ranInt(lb1, L);
			val ub2: int = ranInt(lb2, L);
			val R = [lb1..ub1, lb2..ub2] as Region;
			val totalPoints = (ub1-lb1+1)*(ub2-lb2+1);
			val r = createRandPlaceSet();
			val np = r.np;
			val placeNums = r.placeNums;
			val placeSet  = r.placeSet;

			val DCyclic  = Dist.makeCyclic(R, 0, placeSet);
			var offsWithinPlace: int = 0;
			var pn: int = 0;
			//x10.io.Console.OUT.println("lb1 = "+lb1+" ub1 = "+ub1+" lb2 = "+lb2+" ub2 = "+ub2+" totalPoints = "+totalPoints);

			for (val (i,j): Point(2) in R) {
				//x10.io.Console.OUT.println("placeNum = "+Place.place(pn)+" offsWithinPlace = "+offsWithinPlace+" i = "+i+" j = "+j+" DCyclic[i,j] = "+DCyclic(i, j).id);
				chk(DCyclic(i, j) == P(placeNums(pn)));
				chk(P(placeNums(pn)).id == placeNums(pn));
				pn++;
				if (pn == np) {
					//time to go to next offset
					pn = 0;
					offsWithinPlace++;
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
		val placeSet: Set[Place]!;
		val placeNums: Rail[Int]!;
		def this(n: int, a: Rail[Int]!, s: Set[Place]): randPlaceSet = {
			np = n;
			placeNums = a;
			placeSet = s;
		}
	}

	/**
	 * Create a random, non-empty subset of the places
	 */
	def createRandPlaceSet(): randPlaceSet! = {
		val placeSet: Set[Place] = new HashSet[Place]();
		var np: int;
		val placeNums = Rail.make[int](Place.MAX_PLACES);
		do {
			np = 0;
			val THRESH: int = ranInt(10, 90);
			for (val (i): Point(1) in P) {
				val x: int = ranInt(0, 99);
				if (x >= THRESH) {
					placeSet.add(P(i));
					placeNums(np++) = i;
				}
			}
		} while (np == 0);
		return new randPlaceSet(np, placeNums, placeSet);
	}

	public static def main(var args: Rail[String]): void = {
		new CyclicDistWithPlaceSet().execute();
	}
}
