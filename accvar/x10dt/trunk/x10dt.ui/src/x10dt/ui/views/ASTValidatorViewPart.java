/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
 *******************************************************************************/

package x10dt.ui.views;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import polyglot.ast.Node;
import polyglot.util.ErrorInfo;
import polyglot.util.Position;
import x10dt.core.X10DTCorePlugin;
import x10dt.ui.X10DTUIPlugin;
import x10dt.ui.parser.ParseController;

public class ASTValidatorViewPart extends ViewPart {
	/**
     * Listens to part-related events from the workbench to monitor when text editors are
     * activated/closed, and keep the necessary listeners pointed at the active editor.
     */
    private final class EditorPartListener implements IPartListener {
        public void partActivated(IWorkbenchPart part) {
            if (part instanceof UniversalEditor) {
                setUpActiveEditor((UniversalEditor) part);
            }
        }

        public void partClosed(IWorkbenchPart part) {
            if (part == fActiveEditor) {
                fActiveEditor= null;
                fParseController= null;
                fViolations.clear();
                refreshListView();
            }
        }

        public void partBroughtToTop(IWorkbenchPart part) { }
        public void partDeactivated(IWorkbenchPart part) { }
        public void partOpened(IWorkbenchPart part) { }
    }

    private final class InvariantLabelProvider extends LabelProvider {
		public String getText(Object element) {
			if (element instanceof ErrorInfo) {
				ErrorInfo error= (ErrorInfo) element;
				return "Line " + error.getPosition().line() + ": " + error.getMessage();
			}
			return "<unknown element type: " + element.getClass() + ">";
		}
	}

	private final class InvariantContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

		public void dispose() { }

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof List) {
				List<ErrorInfo> violations= (List<ErrorInfo>) inputElement;
				return violations.toArray(new Object[violations.size()]);
			}
			return null;
		}
	}

	private ListViewer fListViewer;

	/** Used to save view state */
	private IMemento fMemento;

	private UniversalEditor fActiveEditor;

    /**
     * The X10 ParseController for the currently-active editor, if any. Could be null
     * if the current editor is not an X10 source editor.
     */
    private ParseController fParseController;

	private List<ErrorInfo> fViolations= new LinkedList<ErrorInfo>();

	private ParseController.InvariantViolationHandler fViolationHandler = new ParseController.InvariantViolationHandler() {
		public void handleViolation(ErrorInfo error) {
			String errorFile = error.getPosition().file();
			if (errorFile != null && errorFile.endsWith(fParseController.getPath().toOSString())) {
				fViolations.add(error);
			}
		}

		public void clear() {
			fViolations.clear();
 		}

		public void consumeAST(Node root) {
			if (root == null) {
				fViolations.add(new ErrorInfo(ErrorInfo.INVARIANT_VIOLATION_KIND, "Null AST", Position.COMPILER_GENERATED));
			}
			refreshListView();
		}
	};

    private IPartListener fPartListener;

	public ASTValidatorViewPart() {
		super();
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
        site.getPage().addPartListener(fPartListener= new EditorPartListener());
	}

	public void init(IViewSite site, IMemento memento) throws PartInitException {
		init(site);
		this.fMemento= memento;
	}

	@Override
	public void dispose() {
		IWorkbenchPage page;

		if (fActiveEditor != null) {
			page= fActiveEditor.getSite().getPage();
		} else {
			page= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		}
		if (page != null) {
			page.removePartListener(fPartListener);
		}
		super.dispose();
	}

	public void saveState(IMemento memento) {
		IStructuredSelection sel= (IStructuredSelection) fListViewer.getSelection();
		if (sel.isEmpty())
			return;
		memento= memento.createChild("selection");
//		Iterator iter= sel.iterator();
//		while (iter.hasNext()) {
//			Word word= (Word) iter.next();
//			memento.createChild("descriptor", word.toString());
//		}
	}

	private void restoreState() {
		if (fMemento == null)
			return;
//		fMemento= fMemento.getChild("selection");
//		if (fMemento != null) {
//			IMemento descriptors[]= fMemento.getChildren("descriptor");
//			if (descriptors.length > 0) {
//				List objList= new ArrayList(descriptors.length);
//				for(int nX= 0; nX < descriptors.length; nX++) {
//					String id= descriptors[nX].getID();
//					Word word= input.find(id);
//					if (word != null)
//						objList.add(word);
//				}
//				fListViewer.setSelection(new StructuredSelection(objList));
//			}
//		}
//		fMemento= null;
		updateActionEnablement();
	}

	@Override
	public void createPartControl(Composite parent) {
		fListViewer= new ListViewer(parent);

		fListViewer.setContentProvider(new InvariantContentProvider());
		fListViewer.setLabelProvider(new InvariantLabelProvider());
		fListViewer.setInput(fViolations);

		// Create menu and toolbars.
		createActions();
		createMenu();
		createToolbar();
		createContextMenu();
		hookGlobalActions();

		// Restore state from the previous session.
		restoreState();
	}

    private void setUpActiveEditor(UniversalEditor editor) {
        if (editor == null)
            return;

        // Remove our hooks from previous editor, if any
        if (fActiveEditor != null) {
        	IParseController pc= fActiveEditor.getParseController();
        	if (pc instanceof ParseController) {
        		ParseController x10PC= (ParseController) pc;
        		x10PC.setViolationHandler(null);
        	}
        }

        if (!editor.getParseController().getLanguage().getName().equals(X10DTCorePlugin.kLanguageName))
        	return; // only hook into X10 source editors

        fActiveEditor = editor;
        IParseController pc= editor.getParseController();
        if (pc instanceof ParseController) {
        	fParseController = (ParseController) pc;
        	fParseController.setViolationHandler(fViolationHandler);

        	// Force a parse to update this view
        	try {
        		fParseController.parse(editor.getDocumentProvider().getDocument(editor.getEditorInput()).get(), null);
        	} catch (Exception e) {
        		X10DTUIPlugin.log(e);
        	}
        } else {
        	fParseController= null; // UniversalEditor services languages other than X10, so don't assume this is an X10 source editor
        }
    }

	private void refreshListView() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (fListViewer.getControl() != null && !fListViewer.getControl().isDisposed()) {
					fListViewer.setInput(fViolations);
				}
			}
		});
	}

    private void updateActionEnablement() {
        IStructuredSelection sel = (IStructuredSelection) fListViewer.getSelection();
//      deleteItemAction.setEnabled(sel.size() > 0);
    }

    private void hookGlobalActions() {
		// TODO Auto-generated method stub
	}

	private void createActions() {
		// TODO Auto-generated method stub
	}

	private void createToolbar() {
		IToolBarManager mgr= getViewSite().getActionBars().getToolBarManager();
	}

	private void createMenu() {
		IMenuManager mgr= getViewSite().getActionBars().getMenuManager();
	}

	private void createContextMenu() {
		MenuManager menuMgr= new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				fillContextMenu(mgr);
			}
		});

		Menu menu= menuMgr.createContextMenu(fListViewer.getControl());
		fListViewer.getControl().setMenu(menu);

		// Register menu for extension.
		getSite().registerContextMenu(menuMgr, fListViewer);
	}

	private void fillContextMenu(IMenuManager mgr) {

	}

	@Override
	public void setFocus() {
		fListViewer.getControl().setFocus();
	}
}
