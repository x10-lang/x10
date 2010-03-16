package org.eclipse.imp.x10dt.debug.model;

/**
 * X10DT Debugger Model Interface
 * 
 * @author mmk
 * @since 10/10/08
 */
public interface IX10Region {
	int[] getDimensions(); // array of form [<min d0> <max d0> <min d1> <max d1> ... <min d.k-1>  <max d.k-1>] 
}
