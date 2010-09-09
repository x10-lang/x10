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
public class Rail extends x10Test {

	public Rail() {
	  
	}
	public boolean run() {
		region(:rail) r = [0:10];
		dist(:rail) d = dist.factory.block(r);
		double[:rail] a = new double[d];
	    return true;
	}
	public static void main(String[] args) {
		new Rail().execute();
	}
}


