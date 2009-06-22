package org.eclipse.imp.x10dt.debug.ui.presentation;

import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.imp.x10dt.debug.model.impl.X10DebugTarget;
import org.eclipse.imp.x10dt.debug.model.impl.X10DebugTargetAlt;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;

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
	        return "Activity: "+ ((IThread)element).getName();//getThreadText(element);
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
