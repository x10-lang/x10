/*******************************************************************************
* Copyright (c) 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

*******************************************************************************/

package org.eclipse.imp.x10dt.core.preferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.Markings;
import org.eclipse.imp.preferences.PreferencesInitializer;
import org.eclipse.imp.preferences.TabbedPreferencesPage;
import org.eclipse.imp.preferences.PreferencesService;
import org.eclipse.imp.preferences.fields.BooleanFieldEditor;
import org.eclipse.imp.preferences.fields.ComboFieldEditor;
import org.eclipse.imp.preferences.fields.RadioGroupFieldEditor;
import org.eclipse.imp.ui.dialogs.ListSelectionDialog;
import org.eclipse.imp.ui.dialogs.providers.ContentProviderForAllProjects;
import org.eclipse.imp.ui.dialogs.providers.LabelProviderForProjects;
import org.eclipse.imp.x10dt.core.preferences.fields.StringFieldEditor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.osgi.service.prefs.Preferences;


public class ProjectPreferencesTab extends PreferencesTab {
	
	protected org.eclipse.jface.preference.StringFieldEditor selectedProjectName = null;
	protected List detailsLinks = new ArrayList();

	protected IJavaProject javaProject = null;
	
	// SMS 14 Nov 2007
	protected IProject fProject = null;

	
	public ProjectPreferencesTab(IPreferencesService prefService) {
		this.fPrefService = prefService;
		fPrefUtils_x10 = new PreferencesUtilities(prefService);
	}

	public Composite createProjectPreferencesTab(TabbedPreferencesPage page, final TabFolder tabFolder) {
		
		fPrefPage = page;

		/*
		 * Prepare the body of the tab
		 */
	
		GridLayout layout = null;
		
		final Composite composite= new Composite(tabFolder, SWT.NONE);
	        composite.setFont(tabFolder.getFont());
	        final GridData gd= new GridData(SWT.FILL, SWT.CENTER, true, false);
	        gd.widthHint= 0;
	        gd.heightHint= SWT.DEFAULT;
	        gd.horizontalSpan= 1;
	        composite.setLayoutData(gd);
		
		layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		
		// The "tab" on the tab folder
		fTabItem = new TabItem(tabFolder, SWT.NONE);	
		fTabItem.setText("Project");
		fTabItem.setControl(composite);	
		PreferencesTab.TabSelectionListener listener = 
			new PreferencesTab.TabSelectionListener(fPrefPage, fTabItem);
		tabFolder.addSelectionListener(listener);
		
		/*
		 * Add the elements relating to preferences fields and their associated "details" links.
		 */	
		fFields = createFields(page, this, IPreferencesService.PROJECT_LEVEL, composite);


		// Clear some space
		PreferencesUtilities.fillGridPlace(composite, 2);

		
		// Disable the details links since no project is selected at the start	
		for (int i = 0; i < detailsLinks.size(); i++) {
			((Link)detailsLinks.get(i)).setEnabled(false);
		}	
		
		PreferencesUtilities.fillGridPlace(composite, 2);

		// Being newly loaded, the fields may be displayed with some
		// indication that they have been modified.  This should reset
		// that marking.
		clearModifiedMarksOnLabels();
		
		
		/*
		 * Put in the elements related to selecting a project
		 */
					
		// To hold the text selection (label + field) and button
		Group groupHolder = new Group(composite, SWT.SHADOW_ETCHED_IN);
		groupHolder.setText("Project selection");
		groupHolder.setLayout(new GridLayout(2, false));
		groupHolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		
		// To hold the text selection label + field
		Composite projectFieldHolder = new Composite(groupHolder, SWT.NONE);
		//layout = new GridLayout();
		//projectFieldHolder.setLayout(layout);
		projectFieldHolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		
		selectedProjectName = 
			new org.eclipse.jface.preference.StringFieldEditor("SelectedProjectName", "Selected project:  ", projectFieldHolder);
		selectedProjectName.setStringValue("none selected");
		// Clear these here in case there are any saved from a previous interaction with the page
		// (assuming that we should start each  new page with no project selected)
		fPrefService.clearPreferencesAtLevel(IPreferencesService.PROJECT_LEVEL);
		// Set the project name field to be non-editable
		selectedProjectName.getTextControl(projectFieldHolder).setEditable(false);
		// Set the attribute fields to be non-editable, since without a project selected
		// it makes no sense for them to be able to take values

		createSelectProjectButton(groupHolder, composite, "Select Project");
		addProjectSelectionListener(projectFieldHolder);
				
		PreferencesUtilities.fillGridPlace(composite, 3);
		
		/*
		 * Put explanatory notes toward the bottom
		 * (not sure whether WRAP is helpful here; can manually
		 * wrap text in labels with '\n')
		 */
		
		final Composite bottom = new Composite(composite, SWT.BOTTOM | SWT.WRAP);
        layout = new GridLayout();
        bottom.setLayout(layout);
        bottom.setLayoutData(new GridData(SWT.BOTTOM));
        
        Label bar = new Label(bottom, SWT.WRAP);
        GridData data = new GridData();
        data.verticalAlignment = SWT.WRAP;
        bar.setLayoutData(data);
        bar.setText("Preferences are shown here only when a project is selected.\n\n" +
        			"Preferences shown with a white background are set on this level.\n\n" +
        			"Preferences shown with a colored background are inherited from a\nhigher level.\n\n" +
        			Markings.MODIFIED_NOTE + "\n\n" +
        			Markings.TAB_ERROR_NOTE);
        
		PreferencesUtilities.fillGridPlace(bottom, 1);

		/*
		 * Put Restore Defaults and Apply buttons at the very bottom,
		 * disabled if (as expected) there is no project selected and
		 * the tab is otherwise mainly disabled
		 */
        fButtons = fPrefUtils_x10.createDefaultAndApplyButtons(composite, this);
        if (fPrefService.getProject() == null) {
        	for (int i = 0; i < fButtons.length; i++) 
        		fButtons[i].setEnabled(false);
        }
		return composite;
	}

	
	private void addProjectSelectionListener(Composite composite)
	{
		fPrefService.addProjectSelectionListener(new ProjectSelectionListener(composite));
	}
	


	private class ProjectSelectionListener implements PreferencesService.IProjectSelectionListener
	{
		Composite composite = null;
		IEclipsePreferences.IPreferenceChangeListener currentListener = null;

		ProjectSelectionListener(Composite composite) {
			this.composite = composite;
		}
			
		/**
		 * Notification that a project was selected for inclusion in the preferences hierarchy.
		 * The given event must not be <code>null</code>.
		 * 
		 * @param event an event specifying the details about the new node
		 * @see IEclipsePreferences.NodeChangeEvent
		 * @see IEclipsePreferences#addNodeChangeListener(IEclipsePreferences.INodeChangeListener)
		 * @see IEclipsePreferences#removeNodeChangeListener(IEclipsePreferences.INodeChangeListener)
		 */
		public void selection(IPreferencesService.ProjectSelectionEvent event) {
			addressProjectSelection(event, composite);
		}
	}
	

	protected List  currentListeners = new ArrayList();
	protected List 	currentListenerNodes = new ArrayList();
	
	
	protected void addressProjectSelection(IPreferencesService.ProjectSelectionEvent event, Composite composite) {
		// TODO:  Override in subtype with a real implementation
		System.err.println("ProjectPreferencesTab.addressProjectSelection(..):  unimplemented");

		// SMS 20 Jun 2007
		// Adding code from JikesPG project tab implementation to try
		// to flesh out a skeleton (field-independent) implementaiton)
		// just to see what there may be of that
		boolean haveCurrentListeners = false;
		
		Preferences oldeNode = event.getPrevious();
		Preferences newNode = event.getNew();
		
		if (oldeNode == null && newNode == null) {
			// This is what happens for some reason when you clear the project selection.
			// Nothing, really, to do in this case ...
			return;
		}

		// If oldeNode is not null, we want to remove any preference-change listeners from it
		if (oldeNode != null && oldeNode instanceof IEclipsePreferences && haveCurrentListeners) {
			removeProjectPreferenceChangeListeners();
			haveCurrentListeners = false;
		} else {	
			// Print an advisory message if you want to
		}

		
		// Declare a "holder" for each preference field; not strictly necessary
		// but helpful in various manipulations of fields and controls to follow
		//Composite field1Holder = null;
		//Composite field2Holder = null;
		//...

		
		// If we have a new project preferences node, then do various things
		// to set up the project's preferences
		if (newNode != null && newNode instanceof IEclipsePreferences) {
			// Set project name in the selected-project field
			selectedProjectName.setStringValue(newNode.name());
			
			// If the containing composite is not disposed, then set the fields' values
			// and make them enabled and editable (as appropriate to the type of field)
			
			if (!composite.isDisposed()) {		
				// Note:  Where there are toggles between fields, it is a good idea to set the
				// properties of the dependent field here according to the values they should have
				// based on the independent field.  There should be listeners to take care of 
				// that sort of adjustment once the tab is established, but when properties are
				// first initialized here, the properties may not always be set correctly through
				// the toggle.  I'm not entirely sure why that happens, except that there may be
				// a race condition between the setting of the dependent values by the listener
				// and the setting of those values here.  If the values are set by the listener
				// first (which might be surprising, but may be possible) then they will be
				// overwritten by values set here--so the values set here should be consistent
				// with what the listener would set.
				
				// Used in setting enabled and editable status
				boolean enabledState = false;
				
				// Example:
//				// Pretend field1 is a boolean field (checkbox)
//				field1Holder = field1.getChangeControl().getParent();
//				prefUtils.setField(field1, field1Holder);
//				field1.getChangeControl().setEnabled(true);
//				
//				// Pretend field2 is a text-based field
//				field2Holder = field2.getTextControl().getParent();
//				prefUtils.setField(field2, field2Holder);
//				// field2 enabled iff field1 not checked
//				enabledState = !field1.getBooleanValue();
//				field2.getTextControl().setEditable(enabledState);
//				field2.getTextControl().setEnabled(enabledState);
//				field2.setEnabled(enabledState, field2.getParent());
	
				// And so on for other fields
				
				clearModifiedMarksOnLabels();
				
			}
			

			// Add property change listeners
			// Example
//			if (field1Holder != null) addProjectPreferenceChangeListeners(field1, PreferenceConstants.P_FIELD_1, field1Holder);
//			if (field2Holder != null) addProjectPreferenceChangeListeners(fieldw, PreferenceConstants.P_FIELD_2, field2Holder);
			// And so on for other fields ...

			haveCurrentListeners = true;
		}
		
		// Or if we don't have a new project preferences node ...
		if (newNode == null || !(newNode instanceof IEclipsePreferences)) {
			// May happen when the preferences page is first brought up, or
			// if we allow the project to be deselected
			
			// Unset project name in the tab
			selectedProjectName.setStringValue("none selected");
			
			// Clear the preferences from the store
			fPrefService.clearPreferencesAtLevel(IPreferencesService.PROJECT_LEVEL);
			
			// Disable fields and make them non-editable
			if (!composite.isDisposed()) {
				// Example:
//				field1.getChangeControl().setEnabled(false);
//
//				field2.getTextControl(field2Holder).setEnabled(false);
//				field2.getTextControl(field2Holder).setEditable(false);
			}
			
			// Remove listeners
			removeProjectPreferenceChangeListeners();
			haveCurrentListeners = false;
		}
	}
	

	
	protected void addProjectPreferenceChangeListeners(BooleanFieldEditor field, String key, Composite composite)
	{
		IEclipsePreferences[] nodes = fPrefService.getNodesForLevels();
		for (int i = IPreferencesService.PROJECT_INDEX; i < nodes.length; i++) {
			if (nodes[i] != null) {
				PreferencesUtilities.BooleanPreferenceChangeListener listener = 
					fPrefUtils_x10.new BooleanPreferenceChangeListener(field, key, composite);
				nodes[i].addPreferenceChangeListener(listener);
				currentListeners.add(listener);
				currentListenerNodes.add(nodes[i]);
			} else {
				//System.err.println("ProjectPreferencesTab.addPropetyChangeListeners(..):  no listener added at level = " + i + "; node at that level is null");
			}
		}	
	}
	
	
	protected void addProjectPreferenceChangeListeners(ComboFieldEditor field, String key, Composite composite)
	{
		IEclipsePreferences[] nodes = fPrefService.getNodesForLevels();
		for (int i = IPreferencesService.PROJECT_INDEX; i < nodes.length; i++) {
			if (nodes[i] != null) {
				PreferencesUtilities.ComboPreferenceChangeListener listener = 
					fPrefUtils_x10.new ComboPreferenceChangeListener(field, key, composite);
				nodes[i].addPreferenceChangeListener(listener);
				currentListeners.add(listener);
				currentListenerNodes.add(nodes[i]);
			} else {
				//System.err.println("ProjectPreferencesTab.addPropetyChangeListeners(..):  no listener added at level = " + i + "; node at that level is null");
			}
		}	
	}
	
	
	protected void addProjectPreferenceChangeListeners(RadioGroupFieldEditor field, String key, Composite composite)
	{
		IEclipsePreferences[] nodes = fPrefService.getNodesForLevels();
		for (int i = IPreferencesService.PROJECT_INDEX; i < nodes.length; i++) {
			if (nodes[i] != null) {
				PreferencesUtilities.RadioGroupPreferenceChangeListener listener = 
					fPrefUtils_x10.new RadioGroupPreferenceChangeListener(field, key, composite);
				nodes[i].addPreferenceChangeListener(listener);
				currentListeners.add(listener);
				currentListenerNodes.add(nodes[i]);
			} else {
				//System.err.println("ProjectPreferencesTab.addPropetyChangeListeners(..):  no listener added at level = " + i + "; node at that level is null");
			}
		}	
	}
	
	protected void addProjectPreferenceChangeListeners(StringFieldEditor field, String key, Composite composite)
	{
		IEclipsePreferences[] nodes = fPrefService.getNodesForLevels();
		for (int i = IPreferencesService.PROJECT_INDEX; i < nodes.length; i++) {
			if (nodes[i] != null) {
				// SMS 31 Oct 2006
				//ProjectPreferenceChangeListener listener = new ProjectPreferenceChangeListener(field, key, composite);
				PreferencesUtilities.StringPreferenceChangeListener listener = 
					fPrefUtils_x10.new StringPreferenceChangeListener(field, key, composite);
				nodes[i].addPreferenceChangeListener(listener);
				currentListeners.add(listener);
				currentListenerNodes.add(nodes[i]);
			} else {
				//System.err.println("ProjectPreferencesTab.addPropetyChangeListeners(..):  no listener added at level = " + i + "; node at that level is null");
			}
		}	
	}
	


	protected void removeProjectPreferenceChangeListeners()
	{
		// Remove all listeners from their respective nodes
		for (int i = 0; i < currentListeners.size(); i++) {
			((IEclipsePreferences) currentListenerNodes.get(i)).removePreferenceChangeListener(
					((IEclipsePreferences.IPreferenceChangeListener)currentListeners.get(i)));
		}
		// Clear the lists
		currentListeners = new ArrayList();
		currentListenerNodes = new ArrayList();
	}

	
	/**
	 * 
	 * @param	composite	the wiget that holds the button
	 * @param	fieldParent	the wiget that holds the field that will be
	 * 						set when the button is pressed
	 * 						(needed for posting a listener)
	 * @param	text		text that appears in the link
	 */
	protected Button createSelectProjectButton(Composite composite, final Composite fieldParent, String text)
	{
		final Button button = new Button(composite, SWT.NONE);
		button.setText(text);
		
		final class CompositeLinkSelectionListener implements SelectionListener {
			ProjectSelectionButtonResponder responder = null;
			// param was Composite parent
			CompositeLinkSelectionListener(ProjectSelectionButtonResponder responder) {
				this.responder = responder;
			}
			
			public void widgetSelected(SelectionEvent e) {
				//doMakeProjectSelectionLinkActivated((Link) e.widget, fieldParent);
				responder.doProjectSelectionActivated((Button) e.widget, fieldParent);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				//doMakeProjectSelectionLinkActivated((Link) e.widget, fieldParent);
				responder.doProjectSelectionActivated((Button) e.widget, fieldParent);
			}
		}

		CompositeLinkSelectionListener linkSelectionListener =
			new CompositeLinkSelectionListener(new ProjectSelectionButtonResponder());
		
		button.addSelectionListener(linkSelectionListener);
		return button;
	}
	
	
	private class ProjectSelectionButtonResponder
	{		
		public void doProjectSelectionActivated(Button button, Composite composite)
		{
			HashSet projectsWithSpecifics = new HashSet();
			try {
				IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				for (int i= 0; i < projects.length; i++) {
					IProject curr = projects[i];
					//if (hasProjectSpecificOptions(curr.getProject())) {
					projectsWithSpecifics.add(curr);
					//}
				}
			} catch (Exception e) {
				System.err.println("ProjectPreferencesTab:  Exception thrown when obtaining projects; no project selected");
				return;
			}
    
		    // SMS 20 Nov 2007:  Original
//			ProjectSelectionDialog dialog = new ProjectSelectionDialog(
//				button.getShell(),
//				ResourcesPlugin.getWorkspace().getRoot(),
//				projectContentProvider,
//				new LabelProviderForProjects(),
//				"Select Project");
			// SMS 24 Nov 2007:  Replaced with ...
			ContentProviderForAllProjects projectContentProvider = new ContentProviderForAllProjects();
		    ListSelectionDialog dialog = new ListSelectionDialog(
		    		button.getShell(), ResourcesPlugin.getWorkspace().getRoot(),
		            projectContentProvider,
		            new LabelProviderForProjects(), "Select a project");
			
			
			if (dialog.open() == Window.OK) {
				Object[] results = dialog.getResult();
				if (results.length > 0) {
					fProject = (IProject) results[0]; 		//dialog.getFirstResult();
					if (fProject.exists())	
						fPrefService.setProject(fProject);
					else {
						System.err.println("ProjectPreferencesTab:  Selected project does not exist; no project selected");
						return;
					}
				} else {
					System.err.println("ProjectPreferencesTab:  No project available for selection");
					return;
				}
			}

			// Enable the details links since now a project is selected
			for (int i = 0; i < detailsLinks.size(); i++) {
				((Link)detailsLinks.get(i)).setEnabled(true);
			}
			
			// Also enable the Restore Defaults and Apply buttons (if they should be enabled)
			for (int i = 0; i < fButtons.length; i++) {
				if (((Button)fButtons[i]).getText().equals(JFaceResources.getString("defaults"))) {
					fButtons[i].setEnabled(true);
				} else if (((Button)fButtons[i]).getText().equals(JFaceResources.getString("apply"))) {
					fButtons[i].setEnabled(isValid());
				}
			}
			// This will set the enabled state of buttons on the
			// preference page appropriately
	        fPrefPage.setValid(isValid());
		}	
	}
	
    // SMS 20 Nov 2007:  
    protected void setProjectSelectionValidator(
        	ContainerSelectionDialog dialog, boolean validateForPluginProject, boolean validateForIDEProject)
    {
//	    dialog.setValidator(new ValidationUtils.ProjectSelectionValidator());
    }

    
	public void performApply()
	{
		if (fPrefService.getProject() == null) {
			// No preferences node into which to store anything
			clearModifiedMarksOnLabels();	// just in case fields still show modified
			return;
		}
		for (int i = 0; i < fFields.length; i++) {
			fFields[i].store();
			fFields[i].clearModifiedMarkOnLabel();
		}
	}	
	
	 
	public boolean performCancel() {
		// Nullify the project in any case
		fPrefService.setProject(null);
		return true;
	}

	
	public void performDefaults() {
		if (fPrefService.getProject() == null) {
			// If no project set then there's no preferences
			// file from which to load anything	
			return;
		}
	
		// Clear all preferences for this page at this level;
		// "default" values will be set by inheritance from a higher level
		PreferencesInitializer initializer = fPrefPage.getPreferenceInitializer();
		initializer.clearPreferencesOnLevel(IPreferencesService.PROJECT_LEVEL);
		
		for (int i = 0; i < fFields.length; i++) {
			fFields[i].loadWithInheritance();
		}
	}

	
	public boolean performOk()
	{
		if (fPrefService.getProject() != null) {
			// Store each field
			for (int i = 0; i < fFields.length; i++) {
				fFields[i].store();
			}
		} else {
			// Clear preferences because we're closing up dialog;
			// note that a project preferences node will exist, if only
			// in a leftover state, even when no project is selected
			fPrefService.clearPreferencesAtLevel(IPreferencesService.PROJECT_LEVEL);
			//return true;
		}

		// Nullify the project in any case
		fPrefService.setProject(null);
		
		return true;
	}
	
	
}
