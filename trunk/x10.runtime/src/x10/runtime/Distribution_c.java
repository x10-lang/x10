/*
 * Created on Oct 3, 2004
 */
package x10.runtime;

import java.util.Set;

import x10.lang.Place;
import x10.lang.Range;
import x10.lang.Distribution;
import x10.lang.Region;


/**
 * Implementation of Distributions. Some of this code 
 * may later go into x10.runtime, but for the moment and the 
 * sake of simplicity, we leave it here.
 * 
 * @author Christoph von Praun
 */

abstract class Distribution_c extends Region_c implements Distribution {

	public static Distribution_c unique(Set p) {
		throw new Error("TODO");
	}
	
	public static Distribution_c constant(Place p) {
		throw new Error("TODO");
	}
	
	public static Distribution_c block(Region r, Set p) {
		throw new Error("TODO");
	}
	
	public static Distribution_c cyclic(Region r, Set p) {
		throw new Error("TODO");
	}
	
	public static Distribution_c blockcyclic(Region r, Set p, int bsize) {
		throw new Error("TODO");
	}
	
	private Distribution_c(int[] dims) {
		super(dims);
		throw new Error("TODO");
	}
	
	private Distribution_c(Range[] dims) {
		super(dims);
		throw new Error("TODO");
	}

}