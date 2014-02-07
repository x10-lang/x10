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
 * Checks that an inner class can be qualified by a generic instantiation
 * of an outer class, and properly captures the type parameters of that
 * instance.
 */
public class GenericInnerClass[A] extends GenericTest {

    public class Inner[B] { }

    public def make[C]() = new Inner[C]();

    public static def foo() = new GenericInnerClass[Long]().make[Double]();

    public def run() = {
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericInnerClass[Any]().execute();
    }
}
