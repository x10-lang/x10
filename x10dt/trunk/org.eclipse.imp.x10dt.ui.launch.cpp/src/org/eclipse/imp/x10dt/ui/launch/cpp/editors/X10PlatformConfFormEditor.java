/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.editors;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.x10dt.ui.launch.core.dialogs.DialogsFactory;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EValidationStatus;
import org.eclipse.imp.x10dt.ui.launch.cpp.CppLaunchCore;
import org.eclipse.imp.x10dt.ui.launch.cpp.CppLaunchImages;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.X10PlatformConfFactory;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.validation.IX10PlatformChecker;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.validation.IX10PlatformValidationListener;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.validation.PlatformCheckerFactory;
import org.eclipse.imp.x10dt.ui.launch.cpp.utils.PTPConfUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ptp.remote.core.exception.RemoteConnectionException;
import org.eclipse.ptp.remotetools.environment.core.ITargetElement;
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
  
  public void platformValidated() {
    this.fValidateAction.setImageDescriptor(this.fValidPlatformImg);
    if (PTPConfUtils.findResourceManager(this.fX10PlatformConfWorkCopy.getName()) == null) {
      try {
        PTPConfUtils.createResourceManager(this.fX10PlatformConfWorkCopy);
      } catch (RemoteConnectionException except) {
        DialogsFactory.createErrorBuilder().setDetailedMessage(except)
                      .createAndOpen(getSite(), LaunchMessages.XPCFE_ResManagerCreationErrorTitle, 
                                     LaunchMessages.XPCFE_ResManagerCreationErrorMsg);
      }
    }
  }
  
  public void platformValidationFailure(final String message) {
    this.fValidateAction.setImageDescriptor(this.fInvalidPlatformImg);
    final String failureMessage = this.fX10PlatformConfWorkCopy.getCppCompilationConf().getValidationErrorMessage();
    DialogsFactory.createErrorBuilder().setDetailedMessage(failureMessage)
                  .createAndOpen(getSite(), LaunchMessages.XPCFE_ValidationFailureDlgTitle, 
                                 LaunchMessages.XPCFE_ValidationFailureDlgMsg);
  }
  
  public void platformValidationError(final Exception exception) {
    this.fValidateAction.setImageDescriptor(this.fErrorPlatformImg);
    DialogsFactory.createErrorBuilder().setDetailedMessage(exception)
                .createAndOpen(getSite(), LaunchMessages.XPCFE_ValidationErrorDlgTitle, 
                               LaunchMessages.XPCFE_ValidationErrorDlgMsg);
  }
  
  public void remoteConnectionFailure(final Exception exception) {
    final IAction validationAction = this.fValidateAction;
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
  
  // --- IConnectionTypeListener's interface methods implementation
  
  public void connectionChanged(final boolean isLocal, final String remoteConnectionName,
                                final EValidationStatus validationStatus) {
    if (isLocal) {
      getHeaderForm().getMessageManager().removeMessage(this.fConnKey);
      if (this.fValidateAction.isEnabled()) {
        this.fValidateAction.setImageDescriptor(X10PlatformConfFormEditor.this.fUncheckedPlatformImg);
      }
    }
  }
  
  // --- Abstract methods implementation

  protected void addPages() {
    setActiveEditor(this);
    setPartName(getEditorInput().getName());
    try {
      addPage(new ConnectionAndCommunicationConfPage(this, this.fX10PlatformConfWorkCopy));
      addPage(new X10CompilationConfigurationPage(this, this.fX10PlatformConfWorkCopy));
    } catch (PartInitException except) {
      CppLaunchCore.log(except.getStatus());
    }
  }

  public void doSave(final IProgressMonitor monitor) {
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
    
    this.fSaveAction = new SaveAction(CppLaunchImages.createUnmanaged(CppLaunchImages.SAVE_PLATFORM_CONF));
    this.fValidateAction = new ValidateAction(this.fUncheckedPlatformImg);
    form.getToolBarManager().add(this.fSaveAction);
    form.getToolBarManager().add(this.fValidateAction);
    form.getToolBarManager().update(true);
    form.addMessageHyperlinkListener(new HyperlinkAdapter());
    this.fSaveAction.setEnabled(false);
    this.fValidateAction.setEnabled(false);
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
        ((IFormPage) page).getManagedForm().setInput(this.fX10PlatformConfWorkCopy);
      }
    }
    
    if (getActivePage() == -1) {
      super.setActivePage(0);
    }    
  }
  
  public void dispose() {
    removePropertyListener(this);
    super.dispose();
  }
  
  public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
    super.init(site, input);
    final IProject project = ((IFileEditorInput) input).getFile().getProject();
    final IX10PlatformConf platformConf = CppLaunchCore.getInstance().getPlatformConfiguration(project);
    this.fX10PlatformConfWorkCopy = platformConf.createWorkingCopy();
    this.fX10PlatformConfWorkCopy.initializeToDefaultValues();
  }
  
  // --- Private code
  
  private void save() {
    final IFile file = ((IFileEditorInput) getEditorInput()).getFile();
    synchronized (file) {
      try {
        this.fX10PlatformConfWorkCopy.applyChanges();
        X10PlatformConfFactory.save(file, this.fX10PlatformConfWorkCopy);
        commitPages(true);
      } catch (IOException except) {
        DialogsFactory.createErrorBuilder().setDetailedMessage(except)
                      .createAndOpen(getEditorSite(), LaunchMessages.XPCFE_ConfSavingErrorDlgTitle,
                                     LaunchMessages.XPCFE_PipeConnectionFailedMsg);
      } catch (CoreException except) {
        DialogsFactory.createErrorBuilder().setDetailedMessage(except.getStatus())
                      .createAndOpen(getEditorSite(), LaunchMessages.XPCFE_ConfSavingErrorDlgTitle,
                                     LaunchMessages.XPCFE_ContentCopyError);
      }
    }
  }
  
  private synchronized void validate() {
    final IX10PlatformChecker checker = PlatformCheckerFactory.create();
    checker.addValidationListener(this);
    try {
      final IRunnableWithProgress runnable = new IRunnableWithProgress() {
      
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
          checker.validateCppCompilationConf(X10PlatformConfFormEditor.this.fX10PlatformConfWorkCopy, monitor);
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
    
    ValidateAction(final ImageDescriptor acceptConfImgDescriptor) {
      super(LaunchMessages.XPCFE_ValidationPlatformActionMsg, acceptConfImgDescriptor);
    }
    
    // --- Overridden methods
    
    public void run() {
      validate();
    }
    
  }
  
  private final class SaveAction extends Action {
    
    SaveAction(final ImageDescriptor saveConfImgDescriptor) {
      super(LaunchMessages.XPCFE_SavePlatformActionMsg, saveConfImgDescriptor);
    }
    
    // --- Overridden methods
    
    public void run() {
      save();
    }
    
  }
  
  // --- Fields
  
  private IX10PlatformConfWorkCopy fX10PlatformConfWorkCopy;
  
  private IAction fSaveAction;
  
  private IAction fValidateAction;
  
  private final Object fConnKey = new Object();
  
  
  private final ImageDescriptor fValidPlatformImg = CppLaunchImages.createUnmanaged(CppLaunchImages.VALID_PLATFORM_CONF);
  
  private final ImageDescriptor fInvalidPlatformImg = CppLaunchImages.createUnmanaged(CppLaunchImages.INVALID_PLATFORM_CONF);
  
  private final ImageDescriptor fErrorPlatformImg = CppLaunchImages.createUnmanaged(CppLaunchImages.PLATFORM_CONF_VALIDATION_ERROR);
  
  private final ImageDescriptor fUncheckedPlatformImg = CppLaunchImages.createUnmanaged(CppLaunchImages.UNCHEKED_PLATFORM_CONF);

}
