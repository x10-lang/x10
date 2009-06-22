package org.eclipse.imp.x10dt.core.preferences.generated;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.PreferencesInitializer;
import org.eclipse.imp.preferences.PreferencesTab;
import org.eclipse.imp.preferences.TabbedPreferencesPage;
import org.eclipse.imp.x10dt.core.X10Plugin;

/**
 * The IMP-based tabbed preferences page for language X10Preferences.
 * 
 * Naming conventions:  This template uses the language name as a prefix
 * for naming the language plugin class and the preference-tab classes.
 * 	
 */
public class X10Preferences extends TabbedPreferencesPage {

	public X10Preferences() {
		super();
		// Get the language-specific preferences service
		// SMS 28 Mar 2007:  parameterized full name of plugin class
		prefService = X10Plugin.getPreferencesService();
	}

	protected PreferencesTab[] createTabs(IPreferencesService prefService,
			TabbedPreferencesPage page, TabFolder tabFolder) {
		PreferencesTab[] tabs = new PreferencesTab[4];

		X10PreferencesProjectTab projectTab = new X10PreferencesProjectTab(
				prefService);
		projectTab.createTabContents(page, tabFolder);
		tabs[0] = projectTab;

		X10PreferencesInstanceTab instanceTab = new X10PreferencesInstanceTab(prefService, false);
		instanceTab.createTabContents(page, tabFolder);
		tabs[1] = instanceTab;

		X10PreferencesConfigurationTab configurationTab = new X10PreferencesConfigurationTab(
				prefService);
		configurationTab.createTabContents(page, tabFolder);
		tabs[2] = configurationTab;

		X10PreferencesDefaultTab defaultTab = new X10PreferencesDefaultTab(
				prefService);
		defaultTab.createTabContents(page, tabFolder);
		tabs[3] = defaultTab;

		return tabs;
	}

	public PreferencesInitializer getPreferenceInitializer() {
		return new X10PreferencesInitializer();
	}

}
