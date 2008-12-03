/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import java.util.Random;
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
 * @author kemal 4/2005
 */
public class BlockCyclicDist extends x10Test {

	public boolean run() {
		final dist P = dist.factory.unique();
		final int np = place.MAX_PLACES;
		final int COUNT = 200;
		final int L = 5;
		final int K = 1;
		for (point [tries]: [1:COUNT]) {
			final int lb1 = ranInt(-L, L);
			final int lb2 = ranInt(-L, L);
			final int ub1 = ranInt(lb1, L);
			final int ub2 = ranInt(lb2, L);
			final region R = [lb1:ub1,lb2:ub2];
			final int totalPoints = (ub1-lb1+1)*(ub2-lb2+1);
			final int p = totalPoints/np;
			final int bSize = ranInt(1, totalPoints+1);

			final dist DBlockCyclic = dist.factory.blockCyclic(R, bSize);
			int[] offsWithinPlace = new int[np];
			int placeNum = 0;
			int offsWithinBlock = 0;
			// System.out.println("lb1 = "+lb1+" ub1 = "+ub1+" lb2 = "+lb2+" ub2 = "+ub2+" totalPoints = "+totalPoints+" bSize = "+bSize);

			for (point [i,j]: R) {
				// System.out.println("placeNum = "+placeNum+" offsWithinPlace[placeNum] = "+offsWithinPlace[placeNum]+" offsWithinBlock = "+offsWithinBlock+" i = "+i+" j = "+j+" DBlockCyclic[i,j] = "+DBlockCyclic[i,j].id);
				chk(DBlockCyclic[i,j] == P[placeNum]);
				offsWithinPlace[placeNum]++;
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

	public static void main(String[] args) {
		new BlockCyclicDist().execute();
	}
}

