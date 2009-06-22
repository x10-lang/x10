package org.eclipse.imp.x10dt.refactoring;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.services.ILanguageActionsContributor;
import org.eclipse.imp.x10dt.refactoring.actions.MarkContextAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;

public class EditorActionContributor implements ILanguageActionsContributor {

    public void contributeToEditorMenu(UniversalEditor editor, IMenuManager menuManager) {
        IMenuManager refactorMenu= menuManager.findMenuUsingPath("org.eclipse.imp.refactor");
        if (refactorMenu != null) {
            refactorMenu.add(new MarkContextAction(editor));
        }
    }

    public void contributeToMenuBar(UniversalEditor editor, IMenuManager menu) {

    }

    public void contributeToStatusLine(UniversalEditor editor, IStatusLineManager statusLineManager) {

    }

    public void contributeToToolBar(UniversalEditor editor, IToolBarManager toolbarManager) {

    }

}
