package x10dt.ui.editor;

import org.eclipse.imp.editor.TextEditorActionContributor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;

import x10dt.ui.typeHierarchy.Messages;
import x10dt.ui.typeHierarchy.actions.EditorActionDefinitionIds;

public class X10EditorActionContributer extends TextEditorActionContributor {

	private RetargetTextEditorAction fOpenHierarchy;

	public X10EditorActionContributer() {
		fOpenHierarchy = new RetargetTextEditorAction(Messages
				.getBundleForConstructedKeys(), "OpenHierarchy."); //$NON-NLS-1$
		fOpenHierarchy.setActionDefinitionId(EditorActionDefinitionIds.OPEN_HIERARCHY);
	}

	@Override
	public void contributeToMenu(IMenuManager menu) {
		super.contributeToMenu(menu);

		IMenuManager navigateMenu = menu
				.findMenuUsingPath(IWorkbenchActionConstants.M_NAVIGATE);
		if (navigateMenu != null) {
			navigateMenu.appendToGroup(IWorkbenchActionConstants.SHOW_EXT,
					fOpenHierarchy);
		}
	}

	@Override
	public void setActiveEditor(IEditorPart part) {
		super.setActiveEditor(part);

		ITextEditor textEditor = null;
		if (part instanceof ITextEditor)
			textEditor = (ITextEditor) part;

		fOpenHierarchy.setAction(getAction(textEditor,
				EditorActionDefinitionIds.OPEN_HIERARCHY));
		
//		if (part instanceof X10Editor)
//		{
//			((X10Editor)part).ovg.fillActionBars(getActionBars());
//		}
	}
}
