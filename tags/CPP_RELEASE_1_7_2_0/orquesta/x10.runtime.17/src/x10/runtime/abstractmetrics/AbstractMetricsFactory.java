/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime.abstractmetrics;

import x10.runtime.VMInterface;

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
		if (VMInterface.ABSTRACT_EXECUTION_STATS) return new AbstractMetricsImpl();
		else return null;
	}

}
