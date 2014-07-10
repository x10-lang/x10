/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;
import x10.regionarray.*;

/**
 * Test for ateach.
 *
 * @author kemal, 12/2004
 */
public class AtEach extends x10Test {
    private val root = GlobalRef[AtEach](this);
    transient var nplaces: long = 0L;

    public def run(): boolean = {
        val d: Dist = Dist.makeUnique();
        val disagree: DistArray[int]{dist==d} = DistArray.make[int](d);
        val root = this.root;
        finish ateach (p in d) {
            // remember if here and d[p] disagree
            // at any activity at any place
            disagree(p) |= ((here != d(p)) ? 1n : 0n);
            async at(root){atomic {root().nplaces++;}}
        }
        // ensure that d[i] agreed with here in
        // all places
        // and that an activity ran in each place
        return disagree.reduce(((x:Int,y:Int) => x+y),0n) == 0n &&
                nplaces == Place.numPlaces();
    }

    public static def main(Rail[String])  {
        new AtEach().execute();
    }
}
