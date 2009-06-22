/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;


/**
 * Java and X10 permit a call to a method which returns a value to occur as a statement.
 * The returned value is discarded. However, Java does not permit a variable standing alone
 * as a statement. Thus the x10 compiler must check that as a result of flattening it does
 * not produce a variable standing alone. 
 */

public class FlattenInVoid extends x10Test {

    var a: Array[int](2);

    public def this(): FlattenInVoid = {
        a = Array.make[int](([1..10, 1..10] as Region)->here, (var p(i,j): Point): int => { return i+j;});
    }

    def m(var x: int): boolean = {
      return true;
    }
    
    public def run(): boolean = {
        m(a(1, 1));
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new FlattenInVoid().execute();
    }
}
