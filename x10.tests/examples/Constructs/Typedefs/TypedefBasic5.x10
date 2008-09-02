// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * Basic typdefs and type equivalence.
 *
 * @author bdlucas 9/2008
 */

public class TypedefBasic5 extends TypedefTest {

    public def run(): boolean = {
        
        type A(i:int) = int{self>=i};
        a:A(1) = 1;

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefBasic5().execute();
    }
}
