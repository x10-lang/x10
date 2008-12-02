/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * @author Christian Grothoff
 */
public class ValueEquality extends x10Test {
	public boolean run() {
		V v1 = new V(1);
		V v2 = new V(1);
		return v1 == v2;
	}

	public static void main(String[] args) {
		new ValueEquality().execute();
	}

	static value V extends x10.lang.Object {
		int v;
		V(int i) {
			this.v = i;
		}
	}
}

