/*******************************************************************************
* Copyright (c) 2008 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*/

package org.eclipse.imp.x10dt.editor;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.preferences.PreferenceCache;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

public class X10Editor extends UniversalEditor {
	public X10Editor () {
		super();
	}
	public void createPartControl(Composite parent) {
//		
//		System.err.println("PreferenceStore has x10Font="+PreferenceConverter.getStoredRepresentation(PreferenceConverter.getFontDataArray(getPreferenceStore(),"x10Font")));
//		System.err.println("x10Font is "+PreferenceConverter.getStoredRepresentation(JFaceResources.getFont("x10Font").getFontData()));
//		System.err.println("before cpc:  font is"+PreferenceConverter.getStoredRepresentation(PreferenceCache.sourceFont.getFontData()));
		super.createPartControl(parent);
//		System.err.println("after:   font is"+PreferenceConverter.getStoredRepresentation(PreferenceCache.sourceFont.getFontData()));
//		System.err.println("x10Font is "+PreferenceConverter.getStoredRepresentation(JFaceResources.getFont("x10Font").getFontData()));
//		System.err.println("PreferenceStore has x10Font="+PreferenceConverter.getStoredRepresentation(PreferenceConverter.getFontDataArray(getPreferenceStore(),"x10Font")));
//		System.err.println("SourceViewer has x10Font="+PreferenceConverter.getStoredRepresentation(getSourceViewer().getTextWidget().getFont().getFontData()));
	}

}
