/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.preferences;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.imp.x10dt.ui.launch.core.LaunchCore;
import org.eclipse.imp.x10dt.ui.launch.core.LaunchImages;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.imp.x10dt.ui.launch.core.builder.ELanguage;
import org.eclipse.imp.x10dt.ui.launch.core.dialogs.DialogsFactory;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EArchitecture;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EValidStatus;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.IX10PlatformConfiguration;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.X10PlatformsManager;
import org.eclipse.imp.x10dt.ui.launch.core.utils.PTPUtils;
import org.eclipse.imp.x10dt.ui.launch.core.wizards.X10NewPlatformConfWizard;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.IModelManager;
import org.eclipse.ptp.core.PTPCorePlugin;
import org.eclipse.ptp.core.elements.IPUniverse;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.core.elements.attributes.ResourceManagerAttributes;
import org.eclipse.ptp.ui.wizards.RMServicesConfigurationWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Defines X10 Platform Configuration preference page.
 * 
 * @author egeay
 */
public final class X10PlatformConfigurationPrefPage extends PreferencePage 
                                                    implements IWorkbenchPreferencePage, SelectionListener {
  /**
   * Creates the platform configuration preference page.
   */
  public X10PlatformConfigurationPrefPage() {
    super(Messages.XPCPP_PlatformConfigTitle);
  }
  
  // --- Interface methods implementation

  public void init(final IWorkbench workbench) {
    setPreferenceStore(LaunchCore.getInstance().getPreferenceStore());
  }
  
  // --- SelectionListener's interface methods implementation
  
  public void widgetDefaultSelected(final SelectionEvent event) {
    widgetSelected(event);
  }

  public void widgetSelected(final SelectionEvent event) {
    final TableItem[] selection = ((Table) event.widget).getSelection();
    for (final Button button : this.fUpdatableButtons) {
      button.setEnabled(selection.length == 1);
    }
    if (selection.length == 1) {
      updateSummaryText(this.fX10Platforms.get(((ConfTableEntry) selection[0].getData()).getConfigurationName()));
    }
  }
  
  // --- Abstract methods implementation
  
  protected Control createContents(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(1, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    
    final Label label = new Label(composite, SWT.NONE);
    label.setFont(parent.getFont());
    label.setText(Messages.XPCPP_X10PlatformsMsg);
    
    final SashForm sashFrom = new SashForm(composite, SWT.VERTICAL);
    sashFrom.setLayout(new GridLayout(1, false));
    sashFrom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    
    try {
      this.fX10Platforms = X10PlatformsManager.loadPlatformsConfiguration();
    } catch (Exception except) {
      setMessage(Messages.XPCPP_LoadingErrorMsg, IMessageProvider.WARNING);
      LaunchCore.log(IStatus.ERROR, Messages.XPCPP_LoadingErrorLogMsg, except);
    }
    
    addPlatformConfsContainer(sashFrom);
    
    this.fPlatformConfSummaryText = new StyledText(sashFrom, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY);
    this.fPlatformConfSummaryText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    this.fPlatformConfSummaryText.addListener (SWT.Resize, new Listener () {
      public void handleEvent (Event event) {
        final Rectangle rect = X10PlatformConfigurationPrefPage.this.fPlatformConfSummaryText.getClientArea ();
        
        final Image newImage = new Image (getShell().getDisplay(), 1, Math.max (1, rect.height));
        final GC gc = new GC (newImage);
        gc.setForeground (getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        gc.setBackground (getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
        gc.fillGradientRectangle (rect.x, rect.y, 1, rect.height, true);
        gc.dispose();
        X10PlatformConfigurationPrefPage.this.fPlatformConfSummaryText.setBackgroundImage (newImage);
        
        if (X10PlatformConfigurationPrefPage.this.fOldImage != null) {
          X10PlatformConfigurationPrefPage.this.fOldImage.dispose ();
        }
        X10PlatformConfigurationPrefPage.this.fOldImage = newImage;
      }
    });
    
    final Menu copyMenu = new Menu(this.fPlatformConfSummaryText);
    MenuItem copyItem = new MenuItem(copyMenu, SWT.NONE);
    copyItem.addSelectionListener(new SelectionListener() {

      public void widgetSelected(final SelectionEvent event) {
        final StyledText text = X10PlatformConfigurationPrefPage.this.fPlatformConfSummaryText;
        final Point selection = text.getSelection();
        copySummaryToClipboard(text.getText(selection.x, selection.y - 1));
      }

      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetDefaultSelected(event);
      }
      
    });
    copyItem.setText(Messages.XPCPP_CopyMenuItem);
    this.fPlatformConfSummaryText.setMenu(copyMenu);

    return composite;
  }
  
 // --- Overridden methods
  
  public boolean performOk() {
    try {
      X10PlatformsManager.savePlatformsConfiguration(this.fX10Platforms);
    } catch (IOException except) {
      final IStatus status = new Status(IStatus.ERROR, LaunchCore.getInstance().getBundle().getSymbolicName(), 
                                        IStatus.OK, Messages.XPCPP_SavingErrorLogMsg, except);
      ErrorDialog.openError(getShell(), Messages.XPCPP_SavingErrorDialogTitle, Messages.XPCPP_SavingErrorDialogDescr, 
                            status);
      LaunchCore.log(status);
    }
    return true;
  }
  
  // --- Private code
  
  private void addPlatformConfsContainer(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    final Composite tableWrapper = new Composite(composite, SWT.NONE);
    tableWrapper.setFont(parent.getFont());
    tableWrapper.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 7));
    
    final TableViewer tableViewer = new TableViewer(tableWrapper, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.FULL_SELECTION);
    tableViewer.setContentProvider(new IStructuredContentProvider() {
      
      public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
      }
      
      public void dispose() {
      }
      
      @SuppressWarnings("unchecked")
      public Object[] getElements(final Object inputElement) {
        return ((Collection<ConfTableEntry>) inputElement).toArray();
      }
      
    });
    
    final Button validateButton = new Button(composite, SWT.PUSH);
    validateButton.setFont(composite.getFont());
    validateButton.setText(Messages.PCDWP_ValidateBt);
    validateButton.setEnabled(false);
    validateButton.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        final IX10PlatformConfiguration platformConfiguration = getSelectedConfiguration(tableViewer);
        validateConfiguration(platformConfiguration, tableViewer);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    
    new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
    
    final Button addButton = new Button(composite, SWT.PUSH);
    addButton.setFont(composite.getFont());
    addButton.setText(Messages.XPCPP_AddBt);
    addButton.addSelectionListener(new SelectionListener() {
      
      // --- Interface methods implementation
      
      public void widgetSelected(final SelectionEvent event) {
        if (checkForStartedResourceManagers(null /* resourceManagerId */)) {
          final Map<String,IX10PlatformConfiguration> platforms = X10PlatformConfigurationPrefPage.this.fX10Platforms;
          final X10NewPlatformConfWizard wizard = new X10NewPlatformConfWizard(platforms.keySet());
          final WizardDialog dialog = new WizardDialog(getShell(), wizard);
          if (dialog.open() == Window.OK) {
            final IX10PlatformConfiguration platformConf = wizard.getPlatformConfiguration();
            final ConfTableEntry entry = new ConfTableEntry(platformConf);
            X10PlatformConfigurationPrefPage.this.fNameToTableEntry.put(platformConf.getName(), entry);
            tableViewer.add(entry);
            platforms.put(platformConf.getName(), platformConf);
            
            validateConfiguration(platformConf, tableViewer);
          }
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    
    final Button editButton = new Button(composite, SWT.PUSH);
    editButton.setFont(composite.getFont());
    editButton.setText(Messages.XPCPP_EditBt);
    editButton.setEnabled(false);
    editButton.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        final IX10PlatformConfiguration platformConfiguration = getSelectedConfiguration(tableViewer);
        if (checkForStartedResourceManagers(platformConfiguration.getResourceManagerId())) {
          final X10NewPlatformConfWizard wizard = new X10NewPlatformConfWizard(platformConfiguration);
          final WizardDialog dialog = new WizardDialog(getShell(), wizard);
          if (dialog.open() == Window.OK) {
            final IX10PlatformConfiguration platformConf = wizard.getPlatformConfiguration();
            X10PlatformConfigurationPrefPage.this.fX10Platforms.put(platformConfiguration.getName(), platformConf);
            
            validateConfiguration(wizard.getPlatformConfiguration(), tableViewer);
          }
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
    });
    
    final Button removeButton = new Button(composite, SWT.PUSH);
    removeButton.setFont(composite.getFont());
    removeButton.setText(Messages.XPCPP_RemoteBt);
    removeButton.setEnabled(false);
    removeButton.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        final IX10PlatformConfiguration platformConfiguration = getSelectedConfiguration(tableViewer);
        
        X10PlatformConfigurationPrefPage.this.fX10Platforms.remove(platformConfiguration.getName());
        tableViewer.remove(X10PlatformConfigurationPrefPage.this.fNameToTableEntry.get(platformConfiguration.getName()));
        X10PlatformConfigurationPrefPage.this.fNameToTableEntry.remove(platformConfiguration.getName());

        X10PlatformConfigurationPrefPage.this.fPlatformConfSummaryText.setText(""); //$NON-NLS-1$
        for (final Button button : X10PlatformConfigurationPrefPage.this.fUpdatableButtons) {
          button.setEnabled(false);
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
    });
    
    final Button renameBt = new Button(composite, SWT.PUSH);
    renameBt.setFont(composite.getFont());
    renameBt.setText(Messages.XPCPP_RenameBt);
    renameBt.setEnabled(false);
    renameBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        final Map<String, IX10PlatformConfiguration> platforms = X10PlatformConfigurationPrefPage.this.fX10Platforms;
        final IX10PlatformConfiguration configuration = getSelectedConfiguration(tableViewer);
        
        final InputDialog dialog = new InputDialog(getShell(), Messages.XPCPP_RenameDialogTitle, 
                                                   Messages.XPCPP_RenameDialogMsg, null /* initialValue */, 
                                                   new RenamingInputValidor(platforms.keySet()));
        if (dialog.open() == Window.OK) {
          final Map<String, ConfTableEntry> entries = X10PlatformConfigurationPrefPage.this.fNameToTableEntry;
          final ConfTableEntry entry = entries.remove(configuration.getName());
          entries.put(dialog.getValue(), entry);
          entry.setNewConfigurationName(dialog.getValue());
          
          tableViewer.refresh(entry);
          
          platforms.remove(configuration.getName());
          platforms.put(dialog.getValue(), X10PlatformsManager.createNewConfigurationName(configuration, dialog.getValue()));
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
    });
    
    final Image validImg = LaunchImages.createUnmanaged(LaunchImages.CONF_READY).createImage();
    final Image unkownImg = LaunchImages.createUnmanaged(LaunchImages.CONF_UNKNOWN).createImage();
    final Image errorImg = LaunchImages.createUnmanaged(LaunchImages.CONF_ERROR).createImage();
    
    final TableColumnLayout columnLayout = new TableColumnLayout();
    tableWrapper.setLayout(columnLayout);
    
    final TableViewerColumn statusColumn = new TableViewerColumn(tableViewer, SWT.NONE);
    columnLayout.setColumnData(statusColumn.getColumn(), new ColumnWeightData(10, 30));
    statusColumn.setLabelProvider(new CenterImageLabelProvider() {

      protected Image getImage(final Object element) {
        final ConfTableEntry input = (ConfTableEntry) element;
        switch (input.getStatus()) {
          case ERROR:
          case FAILURE:
            return errorImg;
          case UNKNOWN:
            return unkownImg;
          case VALID:
            return validImg;
          default:
            return null;
        }
      }
      
    });
    
    final TableViewerColumn confColumn = new TableViewerColumn(tableViewer, SWT.NONE);
    columnLayout.setColumnData(confColumn.getColumn(), new ColumnWeightData(90, 100));
    confColumn.setLabelProvider(new ColumnLabelProvider() {
      
      public String getText(final Object element) {
        return ((ConfTableEntry) element).getConfigurationName();
      }
      
    });
    
    tableViewer.setInput(getTableInput());
    
    tableViewer.getTable().addSelectionListener(this);
    
    this.fUpdatableButtons = new Button[] { validateButton, editButton, removeButton, renameBt };
  }
  
  private boolean checkForStartedResourceManagers(final String resourceManagerId) {
    final IModelManager modelManager = PTPCorePlugin.getDefault().getModelManager();
    final IPUniverse universe = modelManager.getUniverse();
    final Collection<IResourceManager> stoppedResManagerList = new ArrayList<IResourceManager>();
    for (final IResourceManager resourceManager : universe.getResourceManagers()) {
      if (resourceManager.getState() == ResourceManagerAttributes.State.STARTED) {
        if ((resourceManagerId == null) || (resourceManager.getUniqueName().equals(resourceManagerId))) {
          return true;
        }
      } else if ((resourceManagerId == null) || (resourceManager.getUniqueName().equals(resourceManagerId))) {
        stoppedResManagerList.add(resourceManager);
      }
    }
    if ((resourceManagerId != null) && stoppedResManagerList.isEmpty()) {
      MessageDialog.open(MessageDialog.ERROR, getShell(), Messages.XPCPP_NoResIdDgTitle, Messages.XPCPP_NoResIdDgMsg, SWT.NONE);
      return true;
    }
    
    int dialogResult = -1;
    if (stoppedResManagerList.isEmpty()) {
      final boolean proceed = MessageDialog.open(MessageDialog.CONFIRM, getShell(), Messages.XPCPP_DialogTitle, 
                                                 Messages.XPCPP_RMCreationMsg, SWT.NONE);
      if (proceed) {
        dialogResult = createNewResourceManager(universe);
      }
    } else { 
      if (resourceManagerId == null) {
        // We are adding a new platform configuration. So we should also propose to create a new resource manager.
        final StringBuilder rmBuilder = new StringBuilder();
        for (final IResourceManager resourceManager : stoppedResManagerList) {
          rmBuilder.append('\t').append(resourceManager.getName()).append('\n');
        }
        final MessageDialog dialog = new MessageDialog(getShell(), Messages.XPCPP_RMSelectionDialogTitle, null,
                                                       NLS.bind(Messages.XPCPP_RMSelectionDialogDescr, rmBuilder.toString()),
                                                       MessageDialog.QUESTION,
                                                       new String[] { Messages.XPCPP_CreateNewRMMsg, 
                                                                      Messages.XPCPP_StartExistingRMMsg },
                                                       0 /* defaultIndex */);
        switch (dialog.open()) {
          case 0 :
            dialogResult = createNewResourceManager(universe);
            break;
          case 1 :
            dialogResult = DialogsFactory.openResourceManagerStartDialog(getShell(), stoppedResManagerList);
            break;
        }
      } else {
        final boolean proceed = MessageDialog.open(MessageDialog.CONFIRM, getShell(), Messages.XPCPP_DialogTitle, 
                                                   Messages.XPCPP_RMStartMsg, SWT.NONE);
        if (proceed) {
          dialogResult = DialogsFactory.openResourceManagerStartDialog(getShell(), stoppedResManagerList);
        }
      }
    }
    
    if (dialogResult == Window.OK) {
      for (final IResourceManager resourceManager : universe.getResourceManagers()) {
        if (resourceManager.getState() == ResourceManagerAttributes.State.STARTED) {
          return true;
        }
      }
    }
    return false;
  }
  
  private void copySummaryToClipboard(final String text) {
    if (this.fClipboard != null) {
      this.fClipboard.dispose();
    }
    this.fClipboard = new Clipboard(this.fPlatformConfSummaryText.getDisplay());
    this.fClipboard.setContents(new Object[] { text }, new Transfer[] { TextTransfer.getInstance() });
  }
  
  private int createNewResourceManager(final IPUniverse universe) {
    final RMServicesConfigurationWizard wizard = new RMServicesConfigurationWizard();
    final WizardDialog dialog = new WizardDialog(getShell(), wizard);
    final int dialogResult = dialog.open();
    
    if ((dialogResult == Window.OK) && (universe.getResourceManagers().length > 0)) {
      // We take the first one. There shouldn't more than one, a priori.
      final IResourceManager rmManager = universe.getResourceManagers()[0];
      try {
        rmManager.startUp(new NullProgressMonitor());
      } catch (CoreException except) {
        ErrorDialog.openError(getShell(), Messages.XPCPP_RMStartErrorDialogTitle, 
                              NLS.bind(Messages.XPCPP_RMStartErrorDialogMsg, rmManager.getName()), except.getStatus());
      }
    }
    return dialogResult;
  }
  
  private IX10PlatformConfiguration getSelectedConfiguration(final TableViewer tableViewer) {
    final ConfTableEntry element = (ConfTableEntry) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
    return this.fX10Platforms.get(element.getConfigurationName());
  }
  
  private Collection<ConfTableEntry> getTableInput() {
    this.fNameToTableEntry = new HashMap<String, ConfTableEntry>(this.fX10Platforms.size());
    for (final IX10PlatformConfiguration platformConfiguration : this.fX10Platforms.values()) {
      this.fNameToTableEntry.put(platformConfiguration.getName(), new ConfTableEntry(platformConfiguration));
    }
    return this.fNameToTableEntry.values();
  }

  private void updateSummaryText(final IX10PlatformConfiguration platformConfiguration) {
    final EValidStatus validStatus = platformConfiguration.getValidationStatus();
    if ((validStatus == EValidStatus.ERROR) || (validStatus == EValidStatus.FAILURE)) {
      writeFailureOrError(platformConfiguration);
    } else {
      final StringBuilder sb = new StringBuilder();
      sb.append(NLS.bind(Messages.XPCPP_ConfSummary, platformConfiguration.getName()));
      sb.append(NLS.bind(Messages.XPCPP_X10DistribLoc, platformConfiguration.getX10DistribLocation()));
      sb.append(NLS.bind(Messages.XPCPP_PGASLoc, platformConfiguration.getPGASLocation()));
      sb.append(NLS.bind(Messages.XPCPP_Compiler, platformConfiguration.getCompiler()));
      sb.append(NLS.bind(Messages.XPCPP_CompilerOpts, platformConfiguration.getCompilerOpts()));
      if (platformConfiguration.hasArchivingStep()) {
        sb.append(NLS.bind(Messages.XPCPP_Archiver, platformConfiguration.getArchiver()));
        sb.append(NLS.bind(Messages.XPCPP_ArchivingOpts, platformConfiguration.getArchivingOpts()));
      }
      if (platformConfiguration.hasLinkingStep()) {
        sb.append(NLS.bind(Messages.XPCPP_Linker, platformConfiguration.getLinker()));
        sb.append(NLS.bind(Messages.XPCPP_LinkingOpts, platformConfiguration.getLinkingOpts()));
        sb.append(NLS.bind(Messages.XPCPP_LinkingLibs, platformConfiguration.getLinkingLibs()));
      }
      boolean hasResourceManager = true;
      if (platformConfiguration.getResourceManagerId() != null) {
        final String rmUniqueName = platformConfiguration.getResourceManagerId();
        final IResourceManager resourceManager = PTPUtils.getResourceManager(getShell(), rmUniqueName);
        if (resourceManager == null) {
          hasResourceManager = false;
          sb.append(NLS.bind(Messages.XPCPP_ResourceManagerName, Messages.XPCPP_UnknownRMName));
          sb.insert(0, Messages.XPCPP_NoRMForIdSaved);
        } else {
          sb.append(NLS.bind(Messages.XPCPP_ResourceManagerName, resourceManager.getName()));
          
          if (! resourceManager.getUniqueName().equals(rmUniqueName)) {
            final String newUniqueName = resourceManager.getUniqueName();
            final IX10PlatformConfiguration newConf = X10PlatformsManager.createNewConfigurationRMId(platformConfiguration, 
                                                                                                     newUniqueName);
            this.fX10Platforms.put(platformConfiguration.getName(), newConf);
          }
        }
      }
      sb.append(NLS.bind(Messages.XPCPP_TargetOS, platformConfiguration.getTargetOS()));
      final String architectureName = (platformConfiguration.getArchitecture() == EArchitecture.E32Arch) ? "32" : "64"; //$NON-NLS-1$ //$NON-NLS-2$
      sb.append(NLS.bind(Messages.XPCPP_Architecture, architectureName));
      this.fPlatformConfSummaryText.setText(sb.toString());
      
      if (! hasResourceManager) {
        final StyleRange styleRange = new StyleRange();
        styleRange.start = 0;
        styleRange.length = Messages.XPCPP_NoRMForIdSaved.length();
        styleRange.foreground = this.fPlatformConfSummaryText.getDisplay().getSystemColor(SWT.COLOR_RED);
        this.fPlatformConfSummaryText.setStyleRange(styleRange);
      }
    }
  }
  
  private void validateConfiguration(final IX10PlatformConfiguration platformConfiguration, final TableViewer tableViewer) {
    final String resUniqueName = platformConfiguration.getResourceManagerId();
    final IResourceManager resourceManager = PTPUtils.getResourceManager(getShell(), resUniqueName);
    
    final ELanguage language = platformConfiguration.isCplusPlus() ? ELanguage.CPP : ELanguage.JAVA;
    final IPlatformConfChecker checker = new PlatformConfChecker(resourceManager);
    
    final String compiler = platformConfiguration.getCompiler();
    final String compilingOpts = platformConfiguration.getCompilerOpts();
    final String archiver = platformConfiguration.getArchiver();
    final String archivingOpts = platformConfiguration.getArchivingOpts();
    final String linker = platformConfiguration.getLinker();
    final String linkingOpts = platformConfiguration.getLinkingOpts();
    final String linkingLibs = platformConfiguration.getLinkingLibs();
    final boolean hasLinkingStep = platformConfiguration.hasLinkingStep();
    final String x10DistLoc = platformConfiguration.getX10DistribLocation();
    final String pgasDistLoc = platformConfiguration.getPGASLocation();
    final String[] x10HeadersLocs = platformConfiguration.getX10HeadersLocations();
    final String[] x10LibsLocs = platformConfiguration.getX10LibsLocations();
    
    final IRunnableWithProgress runnable = new IRunnableWithProgress() {
      
      public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        final SubMonitor subMonitor = SubMonitor.convert(monitor, 20);
        
        String returnCompilMsg = null;
        try {
          returnCompilMsg = checker.validateCompilation(language, compiler, compilingOpts, x10DistLoc, pgasDistLoc,
                                                        x10HeadersLocs, x10LibsLocs, subMonitor.newChild(7));
        } catch (Exception except) {
          monitor.done();
          throw new InvocationTargetException(except);
        }
        if (returnCompilMsg == null) {
          String returnArchivingMsg = null;
          try {
            returnArchivingMsg = checker.validateArchiving(archiver, archivingOpts, subMonitor.newChild(3));
          } catch (Exception except) {
            monitor.done();
            throw new InvocationTargetException(except);
          }
          if (returnArchivingMsg != null) {
            monitor.done();
            throw new ValidationException(Messages.PCDWP_ArchivingFailureMsg, returnArchivingMsg);
          }
          if (hasLinkingStep) {
            String returnLinkMsg = null;
            try {
              returnLinkMsg = checker.validateLinking(linker, linkingOpts, linkingLibs, x10HeadersLocs,
                                                      x10LibsLocs, subMonitor.newChild(10));
            } catch (Exception except) {
              monitor.done();
              throw new InvocationTargetException(except);
            }
            if (returnLinkMsg != null) {
              monitor.done();
              throw new ValidationException(Messages.PCDWP_LinkingFailureMsg, returnLinkMsg);
            }
          }
        } else {
          monitor.done();
          throw new ValidationException(Messages.PCDWP_CompilationFailureMsg, returnCompilMsg);
        }
      }          
    };
    try {
      new ProgressMonitorDialog(getShell()).run(true, true, runnable);
      platformConfiguration.defineStatus(EValidStatus.VALID);
    } catch (ValidationException except) {
      platformConfiguration.defineStatus(EValidStatus.FAILURE);
      platformConfiguration.defineValidationErrorStatus(NLS.bind(Messages.XPCPP_OneLineBreak, except.getValidationStepMessage(), 
                                                                 except.getMessage()));
    } catch (Exception except) {
      platformConfiguration.defineStatus(EValidStatus.ERROR);
      final StringWriter strWriter = new StringWriter();
      except.getCause().printStackTrace(new PrintWriter(strWriter, true));
      strWriter.flush();
      platformConfiguration.defineValidationErrorStatus(strWriter.toString());
    }
    final ConfTableEntry entry = this.fNameToTableEntry.get(platformConfiguration.getName());
    entry.setNewStatus(platformConfiguration.getValidationStatus());
    tableViewer.refresh(entry);
    
    updateSummaryText(platformConfiguration);
  }
  
  private void writeFailureOrError(final IX10PlatformConfiguration platformConfiguration) {
    final EValidStatus validStatus = platformConfiguration.getValidationStatus();
    final String statusMsg = (validStatus == EValidStatus.ERROR) ? Messages.XPCPP_ValidationInternalError : 
                                                                   Messages.XPCPP_ValidationFailure;
    this.fPlatformConfSummaryText.setText(NLS.bind(Messages.XPCPP_TwoLinesBreak, statusMsg, 
                                                   platformConfiguration.getValidationErrorMessage()));  
    final StyleRange styleRange = new StyleRange();
    styleRange.start = 0;
    styleRange.length = statusMsg.length();
    styleRange.underline = true;
    final int color = (validStatus == EValidStatus.ERROR) ? SWT.COLOR_RED : SWT.COLOR_MAGENTA;
    styleRange.foreground = this.fPlatformConfSummaryText.getDisplay().getSystemColor(color);
    this.fPlatformConfSummaryText.setStyleRange(styleRange);
  }
  
  // --- Private classes
  
  private static final class RenamingInputValidor implements IInputValidator {
    
    RenamingInputValidor(final Set<String> confNames) {
      this.fConfNames = confNames;
    }

    // --- Interface methods implementation
    
    public String isValid(final String newText) {
      if (this.fConfNames.contains(newText)) {
        return NLS.bind(Messages.XPCPP_ConfNameAlreadyExists, newText);
      } else if (newText.length() == 0) {
        return Messages.XPCPP_EmptyRenamingError;
      } else {
        return null;
      }
    }
    
    // --- Fields
    
    private final Set<String> fConfNames;
    
  }
  
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
  
  private static final class ValidationException extends InterruptedException {
    
    ValidationException(final String validationStepMessage, final String errorMessage) {
      super(errorMessage);
      this.fValidationStepMessage = validationStepMessage;
    }
    
    // --- Internal services
    
    String getValidationStepMessage() {
      return this.fValidationStepMessage;
    }
    
    // --- Fields
    
    private final String fValidationStepMessage;
    
    private static final long serialVersionUID = -7660712314030364087L;

    
  }
  
  private static final class ConfTableEntry {
    
    ConfTableEntry(final IX10PlatformConfiguration platformConfiguration) {
      this.fValidStatus = platformConfiguration.getValidationStatus();
      this.fConfigurationName = platformConfiguration.getName();
    }
    
    // --- Interface methods implementation
    
    String getConfigurationName() {
      return this.fConfigurationName;
    }
    
    EValidStatus getStatus() {
      return this.fValidStatus;
    }
    
    void setNewConfigurationName(final String configurationName) {
      this.fConfigurationName = configurationName;
    }
    
    void setNewStatus(final EValidStatus status) {
      this.fValidStatus = status;
    }
    
    // --- Overridden methods
    
    public boolean equals(final Object rhs) {
      if (getClass().equals(rhs.getClass())) {
        final ConfTableEntry rhsObj = (ConfTableEntry) rhs;
        return this.fValidStatus.equals(rhsObj.fValidStatus) && this.fConfigurationName.equals(rhsObj.fConfigurationName);
      } else {
        return false;
      }
    }
    
    public int hashCode() {
      return this.fValidStatus.hashCode() + this.fConfigurationName.hashCode();
    }
    
    // --- Private code
    
    private EValidStatus fValidStatus;
    
    private String fConfigurationName;
    
  }
  
  // --- Fields
  
  private StyledText fPlatformConfSummaryText;
  
  private Map<String, IX10PlatformConfiguration> fX10Platforms;
  
  private Map<String, ConfTableEntry> fNameToTableEntry;
  
  private Button[] fUpdatableButtons;
  
  private Image fOldImage;
  
  private Clipboard fClipboard;

}
