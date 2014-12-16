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

/**
 * Two remote references to the same object must be equal.
 * @author igor 03/2010
 */
class RemoteRefEquality extends x10Test {

    val rr:GlobalRef[RemoteRefEquality];

    public def this(v:GlobalRef[RemoteRefEquality]) { rr = v; }

    public def this() { this(GlobalRef[RemoteRefEquality](null)); }

    public def run(): boolean {
        chk(Place.numPlaces() > 1, "This test must be run with multiple places");
        val local_ = GlobalRef[RemoteRefEquality](new RemoteRefEquality());
        val remote = at (Place.places().next(here)) GlobalRef[RemoteRefEquality](new RemoteRefEquality(local_));
        Console.OUT.println(local_ == (at (remote) remote().rr)); 
        Console.OUT.println(at (remote) remote().rr == local_);
        return at (remote) remote().rr == local_;
    }

    public static def main(Rail[String]) {
        new RemoteRefEquality().execute();
    }
}

