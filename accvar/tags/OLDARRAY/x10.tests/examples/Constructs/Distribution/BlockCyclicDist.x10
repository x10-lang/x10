/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import java.util.Random;
import harness.x10Test;;

/**
 * Testing block-cyclic dist.
 *
 * Randomly generate block-cyclic dists and check
 * index-to-place mapping for conformance with x10 0.41 spec:
 *
 * The dist blockCyclic(R, N, Q) distributes the elements of
 * R cyclically over the set of places Q in blocks of size N.
 *
 * @author kemal 4/2005
 */
public class BlockCyclicDist extends x10Test {

	public def run(): boolean = {
		val P = dist.makeUnique();
		val np = place.MAX_PLACES;
		val COUNT = 200;
		val L  = 5;
		val K  = 1;
		for (val (tries): Point in 1..COUNT) {
			val lb1 = ranInt(-L, L);
			val lb2 = ranInt(-L, L);
			val ub1 = ranInt(lb1, L);
			val ub2 = ranInt(lb2, L);
			val R  = [lb1..ub1, lb2..ub2] to Region;
			val totalPoints = (ub1-lb1+1)*(ub2-lb2+1);
			val p = totalPoints/np;
			val bSize = ranInt(1, totalPoints+1);

			val DBlockCyclic = dist.makeBlockCyclic(R, bSize);
			val offsWithinPlace = Array.make[int]((0..np-1) -> here, (point)=>0);
			var placeNum: int = 0;
			var offsWithinBlock: int = 0;
			// System.out.println("lb1 = "+lb1+" ub1 = "+ub1+" lb2 = "+lb2+" ub2 = "+ub2+" totalPoints = "+totalPoints+" bSize = "+bSize);

			for (val (i,j):Point(2) in R) {
				// System.out.println("placeNum = "+placeNum+" offsWithinPlace[placeNum] = "+offsWithinPlace[placeNum]+" offsWithinBlock = "+offsWithinBlock+" i = "+i+" j = "+j+" DBlockCyclic[i,j] = "+DBlockCyclic[i,j].id);
				chk(DBlockCyclic(i, j) == P(placeNum));
				offsWithinPlace(placeNum)++;
				offsWithinBlock++;
				if (offsWithinBlock == bSize) {
					//time to go to next placeNum
					offsWithinBlock = 0;
					placeNum++;
					if (placeNum == np) {
						placeNum = 0;
					}
				}
			}
		}
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new BlockCyclicDist().execute();
	}
}
