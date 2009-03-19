/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Comparing objects should not be rewritten to boxed calls.
 * Distilled from the old CompilerNullPointerException test.
 *
 * @author Igor Peshansky
 */
public class ObjectEquality extends x10Test {

	var objField: Object;

	public def run(): boolean = {
		val obj: Object = new Ref();
		if (obj == objField)
			return false;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ObjectEquality().execute();
	}
}
