/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import testPackage.*;
import harness.x10Test;

/**
 * Testing if -classpath './x10lib' is recognized
 * testPackage is a package in ./x10lib
 *
 * @author kemal
 */
public class ClassPathTest extends x10Test {

	public def run(): boolean = {
		return T1.m1(49);
	}

	public static def main(var args: Rail[String]): void = {
		new ClassPathTest().execute();
	}
}
