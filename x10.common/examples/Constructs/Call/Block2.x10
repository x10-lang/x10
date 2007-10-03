/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that the dist.block method propagates region properties from arg to result
 */
public class Block2 extends x10Test {
	public boolean run() {
		dist(:rect&&zeroBased&&rank==1) d = dist.factory.block();
		return true;
	}

	public static void main(String[] args) {
		new Block2().execute();
	}


}

