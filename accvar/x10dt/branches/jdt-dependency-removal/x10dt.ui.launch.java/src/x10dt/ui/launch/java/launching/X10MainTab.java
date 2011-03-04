package x10dt.ui.launch.java.launching;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.imp.model.ISourceEntity;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.utils.Pair;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaMainTab;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.launcher.LauncherMessages;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import polyglot.types.ClassType;
import x10dt.core.X10DTCorePlugin;
import x10dt.ui.Messages;
import x10dt.ui.X10DTUIPlugin;
import x10dt.ui.launch.java.Activator;
import x10dt.ui.utils.LaunchUtils;

@SuppressWarnings("restriction")
final class X10MainTab extends JavaMainTab {

  // --- Overridden methods
  
  public void setDefaults(final ILaunchConfigurationWorkingCopy config) {
    final IJavaElement javaElement = getContext();
    if (javaElement != null) {
      initializeJavaProject(javaElement, config);
    } else {
      config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, EMPTY_STRING);
    }
    if (javaElement != null) {
      try {
        final Pair<ClassType, ISourceEntity> mainType = LaunchUtils.findMainType(new ISourceEntity[] { ModelFactory.open(javaElement.getResource()) }, 
                                                                                X10DTCorePlugin.X10_PRJ_JAVA_NATURE_ID,
                                                                                getShell());
        if (mainType != null) {
          config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, mainType.first.fullName().toString());
        }  
      } catch (Exception except) {
        // Simply forgets the initialization of main type.
      }
    }
  }

  protected IJavaElement getContext() {
    final IWorkbenchPage page = JDIDebugUIPlugin.getActivePage();
    if (page != null) {
      final ISelection selection = page.getSelection();
      if (selection instanceof IStructuredSelection) {
        final IStructuredSelection ss = (IStructuredSelection) selection;
        final Object selectedObject;
        if (ss.size() > 1) {
          // Unless they have a common project, it doesn't really makes sense here.
          IProject commonProject = null;
          for (final Iterator<?> it = ss.iterator(); it.hasNext();) {
            final Object element = it.next();
            final IProject curPrj;
            if (element instanceof IJavaElement) {
              curPrj = ((IJavaElement) element).getJavaProject().getProject();
            } else if (element instanceof IResource) {
              curPrj = ((IResource) element).getProject();
            } else {
              curPrj = null;
            }
            if (commonProject == null) {
              commonProject = curPrj;
            } else if ((curPrj != null) && ! commonProject.equals(curPrj)) {
              commonProject = null;
              break;
            }
          }
          selectedObject = commonProject;
        } else {
          selectedObject = ss.getFirstElement();
        }
        if (selectedObject != null) {
          return processSelection(selectedObject);
        }
      }
      final IEditorPart part = page.getActiveEditor();
      if (part != null) {
        final IEditorInput input = part.getEditorInput();
        return (IJavaElement) input.getAdapter(IJavaElement.class);
      }
    }
    return null;
  }

  protected void handleProjectButtonSelected() {
    final IProject project = chooseX10Project();
    if (project != null) {
      super.fProjText.setText(project.getName());
    }
  }

  protected void handleSearchButtonSelected() {
    final IJavaProject project = getJavaProject();
    final ISourceEntity[] scope;
    if ((project == null) || !project.exists()) {
      scope = null;
    } else {
      final boolean hasValidNature = hasX10ProjectJavaBackEndNature(project.getProject());
      scope = hasValidNature ? new ISourceEntity[] { ModelFactory.getProject(project.getProject()) } : null;
    }
    try {
      final Pair<ClassType, ISourceEntity> mainType = LaunchUtils.findMainType(scope, X10DTCorePlugin.X10_PRJ_JAVA_NATURE_ID,
                                                                              getShell());
      if (mainType != null) {
        super.fMainText.setText(mainType.first.fullName().toString());
        if ((project == null) || !project.exists()) {
          super.fProjText.setText(mainType.second.getProject().getName());
        }
      }
    } catch (InvocationTargetException except) {
      if (except.getTargetException() instanceof CoreException) {
        ErrorDialog.openError(getShell(), Messages.AXLS_MainTypeSearchError, Messages.AXLS_MainTypeSearchErrorMsg,
                              ((CoreException) except.getTargetException()).getStatus());
        X10DTUIPlugin.getInstance().getLog().log(((CoreException) except.getTargetException()).getStatus());
      } else {
        final IStatus status = new Status(IStatus.ERROR, X10DTUIPlugin.PLUGIN_ID, Messages.AXLS_MainTypeSearchInternalError,
                                          except.getTargetException());
        ErrorDialog.openError(getShell(), Messages.AXLS_MainTypeSearchError, Messages.AXLS_MainTypeSearchErrorMsg, status);
        X10DTUIPlugin.getInstance().getLog().log(status);
      }
    } catch (InterruptedException except) {
      // Do nothing.
    }
  }

  // --- Private code

  private IProject chooseX10Project() {
    final ILabelProvider labelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
    final ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
    dialog.setTitle(LauncherMessages.AbstractJavaMainTab_4);
    dialog.setMessage(LauncherMessages.AbstractJavaMainTab_3);
    try {
      dialog.setElements(getProjectList());
    } catch (CoreException except) {
      setErrorMessage(except.getMessage());
      Activator.getDefault().getLog().log(except.getStatus());
    }
    if (dialog.open() == Window.OK) {
      return (IProject) dialog.getFirstResult();
    }
    return null;
  }

  private IProject[] getProjectList() throws CoreException {
    final IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
    final Collection<IProject> projects = new ArrayList<IProject>(allProjects.length);
    for (final IProject project : allProjects) {
      if (project.isOpen() && project.hasNature(X10DTCorePlugin.X10_PRJ_JAVA_NATURE_ID)) {
        projects.add(project);
      }
    }
    return projects.toArray(new IProject[projects.size()]);
  }
  
  private boolean hasX10ProjectJavaBackEndNature(final IProject project) {
    try {
      return project.hasNature(X10DTCorePlugin.X10_PRJ_JAVA_NATURE_ID);
    } catch (CoreException except) {
      return false;
    }
  }
  
  private IJavaElement processSelection(final Object selectedObject) {
    final IProject project;
    if (selectedObject instanceof IJavaElement) {
      project = ((IJavaElement) selectedObject).getJavaProject().getProject();
    } else if (selectedObject instanceof IResource) {
      project = ((IResource) selectedObject).getProject();
    } else {
      project = null;
    }
    if ((project == null) || ! hasX10ProjectJavaBackEndNature(project)) {
      return null;
    }
    // We have one object selected, which is either a IResource or a IJavaElement with Java back-end nature.
    if (selectedObject instanceof IJavaElement) {
      return (IJavaElement) selectedObject;
    } else if (selectedObject instanceof IProject) {
      return JavaCore.create((IProject) selectedObject);
    } else if (selectedObject instanceof IFolder) {
      final IJavaElement je = JavaCore.create((IFolder) selectedObject);
      if (je == null) {
        return JavaCore.create(project);
      } else {
        return je;
      }
    } else if (selectedObject instanceof IFile) {
      final IFile file = (IFile) selectedObject;
      // Pretend as if the corresponding Java source file was selected
      if (file.getFileExtension().equals("x10")) { //$NON-NLS-1$
        final IFile javaSrcFile = project.getFile(file.getProjectRelativePath().removeFileExtension().addFileExtension("java"));//$NON-NLS-1$
        IJavaElement javaElem = JavaCore.create(javaSrcFile);
        if (javaElem == null) {
          javaElem = JavaCore.create(project);
        }
        if (javaElem != null) {
          return javaElem;
        }
      }
    }
    return null;
  }

}
