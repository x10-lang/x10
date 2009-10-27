package org.eclipse.imp.x10dt.debug.model.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Collections;

import org.eclipse.core.internal.runtime.AdapterManager;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementContentProvider;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.imp.x10dt.debug.Activator;
//import org.eclipse.imp.x10dt.debug.model.EvaluationEngineHelper;
import org.eclipse.imp.x10dt.debug.model.IX10Activity;
import org.eclipse.imp.x10dt.debug.model.IX10Application;
import org.eclipse.imp.x10dt.debug.model.impl.X10Application;
import org.eclipse.imp.x10dt.debug.ui.presentation.X10ModelPresentation;
import org.eclipse.jdi.TimeoutException;
import org.eclipse.jdt.debug.core.IJavaBreakpoint;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaReferenceType;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.debug.eval.IAstEvaluationEngine;
import org.eclipse.jdt.debug.eval.IEvaluationListener;
import org.eclipse.jdt.internal.debug.core.EventDispatcher;
import org.eclipse.jdt.internal.debug.core.IJDIEventListener;
import org.eclipse.jdt.internal.debug.core.breakpoints.JavaBreakpoint;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugElement;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugModelMessages;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIReferenceType;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;
import org.eclipse.jdt.internal.debug.core.model.JDIType;
import org.eclipse.ui.activities.IActivity;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.StringReference;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassObjectReference;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.InvocationException;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectCollectedException;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Type;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;


public class X10DebugTargetAlt extends JDIDebugTarget implements IDebugTarget, IJavaDebugTarget, ILaunchListener, IDebugEventSetListener {

	/**
	 * An event handler for thread death events. When a thread
	 * dies in the target VM, its associated model thread is
	 * removed from the debug target.
	 */
	class X10ThreadDeathHandler implements IJDIEventListener {
		
		protected X10ThreadDeathHandler() {
			createRequest();
		}
		
		/**
		 * Creates and registers a request to listen to thread
		 * death events.
		 */
		protected void createRequest() {
			EventRequestManager manager = getEventRequestManager();
			if (manager != null) {
				try {
					EventRequest req= manager.createThreadDeathRequest();
					req.setSuspendPolicy(EventRequest.SUSPEND_NONE);
					req.enable();
					addJDIEventListener(this, req);	
				} catch (RuntimeException e) {
					logError(e);
				}					
			}
		}
				
		/**
		 * Locates the model thread associated with the underlying JDI thread
		 * that has terminated, and removes it from the collection of
		 * threads belonging to this debug target. A terminate event is
		 * fired for the model thread.
		 *
		 * @param event a thread death event
		 * @param target the target in which the thread died
		 * @return <code>true</code> - the thread should be resumed
		 */
		public boolean handleEvent(Event event, JDIDebugTarget target) {
			ThreadReference ref= ((ThreadDeathEvent)event).thread();
			X10Thread thread= (X10Thread)findThread(ref);
			if (thread != null) {
				synchronized (fThreads) {
					fThreads.remove(thread);
				}
				thread.terminated();
				// bug fix start: InvokeMethods was leading to non-termination
				try {
					   System.out.println("Resuming fThreadForInvokingRTMethods : SuspenCount="+fThreadForInvokingRTMethods.suspendCount());
					   fThreadForInvokingRTMethods.resume();
					   //System.out.println("Resuming fThreadForInvokingRTMethods : SuspenCount="+fThreadForInvokingRTMethods.suspendCount());
					   //fThreadForInvokingRTMethods.resume();
					} catch (ObjectCollectedException e) {
						//return false;
					} catch (VMDisconnectedException exception) {
						//return false;
					} 
				//bug fix end	
			}
			
			/*IThread[] threads = getThreads();
			boolean found = false;
			for (int i = 0; i < threads.length; i++) {
				JDIThread th = (JDIThread)threads[i];
				// Works for 1.5, may not work properly for future runtimes
				try{
					if (th.getUnderlyingThread().name().contains("Destroy")){
						System.out.println("DEATHHANDLer for Destroy");
					}
				  if (th.getUnderlyingThread().name().contains("pool")) {
					found=true;
					break;
					//System.out.println("ThreadDeath :Name is "+ th.getUnderlyingThread().name());
				}
				} catch (ObjectCollectedException e) {
					//return false;
				} catch (VMDisconnectedException exception) {
					//return false;
				} 
			}
			if (!found) {
				try {
				   System.out.println("Resuming fThreadForInvokingRTMethods : SuspenCount="+fThreadForInvokingRTMethods.suspendCount());
				   fThreadForInvokingRTMethods.resume();
				   //System.out.println("Resuming fThreadForInvokingRTMethods : SuspenCount="+fThreadForInvokingRTMethods.suspendCount());
				   //fThreadForInvokingRTMethods.resume();
				} catch (ObjectCollectedException e) {
					//return false;
				} catch (VMDisconnectedException exception) {
					//return false;
				} 
				
			}*/
			// bug fix end.
			return true;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.debug.core.IJDIEventListener#wonSuspendVote(com.sun.jdi.event.Event, org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget)
		 */
		public void wonSuspendVote(Event event, JDIDebugTarget target) {
			// do nothing
		}

		/**
		 * BRT 8/14/09, added unimplemented method req'd for Eclipse 3.5 change
		 */
		public void eventSetComplete(Event event, JDIDebugTarget target,
				boolean suspend, EventSet eventSet) {
			// TODO Auto-generated method stub
			System.out.println("X10DebugTargetAlt.eventSetComplete, Do something here.");		
		}

		/**
		 * BRT 8/14/09, added unimplemented method req'd for Eclipse 3.5 change
		 */
		public boolean handleEvent(Event event, JDIDebugTarget target,
				boolean suspendVote, EventSet eventSet) {
			// TODO Auto-generated method stub
			System.out.println("X10DebugTargetAlt.handleEvent, Do something here. ");
			return false;
		}
	
	}

	/**
	 * This is copied (almost) unchanged from superclass, but needs to be copied because we specialize a method that creates instances of it
	 * and it has only package visibility there
	 */
	class X10ThreadStartHandler implements IJDIEventListener {
		
		protected EventRequest fRequest;
		
		protected X10ThreadStartHandler() {
			createRequest();
		} 
		
		/**
		 * Creates and registers a request to handle all thread start
		 * events
		 */
		protected void createRequest() {
			EventRequestManager manager = getEventRequestManager();
			if (manager != null) {			
				try {
					EventRequest req= manager.createThreadStartRequest();
					req.setSuspendPolicy(EventRequest.SUSPEND_NONE);
					req.enable();
					addJDIEventListener(this, req);
					setRequest(req);
				} catch (RuntimeException e) {
					logError(e);
				}
			}
		}

		/**
		 * Creates a model thread for the underlying JDI thread
		 * and adds it to the collection of threads for this 
		 * debug target. As a side effect of creating the thread,
		 * a create event is fired for the model thread.
		 * The event is ignored if the underlying thread is already
		 * marked as collected.
		 * 
		 * @param event a thread start event
		 * @param target the target in which the thread started
		 * @return <code>true</code> - the thread should be resumed
		 */
		public boolean handleEvent(Event event, JDIDebugTarget target) {
			ThreadReference thread= ((ThreadStartEvent)event).thread();
			try {
				if (thread.isCollected()) {
					return false;
				}
			} catch (VMDisconnectedException exception) {
				return false;
			} catch (ObjectCollectedException e) {
				return false;
			} catch (TimeoutException e) {
				// continue - attempt to create the thread
			}
			X10Thread jdiThread= (X10Thread)findThread(thread);
			if (jdiThread == null) {
				jdiThread = createThread(thread);
				if (jdiThread == null) {
					return false;
				}
				//Following code is for watching dynamic activity name changes
				try{
				 ReferenceType th=thread.referenceType();
				 if (th.name().equals("x10.runtime.PoolRunner")){
					Field f = ((ClassType)th).fieldByName("name");
					System.out.println("X10ThreadStartHandler field is "+f.toString());
					if (f!=null)
						new X10ModWatchpointHandler(f);
				 }
				} catch (VMDisconnectedException exception) {
					return false;
				} catch (ObjectCollectedException e) {
					return false;
				}  
				//code ends here

			} else {
				jdiThread.disposeStackFrames();
				jdiThread.fireChangeEvent(DebugEvent.CONTENT);
			}
			return !jdiThread.isSuspended();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.debug.core.IJDIEventListener#wonSuspendVote(com.sun.jdi.event.Event, org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget)
		 */
		public void wonSuspendVote(Event event, JDIDebugTarget target) {
			// do nothing
		}
		
		/**
		 * De-registers this event listener.
		 */
		protected void deleteRequest() {
			if (getRequest() != null) {
				removeJDIEventListener(this, getRequest());
				setRequest(null);
			}
		}
		
		protected EventRequest getRequest() {
			return fRequest;
		}

		protected void setRequest(EventRequest request) {
			fRequest = request;
		}

		/**
		 * BRT 8/14/09, added unimplemented method req'd for Eclipse 3.5 change
		 */
		public void eventSetComplete(Event event, JDIDebugTarget target,
				boolean suspend, EventSet eventSet) {
				System.out.println("X10DebugTargetAlt, added unimplemented method, do something here.");
			// TODO Auto-generated method stub		
		}

		/**
		 * BRT 8/14/09, added unimplemented method req'd for Eclipse 3.5 change
		 */
		public boolean handleEvent(Event event, JDIDebugTarget target,
				boolean suspendVote, EventSet eventSet) {
			// TODO Auto-generated method stub
			System.out.println("X10DebugTargetAlt, added unimplemented method, do something here.");
			return false;
		}
}
class X10ModWatchpointHandler implements IJDIEventListener {
		
		protected EventRequest fRequest;
		
		protected X10ModWatchpointHandler(Field arg1) {
			createRequest(arg1);
		} 
		
		/**
		 * Creates and registers a request to handle all thread start
		 * events
		 */
		protected void createRequest(Field arg1) {
			EventRequestManager manager = getEventRequestManager();
			if (manager != null) {			
				try {
					EventRequest req= manager.createModificationWatchpointRequest(arg1);
					req.setSuspendPolicy(EventRequest.SUSPEND_NONE);
					req.enable();
					addJDIEventListener(this, req);
					setRequest(req);
				} catch (RuntimeException e) {
					logError(e);
				}
			}
		}
		
		public boolean handleEvent(Event event, JDIDebugTarget target) {
			
			ThreadReference thread= ((ModificationWatchpointEvent)event).thread();
			try {
				if (thread.isCollected()) {
					return false;
				}
			} catch (VMDisconnectedException exception) {
				return false;
			} catch (ObjectCollectedException e) {
				return false;
			} catch (TimeoutException e) {
				// continue - attempt to create the thread
			}
			X10Thread jdiThread= (X10Thread)findThread(thread);
			if (jdiThread == null) {
				//jdiThread = createThread(thread);
				//if (jdiThread == null) {
					return false;
				//}

			} else {
//				System.out.println("I am in ModWatchpointHandler!!");
				jdiThread.disposeStackFrames();
				target.fireChangeEvent(DebugEvent.CONTENT);
				//jdiThread.fireChangeEvent(DebugEvent.STATE);
			}
			return !jdiThread.isSuspended();
		}
		
		public void wonSuspendVote(Event event, JDIDebugTarget target) {
			// do nothing
		}
		
		/**
		 * De-registers this event listener.
		 */
		protected void deleteRequest() {
			if (getRequest() != null) {
				removeJDIEventListener(this, getRequest());
				setRequest(null);
			}
		}
		
		protected EventRequest getRequest() {
			return fRequest;
		}

		protected void setRequest(EventRequest request) {
			fRequest = request;
		}

		/**
		 * BRT 8/14/09, added unimplemented method req'd for Eclipse 3.5 change
		 */
		public void eventSetComplete(Event event, JDIDebugTarget target,
				boolean suspend, EventSet eventSet) {
			// TODO Auto-generated method stub
			System.out.println("X10DebugTargetAlt, added unimplemented method, do something here.");
		}

		/**
		 * BRT 8/14/09, added unimplemented method req'd for Eclipse 3.5 change
		 */
		public boolean handleEvent(Event event, JDIDebugTarget target,
				boolean suspendVote, EventSet eventSet) {
			// TODO Auto-generated method stub
			System.out.println("X10DebugTargetAlt, added unimplemented method, do something here.");
			return false;
		}
}
		

	private IDebugTarget fJDebugTarget;
	//S//private ClassObjectReference fX10RT;
	//private X10DebugTargetAlt fX10DebugTarget;
	private Set<IX10Activity> fActivities;
	private ObjectReference fX10RT;
	private ThreadReference fThreadForInvokingRTMethods;
	//S//private ILaunch fLaunch = null;
	private EventDispatcher fEventDispatcher;
	private ArrayList fThreads;
	private ArrayList fPlaces=new ArrayList(5);
	private int noOfPlaces;
	private IX10Application _application;
	
	public X10DebugTargetAlt(ILaunch launch, VirtualMachine jvm, String name,
			boolean supportTerminate, boolean supportDisconnect,
			IProcess process, boolean resume) {
		super(launch, jvm, name, supportTerminate, supportDisconnect, process, resume);
		fJDebugTarget = launch.getDebugTarget();
		if (this.getVM() == null)
			System.out.println("X10Target.vm is null");
	}
	
	public void setPlaceNo(int places){
		noOfPlaces=places;
		System.out.println("Places are "+noOfPlaces);
	}
	
	public int getPlaceNo() {
		return noOfPlaces;
	}
//	public X10DebugTarget(ILaunch launch, VirtualMachine jvm, String name, boolean supportTerminate, boolean supportDisconnect, IProcess process, boolean resume) {
//		super(launch, jvm, name, supportTerminate,supportDisconnect, process, resume);
//		fJDebugTarget = launch.getDebugTarget();
//    }
	
	/**
	 * Initialize event requests and state from the underlying VM.
	 * This method is synchronized to ensure that we do not start
	 * to process an events from the target until our state is
	 * initialized.
	 */
	protected synchronized void initialize() {
//		_application = SampleX10ModelFactory.getApplication();
		//initializeThreadForInvokeMethod();
		setThreadList(new ArrayList(5)); // really this belongs in constructor, but it would have to go *before* super(), which contains call to this (initialize) method.
		setEventDispatcher(new EventDispatcher(this));
		super.initialize();
//		initializeThreadForInvokeMethod();
//		setEventDispatcher(new EventDispatcher(this));
//		setRequestTimeout(JDIDebugModel.getPreferences().getInt(JDIDebugModel.PREF_REQUEST_TIMEOUT));
//		initializeRequests();
//		setThreadList(new ArrayList(5));
//		initializeState();
//		if (this.getVM() == null)
//			System.out.println("after initialize state.vm is null");
//		initializeBreakpoints();
//		getLaunch().addDebugTarget(this);
//        DebugPlugin plugin = DebugPlugin.getDefault();
//		plugin.addDebugEventListener(this);
//		fireCreationEvent();
//		// begin handling/dispatching events after the creation event is handled by all listeners
//		plugin.asyncExec(new Runnable() {
//			public void run() {
//				EventDispatcher dispatcher = getEventDispatcher();
//		        if (dispatcher != null) {
//		            Thread t= new Thread(dispatcher, X10DebugModel.getPluginIdentifier() + JDIDebugModelMessages.JDIDebugTarget_JDI_Event_Dispatcher); 
//		            t.setDaemon(true);
//		            t.start();
//		        }
//			}
//		});
//		if (this.getVM() == null)
//			System.out.println("before initialize end.vm is null");
	}
	/**
	 * Registers event handlers for thread creation,
	 * thread termination.
	 */
	protected void initializeRequests() {
		setX10ThreadStartHandler(new X10ThreadStartHandler());
		new X10ThreadDeathHandler();		
	}
	
	protected void cleanup() {
		super.cleanup();
		setX10ThreadStartHandler(null);
	}


	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * This section copied from superclass with slight modification to permit use of local (copied) ThreadStartHandler class
	 */
	
	
	/**
	 * The thread start event handler
	 */
	private X10ThreadStartHandler fThreadStartHandler= null;
	
	protected X10ThreadStartHandler getX10ThreadStartHandler() {
		return fThreadStartHandler;
	}

	protected void setX10ThreadStartHandler(X10ThreadStartHandler threadStartHandler) {
		fThreadStartHandler = threadStartHandler;
	}
	
	/**
	 * Allows for ThreadStartHandler to do clean up/disposal.
	 */
	private void disposeThreadHandler() {
		X10ThreadStartHandler handler = getX10ThreadStartHandler();
		if (handler != null) {
			handler.deleteRequest();
		}
	}
	
	/**
	 * @see IDisconnect#disconnect()
	 */
	public void disconnect() throws DebugException {

		if (!isAvailable()) {
			// already done
			return;
		}

		if (!canDisconnect()) {
			notSupported(JDIDebugModelMessages.JDIDebugTarget_does_not_support_disconnect); 
		}

		try {
			disposeThreadHandler();
			VirtualMachine vm = getVM();
			if (vm != null) {
				vm.dispose();
			}
		} catch (VMDisconnectedException e) {
			// if the VM disconnects while disconnecting, perform
			// normal disconnect handling
			disconnected();
		} catch (RuntimeException e) {
			targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.JDIDebugTarget_exception_disconnecting, new String[] {e.toString()}), e); 
		}

	}
	
	/**
	 * @see ITerminate#terminate()
	 */
	public void terminate() throws DebugException {
		if (!isAvailable()) {
			return;
		}
		if (!supportsTerminate()) {
			notSupported(JDIDebugModelMessages.JDIDebugTarget_does_not_support_termination); 
		}
		try {
			setTerminating(true);
			disposeThreadHandler();
			VirtualMachine vm = getVM();
			if (vm != null) {
				vm.exit(1);
			}
			IProcess process= getProcess();
			if (process != null) {
				process.terminate();
			}
		} catch (VMDisconnectedException e) {
			// if the VM disconnects while exiting, perform 
			// normal termination processing
			terminated();
		} catch (TimeoutException exception) {
			// if there is a timeout see if the associated process is terminated
			IProcess process = getProcess();
			if (process != null && process.isTerminated()) {
				terminated();
			} else {
				// All we can do is disconnect
				disconnected();
			}
		} catch (RuntimeException e) {
			targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.JDIDebugTarget_exception_terminating, new String[] {e.toString()}), e); 
		}
	}
	////////////////////////////////////////////////////////////////////////////////////////////

	
	protected void initializeBreakpoints() {
		IBreakpointManager manager= DebugPlugin.getDefault().getBreakpointManager();
		manager.addBreakpointListener(this);
		IBreakpoint[] bps = manager.getBreakpoints(/*getModelIdentifier()*/JDIDebugModel.getPluginIdentifier());
		for (int i = 0; i < bps.length; i++) {
			if (bps[i] instanceof IJavaBreakpoint) {
				breakpointAdded(bps[i]);
			}
		}
	}
    
	/**
	 * Sets the event dispatcher for this debug target.
	 * Set once at initialization.
	 * 
	 * @param dispatcher event dispatcher
	 * @see #initialize()
	 */
	private void setEventDispatcher(EventDispatcher dispatcher) {
		fEventDispatcher = dispatcher;
	}
	
	public EventDispatcher getEventDispatcher() {
		return fEventDispatcher;
	}
	
	/*
=======
	
	public ThreadReference getThreadForInvokeMethod() {
		if (fThreadForInvokingRTMethods==null) {
			initializeThreadForInvokeMethod();
		}
		return fThreadForInvokingRTMethods;
	}
>>>>>>> 1.3
	private void initializeThreadForInvokeMethod() {
	    List threads= null;
		VirtualMachine vm = getVM();
		if (vm != null) {
			System.out.println("Shivali: vm not null");
			try {
				threads= vm.allThreads();
			} catch (RuntimeException e) {
				internalError(e);
			}
			if (threads != null) {
				System.out.println("Shivali: threads not null");
				Iterator initialThreads= threads.iterator();
				ThreadReference reference;
				while (initialThreads.hasNext()) {
					reference=(ThreadReference)(initialThreads.next());
					System.out.println("thread name "+reference.name());
				    if (reference.name()=="InvokeMethods"){
				      System.out.println("Shivali: Found the thread");
					  fThreadForInvokingRTMethods=reference;
					  JDIThread thrd= new JDIThread(this,reference);
				    }	
				}
			}	
		}	
	}
	*/
	
	public ThreadReference getThreadForRTMethods() {
		return fThreadForInvokingRTMethods;
	}
		
	public synchronized void createActivities(){
//		IJavaValue val = EvaluationEngineHelper.evaluateSomewhere(this, "x10.lang.Runtime.runtime");
		
		fActivities = new HashSet<IX10Activity>();
		String name = "queued";
		ObjectReference rtor = getX10RTObject();
		if (rtor==null) return; // hack for fix at demo
		List<Method> m= (List<Method>)rtor.referenceType().methodsByName("getWorkerPool");
		System.out.println("Shivali: Method found "+m.size());
		Method meth=null;
		for (Method m1: m) {
			meth = m1; 
		}
		List args=Collections.EMPTY_LIST;
		Value result=null;
		result = invokeX10RTMethod(fX10RT, meth, args);
		if (result!=null) {
		if (result instanceof ArrayReference) {
			if (((ArrayReference)result).length() >0) {
			List<Value> a = ((ArrayReference)result).getValues();
			for (Value activity: a){
				if (activity instanceof ObjectReference){
					//Field nm = (((ObjectReference)activity).referenceType()).fieldByName("name");
					name = getActivityString((ObjectReference)activity);
					X10Activity actvty = new X10Activity(this,name);
					addActivity(actvty);
				}
			}
			}
		}
		}
		//X10Activity actvty = new X10Activity(this, name,parent,place);
		//fActivities.add(actvty);
	}
	
	public void addActivity(IX10Activity a){
		synchronized(fActivities){
			fActivities.add(a);
		}
	}
	
	public Object[] getActivities(){
		if (fActivities==null) createActivities();
		synchronized(fActivities){
		   return fActivities.toArray(new Object[0]);
		}	 
	}
	
	public String getActivityString(ObjectReference a) {
		System.out.println("Inside getActivity String");
		List<Method> m = (List<Method>)a.referenceType().methodsByName("debuggerString");
		System.out.println("Shivali:getActivityString() Method found "+m.size());
		Method meth=null;
		for (Method m1: m) {
			meth = m1; 
		}
		List args=Collections.EMPTY_LIST;
		Value result=null;
		result = invokeX10RTMethod(a,meth, args);
		String name=null;
		if (result instanceof StringReference) {
			name = ((StringReference)result).value();
			System.out.println("getActivityString name = "+ name);
		}
		return name;
	}
	
	synchronized public void initializeX10RTObject() {
		if (fX10RT!=null) return;
		ReferenceType typeForRT=null;
//		while (typeForRT==null) {
		if (typeForRT==null)
			typeForRT = getX10RuntimeType();
			if (typeForRT==null) {
				try {
					Thread.sleep(1000);
					typeForRT = getX10RuntimeType();
					if (typeForRT==null) return;
				} catch (InterruptedException e) {
				}
			}
//		}
		// mmk: is there a reason to create these types?
//		IJavaType[] type = new IJavaType[classes.size()];
//		for (int i = 0; i < type.length; i++) {
//			type[i] = JDIType.createType(this, (Type)classes.get(i));
//		}
		System.out.println("STRATUM "+typeForRT.defaultStratum());
		Field RT;
		RT = ((ClassType)typeForRT).fieldByName("runtime");
		System.out.println("Shivali: field name is "+ RT.name());
		
		Value v = typeForRT.getValue(RT);
		System.out.println("Shivali: value is " + v.toString());
		if (v instanceof ObjectReference){
			fX10RT = (ObjectReference)v;
		}
//		System.out.println("InitializeX10RTObject() :Thread status before invokemethod"+ fThreadForInvokingRTMethods.status());
		//invokePlaceMethod();
	}

	private ReferenceType getX10RuntimeType() {
		VirtualMachine vm = getVM();
		List<ReferenceType> classes = (List<ReferenceType>)vm.classesByName("x10.runtime.DefaultRuntime_c");
		
		System.out.printf("got %d Classes for DefaultRuntime_c:", classes.size());
		for (Type c: classes){
			System.out.println("    "+ c.name());
		}
		
		// find 1st Java class (might there be other classes?)
		ReferenceType typeForRT = null;
		for (ReferenceType t: classes){
			if (t instanceof IJavaReferenceType) {
//				while (typeForRT==null) {
				if (typeForRT==null)
					typeForRT =(ReferenceType)((JDIReferenceType)t).getUnderlyingType();
					if (typeForRT==null) {
						try {
							Thread.sleep(1000);
							typeForRT =(ReferenceType)((JDIReferenceType)t).getUnderlyingType();
						} catch (InterruptedException e) {
						}
					}
//				}
				break;
			} else typeForRT = t;
		}
		return typeForRT;
	}		
    
	public ObjectReference getX10RTObject() {
//		while (fX10RT==null) {
		if (fX10RT==null)
			initializeX10RTObject();
			try {
			if (fX10RT==null) {
				Thread.sleep(1000);
				initializeX10RTObject();
			}
			} catch (InterruptedException e) {
			}
//		}
		return fX10RT;
	}
	
	public Value invokeX10RTMethod(ObjectReference obj, Method meth, List args) {
		//List<Method> m= (List<Method>)fX10RT.referenceType().methodsByName("getWorkerPool");
		//List<Method> m= (List<Method>)fX10RT.referenceType().methodsByName("getPlaces");
		//System.out.println("Shivali: Method found "+m.size());
		//Method meth=null;
		//for (Method m1: m) {
			//meth = m1; 
		//}
		Value result=null;
		int flags = ClassType.INVOKE_SINGLE_THREADED;
		//List args=Collections.EMPTY_LIST;
		try {
			synchronized(fThreadForInvokingRTMethods) {
				if (fThreadForInvokingRTMethods.isSuspended())
					System.out.println("Shivali: InvokeMethods is suspended");
				System.out.println("Shivali : Invoking Method with Thread status "+ fThreadForInvokingRTMethods.status());
				result=obj.invokeMethod(fThreadForInvokingRTMethods, meth, args, flags);
			}
		    System.out.println("Invocation complete");
		}
			catch (InvalidTypeException e) {
				//invokeFailed(e, timeout);
			} catch (ClassNotLoadedException e) {
				//invokeFailed(e, timeout);
			} catch (IncompatibleThreadStateException e) {
				//invokeFailed(JDIDebugModelMessages.JDIThread_Thread_must_be_suspended_by_step_or_breakpoint_to_perform_method_invocation_1, IJavaThread.ERR_INCOMPATIBLE_THREAD_STATE, e, timeout); 
			} catch (InvocationException e) {
				//invokeFailed(e, timeout);
			} catch (RuntimeException e) {
				//invokeFailed(e, timeout);
			}
			//if (result instanceof ArrayReference) {
				//System.out.println("Shivali : no of places " + ((ArrayReference)result).length());
			//}
		return result;	
	}

	public int getNumberOfPlaces() {
		ArrayReference pl;
		ReferenceType typeForPlace = null;
		typeForPlace = fX10RT.referenceType();
		Field place = ((ClassType)typeForPlace).fieldByName("places_");
		Value v=fX10RT.getValue(place);
		if (v instanceof ArrayReference){
		       pl=(ArrayReference)v;
               int noOfPlaces = pl.length();
               System.out.println("Shivali : no of places "+ noOfPlaces);
               return noOfPlaces;
		  }    
		
		return -1;
		
	} 
	protected ArrayReference getArrayReference(Value v) {
		if (v instanceof ArrayReference) {
			return (ArrayReference)v;
		}
		return null;
	}

	public String getModelIdentifier() {
		return Activator.PLUGIN_ID;
	}
//	/**
//	 * Factory method for creating new threads. Creates and returns a new thread
//	 * object for the underlying thread reference, or <code>null</code> if none
//	 * 
//	 * @param reference thread reference
//	 * @return JDI model thread
//	 */
//	
	
	
	/**
	 * Need to redefine (mainly copy) all references to fThreads here
	 * because we private methods prevent us from subclassing thread
	 * instantiation to create X10-facing JDI delegates
	 */
	
	/**
	 * Returns an iterator over the collection of threads. The
	 * returned iterator is made on a copy of the thread list
	 * so that it is thread safe. This method should always be
	 * used instead of getThreadList().iterator()
	 * @return an iterator over the collection of threads
	 */
	private Iterator getThreadIterator() {
		List threadList;
		synchronized (fThreads) {
			threadList= (List) fThreads.clone();
		}
		return threadList.iterator();
	}
	
	private void setThreadList(ArrayList threads) {
	    System.out.println("Entered setThreadList");
		fThreads = threads;
		if (fThreads == null)
			System.out.println("fThreads is null");
	}
	
	public JDIThread getSuspendedThread() {
		Iterator it = getThreadIterator();
		while(it.hasNext()){
			IThread thread = (IThread)it.next();
			if(thread.isSuspended())
				return (JDIThread)thread;
		}
		return null;
	}
	

	protected X10Thread createThread(ThreadReference thread) {
		//IX10Activity x10Thread= newActivityForThread(thread);
		X10Thread x10Thread= newThread(thread); 
		X10Place x10place=null;
		if (x10Thread == null) {
			return null;
		}
		if (isDisconnected()) {
			return null;
		}
		System.out.println("Number is "+x10Thread.uid);
		synchronized (fThreads) {   
			  fThreads.add(x10Thread);
		}
		
		if (thread.name().contains("pool-0")) {
			Iterator itr = fPlaces.iterator();
			boolean found=false;
			while (itr.hasNext()){
				x10place=((X10Place) itr.next());
				if (x10place.geName()== "place-0"){
					found=true;
					break;
				}	
			}
			if (!found){
				x10place = new X10Place(this,"place-0");
			
				fPlaces.add(x10place);
			}	
			x10place.addActiveActivity(x10Thread);		
		}
		x10Thread.fireCreationEvent();
		 
		return x10Thread;
	}
	
	protected IX10Activity newActivityForThread(ThreadReference reference) {
		return (IX10Activity)newThread(reference);
	}
	
	protected X10Thread newThread(ThreadReference reference) {
		try {
			System.out.println("Thread name is "+reference.name());
			if (reference.name().compareTo("InvokeMethods")==0){
				System.out.println("Shivali: Found the thread");
				fThreadForInvokingRTMethods=reference;
				X10Thread invThread= new X10Thread(this,reference);
				//fireSuspendEvent(DebugEvent.BREAKPOINT);
				if (fThreadForInvokingRTMethods.isSuspended())
					System.out.println("Shivali: InvokeMethods is suspended");
				while (!fThreadForInvokingRTMethods.isSuspended()) {
					System.out.println("Shivali: InvokeMethods is not suspended");
				}
				System.out.println("Thread status "+ fThreadForInvokingRTMethods.status());
				return invThread;
			}
				
			//IX10Activity[] activities = _application.getActivities();
			//IX10Activity a = activities[0];
			//return new SampleX10ActivityAsJDIThread(a, this, reference);
			return new X10Thread(this, reference);
		} catch (ObjectCollectedException exception) {
			// ObjectCollectionException can be thrown if the thread has already
			// completed (exited) in the VM.
		} catch (VMDisconnectedException exception) {
			//return false;
		} 
		return null;
	}
	
	public IThread[] getThreads() {
		synchronized (fThreads) {
			return (IThread[])fThreads.toArray(new IThread[0]);
		}
	}
	
	/**
	 * Removes all threads from this target's collection
	 * of threads, firing a terminate event for each.
	 */
	protected void removeAllThreads() {
		Iterator itr= getThreadIterator();
		while (itr.hasNext()) {
			X10Thread child= (X10Thread) itr.next();
			child.terminated();
		}
		synchronized (fThreads) {
		    fThreads.clear();
		}
	}
	
	/**
	 * @see IDebugTarget#hasThreads()
	 */
	public boolean hasThreads() {
		return fThreads.size() > 0;
	}
	
	/*
	 * Shivali: Commenting following out for purpose of
	 * initial testing. In, any case, the activity
	 * specific information should be obtained separately.
	
	protected JDIThread newThread(ThreadReference reference) {
		try {
			//return new X10Thread(this, reference);
		} catch (ObjectCollectedException exception) {
			// ObjectCollectionException can be thrown if the thread has already
			// completed (exited) in the VM.
		}
		return null;
	}
	
	
	// for stubbed testing
	public synchronized IThread[] getThreads() {
		HashSet<IThread> threads = new HashSet<IThread>();
		IX10Activity[] activities = _application.getActivities();
		IThread[] jdiThreads = super.getThreads();
		for (IX10Activity a: activities) {
			threads.add(new SampleX10ActivityAsThreadProxy(a, this));
//			threads.add(new SampleX10ActivityAsJDIThread(a, this, ((JDIThread)jdiThreads[0]).getUnderlyingThread()));
		}
		return threads.toArray(new IThread[0]);
	}
	*/
	
//	Set<IThread> _threads;
//	public synchronized IThread[] getThreads() {
//		// initialize activities for stubbed testing
//		if (_threads==null) {
//			IX10Activity[] activities = _application.getActivities();
////			_threads = new HashSet<IThread>();
//			Set threads = new HashSet<IThread>();
//			IThread[] jdiThreads = super.getThreads();
//			for (IX10Activity a: activities) {
//				threads.add(new SampleX10ActivityAsThreadProxy(a, this));
////				_threads.add(new SampleX10ActivityAsJDIThread(a, this, ((JDIThread)jdiThreads[0]).getUnderlyingThread()));
//			}
//		}
//		return _threads.toArray(new IThread[0]);
//	}

//	public ILaunch getLaunch() {
//		return fLaunch;
//	}
	
	/** The following are copied with slight modifications from superclass
	 *  to replace calls to private methods that we "override"
	 *  due to reliance of getThreadIterator on fThreads
	 */
	/**
	 * Notification that the given breakpoint has been removed
	 * from the breakpoint manager. If this target is not terminated,
	 * the breakpoint is removed from the underlying VM.
	 * 
	 * @param breakpoint the breakpoint has been removed from
	 *  the breakpoint manager.
	 */
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		if (!isAvailable()) {
			return;
		}		
		if (supportsBreakpoint(breakpoint)) {
			try {
				((JavaBreakpoint)breakpoint).removeFromTarget(this);
				getBreakpoints().remove(breakpoint);
				Iterator threads = getThreadIterator();
				while (threads.hasNext()) {
					((X10Thread)threads.next()).removeCurrentBreakpoint(breakpoint);
				}
			} catch (CoreException e) {
				logError(e);
			}
		}
	}
	public JDIThread findThread(ThreadReference tr) {

		Iterator iter= getThreadIterator();
		while (iter.hasNext()) {
			JDIThread thread = (JDIThread) iter.next();
			if (thread.getUnderlyingThread().equals(tr))
				return thread;
		}
		return null;
	}

	/**
	 * Returns whether this target has any threads which are suspended.
	 * @return true if any thread is suspended, false otherwise
	 * @since 3.2
	 */
	private boolean hasSuspendedThreads() {
		Iterator it = getThreadIterator();
		while(it.hasNext()){
			IThread thread = (IThread)it.next();
			if(thread.isSuspended())
				return true;
		}
		return false;
	}
	/**
	 * @see IJavaDebugTarget#isOutOfSynch()
	 */
	public boolean isOutOfSynch() throws DebugException {
		Iterator threads= getThreadIterator();
		while (threads.hasNext()) {
			JDIThread thread= (JDIThread)threads.next();
			if (thread.isOutOfSynch()) {
				return true;
			}
		}
		return false;
	}	
	
	/**
	 * @see IJavaDebugTarget#mayBeOutOfSynch()
	 */
	public boolean mayBeOutOfSynch() {
		Iterator threads= getThreadIterator();
		while (threads.hasNext()) {
			JDIThread thread= (JDIThread)threads.next();
			if (thread.mayBeOutOfSynch()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Notifies threads that they have been suspended
	 */
	protected void suspendThreads() {
		Iterator threads = getThreadIterator();
		while (threads.hasNext()) {
			((X10Thread)threads.next()).suspendedByVM();
		}
	}

	/**
	 * Notifies threads that they have been resumed
	 */
	protected void resumeThreads() throws DebugException {
		Iterator threads = getThreadIterator();
		while (threads.hasNext()) {
			((X10Thread)threads.next()).resumedByVM();
		}
	}

}
