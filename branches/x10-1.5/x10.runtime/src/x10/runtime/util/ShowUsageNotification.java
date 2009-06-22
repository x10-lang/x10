/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime.util;

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


