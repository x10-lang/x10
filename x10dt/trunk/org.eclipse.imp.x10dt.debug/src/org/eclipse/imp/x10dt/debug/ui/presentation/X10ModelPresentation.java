package org.eclipse.imp.x10dt.debug.ui.presentation;

import java.util.List;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.imp.x10dt.debug.model.impl.X10DebugTarget;
import org.eclipse.imp.x10dt.debug.model.impl.X10DebugTargetAlt;
import org.eclipse.imp.x10dt.debug.model.impl.X10Thread;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
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



import com.sun.jdi.ThreadReference;

//import x10.runtime.PoolRunner;

public class X10ModelPresentation implements IDebugModelPresentation {

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
				ObjectReference a = ((X10Thread)element).getFreshActivityObject(t);
				if (a!=null) {
				    System.out.println("X10ModelPresentation: a!=null");	
				    String name = ((X10Thread)element).getName();
				    System.out.println("X10ModelPresentation: name ="+name);
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
		return null;
		} catch (Exception e) {
			return "EXCEPTION";
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

}

