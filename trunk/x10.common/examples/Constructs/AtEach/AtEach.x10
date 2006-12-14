/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test for ateach.
 *
 * @author kemal, 12/2004
 */
public class AtEach extends x10Test {
    int nplaces = 0;

    public boolean run() {
        final dist d = dist.factory.unique(place.places);
        final int[.] disagree = new int[d];
        finish ateach (final point p: d) {
            // remember if here and d[p] disagree
            // at any activity at any place
            disagree[p] |= ((here != d[p]) ? 1 : 0);
            async(this){atomic {nplaces++;}}
        }
        // ensure that d[i] agreed with here in
        // all places
        // and that an activity ran in each place
        return disagree.reduce(intArray.add,0) == 0 &&
                nplaces == place.MAX_PLACES;
    }

    public static void main(String[] args) {
        new AtEach().execute();
    }
}

