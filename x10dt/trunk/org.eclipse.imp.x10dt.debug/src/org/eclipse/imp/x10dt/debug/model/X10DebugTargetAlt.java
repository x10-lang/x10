package org.eclipse.imp.x10dt.debug.model;

import java.util.HashSet;
import java.util.List;

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
import org.eclipse.imp.x10dt.debug.model.impl.stub.SampleX10ActivityAsThreadProxy;
import org.eclipse.imp.x10dt.debug.model.impl.stub.SampleX10Application;
import org.eclipse.imp.x10dt.debug.model.impl.stub.SampleX10ModelFactory;
import org.eclipse.jdi.hcr.ReferenceType;
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

	private IDebugTarget fJDebugTarget;
	private ClassObjectReference fX10RT;
	private ILaunch fLaunch = null;
	private EventDispatcher fEventDispatcher;
	private IX10Application _application;
	public X10DebugTargetAlt(ILaunch launch, VirtualMachine jvm, String name,
			boolean supportTerminate, boolean supportDisconnect,
			IProcess process, boolean resume) {
		super(launch, jvm, name, supportTerminate, supportDisconnect, process, resume);
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
		initializeState();
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
		_application = SampleX10ModelFactory.getApplication();
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
	
	private void setX10RTObject() {
		VirtualMachine vm = ((JDIDebugTarget) fJDebugTarget).getVM();
		List classes = vm.classesByName("DefaultRuntime_c");
		IJavaType[] type = new IJavaType[classes.size()];
		for (int i = 0; i < type.length; i++) {
			type[i] = JDIType.createType((JDIDebugTarget) fJDebugTarget, (Type)classes.get(i));
		}
		if (type[0] instanceof IJavaReferenceType) 
			fX10RT =((ClassType)((JDIReferenceType)type[0]).getUnderlyingType()).classObject();
	}		


	public int getNumberOfPlaces() {
		ReferenceType type = (ReferenceType) fX10RT.referenceType();
		Field place = ((ClassType)type).fieldByName("places_");
		Value v = (ObjectReference)fX10RT.getValue(place);
		if (v instanceof ArrayReference){
	          return getArrayReference(v).length();
		}
		return -1;
		
	} 
	protected ArrayReference getArrayReference(Value v) {
		if (v instanceof ArrayReference) {
			return (ArrayReference)v;
		}
		return null;
	}

	public String getName() throws DebugException {
		return "TESTING --- X10 Root Activity";
	}
	
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
	protected JDIThread newThread(ThreadReference reference) {
		try {
			return new X10Thread(this, reference);
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
	
//	public ILaunch getLaunch() {
//		return fLaunch;
//	}
}
