/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Program that throws an uncaught error was causing a deadlock
 * as of 5/26/2005.
 *
 * @author kemal 5/2005
 */
public class ExceptionBug_MustFailRun extends x10Test {

	int n = 0;
	public boolean run() {
		if (n == 0) throw new Error("Testing error");
		return true;
	}

	public static void main(String[] args) {
		new ExceptionBug_MustFailRun().execute();
	}
}

