package apgas.ui.quickfix;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.ui.CodeStyleConfiguration;
import org.eclipse.jdt.ui.text.java.ClasspathFixProcessor;
import org.eclipse.jdt.ui.text.java.ClasspathFixProcessor.ClasspathFixProposal;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickFixProcessor;

import apgas.ui.Initializer;

public class APGASQuickfixProcessor implements IQuickFixProcessor {

  @Override
  public boolean hasCorrections(ICompilationUnit unit, int problemId) {
    // return problemId == IProblem.UndefinedMethod
    // | problemId == IProblem.UndefinedType;
    return problemId == IProblem.UnresolvedVariable || problemId == 16777218
        || problemId == 67108964;
  }

  @Override
  public IJavaCompletionProposal[] getCorrections(IInvocationContext context,
      IProblemLocation[] locations) throws CoreException {
    for (final IProblemLocation problem : locations) {
      if (((problem.getProblemId() == IProblem.UnresolvedVariable || problem
          .getProblemId() == 16777218) && isAPGAS(problem.getProblemArguments()[0]))
          || (problem.getProblemId() == 67108964 && isAPGAS(problem
              .getProblemArguments()[1]))) {
        if (!apgasInBuildPath(context.getCompilationUnit().getJavaProject())) {
          return getAddAPGASToBuildPathProposals(context);
        }
      }
    }
    return null;
  }

  private boolean isAPGAS(String name) {
    switch (name) {
    // Constructs
    case "finish":
      break;
    case "async":
      break;
    case "asyncAt":
      break;
    case "uncountedAsyncAt":
      break;
    case "at":
      break;
    case "here":
      break;
    case "place":
      break;
    case "places":
      break;
    // APGAS Classes
    case "Configuration":
      break;
    case "DeadPlaceException":
      break;
    case "MultipleException":
      break;
    case "Place":
      break;
    default:
      return false;
    }
    return true;
  }

  private boolean apgasInBuildPath(IJavaProject javaProject) {
    if (javaProject == null) {
      return false;
    }

    IClasspathEntry[] entries = null;
    try {
      entries = javaProject.getRawClasspath();
    } catch (final JavaModelException e) {
      return false;
    }
    for (final IClasspathEntry entry : entries) {
      final int kind = entry.getEntryKind();
      final IPath path = entry.getPath();
      if (kind == IClasspathEntry.CPE_CONTAINER
          && path.toString().equals(Initializer.APGAS_CONTAINER_ID)) {
        return true;
      }

    }
    return false;
  }

  private IJavaCompletionProposal[] getAddAPGASToBuildPathProposals(
      IInvocationContext context) {
    final ICompilationUnit unit = context.getCompilationUnit();
    final IJavaProject project = unit.getJavaProject();
    final String name = "static apgas.Constructs.*";
    final ClasspathFixProposal[] fixProposals = ClasspathFixProcessor
        .getContributedFixImportProposals(project, name, null);

    final List<ImportRewrite> importRewrites = new ArrayList<ImportRewrite>();
    importRewrites.add(getImportRewrite(context.getASTRoot(), name));
    importRewrites.add(getImportRewrite(context.getASTRoot(), "apgas.*"));

    final ArrayList<IJavaCompletionProposal> proposals = new ArrayList<IJavaCompletionProposal>();
    for (final ClasspathFixProposal fixProposal : fixProposals) {
      proposals.add(new APGASClasspathFixCorrelationProposal(project,
          fixProposal, importRewrites));
    }

    final IJavaCompletionProposal[] propArr = new IJavaCompletionProposal[proposals
        .size()];
    for (int i = 0; i < proposals.size(); i++) {
      propArr[i] = proposals.get(i);
    }
    return propArr;
  }

  private ImportRewrite getImportRewrite(CompilationUnit astRoot,
      String typeToImport) {
    if (typeToImport != null) {
      final ImportRewrite importRewrite = CodeStyleConfiguration
          .createImportRewrite(astRoot, true);
      importRewrite.addImport(typeToImport);
      return importRewrite;
    }
    return null;
  }

}
