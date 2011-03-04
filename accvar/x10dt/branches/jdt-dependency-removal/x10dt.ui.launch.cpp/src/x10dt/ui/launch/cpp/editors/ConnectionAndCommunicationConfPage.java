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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import x10dt.ui.launch.cpp.LaunchMessages;


final class ConnectionAndCommunicationConfPage extends X10FormPage {
  
  ConnectionAndCommunicationConfPage(final X10PlatformConfFormEditor editor) {
    super(editor, CONN_AND_COMM_CONF_PAGE_ID, LaunchMessages.RMCP_FormPageTitle);
    
    addCompletePageChangedListener(editor);
  }
  
  // --- Overridden methods
  
  protected void createFormContent(final IManagedForm managedForm) {
    final Composite body = managedForm.getForm().getBody();
    final TableWrapLayout bodyLayout = new TableWrapLayout();
    bodyLayout.numColumns = 2;
    bodyLayout.makeColumnsEqualWidth = true;
    body.setLayout(bodyLayout);
    
    final Collection<IServiceConfigurationListener> rmConfPageListeners = new ArrayList<IServiceConfigurationListener>();
    
    managedForm.addPart(new ConfNameSectionPart(body, this, rmConfPageListeners));
    
    final ConnectionSectionPart connSection = new ConnectionSectionPart(body, this);
    rmConfPageListeners.add(connSection);
    managedForm.addPart(connSection);
    
    final CommunicationInterfaceSectionPart ciSection = new CommunicationInterfaceSectionPart(body, this);
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
