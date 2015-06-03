package apgas.ui;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * Initializes the APGAS Library container.
 */
public class Initializer extends ClasspathContainerInitializer {

  public static final String APGAS_CONTAINER_ID = "apgas.APGAS_CONTAINER";

  /**
   * Public constructor.
   */
  public Initializer() {
  }

  @Override
  public void initialize(IPath containerPath, IJavaProject project)
      throws CoreException {
    try {
      final File apgasFile = FileLocator.getBundleFile(Platform
          .getBundle("apgas"));
      final IClasspathEntry apgas;
      if (apgasFile.isDirectory()) { // --- This case has been added so that we
                                     // can launch a runtime eclipse.
        apgas = JavaCore.newLibraryEntry(
            (new Path(apgasFile.getPath()).append("bin")), null, null);
      } else {
        apgas = JavaCore.newLibraryEntry(new Path(apgasFile.getPath()), null,
            null);
      }

      final IClasspathEntry hazelcast = JavaCore.newLibraryEntry(new Path(
          FileLocator.getBundleFile(Platform.getBundle("com.hazelcast"))
              .getPath()), null, null);
      JavaCore.setClasspathContainer(containerPath,
          new IJavaProject[] { project },
          new IClasspathContainer[] { new Container(containerPath,
              new IClasspathEntry[] { apgas, hazelcast }) }, null);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * The APGAS Library container.
   */
  static class Container implements IClasspathContainer {
    /**
     * The container path.
     */
    IPath containerPath;

    /**
     * The classpath.
     */
    IClasspathEntry[] classpath;

    /**
     * Contructs the APGAS Library container.
     *
     * @param containerPath
     *          the container path
     * @param classpath
     *          teh classpath
     */
    public Container(IPath containerPath, IClasspathEntry[] classpath) {
      this.containerPath = containerPath;
      this.classpath = classpath;
    }

    @Override
    public IClasspathEntry[] getClasspathEntries() {
      return classpath;
    }

    @Override
    public String getDescription() {
      return "APGAS Library";
    }

    @Override
    public int getKind() {
      return IClasspathContainer.K_APPLICATION;
    }

    @Override
    public IPath getPath() {
      return containerPath;
    }
  }
}
