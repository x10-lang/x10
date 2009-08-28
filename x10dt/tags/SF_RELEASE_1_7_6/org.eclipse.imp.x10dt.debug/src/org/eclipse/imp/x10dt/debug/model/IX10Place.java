package org.eclipse.imp.x10dt.debug.model;

import org.eclipse.imp.x10dt.debug.model.IX10Activity;

/**
 * X10DT Debugger Model Interface
 * 
 * @author mmk
 * @since 10/10/08
 */
public interface IX10Place {
	String getName();
	IX10Activity[] getActivities();
	IX10Activity[] getActiveActivities(); // activities at place not blocked;  possibly unnecessary: could be derived from getActivites, and getRunState() on each
}
