package x10dt.search.core;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.IPathEntry;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.pdb.facts.db.FactKey;
import org.eclipse.imp.pdb.facts.db.IFactKey;
import org.eclipse.imp.pdb.facts.db.context.ProjectContext;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.indexing.IndexManager;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IStartup;
import org.osgi.framework.BundleContext;

import x10dt.core.X10DTCorePlugin;
import x10dt.core.utils.X10DTCoreConstants;
import x10dt.search.core.pdb.SearchDBTypes;
import x10dt.search.core.pdb.X10FactTypeNames;

import static x10dt.core.X10DTCorePlugin.*;

/**
 * Main class being notified of the plugin life cycle.
 * 
 * @author egeay
 */
public class SearchCoreActivator extends Plugin implements IStartup, IResourceChangeListener {

  /**
   * The unique plugin id for <b>x10dt.search.core</b>.
   */
  public static final String PLUGIN_ID = "x10dt.search.core"; //$NON-NLS-1$
  
  // --- IStartup's interface methods implementation
  
  public void earlyStartup() {
    final Type hierarchyType = SearchDBTypes.getInstance().getType(X10FactTypeNames.X10_TypeHierarchy);
    for (final IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
      try {
        if (project.isAccessible()) {
          if (project.hasNature(X10_CPP_PRJ_NATURE_ID) || project.hasNature(X10_PRJ_JAVA_NATURE_ID)) {
            final IFactKey key = new FactKey(hierarchyType, new ProjectContext(ModelFactory.open(project)));
            IndexManager.keepFactUpdated(key);
          }
        }
      } catch (CoreException except) {
        log(IStatus.ERROR, NLS.bind(Messages.SCA_ProjectNatureAccessFailed, project.getName()), except);
      } catch (ModelException except) {
        log(IStatus.ERROR, NLS.bind(Messages.SCA_ProjectNonExistent, project.getName()), except);
      }
    }
  }
  
  // --- IResourceChangeListener's interface methods implementation
  
  public void resourceChanged(final IResourceChangeEvent event) {
    final IResourceDelta rootResourceDelta = event.getDelta();
    for (final IResourceDelta resourceDelta : rootResourceDelta.getAffectedChildren()) {
      if (resourceDelta.getResource().getType() == IResource.PROJECT) {
        final IProject project = resourceDelta.getResource().getProject();
        switch (resourceDelta.getKind()) {
          case IResourceDelta.ADDED: {
            final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(2);
            final Future<Void> future = scheduledExecutor.submit(new Callable<Void>() {

              public Void call() {
                IndexManager.lock();
                while (! project.isOpen()) {
                  try {
                    synchronized (this) {
                      wait(200);
                    }
                  } catch (InterruptedException except) {
                    IndexManager.unlock();
                    return null;
                  }
                }
                try {
                  final ISourceProject javaProject = ModelFactory.getProject(project);
                  while (! hasRightNature() || ! hasX10Container(javaProject)) {
                    try {
                      synchronized (this) {
                        wait(200);
                      }
                    } catch (InterruptedException except) {
                      IndexManager.unlock();
                      return null;
                    }
                  }
                } catch (Exception except) {
                  log(IStatus.ERROR, NLS.bind(Messages.SCA_ProjectStatusError, project.getName()), except);
                  IndexManager.unlock();
                  return null;
                }
                
                try {
                  final Type hierarchyType = SearchDBTypes.getInstance().getType(X10FactTypeNames.X10_TypeHierarchy);
                  final IFactKey key = new FactKey(hierarchyType, new ProjectContext(ModelFactory.open(project)));
                  IndexManager.keepFactUpdated(key);
                } catch (ModelException except) {
                  log(IStatus.ERROR, NLS.bind(Messages.SCA_ProjectNonExistent, project.getName()), except);
                } finally {
                  IndexManager.unlock();
                }
                
                return null;
              }
              
              private boolean hasRightNature() throws CoreException {
                return project.hasNature(X10DTCorePlugin.X10_CPP_PRJ_NATURE_ID) || project.hasNature(X10DTCorePlugin.X10_PRJ_JAVA_NATURE_ID);
              }
              
              private boolean hasX10Container(final ISourceProject javaProject) throws ModelException {
            	  if(javaProject != null)
            	  {
            		  for (final IPathEntry cpEntry : javaProject.getBuildPath(LanguageRegistry.findLanguage("X10"))) {
                        if (X10DTCoreConstants.X10_CONTAINER_ENTRY_ID.equals(cpEntry.getRawPath().toString())) {
                        return true;
                      }
                    }
            	  }
                
                  return false;
              }
              
            });
            
            scheduledExecutor.schedule(new Runnable() {
              
              public void run() {
                future.cancel(true);
                scheduledExecutor.shutdown();
              }
              
            }, 2, TimeUnit.SECONDS);
            
            break;
          }
            
          case IResourceDelta.REMOVED: {
            final Type hierarchyType = SearchDBTypes.getInstance().getType(X10FactTypeNames.X10_TypeHierarchy);
            final ISourceProject sourceProject = ModelFactory.getProject(project);
            if (sourceProject != null) {
              IndexManager.cancelFactUpdating(new FactKey(hierarchyType, new ProjectContext(sourceProject)));
            }
            break;
          }
            
          default:
            // Nothing to do.
        }
      }
    }
  }
  
  // --- Public services

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
//  IndexManager.initializeAndSchedule(2000);
    ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
    fPlugin = this;
  }

  public void stop(final BundleContext bundleContext) throws Exception {
    fPlugin = null;
    ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
    super.stop(bundleContext);
  }

  // --- Fields

  private static Plugin fPlugin;

}
