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
import x10.io.Console;

/**
 * Create remote references and see if local GCs are 
 * keeping them alive
 */

public class RemoteRef extends x10Test {

    static val Debug = false;

    static class AnObject {
      var f:Int = 0n;
    }

    static class ResultHolder {
       var success:Boolean = false;
    }

    public def run(): boolean {
        val iterCount = 20n;
        val c = Clock.make();
        val res = GlobalRef[ResultHolder](new ResultHolder());
        spawnRemoteTask(c, iterCount, res);
        for (var i:int=0n; i<iterCount; i++) {
            // TODO: Try to force GC to happen here by doing lots of allocation
            if (Debug) Console.OUT.println("Local before next: "+i);
            Clock.advanceAll();
            if (Debug) Console.OUT.println("Local after next: "+i);
        }
        if (Debug) Console.OUT.println("Local: before last next");
        Clock.advanceAll();
        if (Debug) Console.OUT.println("Local: after last next");
        return res().success;
    }

    public def spawnRemoteTask(c:Clock, iterCount:Int, res:GlobalRef[ResultHolder]) {
        val v = GlobalRef[AnObject](new AnObject());
        async clocked(c) {
            at(Place.places().next(here)) {
                try {
                    for (var i:int = 0n; i<iterCount; i++) {
                    if (Debug) Console.OUT.println("Remote before next: "+i);
                        Clock.advanceAll();
                    if (Debug) Console.OUT.println("Remote after next: "+i);
                        at (v) v().f++; 
                        }
                    if (Debug) Console.OUT.println("Remote: before last next");
                    at (res) { res().success = (at (v) v().f == iterCount); }
                    Clock.advanceAll();
                    if (Debug) Console.OUT.println("Remote: after last next next");
                

                // work around bugs serialising exceptions on managed backend XTENLANG-3112   
                } catch (e:Error) {
                    e.printStackTrace();
                    throw e;
                } catch (e:Exception) {
                    e.printStackTrace();
                    throw e;
                }

            }

        }
    }

    public static def main(var args: Rail[String]): void {
        new RemoteRef().execute();
    }
}
