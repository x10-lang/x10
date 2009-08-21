package org.eclipse.imp.x10dt.debug.model.impl;

import org.eclipse.debug.core.model.DebugElement;
import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.imp.x10dt.debug.Activator;
import org.eclipse.imp.x10dt.debug.model.IX10Activity;
import org.eclipse.imp.x10dt.debug.model.IX10Place;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;

public class X10Place extends X10DebugElement implements IX10Place {
	private String fName;
	HashSet<IX10Activity> _activeActivities = new HashSet();
	HashSet<IX10Activity> _queuedActivities;
	
	public X10Place(JDIDebugTarget target, String name) {
		super(target);
		fName=name;
	}
	
	public String geName() {
		return fName;
	}
	
	public void addActiveActivity(IX10Activity activity) {
		_activeActivities.add(activity);
	}
	
	public void addActivity(IX10Activity activity) {
		_queuedActivities.add(activity);
	}
	
	public IX10Activity[] getActiveActivities() {
		return _activeActivities.toArray(new IX10Activity[_activeActivities.size()]);
	}
	public IX10Activity[] getActivities() {
		ArrayList<IX10Activity> activities = new ArrayList<IX10Activity>(_activeActivities);
		activities.addAll(_queuedActivities);
		return activities.toArray(new IX10Activity[activities.size()]);
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
}