/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.rms.launch_configuration;

import static x10dt.ui.launch.cpp.rms.launch_configuration.LaunchConfigConstants.ATTR_HOSTFILE;
import static x10dt.ui.launch.cpp.rms.launch_configuration.LaunchConfigConstants.ATTR_HOSTLIST;
import static x10dt.ui.launch.cpp.rms.launch_configuration.LaunchConfigConstants.ATTR_NUM_PLACES;
import static x10dt.ui.launch.cpp.rms.launch_configuration.LaunchConfigConstants.ATTR_USE_HOSTFILE;
import static x10dt.ui.launch.cpp.rms.launch_configuration.LaunchConfigConstants.DEFAULT_NUM_PLACES;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.attributes.ArrayAttribute;
import org.eclipse.ptp.core.attributes.IAttribute;
import org.eclipse.ptp.core.attributes.IllegalValueException;
import org.eclipse.ptp.core.elementcontrols.IResourceManagerControl;
import org.eclipse.ptp.core.elements.IPMachine;
import org.eclipse.ptp.core.elements.IPNode;
import org.eclipse.ptp.core.elements.IPQueue;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.core.elements.attributes.JobAttributes;
import org.eclipse.ptp.launch.ui.extensions.IRMLaunchConfigurationContentsChangedListener;
import org.eclipse.ptp.launch.ui.extensions.IRMLaunchConfigurationDynamicTab;
import org.eclipse.ptp.launch.ui.extensions.RMLaunchValidation;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import x10dt.ui.launch.core.Constants;
import x10dt.ui.launch.core.utils.PTPConstants;
import x10dt.ui.launch.cpp.rms.Messages;
import x10dt.ui.launch.cpp.rms.RMSActivator;
import x10dt.ui.launch.cpp.utils.PTPUtils;


final class SocketsRMLaunchConfigurationDynamicTab implements IRMLaunchConfigurationDynamicTab {
  
  SocketsRMLaunchConfigurationDynamicTab() {
    this.fHosts = new ArrayList<String>();
    this.fListeners = new ArrayList<IRMLaunchConfigurationContentsChangedListener>();
  }

  // --- Interface methods implementation
  
  public void addContentsChangedListener(final IRMLaunchConfigurationContentsChangedListener listener) {
    this.fListeners.add(listener);
  }

  public RMLaunchValidation canSave(final Control control, final IResourceManager resourceManager, final IPQueue queue) {
    return new RMLaunchValidation(true, null);
  }

  public void createControl(final Composite parent, final IResourceManager resourceManager, 
                            final IPQueue queue) throws CoreException {
    this.fControl = new Composite(parent, SWT.NONE);
    this.fControl.setFont(parent.getFont());
    this.fControl.setLayout(new GridLayout(1, false));
    this.fControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    
    final Composite placesCompo = new Composite(this.fControl, SWT.NONE);
    placesCompo.setFont(this.fControl.getFont());
    placesCompo.setLayout(new GridLayout(2, false));
    placesCompo.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, true, false));
    
    new Label(placesCompo, SWT.NONE).setText(Messages.SRMLCDT_PlacesNumber);
    this.fNumPlacesSpinner = new Spinner(placesCompo, SWT.BORDER);
    this.fNumPlacesSpinner.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        fireContentChange();
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    
    final Group hostsGroup = new Group(this.fControl, SWT.NONE);
    hostsGroup.setFont(this.fControl.getFont());
    hostsGroup.setLayout(new GridLayout(2, false));
    hostsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    hostsGroup.setText(Messages.SRMLCDT_HostsGroupName);
    
    this.fHostFileBt = new Button(hostsGroup, SWT.RADIO);
    this.fHostFileBt.setText(Messages.SRMLCDT_HostFileBt);
    this.fHostFileBt.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, false, false, 2, 1));
    this.fHostFileBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        updateHostFileEnableState(true);
        fireContentChange();
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    
    final Text hostFileText = new Text(hostsGroup, SWT.BORDER);
    this.fHostFileText = hostFileText;
    final GridData hostFileGData = new GridData(SWT.FILL, SWT.CENTER, true, false);
    hostFileGData.horizontalIndent = 30;
    this.fHostFileText.setLayoutData(hostFileGData);
    this.fHostFileText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        fireContentChange();
      }
      
    });
    
    this.fHostFileBrowseBt = new Button(hostsGroup, SWT.PUSH);
    this.fHostFileBrowseBt.setText(Messages.SRMLCDT_BrowseBt);
    this.fHostFileBrowseBt.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
    this.fHostFileBrowseBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        final IResourceManagerConfiguration conf = ((IResourceManagerControl) resourceManager).getConfiguration();
        final String path;
        final String servicesId = conf.getRemoteServicesId();
        if (PTPConstants.LOCAL_CONN_SERVICE_ID.equals(servicesId)) {
          final FileDialog dialog = new FileDialog(parent.getShell());
          dialog.setText(Messages.SRMLCDT_SelectHostFileDialogTitle);
          path = dialog.open();
        } else {
          final IRemoteServices remoteServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(servicesId);
          final IRemoteConnection connection = remoteServices.getConnectionManager().getConnection(conf.getConnectionName());
          path = PTPUtils.remoteBrowse(parent.getShell(), connection, Messages.SRMLCDT_SelectHostFileDialogTitle, "", false); //$NON-NLS-1$
        }
        if (path != null) {
          hostFileText.setText(path);
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    
    this.fHostListBt = new Button(hostsGroup, SWT.RADIO);
    this.fHostListBt.setText(Messages.SRMLCDT_HostListBt);
    this.fHostListBt.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, false, false, 2, 1));
    this.fHostListBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        updateHostFileEnableState(false);
        fireContentChange();
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
       widgetSelected(event);
      }
      
    });
    
    final Composite tableComposite = new Composite(hostsGroup, SWT.NONE);
    tableComposite.setFont(hostsGroup.getFont());
    final GridData tableGData = new GridData(SWT.FILL, SWT.FILL, true, true);
    tableGData.verticalSpan = 2;
    tableGData.horizontalIndent = 30;
    tableComposite.setLayoutData(tableGData);
    
    final TableViewer viewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);
    this.fHostListViewer = viewer;
    this.fHostListViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
    final TableViewerColumn column = new TableViewerColumn(this.fHostListViewer, SWT.NONE);
    column.setLabelProvider(new ColumnLabelProvider() {

      public String getText(final Object element) {
        return (String) element;
      }
      
    });
    final TextCellEditor editor = new TextCellEditor(viewer.getTable());;
    column.setEditingSupport(new EditingSupport(this.fHostListViewer) {

      protected CellEditor getCellEditor(final Object element) {
        return editor;
      }

      @SuppressWarnings("unqualified-field-access")
      protected boolean canEdit(final Object element) {
        return viewer.getTable().getSelectionIndex() < fHosts.size();
      }

      protected Object getValue(final Object element) {
        return element;
      }

      @SuppressWarnings("unqualified-field-access")
      protected void setValue(final Object element, final Object value) {
        final int index = fHosts.indexOf(element);
        fHosts.remove(element);
        fHosts.add(index, (String) value);
        viewer.refresh();
        
        fireContentChange();
      }

    });
    
    final TableColumnLayout tableColumnLayout = new TableColumnLayout();
    tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(100));
    tableComposite.setLayout(tableColumnLayout);
    
    this.fHostListViewer.setContentProvider(new IStructuredContentProvider() {
      
      public void inputChanged(final Viewer curViewer, final Object oldInput, final Object newInput) {
      }
      
      public void dispose() {
      }
      
      @SuppressWarnings("unqualified-field-access")
      public Object[] getElements(final Object inputElement) {
        return fHosts.toArray(new String[fHosts.size()]);
      }
      
    });
    
    this.fHostListViewer.setInput(this.fHosts);
    this.fHostListViewer.getTable().setLinesVisible(true);
    
    final Button addButton = new Button(hostsGroup, SWT.PUSH);
    addButton.setText(Messages.SRMLCDT_AddBt);
    addButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
    addButton.addSelectionListener(new SelectionListener() {
      
      @SuppressWarnings("unqualified-field-access")
      public void widgetSelected(final SelectionEvent event) {
        fHosts.add(""); //$NON-NLS-1$
        viewer.getTable().select(viewer.getTable().getItemCount() - 1);
        viewer.add(""); //$NON-NLS-1$
        viewer.editElement("", 0); //$NON-NLS-1$
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    
    final Button removeButton = new Button(hostsGroup, SWT.PUSH);
    removeButton.setText(Messages.SRMLCDT_RemoveBt);
    removeButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
    removeButton.addSelectionListener(new SelectionListener() {
      
      @SuppressWarnings("unqualified-field-access")
      public void widgetSelected(final SelectionEvent event) {
        final Object hostName = viewer.getElementAt(viewer.getTable().getSelectionIndex());
        if (hostName != null) {
          fHosts.remove(hostName);
          viewer.remove(hostName);
          
          fireContentChange();
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
  }

  @SuppressWarnings("unchecked")
  public IAttribute<?, ?, ?>[] getAttributes(final IResourceManager resourceManager, final IPQueue queue, 
                                             final ILaunchConfiguration configuration, 
                                             final String mode) throws CoreException {
    final List<IAttribute<?,?,?>> attrs = new ArrayList<IAttribute<?,?,?>>();

    try {
      final int numPlaces = configuration.getAttribute(ATTR_NUM_PLACES, DEFAULT_NUM_PLACES);
      attrs.add(JobAttributes.getNumberOfProcessesAttributeDefinition().create(numPlaces));
      attrs.add(LaunchAttributes.getHostFileAttr().create(configuration.getAttribute(ATTR_HOSTFILE, Constants.EMPTY_STR)));
      final ArrayAttribute<String> attribute = LaunchAttributes.getHostListAttr().create();
      final List<String> defaultHostList = new ArrayList<String>();
      if (defaultHostList.isEmpty()) {
        // In case of launching via shortcut.
        for (final IPMachine machine : resourceManager.getMachines()) {
          for (final IPNode node : machine.getNodes()) {
            defaultHostList.add(node.getName());
          }
        }
      }
      attribute.setValue(configuration.getAttribute(ATTR_HOSTLIST, defaultHostList));
      attrs.add(attribute);
    } catch (IllegalValueException except) {
      throw new CoreException(new Status(IStatus.ERROR, RMSActivator.PLUGIN_ID, Messages.SRMLCDT_InvalidPlacesNb, except));
    }

    return attrs.toArray(new IAttribute<?,?,?>[attrs.size()]);
  }

  public Control getControl() {
    return this.fControl;
  }

  @SuppressWarnings("unchecked")
  public RMLaunchValidation initializeFrom(final Control control, final IResourceManager resourceManager, final IPQueue queue,
                                           final ILaunchConfiguration configuration) {
    this.fHosts.clear();
    for (final IPMachine machine : resourceManager.getMachines()) {
      for (final IPNode node : machine.getNodes()) {
        this.fHosts.add(node.getName());
      }
    }
    try {
      this.fHostFileBt.setSelection(configuration.getAttribute(ATTR_USE_HOSTFILE, false));
      this.fNumPlacesSpinner.setSelection(configuration.getAttribute(ATTR_NUM_PLACES, DEFAULT_NUM_PLACES));
      this.fHostFileText.setText(configuration.getAttribute(ATTR_HOSTFILE, Constants.EMPTY_STR));
      this.fHosts = configuration.getAttribute(ATTR_HOSTLIST, this.fHosts);
      this.fHostListViewer.refresh();
      updateHostFileEnableState(this.fHostFileBt.getSelection());
    } catch (CoreException except) {
      return new RMLaunchValidation(false, NLS.bind(Messages.SRMLCDT_PageInitializationError, except.getMessage()));
    }
    return new RMLaunchValidation(true, null);
  }

  public RMLaunchValidation isValid(final ILaunchConfiguration launchConfig, final IResourceManager resourceManager, 
                                    final IPQueue queue) {
    if (this.fNumPlacesSpinner.getSelection() < 1) {
      return new RMLaunchValidation(false, Messages.SRMLCDT_AtLeastOnePlaceMsg);
    }
    if (this.fHostFileBt.getSelection()) {
      if (this.fHostFileText.getText().trim().length() == 0) {
        return new RMLaunchValidation(false, Messages.SRMLCDT_HostFileRequiredMsg);
      }
    } else {
      if (this.fHosts.size() == 0) {
        return new RMLaunchValidation(false, Messages.SRMLCDT_AtLeastOneHostNameMsg);
      }
      for (final String host : this.fHosts) {
        if (host.trim().length() == 0) {
          return new RMLaunchValidation(false, Messages.SRMLCDT_NoEmptyHostNameMsg);
        }
      }
    }
    return new RMLaunchValidation(true, null);
  }

  public RMLaunchValidation performApply(final ILaunchConfigurationWorkingCopy configuration, 
                                         final IResourceManager resourceManager, final IPQueue queue) {
    configuration.setAttribute(ATTR_USE_HOSTFILE, this.fHostFileBt.getSelection());
    configuration.setAttribute(ATTR_NUM_PLACES, this.fNumPlacesSpinner.getSelection());
    configuration.setAttribute(ATTR_HOSTFILE, this.fHostFileText.getText().trim());
    configuration.setAttribute(ATTR_HOSTLIST, new ArrayList<String>(this.fHosts));
    return new RMLaunchValidation(true, null);
  }

  public void removeContentsChangedListener(final IRMLaunchConfigurationContentsChangedListener listener) {
    this.fListeners.remove(listener);
  }

  public RMLaunchValidation setDefaults(final ILaunchConfigurationWorkingCopy configuration, 
                                        final IResourceManager resourceManager, final IPQueue queue) {
    return new RMLaunchValidation(true, null);
  }
  
  // --- Private code
  
  private void fireContentChange() {
    for (final IRMLaunchConfigurationContentsChangedListener listener : this.fListeners) {
      listener.handleContentsChanged(this);
    }
  }
  
  private void updateHostFileEnableState(final boolean enabled) {
    this.fHostFileText.setEnabled(enabled);
    this.fHostFileBrowseBt.setEnabled(enabled);
    this.fHostListBt.setSelection(! enabled);
    this.fHostListViewer.getTable().setEnabled(! enabled);
  }
  
  // --- Fields
  
  private Composite fControl;
  
  private Spinner fNumPlacesSpinner;
  
  private Button fHostFileBt;
  
  private Text fHostFileText;
  
  private Button fHostFileBrowseBt;
  
  private Button fHostListBt;
  
  private TableViewer fHostListViewer;
  
  private List<String> fHosts;
  
  private final List<IRMLaunchConfigurationContentsChangedListener> fListeners;

}
