package x10dt.ui.navigator.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

import x10dt.ui.navigator.UINavigatorPlugin;

public class PreferenceConstants {
	/**
	 * A named preference that specifies whether children of a compilation unit are shown in the package explorer.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String SHOW_CU_CHILDREN= "x10dt.ui.navigation.packages.cuchildren"; //$NON-NLS-1$

	/**
	 * A named preference that controls if empty inner packages are folded in
	 * the hierarchical mode of the package explorer.
	 * <p>
	 * Value is of type <code>Boolean</code>: if <code>true</code> empty
	 * inner packages are folded.
	 * </p>
	 * @since 2.1
	 */
	public static final String APPEARANCE_FOLD_PACKAGES_IN_PACKAGE_EXPLORER= "x10dt.ui.navigation.flatPackagesInPackageExplorer";//$NON-NLS-1$

	
	
	private PreferenceConstants() {
	}
	
	/**
	 * Returns the UI preference store.
	 *
	 * @return the UI preference store
	 */
	public static IPreferenceStore getPreferenceStore() {
		return UINavigatorPlugin.getDefault().getPreferenceStore();
	}
	
	/**
	 * Initializes the given preference store with the default values.
	 *
	 * @param store the preference store to be initialized
	 *
	 * @since 2.1
	 */
	public static void initializeDefaultValues(IPreferenceStore store) {
		
		// AppearancePreferencePage
		store.setDefault(PreferenceConstants.SHOW_CU_CHILDREN, true);
		store.setDefault(PreferenceConstants.APPEARANCE_FOLD_PACKAGES_IN_PACKAGE_EXPLORER, true);

	}
	
}
