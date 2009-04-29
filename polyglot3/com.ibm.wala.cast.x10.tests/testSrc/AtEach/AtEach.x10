/**
 * Test for ateach.
 *
 * @author kemal, 12/2004
 */
public class AtEach {
    var nplaces: int = 0;

    public def run(): boolean = {
        val d: Dist = Dist.makeUnique(Place.places);
        val disagree: Array[int] = Array.make[int](d);
        finish ateach (val p in d) {
            // remember if here and d[p] disagree
            // at any activity at any place
            disagree(p) |= ((here != d(p)) ? 1 : 0);
            async(this.location){atomic {nplaces++;}}
        }
        // ensure that d[i] agreed with here in
        // all places
        // and that an activity ran in each place
        return disagree.reduce(int.+,0) == 0 && nplaces == Place.MAX_PLACES;
    }

    public static def main(var args: Rail[String]): void = {
        new AtEach().run();
    }
}
