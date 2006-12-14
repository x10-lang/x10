/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The test checks that property syntax is accepted.
 *
 * @author pvarma
 */
public class Prop1(int i, int j : i == j) extends x10Test {

	public Prop1(int k) {
	    this.i=k; this.j=k;
	}
	public boolean run() {
	    return true;
	}
	public static void main(String[] args) {
		new Prop1(2).execute();
	}
}


