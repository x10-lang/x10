/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2011.
 */

import harness.x10Test;

/**
 * Tests GlobalRef.getLocalOrCopy
 * @author milthorpe 06/2011
 */
class GetLocalOrCopy extends x10Test {
    public def run(): boolean {
        val x = new Cell[Long](1);
        val globalX = new GlobalRef[Cell[Long]](x);
        val home = here;
        at (Place.places().next(here)) {
            val y = globalX.getLocalOrCopy();
            chk( x().equals( y() ) );
            if (globalX.home == here) {
                chk(globalX() == y);
            }
        }
        return true;
    }

    public static def main(Rail[String]) {
        new GetLocalOrCopy().execute();
    }
}
