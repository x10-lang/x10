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
import org.eclipse.jdt.internal.debug.core.IJDIEventListener;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugModelMessages;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIThreadGroup;
//import org.eclipse.jdt.internal.debug.core.model.JDIThread.StepHandler;
//import org.eclipse.jdt.internal.debug.core.model.JDIThread.StepReturnHandler;


import org.eclipse.imp.x10dt.debug.model.IX10Activity;
import org.eclipse.imp.x10dt.debug.model.IX10Clock;
import org.eclipse.imp.x10dt.debug.model.IX10Place;
import org.eclipse.imp.x10dt.debug.model.IX10StackFrame;


import com.sun.jdi.InvalidStackFrameException;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectCollectedException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.StringReference;
import com.sun.jdi.BooleanValue;
import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.Value;
import com.sun.jdi.StackFrame;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.StepRequest;

public class X10Thread extends JDIThread implements IThread, IX10Activity {
	
	private IX10Place fPlace;
	private IX10Activity fParent;
	private HashSet<IX10Clock> fClocks;
    private HashSet<IX10Activity> fChildren;
    private ObjectReference fActivity;
    private List fStackFrames;
    //private StepHandler fStepHandler= null;
    //private int fOriginalStepKind;
    //private int fOriginalStepStackDepth;
    //private Location fOriginalStepLocation;
    private boolean fRefreshChildren = true;
    private X10ActivityState fState;
    
    private static int seq=0;
    public int uid;
	private String fFileNameAndLineNo;
	
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

		synchronized(this) {
			uid = seq++;
		}
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
		try {
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
				fActivity = (ObjectReference)result;
				if (fFileNameAndLineNo==null) {
					List<Method> myNameMethods = (List<Method>)((ObjectReference)fActivity).referenceType().methodsByName("myName");
					Method myNameMethod = myNameMethods.get(0);
					Value fnlv = ((X10DebugTargetAlt)getDebugTarget()).invokeX10RTMethod(fActivity, myNameMethod, args);
					if (fnlv instanceof StringReference) {
						fFileNameAndLineNo = ((StringReference)fnlv).value();
					}
				}
			}
			return fActivity;
		  }
		  else {
			System.out.println("getFreshActivityObject: t not a PoolRunner");
			return null;
		  }
		} catch (ObjectCollectedException e) {
			return null;
		} catch (VMDisconnectedException exception) {
			return null;
		} 
	}
	
	public String getName() {
		if (fActivity!=null){
			System.out.println("X10Thread:getName() for fActivity:"+fActivity.toString());
			String place = this.getUnderlyingThread().name();
			if (place.contains("Main Activity"))
				{place = place.substring(4, 6);
				return "ACTIVITY-" + uid + "@PLACE" +place +":" +fFileNameAndLineNo ;}
			/*
			else if (place.contains("Finished")){
				place=place.replace("pool", "PLACE");�
				place=place.substring(0, 16);
			    return place + "(Idle)";
				//return "ACTIVITY: "+this.getUnderlyingThread().toString();
			}*/
			else {
			String aname = ((X10DebugTargetAlt)getDebugTarget()).getActivityString(fActivity);
			System.out.println("X10Thread:getName , aname = "+aname);
			//if (aname.contains("false")) {
				//return "terminated";
			//}
			if (aname==null) {
				aname=" ";
			}
			place = place.substring(4, 6);
		    return "ACTIVITY-" + uid + "@PLACE" +place +":" +aname ;
			}
		}  
		else {
			System.out.println("X10Thread:getName - fActivity is null");
			return this.getUnderlyingThread().name();
		    //return "ACTIVITY: "+this.getUnderlyingThread().toString();
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
	 ThreadReference t=this.getUnderlyingThread();
	 try{
	 if (t.referenceType().name().equals("x10.runtime.PoolRunner")) 
		 getFreshActivityObject(t);
	 } catch (ObjectCollectedException e) {
			return X10ActivityState.Idle;
		} catch (VMDisconnectedException exception) {
			return X10ActivityState.Idle;
		} 
	 
	 if (isSuspended() && fActivity!=null) {
		 fState=X10ActivityState.Suspended;
		 return fState;
	 }
	 else {
		 if (fActivity != null){
			Method meth=null;
			List args=Collections.EMPTY_LIST;
			Value result=null;
			Boolean notFin=true;
			meth=getMethodByName("notFinished");
			result = ((X10DebugTargetAlt)(this.getDebugTarget())).invokeX10RTMethod(fActivity,meth, args);
			if (result instanceof BooleanValue) {
				notFin=((BooleanValue)result).value();
			}
			System.out.println("getRunState: notFin is "+notFin);
			if (notFin){
		    /*List<Method> m = (List<Method>)fActivity.referenceType().methodsByName("hasChildren");
			System.out.println("Shivali:getRunState() Method found "+m.size());
			Method meth=null;
			for (Method m1: m) {
				meth = m1; 
			}
			List args=Collections.EMPTY_LIST;*/
			    result=null;
				meth=getMethodByName("hasChildren");	
			    result = ((X10DebugTargetAlt)(this.getDebugTarget())).invokeX10RTMethod(fActivity,meth, args);
			    String state=null;
			    if (result instanceof StringReference) {
				   state = ((StringReference)result).value();
				   System.out.println("getRunState = "+ state);
			    }
			    if (state!=null && state.equals("true")) {
			    	fState=X10ActivityState.Blocked;
			    	return fState;
			    }
			    else {
			    	fState=X10ActivityState.Running;
			    	return fState;
			    }
			}
			else {
				fState= X10ActivityState.Idle;
				return fState;
			}
		 }
		 else {
			 fState=X10ActivityState.Idle;
			 return fState;
		 }
		}
		
	 //return X10ActivityState.Running;
	}
 
 public String getThreadText() {
	 
	 if (isSuspended() && fActivity!=null){
		 fState=X10ActivityState.Suspended;
	 }
	 if (fState!=X10ActivityState.Idle) {
		    System.out.println("X10ModelPresentation: a!=null");	
		    String name = this.getName();
		    System.out.println("X10ModelPresentation: name ="+name);
		    /*if (name.contains("terminated")) {
		    	String place=t.name();
				place=place.replace("pool", "PLACE");
				place=place.substring(0, 16);
			    return place + "(Idle)";
		    }*/
		    //if (((X10Thread)element).isSuspended()){
		    if (fState==IX10Activity.X10ActivityState.Suspended) {
		    	//name=name.replace("true","Suspended");
		    	name=name.concat(" Suspended");
		    }
		    else if (fState == IX10Activity.X10ActivityState.Blocked) {
		        //name=name.replace("true","Running");
		    	name=name.concat(" Blocked");
		    }
		    else name=name.concat(" Running"); 
		    return name;
		}	
	 else{
			
			String place=this.getUnderlyingThread().name();
			if (place.contains("pool")) {
			  place=place.replace("pool", "PLACE");
			  place=place.substring(0, 16);
		      return place + "(Idle)";// need to call getActivity().toString() on PoolRunner (on VM Side)
			}
			return place;
			//return "Activity:" + t.toString();
		}
	 
 }
 
 //Should be moved to utils eventually
 public Method getMethodByName(String name) {
	 List<Method> m = (List<Method>)fActivity.referenceType().methodsByName(name);
		System.out.println("Shivali:getRunState() Method found "+m.size());
		Method meth=null;
		for (Method m1: m) {
			meth = m1; 
		}
		return meth;
 }
 
 /**
	 * Sets the step handler currently handling a step
	 * request.
	 * 
	 * @param handler the current step handler, or <code>null</code>
	 * 	if none
	 */
 /*
	protected void setPendingStepHandler(StepHandler handler) {
		fStepHandler = handler;
	}
	
	protected void abortStep() {
		StepHandler handler = fStepHandler;
		if (handler != null) {
			handler.abort();
		}
	}
	public boolean isStepping() {
		return fStepHandler != null;
	}
	protected boolean canStep() {
		try {
			return isSuspended()
				&& !isSuspendedQuiet()
				&& (!isPerformingEvaluation() || isInvokingMethod())
				&& !isStepping()
				&& getTopStackFrame() != null
				&& !getJavaDebugTarget().isPerformingHotCodeReplace();
		} catch (DebugException e) {
			return false;
		}
	}
	
	public synchronized void stepInto() throws DebugException {
		if (!canStepInto()) {
			return;
		}
		StepHandler handler = new StepIntoHandler();
		handler.step();
	}

	public synchronized void stepOver() throws DebugException {
		if (!canStepOver()) {
			return;
		}
		StepHandler handler = new StepOverHandler();
		handler.step();
	}

	/* *
	 * This method is synchronized, such that the step request
	 * begins before a background evaluation can be performed.
	 * 
	 * @see IStep#stepReturn()
	 */
 /*
	public synchronized void stepReturn() throws DebugException {
		if (!canStepReturn()) {
			return;
		}
		StepHandler handler = new StepReturnHandler();
		handler.step();
	}
	
	protected void setOriginalStepKind(int stepKind) {
		fOriginalStepKind = stepKind;
	}
	
	protected int getOriginalStepKind() {
		return fOriginalStepKind;
	}
	
	protected void setOriginalStepLocation(Location location) {
		fOriginalStepLocation = location;
	}
	
	protected Location getOriginalStepLocation() {
		return fOriginalStepLocation;
	}
	
	protected void setOriginalStepStackDepth(int depth) {
		fOriginalStepStackDepth = depth;
	}
	
	protected int getOriginalStepStackDepth() {
		return fOriginalStepStackDepth;
	}
	
	
	protected boolean shouldDoExtraStepInto(Location location) throws DebugException {
		if (getOriginalStepKind() != StepRequest.STEP_INTO) {
			return false;
		}
		if (getOriginalStepStackDepth() != getUnderlyingFrameCount()) {
			return false;
		}
		Location origLocation = getOriginalStepLocation();
		if (origLocation == null) {
			return false;
		}	
		// We cannot simply check if the two Locations are equal using the equals()
		// method, since this checks the code index within the method.  Even if the
		// code indices are different, the line numbers may be the same, in which case
		// we need to do the extra step into.
		Method origMethod = origLocation.method();
		Method currMethod = location.method();
		if (!origMethod.equals(currMethod)) {
			return false;
		}	
		if (origLocation.lineNumber() != location.lineNumber()) {
			return false;
		}			
		return true;
	}
	
	
	protected boolean shouldDoStepReturn() throws DebugException {
		if (getOriginalStepKind() == StepRequest.STEP_INTO) {
			if ((getOriginalStepStackDepth() + 1) < getUnderlyingFrameCount()) {
				return true;
			}
		}
		return false;
	}	
	
	protected synchronized void stepToFrame(IStackFrame frame) throws DebugException {
		if (!canStepReturn()) {
			return;
		}
		StepHandler handler = new StepToFrameHandler(frame);
		handler.step();
	}

	
	//protected StepHandler getPendingStepHandler() {
		//return fStepHandler;
	//}
	
 abstract class StepHandler implements IJDIEventListener {
		
		private StepRequest fStepRequest;
		
		
		protected void step() throws DebugException {
			X10StackFrame top = (X10StackFrame)getTopStackFrame();
			if (top == null) {
				return;
			}
			setOriginalStepKind(getStepKind());
			Location location = top.getUnderlyingStackFrame().location();
			setOriginalStepLocation(location);
			setOriginalStepStackDepth(computeStackFrames().size());
			setStepRequest(createStepRequest());
			setPendingStepHandler(this);
			addJDIEventListener(this, getStepRequest());
			setRunning(true);
			preserveStackFrames();
			fireResumeEvent(getStepDetail());
			invokeThread();
		}
		
		/
		protected void invokeThread() throws DebugException {
			try {
				getUnderlyingThread().resume();
			} catch (RuntimeException e) {
				stepEnd();
				//targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.JDIThread_exception_stepping, new String[] {e.toString()}), e); 
			}
		}
		
		protected StepRequest createStepRequest() throws DebugException {
			return createStepRequest(getStepKind());
		}
		
		protected StepRequest createStepRequest(int kind) throws DebugException {
			EventRequestManager manager = getEventRequestManager();
			if (manager == null) {
				requestFailed(JDIDebugModelMessages.JDIThread_Unable_to_create_step_request___VM_disconnected__1, null); 
			}
			try {
				StepRequest request = manager.createStepRequest(getUnderlyingThread(), StepRequest.STEP_LINE, kind);
				request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
				request.addCountFilter(1);
				attachFiltersToStepRequest(request);
				request.enable();
				return request;
			} catch (RuntimeException e) {
				//targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.JDIThread_exception_creating_step_request, new String[] {e.toString()}), e); 
			}			
			// this line will never be executed, as the try block
			// will either return, or the catch block will throw 
			// an exception
			return null;
			
		}
		
		
		protected abstract int getStepKind();
		
		protected abstract int getStepDetail();		
		
		
		protected void setStepRequest(StepRequest request) {
			fStepRequest = request; 
		}
		
		
		protected StepRequest getStepRequest() {
			return fStepRequest;
		}
		
		
		protected void deleteStepRequest() {
			removeJDIEventListener(this, getStepRequest());
			try {
				EventRequestManager manager = getEventRequestManager();
				if (manager != null) {
					manager.deleteEventRequest(getStepRequest());
				}				
				setStepRequest(null);
			} catch (RuntimeException e) {
				logError(e);
			}
		}
		
		
		protected void attachFiltersToStepRequest(StepRequest request) {
			
			if (applyStepFilters() && isStepFiltersEnabled()) {
				Location currentLocation= getOriginalStepLocation();
				//if (currentLocation == null || !JAVA_STRATUM_CONSTANT.equals(currentLocation.declaringType().defaultStratum())) {
				if (currentLocation == null ) {
					return;
				}
//Removed the fix for bug 5587, to address bug 41510				
//				//check if the user has already stopped in a filtered location
//				//is so do not filter @see bug 5587
//				ReferenceType type= currentLocation.declaringType();
//				String typeName= type.name();
				String[] activeFilters = getJavaDebugTarget().getStepFilters();
//				for (int i = 0; i < activeFilters.length; i++) {
//					StringMatcher matcher = new StringMatcher(activeFilters[i], false, false);
//					if (matcher.match(typeName)) {
//						return;
//					}
//				}
				if (activeFilters != null) {
				    for (int i = 0; i < activeFilters.length; i++) {
				        request.addClassExclusionFilter(activeFilters[i]);
				    }
				}
			}
		}
		
		
		protected boolean applyStepFilters() {
			return true;
		}
		
		
		public boolean handleEvent(Event event, JDIDebugTarget target) {
			try {
				StepEvent stepEvent = (StepEvent) event;
				Location currentLocation = stepEvent.location();

				
				if (!getJavaDebugTarget().isStepThruFilters()) {
					if (shouldDoStepReturn()) {
						deleteStepRequest();
						createSecondaryStepRequest(StepRequest.STEP_OUT);
						return true;
					}
				}
				// if the ending step location is filtered and we did not start from
				// a filtered location, or if we're back where
				// we started on a step into, do another step of the same kind
				if (locationShouldBeFiltered(currentLocation) || shouldDoExtraStepInto(currentLocation)) {
					setRunning(true);
					deleteStepRequest();
					createSecondaryStepRequest();			
					return true;		
				// otherwise, we're done stepping
				}
				stepEnd();
				return false;
			} catch (DebugException e) {
				logError(e);
				stepEnd();
				return false;
			}
		}
		
		
		
		
		public void wonSuspendVote(Event event, JDIDebugTarget target) {
			// do nothing
		}

		
		protected boolean locationShouldBeFiltered(Location location) throws DebugException {
			if (applyStepFilters()) {
				Location origLocation= getOriginalStepLocation();
				if (origLocation != null) {
					return !locationIsFiltered(origLocation.method()) && locationIsFiltered(location.method());
				}
			}
			return false;
		}
		
		protected boolean locationIsFiltered(Method method) {
			if (isStepFiltersEnabled()) {
				boolean filterStatics = getJavaDebugTarget().isFilterStaticInitializers();
				boolean filterSynthetics = getJavaDebugTarget().isFilterSynthetics();
				boolean filterConstructors = getJavaDebugTarget().isFilterConstructors();
				if (!(filterStatics || filterSynthetics || filterConstructors)) {
					return false;
				}			
				
				if ((filterStatics && method.isStaticInitializer()) ||
					(filterSynthetics && method.isSynthetic()) ||
					(filterConstructors && method.isConstructor()) ) {
					return true;	
				}
			}
			
			return false;
		}

		
		protected void stepEnd() {
			setRunning(false);
			deleteStepRequest();
			//setPendingStepHandler(null);
			fStepHandler=null;
			queueSuspendEvent(DebugEvent.STEP_END);
		}
		
		
		protected void createSecondaryStepRequest() throws DebugException {
			createSecondaryStepRequest(getStepKind());			
		}
		
		
		protected void createSecondaryStepRequest(int kind) throws DebugException {
			setStepRequest(createStepRequest(kind));
			setPendingStepHandler(this);
			addJDIEventListener(this, getStepRequest());			
		}	
		
		
		protected void abort() {
			if (getStepRequest() != null) {
				deleteStepRequest();
				//setPendingStepHandler(null);
				fStepRequest=null;
			}
		}
}
	
	class StepOverHandler extends StepHandler {
		
		protected int getStepKind() {
			return StepRequest.STEP_OVER;
		}	
		
		
		protected int getStepDetail() {
			return DebugEvent.STEP_OVER;
		}		
	}
	
	class StepIntoHandler extends StepHandler {
		
		protected int getStepKind() {
			return StepRequest.STEP_INTO;
		}	
		
		
		protected int getStepDetail() {
			return DebugEvent.STEP_INTO;
		}
		
	}
	
	
	class StepReturnHandler extends StepHandler {
		
		protected boolean locationShouldBeFiltered(Location location) throws DebugException {
			// if still at the same depth, do another step return (see bug 38744)
			if (getOriginalStepStackDepth() == getUnderlyingFrameCount()) {
				return true;
			}
			return super.locationShouldBeFiltered(location);
		}

		
		protected int getStepKind() {
			return StepRequest.STEP_OUT;
		}	
		
		
		protected int getStepDetail() {
			return DebugEvent.STEP_RETURN;
		}		
	}
	
	
	class StepToFrameHandler extends StepReturnHandler {
		
		private int fRemainingFrames;
		
		
		protected StepToFrameHandler(IStackFrame frame) throws DebugException {
			List frames = computeStackFrames();
			setRemainingFrames(frames.size() - frames.indexOf(frame));
		}
		
		protected void setRemainingFrames(int num) {
			fRemainingFrames = num;
		}
		
		
		protected int getRemainingFrames() {
			return fRemainingFrames;
		}
		
		
		public boolean handleEvent(Event event, JDIDebugTarget target) {
			try {
				int numFrames = getUnderlyingFrameCount();
				// tos should not be null
				if (numFrames <= getRemainingFrames()) {
					stepEnd();
					return false;
				}
				// reset running state and keep going
				setRunning(true);
				deleteStepRequest();
				createSecondaryStepRequest();
				return true;
			} catch (DebugException e) {
				logError(e);
				stepEnd();
				return false;
			}
		}				
	}
	
	class DropToFrameHandler extends StepReturnHandler {
		
		
		private int fFramesToDrop;
		
			
		protected DropToFrameHandler(IStackFrame frame) throws DebugException {
			List frames = computeStackFrames();
			setFramesToDrop(frames.indexOf(frame));
		}
		
		
		protected void setFramesToDrop(int num) {
			fFramesToDrop = num;
		}
		
		
		protected int getFramesToDrop() {
			return fFramesToDrop;
		}		
		
		
		protected void invokeThread() throws DebugException {
			if (getFramesToDrop() < 0) {
				super.invokeThread();
			} else {
				try {
					org.eclipse.jdi.hcr.ThreadReference hcrThread= (org.eclipse.jdi.hcr.ThreadReference) getUnderlyingThread();
					hcrThread.doReturn(null, true);
				} catch (RuntimeException e) {
					stepEnd();
					//targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.JDIThread_exception_while_popping_stack_frame, new String[] {e.toString()}), e); 
				}
			}
		}
		
				
		public boolean handleEvent(Event event, JDIDebugTarget target) {
			// pop is complete, update number of frames to drop
			setFramesToDrop(getFramesToDrop() - 1);
			try {
				if (getFramesToDrop() >= -1) {
					deleteStepRequest();
					doSecondaryStep();
				} else {
					stepEnd();
				}
			} catch (DebugException e) {
				stepEnd();
				logError(e);
			}
			return false;
		}
		
		protected void doSecondaryStep() throws DebugException {
			setStepRequest(createStepRequest());
			setPendingStepHandler(this);
			addJDIEventListener(this, getStepRequest());
			invokeThread();
		}		

		protected StepRequest createStepRequest() throws DebugException {
			EventRequestManager manager = getEventRequestManager();
			if (manager == null) {
				requestFailed(JDIDebugModelMessages.JDIThread_Unable_to_create_step_request___VM_disconnected__2, null); 
			}
			int num = getFramesToDrop();
			if (num > 0) {
				return super.createStepRequest();
			} else if (num == 0) {
				try {
					StepRequest request = ((org.eclipse.jdi.hcr.EventRequestManager) manager).createReenterStepRequest(getUnderlyingThread());
					request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
					request.addCountFilter(1);
					request.enable();
					return request;
				} catch (RuntimeException e) {
					//targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.JDIThread_exception_creating_step_request, new String[] {e.toString()}), e); 
				}			
			} else if (num == -1) {
				try {
					StepRequest request = manager.createStepRequest(getUnderlyingThread(), StepRequest.STEP_LINE, StepRequest.STEP_INTO);
					request.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
					request.addCountFilter(1);
					request.enable();
					return request;
				} catch (RuntimeException e) {
					//targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.JDIThread_exception_creating_step_request, new String[] {e.toString()}), e); 
				}					
			}
			// this line will never be executed, as the try block
			// will either return, or the catch block with throw 
			// an exception
			return null;
		}
	}
*/

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
