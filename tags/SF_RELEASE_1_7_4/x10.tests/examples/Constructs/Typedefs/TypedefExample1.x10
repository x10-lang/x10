// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * Example from spec. If changes need to be made to this code to make
 * it a valid example, update the spec accordingly.
 *
 * @author bdlucas 8/2008
 */

public class TypedefExample1 extends TypedefTest {

    static class Set[T] { }
    static class Map[K,V] { }
    static class List[T] { }

    public def run(): boolean = {
        
        type StringSet = Set[String];
        type MapToList[K,V] = Map[K,List[V]];
        // type Nat = Int{self>=0};
        type Zero = Int{self==0};
        type Int(x: Int) = Int{self==x};
        // type Int(lo: Int, hi: Int) = Int{lo <= self, self <= hi};
        type Int(lo: Int, hi: Int) = Int{lo == self, self == hi};

        // XXX just syntax and type check for now

        return result;
    }



    public static def main(var args: Rail[String]): void = {
        new TypedefExample1().execute();
    }
}
