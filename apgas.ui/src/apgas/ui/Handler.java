package apgas.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Augments the classpath with the APGAS Library container.
 */
public class Handler extends AbstractHandler {
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    final IPath containerPath = new Path(Initializer.APGAS_CONTAINER_ID);
    try {
      final IJavaProject javaProject = JavaCore
          .create((IProject) ((IAdaptable) ((IStructuredSelection) HandlerUtil
              .getCurrentSelection(event)).getFirstElement())
              .getAdapter(IProject.class));
      final IClasspathEntry[] entries = javaProject.getRawClasspath();
      for (int i = 0; i < entries.length; i++) {
        if (entries[i].getEntryKind() == IClasspathEntry.CPE_CONTAINER
            && entries[i].getPath().equals(containerPath)) {
          return null;
        }
      }
      final IClasspathEntry[] cp = new IClasspathEntry[entries.length + 1];
      System.arraycopy(entries, 0, cp, 0, entries.length);
      cp[entries.length] = JavaCore.newContainerEntry(containerPath);
      javaProject.setRawClasspath(cp, new NullProgressMonitor());
    } catch (final Exception e) {
      throw new ExecutionException(e.toString(), e);
    }
    return null;
  }
}
