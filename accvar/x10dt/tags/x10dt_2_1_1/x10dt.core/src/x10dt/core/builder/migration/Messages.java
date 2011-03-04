package x10dt.core.builder.migration;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "x10dt.core.builder.migration.messages"; //$NON-NLS-1$
	public static String ProjectMigrationDialog_deselectAllButtonTitle;
	public static String ProjectMigrationDialog_dontAskCheckboxTitle;
	public static String ProjectMigrationDialog_dontAskExplanationText1;
	public static String ProjectMigrationDialog_dontAskExplanationText2;
	public static String ProjectMigrationDialog_dontAskExplanationText3;
	public static String ProjectMigrationDialog_explanationText;
	public static String ProjectMigrationDialog_proceedButtonLabel;
	public static String ProjectMigrationDialog_projectListLabel;
	public static String ProjectMigrationDialog_selectAllButtonTitle;
	public static String ProjectMigrationDialog_title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {}
}
