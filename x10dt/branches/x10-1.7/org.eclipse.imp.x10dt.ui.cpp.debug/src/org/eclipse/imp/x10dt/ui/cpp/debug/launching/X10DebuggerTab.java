/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug.launching;

import static org.eclipse.imp.x10dt.ui.cpp.debug.Constants.ATTR_REMOTE_DEBUGGER_PATH;
import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_DEBUGGER_HOST;
import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_DEBUGGER_ID;
import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_STOP_IN_MAIN;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.imp.x10dt.ui.cpp.debug.Constants;
import org.eclipse.imp.x10dt.ui.cpp.debug.DebugMessages;
import org.eclipse.imp.x10dt.ui.cpp.launch.LaunchMessages;
import org.eclipse.ptp.core.elementcontrols.IResourceManagerControl;
import org.eclipse.ptp.core.elements.attributes.ResourceManagerAttributes;
import org.eclipse.ptp.launch.ui.LaunchConfigurationTab;
import org.eclipse.ptp.launch.ui.LaunchImages;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteProxyOptions;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.remote.ui.IRemoteUIFileManager;
import org.eclipse.ptp.remote.ui.IRemoteUIServices;
import org.eclipse.ptp.remote.ui.PTPRemoteUIPlugin;
import org.eclipse.ptp.rm.remote.core.AbstractRemoteResourceManagerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public final class X10DebuggerTab extends LaunchConfigurationTab implements ILaunchConfigurationTab {

  // --- Interface methods implementation
  
  public void createControl(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(1, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
    
    createStopInMain(composite);
    
    final Group group = new Group(composite, SWT.NONE);
    group.setFont(parent.getFont());
    group.setLayout(new GridLayout(1, false));
    group.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    group.setText(DebugMessages.DT_GroupName);
    
    createDebugEngineDaemon(group);
    createLocalhostAddress(group);
    createPortConfig(group);
    
    setControl(composite);
  }

  public String getName() {
    return DebugMessages.DT_TabName;
  }

  public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
    configuration.setAttribute(ATTR_DEBUGGER_ID, DEBUGGER_ID);
    configuration.setAttribute(ATTR_STOP_IN_MAIN, this.fStopInMain.getSelection());
    configuration.setAttribute(ATTR_DEBUGGER_HOST, this.fLocalhostAddressText.getText().trim());
    configuration.setAttribute(ATTR_REMOTE_DEBUGGER_PATH, this.fDbgEngineDaemonPathText.getText().trim());
    if (this.fRangePortBt.getSelection()) {
      configuration.setAttribute(Constants.ATTR_RANGE_PORT, this.fRangePortText.getText().trim());
      configuration.setAttribute(Constants.ATTR_SPECIFIC_PORT, (String) null);
    } else {
      configuration.setAttribute(Constants.ATTR_RANGE_PORT, (String) null);
      configuration.setAttribute(Constants.ATTR_SPECIFIC_PORT, this.fSpecificPortText.getText().trim());
    }
  }

  public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
    configuration.setAttribute(ATTR_DEBUGGER_ID, DEBUGGER_ID);
    configuration.setAttribute(ATTR_STOP_IN_MAIN, true);
    configuration.setAttribute(ATTR_DEBUGGER_HOST, (String) null);
    configuration.setAttribute(ATTR_REMOTE_DEBUGGER_PATH, (String) null);
    final StringBuilder sb = new StringBuilder();
    sb.append(Constants.DEFAULT_PORT_RANGE_MIN).append('-').append(Constants.DEFAULT_PORT_RANGE_MAX);
    configuration.setAttribute(Constants.ATTR_RANGE_PORT, sb.toString());
    configuration.setAttribute(Constants.ATTR_SPECIFIC_PORT, Constants.DEFAULT_PORT);
  }
  
  // --- Overridden methods
  
  public void initializeFrom(final ILaunchConfiguration config) {
    setLaunchConfiguration(config);
    try {
      this.fDbgEngineDaemonPathText.setText(config.getAttribute(ATTR_REMOTE_DEBUGGER_PATH, REMOTE_DEBUGGER_DEFAULT_PATH));
    } catch (CoreException except) {
      this.fDbgEngineDaemonPathText.setText(REMOTE_DEBUGGER_DEFAULT_PATH);
    }
    String value = null;
    try {
      value = config.getAttribute(ATTR_DEBUGGER_HOST, (String) null);
    } catch (CoreException except) {
      // Simply forgets in that case. Handles with null value.
    }
    if ((value == null) || (value.length() == 0)) {
      final String address = getDefaultHostAddress(config);
      if (address != null) {
        this.fLocalhostAddressText.setText(address);
      }
    } else {
      this.fLocalhostAddressText.setText(value);
    }
    final String portRange = this.fRangePortText.getText().trim();
    if (portRange.length() == 0) {
      final StringBuilder sb = new StringBuilder();
      sb.append(Constants.DEFAULT_PORT_RANGE_MIN).append('-').append(Constants.DEFAULT_PORT_RANGE_MAX);
      this.fRangePortText.setText(sb.toString());
    }
    final String specificPort = this.fSpecificPortText.getText().trim();
    if (specificPort.length() == 0) {
      this.fSpecificPortText.setText(String.valueOf(Constants.DEFAULT_PORT));
    }
  }
  
  public Image getImage() {
    return LaunchImages.getImage(LaunchImages.IMG_DEBUGGER_TAB);
  }
  
  public boolean isValid(final ILaunchConfiguration launchConfig) {
    if (getErrorMessage() == null) {
      return ((this.fDbgEngineDaemonPathText.getText().trim().length() > 0) &&
              (this.fLocalhostAddressText.getText().trim().length() > 0));
    } else {
      return false;
    }
  }
  
  // --- Private code
  
  private String browseFile() {
    final IResourceManagerControl rm = (IResourceManagerControl) getResourceManager(getLaunchConfiguration());
    if ((rm == null) || (rm.getState() != ResourceManagerAttributes.State.STARTED)) {
      setErrorMessage(LaunchMessages.CAT_NoRunningResManager);
      return null;
    }
    final AbstractRemoteResourceManagerConfiguration rmc = (AbstractRemoteResourceManagerConfiguration) rm.getConfiguration();
    final IRemoteServices remServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(rmc.getRemoteServicesId());
    final IRemoteUIServices remUIServices = PTPRemoteUIPlugin.getDefault().getRemoteUIServices(remServices);

    if (remServices != null && remUIServices != null) {
      final IRemoteConnection rmConn = remServices.getConnectionManager().getConnection(rmc.getConnectionName());
      final IRemoteUIFileManager fileManager = remUIServices.getUIFileManager();
      if (fileManager != null) {
        fileManager.setConnection(rmConn);
        final IPath path = fileManager.browseFile(getShell(), DebugMessages.DT_DbgEngineDaemonLoc,
                                                  this.fDbgEngineDaemonPathText.getText().trim());
        if (path != null) {
          return path.toString();
        }
      }
      return null;
    } else {
      final FileDialog dialog = new FileDialog(getShell());
      dialog.setText(DebugMessages.DT_DbgEngineDaemonLoc);
      dialog.setFileName(this.fDbgEngineDaemonPathText.getText());
      return dialog.open();
    }
  }
  
  private void createDebugEngineDaemon(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(3, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    final Label label = new Label(composite, SWT.NONE);
    label.setFont(composite.getFont());
    label.setText(DebugMessages.DT_DbgEngineDaemonPathLabel);
    
    this.fDbgEngineDaemonPathText = new Text(composite, SWT.SINGLE | SWT.BORDER);
    this.fDbgEngineDaemonPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    this.fDbgEngineDaemonPathText.addModifyListener(new UpdateConfigurationDialogModifyListener());
    
    this.fDbgEngineDaemonBrowseBt = createPushButton(composite, DebugMessages.DT_BrowseBt, null /* image */);
    this.fDbgEngineDaemonBrowseBt.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false));
    this.fDbgEngineDaemonBrowseBt.addSelectionListener(new DebugEngineDaemonSelectionListener());
  }
  
  private void createLocalhostAddress(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(3, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    final Label label = new Label(composite, SWT.NONE);
    label.setFont(composite.getFont());
    label.setText(DebugMessages.DT_LocalAddressLabel);
    
    this.fLocalhostAddressText = new Text(composite, SWT.SINGLE | SWT.BORDER);
    this.fLocalhostAddressText.setLayoutData(new GridData(150, SWT.DEFAULT));
    this.fLocalhostAddressText.addModifyListener(new UpdateConfigurationDialogModifyListener());
    
    this.fLocalhostAddressBt = createPushButton(composite, DebugMessages.DT_GetIPBt, null /* image */);
    this.fLocalhostAddressBt.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false));
    this.fLocalhostAddressBt.addSelectionListener(new LocalhostAddressSelectionListener());
  }
  
  private void createPortConfig(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    
    final Label label = new Label(composite, SWT.NONE);
    label.setText("Port:");
    final GridData labelData = new GridData(SWT.FILL, SWT.NONE, true, false);
    labelData.horizontalSpan = 2;
    label.setLayoutData(labelData);
    
    this.fRangePortBt = createRadioButton(composite, "Random port in range");
    final GridData btGridData = new GridData(SWT.NONE, SWT.CENTER, false, false);
    btGridData.horizontalIndent = 8;
    this.fRangePortBt.setLayoutData(btGridData);
    this.fRangePortBt.setSelection(true);
    this.fRangePortText = new Text(composite, SWT.SINGLE | SWT.BORDER);
    this.fRangePortText.setLayoutData(new GridData(150, SWT.DEFAULT));
    
    this.fSpecificPortBt = createRadioButton(composite, "Specific port");
    this.fSpecificPortBt.setLayoutData(btGridData);
    this.fSpecificPortText = new Text(composite, SWT.SINGLE | SWT.BORDER);
    this.fSpecificPortText.setLayoutData(new GridData(150, SWT.DEFAULT));
  }
  
  private void createStopInMain(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(1, false));
    
    this.fStopInMain = createCheckButton(composite, DebugMessages.DT_StopInMainBt);
    this.fStopInMain.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    this.fStopInMain.addSelectionListener(new UpdateConfigurationDialogSelectionListener());
  }
  
  private String getDefaultHostAddress(final ILaunchConfiguration configuration) {
    final IResourceManagerControl rm = (IResourceManagerControl) getResourceManager(configuration);
    if (rm == null) {
      return null;
    }
    final AbstractRemoteResourceManagerConfiguration rmc = (AbstractRemoteResourceManagerConfiguration) rm.getConfiguration();
    if (rmc.testOption(IRemoteProxyOptions.PORT_FORWARDING)) {
      final IRemoteServices remServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(rmc.getRemoteServicesId());
      final IRemoteUIServices remUIServices = PTPRemoteUIPlugin.getDefault().getRemoteUIServices(remServices);

      if (remServices != null && remUIServices != null) {
        final IRemoteConnection rmConn = remServices.getConnectionManager().getConnection(rmc.getConnectionName());
        return rmConn.getAddress();
      }
    } else {
      final String localAddress = rmc.getLocalAddress();
      if ((localAddress != null) && ! LOCALHOST.equals(localAddress)) {
        return localAddress;
      }
      try {
        final InetAddress ip = InetAddress.getLocalHost();
        return ip.getHostAddress();
      } catch (UnknownHostException except) {
        // Simply forgets
      }
    }
    return null;
  }
  
  // --- Private classes
  
  private class DebugEngineDaemonSelectionListener implements SelectionListener {

    // --- Interface methods implementation
    
    public void widgetDefaultSelected(final SelectionEvent event) {
    }

    public void widgetSelected(final SelectionEvent event) {
      final String file = browseFile();
      if (file != null) {
        X10DebuggerTab.this.fDbgEngineDaemonPathText.setText(file);
      }
      updateLaunchConfigurationDialog();
    }
    
  }
  
  private class LocalhostAddressSelectionListener implements SelectionListener {

    // --- Interface methods implementation
    
    public void widgetDefaultSelected(final SelectionEvent event) {
    }

    public void widgetSelected(final SelectionEvent event) {
      final String address = getDefaultHostAddress(getLaunchConfiguration());
      if (address != null) {
        X10DebuggerTab.this.fLocalhostAddressText.setText(address);
      }
      setDirty(true);
      updateLaunchConfigurationDialog();
    }
    
  }
  
  private class UpdateConfigurationDialogModifyListener implements ModifyListener {

    // --- Interface methods implementation
    
    public void modifyText(final ModifyEvent event) {
      updateLaunchConfigurationDialog();
    }
    
  }
  
  private class UpdateConfigurationDialogSelectionListener implements SelectionListener {

    // --- Interface methods implementation
    
    public void widgetDefaultSelected(final SelectionEvent event) {
    }

    public void widgetSelected(final SelectionEvent event) {
      updateLaunchConfigurationDialog();
    }
    
  }
  
  // --- Fields
  
  private Button fStopInMain;
  
  private Text fDbgEngineDaemonPathText;
  
  private Button fDbgEngineDaemonBrowseBt;
  
  private Text fLocalhostAddressText;
  
  private Button fLocalhostAddressBt;
  
  private Button fRangePortBt;
  
  private Text fRangePortText;
  
  private Button fSpecificPortBt;
  
  private Text fSpecificPortText;
  
  private static final String DEBUGGER_ID = "org.eclipse.imp.x10dt.ui.cpp.debugger"; //$NON-NLS-1$
  
  private static final String REMOTE_DEBUGGER_DEFAULT_PATH = "irmtdbgc"; //$NON-NLS-1$
  
  private static final String LOCALHOST = "localhost"; //$NON-NLS-1$

}
