package org.eclipse.imp.x10dt.debug.model.impl.stub;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.imp.x10dt.debug.model.IX10Activity;
import org.eclipse.imp.x10dt.debug.model.IX10Clock;

public class SampleX10Clock implements IX10Clock {

	private Set<IX10Activity> _activities = null;
	private String _name;

	public SampleX10Clock(String name) {
		super();
		_name = name;
	}
	public void addActivity(IX10Activity activity) {
		if (_activities==null) {
			_activities = new HashSet<IX10Activity>();
			_activities.add(activity);
		}
		_activities.add(activity);
	}

	public IX10Activity[] getActivities() {
		return _activities.toArray(new IX10Activity[_activities.size()]);
	}

	public IX10Activity[] getWaitingActivities() {
		// no waiting activities in sample model
		return new IX10Activity[0];
	}
	
	public String getName() {
		return _name;
	}
	
}
