package org.eclipse.imp.x10dt.debug.model;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.imp.x10dt.debug.model.impl.X10DebugTargetAlt;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.debug.core.IJavaReferenceType;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.eval.IAstEvaluationEngine;
import org.eclipse.jdt.debug.eval.ICompiledExpression;
import org.eclipse.jdt.debug.eval.IEvaluationListener;
import org.eclipse.jdt.debug.eval.IEvaluationResult;
import org.eclipse.jdt.internal.debug.core.logicalstructures.LogicalStructuresMessages;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

public class EvaluationEngineHelper implements IEvaluationListener {

	private static EvaluationEngineHelper evalEngHelperSingleton = new EvaluationEngineHelper();
	public static EvaluationEngineHelper getEvaluationEngineHelper() {
		return evalEngHelperSingleton;
	}
	
	public static IJavaValue evaluateSomewhere(X10DebugTargetAlt target, String programText){
		if (getEvaluationEngineHelper() !=null) {
			try {
				JDIThread suspendedThread = target.getSuspendedThread();
				if (suspendedThread!=null) {
					JDIStackFrame topFrame = (JDIStackFrame) suspendedThread.getTopStackFrame();
					if (topFrame!=null) { // not sure why no-frame threads  get here.  should be filtered out.
						IAstEvaluationEngine engine = target.getEvaluationEngine(getEvaluationEngineHelper().getJavaProject(topFrame));
						IJavaValue val = getEvaluationEngineHelper().evaluate(programText, engine, topFrame);
						return val;
					}
				}
		} catch (DebugException e) {
		}
		}
		return null;
	}

	// Copied from JavaLineBreakpoint
	private Map fProjectsByFrame=new HashMap();
	private IEvaluationResult fResult;

	public IEvaluationResult getResult() { return fResult; }
	public IJavaProject getJavaProject(JDIStackFrame stackFrame) {
	    IJavaProject project= (IJavaProject) fProjectsByFrame.get(stackFrame);
	    if (project == null) {
	        project = computeJavaProject(stackFrame);
	        if (project != null) {
	            fProjectsByFrame.put(stackFrame, project);
	        }
	    }
	    return project;
	}
	
	private IJavaProject computeJavaProject(JDIStackFrame stackFrame) {
		if (stackFrame==null)
			return null;
		ILaunch launch = stackFrame.getLaunch();
		if (launch == null) {
			return null;
		}
		ISourceLocator locator= launch.getSourceLocator();
		if (locator == null)
			return null;
		
		Object sourceElement= null;
		try {
			if (locator instanceof ISourceLookupDirector && !stackFrame.isStatic()) {
				IJavaType thisType = stackFrame.getThis().getJavaType();
				if (thisType instanceof IJavaReferenceType) {
					String[] sourcePaths= ((IJavaReferenceType) thisType).getSourcePaths(null);
					if (sourcePaths != null && sourcePaths.length > 0) {
						sourceElement= ((ISourceLookupDirector) locator).getSourceElement(sourcePaths[0]);
					}
				}
			}
		} catch (DebugException e) {
			DebugPlugin.log(e);
		}
		if (sourceElement == null) {
			sourceElement = locator.getSourceElement(stackFrame);
		}
		if (!(sourceElement instanceof IJavaElement) && sourceElement instanceof IAdaptable) {
			Object element= ((IAdaptable)sourceElement).getAdapter(IJavaElement.class);
			if (element != null) {
				sourceElement= element;
			}
		}
		if (sourceElement instanceof IJavaElement) {
			return ((IJavaElement) sourceElement).getJavaProject();
		} else if (sourceElement instanceof IResource) {
			IJavaProject project = JavaCore.create(((IResource)sourceElement).getProject());
			if (project.exists()) {
				return project;
			}
		}
		return null;
	}
	synchronized public void evaluationComplete(IEvaluationResult result) {
		fResult = result;
		//this.notifyAll();
	}
	
	// Copied with small mods from JavaLogicalStructure$EvaluationBlock
	public IJavaValue evaluate(String snippet, IAstEvaluationEngine evaluationEngine, IJavaStackFrame frame) throws DebugException {
		ICompiledExpression compiledExpression= evaluationEngine.getCompiledExpression(snippet, frame);
		if (compiledExpression.hasErrors()) {
			String[] errorMessages = compiledExpression.getErrorMessages();
			for (String msg: errorMessages) {
				System.out.println("***********" + msg);
			}
//            log(errorMessages);
//			return new JavaStructureErrorValue(errorMessages, fEvaluationValue);
			return null;
		}
		fResult= null;
		evaluationEngine.evaluateExpression(compiledExpression, frame, this, DebugEvent.EVALUATION_IMPLICIT, false);
		synchronized(this) {
			if (fResult == null) {
				try {
					this.wait();
				} catch (InterruptedException e) {
				}
			}
		}
		if (fResult.hasErrors()) {
			DebugException exception = fResult.getException();
			String message;
			if (exception != null) {
				message= MessageFormat.format(LogicalStructuresMessages.JavaLogicalStructure_2, new String[] { exception.getMessage() }); 
			} else {
				message= LogicalStructuresMessages.JavaLogicalStructure_3; 
			}
		}
		return fResult.getValue();
	}
}
