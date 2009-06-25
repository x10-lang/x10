/**
 * Take an intersection of two rectangular regions, construct an array
 * from it, and examine it.
 */

class Test05 extends TestArray {

    public void run() {
        Region r1 = Region.makeRectangular(new int [] {1,2}, new int [] {5,7});
        Region r2 = Region.makeRectangular(new int [] {3,3}, new int [] {8,9});
        Region r3 = r1.intersection(r2);
        prArray("rectangular intersection", r3);
    }
}

