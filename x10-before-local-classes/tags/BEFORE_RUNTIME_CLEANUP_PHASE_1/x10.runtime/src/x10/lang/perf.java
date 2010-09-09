/**
 * The x10.lang.perf class provides an interface to X10 programmers for custom definitions
 * of abstract execution metrics
 */
package x10.lang;

public final class perf {
	public static void addLocalOps(long n) { 
		x10.lang.Runtime.getCurrentActivity().addLocalOps(n); 
	}
}
