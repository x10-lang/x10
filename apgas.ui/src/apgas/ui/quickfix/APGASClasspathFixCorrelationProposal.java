package apgas.ui.quickfix;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.ui.text.java.ClasspathFixProcessor.ClasspathFixProposal;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.PerformChangeOperation;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.PlatformUI;

@SuppressWarnings("javadoc")
public class APGASClasspathFixCorrelationProposal implements
    IJavaCompletionProposal {

  private final IJavaProject fJavaProject;
  private final ClasspathFixProposal fClasspathFixProposal;
  private final List<ImportRewrite> fImportRewrites;

  public APGASClasspathFixCorrelationProposal(IJavaProject project,
      ClasspathFixProposal classpathFixProposal,
      List<ImportRewrite> importRewrites) {
    fJavaProject = project;
    fClasspathFixProposal = classpathFixProposal;
    fImportRewrites = importRewrites;
  }

  @Override
  public void apply(IDocument document) {
    try {
      PlatformUI.getWorkbench().getActiveWorkbenchWindow()
          .run(false, true, new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor)
                throws InvocationTargetException, InterruptedException {
              try {
                final Change change = createChange();
                change.initializeValidationData(new NullProgressMonitor());
                final PerformChangeOperation op = new PerformChangeOperation(
                    change);
                op.setUndoManager(RefactoringCore.getUndoManager(),
                    getDisplayString());
                op.setSchedulingRule(fJavaProject.getProject().getWorkspace()
                    .getRoot());
                op.run(monitor);
              } catch (final CoreException e) {
                throw new InvocationTargetException(e);
              } catch (final OperationCanceledException e) {
                throw new InterruptedException();
              }
            }
          });
    } catch (final InvocationTargetException e) {
      // fail silently
    } catch (final InterruptedException e) {
      // fail silently
    }
  }

  protected Change createChange() throws CoreException {
    final Change change = fClasspathFixProposal.createChange(null);
    if (fImportRewrites != null) {
      final CompositeChange composite = new CompositeChange(getDisplayString());
      composite.add(change);
      for (final ImportRewrite ir : fImportRewrites) {
        final TextFileChange cuChange = new TextFileChange(
            "Add import", (IFile) ir.getCompilationUnit().getResource()); //$NON-NLS-1$
        cuChange.setEdit(ir.rewriteImports(null));
        composite.add(cuChange);
      }
      return composite;
    }
    return change;
  }

  @Override
  public Point getSelection(IDocument document) {
    return null;
  }

  @Override
  public String getAdditionalProposalInfo() {
    return fClasspathFixProposal.getAdditionalProposalInfo();
  }

  @Override
  public String getDisplayString() {
    return fClasspathFixProposal.getDisplayString();
  }

  @Override
  public Image getImage() {
    return fClasspathFixProposal.getImage();
  }

  @Override
  public IContextInformation getContextInformation() {
    return null;
  }

  @Override
  public int getRelevance() {
    return fClasspathFixProposal.getRelevance();
  }

}
