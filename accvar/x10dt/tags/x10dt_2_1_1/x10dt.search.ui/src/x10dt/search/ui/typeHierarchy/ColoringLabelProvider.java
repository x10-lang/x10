package x10dt.search.ui.typeHierarchy;
/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IDecorationContext;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.jface.viewers.StyledString.Styler;

public class ColoringLabelProvider extends DecoratingStyledCellLabelProvider implements ILabelProvider {

	public static final Styler HIGHLIGHT_STYLE= StyledString.createColorRegistryStyler(null, ColoredViewersManager.HIGHLIGHT_BG_COLOR_NAME);
	public static final Styler HIGHLIGHT_WRITE_STYLE= StyledString.createColorRegistryStyler(null, ColoredViewersManager.HIGHLIGHT_WRITE_BG_COLOR_NAME);
	
	public static final Styler INHERITED_STYLER= StyledString.createColorRegistryStyler(ColoredViewersManager.INHERITED_COLOR_NAME, null);

	public ColoringLabelProvider(IStyledLabelProvider labelProvider) {
		this(labelProvider, null, null);
	}

	public ColoringLabelProvider(IStyledLabelProvider labelProvider, ILabelDecorator decorator, IDecorationContext decorationContext) {
		super(labelProvider, decorator, decorationContext);
	}

	public void initialize(ColumnViewer viewer, ViewerColumn column) {
		ColoredViewersManager.install(this);
		setOwnerDrawEnabled(ColoredViewersManager.showColoredLabels());

		super.initialize(viewer, column);
	}

	public void dispose() {
		super.dispose();
		ColoredViewersManager.uninstall(this);
	}

	public void update() {
		ColumnViewer viewer= getViewer();

		if (viewer == null) {
			return;
		}
		
		boolean needsUpdate= false;
		
		boolean showColoredLabels= ColoredViewersManager.showColoredLabels();
		if (showColoredLabels != isOwnerDrawEnabled()) {
			setOwnerDrawEnabled(showColoredLabels);
			needsUpdate= true;
		} else if (showColoredLabels) {
			needsUpdate= true;
		}
		if (needsUpdate) {
			fireLabelProviderChanged(new LabelProviderChangedEvent(this));
		}
	}

	protected StyleRange prepareStyleRange(StyleRange styleRange, boolean applyColors) {
		if (!applyColors && styleRange.background != null) {
			styleRange= super.prepareStyleRange(styleRange, applyColors);
			styleRange.borderStyle= SWT.BORDER_DOT;
			return styleRange;
		}
		return super.prepareStyleRange(styleRange, applyColors);
	}

	public String getText(Object element) {
		return getStyledText(element).getString();
	}

}
