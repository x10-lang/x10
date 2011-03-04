package org.eclipse.imp.x10dt.debug.model.impl;

import java.util.HashSet;

import org.eclipse.imp.x10dt.debug.model.IX10Activity;
import org.eclipse.imp.x10dt.debug.model.IX10Application;
import org.eclipse.imp.x10dt.debug.model.IX10Clock;
import org.eclipse.imp.x10dt.debug.model.IX10Place;

public abstract class X10Application implements IX10Application {
	
	public IX10Clock[] getClocks() {
		// TODO Auto-generated method stub
		return null;
	}

	public IX10Activity getFinishTreeRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	public IX10Place[] getPlaces() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public IX10Activity[] getActivities() {
		HashSet<IX10Activity> activities = new HashSet<IX10Activity>();
		collectActivities(activities, getFinishTreeRoot());
		return activities.toArray(new IX10Activity[activities.size()]);
	}
	private static void collectActivities(HashSet<IX10Activity> activities, IX10Activity activity) {
		activities.add(activity);
		for (IX10Activity a: activity.getFinishChildren()) {
			collectActivities(activities, a);
		}
	}

}
