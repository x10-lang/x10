/**
 * Inverse and difference for some basic rectangular regions. Includes
 * Os, Ls, Us, and a non-connected region.
 */

class Test20 extends TestArray {

    public void run() {

        Region r1 = Region.makeRectangular(new int [] {2,2}, new int [] {3,4});
        prRegion("r1", r1);

        Region r2 = r1.inverse();
        prUnbounded("r2=r1.inverse()", r2);

        Region r3 = Region.makeRectangular(new int [] {0,0}, new int [] {5,5});
        Region r4 = r3.intersection(r2);
        prArray("r4=r3.intersection(r2)", r4);

        Region r5 = r3.difference(r1);
        prArray("r5=r3.difference(r1)", r5);

        Region r6 = Region.makeRectangular(new int [] {2,2}, new int [] {7,7});
        prArray("r6", r6);

        Region r7 = r3.difference(r6);
        prArray("r7=r3.difference(r6)", r7);

        Region r8 = Region.makeRectangular(new int [] {2,2}, new int [] {4,7});
        prArray("r8", r8);

        Region r9 = r3.difference(r8);
        prArray("r9=r3.difference(r8)", r9);

        Region r10 = Region.makeRectangular(new int [] {4,1}, new int [] {5,2});
        prArray("r10", r10);

        Region r11 = r9.difference(r10);
        prArray("r11=r9.difference(r10)", r11);
    }
}

