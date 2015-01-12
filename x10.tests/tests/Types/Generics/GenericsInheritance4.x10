/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;

public class GenericsInheritance4 extends x10Test {

    abstract static class AbstractSubMapIterator[K, V]{K <: Comparable[K]} {
    }

    abstract static class AscendingSubMapIterator[K, V]{K <: Comparable[K]} extends AbstractSubMapIterator[K, V] {
    }

    public def run():Boolean {
        return true;
    }

    public static def main(args: Rail[String]):void {
        new GenericsInheritance4().execute();
    }

}
