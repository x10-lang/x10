package org.eclipse.imp.x10dt.ui.perspective;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.progress.IProgressConstants;

public class X10PerspectiveFactory implements IPerspectiveFactory {
    public void createInitialLayout(IPageLayout layout) {
	String editorArea= layout.getEditorArea();
	IFolderLayout folder= layout.createFolder("left", IPageLayout.LEFT, (float) 0.25, editorArea); //$NON-NLS-1$

	folder.addView(JavaUI.ID_PACKAGES);
	folder.addView(JavaUI.ID_TYPE_HIERARCHY);
	folder.addPlaceholder(IPageLayout.ID_RES_NAV);

	IFolderLayout outputfolder= layout.createFolder("bottom", IPageLayout.BOTTOM, (float) 0.75, editorArea); //$NON-NLS-1$

	outputfolder.addView(IPageLayout.ID_PROBLEM_VIEW);
	outputfolder.addView(JavaUI.ID_JAVADOC_VIEW);
	outputfolder.addView(JavaUI.ID_SOURCE_VIEW);
	outputfolder.addPlaceholder(NewSearchUI.SEARCH_VIEW_ID);
	outputfolder.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW);
	outputfolder.addPlaceholder(IPageLayout.ID_BOOKMARKS);
	outputfolder.addPlaceholder(IProgressConstants.PROGRESS_VIEW_ID);

	layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.RIGHT, (float) 0.75, editorArea);
	layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
	layout.addActionSet(JavaUI.ID_ACTION_SET);
	layout.addActionSet(JavaUI.ID_ELEMENT_CREATION_ACTION_SET);
	layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);

	// views - java
	layout.addShowViewShortcut(JavaUI.ID_PACKAGES);
	layout.addShowViewShortcut(JavaUI.ID_TYPE_HIERARCHY);
	layout.addShowViewShortcut(JavaUI.ID_SOURCE_VIEW);
	layout.addShowViewShortcut(JavaUI.ID_JAVADOC_VIEW);

	// views - search
	layout.addShowViewShortcut(NewSearchUI.SEARCH_VIEW_ID);

	// views - debugging
	layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);

	// views - standard workbench
	layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
	layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
	layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);

	// new actions - Java project creation wizard
	layout.addNewWizardShortcut("com.ibm.watson.safari.x10.newProject"); //$NON-NLS-1$
	layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewPackageCreationWizard"); //$NON-NLS-1$

//	layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewClassCreationWizard"); //$NON-NLS-1$
	layout.addNewWizardShortcut("com.ibm.watson.safari.x10.wizards.NewX10ClassWizard"); //$NON-NLS-1$
	layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewInterfaceCreationWizard"); //$NON-NLS-1$
//	layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewEnumCreationWizard"); //$NON-NLS-1$
//	layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewAnnotationCreationWizard"); //$NON-NLS-1$
	layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewSourceFolderCreationWizard"); //$NON-NLS-1$
//	layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewSnippetFileCreationWizard"); //$NON-NLS-1$
	layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");//$NON-NLS-1$
	layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");//$NON-NLS-1$
	layout.addNewWizardShortcut("org.eclipse.ui.editors.wizards.UntitledTextFileWizard");//$NON-NLS-1$
    }
}
