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

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.language.ServiceFactory;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.imp.services.IDocumentationProvider;
import org.eclipse.imp.services.IReferenceResolver;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.types.Named;
import polyglot.types.Type;
import x10dt.core.X10DTCorePlugin;
import x10dt.ui.parser.ParseController;

public class ASTInfoViewPart extends ViewPart implements ISelectionChangedListener, ISelectionListener {
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
                refreshTextView();
            }
        }

        public void partBroughtToTop(IWorkbenchPart part) { }
        public void partDeactivated(IWorkbenchPart part) { }
        public void partOpened(IWorkbenchPart part) { }
    }

	private TextViewer fTextViewer;

	/** Used to save view state */
	private IMemento fMemento;

	private UniversalEditor fActiveEditor;

    /**
     * The X10 ParseController for the currently-active X10 source editor, if any.
     * Could be null if the current editor is not an X10 source editor.
     */
    private ParseController fParseController;

    private ISourcePositionLocator fLocator;

    private IReferenceResolver fResolver;

    private IDocumentationProvider fDocProvider;

    private IPartListener fPartListener;

	public ASTInfoViewPart() {
		super();
		Language x10Lang = LanguageRegistry.findLanguage(X10DTCorePlugin.kLanguageName);
		fResolver= ServiceFactory.getInstance().getReferenceResolver(x10Lang);
		fDocProvider= ServiceFactory.getInstance().getDocumentationProvider(x10Lang);
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
	public void createPartControl(Composite parent) {
		fTextViewer= new TextViewer(parent, SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
		fTextViewer.setDocument(new Document());

		// Create menu and toolbars.
		createActions();
		createMenu();
		createToolbar();
		createContextMenu();
		hookGlobalActions();

		// Restore state from the previous session.
		restoreState();

		IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		ISelectionService selSvc= win.getSelectionService();

		selSvc.addPostSelectionListener(this);
	}

	@Override
	public void dispose() {
		IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		ISelectionService selSvc= win.getSelectionService();
		IWorkbenchPage page;

		selSvc.removePostSelectionListener(this);
		if (fActiveEditor != null) {
			fActiveEditor.getSelectionProvider().removeSelectionChangedListener(this);
			page= fActiveEditor.getSite().getPage();
		} else {
			page= win.getActivePage();
		}
		if (page != null) {
			page.removePartListener(fPartListener);
		}
		super.dispose();
	}

    private void setUpActiveEditor(UniversalEditor editor) {
        if (editor == null || editor == fActiveEditor)
            return;

        // Remove our hooks from previous editor, if any
        if (fActiveEditor != null) {
        	IParseController pc= fActiveEditor.getParseController();
        	if (pc instanceof ParseController) {
        		ParseController x10PC= (ParseController) pc;
        	}
        	fActiveEditor.getSelectionProvider().removeSelectionChangedListener(this);
        }

        if (!editor.getParseController().getLanguage().getName().equals(X10DTCorePlugin.kLanguageName))
        	return; // only hook into X10 source editors

        fActiveEditor = editor;
        IParseController pc= editor.getParseController();
        if (pc instanceof ParseController) {
        	fActiveEditor.getSelectionProvider().addSelectionChangedListener(this);
        	fParseController = (ParseController) pc;
        	fLocator= fParseController.getSourcePositionLocator();
        } else {
        	fParseController= null; // UniversalEditor services languages other than X10, so don't assume this is an X10 source editor
        }
    }

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part == fActiveEditor && selection instanceof ITextSelection) {
			refreshTextView();
		}
	}

	public void selectionChanged(SelectionChangedEvent event) {
		refreshTextView();
	}

	private void refreshTextView() {
		final StringBuilder sb= new StringBuilder();

		if (fActiveEditor != null) {
			IRegion selRegion= fActiveEditor.getSelectedRegion();

			reportRange(sb, "Selection: ", selRegion.getOffset(), selRegion.getLength());

			Node root= (Node) fParseController.getCurrentAst();

			if (root == null) {
				sb.append("<AST root is null>\n");
			} else {
				Node node;

				try {
					node= (Node) fLocator.findNode(root, selRegion.getOffset(), selRegion.getOffset() + selRegion.getLength() - 1);
				} catch (Exception e) {
					reportException(sb, e, " determining selected node");
					node= null;
				}

				reportNodeExtent(sb, "AST root extent: ", root);
				if (node != null) {
					reportNodeExtent(sb, "Selected node extent: ", node);
					sb.append('\n');
					reportNodeTypes(sb, root, node);
					sb.append('\n');

					if (node instanceof TypeNode) {
                        TypeNode typeNode= (TypeNode) node;
                        sb.append("Type name: ");
                        sb.append(typeNode.toString());
                        sb.append('\n');
                        sb.append('\n');
					} else if (node instanceof Named) {
					    Named named = (Named) node;
					    sb.append("Type name: ");
					    sb.append(named.name().toString());
                        sb.append('\n');
                        sb.append('\n');
					} else if (node instanceof Id) {
					    Id id = (Id) node;
					    sb.append("Identifier: ");
					    sb.append(id.toString());
                        sb.append('\n');
					    sb.append('\n');
					}
					Object referent= reportRef(sb, node);

					sb.append('\n');

					if (referent != null) {
						try {
							String doc= fDocProvider.getDocumentation(referent, fParseController);
	
							sb.append("Documentation: ");
							sb.append((doc != null) ? doc : "<no doc string>");
							sb.append("\n");
						} catch (Exception e) {
							reportException(sb, e, "obtaining documentation");
						}
					}
				}
			}
		}

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (fTextViewer.getControl() != null && !fTextViewer.getControl().isDisposed()) {
					fTextViewer.getDocument().set(sb.toString());
				}
			}
		});
	}

	private Object reportRef(StringBuilder sb, Node node) {
		Object decl;
		try {
			decl= fResolver.getLinkTarget(node, fParseController);
			reportObjectType(sb, decl, "Referent of node: ", "<no referent>");
			return decl;
		} catch(Exception e) {
			reportException(sb, e, "determining referent");
			return null;
		}
	}

	private void reportNodeTypes(StringBuilder sb, Node root, Node node) {
		reportObjectType(sb, root, "Root node type: ", "<null AST root>");
		sb.append('\n');
		reportObjectType(sb, node, "Selected node type: ", "<null node returned>");

		if (node instanceof Expr) {
			Expr expr= (Expr) node;
			Type type= expr.type();

			if (type != null) {
				try {
					String typeStr= type.toString();
					sb.append("Expression type: ");
					sb.append(typeStr);
					sb.append('\n');
				} catch (Exception e) {
					reportException(sb, e, "obtaining type of expression");
				}
			}
		}
	}

	private void reportObjectType(StringBuilder sb, Object node, String header, String ifNullMsg) {
		sb.append(header);
		if (node == null) {
			sb.append(ifNullMsg);
		} else {
			sb.append(node.getClass());
		}
		sb.append('\n');
	}

	private void reportNodeExtent(StringBuilder sb, String header, Object node) {
		try {
			int nodeStart= fLocator.getStartOffset(node);
			int nodeEnd= fLocator.getEndOffset(node);

			reportRange(sb, header, nodeStart, nodeEnd - nodeStart + 1);
		} catch (Exception e) {
			reportException(sb, e, "determining object extent");
		}
	}

	private void reportRange(StringBuilder sb, String header, int offset, int length) {
		sb.append(header);
		sb.append("offset ");
		sb.append(offset);
		sb.append(", length ");
		sb.append(length);
		sb.append(" (end ");
		sb.append(offset + length - 1);
		sb.append(")\n");
	}

	private void reportException(final StringBuilder sb, Exception e, String activity) {
		sb.append("<Exception caught while " + activity + ": ");
		sb.append(e.getMessage());
		sb.append(">\n");
	}

	public void saveState(IMemento memento) {
		ITextSelection sel= (ITextSelection) fTextViewer.getSelection();
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

    private void updateActionEnablement() {
        ITextSelection sel = (ITextSelection) fTextViewer.getSelection();
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

		Menu menu= menuMgr.createContextMenu(fTextViewer.getControl());
		fTextViewer.getControl().setMenu(menu);

		// Register menu for extension.
		getSite().registerContextMenu(menuMgr, fTextViewer);
	}

	private void fillContextMenu(IMenuManager mgr) {

	}

	@Override
	public void setFocus() {
		fTextViewer.getControl().setFocus();
	}
}
