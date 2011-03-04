package x10dt.core.preferences.generated;

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
 * The configuration level preferences tab.
 */
public class X10CompilerOptimizationsConfigurationTab extends ConfigurationPreferencesTab {

	public X10CompilerOptimizationsConfigurationTab(IPreferencesService prefService) {
		super(prefService, true);
	}

	/**
	 * Creates specific preference fields with settings appropriate to
	 * the configuration preferences level.
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
			"configuration", "Optimize", "Optimize",
			"",
			parent,
			true, true,
			false, false,
			false, false,
			true);
		fields.add(Optimize);


		BooleanFieldEditor LoopOptimizations = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"configuration", "LoopOptimizations", "Loop optimizations",
			"",
			parent,
			true, true,
			false, false,
			false, false,
			true);
		fields.add(LoopOptimizations);


		BooleanFieldEditor ArrayOptimizations = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"configuration", "ArrayOptimizations", "Array optimizations",
			"",
			parent,
			true, true,
			false, false,
			false, false,
			true);
		fields.add(ArrayOptimizations);


		BooleanFieldEditor InlineOptimizations = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"configuration", "InlineOptimizations", "Inline optimizations",
			"",
			parent,
			true, true,
			false, false,
			false, false,
			true);
		fields.add(InlineOptimizations);


		BooleanFieldEditor ClosureInlining = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"configuration", "ClosureInlining", "Closure inlining",
			"",
			parent,
			true, true,
			false, false,
			false, false,
			true);
		fields.add(ClosureInlining);


		BooleanFieldEditor WorkStealing = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"configuration", "WorkStealing", "Work stealing",
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
