/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.editors;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.imp.utils.Pair;
import org.eclipse.imp.x10dt.ui.launch.core.Constants;
import org.eclipse.imp.x10dt.ui.launch.core.LaunchImages;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EValidationStatus;
import org.eclipse.imp.x10dt.ui.launch.core.utils.SWTFormUtils;
import org.eclipse.imp.x10dt.ui.launch.cpp.CppLaunchImages;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IConnectionConf;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.validation.IX10PlatformChecker;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.validation.IX10PlatformValidationListener;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.validation.PlatformCheckerFactory;
import org.eclipse.imp.x10dt.ui.launch.cpp.utils.PTPConfUtils;
import org.eclipse.jface.layout.TableColumnLayout;
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
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.remote.core.exception.RemoteConnectionException;
import org.eclipse.ptp.remotetools.environment.core.ITargetElement;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;
import org.eclipse.ptp.services.core.IServiceProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
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
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

final class ConnectionSectionPart extends AbstractCommonSectionFormPart implements IFormPart, IServiceConfigurationListener {

  ConnectionSectionPart(final Composite parent, final ConnectionAndCommunicationConfPage formPage,
                        final IX10PlatformConfWorkCopy x10PlatformConf) {
    super(parent, formPage, x10PlatformConf);
    
    getSection().setFont(parent.getFont());
    getSection().setText(LaunchMessages.RMCP_ConnectionSectionTitle);
    getSection().setDescription(LaunchMessages.RMCP_ConnectionSectionDescr);
    getSection().setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    this.fConnectionTypeListeners = new ArrayList<IConnectionTypeListener>();
    this.fValidationListeners = new ArrayList<IX10PlatformValidationListener>();
    
    createClient(formPage.getManagedForm(), formPage.getManagedForm().getToolkit(), x10PlatformConf);
    addCompletePartListener(formPage);
  }
  
  // --- IServiceConfigurationListener's interface methods implementation
  
  public void serviceConfigurationModified(final String textContent) {
  }

  public void serviceConfigurationSelected(final IServiceProvider serviceProvider) {
    if (serviceProvider instanceof IResourceManagerConfiguration) {
      final IResourceManagerConfiguration rmConf = (IResourceManagerConfiguration) serviceProvider;
      final IRemoteServices rmServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(rmConf.getRemoteServicesId());
      final boolean isLocal = PTPRemoteCorePlugin.getDefault().getDefaultServices().equals(rmServices);
     
      if (isLocal) {
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
          if (connectionInfo.getName().equals(rmConf.getConnectionName())) {
            this.fCurrentConnection = connectionInfo;            
            this.fTableViewer.getTable().select(index);
            this.fTableViewer.getTable().notifyListeners(SWT.Selection, new Event());
          }
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
    }
    final IFormPage page = getFormPage().getEditor().findPage(X10CompilationConfigurationPage.X10_COMPILATION_CONF_PAGE_ID);
    if ((page != null) && (page.getManagedForm() != null)) {
      for (final IFormPart formPart : page.getManagedForm().getParts()) {
        if (formPart instanceof IConnectionTypeListener) {
          this.fConnectionTypeListeners.add((IConnectionTypeListener) formPart);
        }
      }
    }
    
    final Collection<IConnectionInfo> tableInput = new ArrayList<IConnectionInfo>();
    for (final ITargetElement targetElement : PTPConfUtils.getTargetElements()) {
      tableInput.add(new TargetBasedConnectionInfo(targetElement));
    }
    this.fTableViewer.setInput(tableInput);
    final IConnectionConf connectionConf = getPlatformConf().getConnectionConf();
    if (! connectionConf.isLocal() && ! tableInput.isEmpty()) {
      int index = -1;
      for (final IConnectionInfo connectionInfo : tableInput) {
        ++index;
        validateRemoteHostConnection(connectionInfo);
        boolean foundConnection = false;
        if (connectionConf.hasSameConnectionInfo(connectionInfo.getTargetElement())) {
          this.fCurrentConnection = connectionInfo;
          
          for (final IConnectionTypeListener listener : ConnectionSectionPart.this.fConnectionTypeListeners) {
            listener.connectionChanged(false, connectionInfo.getName(), connectionInfo.getValidationStatus());
          }
          
          this.fTableViewer.getTable().select(index);
          this.fTableViewer.getTable().notifyListeners(SWT.Selection, new Event());
          foundConnection = true;
        }
        if (! foundConnection) {
//          for (final Control control : secondGroupControls) {
//            control.setEnabled(false);
//          }
//          for (final Control keyFileControl : keyFileControls) {
//            keyFileControl.setEnabled(false);
//          }
        }
      }
    }
    
    return false;
  }
 
  // --- Private code
  
  private void addListeners(final IManagedForm managedForm, final IX10PlatformConfWorkCopy x10PlatformConf,
                            final Button localConnBt, final Button remoteConnBt, final Button validateBt,
                            final Text hostText, final Spinner portText, final Text userNameText, 
                            final Button passwordAuthBt, final Label passwordLabel, final Text passwordText, 
                            final Button privateKeyFileAuthBt, final Text privateKeyText, final Button browseBt, 
                            final Text passphraseText, final Collection<Control> firstGroupControls, 
                            final Collection<Control> secondGroupControls, final Collection<Control> keyFileControls) {
    localConnBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        if (localConnBt.getSelection()) {
          x10PlatformConf.setIsLocalFlag(true);
          for (final IConnectionTypeListener listener : ConnectionSectionPart.this.fConnectionTypeListeners) {
            listener.connectionChanged(true, null, null);
          }
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
          handleTextValidation(new EmptyTextInputChecker(hostText, LaunchMessages.RMCP_HostLabel), managedForm, hostText);
          handleTextValidation(new EmptyTextInputChecker(userNameText, LaunchMessages.RMCP_UserLabel), managedForm, 
                               userNameText);
          handleTextValidation(new EmptyTextInputChecker(privateKeyText, LaunchMessages.RMCP_PrivateKeyFileLabel), managedForm, 
                               privateKeyText);
          
          for (final IConnectionInfo connectionInfo : getAllConnectionInfo()) {
            notifyConnectionUnknownStatus(connectionInfo);
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
          x10PlatformConf.setIsLocalFlag(false);
          for (final Control control : firstGroupControls) {
            control.setEnabled(true);
          }
          final IConnectionInfo curConnInfo = getCurrentConnectionInfo();
          if (curConnInfo != null) {
            for (final Control control : secondGroupControls) {
              control.setEnabled(true);
            }
            fillConnectionControlsInfo(curConnInfo);
          }
          for (final IConnectionTypeListener listener : ConnectionSectionPart.this.fConnectionTypeListeners) {
            listener.connectionChanged(false, (curConnInfo == null) ? null : curConnInfo.getName(),
                                       (curConnInfo == null) ? null : curConnInfo.getValidationStatus());
          }
          for (final IConnectionInfo connectionInfo : getAllConnectionInfo()) {
            validateRemoteHostConnection(connectionInfo);
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
        handleTextValidation(new EmptyTextInputChecker(hostText, LaunchMessages.RMCP_HostLabel), managedForm, hostText);
        if (ConnectionSectionPart.this.fCurrentConnection == curConnInfo) {
          setPartCompleteFlag(hasCompleteInfo());
          updateConnectionConf(x10PlatformConf);
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
        if (ConnectionSectionPart.this.fCurrentConnection == curConnInfo) {
          setPartCompleteFlag(hasCompleteInfo());
          updateConnectionConf(x10PlatformConf);
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
        handleTextValidation(new EmptyTextInputChecker(userNameText, LaunchMessages.RMCP_UserLabel), managedForm, 
                             userNameText);
        if (ConnectionSectionPart.this.fCurrentConnection == curConnInfo) {
          setPartCompleteFlag(hasCompleteInfo());
          updateConnectionConf(x10PlatformConf);
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
        handleTextValidation(new EmptyTextInputChecker(privateKeyText, LaunchMessages.RMCP_PrivateKeyFileLabel), managedForm, 
                             privateKeyText);
        
        if (ConnectionSectionPart.this.fCurrentConnection == curConnInfo) {
          updateConnectionConf(x10PlatformConf);
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
        if (ConnectionSectionPart.this.fCurrentConnection == curConnInfo) {
          updateConnectionConf(x10PlatformConf);
          updateDirtyState(managedForm);
        }
      }
      
    });
    privateKeyFileAuthBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        for (final Control control : keyFileControls) {
          control.setEnabled(privateKeyFileAuthBt.getSelection());
        }
        handleTextValidation(new EmptyTextInputChecker(privateKeyText, LaunchMessages.RMCP_PrivateKeyFileLabel), managedForm, 
                             privateKeyText);
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
        handleTextValidation(new EmptyTextInputChecker(privateKeyText, LaunchMessages.RMCP_PrivateKeyFileLabel), managedForm, 
                             privateKeyText);
        if (ConnectionSectionPart.this.fCurrentConnection == curConnInfo) {
          setPartCompleteFlag(hasCompleteInfo());
          updateConnectionConf(x10PlatformConf);
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
        if (ConnectionSectionPart.this.fCurrentConnection == curConnInfo) {
          updateConnectionConf(x10PlatformConf);
          updateDirtyState(managedForm);
        }
      }
      
    });
  }
  
  private void createClient(final IManagedForm managedForm, final FormToolkit toolkit, 
                            final IX10PlatformConfWorkCopy x10PlatformConf) {
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
    
    createConnectionsTable(firstGroupControls, secondGroupControls, connNameCompo, x10PlatformConf, toolkit);
    
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
        
    final Label infoLabel = new Label(marginCompo, SWT.WRAP);
    infoLabel.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    infoLabel.setText(LaunchMessages.RMCP_RemoteConnDataInfo);
    infoLabel.setForeground(getFormPage().getSite().getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));

    initializeControls(managedForm, firstGroupControls, secondGroupControls, keyFileControls);
    
    addListeners(managedForm, x10PlatformConf, this.fLocalConnBt, this.fRemoteConnBt, this.fValidateButton,
                 this.fHostText, this.fPortText, this.fUserNameText, this.fPasswordAuthBt, this.fPasswordLabel, 
                 this.fPasswordText, this.fPrivateKeyFileAuthBt, this.fPrivateKeyFileText, browseBt, this.fPassphraseText,
                 firstGroupControls, secondGroupControls, keyFileControls);
    
    getSection().setClient(sectionClient);
  }

  private void createConnectionsTable(final Collection<Control> firstGroupControls, final Collection<Control> sndGroupControls,
                                      final Composite parent, final IX10PlatformConfWorkCopy x10PlatformConf,
                                      final FormToolkit toolkit) {
    final Composite tableWrapper = toolkit.createComposite(parent, SWT.NONE);
    tableWrapper.setFont(parent.getFont());
    final TableWrapData tableViewerWrapData = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB);
    tableViewerWrapData.rowspan = 6;
    tableWrapper.setLayoutData(tableViewerWrapData);
    
    final TableViewer tableViewer = new TableViewer(tableWrapper, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | 
                                                    SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
    tableViewer.getTable().setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
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
        validateRemoteHostConnection(currentConnection);
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
        final IConnectionInfo elementToRemove = (IConnectionInfo) selection.iterator().next();
        PTPConfUtils.removeTargetElement(elementToRemove.getName());
        tableViewer.remove(elementToRemove);
        if (tableViewer.getTable().getItemCount() > 0) {
          if (elementToRemove == ConnectionSectionPart.this.fCurrentConnection) {
            final Object lastElement = tableViewer.getElementAt(tableViewer.getTable().getItemCount() - 1);
            ConnectionSectionPart.this.fCurrentConnection = (IConnectionInfo) lastElement;
            for (final IConnectionTypeListener listener : ConnectionSectionPart.this.fConnectionTypeListeners) {
              listener.connectionChanged(false, ConnectionSectionPart.this.fCurrentConnection.getName(), 
                                         ConnectionSectionPart.this.fCurrentConnection.getValidationStatus());
            }
          }
          tableViewer.getTable().select(tableViewer.getTable().getItemCount() - 1);
          tableViewer.getTable().notifyListeners(SWT.Selection, new Event());
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    firstGroupControls.add(removeButton);
    
    new Label(parent, SWT.NONE).setText(Constants.EMPTY_STR);
        
    final TableColumnLayout columnLayout = new TableColumnLayout();
    tableWrapper.setLayout(columnLayout);
    
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
    columnLayout.setColumnData(currentConfColumn.getColumn(), new ColumnWeightData(10, 50));
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
        updateConnectionConf(x10PlatformConf);
        for (final TableItem tableItem : tableViewer.getTable().getItems()) {
          tableViewer.update(tableItem.getData(), null);
        }
        for (final IConnectionTypeListener listener : ConnectionSectionPart.this.fConnectionTypeListeners) {
          listener.connectionChanged(false, curConnInfo.getName(), curConnInfo.getValidationStatus());
        }
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
    columnLayout.setColumnData(confNameColumn.getColumn(), new ColumnWeightData(80));
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
    columnLayout.setColumnData(statusColumn.getColumn(), new ColumnWeightData(10, 45));
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
          final Object curSelection = ((IStructuredSelection) event.getSelection()).iterator().next();
          final IConnectionInfo connectionInfo = (IConnectionInfo) curSelection;
          
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
  
  private void initializeControls(final IManagedForm managedForm, final Collection<Control> firstGroupControls,
                                  final Collection<Control> secondGroupControls, final Collection<Control> keyFileControls) {
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
    
    if (isPartComplete() && this.fRemoteConnBt.getSelection()) {
      validateRemoteHostConnection(this.fCurrentConnection);
    }
  }
  
  private void notifyConnectionUnknownStatus(final IConnectionInfo connectionInfo) {
    if (this.fCurrentConnection == connectionInfo) {
      for (final IX10PlatformValidationListener listener : this.fValidationListeners) {
        listener.remoteConnectionUnknownStatus();
      }      
    }
    new ConnectionInfoValidationListener(connectionInfo).remoteConnectionUnknownStatus();
  }
  
  private void resetConnectionControlsInfo() {
    this.fHostText.setText(Constants.EMPTY_STR);
    this.fPortText.setSelection(22);
    this.fUserNameText.setText(Constants.EMPTY_STR);
    this.fPasswordAuthBt.setSelection(true);
    this.fPrivateKeyFileAuthBt.setSelection(false);
    this.fPasswordText.setText(Constants.EMPTY_STR);
    this.fPrivateKeyFileText.setText(Constants.EMPTY_STR);
    this.fPassphraseText.setText(Constants.EMPTY_STR);
  }
  
  private void updateConnectionConf(final IX10PlatformConfWorkCopy x10PlatformConf) {
    x10PlatformConf.setConnectionName(this.fCurrentConnection.getName());
    x10PlatformConf.setHostName(this.fCurrentConnection.getHostName());
    x10PlatformConf.setPort(this.fCurrentConnection.getPort());
    x10PlatformConf.setUserName(this.fCurrentConnection.getUserName());
    x10PlatformConf.setIsPasswordBasedAuthenticationFlag(this.fCurrentConnection.isPasswordBasedAuth());
    if (this.fCurrentConnection.isPasswordBasedAuth()) {
      x10PlatformConf.setPassword(this.fCurrentConnection.getPassword());
    } else {
      x10PlatformConf.setPrivateKeyFile(this.fCurrentConnection.getPrivateKeyFile());
      x10PlatformConf.setPassphrase(this.fCurrentConnection.getPassphrase());
    }
  }
  
  private void validateRemoteHostConnection(final IConnectionInfo connectionInfo) {
    final IX10PlatformValidationListener validationListener = new ConnectionInfoValidationListener(connectionInfo);
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
        except.printStackTrace();
      } catch (CoreException except) {
        except.printStackTrace();
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
    
    ConnectionInfoValidationListener(final IConnectionInfo connectionInfo) {
      this.fConnectionInfo = connectionInfo;
    }
    
    // --- Interface methods implementation
    
    public void platformValidated() {
      // Nothing to do.
    }
    
    public void platformValidationFailure(final String message) {
      // Nothing to do.
    }
    
    public void platformValidationError(final Exception exception) {
      // Nothing to do.
    }
    
    public void remoteConnectionFailure(final Exception exception) {
      getFormPage().getEditor().getSite().getShell().getDisplay().syncExec(new Runnable() {
        
        public void run() {
          ConnectionInfoValidationListener.this.fConnectionInfo.setValidationStatus(EValidationStatus.FAILURE);
          ConnectionInfoValidationListener.this.fConnectionInfo.setErrorMessage(exception.getMessage());
          ConnectionSectionPart.this.fTableViewer.update(ConnectionInfoValidationListener.this.fConnectionInfo, null);
          notifyConnectionChanged();
        }
        
      });
    }
    
    public void remoteConnectionUnknownStatus() {
      final Display display = getFormPage().getEditor().getSite().getShell().getDisplay();
      if (display.getThread() == Thread.currentThread()) {
        this.fConnectionInfo.setValidationStatus(EValidationStatus.UNKNOWN);
        ConnectionSectionPart.this.fTableViewer.update(this.fConnectionInfo, null);
        notifyConnectionChanged();
      } else {
        display.syncExec(new Runnable() {
          
          public void run() {
            ConnectionInfoValidationListener.this.fConnectionInfo.setValidationStatus(EValidationStatus.UNKNOWN);
            ConnectionSectionPart.this.fTableViewer.update(ConnectionInfoValidationListener.this.fConnectionInfo, null);
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
          notifyConnectionChanged();
        }
        
      });
    }
    
    // --- Private code
    
    private void notifyConnectionChanged() {
      if (this.fConnectionInfo == ConnectionSectionPart.this.fCurrentConnection) {
        for (final IConnectionTypeListener listener : ConnectionSectionPart.this.fConnectionTypeListeners) {
          listener.connectionChanged(ConnectionSectionPart.this.fLocalConnBt.getSelection(), 
                                     this.fConnectionInfo.getName(), this.fConnectionInfo.getValidationStatus());
        }
      }
    }
    
    // --- Fields
    
    private final IConnectionInfo fConnectionInfo;
    
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
    
  private TableViewer fTableViewer;
  
  private Label fErrorLabel;
  
  private Button fValidateButton;
  
  private IConnectionInfo fCurrentConnection;
   
  
  private final Collection<IConnectionTypeListener> fConnectionTypeListeners;
  
  private final Collection<IX10PlatformValidationListener> fValidationListeners;

}