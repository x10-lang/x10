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
 * @author bdlucas 10/2008
 */

class XTENLANG_23 extends x10Test {

    class P {}
    
    interface S[A,B] {
        def set(A,B): void;
    }
    
    abstract class AA[T] implements S[T,P] {
        public abstract def set(T, P): void;
        public abstract def set(T, int): void;
    }
    
    abstract class BA[T] extends AA[T] implements S[T,P] {
        public final def set(T, P):void = {}
    }
    
    abstract class VA[T] extends BA[T] {}
    
    final class A1[T] extends VA[T] {
        final public def set(T, int): void {}
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_23().execute();
    }
}
