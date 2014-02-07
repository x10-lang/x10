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

public class TypedefOverloading07 extends TypedefTest {

        
        static class X {}
        static class Y[T] {}
        static class Z[T,U] {}

        static class U {}
        static class V(i:long) {def this(i:long):V{self.i==i} = property(i);}
        static class W(i:long,s:String) {def this(i:long,s:String):W{self.i==i&&self.s==s} = property(i,s);}

        static type A = long;
        static type A/*B*/(i:long) = long{self==i};
        static type A/*C*/(s:String) = String{self==s};
        static type A/*D*/(i:long,s:String) = W{self.i==i && self.s==s};
        static type A/*E*/(s:String,i:long) = W{self.i==i && self.s==s};

    public def run(): boolean = {

        a1:A = 1;
        a2:A/*B*/(1) = 1;
        a3:A/*C*/("1") = "1";
        a4:A/*D*/(1,"1") = new W(1,"1");
        a5:A/*E*/("1",1) = a4;

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefOverloading07().execute();
    }
}
