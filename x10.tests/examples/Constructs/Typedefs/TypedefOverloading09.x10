// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

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

public class TypedefOverloading09 extends TypedefTest {

        static class X {}
        static class Y[T] {}
        static class Z[T,U] {}

        static class U {}
        static class V(i:int) {def this(i:int):V{self.i==i} = property(i);}
        static class W(i:int,s:String) {def this(i:int,s:String):W{self.i==i && self.s==s} = property(i,s);}

        static type A = X;
        static type A/*B*/(i:int) = int{self==i};
        static type A/*C*/(s:String) = W{self.i==1 && self.s==s};
        static type A/*D*/(i:int,s:String) = W{self.i==i && self.s==s};
        static type A/*E*/(s:String,i:int) = W{self.s==s && self.i==i};

    public def run(): boolean = {
        
        a1:A = new A();
        a2:A/*B*/(1) = 1;
        a3:A/*C*/("1") = new W(1,"1");
        a4:A/*D*/(1,"1") = a3;
        a5:A/*E*/("1",1) = a3;
        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefOverloading09().execute();
    }
}
