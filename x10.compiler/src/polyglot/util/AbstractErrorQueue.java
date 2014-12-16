/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.util;


/**
 * A <code>StdErrorQueue</code> handles outputing error messages.
 */
public abstract class AbstractErrorQueue implements ErrorQueue
{
    protected boolean flushed;
    protected int errorCount; // todo: errorCount is wrong if we remove/add errors from SilentErrorQueue or StdErrorQueue, so we should move it to SimpleErrorQueue  
    protected final int limit;
    protected final String name;
    
    public AbstractErrorQueue(int limit, String name) {
	this.errorCount = 0;
	this.limit = limit;
	this.name = name;
        this.flushed = true;
    }

    public final void enqueue(int type, String message) {
	enqueue(type, message, null);
    }

    public final void enqueue(int type, String message, Position position) {
	enqueue(new ErrorInfo(type, message, position));
    }

    public final void enqueue(ErrorInfo e) {
	if (ErrorInfo.isErrorKind(e.getErrorKind())) {
	    errorCount++;
	}

	flushed = false;

        displayError(e);

	if (errorCount >= limit) {
	    tooManyErrors(e);
	    flush();
	    throw new ErrorLimitError();
	}
    }

    protected abstract void displayError(ErrorInfo error);
    
    /**
     * This method is called when we have had too many errors. This method
     * give subclasses the opportunity to output appropriate messages, or
     * tidy up.
     * 
     * @param lastError the last error that pushed us over the limit
     */
    protected void tooManyErrors(ErrorInfo lastError) {
    }
    
    /**
     * This method is called to flush the error queue. Subclasses may want to
     * print summary information in this method.
     */
    public void flush() {
        flushed = true;
    }

    public final boolean hasErrors() {
      return errorCount > 0;
    }

    public final int errorCount() {
        return errorCount;
    }
}
