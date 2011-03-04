package x10dt.search.ui.globalSearch;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;

import org.eclipse.jface.text.ITextSelection;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.IWorkbenchAdapter;

import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.NewSearchUI;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.formatter.IndentManipulation;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchPattern;

import org.eclipse.jdt.internal.corext.util.Messages;

import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jdt.ui.search.PatternQuerySpecification;
import org.eclipse.jdt.ui.search.QuerySpecification;

import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.browsing.LogicalPackage;
import org.eclipse.jdt.internal.ui.dialogs.TextFieldNavigationHandler;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.search.MatchLocations.MatchLocationSelectionDialog;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.internal.ui.util.SWTUtil;

public class X10SearchPage extends DialogPage implements ISearchPage {

	private static class SearchPatternData {
		private final int searchFor;
		private final int limitTo;
		private final String pattern;
		private final boolean isCaseSensitive;
		private final int includeMask;
		//private final int matchLocations;
		private final int scope;
		private final IWorkingSet[] workingSets;
		private IJavaElement javaElement;

		public SearchPatternData(int searchFor, int limitTo, int matchLocations, boolean isCaseSensitive, String pattern, IJavaElement element, int includeMask) {
			this(searchFor, limitTo, matchLocations, pattern, isCaseSensitive, element, ISearchPageContainer.WORKSPACE_SCOPE, null, includeMask);
		}

		public SearchPatternData(int searchFor, int limitTo, int matchLocations, String pattern, boolean isCaseSensitive, IJavaElement element, int scope, IWorkingSet[] workingSets, int includeMask) {
			this.searchFor= searchFor;
			this.limitTo= limitTo;
			//this.matchLocations= matchLocations;
			this.pattern= pattern;
			this.isCaseSensitive= isCaseSensitive;
			this.scope= scope;
			this.workingSets= workingSets;
			this.includeMask= includeMask;

			setJavaElement(element);
		}

		public void setJavaElement(IJavaElement javaElement) {
			this.javaElement= javaElement;
		}

		public boolean isCaseSensitive() {
			return isCaseSensitive;
		}

		public IJavaElement getJavaElement() {
			return javaElement;
		}

		public int getLimitTo() {
			return limitTo;
		}

		public String getPattern() {
			return pattern;
		}

		public int getScope() {
			return scope;
		}

		public int getSearchFor() {
			return searchFor;
		}

		public IWorkingSet[] getWorkingSets() {
			return workingSets;
		}

		public int getIncludeMask() {
			return includeMask;
		}

//		public int getMatchLocations() {
//			return matchLocations;
//		}

		public void store(IDialogSettings settings) {
			settings.put("searchFor", searchFor); //$NON-NLS-1$
			settings.put("scope", scope); //$NON-NLS-1$
			settings.put("pattern", pattern); //$NON-NLS-1$
			settings.put("limitTo", limitTo); //$NON-NLS-1$
			//settings.put("matchLocations", matchLocations); //$NON-NLS-1$
			settings.put("javaElement", javaElement != null ? javaElement.getHandleIdentifier() : ""); //$NON-NLS-1$ //$NON-NLS-2$
			settings.put("isCaseSensitive", isCaseSensitive); //$NON-NLS-1$
			if (workingSets != null) {
				String[] wsIds= new String[workingSets.length];
				for (int i= 0; i < workingSets.length; i++) {
					wsIds[i]= workingSets[i].getName();
				}
				settings.put("workingSets", wsIds); //$NON-NLS-1$
			} else {
				settings.put("workingSets", new String[0]); //$NON-NLS-1$
			}
			settings.put("includeMask", includeMask); //$NON-NLS-1$
		}

		public static SearchPatternData create(IDialogSettings settings) {
			String pattern= settings.get("pattern"); //$NON-NLS-1$
			if (pattern.length() == 0) {
				return null;
			}
			IJavaElement elem= null;
			String handleId= settings.get("javaElement"); //$NON-NLS-1$
			if (handleId != null && handleId.length() > 0) {
				IJavaElement restored= JavaCore.create(handleId);
				if (restored != null && isSearchableType(restored) && restored.exists()) {
					elem= restored;
				}
			}
			String[] wsIds= settings.getArray("workingSets"); //$NON-NLS-1$
			IWorkingSet[] workingSets= null;
			if (wsIds != null && wsIds.length > 0) {
				IWorkingSetManager workingSetManager= PlatformUI.getWorkbench().getWorkingSetManager();
				workingSets= new IWorkingSet[wsIds.length];
				for (int i= 0; workingSets != null && i < wsIds.length; i++) {
					workingSets[i]= workingSetManager.getWorkingSet(wsIds[i]);
					if (workingSets[i] == null) {
						workingSets= null;
					}
				}
			}

			try {
				int searchFor= settings.getInt("searchFor"); //$NON-NLS-1$
				int scope= settings.getInt("scope"); //$NON-NLS-1$
				int limitTo= settings.getInt("limitTo"); //$NON-NLS-1$

				int matchLocations= 0;
				if (settings.get("matchLocations") != null) { //$NON-NLS-1$
					matchLocations= settings.getInt("matchLocations"); //$NON-NLS-1$
				}

				boolean isCaseSensitive= settings.getBoolean("isCaseSensitive"); //$NON-NLS-1$

				int includeMask;
				if (settings.get("includeMask") != null) { //$NON-NLS-1$
					includeMask= settings.getInt("includeMask"); //$NON-NLS-1$
				} else {
					includeMask= X10SearchScopeFactory.NO_JRE;
					if (settings.get("includeJRE") == null ? forceIncludeAll(limitTo, elem) : settings.getBoolean("includeJRE")) {  //$NON-NLS-1$ //$NON-NLS-2$
						includeMask= X10SearchScopeFactory.ALL;
					}
				}
				return new SearchPatternData(searchFor, limitTo, matchLocations, pattern, isCaseSensitive, elem, scope, workingSets, includeMask);
			} catch (NumberFormatException e) {
				return null;
			}
		}

	}

	// search for
	private final static int TYPE= IJavaSearchConstants.TYPE;
	private final static int METHOD= IJavaSearchConstants.METHOD;
	private final static int PACKAGE= IJavaSearchConstants.PACKAGE;
	private final static int CONSTRUCTOR= IJavaSearchConstants.CONSTRUCTOR;
	private final static int FIELD= IJavaSearchConstants.FIELD;

	// limit to
	private final static int DECLARATIONS= IJavaSearchConstants.DECLARATIONS;
	private final static int IMPLEMENTORS= IJavaSearchConstants.IMPLEMENTORS;
	private final static int REFERENCES= IJavaSearchConstants.REFERENCES;
	private final static int SPECIFIC_REFERENCES= IJavaSearchConstants.CAST_TYPE_REFERENCE;
	private final static int ALL_OCCURRENCES= IJavaSearchConstants.ALL_OCCURRENCES;
	private final static int READ_ACCESSES= IJavaSearchConstants.READ_ACCESSES;
	private final static int WRITE_ACCESSES= IJavaSearchConstants.WRITE_ACCESSES;

	public static final String PARTICIPANT_EXTENSION_POINT= "x10dt.search.ui.queryParticipants"; //$NON-NLS-1$

	public static final String EXTENSION_POINT_ID= "x10dt.search.ui.globalSearch.X10SearchPage"; //$NON-NLS-1$

	private static final int HISTORY_SIZE= 12;

	// Dialog store id constants
	private final static String PAGE_NAME= "JavaSearchPage"; //$NON-NLS-1$
	private final static String STORE_CASE_SENSITIVE= "CASE_SENSITIVE"; //$NON-NLS-1$
	private final static String STORE_INCLUDE_MASK= "INCLUDE_MASK"; //$NON-NLS-1$
	private final static String STORE_HISTORY= "HISTORY"; //$NON-NLS-1$
	private final static String STORE_HISTORY_SIZE= "HISTORY_SIZE"; //$NON-NLS-1$

	private final List fPreviousSearchPatterns;

	private SearchPatternData fInitialData;
	private IJavaElement fJavaElement;
	private boolean fFirstTime= true;
	private IDialogSettings fDialogSettings;
	private boolean fIsCaseSensitive;

	private Combo fPattern;
	private ISearchPageContainer fContainer;
	private Button fCaseSensitive;

	private Button[] fSearchFor;
	private Button[] fLimitTo;
	private Button[] fIncludeMasks;
	private Group fLimitToGroup;

	private int fMatchLocations;
	private Link fMatchLocationsLink;

	/**
	 *
	 */
	public X10SearchPage() {
		fPreviousSearchPatterns= new ArrayList();
	}


	//---- Action Handling ------------------------------------------------

	public boolean performAction() {
		return performNewSearch();
	}

	private boolean performNewSearch() {
		SearchPatternData data= getPatternData();

		// Setup search scope
		IJavaSearchScope scope= null;
		String scopeDescription= ""; //$NON-NLS-1$

		int searchFor= data.getSearchFor();
		int limitTo= data.getLimitTo();
		if (limitTo == SPECIFIC_REFERENCES) {
			limitTo= fMatchLocations;
		}

		int includeMask= data.getIncludeMask();
		X10SearchScopeFactory factory= X10SearchScopeFactory.getInstance();

		switch (getContainer().getSelectedScope()) {
			case ISearchPageContainer.WORKSPACE_SCOPE:
				scopeDescription= factory.getWorkspaceScopeDescription(includeMask);
				scope= factory.createWorkspaceScope(includeMask);
				break;
			case ISearchPageContainer.SELECTION_SCOPE:
				IJavaElement[] javaElements= factory.getJavaElements(getContainer().getSelection());
				scope= factory.createJavaSearchScope(javaElements, includeMask);
				scopeDescription= factory.getSelectionScopeDescription(javaElements, includeMask);
				break;
			case ISearchPageContainer.SELECTED_PROJECTS_SCOPE: {
				String[] projectNames= getContainer().getSelectedProjectNames();
				scope= factory.createJavaProjectSearchScope(projectNames, includeMask);
				scopeDescription= factory.getProjectScopeDescription(projectNames, includeMask);
				break;
			}
			case ISearchPageContainer.WORKING_SET_SCOPE: {
				IWorkingSet[] workingSets= getContainer().getSelectedWorkingSets();
				// should not happen - just to be sure
				if (workingSets == null || workingSets.length < 1)
					return false;
				scopeDescription= factory.getWorkingSetScopeDescription(workingSets, includeMask);
				scope= factory.createJavaSearchScope(workingSets, includeMask);
				SearchUtil.updateLRUWorkingSets(workingSets);
			}
		}

		QuerySpecification querySpec= null;
		if (data.getJavaElement() != null && getPattern().equals(fInitialData.getPattern())) {
			if (limitTo == REFERENCES)
				SearchUtil.warnIfBinaryConstant(data.getJavaElement(), getShell());
			querySpec= new ElementQuerySpecification(data.getJavaElement(), limitTo, scope, scopeDescription);
		} else {
			querySpec= new PatternQuerySpecification(data.getPattern(), searchFor, data.isCaseSensitive(), limitTo, scope, scopeDescription);
			data.setJavaElement(null);
		}

		X10SearchQuery textSearchJob= new X10SearchQuery(querySpec);
		NewSearchUI.runQueryInBackground(textSearchJob);
		return true;
	}

	private int getLimitTo() {
		for (int i= 0; i < fLimitTo.length; i++) {
			Button button= fLimitTo[i];
			if (button.getSelection()) {
				return getIntData(button);
			}
		}
		return -1;
	}

	private int setLimitTo(int searchFor, int limitTo) {
		if (searchFor != TYPE && limitTo == IMPLEMENTORS) {
			limitTo= REFERENCES;
		}

		if (searchFor != FIELD && (limitTo == READ_ACCESSES || limitTo == WRITE_ACCESSES)) {
			limitTo= REFERENCES;
		}
		
		if (searchFor != TYPE /*&& searchFor != FIELD*/ && searchFor != METHOD && limitTo == SPECIFIC_REFERENCES) {
			limitTo= REFERENCES;
		}
		fillLimitToGroup(searchFor, limitTo);
		return limitTo;
	}

	private int getIncludeMask() {
		int mask= 0;
		for (int i= 0; i < fIncludeMasks.length; i++) {
			Button button= fIncludeMasks[i];
			if (button.getSelection()) {
				mask |= getIntData(button);
			}
		}
		return mask;
	}

	private void setIncludeMask(int includeMask) {
		for (int i= 0; i < fIncludeMasks.length; i++) {
			Button button= fIncludeMasks[i];
			button.setSelection((includeMask & getIntData(button)) != 0);
		}
	}

//	private void setMatchLocations(int matchLocations) {
//		fMatchLocations= matchLocations;
//		updateMatchLocationText();
//	}

	private String[] getPreviousSearchPatterns() {
		// Search results are not persistent
		int patternCount= fPreviousSearchPatterns.size();
		String [] patterns= new String[patternCount];
		for (int i= 0; i < patternCount; i++)
			patterns[i]= ((SearchPatternData) fPreviousSearchPatterns.get(i)).getPattern();
		return patterns;
	}

	private int getSearchFor() {
		for (int i= 0; i < fSearchFor.length; i++) {
			Button button= fSearchFor[i];
			if (button.getSelection()) {
				return getIntData(button);
			}
		}
		Assert.isTrue(false, "shouldNeverHappen"); //$NON-NLS-1$
		return -1;
	}

	private void setSearchFor(int searchFor) {
		for (int i= 0; i < fSearchFor.length; i++) {
			Button button= fSearchFor[i];
			button.setSelection(searchFor == getIntData(button));
		}
	}

	private int getIntData(Button button) {
		return ((Integer) button.getData()).intValue();
	}

	private String getPattern() {
		return fPattern.getText();
	}


	private SearchPatternData findInPrevious(String pattern) {
		for (Iterator iter= fPreviousSearchPatterns.iterator(); iter.hasNext();) {
			SearchPatternData element= (SearchPatternData) iter.next();
			if (pattern.equals(element.getPattern())) {
				return element;
			}
		}
		return null;
	}

	/**
	 * Return search pattern data and update previous searches.
	 * An existing entry will be updated.
	 * @return the pattern data
	 */
	private SearchPatternData getPatternData() {
		String pattern= getPattern();
		SearchPatternData match= findInPrevious(pattern);
		if (match != null) {
			fPreviousSearchPatterns.remove(match);
		}
		match= new SearchPatternData(
				getSearchFor(),
				getLimitTo(),
				fMatchLocations,
				pattern,
				fCaseSensitive.getSelection(),
				fJavaElement,
				getContainer().getSelectedScope(),
				getContainer().getSelectedWorkingSets(),
				getIncludeMask()
		);

		fPreviousSearchPatterns.add(0, match); // insert on top
		return match;
	}

	/*
	 * Implements method from IDialogPage
	 */
	public void setVisible(boolean visible) {
		if (visible && fPattern != null) {
			if (fFirstTime) {
				fFirstTime= false;
				// Set item and text here to prevent page from resizing
				fPattern.setItems(getPreviousSearchPatterns());
				initSelections();
			}
			fPattern.setFocus();
		}
		updateOKStatus();
		super.setVisible(visible);
	}

	public boolean isValid() {
		return true;
	}

	//---- Widget creation ------------------------------------------------


	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		readConfiguration();

		Composite result= new Composite(parent, SWT.NONE);

		GridLayout layout= new GridLayout(2, true);
		layout.horizontalSpacing= 10;
		result.setLayout(layout);

		Control expressionComposite= createExpression(result);
		expressionComposite.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label separator= new Label(result, SWT.NONE);
		separator.setVisible(false);
		GridData data= new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1);
		data.heightHint= convertHeightInCharsToPixels(1) / 3;
		separator.setLayoutData(data);

		Control searchFor= createSearchFor(result);
		searchFor.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));

		Control limitTo= createLimitTo(result);
		limitTo.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));

		Control includeMask= createIncludeMask(result);
		includeMask.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));

		//createParticipants(result);

		SelectionAdapter javaElementInitializer= new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (getSearchFor() == fInitialData.getSearchFor())
					fJavaElement= fInitialData.getJavaElement();
				else
					fJavaElement= null;
				setLimitTo(getSearchFor(), getLimitTo());
				setIncludeMask(getIncludeMask());
				doPatternModified();
			}
		};

		for (int i= 0; i < fSearchFor.length; i++) {
			fSearchFor[i].addSelectionListener(javaElementInitializer);
		}

		setControl(result);

		Dialog.applyDialogFont(result);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(result, IJavaHelpContextIds.JAVA_SEARCH_PAGE);
	}


	/*private Control createParticipants(Composite result) {
		if (!SearchParticipantsExtensionPoint.hasAnyParticipants())
			return new Composite(result, SWT.NULL);
		Button selectParticipants= new Button(result, SWT.PUSH);
		selectParticipants.setText(SearchMessages.getString("SearchPage.select_participants.label")); //$NON-NLS-1$
		GridData gd= new GridData();
		gd.verticalAlignment= GridData.VERTICAL_ALIGN_BEGINNING;
		gd.horizontalAlignment= GridData.HORIZONTAL_ALIGN_END;
		gd.grabExcessHorizontalSpace= false;
		gd.horizontalAlignment= GridData.END;
		gd.horizontalSpan= 2;
		selectParticipants.setLayoutData(gd);
		selectParticipants.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PreferencePageSupport.showPreferencePage(getShell(), "org.eclipse.jdt.ui.preferences.SearchParticipantsExtensionPoint", new SearchParticipantsExtensionPoint()); //$NON-NLS-1$
			}

		});
		return selectParticipants;
	}*/


	private Control createExpression(Composite parent) {
		Composite result= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout(2, false);
		layout.marginWidth= 0;
		layout.marginHeight= 0;
		result.setLayout(layout);

		// Pattern text + info
		Label label= new Label(result, SWT.LEFT);
		label.setText(SearchMessages.SearchPage_expression_label);
		label.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1));

		// Pattern combo
		fPattern= new Combo(result, SWT.SINGLE | SWT.BORDER);
		SWTUtil.setDefaultVisibleItemCount(fPattern);
		fPattern.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handlePatternSelected();
				updateOKStatus();
			}
		});
		fPattern.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				doPatternModified();
				updateOKStatus();

			}
		});
		TextFieldNavigationHandler.install(fPattern);
		GridData data= new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1);
		data.widthHint= convertWidthInCharsToPixels(50);
		fPattern.setLayoutData(data);

		// Ignore case checkbox
		fCaseSensitive= new Button(result, SWT.CHECK);
		fCaseSensitive.setText(SearchMessages.SearchPage_expression_caseSensitive);
		fCaseSensitive.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fIsCaseSensitive= fCaseSensitive.getSelection();
			}
		});
		fCaseSensitive.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));

		return result;
	}

	final void updateOKStatus() {
		boolean isValidPattern= isValidSearchPattern();
		boolean isValidMask= getIncludeMask() != 0;
		getContainer().setPerformActionEnabled(isValidPattern && isValidMask);
	}

	private boolean isValidSearchPattern() {
		if (getPattern().length() == 0) {
			return false;
		}
		if (fJavaElement != null) {
			return true;
		}
		return SearchPattern.createPattern(getPattern(), getSearchFor(), getLimitTo(), SearchPattern.R_EXACT_MATCH) != null;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#dispose()
	 */
	public void dispose() {
		writeConfiguration();
		super.dispose();
	}

	private void doPatternModified() {
		if (fInitialData != null && getPattern().equals(fInitialData.getPattern()) && fInitialData.getJavaElement() != null && fInitialData.getSearchFor() == getSearchFor()) {
			fCaseSensitive.setEnabled(false);
			fCaseSensitive.setSelection(true);
			fJavaElement= fInitialData.getJavaElement();
		} else {
			fCaseSensitive.setEnabled(true);
			fCaseSensitive.setSelection(fIsCaseSensitive);
			fJavaElement= null;
		}
	}

	private void handlePatternSelected() {
		int selectionIndex= fPattern.getSelectionIndex();
		if (selectionIndex < 0 || selectionIndex >= fPreviousSearchPatterns.size())
			return;

		SearchPatternData initialData= (SearchPatternData) fPreviousSearchPatterns.get(selectionIndex);

		setSearchFor(initialData.getSearchFor());
		setLimitTo(initialData.getSearchFor(), initialData.getLimitTo());
		setIncludeMask(initialData.getIncludeMask());
		//setMatchLocations(initialData.getMatchLocations());

		fPattern.setText(initialData.getPattern());
		fIsCaseSensitive= initialData.isCaseSensitive();
		fJavaElement= initialData.getJavaElement();
		fCaseSensitive.setEnabled(fJavaElement == null);
		fCaseSensitive.setSelection(initialData.isCaseSensitive());


		if (initialData.getWorkingSets() != null)
			getContainer().setSelectedWorkingSets(initialData.getWorkingSets());
		else
			getContainer().setSelectedScope(initialData.getScope());

		fInitialData= initialData;
	}


	private Control createSearchFor(Composite parent) {
		Group result= new Group(parent, SWT.NONE);
		result.setText(SearchMessages.SearchPage_searchFor_label);
		result.setLayout(new GridLayout(2, true));

		fSearchFor= new Button[] {
			createButton(result, SWT.RADIO, SearchMessages.SearchPage_searchFor_type, TYPE, true),
			createButton(result, SWT.RADIO, SearchMessages.SearchPage_searchFor_method, METHOD, false),
			createButton(result, SWT.RADIO, SearchMessages.SearchPage_searchFor_package, PACKAGE, false),
			createButton(result, SWT.RADIO, SearchMessages.SearchPage_searchFor_constructor, CONSTRUCTOR, false),
			createButton(result, SWT.RADIO, SearchMessages.SearchPage_searchFor_field, FIELD, false)
		};

		// Fill with dummy radio buttons
		Label filler= new Label(result, SWT.NONE);
		filler.setVisible(false);
		filler.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

		return result;
	}

	private Control createLimitTo(Composite parent) {
		fLimitToGroup= new Group(parent, SWT.NONE);
		fLimitToGroup.setText(SearchMessages.SearchPage_limitTo_label);
		fLimitToGroup.setLayout(new GridLayout(2, false));

		fillLimitToGroup(TYPE, ALL_OCCURRENCES);

		return fLimitToGroup;
	}

	private void fillLimitToGroup(int searchFor, int limitTo) {
		Control[] children= fLimitToGroup.getChildren();
		for (int i= 0; i < children.length; i++) {
			children[i].dispose();
		}
		fMatchLocationsLink= null;


		ArrayList buttons= new ArrayList();
		buttons.add(createButton(fLimitToGroup, SWT.RADIO, SearchMessages.SearchPage_limitTo_allOccurrences, ALL_OCCURRENCES, limitTo == ALL_OCCURRENCES));
		buttons.add(createButton(fLimitToGroup, SWT.RADIO, SearchMessages.SearchPage_limitTo_declarations, DECLARATIONS, limitTo == DECLARATIONS));

		buttons.add(createButton(fLimitToGroup, SWT.RADIO, SearchMessages.SearchPage_limitTo_references, REFERENCES, limitTo == REFERENCES));
		if (searchFor == TYPE) {
			buttons.add(createButton(fLimitToGroup, SWT.RADIO, SearchMessages.SearchPage_limitTo_implementors, IMPLEMENTORS, limitTo == IMPLEMENTORS));

			//buttons.add(createMethodLocationRadio(limitTo == SPECIFIC_REFERENCES));
		}

		if (searchFor == FIELD) {
			buttons.add(createButton(fLimitToGroup, SWT.RADIO, SearchMessages.SearchPage_limitTo_readReferences, READ_ACCESSES, limitTo == READ_ACCESSES));
			buttons.add(createButton(fLimitToGroup, SWT.RADIO, SearchMessages.SearchPage_limitTo_writeReferences, WRITE_ACCESSES, limitTo == WRITE_ACCESSES));
			
//			buttons.add(createMethodLocationRadio(limitTo == SPECIFIC_REFERENCES));
		}

		if (searchFor == METHOD) {
			//buttons.add(createMethodLocationRadio(limitTo == SPECIFIC_REFERENCES));
		}

		fLimitTo= (Button[]) buttons.toArray(new Button[buttons.size()]);

		SelectionAdapter listener= new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				performLimitToSelectionChanged((Button) e.widget);
			}
		};
		for (int i= 0; i < fLimitTo.length; i++) {
			fLimitTo[i].addSelectionListener(listener);
		}
		Dialog.applyDialogFont(fLimitToGroup); // re-apply font as we disposed the previous widgets

		fLimitToGroup.layout();
//		// searchFor == FIELD needs one more row than the others
//		fLimitToGroup.getShell().layout(new Control[] { fLimitToGroup.getChildren()[0] });
	}


//	private Button createMethodLocationRadio(boolean isSelected) {
//		Composite specificComposite= new Composite(fLimitToGroup, SWT.NONE);
//		specificComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
//		GridLayout layout= new GridLayout(2, false);
//		layout.marginWidth= 0;
//		layout.marginHeight= 0;
//		layout.horizontalSpacing= 0;
//		specificComposite.setLayout(layout);
//
//		Button button= createButton(specificComposite, SWT.RADIO, SearchMessages.JavaSearchPage_match_locations_label, SPECIFIC_REFERENCES, isSelected);
//		fMatchLocationsLink= new Link(specificComposite, SWT.NONE);
//		fMatchLocationsLink.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
//		fMatchLocationsLink.addSelectionListener(new SelectionAdapter() {
//			public void widgetDefaultSelected(SelectionEvent e) {
//				performConfigureMatchLocation();
//			}
//			public void widgetSelected(SelectionEvent e) {
//				performConfigureMatchLocation();
//			}
//		});
//		updateMatchLocationText();
//
//		return button;
//	}

//	private void updateMatchLocationText() {
//		if (fMatchLocationsLink != null) {
//			int searchFor= getSearchFor();
//			int totNum= MatchLocations.getTotalNumberOfSettings(searchFor);
//			int currNum= MatchLocations.getNumberOfSelectedSettings(fMatchLocations, searchFor);
//
//			fMatchLocationsLink.setText(Messages.format(SearchMessages.JavaSearchPage_match_location_link_label, new Object[] { new Integer(currNum), new Integer(totNum) }));
//			fMatchLocationsLink.setToolTipText(SearchMessages.JavaSearchPage_match_location_link_label_tooltip);
//		}
//	}

	protected final void performLimitToSelectionChanged(Button button) {
		if (button.getSelection()) {
			for (int i= 0; i < fLimitTo.length; i++) {
				Button curr= fLimitTo[i];
				if (curr != button) {
					curr.setSelection(false);
				}
			}
		}
		updateUseJRE();
	}


	protected final void performConfigureMatchLocation() {
		for (int i= 0; i < fLimitTo.length; i++) {
			Button curr= fLimitTo[i];
			curr.setSelection(getIntData(curr) == SPECIFIC_REFERENCES);
		}

		MatchLocationSelectionDialog locationSelectionDialog= new MatchLocationSelectionDialog(getShell(), fMatchLocations, getSearchFor());
		if (locationSelectionDialog.open() == Window.OK) {
			//setMatchLocations(locationSelectionDialog.getCurrentSelection());
		}
	}


	private Control createIncludeMask(Composite parent) {
		Group result= new Group(parent, SWT.NONE);
		result.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		result.setText(SearchMessages.SearchPage_searchIn_label);
		result.setLayout(new GridLayout(4, false));
		fIncludeMasks= new Button[] {
			createButton(result, SWT.CHECK, SearchMessages.SearchPage_searchIn_sources, X10SearchScopeFactory.SOURCES, true),
			createButton(result, SWT.CHECK, SearchMessages.SearchPage_searchIn_projects, X10SearchScopeFactory.PROJECTS, true),
			createButton(result, SWT.CHECK, SearchMessages.SearchPage_searchIn_jre, X10SearchScopeFactory.JRE, false),
			createButton(result, SWT.CHECK, SearchMessages.SearchPage_searchIn_libraries, X10SearchScopeFactory.LIBS, true),
		};

		SelectionAdapter listener= new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateOKStatus();
			}
		};
		for (int i= 0; i < fIncludeMasks.length; i++) {
			fIncludeMasks[i].addSelectionListener(listener);
		}

		return result;
	}

	private Button createButton(Composite parent, int style, String text, int data, boolean isSelected) {
		Button button= new Button(parent, style);
		button.setText(text);
		button.setData(new Integer(data));
		button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		button.setSelection(isSelected);
		return button;
	}

	private void initSelections() {
		ISelection sel= getContainer().getSelection();

		IWorkbenchPage activePage= JavaPlugin.getActivePage();
		if (activePage != null) {
			IWorkbenchPart activePart= activePage.getActivePart();
			if (activePart instanceof JavaEditor) {
				JavaEditor javaEditor= (JavaEditor) activePart;
				if (javaEditor.isBreadcrumbActive()) {
					sel= javaEditor.getBreadcrumb().getSelectionProvider().getSelection();
				}
			}
		}

		SearchPatternData initData= null;

		if (sel instanceof IStructuredSelection) {
			initData= tryStructuredSelection((IStructuredSelection) sel);
		} else if (sel instanceof ITextSelection) {
			IEditorPart activeEditor= getActiveEditor();
			if (activeEditor instanceof JavaEditor) {
				try {
					IJavaElement[] elements= SelectionConverter.codeResolve((JavaEditor) activeEditor);
					if (elements != null && elements.length > 0) {
						initData= determineInitValuesFrom(elements[0]);
					}
				} catch (JavaModelException e) {
					// ignore
				}
			}
			if (initData == null) {
				initData= trySimpleTextSelection((ITextSelection) sel);
			}
		}
		if (initData == null) {
			initData= getDefaultInitValues();
		}

		fInitialData= initData;
		fJavaElement= initData.getJavaElement();
		fCaseSensitive.setSelection(initData.isCaseSensitive());
		fCaseSensitive.setEnabled(fJavaElement == null);

		setSearchFor(initData.getSearchFor());
		setLimitTo(initData.getSearchFor(), initData.getLimitTo());
		setIncludeMask(initData.getIncludeMask());
		//setMatchLocations(initData.getMatchLocations());

		fPattern.setText(initData.getPattern());
	}

	private void updateUseJRE() {
		setIncludeMask(getIncludeMask());
	}

	private static boolean forceIncludeAll(int limitTo, IJavaElement elem) {
		return elem != null && (limitTo == DECLARATIONS || limitTo == IMPLEMENTORS);
	}

	private SearchPatternData tryStructuredSelection(IStructuredSelection selection) {
		if (selection == null || selection.size() > 1)
			return null;

		Object o= selection.getFirstElement();
		SearchPatternData res= null;
		if (o instanceof IJavaElement) {
			res= determineInitValuesFrom((IJavaElement) o);
		} else if (o instanceof LogicalPackage) {
			LogicalPackage lp= (LogicalPackage)o;
			return new SearchPatternData(PACKAGE, REFERENCES, 0, fIsCaseSensitive, lp.getElementName(), null, getLastIncludeMask());
		} else if (o instanceof IAdaptable) {
			IJavaElement element= (IJavaElement) ((IAdaptable) o).getAdapter(IJavaElement.class);
			if (element != null) {
				res= determineInitValuesFrom(element);
			}
		}
		if (res == null && o instanceof IAdaptable) {
			IWorkbenchAdapter adapter= (IWorkbenchAdapter)((IAdaptable)o).getAdapter(IWorkbenchAdapter.class);
			if (adapter != null) {
				return new SearchPatternData(TYPE, REFERENCES, 0, fIsCaseSensitive, adapter.getLabel(o), null, getLastIncludeMask());
			}
		}
		return res;
	}

	final static boolean isSearchableType(IJavaElement element) {
		switch (element.getElementType()) {
			case IJavaElement.PACKAGE_FRAGMENT:
			case IJavaElement.PACKAGE_DECLARATION:
			case IJavaElement.IMPORT_DECLARATION:
			case IJavaElement.TYPE:
			case IJavaElement.FIELD:
			case IJavaElement.METHOD:
				return true;
		}
		return false;
	}

	private SearchPatternData determineInitValuesFrom(IJavaElement element) {
		try {
			//JavaSearchScopeFactory factory= JavaSearchScopeFactory.getInstance();
			//boolean isInsideJRE= factory.isInsideJRE(element);
			int includeMask= getLastIncludeMask();

			switch (element.getElementType()) {
				case IJavaElement.PACKAGE_FRAGMENT:
				case IJavaElement.PACKAGE_DECLARATION:
					return new SearchPatternData(PACKAGE, REFERENCES, 0, true, element.getElementName(), element, includeMask);
				case IJavaElement.IMPORT_DECLARATION: {
					IImportDeclaration declaration= (IImportDeclaration) element;
					if (declaration.isOnDemand()) {
						String name= Signature.getQualifier(declaration.getElementName());
						return new SearchPatternData(PACKAGE, DECLARATIONS, 0, true, name, element, X10SearchScopeFactory.ALL);
					}
					return new SearchPatternData(TYPE, DECLARATIONS, 0, true, element.getElementName(), element, X10SearchScopeFactory.ALL);
				}
				case IJavaElement.TYPE:
					return new SearchPatternData(TYPE, REFERENCES, 0, true, PatternStrings.getTypeSignature((IType) element), element, includeMask);
				case IJavaElement.COMPILATION_UNIT: {
					IType mainType= ((ICompilationUnit) element).findPrimaryType();
					if (mainType != null) {
						return new SearchPatternData(TYPE, REFERENCES, 0, true, PatternStrings.getTypeSignature(mainType), mainType, includeMask);
					}
					break;
				}
				case IJavaElement.CLASS_FILE: {
					IType mainType= ((IClassFile) element).getType();
					if (mainType.exists()) {
						return new SearchPatternData(TYPE, REFERENCES, 0, true, PatternStrings.getTypeSignature(mainType), mainType, includeMask);
					}
					break;
				}
				case IJavaElement.FIELD:
					return new SearchPatternData(FIELD, REFERENCES, 0, true, PatternStrings.getFieldSignature((IField) element), element, includeMask);
				case IJavaElement.METHOD:
					IMethod method= (IMethod) element;
					int searchFor= method.isConstructor() ? CONSTRUCTOR : METHOD;
					return new SearchPatternData(searchFor, REFERENCES, 0, true, PatternStrings.getMethodSignature(method), element, includeMask);
			}

		} catch (JavaModelException e) {
			if (!e.isDoesNotExist()) {
				ExceptionHandler.handle(e, SearchMessages.Search_Error_javaElementAccess_title, SearchMessages.Search_Error_javaElementAccess_message);
			}
			// element might not exist
		}
		return null;
	}

	private SearchPatternData trySimpleTextSelection(ITextSelection selection) {
		String selectedText= selection.getText();
		if (selectedText != null && selectedText.length() > 0) {
			int i= 0;
			while (i < selectedText.length() && !IndentManipulation.isLineDelimiterChar(selectedText.charAt(i))) {
				i++;
			}
			if (i > 0) {
				return new SearchPatternData(TYPE, REFERENCES, 0, fIsCaseSensitive, selectedText.substring(0, i), null, X10SearchScopeFactory.ALL);
			}
		}
		return null;
	}

	private SearchPatternData getDefaultInitValues() {
		if (!fPreviousSearchPatterns.isEmpty()) {
			return (SearchPatternData) fPreviousSearchPatterns.get(0);
		}

		return new SearchPatternData(TYPE, REFERENCES, 0, fIsCaseSensitive, "", null, getLastIncludeMask()); //$NON-NLS-1$
	}

	private int getLastIncludeMask() {
		try {
			return getDialogSettings().getInt(STORE_INCLUDE_MASK);
		} catch (NumberFormatException e) {
			return X10SearchScopeFactory.NO_JRE;
		}
	}

	/*
	 * Implements method from ISearchPage
	 */
	public void setContainer(ISearchPageContainer container) {
		fContainer= container;
	}

	/**
	 * Returns the search page's container.
	 * @return the search page container
	 */
	private ISearchPageContainer getContainer() {
		return fContainer;
	}

	private IEditorPart getActiveEditor() {
		IWorkbenchPage activePage= JavaPlugin.getActivePage();
		if (activePage != null) {
			return activePage.getActiveEditor();
		}
		return null;
	}

	//--------------- Configuration handling --------------

	/**
	 * Returns the page settings for this Java search page.
	 *
	 * @return the page settings to be used
	 */
	private IDialogSettings getDialogSettings() {
		if (fDialogSettings == null) {
			fDialogSettings= JavaPlugin.getDefault().getDialogSettingsSection(PAGE_NAME);
		}
		return fDialogSettings;
	}

	/**
	 * Initializes itself from the stored page settings.
	 */
	private void readConfiguration() {
		IDialogSettings s= getDialogSettings();
		fIsCaseSensitive= s.getBoolean(STORE_CASE_SENSITIVE);

		try {
			int historySize= s.getInt(STORE_HISTORY_SIZE);
			for (int i= 0; i < historySize; i++) {
				IDialogSettings histSettings= s.getSection(STORE_HISTORY + i);
				if (histSettings != null) {
					SearchPatternData data= SearchPatternData.create(histSettings);
					if (data != null) {
						fPreviousSearchPatterns.add(data);
					}
				}
			}
		} catch (NumberFormatException e) {
			// ignore
		}
	}

	/**
	 * Stores the current configuration in the dialog store.
	 */
	private void writeConfiguration() {
		IDialogSettings s= getDialogSettings();
		s.put(STORE_CASE_SENSITIVE, fIsCaseSensitive);
		s.put(STORE_INCLUDE_MASK, getIncludeMask());

		int historySize= Math.min(fPreviousSearchPatterns.size(), HISTORY_SIZE);
		s.put(STORE_HISTORY_SIZE, historySize);
		for (int i= 0; i < historySize; i++) {
			IDialogSettings histSettings= s.addNewSection(STORE_HISTORY + i);
			SearchPatternData data= ((SearchPatternData) fPreviousSearchPatterns.get(i));
			data.store(histSettings);
		}
	}
}
