// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * A compilation unit may have one or more type definitions or class
 * or interface declarations with the same name, as long as the
 * definitions have distinct parameters according to the method
 * overloading rules (ยง9.7.1).
 *
 * As the two definitions of Int demonstrate, type definitions may be
 * overloaded: two type definitions with different numbers of type
 * parameters or with different types of value parameters, according
 * to the method overloading rules (ง9.7.1), define distinct types.
 *
 * @author bdlucas 9/2008
 */

public class TypedefOverloading10 extends TypedefTest {

    interface A/*E*/(s:String,i:int) {}

    public def run(): boolean = {
        
        class X {}
        class Y[T] {}
        class Z[T,U] {}

        class U {}
        class V(i:int) {def this(i:int):V{self.i==i} = property(i);}
        class W(i:int,s:String) {def this(i:int,s:String):W{self.i==i&&self.s==s} = property(i,s);}

        type A = V{self.i==1};
        type A/*B*/(i:int) = V{self.i==i};
        type A/*C*/(s:String) = W{self.i==1 && self.s==s};
        type A/*D*/(i:int,s:String) = W{self.i==i && self.s==s};
        a1:A = new A();
        a1 = new V(1);
        a2:A/*B*/(1) = a1;
        a3:A/*C*/("1") = new W(1,"1");
        a4:A/*D*/(1,"1") = a3;
        class Foo(i:int,s:String) implements A/*E*/("1",1) {def this(i:int,s:String):Foo{self.i==i&&self.s==s=property(i,s);}
        a5:A/*E*/("1",1) = new Foo(1,"1");

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefOverloading10().execute();
    }
}

