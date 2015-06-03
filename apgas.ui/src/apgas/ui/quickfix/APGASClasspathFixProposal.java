package apgas.ui.quickfix;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.text.java.ClasspathFixProcessor.ClasspathFixProposal;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.NullChange;
import org.eclipse.swt.graphics.Image;

import apgas.ui.Initializer;

public class APGASClasspathFixProposal extends ClasspathFixProposal {

  private final IJavaProject fProject;
  private final int fRelevance;

  public APGASClasspathFixProposal(IJavaProject project, int relevance) {
    this.fProject = project;
    this.fRelevance = relevance;
  }

  @Override
  public Change createChange(IProgressMonitor monitor) throws CoreException {
    if (monitor == null) {
      monitor = new NullProgressMonitor();
    }
    monitor.beginTask("Adding DoodleDebug library...", 1);
    try {
      IClasspathEntry entry = null;
      entry = JavaCore.newContainerEntry(new Path(
          Initializer.APGAS_CONTAINER_ID));
      final IClasspathEntry[] oldEntries = fProject.getRawClasspath();
      final ArrayList<IClasspathEntry> newEntries = new ArrayList<IClasspathEntry>(
          oldEntries.length + 1);
      boolean added = false;
      for (int i = 0; i < oldEntries.length; i++) {
        IClasspathEntry curr = oldEntries[i];
        if (curr.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
          final IPath path = curr.getPath();
          if (path.equals(entry.getPath())) {
            return new NullChange(); // already on build path
          } else if (path.matchingFirstSegments(entry.getPath()) > 0) {
            if (!added) {
              curr = entry; // replace
              added = true;
            } else {
              curr = null;
            }
          }
        } else if (curr.getEntryKind() == IClasspathEntry.CPE_VARIABLE) {

        }
        if (curr != null) {
          newEntries.add(curr);
        }
      }
      if (!added) {
        newEntries.add(entry);
      }

      final IClasspathEntry[] newCPEntries = newEntries
          .toArray(new IClasspathEntry[newEntries.size()]);
      final Change newClasspathChange = newClasspathChange(fProject,
          newCPEntries, fProject.getOutputLocation());
      if (newClasspathChange != null) {
        return newClasspathChange;
      }
    } finally {
      monitor.done();
    }
    return new NullChange();
  }

  @Override
  public String getAdditionalProposalInfo() {
    return "Add APGAS Library to build path";
  }

  @Override
  public String getDisplayString() {
    return "Add APGAS Library to build path";
  }

  @Override
  public Image getImage() {
    return null;
  }

  @Override
  public int getRelevance() {
    return fRelevance;
  }

}
