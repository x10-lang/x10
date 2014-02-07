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

import harness.x10Test;

public class When extends x10Test{
    public def run():boolean {
        val b = new BoxedWBoolean();
        var r:Boolean = false;
        for (1..1000) {
            finish {
                async when (b.value);
                atomic b.value = true;
            }
            b.value = false;
            r |= b.value;
        }
        Console.OUT.println("DONE");
        return !r;
    }

    public static def main(args:Rail[String]) {
        new When().execute();
    }
}

final class BoxedWBoolean {
    var value:Boolean = false;
}
