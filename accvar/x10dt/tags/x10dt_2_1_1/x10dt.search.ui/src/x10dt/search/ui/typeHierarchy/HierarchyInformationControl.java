package x10dt.search.ui.typeHierarchy;
/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/


import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.eclipse.imp.editor.ProblemsLabelDecorator;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.keys.KeySequence;
import org.eclipse.ui.keys.SWTKeySupport;

import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.ui.UISearchPlugin;
import x10dt.search.ui.actions.EditorActionDefinitionIds;
import x10dt.search.ui.typeHierarchy.SuperTypeHierarchyViewer.SuperTypeHierarchyContentProvider;
import x10dt.search.ui.typeHierarchy.TraditionalHierarchyViewer.TraditionalHierarchyContentProvider;

/**
 * Show hierarchy in light-weight control.
 *
 * @since 3.0
 */
public class HierarchyInformationControl extends AbstractInformationControl {

	private TypeHierarchyLifeCycle fLifeCycle;
	private HierarchyLabelProvider fLabelProvider;
	private KeyAdapter fKeyAdapter;

	private Object[] fOtherExpandedElements;
	private TypeHierarchyContentProvider fOtherContentProvider;

//	private IMethod fFocus; // method to filter for or null if type hierarchy
	private boolean fDoFilter;

//	private MethodOverrideTester fMethodOverrideTester;

	public HierarchyInformationControl(UniversalEditor editor, Shell parent, int shellStyle, int treeStyle) {
		super(editor, parent, shellStyle, treeStyle, EditorActionDefinitionIds.OPEN_HIERARCHY, true);
		fOtherExpandedElements= null;
		fDoFilter= true;
//		fMethodOverrideTester= null;
	}

	private KeyAdapter getKeyAdapter() {
		if (fKeyAdapter == null) {
			fKeyAdapter= new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					int accelerator = SWTKeySupport.convertEventToUnmodifiedAccelerator(e);
					KeySequence keySequence = KeySequence.getInstance(SWTKeySupport.convertAcceleratorToKeyStroke(accelerator));
					KeySequence[] sequences= getInvokingCommandKeySequences();
					if (sequences == null)
						return;

					for (int i= 0; i < sequences.length; i++) {
						if (sequences[i].equals(keySequence)) {
							e.doit= false;
							toggleHierarchy();
							return;
						}
					}
				}
			};
		}
		return fKeyAdapter;
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean hasHeader() {
		return true;
	}

	protected Text createFilterText(Composite parent) {
		// text set later
		Text text= super.createFilterText(parent);
		text.addKeyListener(getKeyAdapter());
		return text;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.text.JavaOutlineInformationControl#createTreeViewer(org.eclipse.swt.widgets.Composite, int)
	 */
	protected TreeViewer createTreeViewer(Composite parent, int style) {
		Tree tree= new Tree(parent, SWT.SINGLE | (style & ~SWT.MULTI));
		GridData gd= new GridData(GridData.FILL_BOTH);
		gd.heightHint= tree.getItemHeight() * 12;
		tree.setLayoutData(gd);

		TreeViewer treeViewer= new TreeViewer(tree);
		treeViewer.addFilter(new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return element instanceof ITypeInfo;
			}
		});

		fLifeCycle= new TypeHierarchyLifeCycle(false);

		treeViewer.setComparator(new HierarchyViewerSorter(fLifeCycle));
		treeViewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);

		fLabelProvider= new HierarchyLabelProvider(fLifeCycle);
		fLabelProvider.setFilter(new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return hasFocusMethod((ITypeInfo) element);
			}
		});

		fLabelProvider.setTextFlags(X10ElementLabels.ALL_DEFAULT | X10ElementLabels.T_POST_QUALIFIED);
		fLabelProvider.addLabelDecorator(new ProblemsLabelDecorator(editor.getLanguage()));
		treeViewer.setLabelProvider(new ColoringLabelProvider(fLabelProvider));

		treeViewer.getTree().addKeyListener(getKeyAdapter());

		return treeViewer;
	}

	protected boolean hasFocusMethod(ITypeInfo type) {
//		if (fFocus == null) {
			return true;
//		}
//		if (type.equals(fFocus.getDeclaringType())) {
//			return true;
//		}
//
//		try {
//			IMethod method= findMethod(fFocus, type);
//			if (method != null) {
//				// check visibility
//				IPackageFragment pack= (IPackageFragment) fFocus.getAncestor(ITypeInfo.PACKAGE_FRAGMENT);
//				if (JavaModelUtil.isVisibleInHierarchy(method, pack)) {
//					return true;
//				}
//			}
//		} catch (Exception e) {
//			// ignore
//			JavaPlugin.log(e);
//		}
//		return false;
	}

//	private IMethod findMethod(IMethod filterMethod, IType typeToFindIn) throws Exception {
//		IType filterType= filterMethod.getDeclaringType();
//		ITypeHierarchy hierarchy= fLifeCycle.getHierarchy();
//
//		boolean filterOverrides= JavaModelUtil.isSuperType(hierarchy, typeToFindIn, filterType);
//		IType focusType= filterOverrides ? filterType : typeToFindIn;
//
//		if (fMethodOverrideTester == null || !fMethodOverrideTester.getFocusType().equals(focusType)) {
//			fMethodOverrideTester= new MethodOverrideTester(focusType, hierarchy);
//		}
//
//		if (filterOverrides) {
//			return fMethodOverrideTester.findOverriddenMethodInType(typeToFindIn, filterMethod);
//		} else {
//			return fMethodOverrideTester.findOverridingMethodInType(typeToFindIn, filterMethod);
//		}
//	}

	/**
	 * {@inheritDoc}
	 */
	public void setInput(Object information) {
		ITypeInfo input = null;
		if(information instanceof ICompilationUnit)
		{
			ICompilationUnit cu = (ICompilationUnit)information;
			try {
				input = SelectionConverter.getInput(cu.getProject().getRawProject(), editor.getParseController(), 1);
			} catch (Exception e) {
				UISearchPlugin.log(e);
			}
		}
		
		else if(information instanceof ITypeInfo)
		{
			input = (ITypeInfo)information;
		}
		
		if(!(input instanceof ITypeInfo))
		{
			inputChanged(null, null);
			return;
		}
		
		
//		ITypeInfo input= (ITypeInfo)information;
//		IMethod locked= null;
//		try {
//			ITypeInfo elem= (ITypeInfo) information;
//			if (elem.getElementType() == ITypeInfo.LOCAL_VARIABLE) {
//				elem= elem.getParent();
//			}
//
//			switch (elem.getElementType()) {
//				case ITypeInfo.JAVA_PROJECT :
//				case ITypeInfo.PACKAGE_FRAGMENT_ROOT :
//				case ITypeInfo.PACKAGE_FRAGMENT :
//				case ITypeInfo.TYPE :
//					input= elem;
//					break;
//				case ITypeInfo.COMPILATION_UNIT :
//					input= ((ICompilationUnit) elem).findPrimaryType();
//					break;
//				case ITypeInfo.CLASS_FILE :
//					input= ((IClassFile) elem).getType();
//					break;
//				case ITypeInfo.METHOD :
//					IMethod method= (IMethod) elem;
//					if (!method.isConstructor()) {
//						locked= method;
//					}
//					input= method.getDeclaringType();
//					break;
//				case ITypeInfo.FIELD :
//				case ITypeInfo.INITIALIZER :
//					input= ((IMember) elem).getDeclaringType();
//					break;
//				case ITypeInfo.PACKAGE_DECLARATION :
//					input= elem.getParent().getParent();
//					break;
//				case ITypeInfo.IMPORT_DECLARATION :
//					IImportDeclaration decl= (IImportDeclaration) elem;
//					if (decl.isOnDemand()) {
//						input= JavaModelUtil.findTypeContainer(decl.getJavaProject(), Signature.getQualifier(decl.getElementName()));
//					} else {
//						input= decl.getJavaProject().findType(decl.getElementName());
//					}
//					break;
//				default :
//					JavaPlugin.logErrorMessage("Element unsupported by the hierarchy: " + elem.getClass()); //$NON-NLS-1$
//					// input is null;
//			}
//		} catch (Exception e) {
//			JavaPlugin.log(e);
//		}

		super.setTitleText(getHeaderLabel(input));
//		super.setTitleText(getHeaderLabel(locked == null ? input : locked));
		try {
			fLifeCycle.ensureRefreshedTypeHierarchy(input, UISearchPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow());
		} catch (InvocationTargetException e1) {
			input= null;
		} catch (InterruptedException e1) {
			dispose();
			return;
		}
//		IMember[] memberFilter= locked != null ? new IMember[] { locked } : null;

		TraditionalHierarchyContentProvider contentProvider= new TraditionalHierarchyContentProvider(fLifeCycle);
//		contentProvider.setMemberFilter(memberFilter);
		getTreeViewer().setContentProvider(contentProvider);

		fOtherContentProvider= new SuperTypeHierarchyContentProvider(fLifeCycle);
//		fOtherContentProvider.setMemberFilter(memberFilter);

//		fFocus= locked;

		Object[] topLevelObjects= contentProvider.getElements(fLifeCycle);
		if (topLevelObjects.length > 0 && contentProvider.getChildren(topLevelObjects[0]).length > 40) {
			fDoFilter= false;
		} else {
			getTreeViewer().addFilter(new NamePatternFilter());
		}

		Object selection= null;
//		if (input instanceof IMember) {
			selection=  input;
//		} else 
//		if (topLevelObjects.length > 0) {
//			selection=  topLevelObjects[0];
//		}
		inputChanged(fLifeCycle, selection);
	}

	protected void stringMatcherUpdated() {
		if (fDoFilter) {
			super.stringMatcherUpdated(); // refresh the view
		} else {
			selectFirstMatch();
		}
	}

	protected void toggleHierarchy() {
		TreeViewer treeViewer= getTreeViewer();

		treeViewer.getTree().setRedraw(false);

		Object[] expandedElements= treeViewer.getExpandedElements();
		TypeHierarchyContentProvider contentProvider= (TypeHierarchyContentProvider) treeViewer.getContentProvider();
		treeViewer.setContentProvider(fOtherContentProvider);

		treeViewer.refresh();
		if (fOtherExpandedElements != null) {
			treeViewer.setExpandedElements(fOtherExpandedElements);
		} else {
			treeViewer.expandAll();
		}

		// reveal selection
		Object selectedElement= getSelectedElement();
		if (selectedElement != null)
			getTreeViewer().reveal(selectedElement);
		else
			selectFirstMatch();

		treeViewer.getTree().setRedraw(true);

		fOtherContentProvider= contentProvider;
		fOtherExpandedElements= expandedElements;

		updateStatusFieldText();
	}


	private String getHeaderLabel(ITypeInfo input) {
//		if (input instanceof IMethod) {
//			String[] args= { X10ElementLabels.getElementLabel(input.getParent(), X10ElementLabels.ALL_DEFAULT), X10ElementLabels.getElementLabel(input, X10ElementLabels.ALL_DEFAULT) };
//			return Messages.format(TypeHierarchyMessages.HierarchyInformationControl_methodhierarchy_label, args);
//		} else 
		if (input != null) {
//			String arg= X10ElementLabels.getElementLabel(IJavaElement.TYPE, X10ElementLabels.DEFAULT_QUALIFIED);
			String arg= SearchUtils.getElementName(input);
			return MessageFormat.format(TypeHierarchyMessages.HierarchyInformationControl_hierarchy_label, arg);
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	protected String getStatusFieldText() {
		KeySequence[] sequences= getInvokingCommandKeySequences();
		String keyName= ""; //$NON-NLS-1$
		if (sequences != null && sequences.length > 0)
			keyName= sequences[0].format();

		if (fOtherContentProvider instanceof TraditionalHierarchyContentProvider) {
			return MessageFormat.format(TypeHierarchyMessages.HierarchyInformationControl_toggle_traditionalhierarchy_label, keyName);
		} else {
			return MessageFormat.format(TypeHierarchyMessages.HierarchyInformationControl_toggle_superhierarchy_label, keyName);
		}
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.text.AbstractInformationControl#getId()
	 */
	protected String getId() {
		return "x10dt.search.ui.typeHierarchy.QuickHierarchy"; //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	protected Object getSelectedElement() {
		Object selectedElement= super.getSelectedElement();
//		if (selectedElement instanceof IType && fFocus != null) {
//			IType type= (IType) selectedElement;
//			try {
//				IMethod method= findMethod(fFocus, type);
//				if (method != null) {
//					return method;
//				}
//			} catch (Exception e) {
//				JavaPlugin.log(e);
//			}
//		}
		return selectedElement;
	}
}
