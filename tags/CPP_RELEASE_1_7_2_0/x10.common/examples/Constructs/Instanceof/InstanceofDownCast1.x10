/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks inlined instanceof checking code detect when a constraint is not meet.
 * Issue: The cast is not valid til constraints is not meet.
 * X10DepTypeSubClassOne(:p==2&&a==2) <-- X10DepTypeSubClassOne(:p==1&&a==2)
 * @author vcave
 **/
public class InstanceofDownCast1 extends x10Test {

	public boolean run() {
		x10.lang.Object upcast = new X10DepTypeSubClassOne(1,2);
		return !(upcast instanceof X10DepTypeSubClassOne(:p==2&&a==2));
	}
	
	public static void main(String[] args) {
		new InstanceofDownCast1().execute();
	}
}
 
