/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package x10dt.ui.navigator.internal;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkingSet;

import x10dt.search.ui.typeHierarchy.X10ElementImageDescriptor;


public class PackageExplorerProblemsDecorator extends TreeHierarchyLayoutProblemsDecorator {

	public PackageExplorerProblemsDecorator() {
		super();
	}

	public PackageExplorerProblemsDecorator(boolean isFlatLayout) {
		super(isFlatLayout);
	}

	protected int computeAdornmentFlags(Object obj) {
		if (!(obj instanceof IWorkingSet))
			return super.computeAdornmentFlags(obj);

		IWorkingSet workingSet= (IWorkingSet)obj;
		IAdaptable[] elements= workingSet.getElements();
		int result= 0;
		for (int i= 0; i < elements.length; i++) {
			IAdaptable element= elements[i];
			int flags= super.computeAdornmentFlags(element);
			if ((flags & X10ElementImageDescriptor.ERROR) != 0)
				return X10ElementImageDescriptor.ERROR;
			if ((flags & X10ElementImageDescriptor.WARNING) != 0)
				result= X10ElementImageDescriptor.WARNING;
		}
		return result;
	}
}
