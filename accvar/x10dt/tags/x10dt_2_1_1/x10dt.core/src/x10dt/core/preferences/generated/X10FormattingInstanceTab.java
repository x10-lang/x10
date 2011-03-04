/******************************************/
/* WARNING: GENERATED FILE - DO NOT EDIT! */
/******************************************/
package x10dt.core.preferences.generated;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Group;
import org.eclipse.imp.preferences.*;
import org.eclipse.imp.preferences.fields.*;
import org.osgi.service.prefs.Preferences;


/**
 * The instance level preferences tab.
 */
public class X10FormattingInstanceTab extends InstancePreferencesTab {

	public X10FormattingInstanceTab(IPreferencesService prefService) {
		super(prefService, true);
	}

	/**
	 * Creates specific preference fields with settings appropriate to
	 * the instance preferences level.
	 *
	 * Overrides an unimplemented method in PreferencesTab.
	 *
	 * @return    An array that contains the created preference fields
	 *
	 */
	protected FieldEditor[] createFields(TabbedPreferencesPage page, Composite parent)
	{
		List<FieldEditor> fields = new ArrayList<FieldEditor>();

		IntegerFieldEditor indentWidth = fPrefUtils.makeNewIntegerField(
			page, this, fPrefService,
			"instance", "indentWidth", "indent width",
			"The number of spaces by which to indent various entities relative to their containing constructs",
			parent,
			true, true,
			true, "0",
			false);
		fields.add(indentWidth);


		BooleanFieldEditor indentBlockStatements = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"instance", "indentBlockStatements", "indent block statements",
			"",
			parent,
			true, true,
			true, false,
			false);
		fields.add(indentBlockStatements);


		BooleanFieldEditor indentMethodBody = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"instance", "indentMethodBody", "indent method body",
			"",
			parent,
			true, true,
			true, false,
			false);
		fields.add(indentMethodBody);


		BooleanFieldEditor indentTypeBody = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"instance", "indentTypeBody", "indent type body",
			"",
			parent,
			true, true,
			true, false,
			false);
		fields.add(indentTypeBody);

		return fields.toArray(new FieldEditor[fields.size()]);
	}
}
