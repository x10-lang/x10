/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime.util;

import x10.config.ConfigurationError;

/**
 * A notification for parsing command-line options.
 * Used to notify the caller that the usage message needs to be shown.
 * @author igor
 */
public class ShowUsageNotification extends ConfigurationError {
	/**
	 * Construct a default show usage notification.
	 */
	public ShowUsageNotification() {
		super("");
	}
}


