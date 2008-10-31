/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks "side effect aware" checking code detects when a constraint is not meet.
 * Issue: The cast is not valid til constraints is not meet.
 * X10DepTypeSubClassOne(:p==2&&a==2) <-- X10DepTypeSubClassOne(:p==1&&a==1)
 * @author vcave
 **/
public class InstanceofDownCast2 extends x10Test {
	public boolean run() {
		return !(this.getX10DepTypeSubClassOne(1,1) instanceof X10DepTypeSubClassOne(:p==2&&a==2));
	}

	private x10.lang.Object getX10DepTypeSubClassOne(int c1, int c2) {
		return new X10DepTypeSubClassOne(c1, c2);
	}

	public static void main(String[] args) {
		new InstanceofDownCast2().execute();
	}
}
 
