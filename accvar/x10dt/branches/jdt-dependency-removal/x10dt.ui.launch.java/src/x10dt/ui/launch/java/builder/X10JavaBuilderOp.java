package x10dt.ui.launch.java.builder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.imp.model.ModelFactory;

import x10dt.ui.launch.core.Constants;
import x10dt.ui.launch.core.builder.target_op.IX10BuilderFileOp;
import x10dt.ui.launch.core.utils.ICountableIterable;

public class X10JavaBuilderOp implements IX10BuilderFileOp {

  private IProject fProject;

  private X10JavaBuilder fBuilder;

  public X10JavaBuilderOp(IProject project, X10JavaBuilder builder) {
    this.fProject = project;
    this.fBuilder = builder;
  }

  // --- Interface methods implementation

  public void archive(final IProgressMonitor monitor) throws CoreException {
    // NoOp for Java Backend
  }

  public void cleanFiles(final ICountableIterable<IFile> files, final SubMonitor monitor) throws CoreException {
    for (final IFile file : files) {
      final File javaFile = this.fBuilder.getMainGeneratedFile(ModelFactory.getProject(this.fProject), file);
      if (javaFile != null) {
        if (javaFile.exists()) {
          javaFile.delete();
        }
        final String javaFilePath = javaFile.getAbsolutePath();
        final File classFile = new File(javaFilePath.substring(0, javaFilePath.length() - 5).concat(Constants.CLASS_EXT));
        if (classFile.exists()) {
          classFile.delete();
        }
        // We need to take care of possible anonymous classes now.
        final String typeName = javaFile.getName().substring(0, javaFile.getName().length() - 5);
        for (final File f : javaFile.getParentFile().listFiles(new AnonymousClassFilter(typeName))) {
          f.delete();
        }
      }
    }
  }

  public boolean compile(final IProgressMonitor monitor) throws CoreException {
    return true; // NoOp for Java Backend -- This is done using the post-compilation goal. See JavaBuilderExtensionInfo
  }

  public void copyToOutputDir(final Collection<IFile> files, final SubMonitor monitor) throws CoreException {
    // NoOp for Java Backend
  }

  public boolean hasAllPrerequisites() {
    return true;
  }

  public void transfer(final Collection<File> files, final IProgressMonitor monitor) throws CoreException {
    // NoOp for Java Backend
  }

  // --- Private classes
  
  private static final class AnonymousClassFilter implements FilenameFilter {
    
    AnonymousClassFilter(final String typeName) {
      this.fTypeNamePrefix = typeName + '$';
    }

    // --- Interface methods implementation
    
    public boolean accept(final File dir, final String name) {
      return name.startsWith(this.fTypeNamePrefix) && name.endsWith(Constants.CLASS_EXT);
    }
    
    // --- Private code
    
    private final String fTypeNamePrefix;
    
  }
  
}
