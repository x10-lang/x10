package org.eclipse.imp.x10dt.debug.model.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IStackFrame;

import org.eclipse.imp.x10dt.debug.Activator;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugModelMessages;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIThreadGroup;


import org.eclipse.imp.x10dt.debug.model.IX10Activity;
import org.eclipse.imp.x10dt.debug.model.IX10Clock;
import org.eclipse.imp.x10dt.debug.model.IX10Place;
import org.eclipse.imp.x10dt.debug.model.IX10StackFrame;


import com.sun.jdi.InvalidStackFrameException;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectCollectedException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.StackFrame;

public class X10Thread extends JDIThread implements IThread, IX10Activity {
	
	private IX10Place fPlace;
	private IX10Activity fParent;
	private HashSet<IX10Clock> fClocks;
    private HashSet<IX10Activity> fChildren;
    private ObjectReference fActivity;
    private List fStackFrames;
    private boolean fRefreshChildren = true;
	
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
		//if (thread.name().compareTo("InvokeMethods")==0) {
			//System.out.println("About to fire");
			//fireSuspendEvent(DebugEvent.BREAKPOINT);
		//}
		fStackFrames = new ArrayList();
		/**
		 * @see org.eclipse.debug.core.model.IDebugElement#getModelIdentifier()
		 */

	}
	public String getModelIdentifier() {
		return Activator.getUniqueIdentifier();

		//return getDebugTarget().getModelIdentifier();
	}
	
	protected synchronized List computeStackFrames(boolean refreshChildren) throws DebugException {
		if (isSuspended()) {
			if (isTerminated()) {
				fStackFrames.clear();
			} else if (refreshChildren) {
				List frames = getUnderlyingFrames();
				int oldSize = fStackFrames.size();
				int newSize = frames.size();
				int discard = oldSize - newSize; // number of old frames to discard, if any
				for (int i = 0; i < discard; i++) {
					X10StackFrame invalid = (X10StackFrame) fStackFrames.remove(0);
					invalid.bind(null, -1, true);
				}
				int newFrames = newSize - oldSize; // number of frames to create, if any
				int depth = oldSize;
				for (int i = newFrames - 1; i >= 0; i--) {
					fStackFrames.add(0, new X10StackFrame(this, (StackFrame) frames.get(i), depth));
					depth++;
				}
				int numToRebind = Math.min(newSize, oldSize); // number of frames to attempt to rebind
				int offset = newSize - 1;
				for (depth = 0; depth < numToRebind; depth++) {
					X10StackFrame oldFrame = (X10StackFrame) fStackFrames.get(offset);
					StackFrame frame = (StackFrame) frames.get(offset);
					JDIStackFrame newFrame = oldFrame.bind(frame, depth,true);
					if (newFrame != oldFrame) {
						fStackFrames.set(offset, newFrame);
					}
					offset--;
				}
				

			}
			fRefreshChildren = false;
		} else {
			return Collections.EMPTY_LIST;
		}
		return fStackFrames;
	}	
	
	public synchronized List computeStackFrames() throws DebugException {
		return computeStackFrames(fRefreshChildren);
	}
	
	protected synchronized void preserveStackFrames() {
		fRefreshChildren = true;
		Iterator frames = fStackFrames.iterator();
		while (frames.hasNext()) {
			((X10StackFrame)frames.next()).setUnderlyingStackFrame(null);
		}
	}
	
	protected synchronized void disposeStackFrames() {
		fStackFrames.clear();
		fRefreshChildren = true;
	}
	
	protected void popFrame(IStackFrame frame) throws DebugException {
		JDIDebugTarget target= (JDIDebugTarget)getDebugTarget();
		if (target.canPopFrames()) {
			// JDK 1.4 support
			try {
				// Pop the frame and all frames above it
				StackFrame jdiFrame= null;
				int desiredSize= fStackFrames.size() - fStackFrames.indexOf(frame) - 1;
				int lastSize= fStackFrames.size() + 1; // Set up to pass the first test
				int size= fStackFrames.size();
				while (size < lastSize && size > desiredSize) {
					// Keep popping frames until the stack stops getting smaller
					// or popFrame is gone.
					// see Bug 8054
					jdiFrame = ((X10StackFrame) frame).getUnderlyingStackFrame();
					preserveStackFrames();
					super.getUnderlyingThread().popFrames(jdiFrame);
					lastSize= size;
					size= computeStackFrames().size();
				}
			} catch (IncompatibleThreadStateException exception) {
				//targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.JDIThread_exception_popping, new String[] {exception.toString()}),exception); 
			} catch (InvalidStackFrameException exception) {
				// InvalidStackFrameException can be thrown when all but the
				// deepest frame were popped. Fire a changed notification
				// in case this has occured.
				fireChangeEvent(DebugEvent.CONTENT);
				targetRequestFailed(exception.toString(),exception); 
			} catch (RuntimeException exception) {
				//targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.JDIThread_exception_popping, new String[] {exception.toString()}),exception); 
			}
		}
	}
	
	
	
	private List getUnderlyingFrames() throws DebugException {
		if (!isSuspended()) {
			// Checking isSuspended here eliminates a race condition in resume
			// between the time stack frames are preserved and the time the
			// underlying thread is actually resumed.
			//requestFailed(JDIDebugModelMessages.JDIThread_Unable_to_retrieve_stack_frame___thread_not_suspended__1, null, IJavaThread.ERR_THREAD_NOT_SUSPENDED); 
		}
		try {
			return super.getUnderlyingThread().frames(); 
		} catch (IncompatibleThreadStateException e) {
			//requestFailed(JDIDebugModelMessages.JDIThread_Unable_to_retrieve_stack_frame___thread_not_suspended__1, e, IJavaThread.ERR_THREAD_NOT_SUSPENDED); 
		} catch (RuntimeException e) {
			//targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.JDIThread_exception_retrieving_stack_frames_2, new String[] {e.toString()}), e); 
		} catch (InternalError e) {
			//targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.JDIThread_exception_retrieving_stack_frames_2, new String[] {e.toString()}), e); 
		}
		// execution will not reach this line, as
		// #targetRequestFailed will thrown an exception
		return null;
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
			/*
			else if (place.contains("Finished")){
				place=place.replace("pool", "PLACE");
				place=place.substring(0, 16);
			    return place + "(Idle)";
				//return "ACTIVITY: "+this.getUnderlyingThread().toString();
			}*/
			else {
			String aname = ((X10DebugTargetAlt)(this.getDebugTarget())).getActivityString(fActivity);
			//String aname= ((X10DebugTargetAlt)((IThread)this.getDebugTarget())).getActivityString(fActivity);
			System.out.println("X10Thread:getName , aname = "+aname);
			if (aname.contains("false")) {
				return "terminated";
			}
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
	
	//public void disposeStackFrames() {
		//super.disposeStackFrames();
	//}
	
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

/*
 public IStackFrame[] getStackFrames() throws DebugException {
	 if (isSuspendedQuiet()) {
			return new IX10StackFrame[0];
		}
		List list = computeStackFrames();
		IX10StackFrame x10StackFrames[] = new IX10StackFrame[list.size()];
		int i=0;
		for (Object jsfo: list) {
			x10StackFrames[i++] = new X10StackFrame(getDebugTarget(), (JDIStackFrame)jsfo);
			
		}
		return x10StackFrames;
=======

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
>>>>>>> 1.5
 }
 
 public synchronized IStackFrame getTopStackFrame() throws DebugException {
		List c= computeStackFrames();
		if (c.isEmpty()) {
			return null;
		}
		IX10StackFrame tos = null;
		tos = new X10StackFrame(getDebugTarget(), (JDIStackFrame)(c.get(0)));
		return tos;
		//return (IStackFrame) c.get(0);
	}
*/
}
