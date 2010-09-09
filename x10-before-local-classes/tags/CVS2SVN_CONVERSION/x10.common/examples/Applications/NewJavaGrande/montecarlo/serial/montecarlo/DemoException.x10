/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
package montecarlo;

/**
 * X10 port of montecarlo benchmark from Section 2 of Java Grande Forum Benchmark Suite (Version 2.0).
 *
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 *
 * Porting issues identified:
 */
public class DemoException extends java.lang.Exception {

	/**
	 * Flag for selecting whether to print the stack-trace dump.
	 */
	public static final boolean debug = true;

	/**
	 * Default constructor.
	 */
	public DemoException() {
		super();
		if (debug) {
			printStackTrace();
		}
	}

	/**
	 * Default constructor for reporting an error message.
	 */
	public DemoException(String s) {
		super(s);
		if (debug) {
			printStackTrace();
		}
	}

	/**
	 * Default constructor for reporting an error code.
	 */
	public DemoException(int ierr) {
		super(String.valueOf(ierr));
		if (debug) {
			printStackTrace();
		}
	}
}

