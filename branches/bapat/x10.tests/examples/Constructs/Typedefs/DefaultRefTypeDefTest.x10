// (C) Copyright IBM Corporation 2006, 2007, 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * 
 *
 * @author vj 09/2008
 */
public class DefaultRefTypeDefTest extends x10Test {

    static class Foo[T](n:int, s:T) {
       def this(n:int, s:T):Foo[T](n,s) = {
         property(n,s);
       }
    }
    public def run() = {
        x:Foo[String](2,"a") = new Foo[String](2,"a");
        true
    }

    public static def main(var args: Rail[String]): void = {
        new DefaultRefTypeDefTest().execute();
    }
}
