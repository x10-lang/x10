/*
 * Created on Oct 3, 2004
 */
package x10.lang;

import java.util.Set;

/**
 * Implementation of Distributions. Some of this code 
 * may later go into x10.runtime, but for the moment and the 
 * sake of simplicity, we leave it here.
 * 
 * @author Christoph von Praun
 */

public abstract class Distribution extends Region {

	public static Distribution unique(Set p) {
		throw new Error("TODO");
	}
	
	public static Distribution constant(Place p) {
		throw new Error("TODO");
	}
	
	public static Distribution block(Region r, Set p) {
		throw new Error("TODO");
	}
	
	public static Distribution cyclic(Region r, Set p) {
		throw new Error("TODO");
	}
	
	public static Distribution blockcyclic(Region r, Set p, int bsize) {
		throw new Error("TODO");
	}
	
	private Distribution(int[] dims) {
		super(dims);
		throw new Error("TODO");
	}
	
	private Distribution(Range[] dims) {
		super(dims);
		throw new Error("TODO");
	}

}