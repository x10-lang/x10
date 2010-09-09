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

 

/**
 * Two remote references to the same object must be equal.
 * @author igor 03/2010
 */
class RemoteRefEquality   {

    val rr:RemoteRefEquality;

    public def this(v:RemoteRefEquality) { rr = v; }

    public def this() { this(null); }

    public def run(): boolean {
        chk(Place.places.length > 1, "This test must be run with multiple places");
        val local_ = new RemoteRefEquality();
        val remote = at (here.next()) new RemoteRefEquality(local_);
        Console.OUT.println(local_ == (at (remote) remote.rr)); // workaround XTENLANG-1124
        Console.OUT.println(at (remote) remote.rr == local_);
        return at (remote) remote.rr == local_;
    }

    public static def main(Rail[String]) {
        new RemoteRefEquality().run ();
    }
}

