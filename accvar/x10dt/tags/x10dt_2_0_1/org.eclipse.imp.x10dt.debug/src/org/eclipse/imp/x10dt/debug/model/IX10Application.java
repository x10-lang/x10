package org.eclipse.imp.x10dt.debug.model;

/**
 * X10DT Debugger Model Interface
 * 
 * @author mmk
 * @since 10/10/08
 */
public interface IX10Application {
	IX10Clock[] getClocks();
	IX10Place[] getPlaces();
	IX10Activity getFinishTreeRoot();
	IX10Activity[] getActivities();
}
