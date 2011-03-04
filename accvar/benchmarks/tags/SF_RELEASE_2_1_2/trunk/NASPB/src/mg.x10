/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/*
 * NAS Parallel Benchmarks 3.0
 *
 * CG
 */
import NPB3_0_X10.*;

// import harness.x10Test;

public class mg  {

	public def run(): boolean = {
		MG.entryPoint(["-np8", "CLASS=S"]);
		return true;
	}

	public static def main(args: Array[String](1)): void  {
		new mg().run();
	}
}
