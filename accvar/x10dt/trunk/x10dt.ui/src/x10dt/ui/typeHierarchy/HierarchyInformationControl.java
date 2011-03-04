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
package x10dt.ui.typeHierarchy;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.eclipse.imp.editor.ProblemsLabelDecorator;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.model.ModelFactory.ModelException;
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

import x10dt.search.core.elements.IFieldInfo;
import x10dt.search.core.elements.IMemberInfo;
import x10dt.search.core.elements.IMethodInfo;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.engine.ITypeHierarchy;
import x10dt.ui.X10DTUIPlugin;
import x10dt.ui.typeHierarchy.SuperTypeHierarchyViewer.SuperTypeHierarchyContentProvider;
import x10dt.ui.typeHierarchy.TraditionalHierarchyViewer.TraditionalHierarchyContentProvider;
import x10dt.ui.typeHierarchy.actions.EditorActionDefinitionIds;


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

	private IMethodInfo fFocus; // method to filter for or null if type hierarchy
	private boolean fDoFilter;

	private MethodOverrideTester fMethodOverrideTester;

	public HierarchyInformationControl(UniversalEditor editor, Shell parent, int shellStyle, int treeStyle) {
		super(editor, parent, shellStyle, treeStyle, EditorActionDefinitionIds.OPEN_HIERARCHY, true);
		fOtherExpandedElements= null;
		fDoFilter= true;
		fMethodOverrideTester= null;
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
		fLabelProvider.addLabelDecorator(new ProblemsLabelDecorator(LanguageRegistry.findLanguage("X10")));
		treeViewer.setLabelProvider(new ColoringLabelProvider(fLabelProvider));

		treeViewer.getTree().addKeyListener(getKeyAdapter());

		return treeViewer;
	}

	protected boolean hasFocusMethod(ITypeInfo type) {
		if (fFocus == null) {
			return true;
		}
		if (type.equals(fFocus.getDeclaringType())) {
			return true;
		}

		try {
			IMethodInfo method= findMethod(fFocus, type);
			if (method != null) {
				// check visibility
				String pack = SearchUtils.getPackageName(fFocus.getDeclaringType());
				if (ModelUtil.isVisibleInHierarchy(method, pack)) {
					return true;
				}
			}
		} catch (ModelException e) {
			// ignore
			X10DTUIPlugin.log(e);
		}
		return false;

	}

	private IMethodInfo findMethod(IMethodInfo filterMethod, ITypeInfo typeToFindIn) throws ModelException {
		ITypeInfo filterType= filterMethod.getDeclaringType();
		ITypeHierarchy hierarchy= fLifeCycle.getHierarchy();

		boolean filterOverrides= ModelUtil.isSuperType(hierarchy, typeToFindIn, filterType);
		ITypeInfo focusType= filterOverrides ? filterType : typeToFindIn;

		if (fMethodOverrideTester == null || !fMethodOverrideTester.getFocusType().equals(focusType)) {
			fMethodOverrideTester= new MethodOverrideTester(focusType, hierarchy);
		}

		if (filterOverrides) {
			return fMethodOverrideTester.findOverriddenMethodInType(typeToFindIn, filterMethod);
		} else {
			return fMethodOverrideTester.findOverridingMethodInType(typeToFindIn, filterMethod);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setInput(Object information) {
		if (!(information instanceof IMemberInfo)) {
			inputChanged(null, null);
			return;
		}
		IMemberInfo input= null;
		IMethodInfo locked= null;
		
		IMemberInfo elem= (IMemberInfo) information;
		if(elem instanceof ITypeInfo)
		{
			input= elem;
		}
		
		else if(elem instanceof ICompilationUnit)
		{
//				input= ((ICompilationUnit) elem).findPrimaryType();
		}
		
		else if(elem instanceof IMethodInfo)
		{
			IMethodInfo method= (IMethodInfo) elem;
			if (!method.isConstructor()) {
				locked= method;
			}
			input= method.getDeclaringType();
		}
		
		else if(elem instanceof IFieldInfo)
		{
			input= ((IMemberInfo) elem).getDeclaringType();
		}

		super.setTitleText(getHeaderLabel(locked == null ? input : locked));
		try {
			fLifeCycle.ensureRefreshedTypeHierarchy(input, X10DTUIPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow());
		} catch (InvocationTargetException e1) {
			input= null;
		} catch (InterruptedException e1) {
			dispose();
			return;
		}
		IMemberInfo[] memberFilter= locked != null ? new IMemberInfo[] { locked } : null;

		TraditionalHierarchyContentProvider contentProvider= new TraditionalHierarchyContentProvider(fLifeCycle);
		contentProvider.setMemberFilter(memberFilter);
		getTreeViewer().setContentProvider(contentProvider);

		fOtherContentProvider= new SuperTypeHierarchyContentProvider(fLifeCycle);
		fOtherContentProvider.setMemberFilter(memberFilter);

		fFocus= locked;

		Object[] topLevelObjects= contentProvider.getElements(fLifeCycle);
		if (topLevelObjects.length > 0 && contentProvider.getChildren(topLevelObjects[0]).length > 40) {
			fDoFilter= false;
		} else {
			getTreeViewer().addFilter(new NamePatternFilter());
		}

		Object selection= null;
		if (input instanceof IMemberInfo) {
			selection=  input;
		} else if (topLevelObjects.length > 0) {
			selection=  topLevelObjects[0];
		}
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


	private String getHeaderLabel(IMemberInfo input) {
		if (input instanceof IMethodInfo) {
			String[] args= { X10ElementLabels.getElementLabel(input.getDeclaringType(), X10ElementLabels.ALL_DEFAULT), X10ElementLabels.getElementLabel(input, X10ElementLabels.ALL_DEFAULT) };
			return MessageFormat.format(TypeHierarchyMessages.HierarchyInformationControl_methodhierarchy_label, args);
		} else if (input != null) {
			String arg= X10ElementLabels.getElementLabel(input, X10ElementLabels.DEFAULT_QUALIFIED);
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
		return "org.eclipse.jdt.internal.ui.typehierarchy.QuickHierarchy"; //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	protected Object getSelectedElement() {
		Object selectedElement= super.getSelectedElement();
		if (selectedElement instanceof ITypeInfo && fFocus != null) {
			ITypeInfo type= (ITypeInfo) selectedElement;
			try {
				IMethodInfo method= findMethod(fFocus, type);
				if (method != null) {
					return method;
				}
			} catch (ModelException e) {
				X10DTUIPlugin.log(e);
			}
		}
		return selectedElement;
	}
}
