package x10.runtime.abstractmetrics;

/**
 * The default implementation of the AbstractMetrics interface
 * 
 * @author vcave
 *
 */
public class AbstractMetricsFactory {

	/**
	 * 
	 * @return A newly created AbstractMetrics class
	 */
	public static AbstractMetrics getAbstractMetricsManager() {
		return new AbstractMetricsImpl();
	}

}
