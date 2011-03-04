/******************************************/
/* WARNING: GENERATED FILE - DO NOT EDIT! */
/******************************************/
package x10dt.core.preferences.generated;

import org.eclipse.imp.preferences.PreferencesInitializer;
import org.eclipse.imp.preferences.IPreferencesService;
import x10dt.core.X10DTCorePlugin;

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

		service.setIntPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_TABWIDTH, 4);
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_SPACESFORTABS, false);
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_EDITORPERFORMANCEMODE, false);
		service.setIntPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_INDENTWIDTH, 4);
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_INDENTBLOCKSTATEMENTS, true);
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_INDENTMETHODBODY, true);
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_INDENTTYPEBODY, true);
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_CONSERVATIVEBUILD, true);
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_PERMITASSERT, true);
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_STATICCALLS, false);
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_VERBOSECALLS, false);
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_ECHOCOMPILEARGUMENTSTOCONSOLE, false);
		service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_ADDITIONALCOMPILEROPTIONS, "");
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_OPTIMIZE, false);
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_DISABLECHECKING, false);
		service.setStringPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_DEFAULTRUNTIME, "");
		service.setIntPreference(IPreferencesService.DEFAULT_LEVEL, X10Constants.P_NUMBEROFPLACES, 4);
	}

	/*
	 * Clear (remove) any preferences set on the given level.
	 */
	public void clearPreferencesOnLevel(String level) {
		IPreferencesService service = X10DTCorePlugin.getInstance().getPreferencesService();
		service.clearPreferencesAtLevel(level);

	}
}
