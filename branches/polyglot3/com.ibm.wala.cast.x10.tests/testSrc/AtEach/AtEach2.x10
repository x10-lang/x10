/**
 * Slightly different test for ateach.
 *
 * @author kemal, 12/2004
 * @author vj
 */
public class AtEach2 {
    var nplaces: int = 0;

    public def run(): boolean = {
        val d: Dist = Dist.makeUnique(Place.places);
        finish ateach (val p: Point in d) {
            // remember if here and d[i] disagree
            // at any activity at any place
            chk(here == d(p));
            async(this.location) { atomic { /*this.*/nplaces++; } } //FIXME this hack
        }
        // ensure that an activity ran in each place
        return nplaces == Place.MAX_PLACES;
    }
    
    public static def chk(b: boolean, s: String): void = {
        if (!b) throw new Error(s);
    }

    public static def main(var args: Rail[String]): void = {
        new AtEach2().run();
    }
}
