/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
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


