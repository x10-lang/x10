package x10dt.search.ui.globalSearch;

import org.eclipse.core.runtime.Assert;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionGroup;

import org.eclipse.jdt.ui.actions.GenerateActionGroup;
import org.eclipse.jdt.ui.actions.JavaSearchActionGroup;
import org.eclipse.jdt.ui.actions.OpenEditorActionGroup;
import org.eclipse.jdt.ui.actions.OpenViewActionGroup;
import org.eclipse.jdt.ui.actions.RefactorActionGroup;

import org.eclipse.jdt.internal.ui.actions.CompositeActionGroup;

class NewSearchViewActionGroup extends CompositeActionGroup {

	public NewSearchViewActionGroup(IViewPart part) {
		Assert.isNotNull(part);
		OpenViewActionGroup openViewActionGroup;
		setGroups(new ActionGroup[]{
			new OpenEditorActionGroup(part),
			openViewActionGroup= new OpenViewActionGroup(part),
			new GenerateActionGroup(part),
			new RefactorActionGroup(part),
			new JavaSearchActionGroup(part)
		});
		openViewActionGroup.containsShowInMenu(false);
	}
}

