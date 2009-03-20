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
 * @author pvarma, vj
 */
public value ValueProp1(int i, int j : i == j) extends x10Test {

	public ValueProp1(int k) {
	    property(k,k);
	}
	public boolean run() {
	    return true;
	}
	public static void main(String[] args) {
		new ValueProp1(2).execute();
	}
}


