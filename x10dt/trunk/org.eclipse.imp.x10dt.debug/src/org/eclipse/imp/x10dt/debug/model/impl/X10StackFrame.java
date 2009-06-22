package org.eclipse.imp.x10dt.debug.model.impl;


import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Comparator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.debug.core.IJavaClassType;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaReferenceType;
import org.eclipse.imp.x10dt.debug.Activator;
import org.eclipse.imp.x10dt.debug.model.IX10StackFrame;
import org.eclipse.imp.x10dt.debug.model.IX10Variable;
import org.eclipse.imp.x10dt.debug.model.impl.X10DebugElement;
import org.eclipse.imp.x10dt.debug.model.impl.X10FieldVariable;
import org.eclipse.jdt.internal.debug.core.JDIDebugPlugin;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDILocalVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIReferenceType;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIThisVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugModelMessages;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.Field;
import com.sun.jdi.NativeMethodException;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;

public class X10StackFrame extends JDIStackFrame implements IX10StackFrame {
	
	private StackFrame fStackFrame;
	
	private int fDepth = -2;
	
	private Location fLocation;
	
	private ObjectReference fThisObject;
	
	private boolean fLocalsAvailable = true;
	
	private String fReceivingTypeName;
	//JDIStackFrame _jdiStackFrame;
	//JDIThread _jdiThread;
	List fVariables;
	private boolean fRefreshVariables=true;
	
	public X10StackFrame (JDIThread thread, StackFrame jdiStackFrame, int depth) {
		super(thread,jdiStackFrame,depth);
		bind(jdiStackFrame, depth, true);
		//_jdiStackFrame = jdiStackFrame;
	}

	public X10StackFrame bind(StackFrame frame, int depth){
		return null;
	}
	
	public X10StackFrame bind(StackFrame frame, int depth, boolean isX10Stack){
		synchronized (getThread()) {
			if (fDepth == -2) {
				// first initialization
				fStackFrame = frame;
				fDepth = depth;
				fLocation = frame.location();
				return this;
			} else if (depth == -1) {
				// mark as invalid
				fDepth = -1;
				fStackFrame = null;
				return null;
			} else if (fDepth == depth) {
				Location location = frame.location();
				Method method = location.method();
				if (method.equals(fLocation.method())) {
					try {
						if (method.declaringType().defaultStratum().equals("Java") || //$NON-NLS-1$
						    equals(getSourceName(location), getSourceName(fLocation))) {
							// TODO: what about receiving type being the same?
							fStackFrame = frame;
							fLocation = location;
							clearCachedData();
							return this;
						}
					} catch (DebugException e) {
					}
				}
			}
			// invalidate this frame
			bind(null, -1, true);
			// return a new frame
			return new X10StackFrame((JDIThread)getThread(), frame, depth);
		}

	}
	
	public String getModelIdentifier() {
		return Activator.getUniqueIdentifier();
	}
	
	// Caution: As of now, setUnderlyingStackFrame is called with null
	// It should be handled appropriately in future.
	protected void setUnderlyingStackFrame(StackFrame frame) {
		super.setUnderlyingStackFrame(frame);
		if (frame==null) {
			fRefreshVariables=true;
		}
	}
	
	public IVariable[] getVariables() throws DebugException {
		List list = getVariables0();
		return (IVariable[])list.toArray(new IVariable[list.size()]);
	}
	
	public List getVariables0() throws DebugException {
		synchronized (getThread()) {
			if (fVariables == null) {
				
				// throw exception if native method, so variable view will update
				// with information message
				if (isNative()) {
					requestFailed(JDIDebugModelMessages.JDIStackFrame_Variable_information_unavailable_for_native_methods, null); 
				}
				
				Method method= getUnderlyingMethod();
				fVariables= new ArrayList();
				// #isStatic() does not claim to throw any exceptions - so it is not try/catch coded
				if (method.isStatic()) {
					// add statics
					List allFields= null;
					ReferenceType declaringType = method.declaringType();
					try {
						allFields= declaringType.allFields();
					} catch (RuntimeException e) {
						//targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.JDIStackFrame_exception_retrieving_fields,new String[] {e.toString()}), e); 
						// execution will not reach this line, as 
						// #targetRequestFailed will throw an exception					
						//return Collections.EMPTY_LIST;
						return null;
					}
					if (allFields != null) {
						Iterator fields= allFields.iterator();
						while (fields.hasNext()) {
							Field field= (Field) fields.next();
							if (field.isStatic()) {
								fVariables.add(new X10FieldVariable((JDIDebugTarget)getDebugTarget(), field, declaringType));
							}
						}
						Collections.sort(fVariables, new Comparator() {
							public int compare(Object a, Object b) {
								X10FieldVariable v1= (X10FieldVariable)a;
								X10FieldVariable v2= (X10FieldVariable)b;
								try {
									return v1.getName().compareToIgnoreCase(v2.getName());
								} catch (DebugException de) {
									//logError(de);
									return -1;
								}
							}
						});
					}
				} else {
					// add "this"
					ObjectReference t= getUnderlyingThisObject();
					if (t != null) {
						fVariables.add(new X10ThisVariable((JDIDebugTarget)getDebugTarget(), t));
					}
				}
				Iterator variables= getUnderlyingVisibleVariables().iterator();
				while (variables.hasNext()) {
					LocalVariable var= (LocalVariable) variables.next();
					fVariables.add(new X10LocalVariable(this, var));
				}
			} else if (fRefreshVariables) {
				updateVariables();
			}
			fRefreshVariables = false;
			return fVariables;
		}
		/*IVariable[] baseVariables = _jdiStackFrame.getVariables();
		IX10Variable[] x10Variables = new IX10Variable[baseVariables.length];
		int i=0;
		for (IVariable v: baseVariables) {
			x10Variables[i] = new X10DelegatingVariable(getDebugTarget(), v);
		}
		return x10Variables;
		*/
	}
	
	protected void updateVariables() throws DebugException {
		if (fVariables == null) {
			return;
		}

		Method method= getUnderlyingMethod();
		int index= 0;
		if (!method.isStatic()) {
			// update "this"
			ObjectReference thisObject;
			try {
				thisObject= getUnderlyingThisObject();
			} catch (DebugException exception) {
				if (!getThread().isSuspended()) {
					thisObject= null;
				} else {
					throw exception;
				}
			}
			JDIThisVariable oldThisObject= null;
			if (!fVariables.isEmpty() && fVariables.get(0) instanceof JDIThisVariable) {
				oldThisObject= (JDIThisVariable) fVariables.get(0);
			}
			if (thisObject == null && oldThisObject != null) {
				// removal of 'this'
				fVariables.remove(0);
				index= 0;
			} else {
				if (oldThisObject == null && thisObject != null) {
					// creation of 'this'
					oldThisObject= new X10ThisVariable((JDIDebugTarget)getDebugTarget(),thisObject);
					fVariables.add(0, oldThisObject);
					index= 1;
				} else {
					if (oldThisObject != null) {
						// 'this' still exists, replace with new 'this' if a different receiver
						if (!((X10ThisVariable)oldThisObject).retrieveValue().equals(thisObject)) {
							fVariables.remove(0);
							fVariables.add(0, new X10ThisVariable((JDIDebugTarget)getDebugTarget(),thisObject));
						}
						index= 1;
					}
				}
			}
		}

		List locals= null;
		try {
			locals= getUnderlyingStackFrame().visibleVariables();
		} catch (AbsentInformationException e) {
			locals= Collections.EMPTY_LIST;
		} catch (NativeMethodException e) {
			locals= Collections.EMPTY_LIST;
		} catch (RuntimeException e) {
			//targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.JDIStackFrame_exception_retrieving_visible_variables,new String[] {e.toString()}), e); 
			// execution will not reach this line, as 
			// #targetRequestFailed will throw an exception			
			//return;
		}
		int localIndex= -1;
		while (index < fVariables.size()) {
			Object var= fVariables.get(index);
			if (var instanceof JDILocalVariable) {
				X10LocalVariable local= (X10LocalVariable) fVariables.get(index);
				localIndex= locals.indexOf(local.getLocal());
				if (localIndex >= 0) {
					// update variable with new underling JDI LocalVariable
					local.setLocal((LocalVariable) locals.get(localIndex));
					locals.remove(localIndex);
					index++;
				} else {
					// remove variable
					fVariables.remove(index);
				}
			} else {
				//field variable of a static frame
				index++;
			}
		}

		// add any new locals
		Iterator newOnes= locals.iterator();
		while (newOnes.hasNext()) {
			X10LocalVariable local= new X10LocalVariable(this, (LocalVariable) newOnes.next());
			fVariables.add(local);
		}
	}
    
	
	protected StackFrame getUnderlyingStackFrame() throws DebugException {
		IThread th = getThread();
		synchronized (th) {
			if (fStackFrame == null) {
				if (fDepth == -1) {
					throw new DebugException(new Status(IStatus.ERROR, JDIDebugPlugin.getUniqueIdentifier(), IJavaStackFrame.ERR_INVALID_STACK_FRAME, JDIDebugModelMessages.JDIStackFrame_25, null)); 
				}
				if (th.isSuspended()) {
					// re-index stack frames - See Bug 47198
					((X10Thread)th).computeStackFrames();
					if (fDepth == -1) {
						// If depth is -1, then this is an invalid frame
						throw new DebugException(new Status(IStatus.ERROR, JDIDebugPlugin.getUniqueIdentifier(), IJavaStackFrame.ERR_INVALID_STACK_FRAME, JDIDebugModelMessages.JDIStackFrame_25, null)); 
					}
				} else {
					throw new DebugException(new Status(IStatus.ERROR, JDIDebugPlugin.getUniqueIdentifier(), IJavaThread.ERR_THREAD_NOT_SUSPENDED, JDIDebugModelMessages.JDIStackFrame_25, null)); 
				}
			}
			return fStackFrame;
		}
	}
	
	protected boolean exists() {
		synchronized (getThread()) {
			return fDepth != -1;
		}
	}
	
	public Method getUnderlyingMethod() {
		synchronized (getThread()) {
			return fLocation.method();
		}
	}
	
	public IJavaVariable[] getLocalVariables() throws DebugException {
		List list = getUnderlyingVisibleVariables();
		IJavaVariable[] locals = new IJavaVariable[list.size()];
		for (int i = 0; i < list.size(); i++) {
			locals[i] = new X10LocalVariable(this, (LocalVariable)list.get(i));
		}
		return locals;
	}
	
	public boolean wereLocalsAvailable() {
		return fLocalsAvailable;
	}
	
	public IJavaVariable findVariable(String varName) throws DebugException {
		if (isNative()) {
			return null;
		}
		IVariable[] variables = getVariables();
		IJavaVariable thisVariable= null;
		for (int i = 0; i < variables.length; i++) {
			IJavaVariable var = (IJavaVariable) variables[i];
			if (var.getName().equals(varName)) {
				return var;
			}
			if (var instanceof X10ThisVariable) {
				// save for later - check for instance and static variables
				thisVariable= var;
			}
		}

		if (thisVariable != null) {
			IVariable[] thisChildren = thisVariable.getValue().getVariables();
			for (int i = 0; i < thisChildren.length; i++) {
				IJavaVariable var= (IJavaVariable) thisChildren[i];
				if (var.getName().equals(varName)) {
					return var;
				}
			}
		}

		return null;

	}
	
	public String getReceivingTypeName() throws DebugException {
		if (fStackFrame == null || fReceivingTypeName == null) {
			try {
				if (isObsolete()) {
					fReceivingTypeName=JDIDebugModelMessages.JDIStackFrame__unknown_receiving_type__2; 
				} else {
					ObjectReference thisObject = getUnderlyingThisObject();
					if (thisObject == null) {
						fReceivingTypeName = getDeclaringTypeName();
					} else {
						fReceivingTypeName = JDIReferenceType.getGenericName(thisObject.referenceType());
					}
				}
			} catch (RuntimeException e) {
				if (getThread().isSuspended()) {
					//targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.JDIStackFrame_exception_retrieving_receiving_type, new String[] {e.toString()}), e); 
				}
				return JDIDebugModelMessages.JDIStackFrame__unknown_receiving_type__2; 
			}
		}
		return fReceivingTypeName;
	}
	
	/**
	 * Sets whether locals were available. If the setting is
	 * not the same as the current value, a change event is
	 * fired such that a UI client can update.
	 * 
	 * @param available whether local variable information is
	 * 	available for this stack frame.
	 */
	private void setLocalsAvailable(boolean available) {
		if (available != fLocalsAvailable) {
			fLocalsAvailable = available;
			fireChangeEvent(DebugEvent.STATE);
		}
	}	

	protected ObjectReference getUnderlyingThisObject() throws DebugException {
		synchronized (getThread()) {
			if ((fStackFrame == null || fThisObject == null) && !isStatic()) {
				try {
					fThisObject = getUnderlyingStackFrame().thisObject();
				} catch (RuntimeException e) {
					//targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.JDIStackFrame_exception_retrieving_this,new String[] {e.toString()}), e); 
					// execution will not reach this line, as 
					// #targetRequestFailed will throw an exception			
					return null;
				}
			}
			return fThisObject;
		}
	}
	
	protected List getUnderlyingVisibleVariables() throws DebugException {
		synchronized (getThread()) {
			List variables= Collections.EMPTY_LIST;
			try {
				variables= getUnderlyingStackFrame().visibleVariables();
			} catch (AbsentInformationException e) {
				setLocalsAvailable(false);
			} catch (NativeMethodException e) {
				setLocalsAvailable(false);
			} catch (RuntimeException e) {
				//targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.JDIStackFrame_exception_retrieving_visible_variables_2,new String[] {e.toString()}), e); 
			}
			return variables;
		}
	}
	
	protected void setVariables(List variables) {
		fVariables = variables;
	}
	
	
	public int getLineNumber() throws DebugException {
		synchronized (getThread()) {
			try {
				return fLocation.lineNumber();
			} catch (RuntimeException e) {
				if (getThread().isSuspended()) {
					//targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.JDIStackFrame_exception_retrieving_line_number, new String[] {e.toString()}), e); 
				}
			}
		}
		return -1;
	}
	
	public String getSourceName() throws DebugException {
		synchronized (getThread()) {
			return getSourceName(fLocation); 
		}
	}
	
	public String getSourcePath(String stratum) throws DebugException {
		synchronized (getThread()) {
			try {
				return fLocation.sourcePath(stratum);
			} catch (AbsentInformationException e) {
			} catch (RuntimeException e) {
				//targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.JDIStackFrame_exception_retrieving_source_path, new String[] {e.toString()}), e); 
			}
		}
		return null;
	}
	
	public String getSourcePath() throws DebugException {
		synchronized (getThread()) {
			try {
				return fLocation.sourcePath();
			} catch (AbsentInformationException e) {
			} catch (RuntimeException e) {
				//targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.JDIStackFrame_exception_retrieving_source_path, new String[] {e.toString()}), e); 
			}
		}
		return null;
	}
	
	public int getLineNumber(String stratum) throws DebugException {
		synchronized (getThread()) {
			try {
				return fLocation.lineNumber(stratum);
			} catch (RuntimeException e) {
				if (getThread().isSuspended()) {
					//targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.JDIStackFrame_exception_retrieving_line_number, new String[] {e.toString()}), e); 
				}
			}
		}
		return -1;
	}
	
	public String getSourceName(String stratum) throws DebugException {
		synchronized (getThread()) {
			try {
				return fLocation.sourceName(stratum);
			} catch (AbsentInformationException e) {
			} catch (NativeMethodException e) {
			} catch (RuntimeException e) {
				//targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.JDIStackFrame_exception_retrieving_source_name, new String[] {e.toString()}), e); 
			}
		}
		return null;
	}
	
	private boolean equals(Object o1, Object o2) {
		if (o1 == null) {
			return o2 == null;
		} else {
			return o1.equals(o2);
		}
	}
	
	private String getSourceName(Location location) throws DebugException {
		try {
			return location.sourceName();
		} catch (AbsentInformationException e) {
			return null;
		} catch (NativeMethodException e) {
			return null;
		} catch (RuntimeException e) {
			//targetRequestFailed(MessageFormat.format(JDIDebugModelMessages.JDIStackFrame_exception_retrieving_source_name, new String[] {e.toString()}), e); 
		}
		return null;
	}	
	
	private void clearCachedData() {
		fThisObject= null;
		fReceivingTypeName= null;	
	}
	
	// Methods for the interface IJavaStackFrame 
	
	/*
	public boolean canDropToFrame() {
        return supportsDropToFrame();
    }
	
	public void dropToFrame() throws DebugException {
		_jdiStackFrame.dropToFrame();
	}
	
	public boolean supportsDropToFrame() {
		return _jdiStackFrame.supportsDropToFrame();
	}

	public boolean canStepWithFilters() {
		return _jdiStackFrame.canStepWithFilters();
	}
	
	public void stepWithFilters() throws DebugException {
		_jdiStackFrame.stepWithFilters();
	}
	
	public boolean isNative() throws DebugException {
		return _jdiStackFrame.isNative();
	}
	
	public boolean isConstructor() throws DebugException {
		return _jdiStackFrame.isConstructor();
	}
	
	public boolean isStaticInitializer() throws DebugException {
		return _jdiStackFrame.isStaticInitializer();
	}
	
	public boolean isSynchronized() throws DebugException {
		return _jdiStackFrame.isSynchronized();
	}
	
	public boolean isOutOfSynch() throws DebugException {
		return _jdiStackFrame.isOutOfSynch();
	}
	
	public boolean isObsolete() {
		return _jdiStackFrame.isObsolete();
	}
	
	public boolean isFinal() throws DebugException {
		return _jdiStackFrame.isFinal();
	}
	
	public boolean isSynthetic() throws DebugException {
		return _jdiStackFrame.isSynthetic();
	}
	
	public boolean isPublic() throws DebugException {
		return _jdiStackFrame.isPublic();
	}
	
	public boolean isPrivate() throws DebugException {
		return _jdiStackFrame.isPrivate();
	}
	
	public boolean isProtected() throws DebugException {
		return _jdiStackFrame.isProtected();
	}
	
	public boolean isPackagePrivate() throws DebugException {
		return _jdiStackFrame.isPackagePrivate();
	}
	
	public boolean isStatic() throws DebugException {
		return _jdiStackFrame.isStatic();
	}
	
	
	
	public String getDeclaringTypeName() throws DebugException {
		return _jdiStackFrame.getDeclaringTypeName();
	}
	
	public String getReceivingTypeName() throws DebugException {
		return _jdiStackFrame.getReceivingTypeName();
	}
	
	public String getSignature() throws DebugException {
		return _jdiStackFrame.getSignature();
	}
	
	public String getMethodName() throws DebugException {
		return _jdiStackFrame.getMethodName();
	}
	
	public List getArgumentTypeNames() throws DebugException {
		return _jdiStackFrame.getArgumentTypeNames();
	}
	
	public IJavaVariable findVariable(String varName) throws DebugException {
		return _jdiStackFrame.findVariable(varName);
	}
	
	public String getSourceName() throws DebugException {
		return _jdiStackFrame.getSourceName();
	}
	
	public String getSourceName(String stratum) throws DebugException {
		return _jdiStackFrame.getSourceName(stratum);
	}
	
	
	public String getSourcePath() throws DebugException {
		return _jdiStackFrame.getSourcePath();
	}
	
	public String getSourcePath(String stratum) throws DebugException {
		return _jdiStackFrame.getSourcePath(stratum);
	}
	
	public IJavaVariable[] getLocalVariables() throws DebugException {
		return _jdiStackFrame.getLocalVariables();
	}
	
	public IJavaObject getThis() throws DebugException {
		return _jdiStackFrame.getThis();
	}
	
	public IJavaClassType getDeclaringType() throws DebugException {
		return _jdiStackFrame.getDeclaringType();
	}
	
	public IJavaReferenceType getReferenceType() throws DebugException {
		return _jdiStackFrame.getReferenceType();
	}
	
	public boolean wereLocalsAvailable() {
		return _jdiStackFrame.wereLocalsAvailable();
	}
	
	public boolean isVarArgs() throws DebugException {
		return _jdiStackFrame.isVarArgs();
	}
	
	public boolean canForceReturn() {
		return _jdiStackFrame.canForceReturn();
	}
	
	public void forceReturn(IJavaValue value) throws DebugException {
		 _jdiStackFrame.forceReturn(value);
	}
	
	
	public int getCharEnd() throws DebugException {
		return _jdiStackFrame.getCharEnd();
	}

	public int getCharStart() throws DebugException {
		return _jdiStackFrame.getCharStart();
	}
	

	public int getLineNumber() throws DebugException {
		return _jdiStackFrame.getLineNumber();
	}
	
	public int getLineNumber(String stratum) throws DebugException {
		return _jdiStackFrame.getLineNumber(stratum);
	}

	public String getName() throws DebugException {
		return _jdiStackFrame.getName();
	}

	
	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		// TODO Auto-generated method stub
		return null;
	}
	*/

	public IThread getThread() {
		return super.getThread();
	}
	
	/*
	protected boolean isTopStackFrame() throws DebugException {
		IStackFrame tos = getThread().getTopStackFrame();
		return tos != null && tos.equals(this);
	}

	public Method getUnderlyingMethod() {
		return _jdiStackFrame.getUnderlyingMethod();
	}
	
	
	public boolean hasRegisterGroups() throws DebugException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasVariables() throws DebugException {
		return _jdiStackFrame.hasVariables();
	}

	public ILaunch getLaunch() {
		return _jdiStackFrame.getLaunch();
	}

	public Object getAdapter(Class adapter) {
		return _jdiStackFrame.getAdapter(adapter);
	}

	public boolean canStepInto() {
		return _jdiStackFrame.canStepInto();
	}

	public boolean canStepOver() {
		return _jdiStackFrame.canStepOver();
	}

	public boolean canStepReturn() {
		return _jdiStackFrame.canStepReturn();
	}

	public boolean isStepping() {
		return _jdiStackFrame.isStepping();
	}

	public void stepInto() throws DebugException {
		_jdiStackFrame.stepInto();
	}

	public void stepOver() throws DebugException {
		_jdiStackFrame.stepOver();
	}

	public void stepReturn() throws DebugException {
		_jdiStackFrame.stepReturn();
	}

	public boolean canResume() {
		return _jdiStackFrame.canResume();
	}

	public boolean canSuspend() {
		return _jdiStackFrame.canSuspend();
	}

	public boolean isSuspended() {
		return _jdiStackFrame.isSuspended();
	}

	public void resume() throws DebugException {
		_jdiStackFrame.resume();
	}

	public void suspend() throws DebugException {
		_jdiStackFrame.suspend();
	}

	public boolean canTerminate() {
		return _jdiStackFrame.canTerminate();
	}

	public boolean isTerminated() {
		return _jdiStackFrame.isTerminated();
	}

	public void terminate() throws DebugException {
		_jdiStackFrame.terminate();
	}
*/
}
