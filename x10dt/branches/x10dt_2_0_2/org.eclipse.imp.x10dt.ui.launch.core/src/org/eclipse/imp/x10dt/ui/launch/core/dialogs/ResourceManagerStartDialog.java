/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.dialogs;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.utils.Pair;
import org.eclipse.imp.x10dt.ui.launch.core.LaunchCore;
import org.eclipse.imp.x10dt.ui.launch.core.LaunchImages;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressIndicator;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.core.elements.attributes.ResourceManagerAttributes.State;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


class ResourceManagerStartDialog extends TitleAreaDialog {

  ResourceManagerStartDialog(final Shell parent, final Collection<IResourceManager> resourceManagerList) {
    super(parent);
    this.fResManagerList = resourceManagerList;
  }
  
  // --- Overridden methods
  
  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    shell.setText(Messages.RMSD_DialogTitle);
  }
  
  protected void createButtonsForButtonBar(final Composite parent) {
    final Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    boolean shouldBeEnabled = false;
    for (final IResourceManager resourceManager : this.fResManagerList) {
      if (resourceManager.getState() == State.STARTED) {
        shouldBeEnabled = true;
        break;
      }
    }
    button.setEnabled(shouldBeEnabled);
    button.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        for (final IResourceManager resourceManager : ResourceManagerStartDialog.this.fResManagerList) {
          if (resourceManager.getState() == State.ERROR) {
            new Thread(new Runnable() {
              
              public void run() {
                try {
                  resourceManager.shutdown();
                } catch (CoreException except) {
                  LaunchCore.log(except.getStatus());
                }
              }
              
            }).start();
          }
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
  }
  
  protected Control createContents(Composite parent) {
    setDialogHelpAvailable(false);
    final Control contents = super.createContents(parent);
    setTitle(Messages.RMSD_DialogMsg);
    setMessage(Messages.RMSD_DialogDescription, IMessageProvider.INFORMATION);
    return contents;
  }
  
  protected Control createDialogArea(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    final GridLayout layout = new GridLayout(2, false);
    layout.marginLeft = 15;
    layout.marginRight = 15;
    composite.setLayout(layout);
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    final Label label = new Label(composite, SWT.NONE);
    label.setLayoutData(new GridData(SWT.CENTER, SWT.NONE, false, false));
    label.setText(Messages.RMSD_ResourceManagers);
    new Label(composite, SWT.NONE);
    
    final TableViewer viewer = new TableViewer(composite, SWT.BORDER |  SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | 
                                               SWT.FULL_SELECTION);
    viewer.setContentProvider(new ResManagerContentProvider());
    viewer.setLabelProvider(new ResManagerLabelProvider());
    viewer.setInput(this.fResManagerList);
    
    final Button startButton = new Button(composite, SWT.PUSH);
    startButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
    startButton.setText(Messages.RMSD_StartBtText);
    startButton.setToolTipText(Messages.RMSD_StartBtTooltip);
    startButton.setEnabled(false);
    
    viewer.addSelectionChangedListener(new ISelectionChangedListener() {
      
      public void selectionChanged(final SelectionChangedEvent event) {
        final IStructuredSelection struturedSelection = (IStructuredSelection) event.getSelection();
        startButton.setEnabled(struturedSelection.size() > 0);
      }
      
    });
    
    final Composite piComposite = new Composite(parent, SWT.NONE);
    piComposite.setFont(parent.getFont());
    piComposite.setLayout(layout);
    piComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    final ProgressIndicator progressIndicator = new ProgressIndicator(piComposite);
    progressIndicator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
    progressIndicator.pack();
    
    final Button cancelOptBt = new Button(piComposite, SWT.NONE);
    cancelOptBt.setImage(LaunchImages.createUnmanaged(LaunchImages.TERMINATE).createImage());
    cancelOptBt.setVisible(false);
        
    final Label taskLabel = new Label(piComposite, SWT.LEFT | SWT.WRAP);
    taskLabel.setFont(parent.getFont());
    taskLabel.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 2, 1));

    final Collection<Pair<Thread, IProgressMonitor>> rmThreads = new ArrayList<Pair<Thread, IProgressMonitor>>();
    startButton.addSelectionListener(new SelectionListener() {

      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }

      public void widgetSelected(final SelectionEvent event) {
        progressIndicator.beginAnimatedTask();
        cancelOptBt.setVisible(true);
        startButton.setEnabled(false);
        getButton(IDialogConstants.OK_ID).setEnabled(false);

        final Object[] resManagers = ((IStructuredSelection) viewer.getSelection()).toArray();
        int i = -1;
        final boolean[] finished = new boolean[resManagers.length];
        for (final Object object : resManagers) {
          final int index = ++i;
          final IResourceManager resourceManager = (IResourceManager) object;
          final String taskLabelName = NLS.bind(Messages.RMSD_TaskMsg, resourceManager.getName());
          
          final IProgressMonitor monitor = new NullProgressMonitor();
          
          final Thread rmThread = new Thread(new Runnable() {

            public void run() {
              try {
                resourceManager.startUp(monitor);
              } catch (Exception except) {
                // Do nothing.
              }
              finished[index] = true;
              getShell().getDisplay().asyncExec(new Runnable() {
                public void run() {
                  viewer.refresh(resourceManager);

                  boolean allFinished = true;
                  for (boolean finishFlag : finished) {
                    if (! finishFlag) {
                      allFinished = false;
                      break;
                    }
                  }
                  if (allFinished) {
                    taskLabel.setText(EMPTY_STR);
                    progressIndicator.done();
                    cancelOptBt.setVisible(false);
                    startButton.setEnabled(true);
                    getButton(IDialogConstants.OK_ID).setEnabled(true);
                  }
                }
              });
            }

          });
          rmThreads.add(new Pair<Thread, IProgressMonitor>(rmThread, monitor));
          rmThread.start();

          final Thread rmUpdateThread = new Thread(new Runnable() {

            public void run() {
              while (! finished[index]) {
                try {
                  getShell().getDisplay().syncExec(new Runnable() {
                    public void run() {
                      taskLabel.setText(taskLabelName);
                      viewer.refresh(resourceManager);
                    }
                  });
                  Thread.sleep(500);
                } catch (InterruptedException except) {
                  // Do nothing.
                }
              }
            }

          });
          rmThreads.add(new Pair<Thread, IProgressMonitor>(rmUpdateThread, null));
          rmUpdateThread.start();
        }
      }
    });
    
    cancelOptBt.addSelectionListener(new SelectionListener() {
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetDefaultSelected(event);
      }
      
      public void widgetSelected(final SelectionEvent event) {
        for (final Pair<Thread, IProgressMonitor> rmThread : rmThreads) {
          if (rmThread.second != null) {
            new Thread(new Runnable() {
              
              public void run() {
                rmThread.second.setCanceled(true);
                while (! rmThread.first.isInterrupted()) ;
                getShell().getDisplay().asyncExec(new Runnable() {
                  public void run() {
                    viewer.refresh();

                    taskLabel.setText(EMPTY_STR);
                    progressIndicator.done();
                    cancelOptBt.setVisible(false);
                    startButton.setEnabled(true);
                    getButton(IDialogConstants.OK_ID).setEnabled(true);
                  }
                });
              }
            }).start();
          }
        }
      }
      
    });
    return composite;
  }
  
  // --- Private classes
  
  private static final class ResManagerContentProvider implements IStructuredContentProvider {

    // --- Interface methods implementation
    
    @SuppressWarnings("unchecked")
    public Object[] getElements(final Object inputElement) {
      return ((Collection<IResourceManager>) inputElement).toArray();
    }

    public void dispose() {
    }

    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    }
    
  }
  
  private static final class ResManagerLabelProvider implements ILabelProvider {
    
    ResManagerLabelProvider() {
      this.fStartedImage = LaunchImages.createUnmanaged(LaunchImages.RM_STARTED).createImage();
      this.fStartingImage = LaunchImages.createUnmanaged(LaunchImages.RM_STARTING).createImage();
      this.fStoppedImage = LaunchImages.createUnmanaged(LaunchImages.RM_STOPPED).createImage();
      this.fErrorImage = LaunchImages.createUnmanaged(LaunchImages.RM_ERROR).createImage();
    }

    // --- ILabelProvider's interface methods implementation
    
    public Image getImage(final Object element) {
      final IResourceManager resourceManager = (IResourceManager) element;
      switch (resourceManager.getState()) {
        case STOPPED: return this.fStoppedImage;
        case STARTING: return this.fStartingImage;
        case STARTED: return this.fStartedImage;
        case ERROR: return this.fErrorImage;
        default: return null;
      }
    }

    public String getText(final Object element) {
      final IResourceManager resourceManager = (IResourceManager) element;
      return "   " + resourceManager.getName(); //$NON-NLS-1$
    }

    // --- IBaseLabelProvider's interface methods implementation

    public void addListener(final ILabelProviderListener listener) {
    }

    public void dispose() {
    }

    public boolean isLabelProperty(final Object element, final String property) {
      return false;
    }

    public void removeListener(final ILabelProviderListener listener) {
    }
    
    // --- Fields
    
    private final Image fStartedImage;
    
    private final Image fStartingImage;
    
    private final Image fStoppedImage;
    
    private final Image fErrorImage;
    
  }
  
  // --- Fields
  
  private final Collection<IResourceManager> fResManagerList;
  
  
  private static final String EMPTY_STR = ""; //$NON-NLS-1$
  
}
