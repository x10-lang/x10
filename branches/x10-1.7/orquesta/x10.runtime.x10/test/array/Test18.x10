/**
 * Test triangular regions.
 *
 * (was RegionTriangular)
 */

class Test18 extends TestArray {

    public void run() {

        final Region u = Region.makeRectangular(new int [] {0,0}, new int [] {3,3});
    
        Region r1 = Region.makeUpperTriangular(4);
        prArray("makeUpperTriangular(4)", r1);

        String contains = "";
        String notContains = "";
        Region.Iterator it = u.iterator();
        while (it.hasNext()) {
            int [] x = it.next();
            Point p = Point.make(x);
            if (r1.contains(p)) contains += p;
            else notContains += p;
        }
        pr("contains " + contains);
        pr("does not contain " + notContains);

        Region r2 = Region.makeLowerTriangular(4);
        prArray("makeLowerTriangular(4)", r2);
    }
}

