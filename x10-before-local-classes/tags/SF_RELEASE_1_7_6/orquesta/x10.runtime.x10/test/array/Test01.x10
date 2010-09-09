/**
 * Construct a 3-d rectangular region, make an array from from it, and
 * examine it.
 */

class Test01 extends TestArray {

    public void run() {
        int [] min = {1, 2, 3};
        int [] max = {2, 4, 6};
        Region r = Region.makeRectangular(min, max);
        prArray("3-d rect array", r);
    }
}

