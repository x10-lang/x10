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
import x10.util.Pair;

class CoalescingPairIterator[K,V,I]  { I <: Iterable[V] }  {
    val iter : Iterator[Pair[K,I]] = null;
    var inner : Iterator[V] = null;

    public def advance():Boolean {        
        while (iter.hasNext()) {
            val pair = iter.next();
            inner = pair.second.iterator();
            return true;
        }
        return false;
    }
}    

interface ValueList[V] extends Iterable[V]  {
    public def add(V) :Boolean;
}


/**
 * Test case derived from M3R code. In Native X10,
 * if a type parameter is bound by an interface type, then
 * we need to cast the receiver of interface calls up to the
 * type bound to ensure the appropriate overloaded template
 * in the interface is invoked.
 *
 * Test case is intended to test C++ codegen, so successful 
 * compilation is sufficient for now.
 */
public class GenericIterableTest  extends x10Test {

    public def run():Boolean = true;

    def doit(x:CoalescingPairIterator[String,String,ValueList[String]]) = x.advance();

    public static def main(Rail[String]) {
        new GenericIterableTest().execute();
    }
}
