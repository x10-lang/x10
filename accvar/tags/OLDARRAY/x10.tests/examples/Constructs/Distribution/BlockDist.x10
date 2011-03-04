/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing block dist.
 *
 * Randomly generate block dists and check
 * index-to-place mapping for conformance with x10 0.41 spec
 *
 * The dist block(R, Q) distributes the elements of R (in
 * order) over the set of places Q in blocks as follows. Let p equal
 * |R| div N and q equal |R| mod N, where N is the size of Q. The first
 * q places get successive blocks of size (p + 1) and the remaining
 * places get blocks of size p.
 *
 * @author kemal 4/2005
 */
public class BlockDist extends x10Test {

	public def run(): boolean = {
		val P  = dist.makeUnique();
		val np  = place.MAX_PLACES;
		val COUNT = 200;
		val L  = 5;
		for ((tries): Point(1) in 1..COUNT) {
			val lb1: int = ranInt(-L, L);
			val lb2: int = ranInt(-L, L);
			val ub1: int = ranInt(lb1, L);
			val ub2: int = ranInt(lb2, L);
			val R = [lb1..ub1, lb2..ub2] to Region;
			val DBlock  = dist.makeBlock(R);
			val totalPoints  = (ub1-lb1+1)*(ub2-lb2+1);
			val p  = totalPoints/np;
			val q  = totalPoints%np;
			var offsWithinPlace: Int = 0;
			var placeNum: Int = 0;
			for (val (i,j): point in R) {
				chk(DBlock(i, j) == P(placeNum));
				chk(P(placeNum).id == placeNum);
				offsWithinPlace++;
				if (offsWithinPlace == (p + ((placeNum < q) ? 1 : 0))) {
					//time to go to next place
					offsWithinPlace = 0;
					placeNum++;
				}
			}
		}
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new BlockDist().execute();
	}
}
