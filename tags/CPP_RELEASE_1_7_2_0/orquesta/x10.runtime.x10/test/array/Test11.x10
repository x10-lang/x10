/**
 * Make a full region, examine it, observing an
 * UnboundedRegionException when attemppting to scan it
 */

class Test11 extends TestArray {

    public void run() {
        Region r = Region.makeFull(3);
        prUnbounded("full region", r);
    }
}
