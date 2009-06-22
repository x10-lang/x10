package org.eclipse.imp.x10dt.debug.model.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.imp.x10dt.debug.Activator;
import org.eclipse.imp.x10dt.debug.model.IX10Activity;
import org.eclipse.imp.x10dt.debug.model.IX10Application;
import org.eclipse.imp.x10dt.debug.model.impl.X10Application;
import org.eclipse.imp.x10dt.debug.model.impl.stub.SampleX10ActivityAsJDIThread;
import org.eclipse.imp.x10dt.debug.model.impl.stub.SampleX10ActivityAsThreadProxy;
import org.eclipse.imp.x10dt.debug.model.impl.stub.SampleX10Application;
import org.eclipse.imp.x10dt.debug.model.impl.stub.SampleX10ModelFactory;
import org.eclipse.jdi.TimeoutException;
import org.eclipse.jdt.debug.core.IJavaBreakpoint;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaReferenceType;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.internal.debug.core.EventDispatcher;
import org.eclipse.jdt.internal.debug.core.IJDIEventListener;
import org.eclipse.jdt.internal.debug.core.breakpoints.JavaBreakpoint;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugElement;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugModelMessages;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIReferenceType;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;
import org.eclipse.jdt.internal.debug.core.model.JDIType;
import org.eclipse.ui.activities.IActivity;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.ClassObjectReference;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.ObjectCollectedException;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Type;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;


public class X10DebugTargetAlt extends JDIDebugTarget implements IDebugTarget, ILaunchListener, IDebugEventSetListener {

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
			}
			return true;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.debug.core.IJDIEventListener#wonSuspendVote(com.sun.jdi.event.Event, org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget)
		 */
		public void wonSuspendVote(Event event, JDIDebugTarget target) {
			// do nothing
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
}
	
	private IDebugTarget fJDebugTarget;
	//S//private ClassObjectReference fX10RT;
	//private X10DebugTargetAlt fX10DebugTarget;
	private HashSet<IX10Activity> fActivities;
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
		_application = SampleX10ModelFactory.getApplication();
		setThreadList(new ArrayList(5)); // really this belongs in constructor, but it would have to go *before* super(), which contains call to this (initialize) method.
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
		IBreakpoint[] bps = manager.getBreakpoints("org.eclipse.imp.x10.debug.model");
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
	private void initializeThreadForInvokeMethod() {
	    Thread th = new Thread(new Runnable(){
	    	public void run() {
	    		ArrayList list = new ArrayList();
	    		try{
	    		synchronized(list){
	    			while (list.size()<=0)
	    				wait();
	    		}
	    		}catch (InterruptedException e){
	            }
	    	}
	    });	
	    th.setDaemon(true);
	    th.setName("InvokeMethods");
	    th.start();
	    while (!th.isAlive());
	    List threads= null;
		VirtualMachine vm = getVM();
		if (vm != null) {
			try {
				threads= vm.allThreads();
			} catch (RuntimeException e) {
				internalError(e);
			}
			if (threads != null) {
				Iterator initialThreads= threads.iterator();
				ThreadReference reference;
				while (initialThreads.hasNext()) {
					reference=(ThreadReference)(initialThreads.next());
				    if (reference.name()=="InvokeMethods"){
					  fThreadForInvokingRTMethods=reference;
					  JDIThread thrd= new JDIThread(this,reference);
				    }	
				}
			}	
		}	
	}
	
	
	public void createActivities(){
		//X10Activity actvty = new X10Activity(this, name,parent,place);
		//fActivities.add(actvty);
	}
	
	public Object[] getActivities(){
		return fActivities.toArray();
	}
	
	private void setX10RTObject() {
		//S//VirtualMachine vm = ((JDIDebugTarget) fJDebugTarget).getVM();
		//S//List classes = vm.classesByName("DefaultRuntime_c");
		VirtualMachine vm = this.getVM();
		List classes = vm.classesByName("x10.lang.Runtime");
		Type className;
		//System.out.println("got Class, size = "+ classes.size());
		for (int i=0; i<classes.size(); i++ ){
			className = (Type)classes.get(i);
			//System.out.println("class "+i+" = "+ className.name());
		}
		
		IJavaType[] type = new IJavaType[classes.size()];
		for (int i = 0; i < type.length; i++) {
			type[i] = JDIType.createType(this, (Type)classes.get(i));
		}
		ReferenceType typeForRT=null;
		
		if (type[0] instanceof IJavaReferenceType) 
			typeForRT =(ReferenceType)((JDIReferenceType)type[0]).getUnderlyingType();
		Field RT;
		RT = ((ClassType)typeForRT).fieldByName("runtime");
		//System.out.println("Shivali:field name is "+ RT.name());
		
		Value v = typeForRT.getValue(RT);
		System.out.println("Shivali: value is " + v.toString());
		if (v instanceof ObjectReference){
			fX10RT = (ObjectReference)v;
		}
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
		return Activator.PLUGIN_ID+".model";
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

	protected X10Thread createThread(ThreadReference thread) {
		IX10Activity x10Thread= newActivityForThread(thread);
		X10Place x10place=null;
		if (x10Thread == null) {
			return null;
		}
		if (isDisconnected()) {
			return null;
		}
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
		
		((X10Thread)x10Thread).fireCreationEvent();
		return (X10Thread)x10Thread;
	}
	
	protected IX10Activity newActivityForThread(ThreadReference reference) {
		return (IX10Activity)newThread(reference);
	}
	
	protected JDIThread newThread(ThreadReference reference) {
		try {
			System.out.println("Thread name is "+reference.name());
			IX10Activity[] activities = _application.getActivities();
			IX10Activity a = activities[0];
			return new SampleX10ActivityAsJDIThread(a, this, reference/*((JDIThread)jdiThreads[0]).getUnderlyingThread()*/);
//			return new X10Thread(this, reference);
		} catch (ObjectCollectedException exception) {
			// ObjectCollectionException can be thrown if the thread has already
			// completed (exited) in the VM.
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
