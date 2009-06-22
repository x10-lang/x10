/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.launch.launching;

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;
import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_ARGUMENTS;
import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_EXECUTABLE_PATH;
import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_PROJECT_NAME;
import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_WORK_DIRECTORY;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.model.ISourceEntity;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.x10dt.ui.cpp.launch.Constants;
import org.eclipse.imp.x10dt.ui.cpp.launch.LaunchCore;
import org.eclipse.imp.x10dt.ui.cpp.launch.Messages;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.elementcontrols.IResourceManagerControl;
import org.eclipse.ptp.launch.internal.ui.LaunchImages;
import org.eclipse.ptp.launch.ui.LaunchConfigurationTab;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.remote.ui.IRemoteUIFileManager;
import org.eclipse.ptp.remote.ui.IRemoteUIServices;
import org.eclipse.ptp.remote.ui.PTPRemoteUIPlugin;
import org.eclipse.ptp.rm.remote.core.AbstractRemoteResourceManagerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

@SuppressWarnings("restriction")
final class CppApplicationTab extends LaunchConfigurationTab implements ILaunchConfigurationTab {

  // --- Interface methods implementation
  
  public void createControl(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NULL);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(1, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
    
    createProjectEditor(composite);
    createVerticalSpacer(composite, 2);
    createMainTypeEditor(composite);
    createVerticalSpacer(composite, 2);
    createProgramArgs(composite);
    
    setControl(composite);
  }

  public String getName() {
    return Messages.CAT_TabName;
  }

  public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
    final String projectName = this.fProjectText.getText().trim();    
    configuration.setAttribute(ATTR_PROJECT_NAME, projectName);
    
    final String mainClassName = this.fMainClassText.getText().trim();
    if (mainClassName.length() > 0) {
      configuration.setAttribute(ATTR_MAIN_TYPE_NAME, mainClassName);
      if (projectName.length() > 0) {
        final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        if (project.exists()) {
          configuration.setAttribute(ATTR_EXECUTABLE_PATH, mainClassName);
          configuration.setAttribute(ATTR_WORK_DIRECTORY, mainClassName.substring(0, mainClassName.lastIndexOf('/')));
        }
      }
    }
    
    final String content = this.fPgrmArgsText.getText().trim();    
    configuration.setAttribute(ATTR_ARGUMENTS, (content.length() > 0) ? content : null);
  }

  public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
    configuration.setAttribute(ATTR_PROJECT_NAME, (String) null);
    configuration.setAttribute(ATTR_MAIN_TYPE_NAME, (String) null);
    configuration.setAttribute(ATTR_EXECUTABLE_PATH, (String) null);
    configuration.setAttribute(ATTR_WORK_DIRECTORY, (String) null);
    configuration.setAttribute(ATTR_ARGUMENTS, (String) null);
  }
  
  // --- Overridden methods
  
  public Image getImage() {
    return LaunchImages.getImage(LaunchImages.IMG_MAIN_TAB);
  }
  
  public void initializeFrom(ILaunchConfiguration configuration) {
    super.initializeFrom(configuration);
    
    try {
      this.fProjectText.setText(configuration.getAttribute(ATTR_PROJECT_NAME, EMPTY_STRING));
      this.fMainClassText.setText(configuration.getAttribute(ATTR_MAIN_TYPE_NAME, EMPTY_STRING));
      this.fPgrmArgsText.setText(configuration.getAttribute(ATTR_ARGUMENTS, EMPTY_STRING));
    } catch (CoreException except) {
      setErrorMessage(Messages.CAT_ReadConfigError);
      LaunchCore.log(except.getStatus());
    }
  }
  
  public boolean isValid(final ILaunchConfiguration configuration) {
    setErrorMessage(null);
    setMessage(null);
    
    final String projectName = this.fProjectText.getText().trim();
    if (projectName.length() == 0) {
      setErrorMessage(Messages.CAT_RequiredPrjName);
      return false;
    } else {
      final IWorkspace workspace = ResourcesPlugin.getWorkspace();
      final IStatus status = workspace.validateName(projectName, IResource.PROJECT);
      if (status.isOK()) {
        final IProject project = workspace.getRoot().getProject(projectName);
        if (! project.exists()) {
          setErrorMessage(NLS.bind(Messages.CAT_NoExistingProject, projectName));
          return false;
        }
        if (! project.isOpen()) {
          setErrorMessage(NLS.bind(Messages.CAT_ClosedProject, projectName));
          return false;
        }
        // Project exists and is not closed. Checks now if we have a main class.
        final String className = this.fMainClassText.getText().trim();
        if (className == null) {
          setErrorMessage(Messages.CAT_RequiredMainClassName);
          return false;
        } else if (! hasMain(project, className)) {
          setErrorMessage(Messages.CAT_NoMainMethod);
          return false;
        }
      } else {
        setErrorMessage(NLS.bind(Messages.CAT_IllegalPrjName, projectName));
        return false;
      }
    }
    return true;
  }
  
  // --- Private code
  
  private void createMainTypeEditor(final Composite parent) {
    final Group group = new Group(parent, SWT.NONE);
    group.setFont(parent.getFont());
    group.setLayout(new GridLayout(2, false));
    group.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    group.setText(Messages.CAT_MainClassGroupName);
    
    this.fMainClassText = new Text(group, SWT.SINGLE | SWT.BORDER);
    this.fMainClassText.setFont(group.getFont());
    this.fMainClassText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    this.fMainClassText.addModifyListener(new TextModificationListener());
    
    this.fSearchMainClassBt = createPushButton(group, Messages.CAT_SearchButton, null /* image */);
    this.fSearchMainClassBt.addSelectionListener(new SearchMainClassBtSelectionListener());
  }
  
  private void createProgramArgs(final Composite parent) {
    final Group group = new Group(parent, SWT.NONE);
    group.setFont(parent.getFont());
    group.setLayout(new GridLayout(1, false));
    group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    group.setText(Messages.AT_ProgArgsGroupName);
    
    this.fPgrmArgsText = new Text(group, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
    this.fPgrmArgsText.addTraverseListener(new TraverseListener() {
      public void keyTraversed(final TraverseEvent event) {
        switch (event.detail) {
        case SWT.TRAVERSE_ESCAPE:
        case SWT.TRAVERSE_PAGE_NEXT:
        case SWT.TRAVERSE_PAGE_PREVIOUS:
          event.doit = true;
          break;
        case SWT.TRAVERSE_RETURN:
        case SWT.TRAVERSE_TAB_NEXT:
        case SWT.TRAVERSE_TAB_PREVIOUS:
          if ((CppApplicationTab.this.fPgrmArgsText.getStyle() & SWT.SINGLE) != 0) {
            event.doit = true;
          } else if (! CppApplicationTab.this.fPgrmArgsText.isEnabled() || (event.stateMask & SWT.MODIFIER_MASK) != 0) {
            event.doit = true;
          }
          break;
        }
      }
    });
    final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
    gd.heightHint = 40;
    gd.widthHint = 100;
    this.fPgrmArgsText.setLayoutData(gd);
    this.fPgrmArgsText.setFont(parent.getFont());
    this.fPgrmArgsText.addModifyListener(new ModifyListener() {
      public void modifyText(final ModifyEvent event) {
        updateLaunchConfigurationDialog();
      }
    });

    final Button pgrmArgVarBt = createPushButton(group, Messages.AT_VariablesBtName, null /* image */);
    pgrmArgVarBt.setLayoutData(new GridData(SWT.END, SWT.NONE, false, false));
    pgrmArgVarBt.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(final SelectionEvent event) {
        final StringVariableSelectionDialog dialog = new StringVariableSelectionDialog(getShell());
        dialog.open();
        final String variable = dialog.getVariableExpression();
        if (variable != null) {
          CppApplicationTab.this.fPgrmArgsText.insert(variable);
        }
      }
    });
  }
  
  private void createProjectEditor(final Composite parent) {
    final Group group = new Group(parent, SWT.NONE);
    group.setFont(parent.getFont());
    group.setLayout(new GridLayout(2, false));
    group.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    group.setText(Messages.CAT_ProjectGroupName);
    
    this.fProjectText = new Text(group, SWT.SINGLE | SWT.BORDER);
    this.fProjectText.setFont(group.getFont());
    this.fProjectText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    this.fProjectText.addModifyListener(new TextModificationListener());
    
    this.fProjectBt = createPushButton(group, Messages.CAT_BrowseButton, null /* image */);
    this.fProjectBt.addSelectionListener(new ProjectBtSelectionListener());
  }
  
  private boolean hasMain(final IProject project, final String mainClassName) {
    final IResource resource = project.findMember(mainClassName);
    if (resource != null) {
      try {
        final ISourceEntity sourceEntity = ModelFactory.open(resource);
        if (sourceEntity instanceof ICompilationUnit) {
//      FIXME: It doesn't work so far. To fix.
//          final ICompilationUnit cpUnit = (ICompilationUnit) sourceEntity;
//          final Node astRoot = (Node) cpUnit.getAST(new SilentMessageHandler(), new NullProgressMonitor());
//          final MainMethodNodeVisitor visitor = new MainMethodNodeVisitor();
//          astRoot.visit(visitor);
//          return visitor.hasMainMethod();
        }
      } catch (ModelException except) {
        // Let's give up... and consider the optimistic result.
      }
    }
    // Be optimistic by default.
    return true;
  }
  
  // --- Private classes
  
//  private static final class SilentMessageHandler implements IMessageHandler {
//
//    public void clearMessages() {
//    }
//
//    public void endMessageGroup() {
//    }
//
//    public void handleSimpleMessage(final String msg, final int startOffset, final int endOffset, final int startCol, 
//                                    final int endCol, final int startLine, final int endLine) {
//    }
//
//    public void startMessageGroup(final String groupName) {
//    }
//    
//  }
//  
//  private static final class MainMethodNodeVisitor extends NodeVisitor {
//    
//    // --- Overridden methods
//    
//    public NodeVisitor enter(final Node node) {
//      if ((! this.fHasMain) && (node instanceof MethodDecl)) {
//        final MethodDecl methodDecl = (MethodDecl) node;
//        final X10TypeSystem ts = (X10TypeSystem) methodDecl.returnType().type().typeSystem();
//        this.fHasMain = methodDecl.name().toString().equals("main") && methodDecl.flags().flags().isPublic() && //$NON-NLS-1$
//                        methodDecl.flags().flags().isStatic() && methodDecl.returnType().type().isVoid() &&
//                        (methodDecl.formals().size() == 1) && 
//                        (methodDecl.formals().get(0)).type().type().typeEquals(ts.Rail(ts.String()));
//      }
//      return this;
//    }
//    
//    // --- Internal Services
//    
//    boolean hasMainMethod() {
//      return this.fHasMain;
//    }
//    
//    // --- Fields
//    
//    private boolean fHasMain;
//    
//  }
  
  private final class ProjectBtSelectionListener implements SelectionListener {
    
    // --- Interface methods implementation
    
    public void widgetDefaultSelected(final SelectionEvent event) {
    }

    public void widgetSelected(final SelectionEvent event) {
      final ILabelProvider labelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
      final ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
      dialog.setTitle(Messages.CAT_PrjSelectionDialogTitle);
      dialog.setMessage(Messages.CAT_PrjSelectionDialogMsg);
      try {
        dialog.setElements(getProjectList());
      } catch (CoreException except) {
        setErrorMessage(except.getMessage());
        LaunchCore.log(except.getStatus());
      }
      if (dialog.open() == Window.OK) {
        final IProject project = (IProject) dialog.getFirstResult();
        CppApplicationTab.this.fProjectText.setText(project.getName());
      }
    }
    
    // --- Private code
    
    private IProject[] getProjectList() throws CoreException {
      final IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
      final Collection<IProject> projects = new ArrayList<IProject>(allProjects.length);
      for (final IProject project : allProjects) {
        if (project.hasNature(LaunchCore.X10_CPP_PRJ_NATURE_ID)) {
          projects.add(project);
        }
      }
      return projects.toArray(new IProject[projects.size()]);
    }
    
  }
  
  private final class SearchMainClassBtSelectionListener implements SelectionListener {

    // --- Interface methods implementation
    
    public void widgetDefaultSelected(final SelectionEvent event) {
    }

    public void widgetSelected(final SelectionEvent event) {
      String initialPath = "/"; //$NON-NLS-1$
      final String projectName = CppApplicationTab.this.fProjectText.getText().trim();
      if (projectName.length() > 0) {
        final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        if (project.exists()) {
          try {
            final String workingDir = project.getPersistentProperty(Constants.WORKSPACE_DIR);
            if (workingDir != null) {
              initialPath = workingDir + '/' + projectName;
            }
          } catch (CoreException except) {
            // Simply forgets.
          }
        }
      }
      
      final IResourceManagerControl rm = (IResourceManagerControl) getResourceManager(getLaunchConfiguration());
      final AbstractRemoteResourceManagerConfiguration rmc = (AbstractRemoteResourceManagerConfiguration) rm.getConfiguration();
      final IRemoteServices remServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(rmc.getRemoteServicesId());
      final IRemoteUIServices remUIServices = PTPRemoteUIPlugin.getDefault().getRemoteUIServices(remServices);
      if (remServices != null && remUIServices != null) {
        final IRemoteConnection rmConn = remServices.getConnectionManager().getConnection(rmc.getConnectionName());
        if (rmConn != null) {
          final IRemoteUIFileManager fileMgr = remUIServices.getUIFileManager();
          if (fileMgr != null) {
            fileMgr.setConnection(rmConn);
            fileMgr.showConnections(false);
            final IPath path = fileMgr.browseFile(getShell(), Messages.CAT_SelectMainDialogDescription, initialPath);
            if (path != null) {
              final String file = path.toString();
              CppApplicationTab.this.fMainClassText.setText(file.substring(0, file.length() - 3));
            }
          }
        }
      }
    }
    
  }
  
  private final class TextModificationListener implements ModifyListener {

    // --- Interface methods implementation
    
    public void modifyText(final ModifyEvent event) {
      updateLaunchConfigurationDialog();
    }
    
  }
  
  // --- Fields
  
  private Text fProjectText;
  
  private Text fMainClassText;
  
  private Text fPgrmArgsText;
  
  private Button fProjectBt;
  
  private Button fSearchMainClassBt;

}
