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
 * The default level preferences tab.
 */
public class X10CompilerOptimizationsDefaultTab extends DefaultPreferencesTab {

	public X10CompilerOptimizationsDefaultTab(IPreferencesService prefService) {
		super(prefService, true);
	}

	/**
	 * Creates a language-specific preferences initializer.
	 *
	 * @return    The preference initializer to be used to initialize
	 *            preferences in this tab
	 */
	public AbstractPreferenceInitializer getPreferenceInitializer() {
		X10Initializer preferencesInitializer = new X10Initializer();
		return preferencesInitializer;
	}

	/**
	 * Creates specific preference fields with settings appropriate to
	 * the default preferences level.
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
			"default", "Optimize", "Optimize",
			"",
			parent,
			true, true,
			false, false,
			false, false,
			false);
		fields.add(Optimize);


		BooleanFieldEditor LoopOptimizations = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"default", "LoopOptimizations", "Loop optimizations",
			"",
			parent,
			true, true,
			false, false,
			false, false,
			false);
		fields.add(LoopOptimizations);


		BooleanFieldEditor ArrayOptimizations = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"default", "ArrayOptimizations", "Array optimizations",
			"",
			parent,
			true, true,
			false, false,
			false, false,
			false);
		fields.add(ArrayOptimizations);


		BooleanFieldEditor InlineOptimizations = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"default", "InlineOptimizations", "Inline optimizations",
			"",
			parent,
			true, true,
			false, false,
			false, false,
			false);
		fields.add(InlineOptimizations);


		BooleanFieldEditor ClosureInlining = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"default", "ClosureInlining", "Closure inlining",
			"",
			parent,
			true, true,
			false, false,
			false, false,
			false);
		fields.add(ClosureInlining);


		BooleanFieldEditor WorkStealing = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"default", "WorkStealing", "Work stealing",
			"Enable code generation for the work-stealing scheduler",
			parent,
			true, true,
			false, false,
			false, false,
			false);
		fields.add(WorkStealing);

		return fields.toArray(new FieldEditor[fields.size()]);
	}
}
