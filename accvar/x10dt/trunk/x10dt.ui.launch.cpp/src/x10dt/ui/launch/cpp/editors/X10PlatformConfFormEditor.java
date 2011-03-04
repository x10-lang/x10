/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.remotetools.environment.core.ITargetElement;
import org.eclipse.ptp.services.core.IServiceProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.widgets.Form;

import x10dt.ui.launch.core.Constants;
import x10dt.ui.launch.core.Messages;
import x10dt.ui.launch.core.dialogs.DialogsFactory;
import x10dt.ui.launch.core.platform_conf.ETargetOS;
import x10dt.ui.launch.core.platform_conf.EValidationStatus;
import x10dt.ui.launch.core.utils.CoreResourceUtils;
import x10dt.ui.launch.cpp.CppLaunchCore;
import x10dt.ui.launch.cpp.CppLaunchImages;
import x10dt.ui.launch.cpp.LaunchMessages;
import x10dt.ui.launch.cpp.builder.target_op.TargetOpHelperFactory;
import x10dt.ui.launch.cpp.platform_conf.IConnectionConf;
import x10dt.ui.launch.cpp.platform_conf.ICppCompilationConf;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;
import x10dt.ui.launch.cpp.platform_conf.X10PlatformConfFactory;
import x10dt.ui.launch.cpp.platform_conf.validation.IX10PlatformChecker;
import x10dt.ui.launch.cpp.platform_conf.validation.IX10PlatformValidationListener;
import x10dt.ui.launch.cpp.platform_conf.validation.PlatformCheckerFactory;

/**
 * Main class activating the editor for editing the X10 Platform Configuration file.
 * 
 * @author egeay
 */
public final class X10PlatformConfFormEditor extends SharedHeaderFormEditor 
                                             implements IPropertyListener, ICompletePageChangedListener, 
                                                        IX10PlatformValidationListener, IConnectionTypeListener {
  
  // --- IPropertyListener's interface methods implementation
  
  public void propertyChanged(final Object source, final int propId) {
    if ((this == source) && (propId == IWorkbenchPartConstants.PROP_DIRTY)) {
      final boolean isDirty = isDirty();
      if (isDirty != this.fSaveAction.isEnabled()) {
        this.fSaveAction.setEnabled(isDirty);
      }
    }
  }
  
  // --- ICompletePageChangedListener's interface methods implementation
  
  public final void completePageChanged(final X10FormPage formPage, final boolean isComplete) {
    if (formPage.isPageComplete()) {
      boolean allPagesComplete = true;
      for (final Object page : super.pages) {
        if ((page != null) && (page != formPage) && ! ((X10FormPage) page).isPageComplete()) {
          allPagesComplete = false;
          break;
        }
      }
      this.fValidateAction.setEnabled(allPagesComplete);
      if (allPagesComplete) {
        this.fValidateAction.setImageDescriptor(this.fUncheckedPlatformImg);
      }
    } else {
      this.fValidateAction.setEnabled(false);
    }
  }
  
  // --- IX10PlatformValidationListener's interface methods implementation
  
  public void platformCommunicationInterfaceValidated() {
    this.fValidateAction.setImageDescriptor(this.fValidPlatformImg);  	
  }

	public void platformCommunicationInterfaceValidationFailure(final String message) {
		this.fValidateAction.setImageDescriptor(this.fInvalidPlatformImg);
		CoreResourceUtils.addPlatformConfMarker(((IFileEditorInput) getEditorInput()).getFile(), 
                                            NLS.bind(LaunchMessages.XPCFE_DiscoveryCmdFailedMarkerMsg, Constants.EMPTY_STR),
                                            IMarker.SEVERITY_ERROR, IMarker.PRIORITY_HIGH);
    DialogsFactory.createErrorBuilder().setDetailedMessage(message)
    					    .createAndOpen(getSite(), LaunchMessages.XPCFE_CommInterfaceFailure, 
    					                   LaunchMessages.XPCFE_DiscoveryCmdDialogMsg);
	}
  
  public void platformCppCompilationValidated() {
    // Nothing to do. We still have one step to go.
  }
  
  public void platformCppCompilationValidationFailure(final String message) {
    this.fValidateAction.setImageDescriptor(this.fInvalidPlatformImg);
    final String failureMessage = getCurrentPlatformConf().getCppCompilationConf().getValidationErrorMessage();
    CoreResourceUtils.addPlatformConfMarker(((IFileEditorInput) getEditorInput()).getFile(), 
                                            LaunchMessages.XPCFE_ValidationFailureDlgMsg, IMarker.SEVERITY_ERROR, 
                                            IMarker.PRIORITY_HIGH);
    DialogsFactory.createErrorBuilder().setDetailedMessage(failureMessage)
                  .createAndOpen(getSite(), LaunchMessages.XPCFE_ValidationFailureDlgTitle, 
                                 LaunchMessages.XPCFE_ValidationFailureDlgMsg); 
  }
  
  public void platformCppCompilationValidationError(final Exception exception) {
    this.fValidateAction.setImageDescriptor(this.fErrorPlatformImg);
    CoreResourceUtils.addPlatformConfMarker(((IFileEditorInput) getEditorInput()).getFile(), 
                                            LaunchMessages.XPCFE_ValidationErrorDlgMsg, IMarker.SEVERITY_ERROR, 
                                            IMarker.PRIORITY_HIGH);
    DialogsFactory.createErrorBuilder().setDetailedMessage(exception)
    							.createAndOpen(getSite(), LaunchMessages.XPCFE_ValidationErrorDlgTitle, 
    							               LaunchMessages.XPCFE_ValidationErrorDlgMsg);
  }
  
  public void remoteConnectionFailure(final Exception exception) {
    final IAction validationAction = this.fValidateAction;
    CoreResourceUtils.addPlatformConfMarker(((IFileEditorInput) getEditorInput()).getFile(), 
                                            NLS.bind(LaunchMessages.XPCFE_RemoteConnFailureMarkerMsg, Constants.EMPTY_STR),
                                            IMarker.SEVERITY_ERROR, IMarker.PRIORITY_HIGH);
    getSite().getShell().getDisplay().syncExec(new Runnable() {
      
      public void run() {
        if (validationAction.isEnabled()) {
          validationAction.setImageDescriptor(X10PlatformConfFormEditor.this.fInvalidPlatformImg);
        }
        getHeaderForm().getMessageManager().addMessage(X10PlatformConfFormEditor.this.fConnKey, 
                                                       LaunchMessages.XPCFE_RemoteConnValidationError, null, 
                                                       IMessageProvider.ERROR);
      }
      
    });
  }
  
  public void remoteConnectionUnknownStatus() {
    final IAction validationAction = this.fValidateAction;
    CoreResourceUtils.deletePlatformConfMarkers(((IFileEditorInput) getEditorInput()).getFile());
    getSite().getShell().getDisplay().syncExec(new Runnable() {
      
      public void run() {
        getHeaderForm().getMessageManager().removeMessage(X10PlatformConfFormEditor.this.fConnKey);
        if (validationAction.isEnabled()) {
          validationAction.setImageDescriptor(X10PlatformConfFormEditor.this.fUncheckedPlatformImg);
        }   
      }
      
    });
  }
  
  public void remoteConnectionValidated(final ITargetElement targetElement) {
    final IAction validationAction = this.fValidateAction;
    getSite().getShell().getDisplay().syncExec(new Runnable() {
      
      public void run() {
        getHeaderForm().getMessageManager().removeMessage(X10PlatformConfFormEditor.this.fConnKey);
        if (validationAction.isEnabled()) {
          validationAction.setImageDescriptor(X10PlatformConfFormEditor.this.fUncheckedPlatformImg);
        }
      }
      
    });
  }
  
  public void serviceProviderFailure(final CoreException exception) {
  	// Should never occur.
  	CppLaunchCore.log(exception.getStatus());
  }
  
  // --- IConnectionTypeListener's interface methods implementation
  
  public void connectionChanged(final boolean isLocal, final String remoteConnectionName,
                                final EValidationStatus validationStatus, final boolean shouldDeriveInfo) {
    getHeaderForm().getMessageManager().removeMessage(this.fConnKey);
    if (this.fValidateAction.isEnabled()) {
      this.fValidateAction.setImageDescriptor(this.fUncheckedPlatformImg);
    }
  }
  
  // --- Abstract methods implementation

  protected void addPages() {
    setActiveEditor(this);
    setPartName(getEditorInput().getName());
    try {
      addPage(new ConnectionAndCommunicationConfPage(this));
      addPage(new X10CompilationConfigurationPage(this));
    } catch (PartInitException except) {
      CppLaunchCore.log(except.getStatus());
    }
  }

  public void doSave(final IProgressMonitor monitor) {
    final IFile file = ((IFileEditorInput) getEditorInput()).getFile();
    synchronized (file) {
      CoreResourceUtils.deletePlatformConfMarkers(file);
      
      {//TODO: Probably to remove.
        final IProject project = file.getProject();
        final IWorkspace workspace = project.getWorkspace();
        final IWorkspaceDescription description = workspace.getDescription();
        boolean isAutoBuilding = description.isAutoBuilding();
        if (isAutoBuilding == true) {
          description.setAutoBuilding(false);
          try {
            workspace.setDescription(description);
          } catch (CoreException except) {
            CppLaunchCore.log(except.getStatus());
          }
        }

        monitor.beginTask(null, 3);
        final BuildChangeListener buildListener = new BuildChangeListener();
        try {
          project.getWorkspace().addResourceChangeListener(buildListener);
          project.build(IncrementalProjectBuilder.CLEAN_BUILD, new SubProgressMonitor(monitor, 1));
          while (buildListener.hasFinished())
            ;
        } catch (CoreException except) {
          DialogsFactory.createErrorBuilder()
                        .setDetailedMessage(except.getStatus())
                        .createAndOpen(getEditorSite(), LaunchMessages.XPCFE_ConfSavingErrorDlgTitle,
                                       LaunchMessages.XPCFE_CouldNotCleanOutputDir);
        } finally {
          if (isAutoBuilding) {
            description.setAutoBuilding(true);
            try {
              workspace.setDescription(description);
            } catch (CoreException except) {
              CppLaunchCore.log(except.getStatus());
            }
          }
          project.getWorkspace().removeResourceChangeListener(buildListener);
        }
        if (monitor.isCanceled()) {
          return;
        }
      }

      try {
        getCurrentPlatformConf().applyChanges();
        
        final String outputFolder = getCurrentPlatformConf().getCppCompilationConf().getRemoteOutputFolder();
        if ((outputFolder == null) || (outputFolder.length() == 0)) {
          CoreResourceUtils.addPlatformConfMarker(file, Messages.AXBO_NoRemoteOutputFolder, IMarker.SEVERITY_ERROR, 
                                                  IMarker.PRIORITY_HIGH);
        }
        
        final IConnectionConf connConf = getCurrentPlatformConf().getConnectionConf();
        final boolean isCygwin = getCurrentPlatformConf().getCppCompilationConf().getTargetOS() == ETargetOS.WINDOWS;
        if (TargetOpHelperFactory.create(connConf.isLocal(), isCygwin, connConf.getConnectionName()) == null) {
          CoreResourceUtils.addPlatformConfMarker(file, Messages.CPPB_NoValidConnectionError, IMarker.SEVERITY_ERROR, 
                                                  IMarker.PRIORITY_HIGH);
        }
        
        X10PlatformConfFactory.save(file, getCurrentPlatformConf());
        monitor.worked(1);
        commitPages(true);
        if (! getCurrentPlatformConf().isComplete(false)) {
          CoreResourceUtils.addPlatformConfMarker(file, LaunchMessages.XPCFE_PlatformConfNotComplete, IMarker.SEVERITY_WARNING, 
                                                  IMarker.PRIORITY_HIGH);
        }
        monitor.worked(1);
      } catch (CoreException except) {
        DialogsFactory.createErrorBuilder().setDetailedMessage(except.getStatus())
                      .createAndOpen(getEditorSite(), LaunchMessages.XPCFE_ConfSavingErrorDlgTitle,
                                     LaunchMessages.XPCFE_ContentCopyError);
      } finally {
        monitor.done();
      }
    }
  }

  public void doSaveAs() {
  }

  public boolean isSaveAsAllowed() {
    return false;
  }
  
  // --- Overridden methods
  
  protected void createHeaderContents(final IManagedForm headerForm) {
    final Form form = headerForm.getForm().getForm();
    form.setText(LaunchMessages.XPCFE_FormTitle);
    headerForm.getToolkit().decorateFormHeading(form);
    
    this.fSaveAction = new SaveAction();
    this.fValidateAction = new ValidateAction();
    form.getToolBarManager().add(this.fSaveAction);
    form.getToolBarManager().add(this.fValidateAction);
    form.getToolBarManager().update(true);
    form.addMessageHyperlinkListener(new HyperlinkAdapter());
    this.fSaveAction.setEnabled(false);
  }
  
  protected void createPages() {
    addPages();
    
    addPropertyListener(this);
    
    int index = -1;
    for (final Object page : super.pages) {
      final IFormPage formPage = (IFormPage) page;
      if (formPage != null) {
        formPage.createPartControl(getContainer());
        setControl(++index, formPage.getPartControl());
        formPage.getPartControl().setMenu(getContainer().getMenu());
      }
    }
    for (final Object page : super.pages) {
      if (page != null) {
        ((IFormPage) page).getManagedForm().setInput(getCurrentPlatformConf());
      }
    }
    ((IFormPage) super.pages.get(0)).getManagedForm().reflow(true);
    
    if (getActivePage() == -1) {
      super.setActivePage(0);
    }
    this.fValidateAction.setImageDescriptor(this.fUncheckedPlatformImg);
    this.fValidateAction.setEnabled(getCurrentPlatformConf().isComplete(false));
    this.fSaveAction.setImageDescriptor(CppLaunchImages.createUnmanaged(CppLaunchImages.SAVE_PLATFORM_CONF));
  }
  
  public void dispose() {
    removePropertyListener(this);
    super.dispose();
  }
  
  public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
    super.init(site, input);
    
    final IProject project = ((IFileEditorInput) input).getFile().getProject();
    final IX10PlatformConf platformConf = CppLaunchCore.getInstance().getPlatformConfiguration(project);
    final IX10PlatformConfWorkCopy workCopy = platformConf.createWorkingCopy();
    workCopy.initializeToDefaultValues(project);
    
    this.fX10PlatformConfs.put(workCopy.getName(), workCopy);
    this.fCurConfiguration = workCopy;
  }
  
  // --- Internal services
  
  IX10PlatformConfWorkCopy getCurrentPlatformConf() {
    return this.fCurConfiguration;
  }
  
  void setNewPlatformConfState(final String name, final IServiceProvider serviceProvider) {
    final IX10PlatformConfWorkCopy configuration = this.fX10PlatformConfs.get(name);
    final IFile confFile = ((IFileEditorInput) getEditorInput()).getFile();

    if (configuration == null) {
      final IX10PlatformConf platformConf = X10PlatformConfFactory.createFromProvider(serviceProvider, confFile);
      final IX10PlatformConfWorkCopy workCopy = platformConf.createWorkingCopy();
      
      workCopy.setName(name);
      
      this.fX10PlatformConfs.put(name, workCopy);
      this.fCurConfiguration = workCopy;
    } else {
      this.fCurConfiguration = configuration;
    }
  }
  
  // --- Private code
  
  private synchronized void validate() {
    final IX10PlatformChecker checker = PlatformCheckerFactory.create();
  	CoreResourceUtils.deletePlatformConfMarkers(((IFileEditorInput) getEditorInput()).getFile());
    checker.addValidationListener(this);
    try {
      final IRunnableWithProgress runnable = new IRunnableWithProgress() {
      
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        	final SubMonitor subMonitor = SubMonitor.convert(monitor, 10);
          checker.validateCppCompilationConf(getCurrentPlatformConf(), subMonitor.newChild(5));
          final ICppCompilationConf compConf = getCurrentPlatformConf().getCppCompilationConf();
          if (compConf.getValidationStatus() == EValidationStatus.VALID) {
          	checker.validateCommunicationInterface(getCurrentPlatformConf(), subMonitor.newChild(5));
          } else {
          	subMonitor.done();
          }
        }
      
      };

      try {
        new ProgressMonitorDialog(getEditorSite().getShell()).run(true, true, runnable);
      } catch (InterruptedException except) {
      
      } catch (InvocationTargetException except) {
      
      }
    } finally {
      checker.removeValidationListener(this);
    }
  }
  
  // --- Private classes
  
  private final class ValidateAction extends Action {
    
    ValidateAction() {
      super(LaunchMessages.XPCFE_ValidationPlatformActionMsg);
    }
    
    // --- Overridden methods
    
    public void run() {
      validate();
    }
    
  }
  
  private final class SaveAction extends Action {
    
    SaveAction() {
      super(LaunchMessages.XPCFE_SavePlatformActionMsg);
    }
    
    // --- Overridden methods
    
    public void run() {
      doSave(new NullProgressMonitor());
    }
    
  }
  
  private static final class BuildChangeListener implements IResourceChangeListener {

    // --- Interface methods implementation
    
    public void resourceChanged(final IResourceChangeEvent event) {
      if (event.getType() == IResourceChangeEvent.POST_BUILD) {
        this.fHasFinished = true;
      }
    }
    
    // --- Internal services
    
    boolean hasFinished() {
      return this.fHasFinished;
    }
    
    // --- Fields
    
    private boolean fHasFinished;
    
  }
  
  // --- Fields
  
  private final Map<String, IX10PlatformConfWorkCopy> fX10PlatformConfs = new HashMap<String, IX10PlatformConfWorkCopy>();
  
  private IX10PlatformConfWorkCopy fCurConfiguration; 
  
  private IAction fSaveAction;
  
  private IAction fValidateAction;
  
  private final Object fConnKey = new Object();
  
  
  private final ImageDescriptor fValidPlatformImg = CppLaunchImages.createUnmanaged(CppLaunchImages.VALID_PLATFORM_CONF);
  
  private final ImageDescriptor fInvalidPlatformImg = CppLaunchImages.createUnmanaged(CppLaunchImages.INVALID_PLATFORM_CONF);
  
  private final ImageDescriptor fErrorPlatformImg = CppLaunchImages.createUnmanaged(CppLaunchImages.PLATFORM_CONF_VALIDATION_ERROR);
  
  private final ImageDescriptor fUncheckedPlatformImg = CppLaunchImages.createUnmanaged(CppLaunchImages.UNCHEKED_PLATFORM_CONF);

}
