/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.config;

/**
 * An option error.
 * Used to report an error in option value in the Configuration class.
 * @author igor
 */
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

