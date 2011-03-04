package org.eclipse.imp.x10dt.debug.ui.presentation;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.internal.ui.model.elements.VariableContentProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenCountUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementContentProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IHasChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IViewerUpdate;
import org.eclipse.jdi.internal.ClassTypeImpl;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugModelMessages;
import org.eclipse.jdt.internal.debug.core.model.JDIObjectValue;
import org.eclipse.jdt.internal.debug.core.model.JDIReferenceListVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIVariable;
import org.eclipse.jdt.internal.debug.ui.variables.JavaVariableContentProvider;

import com.sun.jdi.ReferenceType;

public class ActivityFieldFilteringContentProvider extends JavaVariableContentProvider implements
		IElementContentProvider {
	
	static final String activityFieldsNameArray[] = {"place_", "finishState_", "finishStack_", "activityClockManager", "rootNode_", "abstractMetricsManager", "notFinished", "invocationStrategy", "name", "childList"};

	protected Object[] getChildren(Object parent, int index, int length, IPresentationContext context, IViewerUpdate monitor) throws CoreException {
		if (parent instanceof JDIVariable) {
			JDIVariable jv = (JDIVariable)parent;
			IValue jvv = jv.getValue();
			if (jvv instanceof JDIObjectValue) {
			JDIObjectValue jov = (JDIObjectValue) jvv;
			ReferenceType type = jov.getUnderlyingObject().referenceType();
			if (type instanceof ClassTypeImpl) {
				ClassTypeImpl ct = (ClassTypeImpl)type;
				if (ct.superclass().name().equals("x10.runtime.Activity")) {
					// filter out internal activity fields
					Collection retainedFields = new ArrayList();
					Object[] fields = getAllChildren(parent, context);
					for (Object v: fields) {
						if (v instanceof JDIVariable) {
							JDIVariable jField = (JDIVariable)v;
							String jFieldName = jField.getName();
							boolean gotit = false;
							for (String n: activityFieldsNameArray) {
								if (n.equals(jFieldName)) {
									gotit=true;
									break;
								}
							}
							if (!gotit) retainedFields.add(jField);
						}
					}
					Object rfa[] = retainedFields.toArray(new Object[0]);
					return getElements(rfa, index, length);
				}
			}
			}
		}
		return super.getChildren(parent, index, length, context, monitor);
	}
	protected int getChildCount(Object element, IPresentationContext context, IViewerUpdate monitor) throws CoreException {
		if (element instanceof JDIVariable) {
			JDIVariable jv = (JDIVariable)element;
			IValue jvv = jv.getValue();
			if (jvv instanceof JDIObjectValue) {
				JDIObjectValue jov = (JDIObjectValue) jvv;
				ReferenceType type = jov.getUnderlyingObject().referenceType();
				if (type instanceof ClassTypeImpl) {
					ClassTypeImpl ct = (ClassTypeImpl)type;
					if (ct.superclass().name().equals("x10.runtime.Activity")) {
						// filter out internal activity fields
						int nChildren=0;
						Object[] fields = getAllChildren(element, context);
						for (Object v: fields) {
							if (v instanceof JDIVariable) {
								JDIVariable jField = (JDIVariable)v;
								String jFieldName = jField.getName();
								boolean gotit = false;
								for (String n: activityFieldsNameArray) {
									if (n.equals(jFieldName)) {
										gotit=true;
										break;
									}
								}
								if (!gotit) nChildren++;
							} else nChildren++;
						}
						return nChildren;
					}
				}
			}
		}
		return super.getChildCount(element, context, monitor);
	}

//		return getChildren(element, 0, nc, context, monitor).length;
//	}
//	protected boolean hasChildren(Object element, IPresentationContext context,	IViewerUpdate monitor) throws CoreException {
//		return getChildCount(element, context, monitor)>0;
//	}

}
