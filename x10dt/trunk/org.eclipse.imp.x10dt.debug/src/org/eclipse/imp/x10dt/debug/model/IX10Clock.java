package org.eclipse.imp.x10dt.debug.model;

/**
 * X10DT Debugger Model Interface
 * 
 * @author mmk
 * @since 10/10/08
 */
public interface IX10Clock {
	String getName();
	IX10Activity[] getActivities();
	//IX10Activity[] getWaitingActivities();
}
