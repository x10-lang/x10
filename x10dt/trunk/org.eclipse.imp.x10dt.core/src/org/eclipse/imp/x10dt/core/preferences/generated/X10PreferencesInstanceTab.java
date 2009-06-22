package org.eclipse.imp.x10dt.core.preferences.generated;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.TabbedPreferencesPage;
import org.eclipse.imp.x10dt.core.preferences.InstancePreferencesTab;
import org.eclipse.imp.x10dt.core.preferences.fields.FieldEditor;
import org.eclipse.imp.x10dt.core.preferences.fields.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;

/**
 * The instance level preferences tab.
 */
public class X10PreferencesInstanceTab extends InstancePreferencesTab {

	public X10PreferencesInstanceTab(IPreferencesService prefService, boolean noDetails) {
		super(prefService, noDetails);
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

		IntegerFieldEditor TabSize = fPrefUtils_x10.makeNewIntegerField(
			page, this, fPrefService,
			"instance", "TabSize", "TabSize",
			parent,
			true, true,
			true, String.valueOf(8),
			false, "0",
			true, null);
//		Link TabSizeDetailsLink = fPrefUtils_x10.createDetailsLink(parent, TabSize, TabSize.getTextControl().getParent(), "Details ...");

		fields.add(TabSize);


		IntegerFieldEditor NumPlaces = fPrefUtils_x10.makeNewIntegerField(
			page, this, fPrefService,
			"instance", "NumPlaces", "NumPlaces",
			parent,
			true, true,
			true, String.valueOf(8),
			false, "0",
			true, null);
//		Link NumPlacesDetailsLink = fPrefUtils_x10.createDetailsLink(parent, NumPlaces, NumPlaces.getTextControl().getParent(), "Details ...");

		fields.add(NumPlaces);

		FieldEditor[] fieldsArray = new FieldEditor[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			fieldsArray[i] = (FieldEditor) fields.get(i);
		}
		return fieldsArray;
	}
}
