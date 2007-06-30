/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import java.util.Set;
import java.util.HashSet;
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

	const dist P = dist.factory.unique();
	const int COUNT = 200;
	const int L = 5;
	const int K = 1;

	public boolean run() {
		for (point [tries]: [1:COUNT]) {
			final int lb1 = ranInt(-L, L);
			final int lb2 = ranInt(-L, L);
			final int ub1 = ranInt(lb1, L);
			final int ub2 = ranInt(lb2, L);
			final region R = [lb1:ub1,lb2:ub2];
			final int totalPoints = (ub1-lb1+1)*(ub2-lb2+1);
			final int bSize = ranInt(1, totalPoints+1);
			final randPlaceSet r = createRandPlaceSet();
			final int np = r.np;
			final int[] placeNums = r.placeNums;
			final Set placeSet = r.placeSet;

			final dist DBlockCyclic = dist.factory.blockCyclic(R, bSize, placeSet);
			int[] offsWithinPlace = new int[np];
			int pn = 0;
			int offsWithinBlock = 0;
			//System.out.println("lb1 = "+lb1+" ub1 = "+ub1+" lb2 = "+lb2+" ub2 = "+ub2+" totalPoints = "+totalPoints+" bSize = "+bSize);

			for (point [i,j]: R) {
				//System.out.println("placeNum = "+placeNums[pn]+" offsWithinPlace[pn] = "+offsWithinPlace[pn]+" offsWithinBlock = "+offsWithinBlock+" i = "+i+" j = "+j+" DBlockCyclic[i,j] = "+DBlockCyclic[i,j].id);
				chk(DBlockCyclic[i,j] == P[placeNums[pn]]);
				offsWithinPlace[pn]++;
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
		final int np;
		final Set placeSet;
		final int[] placeNums;
		randPlaceSet(int n, int[] a, Set s) {
			np = n;
			placeNums = a;
			placeSet = s;
		}
	}

	/**
	 * Create a random, non-empty subset of the places
	 */
	randPlaceSet createRandPlaceSet() {
		Set placeSet;
		int np;
		int[] placeNums = new int[place.MAX_PLACES];
		do {
			np = 0;
			placeSet = new HashSet();
			final int THRESH = ranInt(10, 90);
			for (point [i]: P) {
				final int x = ranInt(0, 99);
				if (x >= THRESH) {
					placeSet.add(P[i]);
					placeNums[np++] = i;
				}
			}
		} while (np == 0);
		return new randPlaceSet(np, placeNums, placeSet);
	}

	public static void main(String[] args) {
		new BlockCyclicDistWithPlaceSet().execute();
	}
}

