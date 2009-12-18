/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 */
public class SubtypeCheckForUserDefinedConversion_MustFailCompile extends x10Test {
    static class Foo {}
    public static operator (p:ValRail[Int]) = new Foo();
    def run()=true;
    public static def main(Rail[String]) {
	new SubtypeCheckForUserDefinedConversion_MustFailCompile().execute();
    }
}
