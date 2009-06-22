package org.eclipse.imp.x10dt.core.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.PreferencesUtilities;
import org.eclipse.imp.x10dt.core.X10Plugin;

/**
 * Initializes X10 preference default values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
    /*	
     * (non-Javadoc)
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    public void initializeDefaultPreferences() {
    	// SMS 27 Oct 2006
    	// Use the service, Luke
    	
//		IPreferenceStore store= X10Plugin.getInstance().getPreferenceStore();
//	
//		store.setDefault(PreferenceConstants.P_EMIT_MESSAGES, true);
//		// Disabled "auto-add runtime", since that causes subsequent Java compiles to
//		// fail, because then x10.runtime won't be in the Java build-time classpath.
//		store.setDefault(PreferenceConstants.P_AUTO_ADD_RUNTIME, false);
//	
//		store.setDefault(PreferenceConstants.P_X10CONFIG_NAME, "standard");
//		store.setDefault(PreferenceConstants.P_X10CONFIG_FILE, "etc/standard.cfg");
//
//        store.setDefault(PreferenceConstants.P_SAMPLING_FREQ, 50);
//        store.setDefault(PreferenceConstants.P_STATS_DISABLE, "none");
    	
		IPreferencesService service = X10Plugin.getPreferencesService();
	
		service.setBooleanPreference(
			IPreferencesService.DEFAULT_LEVEL, PreferenceConstants.P_EMIT_MESSAGES, true);
		service.setBooleanPreference(
			IPreferencesService.DEFAULT_LEVEL, PreferenceConstants.P_AUTO_ADD_RUNTIME, false);
		service.setStringPreference(
			IPreferencesService.DEFAULT_LEVEL, PreferenceConstants.P_X10CONFIG_NAME, PreferencesUtilities.comboDefaultName);	//"standard");

		// RMF 2/26/2007 - DON'T use File.separator in the following; it's used to find a
		// resource inside a bundle, and "etc\\standard.cfg" is incorrect.
		service.setStringPreference(
			IPreferencesService.DEFAULT_LEVEL, PreferenceConstants.P_X10CONFIG_FILE, "etc/standard.cfg");
		service.setIntPreference(
			IPreferencesService.DEFAULT_LEVEL, PreferenceConstants.P_SAMPLING_FREQ, 50);
		service.setStringPreference(
			IPreferencesService.DEFAULT_LEVEL, PreferenceConstants.P_STATS_DISABLE, "none");
		
    }
}
