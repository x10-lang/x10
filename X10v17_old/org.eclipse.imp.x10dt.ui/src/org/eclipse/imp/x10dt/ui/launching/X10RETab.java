/*******************************************************************************
* Copyright (c) 2008 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

*******************************************************************************/

/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.x10dt.ui.launching;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.imp.x10dt.core.X10Plugin;
import org.eclipse.imp.x10dt.ui.X10UIPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

public class X10RETab extends AbstractLaunchConfigurationTab implements ILaunchConfigurationTab {
    protected Text fX10RuntimeText;
    protected Button fX10RuntimeFolderButton;
    protected Button fX10RuntimeJarButton;
    protected Text fX10RuntimeArgumentsText;

    private WidgetListener fListener= new WidgetListener();

    public void createControl(Composite parent) {
	Font font= parent.getFont();

	Composite topComp= new Composite(parent, SWT.NONE);
	setControl(topComp);
	GridLayout topLayout= new GridLayout();
	topLayout.numColumns= 1;
	topLayout.marginHeight= 0;
	topLayout.marginWidth= 0;
	topComp.setLayout(topLayout);
	GridData gd= new GridData(GridData.FILL_HORIZONTAL);
	topComp.setLayoutData(gd);
	topComp.setFont(font);

	createRuntimeEditor(topComp);
	createRuntimeArgumentsEditor(topComp);
    }

    private void createRuntimeArgumentsEditor(Composite parent) {
	Font font = parent.getFont();
	Group group = new Group(parent, SWT.NONE);
	group.setFont(font);
	GridLayout layout = new GridLayout();
	group.setLayout(layout);
	group.setLayoutData(new GridData(GridData.FILL_BOTH));

	String controlName = "Runtime arguments:";
	group.setText(controlName);

	fX10RuntimeArgumentsText = new Text(group, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
	GridData gd = new GridData(GridData.FILL_BOTH);
	gd.heightHint = 40;
	gd.widthHint = 100;
	fX10RuntimeArgumentsText.setLayoutData(gd);
	fX10RuntimeArgumentsText.setFont(font);
	fX10RuntimeArgumentsText.addModifyListener(new ModifyListener() {
		public void modifyText(ModifyEvent evt) {
			updateLaunchConfigurationDialog();
		}
	});

	String buttonLabel = "Variables...";
	Button runtimeArgVariableButton = createPushButton(group, buttonLabel, null); 
	runtimeArgVariableButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
	runtimeArgVariableButton.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		StringVariableSelectionDialog dialog = new StringVariableSelectionDialog(getShell());
		dialog.open();
		String variable = dialog.getVariableExpression();
		if (variable != null) {
		    fX10RuntimeArgumentsText.insert(variable);
		}
	    }
	});
    }

    /**
         * Creates the widgets for specifying an x10 runtime.
         * @param parent the parent composite
         */
    private void createRuntimeEditor(Composite parent) {
	Font font= parent.getFont();
	Group group= new Group(parent, SWT.NONE);
	group.setText("X10 Runtime Environment");
	GridData gd= new GridData(GridData.FILL_HORIZONTAL);
	group.setLayoutData(gd);
	GridLayout layout= new GridLayout();
	layout.numColumns= 3;
	group.setLayout(layout);
	group.setFont(font);

	fX10RuntimeText= new Text(group, SWT.SINGLE | SWT.BORDER);
	gd= new GridData(GridData.FILL_HORIZONTAL);
	fX10RuntimeText.setLayoutData(gd);
	fX10RuntimeText.setFont(font);
	fX10RuntimeText.addModifyListener(fListener);

	fX10RuntimeFolderButton= createPushButton(group, "Browse for Folder...", null);
	fX10RuntimeFolderButton.addSelectionListener(fListener);

	fX10RuntimeJarButton= createPushButton(group, "Browse for Jar...", null);
	fX10RuntimeJarButton.addSelectionListener(fListener);
    }

    /**
         * A listener which handles widget change events for the controls in this tab.
         */
    private class WidgetListener implements ModifyListener, SelectionListener {
	public void modifyText(ModifyEvent e) {
	    updateLaunchConfigurationDialog();
	}

	public void widgetSelected(SelectionEvent e) {
	    Object source= e.getSource();

	    if (source == fX10RuntimeFolderButton) {
		handleRuntimeButtonSelected(true);
	    } else if (source == fX10RuntimeJarButton) {
		handleRuntimeButtonSelected(false);
	    } else {
		updateLaunchConfigurationDialog();
	    }
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}
    }

    /**
     * Show a dialog that lets the user select a project. This in turn provides context for the main type, allowing the user to key a main type name, or
     * constraining the search for main types to the specified project.
     * @param isFolder true if the user wants to specify a class folder
     */
    protected void handleRuntimeButtonSelected(boolean isFolder) {
	String x10Runtime= isFolder ? chooseX10RuntimeFolder() : chooseX10RuntimeJar();

	if (x10Runtime == null) {
	    return;
	}

	fX10RuntimeText.setText(x10Runtime);
    }

    private String chooseX10RuntimeFolder() {
	DirectoryDialog dialog= new DirectoryDialog(getShell());
	String currentRuntime= fX10RuntimeText.getText();

	dialog.setMessage("Select an X10 runtime for the launch configuration:");

	if (!currentRuntime.trim().equals("")) { //$NON-NLS-1$
	    File path= new File(currentRuntime);

	    if (path.exists()) {
		if (!path.isDirectory())
		    path= path.getParentFile();
		dialog.setFilterPath(path.getAbsolutePath());
	    }
	}

	String selectedDirectory= dialog.open();

	return selectedDirectory;
    }

    private String chooseX10RuntimeJar() {
	FileDialog dialog= new FileDialog(getShell());
	String currentRuntime= fX10RuntimeText.getText();

//	dialog.setMessage("Select an X10 runtime for the launch configuration:");

	if (!currentRuntime.trim().equals("")) { //$NON-NLS-1$
	    File path= new File(currentRuntime);

	    if (path.exists()) {
		dialog.setFilterPath(currentRuntime);
	    }
	}

	String selectedDirectory= dialog.open();

	return selectedDirectory;
    }

    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	String commonPath= X10Plugin.x10CompilerPath;
	// BRT seemed to assume path ending in slash, which it doesn't.  Fix that here.
	if(commonPath.endsWith(File.separator)) {
		commonPath=commonPath.substring(0,commonPath.length()-1);
	}
	String runtimePath= commonPath.substring(0, commonPath.lastIndexOf(File.separatorChar)+1) + "x10.runtime" + File.separator + "classes";

	configuration.setAttribute(X10LaunchConfigAttributes.X10RuntimeAttributeID, runtimePath);
	configuration.setAttribute(X10LaunchConfigAttributes.X10RuntimeArgumentsID, (String)null);
    }

    public void initializeFrom(ILaunchConfiguration configuration) {
	updateRuntimeFromConfig(configuration);
    }

    protected void updateRuntimeFromConfig(ILaunchConfiguration config) {
	String runtimeName= ""; //$NON-NLS-1$
	try {
	    runtimeName= config.getAttribute(X10LaunchConfigAttributes.X10RuntimeAttributeID, "");
	    fX10RuntimeArgumentsText.setText(config.getAttribute(X10LaunchConfigAttributes.X10RuntimeArgumentsID, "")); //$NON-NLS-1$
	} catch (CoreException ce) {
	    X10UIPlugin.log(ce);
	}
	fX10RuntimeText.setText(runtimeName);
    }

    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
	configuration.setAttribute(X10LaunchConfigAttributes.X10RuntimeAttributeID, fX10RuntimeText.getText().trim());
	configuration.setAttribute(X10LaunchConfigAttributes.X10RuntimeArgumentsID, getAttributeValueFrom(fX10RuntimeArgumentsText));
    }

    /**
     * Retuns the string in the text widget, or <code>null</code> if empty.
     * 
     * @return text or <code>null</code>
     */
    protected String getAttributeValueFrom(Text text) {
	String content = text.getText().trim();
	if (content.length() > 0) {
	    return content;
	}
	return null;
    }

    public String getName() {
	return "X10 Runtime";
    }

    public Image getImage() {
	return X10UIPlugin.getInstance().getImage(X10UIPlugin.RUNTIME_IMG_NAME);
    }
}
