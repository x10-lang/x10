package x10.uide.launching;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import com.ibm.watson.safari.x10.preferences.X10Preferences;

import x10.uide.X10UIPlugin;

public class X10RETab extends AbstractLaunchConfigurationTab implements ILaunchConfigurationTab {
    protected Text fX10RuntimeText;
    protected Button fX10RuntimeButton;

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
	layout.numColumns= 2;
	group.setLayout(layout);
	group.setFont(font);

	fX10RuntimeText= new Text(group, SWT.SINGLE | SWT.BORDER);
	gd= new GridData(GridData.FILL_HORIZONTAL);
	fX10RuntimeText.setLayoutData(gd);
	fX10RuntimeText.setFont(font);
	fX10RuntimeText.addModifyListener(fListener);

	fX10RuntimeButton= createPushButton(group, "Browse...", null);
	fX10RuntimeButton.addSelectionListener(fListener);
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

	    if (source == fX10RuntimeButton) {
		handleRuntimeButtonSelected();
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
         */
    protected void handleRuntimeButtonSelected() {
	String x10Runtime= chooseX10Runtime();

	if (x10Runtime == null) {
	    return;
	}

	fX10RuntimeText.setText(x10Runtime);
    }

    private String chooseX10Runtime() {
	DirectoryDialog dialog= new DirectoryDialog(getShell());
	dialog.setMessage("Select an X10 runtime for the launch configuration:");
	String currentWorkingDir= fX10RuntimeText.getText();
	if (!currentWorkingDir.trim().equals("")) { //$NON-NLS-1$
	    File path= new File(currentWorkingDir);
	    if (path.exists()) {
		dialog.setFilterPath(currentWorkingDir);
	    }
	}

	String selectedDirectory= dialog.open();

	return selectedDirectory;
    }

    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	String commonPath= X10Preferences.x10CommonPath;
	String runtimePath= commonPath.substring(0, commonPath.lastIndexOf(File.separatorChar)+1) + "x10.runtime" + File.separator + "classes";

	configuration.setAttribute(X10LaunchConfigAttributes.X10RuntimeAttributeID, runtimePath);
    }

    public void initializeFrom(ILaunchConfiguration configuration) {
	updateRuntimeFromConfig(configuration);
    }

    protected void updateRuntimeFromConfig(ILaunchConfiguration config) {
	String runtimeName= ""; //$NON-NLS-1$
	try {
	    runtimeName= config.getAttribute(X10LaunchConfigAttributes.X10RuntimeAttributeID, "");
	} catch (CoreException ce) {
	    X10UIPlugin.log(ce);
	}
	fX10RuntimeText.setText(runtimeName);
    }

    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
	configuration.setAttribute(X10LaunchConfigAttributes.X10RuntimeAttributeID, fX10RuntimeText.getText().trim());
    }

    public String getName() {
	return "X10 Runtime";
    }

    public Image getImage() {
	return X10UIPlugin.getInstance().getImage(X10UIPlugin.RUNTIME_IMG_NAME);
    }
}
