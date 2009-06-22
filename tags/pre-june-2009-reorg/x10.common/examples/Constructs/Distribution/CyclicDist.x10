/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

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

	public boolean run() {
		final dist P = dist.factory.unique();
		final int np = place.MAX_PLACES;
		final int COUNT = 200;
		final int L = 5;
		for (point [tries]: [1:COUNT]) {
			final int lb1 = ranInt(-L, L);
			final int lb2 = ranInt(-L, L);
			final int ub1 = ranInt(lb1, L);
			final int ub2 = ranInt(lb2, L);

			final region R = [lb1:ub1,lb2:ub2];
			final dist DCyclic = dist.factory.cyclic(R);
			final int totalPoints = (ub1-lb1+1)*(ub2-lb2+1);
			int offsWithinPlace = 0;
			int placeNum = 0;
			System.out.println("lb1 = "+lb1+" ub1 = "+ub1+" lb2 = "+lb2+" ub2 = "+ub2+" totalPoints = "+totalPoints);

			for (point [i,j]: R) {
				System.out.println("placeNum = "+placeNum+" offsWithinPlace = "+offsWithinPlace+" i = "+i+" j = "+j+" DCyclic[i,j] = "+DCyclic[i,j].id);
				chk(P[placeNum].id == placeNum);
				chk(DCyclic[i,j] == P[placeNum]);
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

	public static void main(String[] args) {
		new CyclicDist().execute();
	}
}

