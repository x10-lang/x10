package x10.runtime.abstractmetrics;

import x10.runtime.JITTimeConstants;

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
		if (JITTimeConstants.ABSTRACT_EXECUTION_STATS) return new AbstractMetricsImpl();
		else return null;
	}

}
