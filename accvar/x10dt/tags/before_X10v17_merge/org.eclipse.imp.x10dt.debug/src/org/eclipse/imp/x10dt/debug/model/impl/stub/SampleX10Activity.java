package org.eclipse.imp.x10dt.debug.model.impl.stub;

import java.util.HashSet;
import java.util.Stack;
import java.util.Vector;

import org.eclipse.debug.core.model.IThread;
import org.eclipse.imp.x10dt.debug.model.IX10Activity;
import org.eclipse.imp.x10dt.debug.model.IX10Clock;
import org.eclipse.imp.x10dt.debug.model.IX10Place;
import org.eclipse.imp.x10dt.debug.model.IX10StackFrame;
import org.eclipse.imp.x10dt.debug.model.IX10Activity.X10ActivityState;

public class SampleX10Activity implements IX10Activity {

	private IX10Place _place;
	private IX10Activity _parent;
	private HashSet<IX10Clock> _clocks;
	private HashSet<IX10Activity> _children;
	private X10ActivityState _state;
	private String _name;
	private Stack<IX10StackFrame> _stackFrames;

	public SampleX10Activity(String name, IX10Activity parent, IX10Place place, X10ActivityState state) {
		_name = name;
		_parent = parent;
		_clocks = new HashSet<IX10Clock>();
		_children = new HashSet<IX10Activity>();
		_place = place;
		_state = state;
		_stackFrames = new Stack<IX10StackFrame>();
		if (parent != null) {
			((SampleX10Activity) parent).addChild(this);
		}
	}
	public SampleX10Activity(String name, IX10Activity parent, IX10Place place) {
		this(name, parent, place, X10ActivityState.Running);
	}
	
	public String getName() {
		return _name;
	}
	public IX10Clock blockedOn() {
		// no blocked activities in sample model
		return null;
	}
	public void addClock(IX10Clock clock) {
		_clocks.add(clock);
	}

	public IX10Clock[] getClocks() {
		return _clocks.toArray(new IX10Clock[_clocks.size()]);
	}

	public IX10Activity[] getFinishChildren() {
		return _children.toArray(new IX10Activity[_children.size()]);
	}

	public IX10Activity getFinishParent() {
		return _parent;
	}

	public IX10Place getPlace() {
		return _place;
	}

	public X10ActivityState getRunState() {
		return _state;
	}

	public IX10StackFrame[] getStackFrames() {
		// TODO create stack frames in sample model
		return _stackFrames.toArray(new IX10StackFrame[_stackFrames.size()]);
	}
	
	public void pushStackFrame(IX10StackFrame stackFrame) {
		_stackFrames.push(stackFrame);
	}
	
	public IX10StackFrame popStackFrame() {
		return _stackFrames.pop();
	}
	
	public void addChild(IX10Activity activity) {
		_children.add(activity);
	}
	
	public void setThread (IThread thread) {
		for (IX10StackFrame sf: _stackFrames) {
			((SampleX10StackFrame) sf).setThread(thread);
		}
	}
}
