/**
 * Make a full region, examine it, observing an
 * UnboundedRegionException when attemppting to scan it
 */

class Test11 extends TestArray {

    public void run() {
        Region r = Region.makeFull(3);
        try {
            prRegion("full region", r, true);
        } catch (Throwable e) {
            pr(e.toString());
        }
    }
}
