package apgas.ui.quickfix;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.text.java.ClasspathFixProcessor;

@SuppressWarnings("javadoc")
public class APGASClasspathFixProcessor extends ClasspathFixProcessor {
  @Override
  public ClasspathFixProposal[] getFixImportProposals(IJavaProject project,
      String missingType) throws CoreException {
    final ArrayList<APGASClasspathFixProposal> proposals = new ArrayList<APGASClasspathFixProposal>();
    proposals.add(new APGASClasspathFixProposal(project, 15));
    return proposals.toArray(new ClasspathFixProposal[proposals.size()]);
  }

}
