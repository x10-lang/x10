package org.eclipse.imp.x10dt.debug.ui.presentation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.imp.x10dt.debug.model.impl.X10DebugTargetAlt;
import org.eclipse.imp.x10dt.debug.model.impl.stub.SampleX10ActivityAsJDIThread;
import org.eclipse.jdt.debug.core.IJavaReferenceType;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.eval.IAstEvaluationEngine;
import org.eclipse.jdt.debug.eval.ICompiledExpression;
import org.eclipse.jdt.debug.eval.IEvaluationListener;
import org.eclipse.jdt.debug.eval.IEvaluationResult;
import org.eclipse.jdt.internal.debug.core.logicalstructures.JavaStructureErrorValue;
import org.eclipse.jdt.internal.debug.core.logicalstructures.LogicalStructuresMessages;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.imp.x10dt.debug.model.impl.X10Thread;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;

import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.ui.JDIModelPresentation;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.Method;
import com.sun.jdi.Type;
//import org.eclipse.imp.x10dt.debug.model.X10DebugTargetAlt;
/*
public class X10ModelPresentation extends JDIModelPresentation{
	public String getText(Object element)
	{
		String test = super.getText(element);
		System.out.println("getText is "+ test);
		if (element instanceof IJavaDebugTarget)
		{
			test.concat("20");
			System.out.println("concat result is "+test);
		}
		else if (element instanceof IJavaThread)
		{
			String tar="place";
			String str="pool";
			int i=((X10DebugTargetAlt)((JDIThread)element).getDebugTarget()).getPlaceNo();
			//if (i==4) {
			 test=test.replace(str,tar);
			 System.out.println("In Thread concat result is "+test);
			//}
		}
			return test;
	}
*/	



//import org.eclipse.jdt.core.IJavaElement;
//import org.eclipse.jdt.core.IJavaProject;
//import org.eclipse.jdt.core.JavaCore;

import com.sun.jdi.Method;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;

public class X10ModelPresentation implements IDebugModelPresentation, IEvaluationListener {

	private Map fProjectsByFrame=new HashMap();
	private IEvaluationResult fResult;

	public void computeDetail(IValue value, IValueDetailListener listener) {
		// TODO Auto-generated method stub

	}

	public Image getImage(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getText(Object element) {
		try {
		if (element instanceof X10DebugTargetAlt) {
			return "X10 Application";//getTargetText((X10DebugTarget)element);
		} else if (element instanceof IThread) {
			((X10DebugTargetAlt)((IThread)element).getDebugTarget()).initializeX10RTObject();
			
			ThreadReference th=((X10DebugTargetAlt)((IThread)element).getDebugTarget()).getThreadForRTMethods();
			ThreadReference t = ((JDIThread)element).getUnderlyingThread();
//			System.out.println("underlyingThread: "+t.getClass());
			if (t.referenceType().name().equals("x10.runtime.PoolRunner")) {

				//				List<Method> getActivityMethods = t.referenceType().methodsByName("getActivity", "()Lx10/runtime/Activity;");
//				Method getActivityMethod = getActivityMethods.get(0);
//				JDIStackFrame topFrame = (JDIStackFrame)((JDIThread)thread).getTopStackFrame();
//				Value activityObject = t.invokeMethod(/*target.getThreadForInvokeMethod()*/t, getActivityMethod, new ArrayList(), 0);
//
//				IAstEvaluationEngine engine = target.getEvaluationEngine(getJavaProject(topFrame));
//				ICompiledExpression expression= engine.getCompiledExpression("getActivity()", topFrame);
//				engine.evaluateExpression(expression, topFrame, this, 0, false);

//				return "ACTIVITY: "+t.toString();// need to call getActivity().toString() on PoolRunner (on VM Side)
				ObjectReference a = ((X10Thread)element).getFreshActivityObject(t);
				if (a!=null) {
				    System.out.println("X10ModelPresentation: a!=null");	
				    String name = ((X10Thread)element).getName();
				    System.out.println("X10ModelPresentation: name ="+name);
				    if (name.contains("terminated")) {
				    	String place=t.name();
						place=place.replace("pool", "PLACE");
						place=place.substring(0, 16);
					    return place + "(Idle)";
				    }
				    name=name.replace("true","(running)");
				    return name;
				}
				/*
				if (a!=null){
					String aname=((X10DebugTargetAlt)((IThread)element).getDebugTarget()).getActivityString(a);
					System.out.println("X10ModelPresentation , aname = "+aname+"t.name()="+t.name());
					String place = t.name();
					if (place.contains("Main Activity"))
						return "ACTIVITY" + "@PLACE 0" + ": Main Activity";
					else{
					    place = place.substring(4, 6);
				        return "ACTIVITY" + "@PLACE" +place +":" +aname ;
					}
				}  
				*/
				else{
					
					String place=t.name();
					place=place.replace("pool", "PLACE");
					place=place.substring(0, 16);
				    return place + "(Idle)";// need to call getActivity().toString() on PoolRunner (on VM Side)
				    
					//return "Activity:" + t.toString();
				}
			}
	        return ((X10Thread)element).getUnderlyingThread().name();//getThreadText(element);
	    } else if (element instanceof IStackFrame) {
	        return ((IStackFrame)element).getName(); //getStackFrameText((IStackFrame)element);
//	    } else if (element instanceof X10Watchpoint) {
//	        return getWatchpointText((X10Watchpoint)element);
	    }
	    else if (element instanceof IVariable) {
			return ((IVariable)element).getName();
	    }	
		return null;
		} catch (Exception e) {
			return "EXCEPTION: "+e.toString();
		}
	}
	
	public void setAttribute(String attribute, Object value) {
		// TODO Auto-generated method stub

	}

	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	public String getEditorId(IEditorInput input, Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	public IEditorInput getEditorInput(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

/*
	// Copied from JavaLineBreakpoint
	private IJavaProject getJavaProject(JDIStackFrame stackFrame) {
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
	*/

	public void evaluationComplete(IEvaluationResult result) {
		fResult = result;	
	}
	
//	// Copied with small mods from JavaLogicalStructure$EvaluationBlock
//	/**
//	 * Evaluates the specified snippet and returns the <code>IJavaValue</code> from the evaluation
//	 * @param snippet the snippet to evaluate
//	 * @return the <code>IJavaValue</code> from the evaluation
//	 * @throws DebugException
//	 */
//	public IJavaValue evaluate(String snippet, IAstEvaluationEngine fEvaluationEngine, IJavaStackFrame frame) throws DebugException {
//		ICompiledExpression compiledExpression= fEvaluationEngine.getCompiledExpression(snippet, frame);
//		if (compiledExpression.hasErrors()) {
//			String[] errorMessages = compiledExpression.getErrorMessages();
////            log(errorMessages);
////			return new JavaStructureErrorValue(errorMessages, fEvaluationValue);
//			return null;
//		}
//		fResult= null;
//		fEvaluationEngine.evaluateExpression(compiledExpression, fEvaluationValue, fThread, this, DebugEvent.EVALUATION_IMPLICIT, false);
//		synchronized(this) {
//			if (fResult == null) {
//				try {
//					this.wait();
//				} catch (InterruptedException e) {
//				}
//			}
//		}
//		if (fResult == null) {
//			return new JavaStructureErrorValue(LogicalStructuresMessages.JavaLogicalStructure_1, fEvaluationValue); 
//		}
//		if (fResult.hasErrors()) {
//			DebugException exception = fResult.getException();
//			String message;
//			if (exception != null) {
//				message= MessageFormat.format(LogicalStructuresMessages.JavaLogicalStructure_2, new String[] { exception.getMessage() }); 
//			} else {
//				message= LogicalStructuresMessages.JavaLogicalStructure_3; 
//			}
//			return new JavaStructureErrorValue(message, fEvaluationValue);
//		}
//		return fResult.getValue();
//	}

}

