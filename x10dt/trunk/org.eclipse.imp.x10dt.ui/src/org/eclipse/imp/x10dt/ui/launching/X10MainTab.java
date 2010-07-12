package org.eclipse.imp.x10dt.ui.launching;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.imp.utils.Pair;
import org.eclipse.imp.x10dt.core.X10DTCorePlugin;
import org.eclipse.imp.x10dt.ui.Messages;
import org.eclipse.imp.x10dt.ui.utils.LaunchUtils;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaMainTab;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;

import polyglot.types.ClassType;

public class X10MainTab extends JavaMainTab {

  // --- Overridden methods
  
  protected IJavaElement getContext() {
    IWorkbenchPage page = JDIDebugUIPlugin.getActivePage();
    if (page != null) {
      ISelection selection = page.getSelection();
      if (selection instanceof IStructuredSelection) {
        IStructuredSelection ss = (IStructuredSelection) selection;
        if (!ss.isEmpty()) {
          Object obj = ss.getFirstElement();
          if (obj instanceof IJavaElement) {
            return (IJavaElement) obj;
          }
          if (obj instanceof IFolder) {
            IJavaElement je = JavaCore.create((IFolder) obj);
            if (je == null) {
              IProject pro = ((IResource) obj).getProject();
              je = JavaCore.create(pro);
            }
            if (je != null) {
              return je;
            }
          } else if (obj instanceof IFile) {
            IFile file = (IFile) obj;
            // Pretend as if the corresponding Java source file was selected
            if (file.getFileExtension().equals("x10")) {
              IProject project = file.getProject();
              IFile javaSrcFile = project.getFile(file.getProjectRelativePath().removeFileExtension().addFileExtension("java"));
              IJavaElement javaElem = JavaCore.create(javaSrcFile);
              if (javaElem == null) {
                IProject pro = ((IResource) obj).getProject();
                javaElem = JavaCore.create(pro);
              }
              if (javaElem != null) {
                return javaElem;
              }
            }
          }
        }
      }
      IEditorPart part = page.getActiveEditor();
      if (part != null) {
        IEditorInput input = part.getEditorInput();
        return (IJavaElement) input.getAdapter(IJavaElement.class);
      }
    }
    return null;
  }
  
  protected void handleSearchButtonSelected() {
    final IJavaProject project = getJavaProject();
    final IJavaElement[] scope;
    if ((project == null) || ! project.exists()) {
      scope = null;
    } else {
      boolean hasValidNature = false;
      try {
        hasValidNature = project.getProject().hasNature(X10DTCorePlugin.PROJECT_NATURE_ID);
      } catch (CoreException except) {
        // Do nothing.
      }
      scope = hasValidNature ? new IJavaElement[] { project } : null;
    }
    try {
      final Pair<ClassType, IJavaElement> mainType = LaunchUtils.findMainType(scope, X10DTCorePlugin.PROJECT_NATURE_ID,
                                                                              getShell());
      if (mainType != null) {
        super.fMainText.setText(mainType.first.fullName().toString());
        if ((project == null) || ! project.exists()) {
          super.fProjText.setText(mainType.second.getJavaProject().getElementName());
        }
      }
    } catch (InvocationTargetException except) {
      ErrorDialog.openError(getShell(), Messages.AXLS_MainTypeSearchError, Messages.AXLS_MainTypeSearchErrorMsg, 
                            ((CoreException) except.getTargetException()).getStatus());
    } catch (InterruptedException except) {
      // Do nothing.
    }
  }
  
}
