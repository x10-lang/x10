/**
 * Test banded regions.
 *
 * (was RegionBanded)
 */

class Test17 extends TestArray {

    public void run() {

        final Region u = Region.makeRectangular(new int [] {0,0}, new int [] {4,4});
    
        Region b1 = Region.makeBanded(5, 1, 1);
        prArray("makeBanded(5,1,1)", b1);

        String contains = "";
        String notContains = "";
        Region.Iterator it = u.iterator();
        while (it.hasNext()) {
            int [] x = it.next();
            Point p = Point.make(x);
            if (b1.contains(p)) contains += p;
            else notContains += p;
        }
        pr("contains " + contains);
        pr("does not contain " + notContains);

        Region b2 = Region.makeBanded(5, 1, 2);
        prArray("makeBanded(5,1,2)", b2);

        Region b3 = Region.makeBanded(5, 2, 1);
        prArray("makeBanded(5,2,1)", b3);

    }
}

