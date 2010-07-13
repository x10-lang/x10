package org.eclipse.imp.x10dt.core.preferences.generated;

import org.eclipse.swt.widgets.TabFolder;import org.eclipse.imp.preferences.IPreferencesService;import org.eclipse.imp.preferences.PreferencesInitializer;import org.eclipse.imp.preferences.PreferencesTab;import org.eclipse.imp.preferences.TabbedPreferencesPage;import org.eclipse.imp.x10dt.core.X10DTCorePlugin;

/**
 * A preference page class.
 */


public class X10FormattingPreferencePage extends TabbedPreferencesPage {
	public X10FormattingPreferencePage() {
		super();
		prefService = X10DTCorePlugin.getInstance().getPreferencesService();
	}

	protected PreferencesTab[] createTabs(IPreferencesService prefService,
		TabbedPreferencesPage page, TabFolder tabFolder) {
		PreferencesTab[] tabs = new PreferencesTab[4];

		X10FormattingDefaultTab defaultTab = new X10FormattingDefaultTab(prefService);
		defaultTab.createTabContents(page, tabFolder);
		tabs[0] = defaultTab;

		X10FormattingConfigurationTab configurationTab = new X10FormattingConfigurationTab(prefService);
		configurationTab.createTabContents(page, tabFolder);
		tabs[1] = configurationTab;

		X10FormattingInstanceTab instanceTab = new X10FormattingInstanceTab(prefService);
		instanceTab.createTabContents(page, tabFolder);
		tabs[2] = instanceTab;

		X10FormattingProjectTab projectTab = new X10FormattingProjectTab(prefService);
		projectTab.createTabContents(page, tabFolder);
		tabs[3] = projectTab;

		return tabs;
	}

	public PreferencesInitializer getPreferenceInitializer() {
		return new X10Initializer();
	}
}
