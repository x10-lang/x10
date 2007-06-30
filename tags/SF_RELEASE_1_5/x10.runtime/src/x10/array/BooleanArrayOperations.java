/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on 13.09.2006
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package x10.array;

import x10.lang.BooleanReferenceArray;
import x10.lang.point;

/**
 * Helper routines for boolean array manipulation.
 * This class contains only static methods, and is not intended
 * to be instantiated or subclassed.
 * 
 * @author igor
 */
public final class BooleanArrayOperations {

	protected BooleanArrayOperations() { }

	public static boolean bitAndSet(BooleanReferenceArray a, boolean v, point/*(region)*/ p) {
		return a.set(a.get(p)&v,p);
	}
	public static boolean bitAndSet(BooleanReferenceArray a, boolean v, int p) {
		return a.set(a.get(p)&v,p);
	}
	public static boolean bitAndSet(BooleanReferenceArray a, boolean v, int p, int q) {
		return a.set(a.get(p,q)&v,p,q);
	}
	public static boolean bitAndSet(BooleanReferenceArray a, boolean v, int p, int q, int r) {
		return a.set(a.get(p,q,r)&v,p,q,r);
	}
	public static boolean bitAndSet(BooleanReferenceArray a, boolean v, int p, int q, int r, int s) {
		return a.set(a.get(p,q,r,s)&v,p,q,r,s);
	}
	public static boolean bitOrSet(BooleanReferenceArray a, boolean v, point/*(region)*/ p) {
		return a.set(a.get(p)|v,p);
	}
	public static boolean bitOrSet(BooleanReferenceArray a, boolean v, int p) {
		return a.set(a.get(p)|v,p);
	}
	public static boolean bitOrSet(BooleanReferenceArray a, boolean v, int p, int q) {
		return a.set(a.get(p,q)|v,p,q);
	}
	public static boolean bitOrSet(BooleanReferenceArray a, boolean v, int p, int q, int r) {
		return a.set(a.get(p,q,r)|v,p,q,r);
	}
	public static boolean bitOrSet(BooleanReferenceArray a, boolean v, int p, int q, int r, int s) {
		return a.set(a.get(p,q,r,s)|v,p,q,r,s);
	}
	public static boolean bitXorSet(BooleanReferenceArray a, boolean v, point/*(region)*/ p) {
		return a.set(a.get(p)^v,p);
	}
	public static boolean bitXorSet(BooleanReferenceArray a, boolean v, int p) {
		return a.set(a.get(p)^v,p);
	}
	public static boolean bitXorSet(BooleanReferenceArray a, boolean v, int p, int q) {
		return a.set(a.get(p,q)^v,p,q);
	}
	public static boolean bitXorSet(BooleanReferenceArray a, boolean v, int p, int q, int r) {
		return a.set(a.get(p,q,r)^v,p,q,r);
	}
	public static boolean bitXorSet(BooleanReferenceArray a, boolean v, int p, int q, int r, int s) {
		return a.set(a.get(p,q,r,s)^v,p,q,r,s);
	}
}
