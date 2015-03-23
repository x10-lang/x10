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
 * Checks that type parameters of a deeply nested inner class are
 * captured properly, and that inner classes can be qualified by
 * instantiated outer classes.
 */
public class GenericDeepInnerClass[A] extends GenericTest {

    public class Inner[B] {
        public class InnerInner[C] {
            public class InnerInnerInner[D] { }
            public def make[E]() = new InnerInnerInner[E]();
        }
        public def make[F]() = new InnerInner[F]();
    }
    public def make[G]() = new Inner[G]();

    public static def foo():GenericDeepInnerClass[Long].Inner[Double].InnerInner[Long].InnerInnerInner[Char] = new GenericDeepInnerClass[Long]().make[Double]().make[Long]().make[Char]();

    public def run() {
        return true;
    }

    public static def main(var args: Rail[String]): void {
        new GenericDeepInnerClass[Any]().execute();
    }
}
