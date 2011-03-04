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
package x10dt.search.ui.dialogs;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.imp.ui.wizards.buildpaths.Strings;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.search.internal.ui.text.BasicElementLabels;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.actions.WorkingSetFilterActionGroup;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.engine.X10SearchEngine;
import x10dt.search.core.engine.scope.IX10SearchScope;
import x10dt.search.core.engine.scope.SearchScopeFactory;
import x10dt.search.core.engine.scope.X10SearchScope;
import x10dt.search.ui.Messages;
import x10dt.search.ui.UISearchPlugin;
import x10dt.search.ui.typeHierarchy.DefaultTypeNameMatch;
import x10dt.search.ui.typeHierarchy.ITypeInfoFilterExtension;
import x10dt.search.ui.typeHierarchy.ITypeInfoImageProvider;
import x10dt.search.ui.typeHierarchy.ITypeSelectionComponent;
import x10dt.search.ui.typeHierarchy.SearchUtils;
import x10dt.search.ui.typeHierarchy.TextFieldNavigationHandler;
import x10dt.search.ui.typeHierarchy.TypeFilter;
import x10dt.search.ui.typeHierarchy.TypeInfoFilter;
import x10dt.search.ui.typeHierarchy.TypeInfoRequestorAdapter;
import x10dt.search.ui.typeHierarchy.TypeNameMatch;
import x10dt.search.ui.typeHierarchy.TypeNameMatchLabelProvider;
import x10dt.search.ui.typeHierarchy.TypeNameMatchRequestor;
import x10dt.search.ui.typeHierarchy.TypeSelectionExtension;


/**
 * Shows a list of Java types to the user with a text entry field for a string
 * pattern used to filter the list of types.
 *
 * @since 3.3
 */
public class FilteredTypesSelectionDialog extends FilteredItemsSelectionDialog implements ITypeSelectionComponent {

	/**
	 * User-readable string for separating post qualified names (e.g. " - ").
	 */
	public final static String CONCAT_STRING= " - ";
	
	/**
	 * Disabled "Show Container for Duplicates because of
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=184693 .
	 */
	private static final boolean BUG_184693= true;

	private static final String DIALOG_SETTINGS= "x10dt.search.ui.dialogs.FilteredTypesSelectionDialog"; //$NON-NLS-1$

	private static final String SHOW_CONTAINER_FOR_DUPLICATES= "ShowContainerForDuplicates"; //$NON-NLS-1$

	private static final String WORKINGS_SET_SETTINGS= "WorkingSet"; //$NON-NLS-1$

	private WorkingSetFilterActionGroup fFilterActionGroup;

	private final TypeItemLabelProvider fTypeInfoLabelProvider;

	private String fTitle;

	private ShowContainerForDuplicatesAction fShowContainerForDuplicatesAction;

	private IX10SearchScope fSearchScope;

	private boolean fAllowScopeSwitching;

	private final int fElementKinds;

	private final ITypeInfoFilterExtension fFilterExtension;

	private final TypeSelectionExtension fExtension;

	private ISelectionStatusValidator fValidator;

	private final TypeInfoUtil fTypeInfoUtil;

	private static boolean fgFirstTime= true;

	private final TypeItemsComparator fTypeItemsComparator;

	private int fTypeFilterVersion= 0;

	private TypeItemsFilter fFilter;

	/**
	 * Creates new FilteredTypesSelectionDialog instance
	 *
	 * @param parent
	 *            shell to parent the dialog on
	 * @param multi
	 *            <code>true</code> if multiple selection is allowed
	 * @param context
	 *            context used to execute long-running operations associated
	 *            with this dialog
	 * @param scope
	 *            scope used when searching for types
	 * @param elementKinds
	 *            flags defining nature of searched elements; the only valid
	 *            values are: <code>IJavaSearchConstants.TYPE</code>
	 * 	<code>IJavaSearchConstants.ANNOTATION_TYPE</code>
	 * 	<code>IJavaSearchConstants.INTERFACE</code>
	 * 	<code>IJavaSearchConstants.ENUM</code>
	 * 	<code>IJavaSearchConstants.CLASS_AND_INTERFACE</code>
	 * 	<code>IJavaSearchConstants.CLASS_AND_ENUM</code>.
	 *            Please note that the bitwise OR combination of the elementary
	 *            constants is not supported.
	 */
	public FilteredTypesSelectionDialog(Shell parent, boolean multi, IRunnableContext context, IX10SearchScope scope, int elementKinds) {
		this(parent, multi, context, scope, elementKinds, null);
	}

	/**
	 * Creates new FilteredTypesSelectionDialog instance.
	 *
	 * @param shell
	 *            shell to parent the dialog on
	 * @param multi
	 *            <code>true</code> if multiple selection is allowed
	 * @param context
	 *            context used to execute long-running operations associated
	 *            with this dialog
	 * @param scope
	 *            scope used when searching for types. If the scope is <code>null</code>,
	 *            then workspace is scope is used as default, and the user can
	 *            choose a working set as scope.
	 * @param elementKinds
	 *            flags defining nature of searched elements; the only valid
	 *            values are: <code>IJavaSearchConstants.TYPE</code>
	 * 	<code>IJavaSearchConstants.ANNOTATION_TYPE</code>
	 * 	<code>IJavaSearchConstants.INTERFACE</code>
	 * 	<code>IJavaSearchConstants.ENUM</code>
	 * 	<code>IJavaSearchConstants.CLASS_AND_INTERFACE</code>
	 * 	<code>IJavaSearchConstants.CLASS_AND_ENUM</code>.
	 *            Please note that the bitwise OR combination of the elementary
	 *            constants is not supported.
	 * @param extension
	 *            an extension of the standard type selection dialog; See
	 *            {@link TypeSelectionExtension}
	 */
	public FilteredTypesSelectionDialog(Shell shell, boolean multi, IRunnableContext context, IX10SearchScope scope, int elementKinds, TypeSelectionExtension extension) {
		super(shell, multi);

		setSelectionHistory(new TypeSelectionHistory());

		if (scope == null) {
			fAllowScopeSwitching= true;
			scope= SearchScopeFactory.createWorkspaceScope(X10SearchScope.ALL);
		}

		fElementKinds= elementKinds;
		fExtension= extension;
		fFilterExtension= (extension == null) ? null : extension.getFilterExtension();
		fSearchScope= scope;

		if (extension != null) {
			fValidator= extension.getSelectionValidator();
		}

		fTypeInfoUtil= new TypeInfoUtil(extension != null ? extension.getImageProvider() : null);

		fTypeInfoLabelProvider= new TypeItemLabelProvider();
		
		setListLabelProvider(fTypeInfoLabelProvider);
		setListSelectionLabelDecorator(fTypeInfoLabelProvider);

		setDetailsLabelProvider(new TypeItemDetailsLabelProvider(fTypeInfoUtil));

		fTypeItemsComparator= new TypeItemsComparator();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.dialogs.SelectionDialog#setTitle(java.lang.String)
	 */
	public void setTitle(String title) {
		super.setTitle(title);
		fTitle= title;
	}

	/**
	 * Adds or replaces subtitle of the dialog
	 *
	 * @param text
	 *            the new subtitle for this dialog
	 */
	private void setSubtitle(String text) {
		if (text == null || text.length() == 0) {
			getShell().setText(fTitle);
		} else {
			getShell().setText(MessageFormat.format(Messages.FilteredTypeSelectionDialog_titleFormat, new String[] { fTitle, text }));
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.dialogs.AbstractSearchDialog#getDialogSettings()
	 */
	protected IDialogSettings getDialogSettings() {
		IDialogSettings settings= UISearchPlugin.getDefault().getDialogSettings().getSection(DIALOG_SETTINGS);

		if (settings == null) {
			settings= UISearchPlugin.getDefault().getDialogSettings().addNewSection(DIALOG_SETTINGS);
		}

		return settings;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.dialogs.AbstractSearchDialog#storeDialog(org.eclipse.jface.dialogs.IDialogSettings)
	 */
	protected void storeDialog(IDialogSettings settings) {
		super.storeDialog(settings);

		if (! BUG_184693) {
			settings.put(SHOW_CONTAINER_FOR_DUPLICATES, fShowContainerForDuplicatesAction.isChecked());
		}

		if (fFilterActionGroup != null) {
			XMLMemento memento= XMLMemento.createWriteRoot("workingSet"); //$NON-NLS-1$
			fFilterActionGroup.saveState(memento);
			fFilterActionGroup.dispose();
			StringWriter writer= new StringWriter();
			try {
				memento.save(writer);
				settings.put(WORKINGS_SET_SETTINGS, writer.getBuffer().toString());
			} catch (IOException e) {
				// don't do anything. Simply don't store the settings
				UISearchPlugin.log(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.dialogs.AbstractSearchDialog#restoreDialog(org.eclipse.jface.dialogs.IDialogSettings)
	 */
	protected void restoreDialog(IDialogSettings settings) {
		super.restoreDialog(settings);

		if (! BUG_184693) {
			boolean showContainer= settings.getBoolean(SHOW_CONTAINER_FOR_DUPLICATES);
			fShowContainerForDuplicatesAction.setChecked(showContainer);
			fTypeInfoLabelProvider.setContainerInfo(showContainer);
		} else {
			fTypeInfoLabelProvider.setContainerInfo(true);
		}

		if (fAllowScopeSwitching) {
			String setting= settings.get(WORKINGS_SET_SETTINGS);
			if (setting != null) {
				try {
					IMemento memento= XMLMemento.createReadRoot(new StringReader(setting));
					fFilterActionGroup.restoreState(memento);
				} catch (WorkbenchException e) {
					// don't do anything. Simply don't restore the settings
					UISearchPlugin.log(e);
				}
			}
			IWorkingSet ws= fFilterActionGroup.getWorkingSet();
			if (ws == null || (ws.isAggregateWorkingSet() && ws.isEmpty())) {
				setSearchScope(SearchScopeFactory.createWorkspaceScope(X10SearchScope.ALL));
				setSubtitle(null);
			} else {
				//setSearchScope(SearchScopeFactory.createWorkspaceScope(X10SearchScope.ALL, ws.getElements()));
				setSubtitle(ws.getLabel());
			}
		}

		// TypeNameMatch[] types = OpenTypeHistory.getInstance().getTypeInfos();
		//
		// for (int i = 0; i < types.length; i++) {
		// TypeNameMatch type = types[i];
		// accessedHistoryItem(type);
		// }
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.dialogs.AbstractSearchDialog#fillViewMenu(org.eclipse.jface.action.IMenuManager)
	 */
	protected void fillViewMenu(IMenuManager menuManager) {
		super.fillViewMenu(menuManager);

		if (! BUG_184693) {
			fShowContainerForDuplicatesAction= new ShowContainerForDuplicatesAction();
			menuManager.add(fShowContainerForDuplicatesAction);
		}
		if (fAllowScopeSwitching) {
			fFilterActionGroup= new WorkingSetFilterActionGroup(getShell(), new IPropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent event) {
					IWorkingSet ws= (IWorkingSet) event.getNewValue();
					if (ws == null || (ws.isAggregateWorkingSet() && ws.isEmpty())) {
						setSearchScope(SearchScopeFactory.createWorkspaceScope(X10SearchScope.ALL));
						setSubtitle(null);
					} else {
//						setSearchScope(SearchScopeFactory.createWorkspaceScope(X10SearchScope.ALL, ws.getElements());
						setSubtitle(ws.getLabel());
					}

					applyFilter();
				}
			});
//			fFilterActionGroup.fillContextMenu(menuManager);
		}

//		menuManager.add(new Separator());
//		menuManager.add(new TypeFiltersPreferencesAction());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#createExtendedContentArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createExtendedContentArea(Composite parent) {
		Control addition= null;

		if (fExtension != null) {

			addition= fExtension.createContentArea(parent);
			if (addition != null) {
				GridData gd= new GridData(GridData.FILL_HORIZONTAL);
				gd.horizontalSpan= 2;
				addition.setLayoutData(gd);

			}

			fExtension.initialize(this);
		}

		return addition;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.dialogs.SelectionDialog#setResult(java.util.List)
	 */
	protected void setResult(List newResult) {

		List resultToReturn= new ArrayList();

		for (int i= 0; i < newResult.size(); i++) {
			if (newResult.get(i) instanceof TypeNameMatch) {
				ITypeInfo type= ((TypeNameMatch) newResult.get(i)).getType();
				if (type.exists(new NullProgressMonitor())) {
					// items are added to history in the
					// org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#computeResult()
					// method
					resultToReturn.add(type);
				} 
				else {
					TypeNameMatch typeInfo= (TypeNameMatch) newResult.get(i);
					String root= typeInfo.getPackageName();
					String containerName= root;
					String message= MessageFormat.format(Messages.FilteredTypesSelectionDialog_dialogMessage, new String[] 
					{ SearchUtils.getFullyQualifiedName(type), containerName });
					MessageDialog.openError(getShell(), fTitle, message);
					getSelectionHistory().remove(typeInfo);
				}
			}
		}

		super.setResult(resultToReturn);
	}

	/*
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#create()
	 */
	public void create() {
		super.create();
		Control patternControl= getPatternControl();
		if (patternControl instanceof Text) {
			TextFieldNavigationHandler.install((Text) patternControl);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.window.Window#open()
	 */
	public int open() {
		if (getInitialPattern() == null) {
			IWorkbenchWindow window= PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window != null) {
				ISelection selection= window.getSelectionService().getSelection();
				if (selection instanceof ITextSelection) {
					String text= ((ITextSelection) selection).getText();
					if (text != null) {
						text= text.trim();
						if (text.length() > 0) {
							setInitialPattern(text, FULL_SELECTION);
						}
					}
				}
			}
		}
		return super.open();
	}

	/**
	 * Sets a new validator.
	 *
	 * @param validator
	 *            the new validator
	 */
	public void setValidator(ISelectionStatusValidator validator) {
		fValidator= validator;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#createFilter()
	 */
	protected ItemsFilter createFilter() {
		fFilter= new TypeItemsFilter(fSearchScope, fElementKinds, fFilterExtension);
		return fFilter;
	}

	/*
	 * @see org.eclipse.ui.dialogs.SelectionStatusDialog#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		//PlatformUI.getWorkbench().getHelpSystem().setHelp(shell, IJavaHelpContextIds.TYPE_SELECTION_DIALOG2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#fillContentProvider(org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.AbstractContentProvider,
	 *      org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.ItemsFilter,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void fillContentProvider(AbstractContentProvider provider, ItemsFilter itemsFilter, IProgressMonitor progressMonitor) throws CoreException {
		TypeItemsFilter typeSearchFilter= (TypeItemsFilter) itemsFilter;
		TypeSearchRequestor requestor= new TypeSearchRequestor(provider, typeSearchFilter);
//		String packPattern= typeSearchFilter.getPackagePattern();
		progressMonitor.setTaskName(Messages.FilteredTypesSelectionDialog_searchJob_taskName);

		/*
		 * Setting the filter into match everything mode avoids filtering twice
		 * by the same pattern (the search engine only provides filtered
		 * matches). For the case when the pattern is a camel case pattern with
		 * a terminator, the filter is not set to match everything mode because
		 * jdt.core's SearchPattern does not support that case.
		 */
		String typePattern= typeSearchFilter.getNamePattern();
//		int matchRule= typeSearchFilter.getMatchRule();
		typeSearchFilter.setMatchEverythingMode(true);

		try {
			for(ITypeInfo type : X10SearchEngine.getAllMatchingTypeInfo(typeSearchFilter.getSearchScope(), SearchUtils.getTypeRegex(typePattern), false, progressMonitor))
			{
				TypeNameMatch match = new DefaultTypeNameMatch(type, type.getX10FlagsCode());
				requestor.acceptTypeNameMatch(match);
			}
			
		} catch(InterruptedException e) {
			// ignore -- search cancelled
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, UISearchPlugin.PLUGIN_ID, e.getMessage(), e));
		} finally {
			typeSearchFilter.setMatchEverythingMode(false);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#getItemsComparator()
	 */
	protected Comparator getItemsComparator() {
		return fTypeItemsComparator;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#getElementName(java.lang.Object)
	 */
	public String getElementName(Object item) {
		TypeNameMatch type= (TypeNameMatch) item;
		return type.getSimpleTypeName();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#validateItem(java.lang.Object)
	 */
	protected IStatus validateItem(Object item) {

		if (item == null)
			return new Status(IStatus.ERROR, UISearchPlugin.PLUGIN_ID, IStatus.ERROR, "", null); //$NON-NLS-1$

		if (fValidator != null) {
			ITypeInfo type= ((TypeNameMatch) item).getType();
			if (!type.exists(new NullProgressMonitor())) {
				String qualifiedName= SearchUtils.getFullyQualifiedName(type);
				return new Status(IStatus.ERROR, UISearchPlugin.PLUGIN_ID, IStatus.ERROR, MessageFormat.format(Messages.FilteredTypesSelectionDialog_error_type_doesnot_exist, qualifiedName), null);
			}
			Object[] elements= { type };
			return fValidator.validate(elements);
		} else
			return new Status(IStatus.OK, UISearchPlugin.PLUGIN_ID, IStatus.OK, "", null); //$NON-NLS-1$
	}

	/**
	 * Sets search scope used when searching for types.
	 *
	 * @param scope
	 *            the new scope
	 */
	private void setSearchScope(IX10SearchScope scope) {
		fSearchScope= scope;
	}

	/*
	 * We only have to ensure history consistency here since the search engine
	 * takes care of working copies.
	 */
	private static class ConsistencyRunnable implements IRunnableWithProgress {
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			if (fgFirstTime) {
				// Join the initialize after load job.
				IJobManager manager= Job.getJobManager();
				manager.join(UISearchPlugin.PLUGIN_ID, monitor);
			}
			OpenTypeHistory history= OpenTypeHistory.getInstance();
			if (fgFirstTime || history.isEmpty()) {
				if (history.needConsistencyCheck()) {
					monitor.beginTask(Messages.TypeSelectionDialog_progress_consistency, 100);
					refreshSearchIndices(new SubProgressMonitor(monitor, 90));
					history.checkConsistency(new SubProgressMonitor(monitor, 10));
				} else {
					refreshSearchIndices(monitor);
				}
				monitor.done();
				fgFirstTime= false;
			} else {
				history.checkConsistency(monitor);
			}
		}
		public static boolean needsExecution() {
			OpenTypeHistory history= OpenTypeHistory.getInstance();
			return fgFirstTime || history.isEmpty() || history.needConsistencyCheck();
		}
		private void refreshSearchIndices(IProgressMonitor monitor) throws InvocationTargetException {
//			try {
//				new X10SearchEngine().searchAllTypeNames(
//						null,
//						0,
//						// make sure we search a concrete name. This is faster according to Kent
//						"_______________".toCharArray(), //$NON-NLS-1$
//						SearchPattern.RULE_EXACT_MATCH | SearchPattern.RULE_CASE_SENSITIVE,
//						IJavaSearchConstants.ENUM,
//						X10SearchEngine.createWorkspaceScope(),
//						new TypeNameRequestor() {},
//						IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
//						monitor);
//			} catch (Exception e) {
//				throw new InvocationTargetException(e);
//			}
		}
	}

	/*
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#reloadCache(boolean, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void reloadCache(boolean checkDuplicates, IProgressMonitor monitor) {
		IProgressMonitor remainingMonitor;
		if (ConsistencyRunnable.needsExecution()) {
			monitor.beginTask(Messages.TypeSelectionDialog_progress_consistency, 10);
			try {
				ConsistencyRunnable runnable= new ConsistencyRunnable();
				runnable.run(new SubProgressMonitor(monitor, 1));
			} catch (InvocationTargetException e) {
				UISearchPlugin.log(e);
				//ExceptionHandler.handle(e, Messages.TypeSelectionDialog_error3Title, Messages.TypeSelectionDialog_error3Message);
				close();
				return;
			} catch (InterruptedException e) {
				// cancelled by user
				close();
				return;
			}
			remainingMonitor= new SubProgressMonitor(monitor, 9);
		} else {
			remainingMonitor= monitor;
		}
		super.reloadCache(checkDuplicates, remainingMonitor);
		monitor.done();
	}

	/*
	 * @see org.eclipse.jdt.ui.dialogs.ITypeSelectionComponent#triggerSearch()
	 */
	public void triggerSearch() {
		fTypeFilterVersion++;
		applyFilter();
	}

	/**
	 * The <code>ShowContainerForDuplicatesAction</code> provides means to
	 * show/hide container information for duplicate elements.
	 */
	private class ShowContainerForDuplicatesAction extends Action {

		/**
		 * Creates a new instance of the class
		 */
		public ShowContainerForDuplicatesAction() {
			super(Messages.FilteredTypeSelectionDialog_showContainerForDuplicatesAction, IAction.AS_CHECK_BOX);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.jface.action.Action#run()
		 */
		public void run() {
			fTypeInfoLabelProvider.setContainerInfo(isChecked());
		}
	}

//	private class TypeFiltersPreferencesAction extends Action {
//
//		public TypeFiltersPreferencesAction() {
//			super(Messages.FilteredTypesSelectionDialog_TypeFiltersPreferencesAction_label);
//		}
//
//		/*
//		 * (non-Javadoc)
//		 *
//		 * @see org.eclipse.jface.action.Action#run()
//		 */
//		public void run() {
//			String typeFilterID= TypeFilterPreferencePage.TYPE_FILTER_PREF_PAGE_ID;
//			PreferencesUtil.createPreferenceDialogOn(getShell(), typeFilterID, new String[] { typeFilterID }, null).open();
//			triggerSearch();
//		}
//	}

	/**
	 * A <code>LabelProvider</code> for (the table of) types.
	 */
	private class TypeItemLabelProvider extends LabelProvider implements ILabelDecorator, IStyledLabelProvider {

		private boolean fContainerInfo;
		private LocalResourceManager fImageManager;

		private Font fBoldFont;

		private Styler fBoldStyler;

		private Styler fBoldQualifierStyler;

		public TypeItemLabelProvider() {
			fImageManager= new LocalResourceManager(JFaceResources.getResources());
			fBoldStyler= createBoldStyler();
			fBoldQualifierStyler= createBoldQualifierStyler();
		}

		/*
		 * @see org.eclipse.jface.viewers.BaseLabelProvider#dispose()
		 */
		public void dispose() {
			super.dispose();
			fImageManager.dispose();
			if (fBoldFont != null) {
				fBoldFont.dispose();
				fBoldFont= null;
			}
		}

		public void setContainerInfo(boolean containerInfo) {
			fContainerInfo= containerInfo;
			fireLabelProviderChanged(new LabelProviderChangedEvent(this));
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object element) {
			if (!(element instanceof TypeNameMatch)) {
				return super.getImage(element);
			}
			ImageDescriptor contributedImageDescriptor= fTypeInfoUtil.getContributedImageDescriptor(element);
			if (contributedImageDescriptor == null) {
				return TypeNameMatchLabelProvider.getImage((TypeNameMatch) element, TypeNameMatchLabelProvider.SHOW_TYPE_ONLY);
			} else {
				return fImageManager.createImage(contributedImageDescriptor);
			}
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object element) {
			if (!(element instanceof TypeNameMatch)) {
				return super.getText(element);
			}
			TypeNameMatch typeMatch= (TypeNameMatch) element;
			if (fContainerInfo && isDuplicateElement(element)) {
				return BasicElementLabels.getResourceName(fTypeInfoUtil.getFullyQualifiedText(typeMatch));
			}

			if (!fContainerInfo && isDuplicateElement(element)) {
				return BasicElementLabels.getResourceName(fTypeInfoUtil.getQualifiedText(typeMatch));
			}

			return BasicElementLabels.getResourceName(typeMatch.getSimpleTypeName());
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateImage(org.eclipse.swt.graphics.Image,
		 *      java.lang.Object)
		 */
		public Image decorateImage(Image image, Object element) {
			return image;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateText(java.lang.String,
		 *      java.lang.Object)
		 */
		public String decorateText(String text, Object element) {
			if (!(element instanceof TypeNameMatch)) {
				return null;
			}

			if (fContainerInfo && isDuplicateElement(element)) {
				return BasicElementLabels.getResourceName(fTypeInfoUtil.getFullyQualifiedText((TypeNameMatch) element));
			}

			return BasicElementLabels.getResourceName(fTypeInfoUtil.getQualifiedText((TypeNameMatch) element));
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider#getStyledText(java.lang.Object)
		 */
		public StyledString getStyledText(Object element) {
			String text= getText(element);
			StyledString string= new StyledString(text);

			int index= text.indexOf(CONCAT_STRING);

			final String namePattern= fFilter != null ? fFilter.getNamePattern() : null;
			if (namePattern != null && !"*".equals(namePattern)) { //$NON-NLS-1$
				String typeName= index == -1 ? text : text.substring(0, index);
				int[] matchingRegions= SearchUtils.getMatchingRegions(namePattern, typeName, fFilter.getMatchRule());
				markMatchingRegions(string, 0, matchingRegions, fBoldStyler);
			}

			if (index != -1) {
				string.setStyle(index, text.length() - index, StyledString.QUALIFIER_STYLER);
				final String packagePattern= fFilter != null ? fFilter.getPackagePattern() : null;
				if (packagePattern != null && !"*".equals(packagePattern)) { //$NON-NLS-1$
					index= index + CONCAT_STRING.length();
					int endIndex= text.indexOf(CONCAT_STRING, index);
					String packageName;
					if (endIndex == -1)
						packageName= text.substring(index);
					else
						packageName= text.substring(index, endIndex);
					int[] matchingRegions= SearchUtils.getMatchingRegions(packagePattern, packageName, fFilter.getPackageFlags());
					markMatchingRegions(string, index, matchingRegions, fBoldQualifierStyler);
				}
			}
			return string;
		}

		private void markMatchingRegions(StyledString string, int index, int[] matchingRegions, Styler styler) {
			if (matchingRegions != null) {
				int offset= -1;
				int length= 0;
				for (int i= 0; i + 1 < matchingRegions.length; i= i + 2) {
					if (offset == -1)
						offset= index + matchingRegions[i];
					
					// Concatenate adjacent regions
					if (i + 2 < matchingRegions.length && matchingRegions[i] + matchingRegions[i + 1] == matchingRegions[i + 2]) {
						length= length + matchingRegions[i + 1];
					} else {
						string.setStyle(offset, length + matchingRegions[i + 1], styler);
						offset= -1;
						length= 0;
					}
				}
			}
		}

		/**
		 * Create the bold variant of the currently used font.
		 * 
		 * @return the bold font
		 * @since 3.5
		 */
		private Font getBoldFont() {
			if (fBoldFont == null) {
				Font font= getDialogArea().getFont();
				FontData[] data= font.getFontData();
				for (int i= 0; i < data.length; i++) {
					data[i].setStyle(SWT.BOLD);
				}
				fBoldFont= new Font(font.getDevice(), data);
			}
			return fBoldFont;
		}

		private Styler createBoldStyler() {
			return new Styler() {
				public void applyStyles(TextStyle textStyle) {
					textStyle.font= getBoldFont();
				}
			};
		}

		private Styler createBoldQualifierStyler() {
			return new Styler() {
				public void applyStyles(TextStyle textStyle) {
					StyledString.QUALIFIER_STYLER.applyStyles(textStyle);
					textStyle.font= getBoldFont();
				}
			};
		}

	}

	/**
	 * A <code>LabelProvider</code> for the label showing type details.
	 */
	private static class TypeItemDetailsLabelProvider extends LabelProvider {

		private final TypeInfoUtil fTypeInfoUtil;

		public TypeItemDetailsLabelProvider(TypeInfoUtil typeInfoUtil) {
			fTypeInfoUtil= typeInfoUtil;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object element) {
			if (element instanceof TypeNameMatch) {
				return TypeNameMatchLabelProvider.getImage((TypeNameMatch) element, TypeNameMatchLabelProvider.SHOW_TYPE_CONTAINER_ONLY);
			}

			return super.getImage(element);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object element) {
			if (element instanceof TypeNameMatch) {
				return BasicElementLabels.getResourceName(fTypeInfoUtil.getQualificationText((TypeNameMatch) element));
			}

			return super.getText(element);
		}
	}

	private static class TypeInfoUtil {

		private final ITypeInfoImageProvider fProviderExtension;

		private final TypeInfoRequestorAdapter fAdapter= new TypeInfoRequestorAdapter();

		private final Map fLib2Name= new HashMap();

		// private final String[] fInstallLocations;

		// private final String[] fVMNames;

		public TypeInfoUtil(ITypeInfoImageProvider extension) {
			fProviderExtension= extension;
//			List locations= new ArrayList();
//			List labels= new ArrayList();
//			IVMInstallType[] installs= JavaRuntime.getVMInstallTypes();
//			for (int i= 0; i < installs.length; i++) {
//				processVMInstallType(installs[i], locations, labels);
//			}
//			fInstallLocations= (String[]) locations.toArray(new String[locations.size()]);
//			fVMNames= (String[]) labels.toArray(new String[labels.size()]);

		}

//		private void processVMInstallType(IVMInstallType installType, List locations, List labels) {
//			if (installType != null) {
//				IVMInstall[] installs= installType.getVMInstalls();
//				boolean isMac= Platform.OS_MACOSX.equals(Platform.getOS());
//				final String HOME_SUFFIX= "/Home"; //$NON-NLS-1$
//				for (int i= 0; i < installs.length; i++) {
//					String label= getFormattedLabel(installs[i].getName());
//					LibraryLocation[] libLocations= installs[i].getLibraryLocations();
//					if (libLocations != null) {
//						processLibraryLocation(libLocations, label);
//					} else {
//						String filePath= installs[i].getInstallLocation().getAbsolutePath();
//						// on MacOS X install locations end in an additional
//						// "/Home" segment; remove it
//						if (isMac && filePath.endsWith(HOME_SUFFIX))
//							filePath= filePath.substring(0, filePath.length() - HOME_SUFFIX.length() + 1);
//						locations.add(filePath);
//						labels.add(label);
//					}
//				}
//			}
//		}
//
//		private void processLibraryLocation(LibraryLocation[] libLocations, String label) {
//			for (int l= 0; l < libLocations.length; l++) {
//				LibraryLocation location= libLocations[l];
//				fLib2Name.put(location.getSystemLibraryPath().toOSString(), label);
//			}
//		}

		private String getFormattedLabel(String name) {
			return MessageFormat.format(Messages.FilteredTypesSelectionDialog_library_name_format, name);
		}



		public String getQualifiedText(TypeNameMatch type) {
			StringBuffer result= new StringBuffer();
			result.append(type.getSimpleTypeName());
			String containerName= type.getTypeContainerName();
			result.append(CONCAT_STRING);
			if (containerName.length() > 0) {
				result.append(containerName);
			} else {
				result.append(Messages.FilteredTypesSelectionDialog_default_package);
			}
			return result.toString();
		}

		public String getFullyQualifiedText(TypeNameMatch type) {
			StringBuffer result= new StringBuffer();
			result.append(type.getSimpleTypeName());
			String containerName= type.getTypeContainerName();
			if (containerName.length() > 0) {
				result.append(CONCAT_STRING);
				result.append(containerName);
			}
			result.append(CONCAT_STRING);
			result.append(getContainerName(type));
			return result.toString();
		}

		public String getQualificationText(TypeNameMatch type) {
			StringBuffer result= new StringBuffer();
			String containerName= type.getTypeContainerName();
			if (containerName.length() > 0) {
				result.append(containerName);
				result.append(CONCAT_STRING);
			}
			result.append(getContainerName(type));
			return result.toString();
		}

		public ImageDescriptor getContributedImageDescriptor(Object element) {
			TypeNameMatch type= (TypeNameMatch) element;
			if (fProviderExtension != null) {
				fAdapter.setMatch(type);
				return fProviderExtension.getImageDescriptor(fAdapter);
			}
			return null;
		}

		private String getContainerName(TypeNameMatch type) {
			IResource res = SearchUtils.getResource(type.getType());

			if(res == null)
			{
				URI uri = type.getType().getLocation().getURI();
				String scheme = uri.getSchemeSpecificPart();
				if(uri.getScheme().equals("jar"))
				{
					scheme = scheme.substring(0, scheme.lastIndexOf(":"));
					scheme = scheme.replace("file:/", "");
				}
				
				IPath path = new Path(scheme);
				return path.toOSString();
			}
			
//			if (root.isExternal()) {
//				String name= root.getPath().toOSString();
//				for (int i= 0; i < fInstallLocations.length; i++) {
//					if (name.startsWith(fInstallLocations[i])) {
//						return fVMNames[i];
//					}
//				}
//				String lib= (String) fLib2Name.get(name);
//				if (lib != null)
//					return lib;
//			}
//			StringBuffer buf= new StringBuffer();
//			JavaElementLabels.getPackageFragmentRootLabel(root, JavaElementLabels.ROOT_QUALIFIED | JavaElementLabels.ROOT_VARIABLE, buf);
			return res.getParent().getFullPath().toPortableString().replaceFirst("/", "");
		}
	}

	/**
	 * Filters types using pattern, scope, element kind and filter extension.
	 */
	private class TypeItemsFilter extends ItemsFilter {

		private boolean fMatchEverything= false;

		private final int fMyTypeFilterVersion= fTypeFilterVersion;

		private final TypeInfoFilter fTypeInfoFilter;


		public TypeItemsFilter(IX10SearchScope scope, int elementKind, ITypeInfoFilterExtension extension) {
			/*
			 * Horribly convoluted initialization:
			 * FilteredItemsSelectionDialog.ItemsFilter#ItemsFilter(SearchPattern)
			 * fetches the pattern string from the Text widget of the outer class and
			 * initializes the given SearchPattern with that string.
			 * The default SearchPattern also removes whitespace from the pattern string,
			 * which is why we have to supply our own (dummy) implementation.
			 */
			super(new TypeSearchPattern());
			String pattern= patternMatcher.getPattern();
			fTypeInfoFilter= new TypeInfoFilter(pattern, scope, elementKind, extension);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.ItemsFilter#isSubFilter(org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.ItemsFilter)
		 */
		public boolean isSubFilter(ItemsFilter filter) {
			if (! (filter instanceof TypeItemsFilter))
				return false;
			TypeItemsFilter typeItemsFilter= (TypeItemsFilter) filter;
			if (fMyTypeFilterVersion != typeItemsFilter.getMyTypeFilterVersion())
				return false;

			//Caveat: This method is defined the wrong way 'round in FilteredItemsSelectionDialog!
			//WRONG (has reverse meaning!): return fTypeInfoFilter.isSubFilter(filter.getPattern());
			return typeItemsFilter.fTypeInfoFilter.isSubFilter(fTypeInfoFilter.getText());
		}

		public boolean equalsFilter(ItemsFilter iFilter) {
			if (!(iFilter instanceof TypeItemsFilter))
				return false;
			TypeItemsFilter typeItemsFilter= (TypeItemsFilter) iFilter;
			if (! getPattern().equals(typeItemsFilter.getPattern()))
				return false;
			if (getSearchScope() != typeItemsFilter.getSearchScope())
				return false;
			if (fMyTypeFilterVersion != typeItemsFilter.getMyTypeFilterVersion())
				return false;
			return true;
		}

		public int getElementKind() {
			return fTypeInfoFilter.getElementKind();
		}

		public IX10SearchScope getSearchScope() {
			return fTypeInfoFilter.getSearchScope();
		}

		public int getMyTypeFilterVersion() {
			return fMyTypeFilterVersion;
		}

		public String getNamePattern() {
			return fTypeInfoFilter.getNamePattern();
		}

		public String getPackagePattern() {
			return fTypeInfoFilter.getPackagePattern();
		}

		public int getPackageFlags() {
			return fTypeInfoFilter.getPackageFlags();
		}

		public boolean matchesRawNamePattern(TypeNameMatch type) {
			return fTypeInfoFilter.matchesRawNamePattern(type);
		}

		public boolean matchesFilterExtension(TypeNameMatch type) {
			return fTypeInfoFilter.matchesFilterExtension(type);
		}

		/**
		 * Set filter to "match everything" mode.
		 *
		 * @param matchEverything if <code>true</code>, {@link #matchItem(Object)} always returns true.
		 * 					If <code>false</code>, the filter is enabled.
		 */
		public void setMatchEverythingMode(boolean matchEverything) {
			fMatchEverything= matchEverything;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.ItemsFilter#isConsistentItem(java.lang.Object)
		 */
		public boolean isConsistentItem(Object item) {
			return true;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.ItemsFilter#matchItem(java.lang.Object)
		 */
		public boolean matchItem(Object item) {
			if (fMatchEverything)
				return true;

			TypeNameMatch type= (TypeNameMatch) item;
			return fTypeInfoFilter.matchesHistoryElement(type);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.ItemsFilter#matchesRawNamePattern(java.lang.Object)
		 */
		public boolean matchesRawNamePattern(Object item) {
			TypeNameMatch type= (TypeNameMatch) item;
			return matchesRawNamePattern(type);
		}

		/**
		 * @return search flags
		 * @see org.eclipse.jdt.core.search.SearchPattern#getMatchRule()
		 */
		public int getMatchRule() {
			return fTypeInfoFilter.getSearchFlags();
		}

		public String getPattern() {
			return fTypeInfoFilter.getText();
		}

		public boolean isCamelCasePattern() {
			return fTypeInfoFilter.isCamelCasePattern();
		}

		/**
		 * Matches text with filter.
		 * 
		 * @param text the text to match with the filter
		 * @return never returns
		 * @throws UnsupportedOperationException always
		 * 
		 * @deprecated not used
		 */
		protected boolean matches(String text) {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Replaces functionality of {@link org.eclipse.ui.dialogs.SearchPattern} with an
	 * adapter implementation that delegates to {@link TypeInfoFilter}.
	 */
	private static class TypeSearchPattern extends org.eclipse.ui.dialogs.SearchPattern {

		private String fPattern;

		public void setPattern(String stringPattern) {
			fPattern= stringPattern;
		}

		public String getPattern() {
			return fPattern;
		}
	}

	/**
	 * A <code>TypeSearchRequestor</code> collects matches filtered using
	 * <code>TypeItemsFilter</code>. The attached content provider is filled
	 * on the basis of the collected entries (instances of
	 * <code>TypeNameMatch</code>).
	 */
	private static class TypeSearchRequestor extends TypeNameMatchRequestor {
		private volatile boolean fStop;

		private final AbstractContentProvider fContentProvider;

		private final TypeItemsFilter fTypeItemsFilter;

		public TypeSearchRequestor(AbstractContentProvider contentProvider, TypeItemsFilter typeItemsFilter) {
			super();
			fContentProvider= contentProvider;
			fTypeItemsFilter= typeItemsFilter;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.jdt.core.search.TypeNameMatchRequestor#acceptTypeNameMatch(org.eclipse.jdt.core.search.TypeNameMatch)
		 */
		public void acceptTypeNameMatch(TypeNameMatch match) {
			if (fStop)
				return;
			if (TypeFilter.isFiltered(match))
				return;
			if (fTypeItemsFilter.matchesFilterExtension(match))
				fContentProvider.add(match, fTypeItemsFilter);
		}

	}

	/**
	 * Compares TypeItems is used during sorting
	 */
	private static class TypeItemsComparator implements Comparator {

		private final Map fLib2Name= new HashMap();

//		private final String[] fInstallLocations;

//		private final String[] fVMNames;

		/**
		 * Creates new instance of TypeItemsComparator
		 */
//		public TypeItemsComparator() {
//			List locations= new ArrayList();
//			List labels= new ArrayList();
//			IVMInstallType[] installs= JavaRuntime.getVMInstallTypes();
//			for (int i= 0; i < installs.length; i++) {
//				processVMInstallType(installs[i], locations, labels);
//			}
//			fInstallLocations= (String[]) locations.toArray(new String[locations.size()]);
//			fVMNames= (String[]) labels.toArray(new String[labels.size()]);
//		}
//
//		private void processVMInstallType(IVMInstallType installType, List locations, List labels) {
//			if (installType != null) {
//				IVMInstall[] installs= installType.getVMInstalls();
//				boolean isMac= Platform.OS_MACOSX.equals(Platform.getOS());
//				final String HOME_SUFFIX= "/Home"; //$NON-NLS-1$
//				for (int i= 0; i < installs.length; i++) {
//					String label= getFormattedLabel(installs[i].getName());
//					LibraryLocation[] libLocations= installs[i].getLibraryLocations();
//					if (libLocations != null) {
//						processLibraryLocation(libLocations, label);
//					} else {
//						String filePath= installs[i].getInstallLocation().getAbsolutePath();
//						// on MacOS X install locations end in an additional
//						// "/Home" segment; remove it
//						if (isMac && filePath.endsWith(HOME_SUFFIX))
//							filePath= filePath.substring(0, filePath.length() - HOME_SUFFIX.length() + 1);
//						locations.add(filePath);
//						labels.add(label);
//					}
//				}
//			}
//		}
//
//		private void processLibraryLocation(LibraryLocation[] libLocations, String label) {
//			for (int l= 0; l < libLocations.length; l++) {
//				LibraryLocation location= libLocations[l];
//				fLib2Name.put(location.getSystemLibraryPath().toString(), label);
//			}
//		}

		private String getFormattedLabel(String name) {
			return MessageFormat.format(Messages.FilteredTypesSelectionDialog_library_name_format, new Object[] { name });
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object left, Object right) {

			TypeNameMatch leftInfo= (TypeNameMatch) left;
			TypeNameMatch rightInfo= (TypeNameMatch) right;

			int result= compareName(leftInfo.getSimpleTypeName(), rightInfo.getSimpleTypeName());
			if (result != 0)
				return result;
			result= compareTypeContainerName(leftInfo.getTypeContainerName(), rightInfo.getTypeContainerName());
			if (result != 0)
				return result;

			int leftCategory= getElementTypeCategory(leftInfo);
			int rightCategory= getElementTypeCategory(rightInfo);
			if (leftCategory < rightCategory)
				return -1;
			if (leftCategory > rightCategory)
				return +1;
			return compareContainerName(leftInfo, rightInfo);
		}

		private int compareName(String leftString, String rightString) {
			int result= leftString.compareToIgnoreCase(rightString);
			if (result != 0 || rightString.length() == 0) {
				return result;
			} else if (Strings.isLowerCase(leftString.charAt(0)) && !Strings.isLowerCase(rightString.charAt(0))) {
				return +1;
			} else if (Strings.isLowerCase(rightString.charAt(0)) && !Strings.isLowerCase(leftString.charAt(0))) {
				return -1;
			} else {
				return leftString.compareTo(rightString);
			}
		}

		private int compareTypeContainerName(String leftString, String rightString) {
			int leftLength= leftString.length();
			int rightLength= rightString.length();
			if (leftLength == 0 && rightLength > 0)
				return -1;
			if (leftLength == 0 && rightLength == 0)
				return 0;
			if (leftLength > 0 && rightLength == 0)
				return +1;
			return compareName(leftString, rightString);
		}

		private int compareContainerName(TypeNameMatch leftType, TypeNameMatch rightType) {
			return getContainerName(leftType).compareTo(getContainerName(rightType));
		}

		private String getContainerName(TypeNameMatch type) {
			String root= type.getPackageName();
//			if (root.isExternal()) {
//				String name= root.getPath().toOSString();
//				for (int i= 0; i < fInstallLocations.length; i++) {
//					if (name.startsWith(fInstallLocations[i])) {
//						return fVMNames[i];
//					}
//				}
//				String lib= (String) fLib2Name.get(name);
//				if (lib != null)
//					return lib;
//			}
//			StringBuffer buf= new StringBuffer();
			//JavaElementLabels.getPackageFragmentRootLabel(root, JavaElementLabels.ROOT_QUALIFIED | JavaElementLabels.ROOT_VARIABLE, buf);
			return root;
		}

		private int getElementTypeCategory(TypeNameMatch type) {
//			try {
//				if (type.getPackageFragmentRoot().getKind() == IPackageFragmentRoot.K_SOURCE)
//					return 0;
//			} catch (Exception e) {
//				UISearchPlugin.log(e);
//			}
			return 1;
		}
	}

	/**
	 * Extends the <code>SelectionHistory</code>, providing support for
	 * <code>OpenTypeHistory</code>.
	 */
	protected class TypeSelectionHistory extends SelectionHistory {

		/**
		 * Creates new instance of TypeSelectionHistory
		 */

		public TypeSelectionHistory() {
			super();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.SelectionHistory#accessed(java.lang.Object)
		 */
		public synchronized void accessed(Object object) {
			super.accessed(object);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.SelectionHistory#remove(java.lang.Object)
		 */
		public synchronized boolean remove(Object element) {
			OpenTypeHistory.getInstance().remove((TypeNameMatch) element);
			return super.remove(element);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.SelectionHistory#load(org.eclipse.ui.IMemento)
		 */
		public void load(IMemento memento) {
			TypeNameMatch[] types= OpenTypeHistory.getInstance().getTypeInfos();

			for (int i= types.length - 1; i >= 0 ; i--) { // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=205314
				TypeNameMatch type= types[i];
				accessed(type);
			}
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.SelectionHistory#save(org.eclipse.ui.IMemento)
		 */
		public void save(IMemento memento) {
			persistHistory();
		}

		/**
		 * Stores contents of the local history into persistent history
		 * container.
		 */
		private synchronized void persistHistory() {
			if (getReturnCode() == OK) {
				Object[] items= getHistoryItems();
				for (int i= 0; i < items.length; i++) {
					OpenTypeHistory.getInstance().accessed((TypeNameMatch) items[i]);
				}
			}
		}

		protected Object restoreItemFromMemento(IMemento element) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.SelectionHistory#storeItemToMemento(java.lang.Object,
		 *      org.eclipse.ui.IMemento)
		 */
		protected void storeItemToMemento(Object item, IMemento element) {

		}

	}

}
