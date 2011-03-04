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

import org.eclipse.imp.editor.ProblemsLabelDecorator;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.ISourceFolder;
import org.eclipse.imp.utils.BuildPathUtils;


/**
 * Special problem decorator for hierarchical package layout.
 * <p>
 * It only decorates package fragments which are not covered by the
 * <code>ProblemsLabelDecorator</code>.
 * </p>
 *
 * @see org.eclipse.jdt.ui.ProblemsLabelDecorator
 * @since 2.1
 */
public class TreeHierarchyLayoutProblemsDecorator extends ProblemsLabelDecorator {

	private boolean fIsFlatLayout;

	public TreeHierarchyLayoutProblemsDecorator() {
		this(false);
	}

	public TreeHierarchyLayoutProblemsDecorator(boolean isFlatLayout) {
		super(LanguageRegistry.findLanguage("X10"));
		fIsFlatLayout= isFlatLayout;
	}

	protected int computePackageAdornmentFlags(ISourceFolder fragment) {
		if (!fIsFlatLayout && !BuildPathUtils.isDefaultPackage(fragment.getName())) {
			return super.computeAdornmentFlags(fragment.getResource());
		}
		return super.computeAdornmentFlags(fragment);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.ProblemsLabelDecorator#computeAdornmentFlags(java.lang.Object)
	 */
	protected int computeAdornmentFlags(Object element) {
		if (element instanceof ISourceFolder) {
			return computePackageAdornmentFlags((ISourceFolder) element);
		} 
//		else if (element instanceof LogicalPackage) {
//			ISourceFolder[] fragments= ((LogicalPackage) element).getFragments();
//			int res= 0;
//			for (int i= 0; i < fragments.length; i++) {
//				int flags= computePackageAdornmentFlags(fragments[i]);
//				if (flags == X10ElementImageDescriptor.ERROR) {
//					return flags;
//				} else if (flags != 0) {
//					res= flags;
//				}
//			}
//			return res;
//		}
		return super.computeAdornmentFlags(element);
	}

	public void setIsFlatLayout(boolean state) {
		fIsFlatLayout= state;
	}

}
