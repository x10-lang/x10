/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime.util;

/**
 * A configuration error.
 * Used to report an error while loading a configuration file from the
 * Configuration class.
 * @author igor
 */
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

