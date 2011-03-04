package x10dt.ui.typeHierarchy;
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


import org.eclipse.imp.editor.ProblemsLabelDecorator;
import org.eclipse.imp.language.Language;
import org.eclipse.jface.viewers.DecorationContext;
import org.eclipse.ui.PlatformUI;

public class DecoratingLabelProvider extends ColoringLabelProvider {

	/**
	 * Decorating label provider for Java. Combines a JavaUILabelProvider
	 * with problem and override indicator with the workbench decorator (label
	 * decorator extension point).
	 * @param labelProvider the label provider to decorate
	 */
	public DecoratingLabelProvider(X10LabelProvider labelProvider, Language lang) {
		this(labelProvider, true, lang);
	}

	/**
	 * Decorating label provider for Java. Combines a JavaUILabelProvider
	 * (if enabled with problem indicator) with the workbench
	 * decorator (label decorator extension point).
	 * 	@param labelProvider the label provider to decorate
	 * @param errorTick show error ticks
	 */
	public DecoratingLabelProvider(X10LabelProvider labelProvider, boolean errorTick, Language lang) {
		this(labelProvider, errorTick, true, lang);
	}

	/**
	 * Decorating label provider for Java. Combines a JavaUILabelProvider
	 * (if enabled with problem indicator) with the workbench
	 * decorator (label decorator extension point).
	 * 	@param labelProvider the label provider to decorate
	 * @param errorTick show error ticks
	 * @param flatPackageMode configure flat package mode
	 */
	public DecoratingLabelProvider(X10LabelProvider labelProvider, boolean errorTick, boolean flatPackageMode, Language lang) {
		super(labelProvider, PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), DecorationContext.DEFAULT_CONTEXT);
		if (errorTick) {
			labelProvider.addLabelDecorator(new ProblemsLabelDecorator(lang));
		}
		setFlatPackageMode(flatPackageMode);
	}

	/**
	 * Tells the label decorator if the view presents packages flat or hierarchical.
	 * @param enable If set, packages are presented in flat mode.
	 */
	public void setFlatPackageMode(boolean enable) {
//		if (enable) {
			setDecorationContext(DecorationContext.DEFAULT_CONTEXT);
//		} else {
//			setDecorationContext(HierarchicalDecorationContext.getContext());
//		}
	}

}
