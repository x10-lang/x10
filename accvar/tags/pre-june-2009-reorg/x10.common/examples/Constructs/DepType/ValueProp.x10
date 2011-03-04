/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The test checks that property syntax is accepted for value classes.
 *
 * @author vj
 */
public value class ValueProp(int i, int j) extends x10Test {

	public ValueProp(int i, int j) {
	    property(i,j);
	}
	public boolean run() {
	    return true;
	}
	public static void main(String[] args) {
		new ValueProp(2,3).execute();
	}
}


