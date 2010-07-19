package org.eclipse.imp.x10dt.core.preferences.generated;

import org.eclipse.imp.preferences.PreferencesInitializer;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.x10dt.core.X10DTCorePlugin;

/**
 * Initializations of default values for preferences.
 */
public class X10Initializer extends PreferencesInitializer {
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferencesService service = X10DTCorePlugin.getInstance().getPreferencesService();

		service.setIntPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_TABSIZE, 4);
		service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_SOURCEFONT, "courier");
		service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_DEFAULTRUNTIME, "");
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_BADPLACERUNTIMECHECK, true);
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_LOOPOPTIMIZATIONS, true);
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_ARRAYOPTIMIZATIONS, true);
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_PERMITASSERT, true);
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_ECHOCOMPILEARGUMENTSTOCONSOLE, false);
		service.setIntPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_NUMPLACES, 4);
		service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_ADDITIONALCOMPILEROPTIONS, "-commandlineonly");
	}

	/*
	 * Clear (remove) any preferences set on the given level.
	 */
	public void clearPreferencesOnLevel(String level) {
		IPreferencesService service = X10DTCorePlugin.getInstance().getPreferencesService();
		service.clearPreferencesAtLevel(level);

	}
}
