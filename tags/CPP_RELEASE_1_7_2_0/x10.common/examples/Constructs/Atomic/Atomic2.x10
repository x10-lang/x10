/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * atomic enclosing a void function call
 * that throws an exception.
 *
 * @author kemal 4/2005
 */
public class Atomic2 extends x10Test {

	int x = 0;


	public boolean run() {
		finish async(this) atomic x++;
		atomic chk(x == 1);

		boolean gotException = false;
		try {
			atomic chk(x == 0);
		} catch (Throwable e) {
			gotException = true;
		}
		chk(gotException);
		return true;
	}

	public static void main(String[] args) {
		new Atomic2().execute();
	}
}

