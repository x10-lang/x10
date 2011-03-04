package x10dt.search.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.pdb.facts.db.FactKey;
import org.eclipse.imp.pdb.facts.db.IFactKey;
import org.eclipse.imp.pdb.facts.db.context.ProjectContext;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.indexing.IResourceKeyFactory;
import org.eclipse.imp.pdb.indexing.IResourcePredicate;
import org.eclipse.imp.pdb.indexing.Indexer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.BundleContext;

import x10dt.core.X10DTCorePlugin;
import x10dt.core.utils.X10DTCoreConstants;
import x10dt.search.core.pdb.SearchDBTypes;
import x10dt.search.core.pdb.X10FactTypeNames;

/**
 * Main class being notified of the plugin life cycle.
 * 
 * @author egeay
 */
public class SearchCoreActivator extends Plugin implements IResourceKeyFactory, IResourcePredicate {

  /**
   * The unique plugin id for <b>x10dt.search.core</b>.
   */
  public static final String PLUGIN_ID = "x10dt.search.core"; //$NON-NLS-1$
  
  /**
   * Constant identifying the job family identifier for the X10 indexer background job.
   */
  public static final Object FAMILY_X10_INDEXER = new Object();
  
  // --- IResourceKeyFactory's interface methods implementation
  
  public IFactKey createKeyForResource(final IResource resource) {
    try {
      final Type hierarchyType = SearchDBTypes.getInstance().getType(X10FactTypeNames.X10_TypeHierarchy);
      return new FactKey(hierarchyType, new ProjectContext(ModelFactory.open(resource.getProject())));
    } catch (ModelException except) {
      log(IStatus.ERROR, NLS.bind(Messages.SCA_ProjectNonExistent, resource.getProject().getName()), except);
      return null;
    }
  }
  
  // --- IResourcePredicate's interface methods implementation

  public boolean satisfies(final IResource resource) {
    final IProject project = resource.getProject();
    try {
      if (project.isAccessible() && (project.hasNature(X10DTCorePlugin.X10_CPP_PRJ_NATURE_ID) || 
          project.hasNature(X10DTCorePlugin.X10_PRJ_JAVA_NATURE_ID))) {
        final IJavaProject javaProject = JavaCore.create(project);
        for (final IClasspathEntry cpEntry : javaProject.getRawClasspath()) {
          if (X10DTCoreConstants.X10_CONTAINER_ENTRY_ID.equals(cpEntry.getPath().toString())) {
            return true;
          }
        }
        return false;
      }
    } catch (JavaModelException except) {
      log(IStatus.ERROR, NLS.bind(Messages.SCA_ProjectStatusError, project.getName()), except);
    } catch (CoreException except) {
      log(IStatus.ERROR, NLS.bind(Messages.SCA_ProjectNatureAccessFailed, project.getName()), except);
    }
    return false;
  }
  
  // --- Public services

  /**
   * Returns the X10DT search indexer.
   * 
   * @return A non-null instance of the plugin is active.
   */
  public static Indexer getIndexer() {
    return fIndexer;
  }
  
  /**
   * Returns the context associated with the bundle for <b>x10dt.search.core</b> plugin.
   * 
   * @return A non-null value if the plugin is started, otherwise <b>null</b>.
   */
  public static Plugin getInstance() {
    return fPlugin;
  }
  
  /**
   * Logs (if the plugin is active) the operation outcome provided through the status instance.
   * 
   * @param status The status to log.
   */
  public static final void log(final IStatus status) {
    if (fPlugin != null) {
      fPlugin.getLog().log(status);
    }
  }
  
  /**
   * Logs (if the plugin is active) the outcome of an operation with the parameters provided.
   * 
   * @param severity The severity of the message. The code is one of {@link IStatus#OK}, {@link IStatus#ERROR}, 
   * {@link IStatus#INFO}, {@link IStatus#WARNING} or {@link IStatus#CANCEL}.
   * @param code The plug-in-specific status code, or {@link IStatus#OK}.
   * @param message The human-readable message, localized to the current locale.
   * @param exception The exception to log, or <b>null</b> if not applicable.
   */
  public static final void log(final int severity, final int code, final String message, final Throwable exception) {
    if (fPlugin != null) {
      fPlugin.getLog().log(new Status(severity, fPlugin.getBundle().getSymbolicName(), code, message, exception));
    }
  }
  
  /**
   * Logs (if the plugin is active) the outcome of an operation with the parameters provided.
   * 
   * <p>Similar to {@link #log(int, int, String, Throwable)} with <code>code = IStatus.OK</code>.
   * 
   * @param severity The severity of the message. The code is one of {@link IStatus#OK}, {@link IStatus#ERROR}, 
   * {@link IStatus#INFO}, {@link IStatus#WARNING} or {@link IStatus#CANCEL}.
   * @param message The human-readable message, localized to the current locale.
   * @param exception The exception to log, or <b>null</b> if not applicable.
   */
  public static final void log(final int severity, final String message, final Throwable exception) {
    if (fPlugin != null) {
      fPlugin.getLog().log(new Status(severity, fPlugin.getBundle().getSymbolicName(), message, exception));
    }
  }
  
  /**
   * Logs (if the plugin is active) the outcome of an operation with the parameters provided.
   * 
   * <p>Similar to {@link #log(int, int, String, Throwable)} with <code>code = IStatus.OK</code> and 
   * <code>exception = null</code>.
   * 
   * @param severity The severity of the message. The code is one of {@link IStatus#OK}, {@link IStatus#ERROR}, 
   * {@link IStatus#INFO}, {@link IStatus#WARNING} or {@link IStatus#CANCEL}.
   * @param message The human-readable message, localized to the current locale.
   */
  public static final void log(final int severity, final String message) {
    if (fPlugin != null) {
      fPlugin.getLog().log(new Status(severity, fPlugin.getBundle().getSymbolicName(), message));
    }
  }

  // --- Overridden methods

  public void start(final BundleContext bundleContext) throws Exception {
    super.start(bundleContext);
    fPlugin = this;
    fIndexer = new Indexer(Messages.SCA_IndexerName, FAMILY_X10_INDEXER);
    fIndexer.initialize(0);
    fIndexer.manageKeysForProjects(this, this);
  }

  public void stop(final BundleContext bundleContext) throws Exception {
    fPlugin = null;
    fIndexer = null;
    super.stop(bundleContext);
  }

  // --- Fields

  private static Plugin fPlugin;
  
  private static Indexer fIndexer;

}
