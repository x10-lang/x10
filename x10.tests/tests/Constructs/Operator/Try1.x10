/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 *  (C) Copyright Australian National University 2009-2010.
 */

import harness.x10Test;
import x10.util.*;

/**
 * Test operator redefinition. Example that suppresses nested MultipleExceptions.
 * @author mandel
 */

class Try1 extends x10Test {

    static class Flatten {

        private static def flatten(me: MultipleExceptions, acc: GrowableRail[CheckedThrowable]) {
            for (e in me.exceptions) {
                if (e instanceof MultipleExceptions) {
                    flatten(e as MultipleExceptions, acc);
                } else {
                    acc.add(e);
                }
            }
        }

        public static operator try (body: () => void,
                                    handler: (MultipleExceptions) => void) {
            try { body(); }
            catch (me: MultipleExceptions) {
                val exns = new GrowableRail[CheckedThrowable]();
                flatten(me, exns);
                handler (new MultipleExceptions(exns));
            }
        }

    }

    public def run() : boolean {
      Flatten.try {
        finish {
            async { throw new Exception("Exn 1"); }
            async finish {
                async { throw new Exception("Exn 2"); }
                finish {
                    async { throw new Exception("Exn 3"); }
                    throw new Exception("Exn 4");
                }
            }
        }
      } catch (e: MultipleExceptions) {
          chk(4 == e.exceptions.size);
      }
      return true;
    }

    public static def main(Rail[String]) {
        new Try1().execute();
    }
}
