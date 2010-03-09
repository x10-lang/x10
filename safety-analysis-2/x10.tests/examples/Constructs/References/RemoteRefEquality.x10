/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

/**
 * Two remote references to the same object must be equal.
 * @author igor 06/2009
 */
class RemoteRefEquality extends x10Test {

    val rr:RemoteRefEquality;

    public def this(v:RemoteRefEquality) { rr = v; }

    public def this() { this(null); }

    public def run(): boolean {
        chk(Place.places.length > 1, "This test must be run with multiple places");
        val local = new RemoteRefEquality();
        val remote = at (here.next()) new RemoteRefEquality(local);
        Console.OUT.println(local == (at (remote) remote.rr)); // workaround XTENLANG-1124
        Console.OUT.println(at (remote) remote.rr == local);
        return at (remote) remote.rr == local;
    }

    public static def main(Rail[String]) {
        new RemoteRefEquality().execute();
    }
}

