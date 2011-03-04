/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.core.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;

import x10dt.ui.launch.core.Messages;
import x10dt.ui.launch.core.utils.UIUtils;


final class ErrorAndLogDialog extends Dialog {

  ErrorAndLogDialog(final Shell parentShell, final String dialogTitle, final String topMessage, final String detailedMessage,
                    final Image image, final boolean showErrorLogLink) {
    super(parentShell);
    this.fDialogTitle = dialogTitle;
    this.fTopMessage = topMessage;
    this.fDetailedMessage = detailedMessage;
    if (image == null) {
      this.fImage = parentShell.getDisplay().getSystemImage(SWT.ICON_ERROR);
    } else {
      this.fImage = image;
    }
    this.fShowErroLogLink = showErrorLogLink;
  }
  
  // --- Abstract methods implementation

  protected Image getImage() {
    return this.fImage;
  }
  
  // --- Overridden methods
  
  protected void configureShell(final Shell shell) {
    super.configureShell(shell);
    shell.setText(this.fDialogTitle);
  }
  
  protected void createButtonsForButtonBar(final Composite parent) {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
  }
  
  protected Control createDialogArea(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(1, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    composite.setFont(parent.getFont());
    
    createMessageArea(composite);
    if (this.fShowErroLogLink) {
      createErrorLogLink(composite);
    }
    
    if (this.fDetailedMessage != null) {
      createDetailsArea(composite);
    }
    
    return composite;
  }
  
  protected boolean isResizable() {
    return true;
  }
  
  // --- Private code
  
  private void copyDetailsToClipboard(final StyledText styledText) {
    if (this.fClipboard != null) {
      this.fClipboard.dispose();
    }    
    this.fClipboard = new Clipboard(getShell().getDisplay());
    this.fClipboard.setContents(new Object[] { styledText.getSelectionText() }, new Transfer[] { TextTransfer.getInstance() });
  }
  
  private void createDetailsArea(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(1, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    
    final StyledText styledText = new StyledText(composite, SWT.WRAP | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true)
                   .hint(convertHorizontalDLUsToPixels(300), convertHorizontalDLUsToPixels(100))
                   .applyTo(styledText);
    styledText.setText(this.fDetailedMessage);
    styledText.setFocus();
    
    final Menu copyMenu = new Menu(styledText);
    final MenuItem copyItem = new MenuItem(copyMenu, SWT.NONE);
    copyItem.addSelectionListener(new SelectionListener() {

      public void widgetSelected(final SelectionEvent event) {
        copyDetailsToClipboard(styledText);
      }

      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetDefaultSelected(event);
      }
      
    });
    copyItem.setText(Messages.EALD_CopyMsg);
    styledText.setMenu(copyMenu);
  }
  
  private void createErrorLogLink(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(1, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    composite.setFont(parent.getFont());
    
    final StyledText link = new StyledText(composite, SWT.READ_ONLY);
    link.setText(NLS.bind(Messages.EALD_ErrorLogStartMsg, Messages.EALD_ErrorLogView));
    link.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
    final StyleRange styleRange = new StyleRange();
    styleRange.start = link.getText().length() - Messages.EALD_ErrorLogView.length();
    styleRange.length = Messages.EALD_ErrorLogView.length();
    styleRange.underline = true;
    styleRange.underlineStyle = SWT.UNDERLINE_LINK;
    styleRange.underlineColor = null;
    styleRange.foreground = getShell().getDisplay().getSystemColor(SWT.COLOR_RED);
    link.setStyleRange(styleRange);

    link.addMouseListener(new MouseListener() {
      
      public void mouseUp(final MouseEvent event) {
      }
      
      public void mouseDown(final MouseEvent event) {
        int offset = link.getOffsetAtLocation(new Point(event.x, event.y));
        final StyleRange range = link.getStyleRangeAtOffset(offset);
        if (range != null && range.underlineStyle == SWT.UNDERLINE_LINK) {
          try {
            UIUtils.showErrorLogView();
          } catch (PartInitException except) {
            // Let's forget.
          }
          setReturnCode(OK);
          close();
        }
      }
      
      public void mouseDoubleClick(final MouseEvent event) {
      }
      
    });
    
  }
  
  private void createMessageArea(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    composite.setFont(parent.getFont());
    
    final Label imageLabel = new Label(composite, SWT.NONE);
    imageLabel.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, false, false));
    imageLabel.setImage(this.fImage);
    
    final Label messageLabel = new Label(composite, SWT.NONE);
    GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
                   .hint(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH),SWT.DEFAULT)
                   .applyTo(messageLabel);
    messageLabel.setText(this.fTopMessage);
  }
  
  // --- Fields
  
  private final String fDialogTitle;
  
  private final String fTopMessage;
  
  private final String fDetailedMessage;
  
  private final Image fImage;
  
  private final boolean fShowErroLogLink;
  
  private Clipboard fClipboard;

}
