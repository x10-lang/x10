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
 * @author vj
 */

public class JGFSORBenchSizeA extends x10Test {     
    public def run() {
	JGFInstrumentor.printHeader(2, 0, Place.MAX_PLACES);       
	val sor = new JGFSORBench();       
	sor.JGFrun(0);
	return true;
    }
    public static def main(Rail[String])  {
	new JGFSORBenchSizeA().execute();
    }
}
