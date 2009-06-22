package com.ibm.watson.safari.x10.preferences;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.framework.Bundle;

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
        
	try {
	    Bundle x10CommonBundle= Platform.getBundle("x10.common");
            URL x10CommonURL= Platform.asLocalURL(Platform.find(x10CommonBundle, new Path("")));
            URL stdCfgURL= Platform.asLocalURL(Platform.find(x10CommonBundle, new Path("etc/standard.cfg")));
	    String x10CommonPath= x10CommonURL.getPath();
            String stdCfgPath= stdCfgURL.getPath();

            if (Platform.getOS().equals("win32")) {
                x10CommonPath= x10CommonPath.substring(1);
                stdCfgPath= stdCfgPath.substring(1);
            }
            store.setDefault(PreferenceConstants.P_X10COMMON_PATH, x10CommonPath);
            store.setDefault(PreferenceConstants.P_X10CONFIG_FILE, stdCfgPath);
	} catch (IOException e) {
	    e.printStackTrace();
	}
//	store.setDefault(PreferenceConstants.P_CHOICE, "choice2");
    }
}
