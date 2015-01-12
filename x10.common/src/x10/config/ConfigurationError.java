/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.config;

/**
 * A configuration error.
 * Used to report an error while loading a configuration file from the
 * Configuration class.
 * @author igor
 */
@SuppressWarnings("serial")
public class ConfigurationError extends Exception {
	/**
	 * Construct a configuration error with a detail message.
	 */
	public ConfigurationError(String message) {
		super(message);
	}

	/**
	 * Construct a configuration error with a cause.
	 */
	public ConfigurationError(Throwable cause) {
		super(cause);
	}

	/**
	 * Construct a configuration error with a detail message and a cause.
	 */
	public ConfigurationError(String message, Throwable cause) {
		super(message, cause);
	}
}

