/**
 * Intersect a rectangular region with a full region, construct an
 * array from it, and examine it.
 */

class Test12 extends TestArray {

    public void run() {
        Region r1 = Region.makeRectangular(new int [] {1,2}, new int [] {3,4});
        Region r2 = Region.makeFull(2);
        Region r3 = r1.intersection(r2);
        prArray("full intersected with rectangle", r3);
    }
}


