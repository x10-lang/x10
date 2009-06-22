package org.eclipse.imp.x10dt.ui.launching;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.imp.x10dt.core.X10Plugin;
import org.eclipse.imp.x10dt.ui.X10UIPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.debug.ui.launchConfigurations.AppletParametersTab;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public class X10LaunchShortcut implements ILaunchShortcut {
    public static final String X10LaunchConfigTypeID= "org.eclipse.imp.x10dt.ui.launching.X10LaunchConfigurationType";

    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    public void launch(ISelection selection, String mode) {
	if (selection instanceof IStructuredSelection) {
	    searchAndLaunch(((IStructuredSelection) selection).toArray(), mode);
	}
    }

    public void launch(IEditorPart editor, String mode) {
	IEditorInput input= editor.getEditorInput();
	if (input instanceof IFileEditorInput) {
	    IFileEditorInput fileInput= (IFileEditorInput) input;
	    searchAndLaunch(new Object[] { fileInput.getFile() }, mode);
	}
    }

    /**
     * Prompts the user to select a type
     * 
     * @return the selected type or <code>null</code> if none.
     */
    protected IType chooseType(IType[] types, String mode) {
	ElementListSelectionDialog dialog= new ElementListSelectionDialog(getShell(), new JavaElementLabelProvider());
	dialog.setElements(types);
	dialog.setTitle("Choose a main type to launch");
	if (mode.equals(ILaunchManager.DEBUG_MODE)) {
	    dialog.setMessage("Select application to debug");
	} else {
	    dialog.setMessage("Select application to run");
	}
	dialog.setMultipleSelection(false);
	if (dialog.open() == Window.OK) {
	    return (IType) dialog.getFirstResult();
	}
	return null;
    }

    protected void searchAndLaunch(Object[] search, String mode) {
	IType[] types= null;

	if (search != null) {
	    IFile file= (IFile) search[0];
	    try {
		// TODO Need to scan the X10 sources for types that have a main method...
		// types= AppletLaunchConfigurationUtils.findApplets(new ProgressMonitorDialog(getShell()), search);
		IFile javaFile= file;
		if (file.getFileExtension().equals("x10"))
		    javaFile= file.getProject().getFile(file.getProjectRelativePath().removeFileExtension().addFileExtension("java"));
		types= new IType[] { ((ICompilationUnit) JavaCore.create(javaFile)).findPrimaryType() };
	    } catch (Exception e) {
		/* Handle exceptions */
	    }
	    IType type= null;
	    if (types == null || types.length == 0) {
		MessageDialog.openInformation(getShell(), "X10 Application Launch", "No X10 applications found.");
	    } else if (types.length > 1) {
		type= chooseType(types, mode);
	    } else {
		type= types[0];
	    }
	    if (type != null) {
		launch(type, mode);
	    } else
		MessageDialog.openInformation(getShell(), "X10 Application Launch", "Generated Java application not found for X10 application " + file.getName() + "; perhaps there is a pending compilation error?");
	}
    }

    private Shell getShell() {
	IWorkbenchWindow window= getActiveWorkbenchWindow();
	if (window != null) {
	    return window.getShell();
	}
	return null;
    }

    private IWorkbenchWindow getActiveWorkbenchWindow() {
	return X10UIPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow();
    }

    /**
     * Locate a configuration to relaunch for the given type. If one cannot be found, create one.
     * 
     * @return a re-useable config or <code>null</code> if none
     */
    protected ILaunchConfiguration findLaunchConfiguration(IType type, String mode) {
	ILaunchConfigurationType configType= getX10LaunchConfigType();
	List candidateConfigs= Collections.EMPTY_LIST;
	try {
	    ILaunchConfiguration[] configs= DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(configType);

	    // TODO The following code that looks for a suitable configuration is Java-specific; rewrite it?
	    candidateConfigs= new ArrayList(configs.length);
	    for(int i= 0; i < configs.length; i++) {
		ILaunchConfiguration config= configs[i];
		if (config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "").equals(type.getFullyQualifiedName())) { //$NON-NLS-1$
		    if (config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "").equals(type.getJavaProject().getElementName())) { //$NON-NLS-1$
			candidateConfigs.add(config);
		    }
		}
	    }
	} catch (CoreException e) {
	    X10UIPlugin.log(e);
	}

	// If there are no existing configs associated with the IType, create one.
	// If there is exactly one config associated with the IType, return it.
	// Otherwise, if there is more than one config associated with the IType, prompt the
	// user to choose one.
	int candidateCount= candidateConfigs.size();
	if (candidateCount < 1) {
	    return createConfiguration(type);
	} else if (candidateCount == 1) {
	    return (ILaunchConfiguration) candidateConfigs.get(0);
	} else {
	    // Prompt the user to choose a config. A null result means the user
	    // cancelled the dialog, in which case this method returns null,
	    // since cancelling the dialog should also cancel launching anything.
	    ILaunchConfiguration config= chooseConfiguration(candidateConfigs, mode);
	    if (config != null) {
		return config;
	    }
	}
	return null;
    }

    /**
     * Create & return a new configuration based on the specified <code>IType</code>.
     */
    protected ILaunchConfiguration createConfiguration(IType type) {
	ILaunchConfiguration config= null;
	try {
	    ILaunchConfigurationType configType= getX10LaunchConfigType();
	    ILaunchConfigurationWorkingCopy wc= configType.newInstance(null, DebugPlugin.getDefault().getLaunchManager()
		    .generateUniqueLaunchConfigurationNameFrom(type.getElementName()));
	    wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, type.getFullyQualifiedName());
	    wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, type.getJavaProject().getElementName());
	    wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_APPLET_WIDTH, AppletParametersTab.DEFAULT_APPLET_WIDTH);
	    wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_APPLET_HEIGHT, AppletParametersTab.DEFAULT_APPLET_HEIGHT);
	    wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_APPLET_NAME, EMPTY_STRING);

	    String commonPath= X10Plugin.x10CompilerPath;
	    String runtimePath= commonPath.substring(0, commonPath.lastIndexOf(File.separatorChar)+1) + "x10.runtime" + File.separator + "classes";

	    wc.setAttribute(X10LaunchConfigAttributes.X10RuntimeAttributeID, runtimePath);

	    config= wc.doSave();
	} catch (CoreException ce) {
	    X10UIPlugin.log(ce);
	}
	return config;
    }

    /**
     * Show a selection dialog that allows the user to choose one of the specified launch configurations. Return the chosen config, or <code>null</code>
     * if the user cancelled the dialog.
     */
    protected ILaunchConfiguration chooseConfiguration(List configList, String mode) {
	IDebugModelPresentation labelProvider= DebugUITools.newDebugModelPresentation();
	ElementListSelectionDialog dialog= new ElementListSelectionDialog(getShell(), labelProvider);

	dialog.setElements(configList.toArray());
	dialog.setTitle("Select an X10 configuration");
	if (mode.equals(ILaunchManager.DEBUG_MODE)) {
	    dialog.setMessage("Select an X10 configuration to debug");
	} else {
	    dialog.setMessage("Select an X10 configuration to debug");
	}
	dialog.setMultipleSelection(false);

	int result= dialog.open();

	labelProvider.dispose();
	if (result == Window.OK) {
	    return (ILaunchConfiguration) dialog.getFirstResult();
	}
	return null;
    }

    /**
     * Returns the local java launch config type
     */
    protected ILaunchConfigurationType getX10LaunchConfigType() {
	ILaunchManager lm= DebugPlugin.getDefault().getLaunchManager();

	return lm.getLaunchConfigurationType(X10LaunchConfigTypeID);
    }

    protected void launch(IType type, String mode) {
	try {
	    ILaunchConfiguration config= findLaunchConfiguration(type, mode);
	    if (config != null) {
		config.launch(mode, null);
	    }
	} catch (CoreException e) {
	    /* Handle exceptions */
	}
    }
}
