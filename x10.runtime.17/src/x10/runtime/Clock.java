/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime;

/**
 * Used to access XRX clock implementation from Java runtime
 * 
 * @author tardieu
 *
 */
public interface Clock {
	public void _register();
	public void _resume();
	public void _next();
	public void _drop();
}
