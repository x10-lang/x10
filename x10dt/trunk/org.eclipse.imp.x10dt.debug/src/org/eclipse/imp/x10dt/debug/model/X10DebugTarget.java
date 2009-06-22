package org.eclipse.imp.x10dt.debug.model;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdi.hcr.ReferenceType;
import org.eclipse.jdi.internal.ReferenceTypeImpl;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaReferenceType;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIType;
import org.eclipse.jdt.internal.debug.eval.ast.instructions.Value;

import com.sun.jdi.ClassObjectReference;
import com.sun.jdi.Field;
import com.sun.jdi.VirtualMachine;

public class X10DebugTarget extends JDIDebugTarget {

	private IJavaDebugTarget fJDebugTarget;
	private ClassObjectReference fX10RT;

	public X10DebugTarget(ILaunch launch, VirtualMachine jvm, String name,
			boolean supportTerminate, boolean supportDisconnect,
			IProcess process, boolean resume) {
		super(launch, jvm, name, supportTerminate, supportDisconnect, process, resume);
		initialize();        
	}

	protected void initialize() {
		setJDIDebugTarget();
		setX10RTObject();
	}

	
	//******SHIVALI: I don't fully understand this.
	//******According to tutorial, DebugContext is available from debug views via DebugContextProvider.  Not available directly from here.
	//******But why do you need it?  X10DebugTarget should inherit from JDIDebugTarget (that's what you proposed, right?)
	//******So the java debug target identified by this method is just "this", isn't it?
	//******If for some reason this doesn't work, I'd say the place to do it would be in the constructor, where launch is provided as parameter
	//******Or equivalently, pass launch as param from constructor to this method via initialize.
	private void setJDIDebugTarget() {
		IAdaptable context = getDebugContext();
		if  (context == null) {
			ILaunch[] launches =
				DebugPlugin.getDefault().getLaunchManager().getLaunches();
			if (launches.length > 0) {
				context = launches[launches.length - 1];
			}
		}
		ILaunch launch= (ILaunch)context;
		fJDebugTarget = (IJavaDebugTarget) launch.getDebugTarget();

	}

	private void setX10RTObject() {
		VirtualMachine vm = ((JDIDebugTarget) fJDebugTarget).getVM();
		List classes = vm.classesByName(gDefaultRuntime_ch);
		IJavaType type = newIJavaType; //a single instance of runtime so list of 
		//classes contains only one element
		type = JDIType.createType(this, (Type)classes.get(i));
		if (type instanceof IJavaReferenceType) 
			fX10RT =((ReferenceType)type.getUnderlyingType()).ClassObject();
	}		


	public int getNumberOfPlaces() {
		ReferenceTypeImpl type = (ReferenceTypeImpl) fX10RT.referenceType();
		Field place = type.fieldByName(gplaces_h);
		Value v = fX10RT.getValue(place);
	} 
}
