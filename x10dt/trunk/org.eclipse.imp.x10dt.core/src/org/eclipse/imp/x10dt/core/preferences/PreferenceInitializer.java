package com.ibm.watson.safari.x10.preferences;

import java.io.File;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.ibm.watson.safari.x10.X10Plugin;

/**
 * Initializes X10 preference default values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
    /*
     * (non-Javadoc)
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    public void initializeDefaultPreferences() {
	IPreferenceStore store= X10Plugin.getInstance().getPreferenceStore();

	store.setDefault(PreferenceConstants.P_EMIT_MESSAGES, true);
	// Disabled "auto-add runtime", since that causes subsequent Java compiles to
	// fail, because then x10.runtime won't be in the Java build-time classpath.
	store.setDefault(PreferenceConstants.P_AUTO_ADD_RUNTIME, false);
	store.setDefault(PreferenceConstants.P_X10COMMON_PATH, "???");
	store.setDefault(PreferenceConstants.P_X10CONFIG_FILE, store.getString(PreferenceConstants.P_X10COMMON_PATH) + File.separator + "standard.cfg");
//	store.setDefault(PreferenceConstants.P_CHOICE, "choice2");
    }
}
