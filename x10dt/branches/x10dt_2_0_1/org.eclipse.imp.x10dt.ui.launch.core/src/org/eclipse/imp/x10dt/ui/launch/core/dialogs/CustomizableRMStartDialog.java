/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.dialogs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.imp.x10dt.ui.launch.core.LaunchCore;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ptp.core.PTPCorePlugin;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.core.elements.attributes.ResourceManagerAttributes.State;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;


final class CustomizableRMStartDialog extends ResourceManagerStartDialog {

  CustomizableRMStartDialog(final Shell parent, final Collection<IResourceManager> resourceManagerList,
                            final String styledDialogText) {
    super(parent, resourceManagerList);
    this.fStyledDialogText = styledDialogText;
  }
  
  // --- Overridden methods
  
  protected Control createDialogArea(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(1, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    
    final Composite linkComposite = new Composite(composite, SWT.NONE);
    linkComposite.setFont(composite.getFont());
    final GridLayout layout = new GridLayout(1, false);
    layout.marginLeft = 15;
    layout.marginRight = 15;
    linkComposite.setLayout(layout);
    
    final Link linkText = new Link(linkComposite, SWT.NONE);
    linkText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    linkText.setText(this.fStyledDialogText);
    
    linkText.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        final IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
        try {
          support.getExternalBrowser().openURL(new URL(event.text));
        } catch (PartInitException except) {
          ErrorDialog.openError(getShell(), Messages.CRMSD_ErrDialogTitle, Messages.CRMSD_ErrDialogMsg, except.getStatus());
        } catch (MalformedURLException except) {
          // This should never occur.
          LaunchCore.log(IStatus.ERROR, Messages.CRMSD_MalformedJIRAURL, except);
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    
    super.createDialogArea(composite);
    
    return composite;
  }
  
  public int open() {
    final int code = super.open();
    
    final List<IResourceManager> startedRMs = new ArrayList<IResourceManager>();
    for (final IResourceManager resourceManager : PTPCorePlugin.getDefault().getUniverse().getResourceManagers()) {
      if (resourceManager.getState() == State.STARTED) {
        startedRMs.add(resourceManager);
      }
    }
    if (startedRMs.size() == 1) {
      this.fSelectedResourceManager = startedRMs.iterator().next();
    } else if (! startedRMs.isEmpty()) {
      final UniqueRMDialogSelector selectorDialog = new UniqueRMDialogSelector(getParentShell(), startedRMs);
      selectorDialog.open();
      this.fSelectedResourceManager = selectorDialog.getSelectedResourceManager();
    }
    
    return code;
  }
  
  // --- Internal services
  
  IResourceManager getSelectedResourceManager() {
    return this.fSelectedResourceManager;
  }
  
  // --- Fields
  
  private final String fStyledDialogText;
  
  private IResourceManager fSelectedResourceManager;

}
