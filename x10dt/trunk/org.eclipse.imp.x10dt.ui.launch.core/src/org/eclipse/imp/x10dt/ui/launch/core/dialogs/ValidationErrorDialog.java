/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.dialogs;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;


final class ValidationErrorDialog extends IconAndMessageDialog {
  
  ValidationErrorDialog(final Shell parentShell, final String dialogTitle, final String errorMessage, final IStatus status,
                        final String codeText) {
    super(parentShell);
    this.fDialogTitle = dialogTitle;
    super.message = errorMessage;
    this.fStatus = status;
    this.fCodeText = codeText;
  }
  
  // --- Abstract methods implementation
  
  protected Image getImage() {
    return getErrorImage();
  }
  
  // --- Overridden methods
  
  protected void createButtonsForButtonBar(final Composite parent) {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
  }
  
  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    shell.setText(this.fDialogTitle);
  }
  
  protected Control createDialogArea(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, true, 2, 1));
    composite.setFont(parent.getFont());
    
    createMessageArea(composite);
    if (this.fStatus != null) {
      createDetailsArea(parent);
    }
    
    return composite;
  }
  
  protected Control createMessageArea(final Composite composite) {
    super.createMessageArea(composite);
    GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, true)
                   .hint(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT)
                   .applyTo(super.messageLabel);
    return composite;
  }
  
  protected boolean isResizable() {
    return true;
  }
  
  // --- Private code
  
  private void copyDetailsToClipboard() {
    if (this.fClipboard != null) {
      this.fClipboard.dispose();
    }
    final StringBuilder detailsBuilder = new StringBuilder();
    populateDetailsBuffer(this.fStatus, detailsBuilder, 0);
    
    this.fClipboard = new Clipboard(this.fDetailsList.getDisplay());
    this.fClipboard.setContents(new Object[] { detailsBuilder.toString() }, new Transfer[] { TextTransfer.getInstance() });
  }
  
  private void createDetailsArea(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

    this.fDetailsBt = new Button(composite, SWT.PUSH);
    this.fDetailsBt.setLayoutData(new GridData(SWT.CENTER, SWT.NONE, true, false));
    this.fDetailsBt.setText(Messages.VED_DetailsBtShowMsg);
    this.fDetailsBt.addSelectionListener(new SelectionListener() {
        
      public void widgetSelected(final SelectionEvent event) {
        toggleDetailsArea();
      }
        
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
        
    });
    
    if ((this.fCodeText != null) && (this.fCodeText.length() > 0)) {
      this.fShowTestCodeBt = new Button(composite, SWT.PUSH);
      this.fShowTestCodeBt.setLayoutData(new GridData(SWT.CENTER, SWT.NONE, true, false));
      this.fShowTestCodeBt.setText(Messages.VED_ShowTestBtMsg);
      this.fShowTestCodeBt.addSelectionListener(new SelectionListener() {
      
        public void widgetSelected(final SelectionEvent event) {
          new TextDialog(getShell(), Messages.VED_ShowTestDialogTitle, ValidationErrorDialog.this.fCodeText).open();
        }
      
        public void widgetDefaultSelected(final SelectionEvent event) {
          widgetSelected(event);
        }
      
      });
    }
  }
  
  private List createDropDownList(final Composite parent) {
    final List list = new List(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
    final int nbElements = populateList(0, list, this.fStatus, 0);
    GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
    final int max = Math.min(list.getItemHeight() * (nbElements + 1), getParentShell().getSize().y);
    data.heightHint = max;
    list.setLayoutData(data);
    list.setFont(parent.getFont());
    this.fDetailsShown = true;
    
    final Menu copyMenu = new Menu(list);
    MenuItem copyItem = new MenuItem(copyMenu, SWT.NONE);
    copyItem.addSelectionListener(new SelectionListener() {

      public void widgetSelected(final SelectionEvent event) {
        copyDetailsToClipboard();
      }

      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetDefaultSelected(event);
      }
      
    });
    copyItem.setText(Messages.VED_CopyMenuItem);
    list.setMenu(copyMenu);
    
    return list;
  }
  
  private int populateList(final int nbElements, final List listToPopulate, final IStatus buildingStatus, final int nesting) {
    final Throwable exception = buildingStatus.getException();
    boolean isCoreException = exception instanceof CoreException;
    boolean incrementNesting = false;
    int counter = nbElements;

    final StringBuffer sb = new StringBuffer();
    for (int i = 0; i < nesting; ++i) {
      sb.append(NESTING_INDENT);
    }
    final String[] statusMsg = buildingStatus.getMessage().split("\n"); //$NON-NLS-1$
    for (final String str : statusMsg) {
      ++counter;
      listToPopulate.add(sb.toString() + str);
    }
    incrementNesting = true;

    if (! isCoreException && (exception != null)) {
      final StringBuilder exceptMsgBuilder = new StringBuilder();
      for (int i = 0; i < nesting; ++i) {
        exceptMsgBuilder.append(NESTING_INDENT);
      }
      if (exception.getLocalizedMessage() == null) {
        exceptMsgBuilder.append(exception.toString());
      } else {
        exceptMsgBuilder.append(exception.getLocalizedMessage());
      }
      ++counter;
      listToPopulate.add(exceptMsgBuilder.toString());
      
      final StringBuilder newIndent = new StringBuilder();
      for (int i = -1; i < nesting; ++i) {
        newIndent.append(NESTING_INDENT);
      }
      for (final StackTraceElement traceElement : exception.getStackTrace()) {
        ++counter;
        listToPopulate.add(newIndent.toString() + traceElement.toString());
      }
      
      incrementNesting = true;
    }

    // Look for a nested core exception
    if (isCoreException) {
      final CoreException coreException = (CoreException) exception;
      // Only print the exception message if it is not contained in the parent message
      if (super.message == null || super.message.indexOf(coreException.getStatus().getMessage()) == -1) {
        populateList(counter, listToPopulate, coreException.getStatus(), incrementNesting ? nesting + 1 : nesting);
      }
    }

    for (final IStatus child : buildingStatus.getChildren()) {
      populateList(counter, listToPopulate, child, incrementNesting ? nesting + 1 : nesting);
    }
    
    return counter;
  }
  
  private void populateDetailsBuffer(final IStatus status, final StringBuilder detailsBuilder, int nesting) {
    for (int i = 0; i < nesting; ++i) {
      detailsBuilder.append(NESTING_INDENT);
    }
    detailsBuilder.append(status.getMessage()).append('\n');

    // Look for a nested core exception
    final Throwable exception = status.getException();
    if (exception instanceof CoreException) {
      final CoreException coreException = (CoreException) exception;
      populateDetailsBuffer(coreException.getStatus(), detailsBuilder, nesting + 1);
    } else if (exception != null) {
      for (int i = 0; i < nesting; ++i) {
        detailsBuilder.append(NESTING_INDENT);
      }
      if (exception.getLocalizedMessage() == null) {
        detailsBuilder.append(exception.toString());
      } else {
        detailsBuilder.append(exception.toString());
      }
      detailsBuilder.append('\n');
      
      final StringBuilder indentBuilder = new StringBuilder();
      for (int i = -1; i < nesting; ++i) {
        indentBuilder.append(NESTING_INDENT);
      }
      for (final StackTraceElement traceElement : exception.getStackTrace()) {
        detailsBuilder.append(indentBuilder).append(traceElement).append('\n');
      }
    }

    for (final IStatus child : status.getChildren()) {
      populateDetailsBuffer(child, detailsBuilder, nesting + 1);
    }
  }
  
  private void toggleDetailsArea() {
    final Point windowSize = getShell().getSize();
    final Point oldSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
    if (this.fDetailsShown) {
      this.fDetailsList.dispose();
      this.fDetailsShown = false;
      this.fDetailsBt.setText(Messages.VED_DetailsBtShowMsg);
    } else {
      this.fDetailsList = createDropDownList((Composite) getContents());
      this.fDetailsBt.setText(Messages.VED_DetailsBtHideMsg);
      getContents().getShell().redraw();
    }
    final Point newSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
    getShell().setSize(new Point(windowSize.x, windowSize.y + (newSize.y - oldSize.y)));
  }
  
  // --- Fields
  
  private final String fDialogTitle;
  
  private final IStatus fStatus;
  
  private final String fCodeText;
  
  private Button fDetailsBt;
  
  private Button fShowTestCodeBt;
  
  private List fDetailsList;
  
  private boolean fDetailsShown;
  
  private Clipboard fClipboard;
  
  
  private static final String NESTING_INDENT = "  "; //$NON-NLS-1$

}