/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.imp.utils.Pair;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ptp.remote.core.exception.RemoteConnectionException;
import org.eclipse.ptp.remotetools.environment.core.ITargetElement;
import org.eclipse.ptp.services.core.IServiceProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import x10dt.ui.launch.core.Constants;
import x10dt.ui.launch.core.LaunchImages;
import x10dt.ui.launch.core.dialogs.DialogsFactory;
import x10dt.ui.launch.core.platform_conf.EValidationStatus;
import x10dt.ui.launch.core.utils.CoreResourceUtils;
import x10dt.ui.launch.core.utils.KeyboardUtils;
import x10dt.ui.launch.core.utils.SWTFormUtils;
import x10dt.ui.launch.cpp.CppLaunchCore;
import x10dt.ui.launch.cpp.CppLaunchImages;
import x10dt.ui.launch.cpp.LaunchMessages;
import x10dt.ui.launch.cpp.platform_conf.IConnectionConf;
import x10dt.ui.launch.cpp.platform_conf.validation.IX10PlatformChecker;
import x10dt.ui.launch.cpp.platform_conf.validation.IX10PlatformValidationListener;
import x10dt.ui.launch.cpp.platform_conf.validation.PlatformCheckerFactory;
import x10dt.ui.launch.cpp.utils.PTPConfUtils;

final class ConnectionSectionPart extends AbstractCommonSectionFormPart implements IFormPart, IServiceConfigurationListener {

  ConnectionSectionPart(final Composite parent, final ConnectionAndCommunicationConfPage formPage) {
    super(parent, formPage);
    
    getSection().setFont(parent.getFont());
    getSection().setText(LaunchMessages.RMCP_ConnectionSectionTitle);
    getSection().setDescription(LaunchMessages.RMCP_ConnectionSectionDescr);
    getSection().setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    
    this.fConnectionTypeListeners = new ArrayList<IConnectionTypeListener>();
    this.fValidationListeners = new ArrayList<IX10PlatformValidationListener>();
    this.fConnectionSwitchListeners = new ArrayList<IConnectionSwitchListener>();
    
    createClient(formPage.getManagedForm(), formPage.getManagedForm().getToolkit());
    addCompletePartListener(formPage);
  }
  
  // --- IServiceConfigurationListener's interface methods implementation
  
  public void serviceConfigurationModified(final String textContent) {
  }

  public void serviceConfigurationSelected(final IServiceProvider serviceProvider) {
    final IConnectionConf ciConf = getPlatformConf().getConnectionConf();
    if (ciConf.isLocal()) {
      this.fLocalConnBt.setSelection(true);
      this.fRemoteConnBt.setSelection(false);
      this.fLocalConnBt.notifyListeners(SWT.Selection, new Event());
    } else {
      this.fRemoteConnBt.setSelection(true);
      this.fLocalConnBt.setSelection(false);
      this.fRemoteConnBt.notifyListeners(SWT.Selection, new Event());
        
      int index = -1;
      for (final IConnectionInfo connectionInfo : getAllConnectionInfo()) {
        ++index;
        if (connectionInfo.getName().equals(ciConf.getConnectionName())) {
          this.fCurrentConnection = connectionInfo;
          this.fTableViewer.getTable().select(index);
          this.fTableViewer.getTable().notifyListeners(SWT.Selection, new Event());
        }
      }
    }
  }
  
  // ---IFormPart's interface methods implementation
  
  public void dispose() {
    removeCompletePartListener(getFormPage());
    this.fConnectionTypeListeners.clear();
  }
  
  // --- Overridden methods
  
  public boolean setFormInput(final Object input) {
    setPartCompleteFlag(hasCompleteInfo());
    
    this.fValidationListeners.add((IX10PlatformValidationListener) getFormPage().getEditor());
    
    this.fConnectionTypeListeners.add((IConnectionTypeListener) getFormPage().getEditor());
    for (final IFormPart formPart : getFormPage().getManagedForm().getParts()) {
      if (formPart instanceof IConnectionTypeListener) {
        this.fConnectionTypeListeners.add((IConnectionTypeListener) formPart);
      }
      if (formPart instanceof IConnectionSwitchListener) {
    	  this.fConnectionSwitchListeners.add((IConnectionSwitchListener) formPart);
      }
    }
    final IFormPage page = getFormPage().getEditor().findPage(X10CompilationConfigurationPage.X10_COMPILATION_CONF_PAGE_ID);
    if ((page != null) && (page.getManagedForm() != null)) {
      for (final IFormPart formPart : page.getManagedForm().getParts()) {
        if (formPart instanceof IConnectionTypeListener) {
          this.fConnectionTypeListeners.add((IConnectionTypeListener) formPart);
        }
      }
    }
    
    return false;
  }
 
  // --- Private code
  
  private void addListeners(final IManagedForm managedForm, final Button localConnBt, final Button remoteConnBt, 
                            final Button validateBt, final Text hostText, final Spinner portText, final Text userNameText, 
                            final Button passwordAuthBt, final Label passwordLabel, final Text passwordText, 
                            final Button privateKeyFileAuthBt, final Text privateKeyText, final Button browseBt, 
                            final Text passphraseText, final Collection<Control> firstGroupControls, 
                            final Button portForwarding, final Text localAddress, final Spinner connectionTimeoutSpinner,
                            final Collection<Control> secondGroupControls, final Collection<Control> keyFileControls,
                            final Collection<Control> localAddressControls) {
    localConnBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        if (localConnBt.getSelection()) {
          getPlatformConf().setIsLocalFlag(true);

          for (final Control control : firstGroupControls) {
            control.setEnabled(false);
          }
          for (final Control control : secondGroupControls) {
            control.setEnabled(false);
          }
          for (final Control keyFileControl : keyFileControls) {
            keyFileControl.setEnabled(false);
          }
          validateBt.setEnabled(false);
          handleEmptyTextValidation(hostText, LaunchMessages.RMCP_HostLabel);
          handleEmptyTextValidation(userNameText, LaunchMessages.RMCP_UserLabel);
          handleEmptyTextValidation(privateKeyText, LaunchMessages.RMCP_PrivateKeyFileLabel);
          
          for (final IConnectionInfo connectionInfo : getAllConnectionInfo()) {
            new ConnectionInfoValidationListener(connectionInfo, false).remoteConnectionUnknownStatus();
          }
          for (final IConnectionSwitchListener listener : ConnectionSectionPart.this.fConnectionSwitchListeners) {
        	  listener.connectionSwitched(true);
          }
          for (final IConnectionTypeListener listener : ConnectionSectionPart.this.fConnectionTypeListeners) {
            listener.connectionChanged(true, null, null, true);
          }
          
          setPartCompleteFlag(hasCompleteInfo());
          updateDirtyState(managedForm);
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    remoteConnBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        if (remoteConnBt.getSelection()) {
          getPlatformConf().setIsLocalFlag(false);
          for (final Control control : firstGroupControls) {
            control.setEnabled(true);
          }
          final IConnectionInfo curConnInfo = ConnectionSectionPart.this.fCurrentConnection;
          if (curConnInfo != null) {
            for (final Control control : secondGroupControls) {
              control.setEnabled(true);
            }
            fillConnectionControlsInfo(curConnInfo);
            updateConnectionConf();
          }
          for (final IConnectionSwitchListener listener : ConnectionSectionPart.this.fConnectionSwitchListeners) {
        	  listener.connectionSwitched(true);
          }
          for (final IConnectionInfo connectionInfo : getAllConnectionInfo()) {
            validateRemoteHostConnection(connectionInfo, (connectionInfo == curConnInfo));
          }
          setPartCompleteFlag(hasCompleteInfo());
          updateDirtyState(managedForm);
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    hostText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        final IConnectionInfo curConnInfo = getCurrentConnectionInfo();
        if (curConnInfo != null) {
          if (! curConnInfo.getHostName().equals(hostText.getText().trim())) {
            notifyConnectionUnknownStatus(curConnInfo);
          }
          curConnInfo.setHostName(hostText.getText().trim());
        }
        handleEmptyTextValidation(hostText, LaunchMessages.RMCP_HostLabel);
        if ((curConnInfo != null) && (ConnectionSectionPart.this.fCurrentConnection == curConnInfo)) {
          setPartCompleteFlag(hasCompleteInfo());
          updateConnectionConf();
          updateDirtyState(managedForm);
        }
      }
      
    });
    portText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        final IConnectionInfo curConnInfo = getCurrentConnectionInfo();
        if (curConnInfo != null) {
          if (curConnInfo.getPort() != portText.getSelection()) {
            notifyConnectionUnknownStatus(curConnInfo);
          }
          curConnInfo.setPort(portText.getSelection());
        }
        if ((curConnInfo != null) && (ConnectionSectionPart.this.fCurrentConnection == curConnInfo)) {
          setPartCompleteFlag(hasCompleteInfo());
          updateConnectionConf();
          updateDirtyState(managedForm);
        }
      }
      
    });
    userNameText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        final IConnectionInfo curConnInfo = getCurrentConnectionInfo();
        if (curConnInfo != null) {
          if (! curConnInfo.getUserName().equals(userNameText.getText().trim())) {
            notifyConnectionUnknownStatus(curConnInfo);
          }
          curConnInfo.setUserName(userNameText.getText().trim());
        }
        handleEmptyTextValidation(userNameText, LaunchMessages.RMCP_UserLabel);
        if ((curConnInfo != null) && (ConnectionSectionPart.this.fCurrentConnection == curConnInfo)) {
          setPartCompleteFlag(hasCompleteInfo());
          updateConnectionConf();
          updateDirtyState(managedForm);
        }
      }
      
    });
    passwordAuthBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        passwordLabel.setEnabled(passwordAuthBt.getSelection());
        passwordText.setEnabled(passwordAuthBt.getSelection());
        final IConnectionInfo curConnInfo = getCurrentConnectionInfo();
        if (curConnInfo != null) {
          if (curConnInfo.isPasswordBasedAuth() != passwordAuthBt.getSelection()) {
            notifyConnectionUnknownStatus(curConnInfo);
          }
          curConnInfo.setIsPasswordBasedFlag(passwordAuthBt.getSelection());
        }
        for (final Control control : keyFileControls) {
          control.setEnabled(privateKeyFileAuthBt.getSelection());
        }
        handleEmptyTextValidation(privateKeyText, LaunchMessages.RMCP_PrivateKeyFileLabel);
        
        if ((curConnInfo != null) && (ConnectionSectionPart.this.fCurrentConnection == curConnInfo)) {
          updateConnectionConf();
          updateDirtyState(managedForm);
          setPartCompleteFlag(hasCompleteInfo());
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    passwordText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        final IConnectionInfo curConnInfo = getCurrentConnectionInfo();
        if (curConnInfo != null) {
          if (! curConnInfo.getPassword().equals(passwordText.getText().trim())) {
            notifyConnectionUnknownStatus(curConnInfo);
          }
          curConnInfo.setPassword(passwordText.getText().trim());
        }
        if ((curConnInfo != null) && (ConnectionSectionPart.this.fCurrentConnection == curConnInfo)) {
          updateConnectionConf();
          updateDirtyState(managedForm);
        }
      }
      
    });
    privateKeyFileAuthBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        for (final Control control : keyFileControls) {
          control.setEnabled(privateKeyFileAuthBt.getSelection());
        }
        handleEmptyTextValidation(privateKeyText, LaunchMessages.RMCP_PrivateKeyFileLabel);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    privateKeyText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        final IConnectionInfo curConnInfo = getCurrentConnectionInfo();
        if (curConnInfo != null) {
          if (! curConnInfo.getPrivateKeyFile().equals(privateKeyText.getText().trim())) {
            notifyConnectionUnknownStatus(curConnInfo);
          }
          curConnInfo.setPrivateKeyFile(privateKeyText.getText().trim());
        }
        handleEmptyTextValidation(privateKeyText, LaunchMessages.RMCP_PrivateKeyFileLabel);
        if ((curConnInfo != null) && (ConnectionSectionPart.this.fCurrentConnection == curConnInfo)) {
          setPartCompleteFlag(hasCompleteInfo());
          updateConnectionConf();
          updateDirtyState(managedForm);
        }
      }
      
    });
    browseBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        final FileDialog fileDialog = new FileDialog(getFormPage().getSite().getShell(), SWT.OPEN);
        fileDialog.setText(LaunchMessages.RMCP_SelectPrivateKeyFileDialogTitle);
        final String filPath = fileDialog.open();
        if (filPath != null) {
          privateKeyText.setText(filPath);
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    passphraseText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        final IConnectionInfo curConnInfo = getCurrentConnectionInfo();
        if (curConnInfo != null) {
          if (! curConnInfo.getPassphrase().equals(passphraseText.getText().trim())) {
            notifyConnectionUnknownStatus(curConnInfo);
          }
          curConnInfo.setPassphrase(passphraseText.getText().trim());
        }
        if ((curConnInfo != null) && (ConnectionSectionPart.this.fCurrentConnection == curConnInfo)) {
          updateConnectionConf();
          updateDirtyState(managedForm);
        }
      }
      
    });
    portForwarding.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        getPlatformConf().setShouldUsePortForwarding(portForwarding.getSelection());
        for (final Control control : firstGroupControls) {
          control.setEnabled(true);
        }
        final IConnectionInfo curConnInfo = ConnectionSectionPart.this.fCurrentConnection;
        if (curConnInfo != null) {
          notifyConnectionUnknownStatus(curConnInfo);
          fillConnectionControlsInfo(curConnInfo);
          updateConnectionConf();
        }
        
        for (final Control localAddressControl : localAddressControls) {
          localAddressControl.setEnabled(portForwarding.getSelection());
        }
        
        setPartCompleteFlag(hasCompleteInfo());
        updateDirtyState(managedForm);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    localAddress.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        getPlatformConf().setLocalAddress(localAddress.getText().trim());

        final IConnectionInfo curConnInfo = ConnectionSectionPart.this.fCurrentConnection;
        if (curConnInfo != null) {
          notifyConnectionUnknownStatus(curConnInfo);
          fillConnectionControlsInfo(curConnInfo);
          updateConnectionConf();
        }
        
        setPartCompleteFlag(hasCompleteInfo());
        updateDirtyState(managedForm);
      }
      
    });
    connectionTimeoutSpinner.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        getPlatformConf().setConnectionTimeout(connectionTimeoutSpinner.getSelection());

        final IConnectionInfo curConnInfo = ConnectionSectionPart.this.fCurrentConnection;
        if (curConnInfo != null) {
          curConnInfo.setConnectionTimeout(connectionTimeoutSpinner.getSelection());
          
          if (curConnInfo.getConnectionTimeout() != connectionTimeoutSpinner.getSelection()) {
            notifyConnectionUnknownStatus(curConnInfo);
            fillConnectionControlsInfo(curConnInfo);
          }
          updateConnectionConf();
        }
        
        setPartCompleteFlag(hasCompleteInfo());
        updateDirtyState(managedForm);
      }
      
    });
  }
  
  private void createClient(final IManagedForm managedForm, final FormToolkit toolkit) {
    final Composite sectionClient = toolkit.createComposite(getSection());
    sectionClient.setLayout(new TableWrapLayout());
    sectionClient.setFont(getSection().getFont());
    sectionClient.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    
    final Collection<Control> firstGroupControls = new ArrayList<Control>();
    final Collection<Control> secondGroupControls = new ArrayList<Control>();
    
    this.fLocalConnBt = toolkit.createButton(sectionClient, LaunchMessages.RMCP_LocalConnBt, SWT.RADIO);
    
    this.fRemoteConnBt = toolkit.createButton(sectionClient, LaunchMessages.RMCP_RemoteConnBt, SWT.RADIO);
    
    final Composite marginCompo = toolkit.createComposite(sectionClient, SWT.NONE);
    marginCompo.setFont(sectionClient.getFont());
    marginCompo.setLayout(new TableWrapLayout());
    final TableWrapData gd = new TableWrapData(TableWrapData.FILL_GRAB);
    gd.indent = 20;
    marginCompo.setLayoutData(gd);
        
    final Group groupCompo = new Group(marginCompo, SWT.NONE);
    groupCompo.setFont(marginCompo.getFont());
    groupCompo.setLayout(new TableWrapLayout());
    groupCompo.setText(LaunchMessages.RMCP_RemoteConnGroupName);
    groupCompo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

    final Composite connNameCompo = toolkit.createComposite(groupCompo, SWT.NONE);
    connNameCompo.setFont(groupCompo.getFont());
    final TableWrapLayout connNameLayout = new TableWrapLayout();
    connNameLayout.numColumns = 2;
    connNameCompo.setLayout(connNameLayout);
    connNameCompo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    
    createConnectionsTable(firstGroupControls, secondGroupControls, connNameCompo, toolkit);
    
    this.fErrorLabel = toolkit.createLabel(groupCompo, null, SWT.WRAP);
    final TableWrapData twData = new TableWrapData(TableWrapData.FILL_GRAB);
    twData.heightHint = groupCompo.getFont().getFontData()[0].getHeight() * 4;
    this.fErrorLabel.setLayoutData(twData);
    this.fErrorLabel.setForeground(getFormPage().getSite().getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
    
    final Label separator = new Label(groupCompo, SWT.SEPARATOR | SWT.SHADOW_OUT | SWT.HORIZONTAL);
    separator.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    
    final Composite hostPortCompo = toolkit.createComposite(groupCompo, SWT.NONE);
    hostPortCompo.setFont(groupCompo.getFont());
    final TableWrapLayout hostLayout = new TableWrapLayout();
    hostLayout.numColumns = 4;
    hostPortCompo.setLayout(hostLayout);
    hostPortCompo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    
    this.fHostText = SWTFormUtils.createLabelAndText(hostPortCompo, LaunchMessages.RMCP_HostLabel, toolkit, 
                                                     secondGroupControls);
    
    final Label portLabel = toolkit.createLabel(hostPortCompo, LaunchMessages.RMCP_PortLabel, SWT.WRAP);
    portLabel.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.MIDDLE));
    secondGroupControls.add(portLabel);
    this.fPortText = new Spinner(hostPortCompo, SWT.SINGLE | SWT.BORDER);
    this.fPortText.setMinimum(0);
    this.fPortText.setSelection(22);
    this.fPortText.setTextLimit(10);
    this.fPortText.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.MIDDLE));
    secondGroupControls.add(this.fPortText);
    
    this.fUserNameText = SWTFormUtils.createLabelAndText(groupCompo, LaunchMessages.RMCP_UserLabel, toolkit, 
                                                         secondGroupControls);
    
    this.fPasswordAuthBt = toolkit.createButton(groupCompo, LaunchMessages.RMCP_PasswordBasedAuthBt, SWT.RADIO);
    this.fPasswordAuthBt.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    secondGroupControls.add(this.fPasswordAuthBt);
    
    final Composite passwordCompo = toolkit.createComposite(groupCompo);
    passwordCompo.setFont(getSection().getFont());
    final TableWrapLayout tableWrapLayout = new TableWrapLayout();
    tableWrapLayout.numColumns = 2;
    passwordCompo.setLayout(tableWrapLayout);
    passwordCompo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    
    this.fPasswordLabel = toolkit.createLabel(passwordCompo, LaunchMessages.RMCP_PasswordLabel, SWT.WRAP);
    this.fPasswordLabel.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.MIDDLE));
    secondGroupControls.add(this.fPasswordLabel);
    this.fPasswordText = toolkit.createText(passwordCompo, null /* value */, SWT.PASSWORD);
    this.fPasswordText.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    secondGroupControls.add(this.fPasswordText);

    this.fPrivateKeyFileAuthBt = toolkit.createButton(groupCompo, LaunchMessages.RMCP_PublickKeyAuthBt, SWT.RADIO);
    this.fPrivateKeyFileAuthBt.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    secondGroupControls.add(this.fPrivateKeyFileAuthBt);
    
    final Collection<Control> keyFileControls = new ArrayList<Control>();
    final Pair<Text, Button> pair = SWTFormUtils.createLabelTextButton(groupCompo, LaunchMessages.RMCP_PrivateKeyFileLabel, 
                                                                       LaunchMessages.XPCP_BrowseBt, toolkit, 
                                                                       keyFileControls);
    this.fPrivateKeyFileText = pair.first;
    final Button browseBt = pair.second;
    
    this.fPassphraseText = SWTFormUtils.createLabelAndText(groupCompo, LaunchMessages.RMCP_PassphraseLabel, toolkit, 
                                                           keyFileControls);
    
    final Label separator2 = new Label(groupCompo, SWT.SEPARATOR | SWT.SHADOW_OUT | SWT.HORIZONTAL);
    separator2.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    
    
    final Composite timeoutPortFwdCompo = toolkit.createComposite(groupCompo);
    timeoutPortFwdCompo.setFont(getSection().getFont());
    final TableWrapLayout timeoutWLayout = new TableWrapLayout();
    timeoutWLayout.numColumns = 3;
    timeoutPortFwdCompo.setLayout(timeoutWLayout);
    timeoutPortFwdCompo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    
    this.fUsePortForwardingBt = toolkit.createButton(timeoutPortFwdCompo, LaunchMessages.CSP_UsePortFwrd, SWT.CHECK);
    secondGroupControls.add(this.fUsePortForwardingBt);
    this.fUsePortForwardingBt.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.MIDDLE));
    final Label timeoutLabel = toolkit.createLabel(timeoutPortFwdCompo, LaunchMessages.CSP_ConnTimeout);
    timeoutLabel.setLayoutData(new TableWrapData(TableWrapData.RIGHT, TableWrapData.MIDDLE));
    secondGroupControls.add(timeoutLabel);
    
    this.fConnectionTimeoutSpinner = new Spinner(timeoutPortFwdCompo, SWT.SINGLE | SWT.BORDER);
    this.fConnectionTimeoutSpinner.setMinimum(0);
    this.fConnectionTimeoutSpinner.setTextLimit(5);
    this.fConnectionTimeoutSpinner.setLayoutData(new TableWrapData(TableWrapData.RIGHT, TableWrapData.MIDDLE));
    secondGroupControls.add(this.fConnectionTimeoutSpinner);
    
    final Collection<Control> localAddressControls = new ArrayList<Control>();
    this.fLocalAddressText = SWTFormUtils.createLabelAndText(groupCompo, LaunchMessages.CSP_LocalAddress, toolkit, 
                                                             localAddressControls);
    secondGroupControls.addAll(localAddressControls);

    final Label infoLabel = new Label(marginCompo, SWT.WRAP);
    infoLabel.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    infoLabel.setText(LaunchMessages.RMCP_RemoteConnDataInfo);
    infoLabel.setForeground(getFormPage().getSite().getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
    
    final Collection<IConnectionInfo> tableInput = new ArrayList<IConnectionInfo>();
    for (final ITargetElement targetElement : PTPConfUtils.getTargetElements()) {
      tableInput.add(new TargetBasedConnectionInfo(targetElement));
    }
    this.fTableViewer.setInput(tableInput);

    initializeControls(firstGroupControls, secondGroupControls, keyFileControls, tableInput);
    
    addListeners(managedForm, this.fLocalConnBt, this.fRemoteConnBt, this.fValidateButton,
                 this.fHostText, this.fPortText, this.fUserNameText, this.fPasswordAuthBt, this.fPasswordLabel, 
                 this.fPasswordText, this.fPrivateKeyFileAuthBt, this.fPrivateKeyFileText, browseBt, this.fPassphraseText,
                 firstGroupControls, this.fUsePortForwardingBt, this.fLocalAddressText, this.fConnectionTimeoutSpinner,
                 secondGroupControls, keyFileControls, localAddressControls);
    
    getSection().setClient(sectionClient);
  }
  
  private void createConnectionsTable(final Collection<Control> firstGroupControls, final Collection<Control> sndGroupControls,
                                      final Composite parent, final FormToolkit toolkit) {
    final TableViewer tableViewer = new TableViewer(parent, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL |
                                                    SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
    this.fTableViewer = tableViewer;
    
    tableViewer.setContentProvider(new IStructuredContentProvider() {
      
      public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
      }
      
      public void dispose() {
      }
      
      @SuppressWarnings("unchecked")
      public Object[] getElements(final Object inputElement) {
        return ((Collection<IConnectionInfo>) inputElement).toArray();
      }
      
    });

    final Button validateButton = new Button(parent, SWT.PUSH);
    this.fValidateButton = validateButton;
    validateButton.setFont(parent.getFont());
    validateButton.setLayoutData(new TableWrapData(TableWrapData.FILL));
    validateButton.setText(LaunchMessages.RMCP_ValidateBt);
    validateButton.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
        final IConnectionInfo currentConnection = (IConnectionInfo) selection.iterator().next();
        currentConnection.setValidationStatus(EValidationStatus.UNKNOWN);
        tableViewer.update(currentConnection, null);
        
        ConnectionSectionPart.this.fCurrentConnection = currentConnection;
        updateConnectionConf();
        for (final TableItem tableItem : tableViewer.getTable().getItems()) {
          tableViewer.update(tableItem.getData(), null);
        }
        validateRemoteHostConnection(currentConnection, true);
        setPartCompleteFlag(hasCompleteInfo());
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    validateButton.setEnabled(false);
    
    new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(new TableWrapData(TableWrapData.FILL));
        
    final Button addButton = new Button(parent, SWT.PUSH);
    addButton.setFont(parent.getFont());
    addButton.setLayoutData(new TableWrapData(TableWrapData.FILL));
    addButton.setText(LaunchMessages.RMCP_AddBt);
    addButton.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        final IConnectionInfo connectionInfo = new DefaultConnectionInfo();
        tableViewer.add(connectionInfo);
        tableViewer.getTable().setTopIndex(tableViewer.getTable().getItemCount());
        tableViewer.getTable().select(tableViewer.getTable().getItemCount() - 1);
        tableViewer.editElement(connectionInfo, 1);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    firstGroupControls.add(addButton);
    
    final Button removeButton = new Button(parent, SWT.PUSH);
    removeButton.setFont(parent.getFont());
    removeButton.setLayoutData(new TableWrapData(TableWrapData.FILL));
    removeButton.setText(LaunchMessages.RMCP_RemoteBt);
    removeButton.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
        if (! selection.isEmpty()) {
          final IConnectionInfo elementToRemove = (IConnectionInfo) selection.iterator().next();
          PTPConfUtils.removeTargetElement(elementToRemove.getName());
          tableViewer.remove(elementToRemove);
          if (tableViewer.getTable().getItemCount() > 0) {
        	  if (elementToRemove == ConnectionSectionPart.this.fCurrentConnection) {
        		  final Object lastElement = tableViewer.getElementAt(tableViewer.getTable().getItemCount() - 1);
        		  ConnectionSectionPart.this.fCurrentConnection = (IConnectionInfo) lastElement;
        		  updateConnectionConf();
        		  for (final IConnectionTypeListener listener : ConnectionSectionPart.this.fConnectionTypeListeners) {
                    listener.connectionChanged(false, ConnectionSectionPart.this.fCurrentConnection.getName(), 
                    						   ConnectionSectionPart.this.fCurrentConnection.getValidationStatus(), true);
        		  }
        	  }
        	  tableViewer.getTable().select(tableViewer.getTable().getItemCount() - 1);
        	  tableViewer.getTable().notifyListeners(SWT.Selection, new Event());
          }
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    firstGroupControls.add(removeButton);

    final TableLayout tableLayout = new TableLayout();
    tableLayout.addColumnData(new ColumnWeightData(15, 50));
    tableLayout.addColumnData(new ColumnWeightData(70, 100));
    tableLayout.addColumnData(new ColumnWeightData(15, 45));
    tableViewer.getTable().setLayout(tableLayout);
    final TableWrapData twData = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB);
    twData.rowspan = 4;
    tableViewer.getTable().setLayoutData(twData);
    toolkit.adapt(tableViewer.getTable(), false, false);
    
    final Image rcDisabledImg = LaunchImages.createUnmanaged(LaunchImages.RM_STOPPED).createImage();
    final Image rcInvalidImg = LaunchImages.createUnmanaged(LaunchImages.RM_ERROR).createImage();
    final Image rcValidImg = LaunchImages.createUnmanaged(LaunchImages.RM_STARTED).createImage();
    final Image curConnImg = CppLaunchImages.createUnmanaged(CppLaunchImages.CUR_CONNECTION).createImage();
    
    tableViewer.getTable().addListener(SWT.MeasureItem, new Listener() {
      
      public void handleEvent(final Event event) {
        event.height = rcDisabledImg.getBounds().height + 5;
      }
      
    });
    
    final TableViewerColumn currentConfColumn = new TableViewerColumn(tableViewer, SWT.NONE);
    currentConfColumn.getColumn().setText(LaunchMessages.RMCP_CurrentColumn);
    currentConfColumn.setLabelProvider(new CenterImageLabelProvider() {

      protected Image getImage(final Object element) {
        if (ConnectionSectionPart.this.fCurrentConnection == element) {
          return curConnImg;
        } else {
          return null;
        }
      }
      
    });
    
    final CheckboxCellEditor currentFlagEditor = new CheckboxCellEditor(tableViewer.getTable(), SWT.CHECK | SWT.READ_ONLY);
    currentConfColumn.setEditingSupport(new EditingSupport(tableViewer) {
      
      protected void setValue(final Object element, final Object value) {
        final IConnectionInfo curConnInfo = (IConnectionInfo) element;
        ConnectionSectionPart.this.fCurrentConnection = curConnInfo;
        for (final TableItem tableItem : tableViewer.getTable().getItems()) {
          tableViewer.update(tableItem.getData(), null);
        }
        validateRemoteHostConnection(curConnInfo, true);
        setPartCompleteFlag(hasCompleteInfo());
      }
      
      protected Object getValue(final Object element) {
        return (element == ConnectionSectionPart.this.fCurrentConnection) ? Boolean.TRUE : Boolean.FALSE;
      }
      
      protected CellEditor getCellEditor(final Object element) {
        return currentFlagEditor;
      }
      
      protected boolean canEdit(final Object element) {
        return true;
      }
      
    });
    
    final TableViewerColumn confNameColumn = new TableViewerColumn(tableViewer, SWT.NONE);
    confNameColumn.getColumn().setText(LaunchMessages.RMCP_NameColumn);
    confNameColumn.setLabelProvider(new ColumnLabelProvider() {
      
      public String getText(final Object element) {
        return ((IConnectionInfo) element).getName();
      }
      
    });
    final TextCellEditor confNameEditor = new TextCellEditor(tableViewer.getTable());
    confNameColumn.setEditingSupport(new EditingSupport(tableViewer) {
      
      protected void setValue(final Object element, final Object value) {
        String connectionName = (String) value;
        final IConnectionInfo curConnInfo = (IConnectionInfo) element;
        
        for (final TableItem tableItem : tableViewer.getTable().getItems()) {
          final IConnectionInfo connInfo = (IConnectionInfo) tableItem.getData();
          if (connInfo != curConnInfo) {
            if (connInfo.getName().equals(connectionName)) {
              connectionName = Constants.EMPTY_STR;
              break;
            }
          }
        }
        
        curConnInfo.setName(connectionName);
        tableViewer.update(element, null);
        
        final boolean shouldEnableControls = connectionName.length() > 0;
        for (final Control control : sndGroupControls) {
          control.setEnabled(shouldEnableControls);
        }
        
        if (connectionName.length() == 0) {
          resetConnectionControlsInfo();
        }
      }
      
      protected Object getValue(final Object element) {
        return ((IConnectionInfo) element).getName();
      }
      
      protected CellEditor getCellEditor(final Object element) {
        return confNameEditor;
      }
      
      protected boolean canEdit(final Object element) {
        return true;
      }
      
    });
    
    final TableViewerColumn statusColumn = new TableViewerColumn(tableViewer, SWT.NONE);
    statusColumn.getColumn().setText(LaunchMessages.RMCP_StatusColumn);
    statusColumn.setLabelProvider(new CenterImageLabelProvider() {

      protected Image getImage(final Object element) {
        switch (((IConnectionInfo) element).getValidationStatus()) {
          case ERROR:
          case FAILURE:
            return rcInvalidImg;
          case VALID:
            return rcValidImg;
          default:
            return rcDisabledImg;
        }
      }
      
    });
    
    tableViewer.getTable().setLinesVisible(true);
    tableViewer.getTable().setHeaderVisible(true);
    
    tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      
      public void selectionChanged(final SelectionChangedEvent event) {
        if (event.getSelection().isEmpty()) {
          for (final Control control : sndGroupControls) {
            control.setEnabled(false);
          }
          resetConnectionControlsInfo();
          validateButton.setEnabled(false);
        } else {
          if (! validateButton.isEnabled()) {
            validateButton.setEnabled(true);
          }
          final Object newSelection = ((IStructuredSelection) event.getSelection()).iterator().next();
          final IConnectionInfo connectionInfo = (IConnectionInfo) newSelection;
          
          if ((connectionInfo.getValidationStatus() == EValidationStatus.FAILURE) ||
              (connectionInfo.getValidationStatus() == EValidationStatus.ERROR)) {
            ConnectionSectionPart.this.fErrorLabel.setText(connectionInfo.getErrorMessage());
          } else {
            ConnectionSectionPart.this.fErrorLabel.setText(Constants.EMPTY_STR);
          }
          
          for (final Control control : sndGroupControls) {
            control.setEnabled(true);
          }
          
          if (connectionInfo.getName().length() == 0) {
            resetConnectionControlsInfo();
          } else {
            fillConnectionControlsInfo(connectionInfo);
          }
        }
      }
      
    });
    
    firstGroupControls.add(tableViewer.getTable());
  }
  
  private void fillConnectionControlsInfo(final IConnectionInfo connectionInfo) {
    this.fHostText.setText(connectionInfo.getHostName());
    this.fPortText.setSelection(connectionInfo.getPort());
    this.fUserNameText.setText(connectionInfo.getUserName());
    this.fPasswordAuthBt.setSelection(connectionInfo.isPasswordBasedAuth());
    this.fPasswordAuthBt.notifyListeners(SWT.Selection, new Event());
    this.fPrivateKeyFileAuthBt.setSelection(! connectionInfo.isPasswordBasedAuth());
    this.fPrivateKeyFileAuthBt.notifyListeners(SWT.Selection, new Event());
    if (connectionInfo.isPasswordBasedAuth()) {
      this.fPasswordText.setText(connectionInfo.getPassword());
    } else {
      this.fPrivateKeyFileText.setText(connectionInfo.getPrivateKeyFile());
      this.fPassphraseText.setText(connectionInfo.getPassphrase());
    }
    this.fConnectionTimeoutSpinner.setSelection(connectionInfo.getConnectionTimeout());
  }
  
  private Iterable<IConnectionInfo> getAllConnectionInfo() {
    final Collection<IConnectionInfo> connectionsInfo = new ArrayList<IConnectionInfo>();
    for (final TableItem tableItem : this.fTableViewer.getTable().getItems()) {
      connectionsInfo.add((IConnectionInfo) tableItem.getData());
    }
    return connectionsInfo;
  }
  
  private IConnectionInfo getCurrentConnectionInfo() {
    final IStructuredSelection selection = (IStructuredSelection) this.fTableViewer.getSelection();
    if (selection.isEmpty()) {
      return null;
    } else {
      return (IConnectionInfo) selection.iterator().next();
    }
  }
  
  private boolean hasCompleteInfo() {
    if (this.fLocalConnBt.getSelection()) {
      return true;
    } else {
      if (this.fCurrentConnection == null) {
        return false;
      }
      if (this.fCurrentConnection.getName().length() == 0) {
        return false;
      }
      if (this.fCurrentConnection.getHostName().length() == 0) {
        return false;
      }
      if (this.fCurrentConnection.getUserName().length() == 0) {
        return false;
      }
      if (this.fCurrentConnection.isPasswordBasedAuth()) {
        return true;
      } else {
        return this.fCurrentConnection.getPrivateKeyFile().length() > 0;
      }
    }
  }
  
  private void initializeControls(final Collection<Control> firstGroupControls, final Collection<Control> secondGroupControls,
                                  final Collection<Control> keyFileControls, final Collection<IConnectionInfo> tableInput) {
    final IConnectionConf connectionConf = getPlatformConf().getConnectionConf();
    if (connectionConf.isLocal()) {
      this.fLocalConnBt.setSelection(true);
      this.fRemoteConnBt.setSelection(false);
      for (final Control control : firstGroupControls) {
        control.setEnabled(false);
      }
      for (final Control control : secondGroupControls) {
        control.setEnabled(false);
      }
      for (final Control keyFileControl : keyFileControls) {
        keyFileControl.setEnabled(false);
      }
    } else {
      this.fRemoteConnBt.setSelection(true);
      this.fLocalConnBt.setSelection(false);
    }
    
    int index = -1;
    boolean found = false;
    for (final IConnectionInfo connectionInfo : tableInput) {
      ++index;
      validateRemoteHostConnection(connectionInfo, false);
      if (connectionConf.hasSameConnectionInfo(connectionInfo.getTargetElement())) {
        this.fCurrentConnection = connectionInfo;
        fillConnectionControlsInfo(connectionInfo);
        found = true;
      }
    }
    if (! tableInput.isEmpty() && ! found) { // We have not found an equivalent, let's select the first one.
      this.fCurrentConnection = tableInput.iterator().next();
      fillConnectionControlsInfo(this.fCurrentConnection);
    }
    
    KeyboardUtils.addDelayedActionOnControl(this.fPrivateKeyFileText, new Runnable() {
      
      public void run() {
        getFormPage().getEditorSite().getShell().getDisplay().asyncExec(new Runnable() {
          
          public void run() {
            handleLocalPathValidation(ConnectionSectionPart.this.fPrivateKeyFileText, LaunchMessages.RMCP_PrivateKeyFileLabel);
          }
          
        });
      }
      
    });
    
    this.fLocalAddressText.setText(connectionConf.getLocalAddress());
    this.fUsePortForwardingBt.setSelection(connectionConf.shouldUsePortForwarding());
  }
  
  private void notifyConnectionUnknownStatus(final IConnectionInfo connectionInfo) {
    if (this.fCurrentConnection == connectionInfo) {
      for (final IX10PlatformValidationListener listener : this.fValidationListeners) {
        listener.remoteConnectionUnknownStatus();
      }      
    }
    new ConnectionInfoValidationListener(connectionInfo, true).remoteConnectionUnknownStatus();
  }
  
  private void resetConnectionControlsInfo() {
    this.fHostText.setText(Constants.EMPTY_STR);
    this.fPortText.setSelection(22);
    this.fUserNameText.setText(Constants.EMPTY_STR);
    this.fPasswordAuthBt.setSelection(true);
    this.fPrivateKeyFileAuthBt.setSelection(false);
    this.fPrivateKeyFileAuthBt.notifyListeners(SWT.Selection, new Event());
    this.fPasswordText.setText(Constants.EMPTY_STR);
    this.fPrivateKeyFileText.setText(Constants.EMPTY_STR);
    this.fPassphraseText.setText(Constants.EMPTY_STR);
    this.fConnectionTimeoutSpinner.setSelection(5);
  }
  
  private void updateConnectionConf() {
    getPlatformConf().setConnectionName(this.fCurrentConnection.getName());
    getPlatformConf().setHostName(this.fCurrentConnection.getHostName());
    getPlatformConf().setPort(this.fCurrentConnection.getPort());
    getPlatformConf().setUserName(this.fCurrentConnection.getUserName());
    getPlatformConf().setIsPasswordBasedAuthenticationFlag(this.fCurrentConnection.isPasswordBasedAuth());
    if (this.fCurrentConnection.isPasswordBasedAuth()) {
      getPlatformConf().setPassword(this.fCurrentConnection.getPassword());
    } else {
      getPlatformConf().setPrivateKeyFile(this.fCurrentConnection.getPrivateKeyFile());
      getPlatformConf().setPassphrase(this.fCurrentConnection.getPassphrase());
    }
  }
  
  private void validateRemoteHostConnection(final IConnectionInfo connectionInfo, final boolean shouldNotifyChanges) {
  	if (connectionInfo == this.fCurrentConnection) {
  		CoreResourceUtils.deletePlatformConfMarkers(((IFileEditorInput) getFormPage().getEditorInput()).getFile());
  	}
  	
    final IX10PlatformValidationListener validationListener = new ConnectionInfoValidationListener(connectionInfo, 
                                                                                                   shouldNotifyChanges);
    final IX10PlatformChecker checker = PlatformCheckerFactory.create();
    checker.addValidationListener(validationListener);
    if (this.fCurrentConnection == connectionInfo) {
      for (final IX10PlatformValidationListener listener : this.fValidationListeners) {
        checker.addValidationListener(listener);
      }
    }
    try {
      final ITargetElement targetElement;
      if (connectionInfo.getTargetElement() == null) {
        targetElement = PTPConfUtils.createRemoteConnection(connectionInfo.getName(), connectionInfo.getPTPAttributes());
      } else {
        connectionInfo.applyChangesToTargetElement();
        targetElement = connectionInfo.getTargetElement();
      }

      final Job job = new Job(LaunchMessages.RMCP_ValidateRemoteConn) {

        protected IStatus run(final IProgressMonitor monitor) {
          checker.validateRemoteConnectionConf(targetElement, monitor);

          checker.removeValidationListener(validationListener);
          if (ConnectionSectionPart.this.fCurrentConnection == connectionInfo) {
            for (final IX10PlatformValidationListener listener : ConnectionSectionPart.this.fValidationListeners) {
              checker.removeValidationListener(listener);
            }
          }

          return Status.OK_STATUS;
        }

      };
      job.setPriority(Job.INTERACTIVE);
      job.schedule();
    } catch (RemoteConnectionException except) {
      DialogsFactory.createErrorBuilder().setDetailedMessage(except)
                    .createAndOpen(getFormPage().getEditorSite(), LaunchMessages.RMCP_RemoteConnErrorTitle, 
                                   LaunchMessages.RMCP_RemoteConnErrorMsg);
    } catch (CoreException except) {
      DialogsFactory.createErrorBuilder().setDetailedMessage(except.getStatus())
                    .createAndOpen(getFormPage().getEditorSite(), LaunchMessages.RMCP_RemoteConnErrorTitle,
                                   LaunchMessages.RMCP_RemoteConnErrorMsg);
    }
  }
  
  // --- Private classes
  
  private abstract class CenterImageLabelProvider extends OwnerDrawLabelProvider {
    
    // --- Abstract methods definition
    
    protected abstract Image getImage(final Object element);
    
    // --- Abstract methods implementation

    protected void measure(final Event event, final Object element) {
      event.height = (int) (event.gc.getFontMetrics().getHeight() * 1.5);
    }

    protected void paint(final Event event, final Object element) {
      final Image image = getImage(element);

      if (image != null) {
        final Rectangle bounds = ((TableItem) event.item).getBounds(event.index);
        final Rectangle imgBounds = image.getBounds();
        bounds.width /= 2;
        bounds.width -= imgBounds.width / 2;
        bounds.height /= 2;
        bounds.height -= imgBounds.height / 2;

        final int x = bounds.width > 0 ? bounds.x + bounds.width : bounds.x;
        final int y = bounds.height > 0 ? bounds.y + bounds.height : bounds.y;

        event.gc.drawImage(image, x, y);
      }
    }
    
  }
  
  private final class ConnectionInfoValidationListener implements IX10PlatformValidationListener {
    
    ConnectionInfoValidationListener(final IConnectionInfo connectionInfo, final boolean shouldNotifyChanges) {
      this.fConnectionInfo = connectionInfo;
      this.fShouldNotifyChanges = shouldNotifyChanges;
    }
    
    // --- Interface methods implementation
    
    public void platformCommunicationInterfaceValidated() {
    	// Nothing to do.
    }
    
    public void platformCommunicationInterfaceValidationFailure(final String message) {
    	// Nothing to do.
    }
    
    public void platformCppCompilationValidated() {
      // Nothing to do.
    }
    
    public void platformCppCompilationValidationFailure(final String message) {
      // Nothing to do.
    }
    
    public void platformCppCompilationValidationError(final Exception exception) {
      // Nothing to do.
    }
    
    public void remoteConnectionFailure(final Exception exception) {
      getFormPage().getEditor().getSite().getShell().getDisplay().syncExec(new Runnable() {
        
        public void run() {
          ConnectionInfoValidationListener.this.fConnectionInfo.setValidationStatus(EValidationStatus.FAILURE);
          ConnectionInfoValidationListener.this.fConnectionInfo.setErrorMessage(exception.getMessage());
          ConnectionSectionPart.this.fTableViewer.update(ConnectionInfoValidationListener.this.fConnectionInfo, null);
          if (ConnectionInfoValidationListener.this.fShouldNotifyChanges) {
            ConnectionSectionPart.this.fErrorLabel.setText(exception.getMessage());
          }
          notifyConnectionChanged();
        }
        
      });
    }
    
    public void remoteConnectionUnknownStatus() {
      final Display display = getFormPage().getEditor().getSite().getShell().getDisplay();
      if (display.getThread() == Thread.currentThread()) {
        this.fConnectionInfo.setValidationStatus(EValidationStatus.UNKNOWN);
        ConnectionSectionPart.this.fTableViewer.update(this.fConnectionInfo, null);
        if (ConnectionInfoValidationListener.this.fShouldNotifyChanges) {
          ConnectionSectionPart.this.fErrorLabel.setText(Constants.EMPTY_STR);
        }
        notifyConnectionChanged();
      } else {
        display.syncExec(new Runnable() {
          
          public void run() {
            ConnectionInfoValidationListener.this.fConnectionInfo.setValidationStatus(EValidationStatus.UNKNOWN);
            ConnectionSectionPart.this.fTableViewer.update(ConnectionInfoValidationListener.this.fConnectionInfo, null);
            if (ConnectionInfoValidationListener.this.fShouldNotifyChanges) {
              ConnectionSectionPart.this.fErrorLabel.setText(Constants.EMPTY_STR);
            }
            notifyConnectionChanged();
          }
          
        });
      }
    }
    
    public void remoteConnectionValidated(final ITargetElement targetElement) {
      getFormPage().getEditor().getSite().getShell().getDisplay().syncExec(new Runnable() {
        
        public void run() {
          ConnectionInfoValidationListener.this.fConnectionInfo.setValidationStatus(EValidationStatus.VALID);
          ConnectionSectionPart.this.fTableViewer.update(ConnectionInfoValidationListener.this.fConnectionInfo, null);
          ConnectionSectionPart.this.fErrorLabel.setText(Constants.EMPTY_STR);
          notifyConnectionChanged();
        }
        
      });
    }
    
    public void serviceProviderFailure(final CoreException exception) {
    	// Should never occur.
    	CppLaunchCore.log(exception.getStatus());
    }
    
    // --- Private code
    
    private void notifyConnectionChanged() {
      if (this.fShouldNotifyChanges) {
        for (final IConnectionTypeListener listener : ConnectionSectionPart.this.fConnectionTypeListeners) {
          listener.connectionChanged(ConnectionSectionPart.this.fLocalConnBt.getSelection(), 
                                     this.fConnectionInfo.getName(), this.fConnectionInfo.getValidationStatus(), true);
        }
      }
    }
    
    // --- Fields
    
    private final IConnectionInfo fConnectionInfo;
    
    private final boolean fShouldNotifyChanges;
    
  }
  
  // --- Fields
  
  private Button fLocalConnBt;
  
  private Button fRemoteConnBt;
  
  private Text fHostText;
  
  private Spinner fPortText;
  
  private Text fUserNameText;
  
  private Button fPasswordAuthBt;
  
  private Label fPasswordLabel;
  
  private Text fPasswordText;
  
  private Button fPrivateKeyFileAuthBt;
  
  private Text fPrivateKeyFileText;
  
  private Text fPassphraseText;
  
  private Spinner fConnectionTimeoutSpinner;
  
  private Button fUsePortForwardingBt;
  
  private Text fLocalAddressText;
    
  private TableViewer fTableViewer;
  
  private Label fErrorLabel;
  
  private Button fValidateButton;
  
  private IConnectionInfo fCurrentConnection;
     
  
  private final Collection<IConnectionTypeListener> fConnectionTypeListeners;
  
  private final Collection<IX10PlatformValidationListener> fValidationListeners;
  
  private final Collection<IConnectionSwitchListener> fConnectionSwitchListeners;

}