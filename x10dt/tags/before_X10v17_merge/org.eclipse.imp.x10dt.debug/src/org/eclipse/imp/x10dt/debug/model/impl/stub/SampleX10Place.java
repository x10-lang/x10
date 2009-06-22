package org.eclipse.imp.x10dt.debug.model.impl.stub;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.imp.x10dt.debug.model.IX10Activity;
import org.eclipse.imp.x10dt.debug.model.IX10Place;

public class SampleX10Place implements IX10Place {

	private String _name;
	HashSet<IX10Activity> _activeActivities;
	HashSet<IX10Activity> _blockedActivities;

	public SampleX10Place(String name) {
		_name = name;
		_activeActivities = new HashSet<IX10Activity>();
		_blockedActivities = new HashSet<IX10Activity>();
	}
	
	public void AddActivity(IX10Activity activity) {
		_activeActivities.add(activity);
	}

	public boolean block(IX10Activity activity) {
		if (_activeActivities.contains(activity)) {
			_blockedActivities.add(activity);
			_activeActivities.remove(activity);
			return true;
		}
		return false;
	}
	public boolean unblock(IX10Activity activity) {
		if (_blockedActivities.contains(activity)) {
			_blockedActivities.remove(activity);
			_activeActivities.add(activity);
			return true;
		}
		return false;
	}
	public IX10Activity[] getActiveActivities() {
		return _activeActivities.toArray(new IX10Activity[_activeActivities.size()]);
	}

	public IX10Activity[] getActivities() {
		ArrayList<IX10Activity> activities = new ArrayList<IX10Activity>(_activeActivities);
		activities.addAll(_blockedActivities);
		return activities.toArray(new IX10Activity[activities.size()]);
	}

	public String getName() {
		return _name;
	}
}
