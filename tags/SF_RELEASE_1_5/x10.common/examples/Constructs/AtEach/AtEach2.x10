/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Slightly different test for ateach.
 *
 * @author kemal, 12/2004
 * @author vj
 */
public class AtEach2 extends x10Test {
    int nplaces = 0;

    public boolean run() {
        final dist d = dist.factory.unique(place.places);
        finish ateach (point p: d) {
            // remember if here and d[i] disagree
            // at any activity at any place
            chk(here == d[p]);
            async(this) { atomic { /*this.*/nplaces++; } } //FIXME this hack
        }
        // ensure that an activity ran in each place
        return nplaces == place.MAX_PLACES;
    }

    public static void main(String[] args) {
        new AtEach2().execute();
    }
}

