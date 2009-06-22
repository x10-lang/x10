/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//The current release does not implement the check that for every constructor
// with a defined return type, every path to the exit node contains
// an invocation of property that is strong enough to entail the return type.

import harness.x10Test;

/**
 * Test that the compiler detects a situation in which one branch of a conditional has
 * a property clause but not another.
 *
 * @author vj
 */
public class PropertyNotInvoked2_MustFailCompile extends x10Test {

    static class Tester(i: int(2)) {
        public def this(arg:int(2)):Tester{self.i==arg} {
            if (arg == 2) {
                property(arg);
            } else {
                i=2;
            }
        }
    }

    public def run(): boolean = {
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new PropertyNotInvoked2_MustFailCompile().execute();
    }
}
