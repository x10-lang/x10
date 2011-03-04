package x10dt.ui.editor;

import org.eclipse.imp.editor.StructuredSourceViewerConfiguration;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.ui.DefaultPartListener;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.texteditor.TextOperationAction;

import x10dt.search.ui.Messages;
import x10dt.search.ui.actions.EditorActionDefinitionIds;
import x10dt.search.ui.actions.OpenViewActionGroup;
import x10dt.search.ui.typeHierarchy.X10Constants;
import x10dt.ui.X10DTUIPlugin;

public class X10Editor extends UniversalEditor {
	private DefaultPartListener fRefreshContributions;

	public static final String TYPE_ACTION_SET = X10DTUIPlugin.PLUGIN_ID
			+ ".typeActionSet";
	
	/**
	 * Text operation code for requesting the hierarchy for the current input.
	 */
	public static final int SHOW_HIERARCHY= 53;

	OpenViewActionGroup ovg;

	public X10Editor() {
		super();
	}

	@Override
	protected StructuredSourceViewerConfiguration createSourceViewerConfiguration() {
		// RMF 13 Oct 2010 - For some reason, using the X10DTUIPlugin's
		// preference store
		// causes the eventStateMask to be 0, which effectively turns
		// hyperlinking on
		// even when the Cmd/Ctrl key is UP, the opposite of the normal
		// behavior.
		return new X10StructuredSourceViewerConfiguration(getPreferenceStore(),
				this);
	}

	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		registerEditorContributionsActivator();
	}

	@Override
	public void dispose() {
		unregisterEditorContributionsActivator();

		if (ovg != null) {
			ovg.dispose();
			ovg = null;
		}

		super.dispose();
	}

	/**
	 * Makes sure that menu items and status bar items disappear as the editor
	 * is out of focus, and reappear when it gets focus again. This does not
	 * work for toolbar items for unknown reasons, they stay visible.
	 * 
	 */
	private void registerEditorContributionsActivator() {
		fRefreshContributions = new DefaultPartListener() {
			@Override
			public void partActivated(IWorkbenchPart part) {
				if (part instanceof UniversalEditor || part.getSite().getPage().getPerspective().getId().equals(X10Constants.ID_HIERARCHYPERSPECTIVE)) {
					part.getSite().getPage().showActionSet(TYPE_ACTION_SET);
				} else {
					part.getSite().getPage().hideActionSet(TYPE_ACTION_SET);
				}
			}
		};
		getSite().getPage().addPartListener(fRefreshContributions);
	}

	private void unregisterEditorContributionsActivator() {
		if (fRefreshContributions != null) {
			getSite().getPage().removePartListener(fRefreshContributions);
		}
		fRefreshContributions = null;
	}

	@Override
	protected void createActions() {
		super.createActions();

		IAction action = new TextOperationAction(Messages
				.getBundleForConstructedKeys(),
				"OpenHierarchy.", this, SHOW_HIERARCHY, true); //$NON-NLS-1$
		action.setActionDefinitionId(EditorActionDefinitionIds.OPEN_HIERARCHY);
		setAction(EditorActionDefinitionIds.OPEN_HIERARCHY, action);
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(action,
//				IJavaHelpContextIds.OPEN_HIERARCHY_ACTION);

		ovg = new OpenViewActionGroup(this);
	}

	@Override
	protected void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);

		// Quick views
		IAction action = getAction(EditorActionDefinitionIds.OPEN_HIERARCHY);
		menu.appendToGroup(IContextMenuConstants.GROUP_OPEN, action);
		menu.insertAfter(IContextMenuConstants.GROUP_OPEN, new GroupMarker(
				IContextMenuConstants.GROUP_SHOW));

		ActionContext context = new ActionContext(getSelectionProvider()
				.getSelection());
		ovg.setContext(context);
		ovg.fillContextMenu(menu);
		ovg.setContext(null);
	}

	/*
	 * @seeorg.eclipse.ui.texteditor.AbstractDecoratedTextEditor#
	 * initializeKeyBindingScopes()
	 */
	protected void initializeKeyBindingScopes() {
		setKeyBindingScopes(new String[] { X10DTUIPlugin.EDITOR_SCOPE }); //$NON-NLS-1$
	}
}
