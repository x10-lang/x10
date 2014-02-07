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
 * Test cases for structs upcast to Any and then downcast to
 * various related types.
 *
 * See XTENLANG-1013 for discussion of language design decision
 * encoded in this test case.
 */
class StructToAnyAndBack extends x10Test {

    static struct S implements Comparable[S] {
       val x:int;
       def this(a:int) { x = a; }
       public def compareTo(o:S) { return x.compareTo(o.x); }
    }

    static def testAStruct[S,T](v:Any):boolean {
        val x = v as S;             // ERR: This is an unsound cast because X10 currently does not perform constraint solving at runtime for generic parameters.
        val y = v as Comparable[S]; // ERR: This is an unsound cast because X10 currently does not perform constraint solving at runtime for generic parameters.
        try {
            val z = v as T;  // ERR: This is an unsound cast because X10 currently does not perform constraint solving at runtime for generic parameters.
            Console.OUT.println("Did not raise exception "+v.typeName());
            return false;
        } catch (e:ClassCastException) {
            return true;
        } catch (e:Exception) {
            Console.OUT.println("Raised wrong exception "+e);
            return false;
        }
    }
    
    public def run():boolean {
        var res:boolean = true;
        res &= testAStruct[int,float](1n);
        res &= testAStruct[ubyte, double](0yu);
        res &= testAStruct[float, short](3.0f);
        res &= testAStruct[S, int](S(10n));

        return res;
    }

    public static def main(Rail[String]) {
        new StructToAnyAndBack().execute();
    }
}
