/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Testing cyclic dist.
 *
 * Randomly generate cyclic dists and check
 * index-to-place mapping for conformance with x10 0.41 spec:
 *
 *The dist cyclic(R, Q) distributes the points in R
 *cyclically across places in Q in order.
 *
 * @author kemal 4/2005
 */
public class CyclicDist extends x10Test {

	public def run(): boolean = {
		val P: dist = Dist.makeUnique();
		val np: int = place.MAX_PLACES;
		val COUNT: int = 200;
		val L: int = 5;
		for (val (tries): point in 1..COUNT) {
			val lb1: int = ranInt(-L, L);
			val lb2: int = ranInt(-L, L);
			val ub1: int = ranInt(lb1, L);
			val ub2: int = ranInt(lb2, L);

			val R = [lb1..ub1, lb2..ub2] to Region;
			val DCyclic  = Dist.makeCyclic(R);
			val totalPoints: int = (ub1-lb1+1)*(ub2-lb2+1);
			var offsWithinPlace: int = 0;
			var placeNum: int = 0;
			System.out.println("lb1 = "+lb1+" ub1 = "+ub1+" lb2 = "+lb2+" ub2 = "+ub2+" totalPoints = "+totalPoints);

			for (val (i,j): point in R) {
				System.out.println("placeNum = "+placeNum+" offsWithinPlace = "+offsWithinPlace+" i = "+i+" j = "+j+" DCyclic[i,j] = "+DCyclic(i, j).id);
				chk(P(placeNum).id == placeNum);
				chk(DCyclic(i, j) == P(placeNum));
				placeNum++;
				if (placeNum == np) {
					//time to go to next offset
					placeNum = 0;
					offsWithinPlace++;
				}
			}
		}
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new CyclicDist().execute();
	}
}
