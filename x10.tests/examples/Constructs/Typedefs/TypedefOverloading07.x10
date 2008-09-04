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

public class TypedefOverloading07 extends TypedefTest {

    public def run(): boolean = {
        
        class X {}
        class Y[T] {}
        class Z[T,U] {}

        class U {}
        class V(i:int) {def this(i:int):V{self.i==i} = property(i);}
        class W(i:int,s:String) {def this(i:int,s:String):W{self.i==i&&self.s==s} = property(i,s);}

        type A = int;
        type A/*B*/(i:int) = int{self>=i};
        type A/*C*/(s:String) = String{self==s};
        type A/*D*/(i:int,s:String) = W{self.i==i && self.s==s};
        type A/*E*/(s:String,i:int) = W{self.i==i && self.s==s};
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

