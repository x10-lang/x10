/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

/**
 * Job submitted to thread pool
 * @author tardieu
 */
value Job(activity: Activity, place: Place) {
	def this(activity: Activity, place: Place) = property(activity, place);
}
