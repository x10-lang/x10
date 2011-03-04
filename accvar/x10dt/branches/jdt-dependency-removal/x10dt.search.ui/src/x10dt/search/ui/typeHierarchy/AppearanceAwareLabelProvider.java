package x10dt.search.ui.typeHierarchy;
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


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.language.ServiceFactory;
import org.eclipse.imp.preferences.PreferenceConstants;
import org.eclipse.imp.ui.ElementLabels;
import org.eclipse.imp.ui.PreferenceAwareLabelProvider;
import org.eclipse.jface.preference.IPreferenceStore;

import x10dt.search.ui.UISearchPlugin;


/**
 * JavaUILabelProvider that respects settings from the Appearance preference page.
 * Triggers a viewer update when a preference changes.
 */
public class AppearanceAwareLabelProvider extends PreferenceAwareLabelProvider {

	private ArrayList<String> prefs;
	private Map<String, Long> masks;
	
	public final static long DEFAULT_TEXTFLAGS= ElementLabels.ROOT_VARIABLE | ElementLabels.T_TYPE_PARAMETERS | ElementLabels.M_PARAMETER_TYPES |
		ElementLabels.M_APP_TYPE_PARAMETERS | ElementLabels.M_APP_RETURNTYPE  | ElementLabels.REFERENCED_ROOT_POST_QUALIFIED;
	public final static int DEFAULT_IMAGEFLAGS= X10ElementImageProvider.OVERLAY_ICONS;
	
	

	/**
	 * Constructor for AppearanceAwareLabelProvider.
	 * @param textFlags Flags defined in <code>X10ElementLabels</code>.
	 * @param imageFlags Flags defined in <code>JavaElementImageProvider</code>.
	 */
	public AppearanceAwareLabelProvider(long textFlags, int imageFlags) {
		super(ServiceFactory.getInstance().getLabelProvider(LanguageRegistry.findLanguage("X10")), new X10ElementImageProvider(), textFlags, imageFlags);
	}

	/**
	 * Creates a labelProvider with DEFAULT_TEXTFLAGS and DEFAULT_IMAGEFLAGS
	 */
	public AppearanceAwareLabelProvider() {
		this(DEFAULT_TEXTFLAGS, DEFAULT_IMAGEFLAGS);
	}
	
	

	@Override
	protected List<String> getTextPreferences() {
		if(prefs == null)
		{
			prefs = new ArrayList<String>();
			prefs.add(X10Constants.APPEARANCE_METHOD_RETURNTYPE);
			prefs.add(X10Constants.APPEARANCE_METHOD_TYPEPARAMETERS);
			prefs.add(PreferenceConstants.APPEARANCE_COMPRESS_PACKAGE_NAMES);
			prefs.add(X10Constants.APPEARANCE_CATEGORY);
			
			masks = new HashMap<String, Long>();
			masks.put(X10Constants.APPEARANCE_METHOD_RETURNTYPE, Long.parseLong("" + X10ElementLabels.M_APP_RETURNTYPE));
			masks.put(X10Constants.APPEARANCE_METHOD_TYPEPARAMETERS, Long.parseLong("" + X10ElementLabels.M_APP_TYPE_PARAMETERS));
			masks.put(PreferenceConstants.APPEARANCE_COMPRESS_PACKAGE_NAMES, Long.parseLong("" + X10ElementLabels.P_COMPRESSED));
			masks.put(X10Constants.APPEARANCE_CATEGORY, Long.parseLong("" + X10ElementLabels.ALL_CATEGORY));
			
		}
		return prefs;
	}

	@Override
	protected List<String> getImagePreferences() {
		return Collections.emptyList();
	}

	@Override
	protected IPreferenceStore getPreferenceStore(String preference) {
		return UISearchPlugin.getDefault().getPreferenceStore();
	}

	@Override
	protected long getMask(String preference) {
		return masks.get(preference);
	}
}
