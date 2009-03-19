/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Slightly different test for ateach.
 *
 * @author kemal, 12/2004
 * @author vj
 */
public class AtEach2 extends x10Test {
    var nplaces: int = 0;

    public def run(): boolean = {
        val d: dist = Dist.makeUnique(place.places);
        finish ateach (val p: point in d) {
            // remember if here and d[i] disagree
            // at any activity at any place
            chk(here == d(p));
            async(this) { atomic { /*this.*/nplaces++; } } //FIXME this hack
        }
        // ensure that an activity ran in each place
        return nplaces == place.MAX_PLACES;
    }

    public static def main(var args: Rail[String]): void = {
        new AtEach2().execute();
    }
}
