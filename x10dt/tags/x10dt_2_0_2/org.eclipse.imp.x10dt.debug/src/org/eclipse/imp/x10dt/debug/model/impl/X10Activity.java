package org.eclipse.imp.x10dt.debug.model.impl;


import java.util.HashSet;

import org.eclipse.imp.x10dt.debug.model.IX10Activity;
import org.eclipse.imp.x10dt.debug.model.IX10Clock;
import org.eclipse.imp.x10dt.debug.model.IX10Place;
import org.eclipse.imp.x10dt.debug.model.IX10StackFrame;


public class X10Activity extends X10DebugElement implements IX10Activity{
	
	private IX10Place fPlace;
	private IX10Activity fParent;
	private HashSet<IX10Clock> fClocks;
	private HashSet<IX10Activity> fChildren;
	private X10ActivityState _state;
	private String fName;

	private static int seq=0;
    public int uid;
	
	
	public X10Activity(X10DebugTargetAlt target) {
	       	super(target);
			synchronized(this) {
				uid = seq++;
			}
	}
	
	public X10Activity(X10DebugTargetAlt target, String name) {
       	super(target);
       	fName=name;
		synchronized(this) {
			uid = seq++;
		}
}
	
	/*
	public X10Activity(String name,IX10Activity parent, IX10Place place){
		fName=name;
		fPlace=place;
		init();
	}
	*/
	
	private void init() {
		
	}
	
	public String getName() {
		return fName;
	}
	public IX10Clock[] getClocks() {
		return fClocks.toArray(new IX10Clock[fClocks.size()]);
	}

	public IX10Activity[] getFinishChildren() {
		return fChildren.toArray(new IX10Activity[fChildren.size()]);
	}

	public IX10Activity getFinishParent() {
		return fParent;
	}

	public IX10Place getPlace() {
		return fPlace;
	}
	
	//public String getModelIdentifier() {
		//return X10DebugModel.getPluginIdentifier()+".model";
	//}
	
	public X10ActivityState getRunState(){
		return X10ActivityState.Running;
	}
	
	public IX10StackFrame[] getStackFrames() {
		return new IX10StackFrame[0];
	}
	public String toString() {
		return getName();
	}
}