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

public class When {
    public def run():boolean {
        val b = new BoxedWBoolean();
        var r:Boolean = false;
        for (var i:Int=0; i<1000; i++) {
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

    public static def main(args:Array[String](1)) {
        val r = new When().run();
        if(r){
             x10.io.Console.OUT.println("++++++Test succeeded.");
        }
    }
}

final class BoxedWBoolean {
    var value:Boolean = false;
}
