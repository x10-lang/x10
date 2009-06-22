/**
 * Inverse, difference and union for some basic rectangular
 * regions. Includes Os, Ls, Us, and a non-connected region.
 *
 * Uses "bump" argument to prArray to probe for whether constituent
 * regions of UnionRegion are disjoint: all array elements in region
 * should be bumped once and therefore all should be 1.
 */

class Test20 extends TestArray {

    public void run() {

        Region r1 = Region.makeRectangular(new int [] {2,2}, new int [] {3,4});
        prRegion("r1", r1);

        Region r2 = r1.complement();
        prUnbounded("r2=r1.complement()", r2);

        Region r3 = Region.makeRectangular(new int [] {0,0}, new int [] {5,5});
        Region r4 = r3.intersection(r2);
        prArray("r4=r3.intersection(r2)", r4, true);

        Region r5 = r3.difference(r1);
        prArray("r5=r3.difference(r1)", r5, true);

        Region r6 = Region.makeRectangular(new int [] {2,2}, new int [] {7,7});
        prArray("r6", r6, true);

        Region r7 = r3.difference(r6);
        prArray("r7=r3.difference(r6)", r7, true);

        Region r8 = Region.makeRectangular(new int [] {2,2}, new int [] {4,7});
        prArray("r8", r8, true);

        Region r9 = r3.difference(r8);
        prArray("r9=r3.difference(r8)", r9, true);

        Region r10 = Region.makeRectangular(new int [] {4,1}, new int [] {5,2});
        prArray("r10", r10, true);

        Region r11 = r9.difference(r10);
        prArray("r11=r9.difference(r10)", r11, true);

        prArray("r3.union(r6)", r3.union(r6), true);

        prArray("r6.union(r3)", r6.union(r3), true);

        prArray("r3.union(r8)", r3.union(r8), true);

        prArray("r8.union(r3)", r8.union(r3), true);
    }
}

