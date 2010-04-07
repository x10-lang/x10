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

import org.eclipse.imp.x10dt.ui.launch.core.Constants;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.TableWrapLayout;


final class ConnectionAndCommunicationConfPage extends X10FormPage {
  
  ConnectionAndCommunicationConfPage(final X10PlatformConfFormEditor editor, final IX10PlatformConfWorkCopy x10PlatformConf) {
    super(x10PlatformConf, editor, CONN_AND_COMM_CONF_PAGE_ID, LaunchMessages.RMCP_FormPageTitle);
    
    addCompletePageChangedListener(editor);
  }
  
  // --- Overridden methods
  
  protected void createFormContent(final IManagedForm managedForm) {
    final Composite body = managedForm.getForm().getBody();
    body.setLayout(new GridLayout(2, true));
    
    final Collection<IServiceConfigurationListener> rmConfPageListeners = new ArrayList<IServiceConfigurationListener>();
    
    final Composite upper = managedForm.getToolkit().createComposite(body, SWT.NONE);
    upper.setFont(body.getFont());
    final TableWrapLayout upperLayout = new TableWrapLayout();
    upperLayout.numColumns = 4;
    upperLayout.makeColumnsEqualWidth = true;
    upper.setLayout(new GridLayout(4, true));
    upper.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 2, 1));
    new Label(upper, SWT.NONE).setText(Constants.EMPTY_STR);
    managedForm.addPart(new ConfNameSectionPart(upper, this, getPlatformConf(), rmConfPageListeners));
    
    final Composite left = managedForm.getToolkit().createComposite(body);
    left.setFont(body.getFont());
    left.setLayout(new GridLayout(1,false));
    final GridData gd1 = new GridData(SWT.FILL, SWT.FILL, true, true);
    gd1.widthHint = 0;
    gd1.heightHint = 0;
    left.setLayoutData(gd1);
    
    final ConnectionSectionPart connSection = new ConnectionSectionPart(left, this, getPlatformConf());
    rmConfPageListeners.add(connSection);
    managedForm.addPart(connSection);
    
    final Composite right = managedForm.getToolkit().createComposite(body);
    right.setFont(body.getFont());
    right.setLayout(new GridLayout(1,false));
    final GridData gd2 = new GridData(SWT.FILL, SWT.FILL, true, true);
    gd2.widthHint = 0;
    gd2.heightHint = 0;
    right.setLayoutData(gd2);
    
    final CommunicationInterfaceSectionPart ciSection = new CommunicationInterfaceSectionPart(right, this, getPlatformConf());
    rmConfPageListeners.add(ciSection);
    managedForm.addPart(ciSection);
  }
  
  public void dispose() {
    removeCompletePageChangedListener((X10PlatformConfFormEditor) getEditor());
    super.dispose();
  }
  
  // --- Fields    
  
  private static final String CONN_AND_COMM_CONF_PAGE_ID = "x10.connection-and-communication.page"; //$NON-NLS-1$
  
}
