/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.core.dialogs;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import x10dt.ui.launch.core.LaunchCore;
import x10dt.ui.launch.core.Messages;
import x10dt.ui.launch.core.utils.StringUtils;


final class ErrorDialogBuilder implements IErrorDialogBuilder {
  
  // --- Interface methods implementation

  public int createAndOpen(final Shell shell, final String dialogTitle, final String topMessage) {
    if ((shell == null) || shell.isDisposed()) {
      LaunchCore.log(IStatus.ERROR, Messages.EDB_NoShellError);
      return Window.CANCEL;
    } else {
      if (this.fShouldLog) {
        createLogMessage(topMessage);
      }
      return launch(shell, dialogTitle, topMessage);
    }
  }

  public int createAndOpen(final IShellProvider shellProvider, final String dialogTitle, final String topMessage) {
    final boolean shouldLog = this.fShouldLog;
    final String detailedMessage = this.fDetailedMessage;
    final Image image = this.fImage;
    if (shouldLog) {
      createLogMessage(topMessage);
    }
    if ((shellProvider.getShell() == null) || shellProvider.getShell().isDisposed()) {
      final int[] result = new int[1];
      Display display = Display.getCurrent();
      if (display == null) {
        final IWorkbenchWindow workbenchWindow = LaunchCore.getInstance().getWorkbench().getActiveWorkbenchWindow();
        if (workbenchWindow == null) {
          LaunchCore.log(IStatus.ERROR, Messages.EDB_NoShellError);
          return Window.CANCEL;
        }
        display = workbenchWindow.getShell().getDisplay();
      }
      display.syncExec(new Runnable() {
        
        public void run() {
          result[0] = new ErrorAndLogDialog(shellProvider.getShell(), dialogTitle, topMessage, detailedMessage, image, 
                                            shouldLog).open();
        }
        
      });
      return result[0];
    } else {
      return launch(shellProvider.getShell(), dialogTitle, topMessage);
    }
  }
  
  public IErrorDialogBuilder setDetailedMessage(final String detailedMessage) {
    this.fDetailedMessage = detailedMessage;
    return this;
  }
  
  public IErrorDialogBuilder setDetailedMessage(final Throwable exception) {
    if (exception instanceof CoreException) {
      setDetailedMessage(((CoreException) exception).getStatus());
    } else {
      this.fDetailedMessage = StringUtils.getStackTrace(exception);
      this.fThrowable = exception;
    }
    return this;
  }
  
  public IErrorDialogBuilder setDetailedMessage(final IStatus status) {
    this.fStatus = status;
    if (status.getException() == null) {
      this.fDetailedMessage = status.getMessage();
    } else {
      this.fDetailedMessage = NLS.bind(Messages.DF_LineBreak, status.getMessage(), 
                                       StringUtils.getStackTrace(status.getException()));
    }
    return this;
  }
  
  public IErrorDialogBuilder setDetailedMessage(final InvocationTargetException exception) {
    final Throwable throwable = exception.getTargetException();
    this.fTargetException = exception;
    if (throwable instanceof CoreException) {
      setDetailedMessage(((CoreException) throwable).getStatus());
    } else {
      this.fDetailedMessage = StringUtils.getStackTrace(throwable);
    }
    return this;
  }
  
  public IErrorDialogBuilder setImage(final Image image) {
    this.fImage = image;
    return this;
  }

  public IErrorDialogBuilder setShouldLogFlag(boolean shouldLog) {
    this.fShouldLog = shouldLog;
    return this;
  }
  
  // --- Private code
  
  private void createLogMessage(final String topMessage) {
    if (this.fDetailedMessage == null) {
      LaunchCore.log(IStatus.ERROR, this.fDetailedMessage);
    } else {
      if (this.fThrowable != null) {
        LaunchCore.log(IStatus.ERROR, topMessage, this.fThrowable);
      } else if (this.fStatus != null) {
        createLogMessage(topMessage, this.fStatus);
      } else if (this.fTargetException != null) {
        final Throwable throwable = this.fTargetException.getTargetException();
        if (throwable instanceof CoreException) {
          createLogMessage(topMessage, ((CoreException) throwable).getStatus());
        } else {
          LaunchCore.log(IStatus.ERROR, topMessage, throwable);
        }
      } else {
        LaunchCore.log(IStatus.ERROR, NLS.bind(Messages.DF_LineBreak, topMessage, this.fDetailedMessage));
      }
    }
  }
  
  private void createLogMessage(final String topMessage, final IStatus status) {
    if (status.getException() == null) {
      LaunchCore.log(IStatus.ERROR, NLS.bind(Messages.DF_LineBreak, topMessage, status.getMessage()));
    } else {
      LaunchCore.log(IStatus.ERROR, status.getMessage(), status.getException());
    }
  }
  
  private int launch(final Shell shell, final String dialogTitle, final String topMessage) {
    if (Thread.currentThread() == shell.getDisplay().getThread()) {
      return new ErrorAndLogDialog(shell, dialogTitle, topMessage, this.fDetailedMessage, this.fImage, 
                                   this.fShouldLog).open();
    } else {
      final int[] result = new int[1];
      final boolean shouldLog = this.fShouldLog;
      final String detailedMessage = this.fDetailedMessage;
      final Image image = this.fImage;
      shell.getDisplay().syncExec(new Runnable() {

        public void run() {
          result[0] = new ErrorAndLogDialog(shell, dialogTitle, topMessage, detailedMessage, image, shouldLog).open();
        }
        
      });
      return result[0];
    }
  }
  
  // --- Fields
  
  private String fDetailedMessage;
  
  private boolean fShouldLog = true;
  
  private Image fImage;
  
  private Throwable fThrowable;
  
  private IStatus fStatus;
  
  private InvocationTargetException fTargetException;

}
