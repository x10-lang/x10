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
	public def run(): boolean = {
		var d: Dist{rect&&zeroBased&&rank==1} = Dist.makeBlock();
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new Block2().execute();
	}


}
