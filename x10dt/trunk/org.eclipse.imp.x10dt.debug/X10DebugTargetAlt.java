package org.eclipse.imp.x10dt.debug.model.shivali;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
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
import org.eclipse.imp.x10dt.debug.model.impl.X10Application;
import org.eclipse.imp.x10dt.debug.model.impl.X10Activity;
import org.eclipse.imp.x10dt.debug.model.impl.stub.SampleX10ActivityAsThreadProxy;
import org.eclipse.imp.x10dt.debug.model.impl.stub.SampleX10Application;
import org.eclipse.imp.x10dt.debug.model.impl.stub.SampleX10ModelFactory;
import org.eclipse.imp.x10dt.debug.model.IX10Activity;
import org.eclipse.imp.x10dt.debug.model.IX10Application;
import org.eclipse.imp.x10dt.debug.model.X10DebugModel;
import org.eclipse.imp.x10dt.debug.model.X10Thread;
import org.eclipse.imp.x10dt.debug.model.X10Place;
import com.sun.jdi.ReferenceType;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaReferenceType;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.internal.debug.core.EventDispatcher;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugElement;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugModelMessages;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIReferenceType;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;
import org.eclipse.jdt.internal.debug.core.model.JDIType;
import org.eclipse.jdt.debug.core.IJavaBreakpoint;
import org.eclipse.debug.core.IBreakpointManager;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.ClassObjectReference;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.ObjectCollectedException;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;


public class X10DebugTargetAlt extends JDIDebugTarget implements IDebugTarget, ILaunchListener, IDebugEventSetListener {

	//private X10DebugTargetAlt fX10DebugTarget;
	private HashSet<IX10Activity> fActivities;
	private ObjectReference fX10RT;
	private ThreadReference fThreadForInvokingRTMethods;
	//private ILaunch fLaunch = null;
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
		setEventDispatcher(new EventDispatcher(this));
		setRequestTimeout(JDIDebugModel.getPreferences().getInt(JDIDebugModel.PREF_REQUEST_TIMEOUT));
		initializeRequests();
		setThreadList(new ArrayList(5));
		initializeState();
		if (this.getVM() == null)
			System.out.println("after initialize state.vm is null");
		initializeBreakpoints();
		getLaunch().addDebugTarget(this);
        DebugPlugin plugin = DebugPlugin.getDefault();
		plugin.addDebugEventListener(this);
		fireCreationEvent();
		// begin handling/dispatching events after the creation event is handled by all listeners
		plugin.asyncExec(new Runnable() {
			public void run() {
				EventDispatcher dispatcher = getEventDispatcher();
		        if (dispatcher != null) {
		            Thread t= new Thread(dispatcher, X10DebugModel.getPluginIdentifier() + JDIDebugModelMessages.JDIDebugTarget_JDI_Event_Dispatcher); 
		            t.setDaemon(true);
		            t.start();
		        }
			}
		});
		//_application = SampleX10ModelFactory.getApplication();
		//initializeThreadForInvokeMethod();
		if (this.getVM() == null)
			System.out.println("before initialize end.vm is null");
	}
	
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

	/*
	public String getName() throws DebugException {
		return "TESTING --- X10 Root Activity";
	}
	*/
	
	public String getModelIdentifier() {
		return Activator.PLUGIN_ID+".model";
	}
	/**
	 * Factory method for creating new threads. Creates and returns a new thread
	 * object for the underlying thread reference, or <code>null</code> if none
	 * 
	 * @param reference thread reference
	 * @return JDI model thread
	 */
	
	public boolean hasThreads() {
		return fThreads.size() > 0;
	}
	
	private Iterator getThreadIterator() {
		List threadList;
		synchronized (fThreads) {
			threadList= (List) fThreads.clone();
		}
		return threadList.iterator();
	}
	
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
	
	
	private void setThreadList(ArrayList threads) {
	    System.out.println("Entered setThreadList");
		fThreads = threads;
		if (fThreads == null)
			System.out.println("fThreads is null");
	}
	
	protected X10Thread createThread(ThreadReference thread) {
		X10Thread x10Thread= newThread(thread);
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
		
		x10Thread.fireCreationEvent();
		return x10Thread;
	}
	
	protected X10Thread newThread(ThreadReference reference) {
		try {
			System.out.println("Thread name is "+reference.name());
			
			return new X10Thread(this, reference);
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
	public IThread[] getThreads() {
		HashSet<IThread> threads = new HashSet<IThread>();
		IX10Activity[] activities = _application.getActivities();
		for (IX10Activity a: activities) {
			threads.add(new SampleX10ActivityAsThreadProxy(a, this));
		}
		return threads.toArray(new IThread[threads.size()]);
	}
	*/
//	public ILaunch getLaunch() {
//		return fLaunch;
//	}
}
