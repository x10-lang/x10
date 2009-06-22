package com.ibm.watson.safari.x10.preferences;

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
            Bundle x10CompilerBundle= Platform.getBundle("x10.compiler");
            URL x10CompilerDataURL= Platform.asLocalURL(Platform.find(x10CompilerBundle, new Path("data")));
            URL stdCfgURL= Platform.asLocalURL(Platform.find(x10CommonBundle, new Path("etc/standard.cfg")));
	    String x10CommonPath= x10CommonURL.getPath();
            String stdCfgPath= stdCfgURL.getPath();
            String x10CompilerDataPath= x10CompilerDataURL.getPath();

            if (Platform.getOS().equals("win32")) {
                x10CommonPath= x10CommonPath.substring(1);
                stdCfgPath= stdCfgPath.substring(1);
                x10CompilerDataPath= x10CompilerDataPath.substring(1);
            }
            store.setDefault(PreferenceConstants.P_X10COMMON_PATH, x10CommonPath);
            store.setDefault(PreferenceConstants.P_X10CONFIG_FILE, stdCfgPath);
            store.setDefault(PreferenceConstants.P_COMPILER_DATA_DIR, x10CompilerDataPath);
	} catch (IOException e) {
	    e.printStackTrace();
	}
        store.setDefault(PreferenceConstants.P_SAMPLING_FREQ, 50);
        store.setDefault(PreferenceConstants.P_STATS_DISABLE, "none");
    }
}
