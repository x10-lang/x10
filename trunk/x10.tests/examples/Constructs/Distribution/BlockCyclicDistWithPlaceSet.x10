/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

import x10.util.Set;
import x10.util.HashSet;

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

	public const P = Dist.makeUnique();
	public const COUNT = 200;
	public const L = 5;
	public const K = 1;

	public def run(): boolean = {
		for (val (tries): Point in 1..COUNT) {
			val lb1: int = ranInt(-L, L);
			val lb2: int = ranInt(-L, L);
			val ub1: int = ranInt(lb1, L);
			val ub2: int = ranInt(lb2, L);
			val R = [lb1..ub1, lb2..ub2] as Region;
			val totalPoints = (ub1-lb1+1)*(ub2-lb2+1);
			val bSize: int = ranInt(1, totalPoints+1);
			val r = createRandPlaceSet();
			val np = r.np;
			val placeNums = r.placeNums;
			val placeSet  = r.placeSet;

			val DBlockCyclic = Dist.makeBlockCyclic(R, 0, bSize, placeSet);
			val offsWithinPlace = Rail.make[int](np);
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
		val placeSet: Set[Place]!;
		val placeNums: Rail[Int]!;
		def this(n: int, a: Rail[Int]!, s: Set[Place]!): randPlaceSet = {
			np = n;
			placeNums = a;
			placeSet = s;
		}
	}

	/**
	 * Create a random, non-empty subset of the places
	 */
	def createRandPlaceSet(): randPlaceSet! = {
		val placeSet: Set[Place]! = new HashSet[Place]();
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
		new BlockCyclicDistWithPlaceSet().execute();
	}
}
