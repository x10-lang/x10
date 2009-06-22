package org.eclipse.imp.x10dt.debug.model.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.imp.x10dt.debug.model.IX10Activity;
import org.eclipse.imp.x10dt.debug.model.IX10Clock;
import org.eclipse.imp.x10dt.debug.model.IX10Place;
import org.eclipse.imp.x10dt.debug.model.IX10StackFrame;
import org.eclipse.imp.x10dt.debug.model.impl.jdi.X10DelegatingStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

import com.sun.jdi.Method;
import com.sun.jdi.ObjectCollectedException;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;

public class X10Thread extends JDIThread implements IThread, IX10Activity {
	
	private IX10Place fPlace;
	private IX10Activity fParent;
	private HashSet<IX10Clock> fClocks;
    private HashSet<IX10Activity> fChildren;
    private ObjectReference fActivity;
	
	/**
	 * Constructs a new thread group in the given target based on the underlying
	 * thread group reference.
	 *  
	 * @param target debug target
	 * @param group thread group reference
	 */
	/**
	 * Creates a new thread on the underlying thread reference
	 * in the given debug target.
	 * 
	 * @param target the debug target in which this thread is contained
	 * @param thread the underlying thread on the VM
	 * @exception ObjectCollectedException if the underlying thread has been
	 * garbage collected and cannot be properly initialized
	 */
	public X10Thread(JDIDebugTarget target, ThreadReference thread) throws ObjectCollectedException {
		super(target, thread);
		/**
		 * @see org.eclipse.debug.core.model.IDebugElement#getModelIdentifier()
		 */

	}
	public String getModelIdentifier() {
		return getDebugTarget().getModelIdentifier();
	}
	
	public ObjectReference getFreshActivityObject(ThreadReference t) {
		Value result=null;
		if (t.referenceType().name().equals("x10.runtime.PoolRunner")) {
			System.out.println("getFreshActivityObject: t is of type PoolRunner "+t.name());
			//if (t.name().contains("pool-0-thread-2")){
			
			List<Method> m = (List<Method>)((ObjectReference)t).referenceType().methodsByName("getActivity");
			System.out.println("Shivali:getFreshActivityObject() Method found "+m.size());
			Method meth=null;
			for (Method m1: m) {
				meth = m1; 
			}
			List args=Collections.EMPTY_LIST;
			
			result = ((X10DebugTargetAlt)(this.getDebugTarget())).invokeX10RTMethod((ObjectReference)t,meth, args);
			
			System.out.println("After invoking getActivity() method ");
			if (result==null){
				System.out.println("getFreshActivityObject returning null");
				return null;
			}
			if (result instanceof ObjectReference) {
				System.out.println("Shivali:getFreshActivityObject got Activity");
				fActivity = (ObjectReference)result;
			}
			return fActivity;
		}
		else {
			System.out.println("getFreshActivityObject: t not a PoolRunner");
			return null;
		}
	}
	
	public String getName() {
		if (fActivity!=null){
			System.out.println("X10Thread:getName() ");
			String place = this.getUnderlyingThread().name();
			if (place.contains("Main Activity"))
				return "ACTIVITY" + "@PLACE 0" + ": Main Activity";
			else {
			String aname = ((X10DebugTargetAlt)(this.getDebugTarget())).getActivityString(fActivity);
			//String aname= ((X10DebugTargetAlt)((IThread)this.getDebugTarget())).getActivityString(fActivity);
			System.out.println("X10Thread:getName , aname = "+aname);
			place = place.substring(4, 6);
		    return "ACTIVITY" + "@PLACE" +place +":" +aname ;
			}
		}  
		else {
			System.out.println("X10Thread:getName - fActivity is null");
		    return "ACTIVITY: "+this.getUnderlyingThread().toString();
		}
	}
	
	public ObjectReference getActivityObject() {
		return fActivity;
	}
	// Copied from superclass to get around visibility gotchas
	public void terminated() {
		super.terminated();
	}
	public void disposeStackFrames() {
		super.disposeStackFrames();
	}
	protected void removeCurrentBreakpoint(IBreakpoint bp) {
		super.removeCurrentBreakpoint(bp);
	}
	protected synchronized void suspendedByVM(){
		super.suspendedByVM();
	}
	protected synchronized void resumedByVM() throws DebugException{
		super.resumedByVM();
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

 public X10ActivityState getRunState() {
		return X10ActivityState.Running;
	}

 // mmk re-added java stack frames for testing purposes.  Feel free to remove once the new impl in place.
public IX10StackFrame[] getStackFrames() {
//         // TODO create stack frames in sample model
//         return new IX10StackFrame[0];
	if (isSuspendedQuiet()) {
		return new IX10StackFrame[0];
	}
	List list;
	try {
		list = computeStackFrames();
	} catch (DebugException e) {
		list = null;
	}
	IX10StackFrame x10StackFrames[] = new IX10StackFrame[list==null? 0 : list.size()];
	if (list!=null) {
		int i=0;
		for (Object jsfo: list) {
			x10StackFrames[i++] = new X10DelegatingStackFrame(getDebugTarget(), (JDIStackFrame)jsfo);

		}
	}
	return x10StackFrames;
 }

}
