/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.config;

/**
 * An option error.
 * Used to report an error in option value in the Configuration class.
 * @author igor
 */
@SuppressWarnings("serial")
public class OptionError extends Exception {
	/**
	 * Construct an option error with a detail message.
	 */
	public OptionError(String message) {
		super(message);
	}

	/**
	 * Construct an option error with a cause.
	 */
	public OptionError(Throwable cause) {
		super(cause);
	}

	/**
	 * Construct an option error with a detail message and a cause.
	 */
	public OptionError(String message, Throwable cause) {
		super(message, cause);
	}
}

