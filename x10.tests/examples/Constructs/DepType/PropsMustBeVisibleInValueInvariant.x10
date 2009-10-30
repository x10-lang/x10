/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Checks that properties i and j can be used in the invariant for the class for a struct
 *
 * @author  vj
 */
public class PropsMustBeVisibleInValueInvariant extends x10Test {

    static struct Value2(i:int, j:int){i==j}  {
        public def this(k:int):Value2{self.i==k} = {
            property(k,k);
        }
	public def typeName() = "Value2";
    }
    public def run() = {
        Value2(4);
        return true;
    }
    public static def main(Rail[String]): void = {
        new PropsMustBeVisibleInValueInvariant().execute();
    }
}
