package x10dt.tests.services;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Main class being notified of the plugin life cycle.
 * 
 * @author egeay
 */
public final class TestsServicesActivator implements BundleActivator {

  // --- Public services

  /**
   * Returns the context associated with the bundle for <b>x10dt.tests.services</b> plugin.
   * 
   * A non-null value if the plugin is started, otherwise <b>null</b>.
   */
  public static BundleContext getContext() {
    return fContext;
  }

  // --- Overridden methods

  public void start(final BundleContext context) throws Exception {
    fContext = context;
  }

  public void stop(final BundleContext context) throws Exception {
    fContext = null;
  }

  // --- Fields

  private static BundleContext fContext;

}
