package org.eclipse.imp.x10dt.debug.model.impl.stub;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.imp.x10dt.debug.model.IX10Activity;
import org.eclipse.imp.x10dt.debug.model.IX10Clock;
import org.eclipse.imp.x10dt.debug.model.IX10Place;
import org.eclipse.imp.x10dt.debug.model.impl.X10Application;

public class SampleX10Application extends X10Application {
	
	private IX10Activity _finishRoot;

	public SampleX10Application(IX10Activity finishRoot) {
		_finishRoot = finishRoot;
	}
	public IX10Activity getFinishTreeRoot() {
		return _finishRoot;
	}

	public IX10Clock[] getClocks() {
		HashSet<IX10Clock> clocks = new HashSet<IX10Clock>();
		collectClocks(clocks, _finishRoot);
		return clocks.toArray(new IX10Clock[clocks.size()]);
	}
	
	public IX10Place[] getPlaces() {
		HashSet<IX10Place> places = new HashSet<IX10Place>();
		collectPlaces(places, _finishRoot);
		return places.toArray(new IX10Place[places.size()]);
	}

	private static void collectClocks(HashSet<IX10Clock> clocks, IX10Activity activity) {
		for (IX10Clock c: activity.getClocks()) {
			clocks.add(c);
		}
		for (IX10Activity a: activity.getFinishChildren()) {
			collectClocks(clocks, a);
		}
	}
	
	private static void collectPlaces(HashSet<IX10Place> places, IX10Activity activity) {
		places.add(activity.getPlace());
		for (IX10Activity a: activity.getFinishChildren()) {
			collectPlaces(places, a);
		}
	}

}
