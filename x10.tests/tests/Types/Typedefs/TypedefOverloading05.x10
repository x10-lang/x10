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

/**
 * A compilation unit may have one or more type definitions or class
 * or interface declarations with the same name, as long as the
 * definitions have distinct parameters according to the method
 * overloading rules (9.7.1).
 *
 * As the two definitions of Int demonstrate, type definitions may be
 * overloaded: two type definitions with different numbers of type
 * parameters or with different types of value parameters, according
 * to the method overloading rules (9.7.1), define distinct types.
 *
 * @author bdlucas 9/2008
 */

public class TypedefOverloading05 extends TypedefTest {

        static class X {}
        static class Y[T] {}
        static class Z[T,U] {}

        static class U {}
        static class V(i:int) {def this(i:int):V{self.i==i} = property(i);}
        static class W(i:int,s:String) {def this(i:int,s:String):W{self.i==i&&self.s==s} = property(i,s);}

        static type A = X;
        static type A/*B*/[T] = Z[T,T];
        static type A/*C*/[T,U] = Z[T,U];

    public def run(): boolean = {
        
        a1:A = new A();
        a2:A/*B*/[int] = new Z[int,int]();
        a3:A/*C*/[int,String] = new Z[int,String]();

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefOverloading05().execute();
    }
}
