package org.eclipse.imp.x10dt.debug.ui.presentation;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.imp.x10dt.debug.model.IX10Activity;
import org.eclipse.imp.x10dt.debug.model.impl.X10DebugTargetAlt;
import org.eclipse.imp.x10dt.debug.model.impl.X10StackFrame;
import org.eclipse.imp.x10dt.debug.model.impl.X10Thread;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.internal.debug.core.breakpoints.JavaExceptionBreakpoint;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;
import org.eclipse.jdt.internal.debug.ui.DebugUIMessages;
import org.eclipse.jdt.internal.debug.ui.JDIModelPresentation;

import com.sun.jdi.ThreadReference;

public class X10ModelPresentation extends JDIModelPresentation implements IDebugModelPresentation {

	public String getText(Object element) {
		try {
		if (element instanceof X10DebugTargetAlt) {
			return "X10 Application";//getTargetText((X10DebugTarget)element);
		} else if (element instanceof IThread) {
			IThread threadElement = (IThread)element;
			X10DebugTargetAlt target = (X10DebugTargetAlt) threadElement.getDebugTarget();
			target.initializeX10RTObject();
			
			//ThreadReference th=target.getThreadForRTMethods();
			ThreadReference t = ((JDIThread)element).getUnderlyingThread();
			//JDIThread thread = target.findThread(t);
//			System.out.println("underlyingThread: "+t.getClass());
			//if (t.referenceType().name().equals("x10.runtime.PoolRunner")) {
			//if (t.referenceType().name().equals("x10.runtime.PoolRunner")) {
				//The following will evaluate activity asynchronously:
				/*JDIStackFrame topFrame = (JDIStackFrame)((JDIThread)element).getTopStackFrame();
				if (topFrame!=null) { // not sure why no-frame threads  get here.  should be filtered out.
					IAstEvaluationEngine engine = target.getEvaluationEngine(getJavaProject(topFrame));
					IJavaValue val = evaluate("x10.lang.Runtime.runtime.getCurrentActivity()", engine, topFrame);
					if (val!=null) {
						val = evaluate("x10.lang.Runtime.runtime.getCurrentActivity().myName()", engine, topFrame);
						System.out.println("AstEvaluationEngine: activity.getName ="+val.getValueString());
					}
				}*/

				X10Thread xt = (X10Thread)element;
				String s = xt.getThreadText();
				IX10Activity.X10ActivityState st = ((X10Thread)element).getRunState();
			    for (IBreakpoint b: xt.getBreakpoints()) {
			    	if ( b instanceof JavaExceptionBreakpoint) {
			    		s += " ["+((JavaExceptionBreakpoint)b).getExceptionTypeName()+"]";
			    	}
			    }
			    return s;
			    //if (a!=null) {
			    /*if (st!=IX10Activity.X10ActivityState.Idle) {
				    System.out.println("X10ModelPresentation: a!=null");	
				    String name = ((X10Thread)element).getName();
				    System.out.println("X10ModelPresentation: name ="+name);
				   
				    //if (((X10Thread)element).isSuspended()){
				    if (st==IX10Activity.X10ActivityState.Suspended) {
				    	//name=name.replace("true","Suspended");
				    	name=name.concat(" Suspended");
				    }
				    else if (st == IX10Activity.X10ActivityState.Blocked) {
				        //name=name.replace("true","Running");
				    	name=name.concat(" Blocked");
				    }
				    else name=name.concat(" Running"); 
				    return name;
				}
				
				else{
					
					String place=t.name();
					if (place.contains("pool")) {
					  place=place.replace("pool", "PLACE");
					  place=place.substring(0, 16);
				      return place + "(Idle)";// need to call getActivity().toString() on PoolRunner (on VM Side)
					}
					return place;
					//return "Activity:" + t.toString();
				}
			}
	        return ((X10Thread)element).getUnderlyingThread().name();//getThreadText(element);
				}*/
		} else if (element instanceof IX10Activity) {
			return ((IX10Activity)element).getName();
	    } else if (element instanceof IStackFrame) {
	    	// The following if condition can be used in StackFrame Filtering in the corresponding adaptor
	    	if (((X10DebugTargetAlt)((X10StackFrame)element).getDebugTarget()).isStepFiltersEnabled()) {
	    		String reftype=((JDIStackFrame)element).getReferenceType().getName();
	    		if (reftype.contains("x10.runtime")|| reftype.contains("x10.lang") || reftype.contains("x10.array")) {
	    			String name=getStackFrameText((IStackFrame) element);
	    			return name+"(Native Runtime Call)";
	    		}
	    	}
	    	//shivali: I tried getStackFrameText to see details of stackframes. It may be
	    	//modified further according to what needs to be presented
	        return getStackFrameText((IStackFrame) element);
	    	//return ((IStackFrame)element).getName(); //getStackFrameText((IStackFrame)element);
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
	
	protected String getStackFrameText(IStackFrame stackFrame) throws DebugException {
		IJavaStackFrame frame= (IJavaStackFrame) stackFrame.getAdapter(IJavaStackFrame.class);
		if (frame != null) {
			StringBuffer label= new StringBuffer();
			
			String dec= DebugUIMessages.JDIModelPresentation_unknown_declaring_type__4; 
			try {
				dec= frame.getDeclaringTypeName();
			} catch (DebugException exception) {
			}
			if (frame.isObsolete()) {
				label.append(DebugUIMessages.JDIModelPresentation__obsolete_method_in__1); 
				label.append(dec);
				label.append('>');
				return label.toString();
			}
			
			boolean javaStratum= true;
			try {
				//System.out.println("DEC TYPE "+frame.getReferenceType().getName());
				javaStratum = frame.getReferenceType().getDefaultStratum().equals("Java"); //$NON-NLS-1$
			} catch (DebugException e) {
			}
			
			if (javaStratum) {
				// receiver name
				String rec= DebugUIMessages.JDIModelPresentation_unknown_receiving_type__5; 
				try {
					rec= frame.getReceivingTypeName();
				} catch (DebugException exception) {
				}
				label.append(removeQualifierFromGenericName(rec));
				//label.append(rec);

				// append declaring type name if different
				if (!dec.equals(rec)) {
					label.append('(');
					label.append(removeQualifierFromGenericName(dec));
					//label.append(dec);
					label.append(')');
				}
				// append a dot separator and method name
				label.append('.');
				try {
					label.append(frame.getMethodName());
				} catch (DebugException exception) {
					label.append(DebugUIMessages.JDIModelPresentation_unknown_method_name__6); 
				}
				try {
					List args= frame.getArgumentTypeNames();
					if (args.isEmpty()) {
						label.append("()"); //$NON-NLS-1$
					} else {
						label.append('(');
						Iterator iter= args.iterator();
						while (iter.hasNext()) {
							label.append(removeQualifierFromGenericName((String) iter.next()));
							if (iter.hasNext()) {
								label.append(", "); //$NON-NLS-1$
							} else if (frame.isVarArgs()) {
								label.replace(label.length() - 2, label.length(), "..."); //$NON-NLS-1$
							}
						}
						label.append(')');
					}
				} catch (DebugException exception) {
					label.append(DebugUIMessages.JDIModelPresentation__unknown_arguements___7); 
				}
			} else {
				/*if (isShowQualifiedNames()) {
					label.append(frame.getSourcePath());
				} else {*/
				try {
					label.append(frame.getMethodName());
				} catch (DebugException exception) {
					
				}
				try {
					List args= frame.getArgumentTypeNames();
					if (args.isEmpty()) {
						label.append("()"); //$NON-NLS-1$
					} else {
						label.append('(');
						Iterator iter= args.iterator();
						while (iter.hasNext()) {
							label.append(removeQualifierFromGenericName((String) iter.next()));
							if (iter.hasNext()) {
								label.append(", "); //$NON-NLS-1$
							} else if (frame.isVarArgs()) {
								label.replace(label.length() - 2, label.length(), "..."); //$NON-NLS-1$
							}
						}
						label.append(')');
					}
				} catch (DebugException exception) {
					label.append(DebugUIMessages.JDIModelPresentation__unknown_arguements___7); 
				}
				    label.append(" : ");
					label.append(frame.getSourceName());
					/*
					try {
						int lineNumber= frame.getLineNumber("x10");
						label.append(' ');
						label.append(DebugUIMessages.JDIModelPresentation_line__76); 
						label.append(' ');
						if (lineNumber >= 0) {
							label.append(lineNumber);
						} else {
							label.append(DebugUIMessages.JDIModelPresentation_not_available); 
							if (frame.isNative()) {
								label.append(' ');
								label.append(DebugUIMessages.JDIModelPresentation_native_method); 
							}
						}
					} catch (DebugException exception) {
						label.append(DebugUIMessages.JDIModelPresentation__unknown_line_number__8); 
					}
					
					if (!frame.wereLocalsAvailable()) {
						label.append(' ');
						label.append(DebugUIMessages.JDIModelPresentation_local_variables_unavailable); 
					}
					
					return label.toString();
*/
				

				//}
			}

			try {
				int lineNumber= frame.getLineNumber();
				label.append(' ');
				label.append(DebugUIMessages.JDIModelPresentation_line__76); 
				label.append(' ');
				if (lineNumber >= 0) {
					label.append(lineNumber);
				} else {
					label.append(DebugUIMessages.JDIModelPresentation_not_available); 
					if (frame.isNative()) {
						label.append(' ');
						label.append(DebugUIMessages.JDIModelPresentation_native_method); 
					}
				}
			} catch (DebugException exception) {
				label.append(DebugUIMessages.JDIModelPresentation__unknown_line_number__8); 
			}
			
			if (!frame.wereLocalsAvailable()) {
				label.append(' ');
				label.append(DebugUIMessages.JDIModelPresentation_local_variables_unavailable); 
			}
			
			return label.toString();

		}
		return null;
	}
	
	public String removeQualifierFromGenericName(String qualifiedName) {
		if (qualifiedName.endsWith("...")) { //$NON-NLS-1$
			// handle variable argument name
			return removeQualifierFromGenericName(qualifiedName.substring(0, qualifiedName.length() - 3)) + "..."; //$NON-NLS-1$
		}
		if (qualifiedName.endsWith("[]")) { //$NON-NLS-1$
			// handle array type
			return removeQualifierFromGenericName(qualifiedName.substring(0, qualifiedName.length() - 2)) + "[]"; //$NON-NLS-1$
		}
		// check if the type has parameters
		int parameterStart= qualifiedName.indexOf('<');
		if (parameterStart == -1) {
			return getSimpleName(qualifiedName);
		}
		// get the list of the parameters and generates their simple name
		List parameters= getNameList(qualifiedName.substring(parameterStart + 1, qualifiedName.length() - 1));
		StringBuffer name= new StringBuffer(getSimpleName(qualifiedName.substring(0, parameterStart)));
		name.append('<');
		Iterator iterator= parameters.iterator();
		if (iterator.hasNext()) {
			name.append(removeQualifierFromGenericName((String)iterator.next()));
			while (iterator.hasNext()) {
				name.append(',').append(removeQualifierFromGenericName((String)iterator.next()));
			}
		}
		name.append('>');
		return name.toString();
	}
	
	private String getSimpleName(String qualifiedName) {
		int index = qualifiedName.lastIndexOf('.');
		if (index >= 0) {
			return qualifiedName.substring(index + 1);
		}
		return qualifiedName;
	}
	
	private List getNameList(String listName) {
		List names= new ArrayList();
		StringTokenizer tokenizer= new StringTokenizer(listName, ",<>", true); //$NON-NLS-1$
		int enclosingLevel= 0;
		int startPos= 0;
		int currentPos= 0;
		while (tokenizer.hasMoreTokens()) {
			String token= tokenizer.nextToken();
			switch (token.charAt(0)) {
				case ',':
					if (enclosingLevel == 0) {
						names.add(listName.substring(startPos, currentPos));
						startPos= currentPos + 1;
					}
					break;
				case '<':
					enclosingLevel++;
					break;
				case '>':
					enclosingLevel--;
					break;
			}
			currentPos += token.length();
		}
		names.add(listName.substring(startPos));
		return names;
	}
	
}

