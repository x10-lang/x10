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

/**
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Classes implementing interfaces with properties must also
 * define the same properties, or else an error is thrown
 *
 * @author raj
 */
public class InterfaceProp_MustFailCompile extends x10Test {

    interface I {
        public property i(): Int;
        public def a(): void;
    }
    interface J extends I {
        public property k(): Int;
        public def a(): void;
    }
    class E(k:Int) implements J { // ERR: InterfaceProp_MustFailCompile.E should be declared abstract; it does not define i(): x10.lang.Int, which is declared in InterfaceProp_MustFailCompile.I
        public property k() = k;
        public def this(kk:Int) {
            property(kk);
        }
        public def a(): void {
            val x:Int;
        }
    }

    public def run(): Boolean = {
        new E(1n);
        return true;
    }
    public static def main(args: Rail[String]): void = {
        new InterfaceProp_MustFailCompile().execute();
    }
}
