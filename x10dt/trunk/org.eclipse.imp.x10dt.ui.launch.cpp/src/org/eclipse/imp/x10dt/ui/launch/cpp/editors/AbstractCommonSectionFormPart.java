/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.editors;

import java.util.Collection;

import org.eclipse.imp.utils.Pair;
import org.eclipse.imp.x10dt.ui.launch.core.Constants;
import org.eclipse.imp.x10dt.ui.launch.core.utils.PTPUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.SWTFormUtils;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;
import org.eclipse.imp.x10dt.ui.launch.cpp.utils.PTPConfUtils;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;


abstract class AbstractCommonSectionFormPart extends AbstractCompleteFormPart implements IFormPart {
 
  AbstractCommonSectionFormPart(final Composite parent, final X10FormPage formPage, 
                                final IX10PlatformConfWorkCopy x10PlatformConf) {
    final FormToolkit formToolkit = formPage.getManagedForm().getToolkit();
    if (parent == null) {
      this.fSection = null;
    } else {
      this.fSection = formToolkit.createSection(parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    }
    this.fFormPage = formPage;
    this.fX10PlatformConf = x10PlatformConf;
  }
  
  AbstractCommonSectionFormPart(final X10FormPage formPage, final IX10PlatformConfWorkCopy x10PlatformConf) {
    this(null, formPage, x10PlatformConf);
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
    this.fX10PlatformConf.refresh();
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
    pair.second.addSelectionListener(new SelectionListener() {
      
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
          pair.first.setText(path);
        }
      }
      
    });
    return pair;
  }
  
  protected final X10FormPage getFormPage() {
    return this.fFormPage;
  }
  
  protected final IX10PlatformConfWorkCopy getPlatformConf() {
    return this.fX10PlatformConf;
  }
  
  protected final Section getSection() {
    return this.fSection;
  }
  
  protected final void handleTextValidation(final IFormChecker checker, final IManagedForm pageForm, final Control control) {
    final IManagedForm headerForm = ((SharedHeaderFormEditor) this.fFormPage.getEditor()).getHeaderForm();
    checker.check(headerForm.getMessageManager());
    checker.check(pageForm.getMessageManager(), control, pageForm);
  }
  
  protected final void updateDirtyState(final IManagedForm managedForm) {
    if (isDirty()) {
      if (! this.fX10PlatformConf.isDirty()) {
        this.fIsDirty = false;
        this.fIsStale = false;
        managedForm.dirtyStateChanged();
      }
    } else {
      if (this.fX10PlatformConf.isDirty()) {
        this.fIsDirty = true;
        managedForm.dirtyStateChanged();
      }
      if (this.fX10PlatformConf.isStale()) {
        this.fIsStale = true;
        managedForm.staleStateChanged();
      }
    }
  }
  
  // --- Private code
  
  private final Section fSection;
  
  private final X10FormPage fFormPage;
  
  private final IX10PlatformConfWorkCopy fX10PlatformConf;
  
  private IManagedForm fManagedForm;
  
  private boolean fIsDirty;
  
  private boolean fIsStale;

}
