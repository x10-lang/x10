/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import sor.*;
import jgfutil.*;
import harness.x10Test;

/**
 * X10 port of jgfutil from Java Grande Forum Benchmark Suite (Version 2.0).
 *
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 *
 * No porting issues identified.
 */
public class JGFSORBenchSizeA extends x10Test {

	public boolean run() {
		JGFInstrumentor.printHeader(2, 0);
		JGFSORBench sor = new JGFSORBench();
		sor.JGFrun(0);
		return true;
	}

	public static void main(String[] args) {
		new JGFSORBenchSizeA().execute();
	}
}

