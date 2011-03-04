/**
 * Basic rectangular region equality.
 *
 * (was RegionEquality)
 */

class Test19 extends TestArray {

    public void run() {

        Region r1 = Region.makeRectangular(new int [] {0,1}, new int [] {5,6});
        Region r2 = Region.makeRectangular(new int [] {1,2}, new int [] {6,7});
        Region r3 = Region.makeRectangular(new int [] {1,2}, new int [] {5,6});
        Region r4 = r1.intersection(r2);

        comp(r1,r2);
        comp(r1,r3);
        comp(r1,r4);
        comp(r3,r4);

        comp(r2,r1);
        comp(r3,r1);
        comp(r4,r1);
        comp(r4,r3);

    }

    void comp(Region a, Region b) {
        pr(a + "==" + b + " " + a.$eq(b) + " " + a.equals(b));
    }
}

