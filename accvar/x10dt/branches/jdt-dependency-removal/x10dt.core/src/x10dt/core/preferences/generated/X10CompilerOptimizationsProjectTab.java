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
 * The project level preferences tab.
 */
public class X10CompilerOptimizationsProjectTab extends ProjectPreferencesTab {

	public X10CompilerOptimizationsProjectTab(IPreferencesService prefService) {
		super(prefService, true);
	}

	/**
	 * Creates specific preference fields with settings appropriate to
	 * the project preferences level.
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
			"project", "Optimize", "Optimize",
			"",
			parent,
			false, false,
			false, false,
			false, false,
			true);
		fields.add(Optimize);


		BooleanFieldEditor LoopOptimizations = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"project", "LoopOptimizations", "Loop optimizations",
			"",
			parent,
			false, false,
			false, false,
			false, false,
			true);
		fields.add(LoopOptimizations);


		BooleanFieldEditor ArrayOptimizations = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"project", "ArrayOptimizations", "Array optimizations",
			"",
			parent,
			false, false,
			false, false,
			false, false,
			true);
		fields.add(ArrayOptimizations);


		BooleanFieldEditor InlineOptimizations = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"project", "InlineOptimizations", "Inline optimizations",
			"",
			parent,
			false, false,
			false, false,
			false, false,
			true);
		fields.add(InlineOptimizations);


		BooleanFieldEditor ClosureInlining = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"project", "ClosureInlining", "Closure inlining",
			"",
			parent,
			false, false,
			false, false,
			false, false,
			true);
		fields.add(ClosureInlining);


		BooleanFieldEditor WorkStealing = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"project", "WorkStealing", "Work stealing",
			"Enable code generation for the work-stealing scheduler",
			parent,
			false, false,
			false, false,
			false, false,
			true);
		fields.add(WorkStealing);

		return fields.toArray(new FieldEditor[fields.size()]);
	}


	protected void addressProjectSelection(IPreferencesService.ProjectSelectionEvent event, Composite composite)
	{
		boolean haveCurrentListeners = false;

		Preferences oldNode = event.getPrevious();
		Preferences newNode = event.getNew();

		if (oldNode == null && newNode == null) {
			// Happens sometimes when you clear the project selection.
			// Nothing, really, to do in this case ...
			return;
		}

		// If oldeNode is not null, we want to remove any preference-change listeners from it
		if (oldNode != null && oldNode instanceof IEclipsePreferences && haveCurrentListeners) {
			removeProjectPreferenceChangeListeners();
			haveCurrentListeners = false;
		} else {
			// Print an advisory message if you want to
		}

		// Declare local references to the fields
		BooleanFieldEditor Optimize = (BooleanFieldEditor) fFields[0];
		Link OptimizeDetailsLink = (Link) fDetailsLinks.get(0);
		BooleanFieldEditor LoopOptimizations = (BooleanFieldEditor) fFields[1];
		Link LoopOptimizationsDetailsLink = (Link) fDetailsLinks.get(1);
		BooleanFieldEditor ArrayOptimizations = (BooleanFieldEditor) fFields[2];
		Link ArrayOptimizationsDetailsLink = (Link) fDetailsLinks.get(2);
		BooleanFieldEditor InlineOptimizations = (BooleanFieldEditor) fFields[3];
		Link InlineOptimizationsDetailsLink = (Link) fDetailsLinks.get(3);
		BooleanFieldEditor ClosureInlining = (BooleanFieldEditor) fFields[4];
		Link ClosureInliningDetailsLink = (Link) fDetailsLinks.get(4);
		BooleanFieldEditor WorkStealing = (BooleanFieldEditor) fFields[5];
		Link WorkStealingDetailsLink = (Link) fDetailsLinks.get(5);

		// If we have a new project preferences node, then do various things
		// to set up the project's preferences
		if (newNode != null && newNode instanceof IEclipsePreferences) {
			// If the containing composite is not disposed, then set field values
			// and make them enabled and editable (as appropriate to the type of field)

			if (!composite.isDisposed()) {
				// Note:  Where there are toggles between fields, it is a good idea to set the
				// properties of the dependent field here according to the values they should have
				// based on the independent field.  There should be listeners to take care of 
				// that sort of adjustment once the tab is established, but when properties are
				// first initialized here, the properties may not always be set correctly through
				// the toggle.  I'm not entirely sure why that happens, except that there may be
				// a race condition between the setting of the dependent values by the listener
				// and the setting of those values here.  If the values are set by the listener
				// first (which might be surprising, but may be possible) then they will be
				// overwritten by values set here--so the values set here should be consistent
				// with what the listener would set.

				fPrefUtils.setField(Optimize, Optimize.getHolder());
				Optimize.getChangeControl().setEnabled(true);
				OptimizeDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				fPrefUtils.setField(LoopOptimizations, LoopOptimizations.getHolder());
				LoopOptimizations.getChangeControl().setEnabled(true);
				LoopOptimizationsDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				fPrefUtils.setField(ArrayOptimizations, ArrayOptimizations.getHolder());
				ArrayOptimizations.getChangeControl().setEnabled(true);
				ArrayOptimizationsDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				fPrefUtils.setField(InlineOptimizations, InlineOptimizations.getHolder());
				InlineOptimizations.getChangeControl().setEnabled(true);
				InlineOptimizationsDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				fPrefUtils.setField(ClosureInlining, ClosureInlining.getHolder());
				ClosureInlining.getChangeControl().setEnabled(true);
				ClosureInliningDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				fPrefUtils.setField(WorkStealing, WorkStealing.getHolder());
				WorkStealing.getChangeControl().setEnabled(true);
				WorkStealingDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				clearModifiedMarksOnLabels();
			}

			// Add property change listeners
			if (Optimize.getHolder() != null) addProjectPreferenceChangeListeners(Optimize, "Optimize", Optimize.getHolder());
			if (LoopOptimizations.getHolder() != null) addProjectPreferenceChangeListeners(LoopOptimizations, "LoopOptimizations", LoopOptimizations.getHolder());
			if (ArrayOptimizations.getHolder() != null) addProjectPreferenceChangeListeners(ArrayOptimizations, "ArrayOptimizations", ArrayOptimizations.getHolder());
			if (InlineOptimizations.getHolder() != null) addProjectPreferenceChangeListeners(InlineOptimizations, "InlineOptimizations", InlineOptimizations.getHolder());
			if (ClosureInlining.getHolder() != null) addProjectPreferenceChangeListeners(ClosureInlining, "ClosureInlining", ClosureInlining.getHolder());
			if (WorkStealing.getHolder() != null) addProjectPreferenceChangeListeners(WorkStealing, "WorkStealing", WorkStealing.getHolder());

			haveCurrentListeners = true;
		}

		// Or if we don't have a new project preferences node ...
		if (newNode == null || !(newNode instanceof IEclipsePreferences)) {
			// May happen when the preferences page is first brought up, or
			// if we allow the project to be deselected\nn			// Clear the preferences from the store
			fPrefService.clearPreferencesAtLevel(IPreferencesService.PROJECT_LEVEL);

			// Disable fields and make them non-editable
			if (!composite.isDisposed()) {
				Optimize.getChangeControl().setEnabled(false);

				LoopOptimizations.getChangeControl().setEnabled(false);

				ArrayOptimizations.getChangeControl().setEnabled(false);

				InlineOptimizations.getChangeControl().setEnabled(false);

				ClosureInlining.getChangeControl().setEnabled(false);

				WorkStealing.getChangeControl().setEnabled(false);

			}

			// Remove listeners
			removeProjectPreferenceChangeListeners();
			haveCurrentListeners = false;
			// To help assure that field properties are established properly
			performApply();
		}
	}


}
