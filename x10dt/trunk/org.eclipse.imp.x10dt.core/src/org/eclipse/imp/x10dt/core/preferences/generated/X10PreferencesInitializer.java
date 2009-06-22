package org.eclipse.imp.x10dt.core.preferences.generated;

import org.eclipse.imp.preferences.PreferenceCache;
import org.eclipse.imp.preferences.PreferenceConstants;
import org.eclipse.imp.preferences.PreferencesInitializer;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.runtime.RuntimePlugin;
import org.eclipse.imp.x10dt.core.X10Plugin;
import org.eclipse.imp.x10dt.core.X10PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.FontData;

/**
 * Initializations of default values for preferences.
 */


public class X10PreferencesInitializer extends PreferencesInitializer {
    // Default option values
    private final static String DEFAULT_FONT_NAME = "org.eclipse.jface.textfont";
    private final static String DEFAULT_TAB_WIDTH = "4";
    private final static boolean DEFAULT_OPTION_BAD_PLACE_CHECK = true;
    private final static boolean DEFAULT_OPTION_LOOP_OPTIMIZATIONS = true;
    private final static boolean DEFAULT_OPTION_ARRAY_OPTIMIZATIONS = true;
    private final static boolean DEFAULT_OPTION_ASSERT = true;

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferencesService service = X10Plugin.getInstance().getPreferencesService();

        service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, org.eclipse.imp.preferences.PreferenceConstants.P_TAB_WIDTH, DEFAULT_TAB_WIDTH);
        service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10PreferenceConstants.P_BAD_PLACE_CHECK, DEFAULT_OPTION_BAD_PLACE_CHECK);
        service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10PreferenceConstants.P_LOOP_OPTIMIZATIONS, DEFAULT_OPTION_LOOP_OPTIMIZATIONS);
        service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10PreferenceConstants.P_ARRAY_OPTIMIZATIONS, DEFAULT_OPTION_ARRAY_OPTIMIZATIONS);
        service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10PreferenceConstants.P_ASSERT, DEFAULT_OPTION_ASSERT);
        service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, X10PreferenceConstants.P_NUM_PLACES, "4");
        service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, X10PreferenceConstants.P_ADDITIONAL_COMPILER_OPTIONS, "");//PORT1.7 IMP changes need this

        // RMF 1/9/2009 - This is bogus but at the moment, the only way to set the X10 (IMP) editor's
        // default font -- it sets the preference store of the entire IMP runtime.
        IPreferenceStore prefStore = RuntimePlugin.getInstance().getPreferenceStore();
        FontData[] fontData= (FontData[]) JFaceResources.getFontRegistry().getFontData(DEFAULT_FONT_NAME);
        PreferenceConverter.setDefault(prefStore, PreferenceConstants.P_SOURCE_FONT, fontData);

        String fontStr= PreferenceConverter.getStoredRepresentation(fontData);
        service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, X10PreferenceConstants.P_X10_FONT, fontStr);
        service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, PreferenceConstants.P_SOURCE_FONT, fontStr);

        service.setIntPreference(IPreferencesService.DEFAULT_LEVEL, org.eclipse.imp.preferences.PreferenceConstants.P_TAB_WIDTH, 4);

        // Need the following to get initial tab width to work properly on eclipse startup with and w/o new workspace
        Integer tabWidth = service.getIntPreference(org.eclipse.imp.preferences.PreferenceConstants.P_TAB_WIDTH);
        prefStore.setValue(org.eclipse.imp.preferences.PreferenceConstants.P_TAB_WIDTH, tabWidth);
        PreferenceCache.tabWidth = tabWidth.intValue();
	}

	/*
	 * Clear (remove) any preferences set on the given level.
	 */
	public void clearPreferencesOnLevel(String level) {
		IPreferencesService service = X10Plugin.getInstance().getPreferencesService();
		service.clearPreferencesAtLevel(level);
	}
}
