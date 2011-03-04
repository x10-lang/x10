/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors;

import java.io.File;
import java.util.Collection;

import org.eclipse.imp.utils.Pair;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.services.core.IServiceProvider;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import x10dt.ui.launch.core.Constants;
import x10dt.ui.launch.core.platform_conf.ETargetOS;
import x10dt.ui.launch.core.utils.SWTFormUtils;
import x10dt.ui.launch.cpp.LaunchMessages;
import x10dt.ui.launch.cpp.builder.target_op.ITargetOpHelper;
import x10dt.ui.launch.cpp.builder.target_op.TargetOpHelperFactory;
import x10dt.ui.launch.cpp.editors.form_validation.FormCheckerFactory;
import x10dt.ui.launch.cpp.editors.form_validation.IFormControlChecker;
import x10dt.ui.launch.cpp.platform_conf.IConnectionConf;
import x10dt.ui.launch.cpp.platform_conf.ICppCompilationConf;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;
import x10dt.ui.launch.cpp.utils.PTPConfUtils;
import x10dt.ui.launch.cpp.utils.PTPUtils;


abstract class AbstractCommonSectionFormPart extends AbstractCompleteFormPart implements IFormPart {
 
  AbstractCommonSectionFormPart(final Composite parent, final X10FormPage formPage) {
    final FormToolkit formToolkit = formPage.getManagedForm().getToolkit();
    if (parent == null) {
      this.fSection = null;
    } else {
      this.fSection = formToolkit.createSection(parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    }
    this.fFormPage = formPage;
  }
  
  AbstractCommonSectionFormPart(final X10FormPage formPage) {
    this(null, formPage);
  }
  
  // --- Interface methods implementation
  
  public final void initialize(final IManagedForm managedForm) {
    this.fManagedForm = managedForm;
  }
  
  public final boolean isDirty() {
    return this.fIsDirty;
  }
  
  public final boolean isStale() {
    return this.fIsStale;
  }
  
  public final void refresh() {
    getPlatformConf().refresh();
    setFormInput(getPlatformConf());
    
    this.fIsDirty = false;
    this.fIsStale = false;
  }
  
  public final void setFocus() {
    final Control client = this.fSection.getClient();
    if (client != null) {
      client.setFocus();
    }
  }
  
  public boolean setFormInput(final Object input) {
    return false;
  }
  
  // --- Overridden methods
  
  public final void commit(final boolean onSave) {
    if (onSave) {
      this.fIsDirty = false;
      this.fManagedForm.dirtyStateChanged();
    }
  }
  
  // --- Code for descendants
  
  protected final Pair<Text, Button>  createLabelTextBrowseBt(final Composite parent, final String labelText, 
                                                              final String buttonText, final FormToolkit toolkit) {
    return createLabelTextBrowseBt(parent, labelText, buttonText, toolkit, null, 1);
  }
  
  protected final Pair<Text, Button>  createLabelTextBrowseBt(final Composite parent, final String labelText, 
                                                              final String buttonText, final FormToolkit toolkit, 
                                                              final Collection<Control> controlsContainer) {
    return createLabelTextBrowseBt(parent, labelText, buttonText, toolkit, controlsContainer, 3);
  }
  
  protected final Pair<Text, Button>  createLabelTextBrowseBt(final Composite parent, final String labelText, 
                                                              final String buttonText, final FormToolkit toolkit, 
                                                              final Collection<Control> controlsContainer,
                                                              final int textHeightFactor) {
    final Pair<Text, Button> pair = SWTFormUtils.createLabelTextButton(parent, labelText, buttonText, toolkit, 
                                                                       controlsContainer, textHeightFactor);
    if (controlsContainer != null) {
      controlsContainer.remove(pair.second);
    }
    pair.second.addSelectionListener(new DirectoryDialogSelectionListener(pair.first));
    return pair;
  }
  

  protected ITargetOpHelper createTargetOpHelper() {
    final IX10PlatformConf platformConf = getPlatformConf();
    final ICppCompilationConf cppCompConf = platformConf.getCppCompilationConf();
    final IConnectionConf connConf = platformConf.getConnectionConf();
    return TargetOpHelperFactory.create(connConf.isLocal(), cppCompConf.getTargetOS() == ETargetOS.WINDOWS, 
                                        connConf.getConnectionName());
  }
  
  protected final X10FormPage getFormPage() {
    return this.fFormPage;
  }
  
  protected final IX10PlatformConfWorkCopy getPlatformConf() {
    return ((X10PlatformConfFormEditor) this.fFormPage.getEditor()).getCurrentPlatformConf();
  }
  
  protected final Section getSection() {
    return this.fSection;
  }
  
  protected final void handleEmptyTextValidation(final Combo combo, final String controlInfo) {
    final IFormControlChecker checker = FormCheckerFactory.createEmptyControlChecker(this.fFormPage, combo, controlInfo);
    checker.validate(combo.getText().trim());
  }
  
  protected final void handleEmptyTextValidation(final Text text, final String controlInfo) {
    final IFormControlChecker checker = FormCheckerFactory.createEmptyControlChecker(this.fFormPage, text, controlInfo);
    checker.validate(text.getText().trim());
  }
  
  protected final boolean handleLocalPathValidation(final Text text, final String controlInfo) {
      return handleLocalPathValidation(text, null, controlInfo);
  }

  protected final boolean handleLocalPathValidation(final Text text, final String pathSuffix, final String controlInfo) {
    final IFormControlChecker checker = FormCheckerFactory.createLocalPathControlChecker(this.fFormPage, text, controlInfo);
    String fieldContents= text.getText().trim();
    String path = (pathSuffix != null && pathSuffix.length() > 0) ? fieldContents + File.separator + pathSuffix : fieldContents;

    return checker.validate(path);
  }

  protected final boolean handlePathValidation(final Text text, final String controlInfo) {
    return handlePathValidation(text, null, controlInfo);
  }

  protected final boolean handlePathValidation(final Text text, final String pathSuffix, final String controlInfo) {
    final ITargetOpHelper targetOpHelper = createTargetOpHelper();
    if (targetOpHelper != null) {
      final IFormControlChecker checker = FormCheckerFactory.createValidPathControlChecker(targetOpHelper, this.fFormPage, 
                                                                                           text, controlInfo);
      final String fieldContents = text.getText().trim();
      final String path = (pathSuffix != null && pathSuffix.length() > 0) ? fieldContents + File.separator + pathSuffix : fieldContents;
      final String remotePath = targetOpHelper.getTargetSystemPath(path);

      return checker.validate(remotePath);
    }
    return false;
  }

  protected final boolean handleFolderAndChildValidation(final Text text, final String folderChild, 
                                                         final String folderMsgLabel, final String childMsgLabel) {
    if (! handlePathValidation(text, folderMsgLabel)) {
      return false;
    }
    return handlePathValidation(text, folderChild, childMsgLabel);
  }

  protected final void setNewPlatformConfState(final String name, final IServiceProvider serviceProvider) {
    ((X10PlatformConfFormEditor) this.fFormPage.getEditor()).setNewPlatformConfState(name, serviceProvider);
  }
  
  protected final void updateDirtyState(final IManagedForm managedForm) {
    final IX10PlatformConfWorkCopy platformConf = getPlatformConf();
    if (isDirty()) {
      if (! platformConf.isDirty()) {
        this.fIsDirty = false;
        this.fIsStale = false;
        managedForm.dirtyStateChanged();
      }
    } else {
      if (platformConf.isDirty()) {
        this.fIsDirty = true;
        managedForm.dirtyStateChanged();
      }
      if (platformConf.isStale()) {
        this.fIsStale = true;
        managedForm.staleStateChanged();
      }
    }
  }
  
  // --- Internal classes
  
  final class DirectoryDialogSelectionListener implements SelectionListener {
    
    DirectoryDialogSelectionListener(final Text text) {
      this.fText = text;
    }
    
    // --- Interface methods implementation
    
    public void widgetDefaultSelected(final SelectionEvent event) {
      widgetSelected(event);
    }

    public void widgetSelected(final SelectionEvent event) {
      final String path;
      if (getPlatformConf().getConnectionConf().isLocal()) {
        final DirectoryDialog dialog = new DirectoryDialog(getFormPage().getSite().getShell());
        dialog.setText(LaunchMessages.ACSFP_SelectDirLoc);
        path = dialog.open();
      } else {
        final IRemoteConnection rmConnection = PTPConfUtils.findRemoteConnection(getPlatformConf().getConnectionConf());
        assert rmConnection != null; // By construction it should never be null.
        path = PTPUtils.remoteBrowse(getFormPage().getSite().getShell(), rmConnection, LaunchMessages.ACSFP_SelectDirLoc, 
                                     Constants.EMPTY_STR, true /* browseDirectory */);
      }
      if (path != null) {
        this.fText.setText(path);
      }
    }
    
    // --- Fields
    
    private final Text fText;
    
  }
  
  final class FileDialogSelectionListener implements SelectionListener {
    
    FileDialogSelectionListener(final Text text) {
      this.fText = text;
    }
    
    // --- Interface methods implementation
    
    public void widgetDefaultSelected(final SelectionEvent event) {
      widgetSelected(event);
    }

    public void widgetSelected(final SelectionEvent event) {
      final String path;
      if (getPlatformConf().getConnectionConf().isLocal()) {
        final FileDialog dialog = new FileDialog(getFormPage().getSite().getShell());
        dialog.setText(LaunchMessages.ACSFP_SelectFileLoc);
        path = dialog.open();
      } else {
        final IRemoteConnection rmConnection = PTPConfUtils.findRemoteConnection(getPlatformConf().getConnectionConf());
        assert rmConnection != null; // By construction it should never be null.
        path = PTPUtils.remoteBrowse(getFormPage().getSite().getShell(), rmConnection, LaunchMessages.ACSFP_SelectDirLoc, 
                                     Constants.EMPTY_STR, false /* browseDirectory */);
      }
      if (path != null) {
        this.fText.setText(path);
      }
    }
    
    // --- Fields
    
    private final Text fText;
    
  }
  
  // --- Private code
  
  private final Section fSection;
  
  private final X10FormPage fFormPage;
  
  private IManagedForm fManagedForm;
  
  private boolean fIsDirty;
  
  private boolean fIsStale;

}
