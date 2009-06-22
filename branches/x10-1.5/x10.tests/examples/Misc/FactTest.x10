/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing factorial
 */
public class FactTest extends x10Test {

    public def run() = fact(5) == 120;
    public def fact(var v: Int):Int = v <=1 ? 1 : v*fact(v-1);
    public static def main(var args: Rail[String]): void = {
	new FactTest().execute();
    }
}
