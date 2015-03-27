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
//OPTIONS: -WORK_STEALING=true

/*
 * Ateach statement. Cannot pass WS compile
 */

public class AtEach_MustFailCompile {
    
    private val root = GlobalRef[AtEach_MustFailCompile](this);
    transient var nplaces: int = 0;

    public def run(): boolean {
        val d: Dist = Dist.makeUnique();
        val disagree: DistArray[int]{dist==d} = DistArray.make[int](d);
        val root = this.root;
        finish ateach (p in d) {
            // remember if here and d[p] disagree
            // at any activity at any place
            disagree(p) |= ((here != d(p)) ? 1 : 0);
            async at(root){atomic {root().nplaces++;}}
        }
        // ensure that d[i] agreed with here in
        // all places
        // and that an activity ran in each place
        val result = disagree.reduce(((x:Int,y:Int) => x+y),0) == 0 &&
            nplaces == Place.numPlaces();
        Console.OUT.println("AtEach: result = " + result);
        return result;
    }
    
    public static def main(Rail[String]) {
        val r = new AtEach_MustFailCompile().run();
        if(r){
             x10.io.Console.OUT.println("++++++Test succeeded.");
        }
    }
}