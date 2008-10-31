/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * The x10.lang.perf class provides an interface to X10 programmers for custom definitions
 * of abstract execution metrics
 */
package x10.lang;

public final class perf {
	public static void addLocalOps(long n) { 
		x10.lang.Runtime.getCurrentActivity().addLocalOps(n); 
		x10.lang.Runtime.getCurrentActivity().addCritPathOps(n); 
	}
}
