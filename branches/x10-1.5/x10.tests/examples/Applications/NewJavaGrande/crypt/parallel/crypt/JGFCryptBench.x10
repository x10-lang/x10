/**************************************************************************
*                                                                         *
*             Java Grande Forum Benchmark Suite - Version 2.0             *
*                                                                         *
*                            produced by                                  *
*                                                                         *
*                  Java Grande Benchmarking Project                       *
*                                                                         *
*                                at                                       *
*                                                                         *
*                Edinburgh Parallel Computing Centre                      *
*                                                                         *
*                email: epcc-javagrande@epcc.ed.ac.uk                     *
*                                                                         *
*                                                                         *
*      This version copyright (c) The University of Edinburgh, 1999.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/
package crypt;

import jgfutil.*;
import x10.io.Console;

public class JGFCryptBench extends IDEATest implements JGFSection2 {

	private var size: int;
	private val datasizes  = [ 30000, 20000000, 50000000 ];

	public def JGFsetsize(var size: int): void = {
		this.size = size;
	}

	public def JGFinitialise(): void = {
		array_rows = datasizes(size);
		buildTestData();
	}

	public def JGFkernel(): void = {
		Do();
	}

	public def JGFvalidate(): void = {
		for (var i: int = 0; i < array_rows; i++) {
			if (plain1(i) != plain2(i)) {
				Console.OUT.println("Validation failed");
				Console.OUT.println("Original Byte " + i + " = " + plain1(i));
				Console.OUT.println("Encrypted Byte " + i + " = " + crypt1(i));
				Console.OUT.println("Decrypted Byte " + i + " = " + plain2(i));
				throw new Error("Validation failed");
				//break;
			}
		}
	}

	public def JGFtidyup(): void = {
		freeTestData();
	}

	public def JGFrun(var size: int): void = {
		JGFInstrumentor.addTimer("Section2:Crypt:Kernel", "Kbyte", size);

		JGFsetsize(size);
		JGFinitialise();
		JGFkernel();
		JGFvalidate();
		JGFtidyup();

		JGFInstrumentor.addOpsToTimer("Section2:Crypt:Kernel", (2*array_rows)/1000.);
		JGFInstrumentor.printTimer("Section2:Crypt:Kernel");
	}
}
