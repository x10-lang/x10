package org.eclipse.imp.x10dt.core.preferences.generated;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.imp.preferences.*;
import org.eclipse.imp.preferences.fields.*;
import org.osgi.service.prefs.Preferences;


/**
 * The instance level preferences tab.
 */
public class X10CompilerOptimizationsInstanceTab extends InstancePreferencesTab {

	public X10CompilerOptimizationsInstanceTab(IPreferencesService prefService) {
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

		BooleanFieldEditor Optimize = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"instance", "Optimize", "Optimize",
			"",
			parent,
			true, true,
			false, false,
			false, false,
			true);
		fields.add(Optimize);


		BooleanFieldEditor LoopOptimizations = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"instance", "LoopOptimizations", "Loop optimizations",
			"",
			parent,
			true, true,
			false, false,
			false, false,
			true);
		fields.add(LoopOptimizations);


		BooleanFieldEditor InlineOptimizations = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"instance", "InlineOptimizations", "Inline optimizations",
			"",
			parent,
			true, true,
			false, false,
			false, false,
			true);
		fields.add(InlineOptimizations);


		BooleanFieldEditor ClosureInlining = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"instance", "ClosureInlining", "Closure inlining",
			"",
			parent,
			true, true,
			false, false,
			false, false,
			true);
		fields.add(ClosureInlining);


		BooleanFieldEditor WorkStealing = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"instance", "WorkStealing", "Work stealing",
			"Enable code generation for the work-stealing scheduler",
			parent,
			true, true,
			false, false,
			false, false,
			true);
		fields.add(WorkStealing);

		return fields.toArray(new FieldEditor[fields.size()]);
	}
}
