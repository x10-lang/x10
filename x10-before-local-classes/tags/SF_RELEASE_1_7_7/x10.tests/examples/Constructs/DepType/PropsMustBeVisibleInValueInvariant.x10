/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Checks that properties i and j can be used in the invariant for the class for a value class.
 *
 * @author  vj
 */
public class PropsMustBeVisibleInValueInvariant extends x10Test {

    static value Value2(i:int, j:int){i==j}  {
        public def this(k:int):Value2{self.i==k} = {
            property(k,k);
        }
    }
    public def run() = {
        new Value2(4);
        return true;
    }
    public static def main(Rail[String]): void = {
        new PropsMustBeVisibleInValueInvariant().execute();
    }
}
