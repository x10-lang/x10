/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

public class JClass {
    static class AThrowable extends Throwable {
	public AThrowable() {
	    super();
	}
	public AThrowable(String detailMessage) {
	    super(detailMessage);
	}
	public AThrowable(String detailMessage, Throwable throwable) {
	    super(detailMessage, throwable);
	}
	public AThrowable(Throwable throwable) {
	    super(throwable);
	}
    }
    static class AnException extends Exception {
	public AnException() {
	    super();
	}
	public AnException(String detailMessage) {
	    super(detailMessage);
	}
	public AnException(String detailMessage, Throwable throwable) {
	    super(detailMessage, throwable);
	}
	public AnException(Throwable throwable) {
	    super(throwable);
	}
    }
    static class ARuntimeException extends RuntimeException {
	public ARuntimeException() {
	    super();
	}
	public ARuntimeException(String detailMessage) {
	    super(detailMessage);
	}
	public ARuntimeException(String detailMessage, Throwable throwable) {
	    super(detailMessage, throwable);
	}
	public ARuntimeException(Throwable throwable) {
	    super(throwable);
	}
    }
    static class AnError extends Error {
	public AnError() {
	    super();
	}
	public AnError(String detailMessage) {
	    super(detailMessage);
	}
	public AnError(String detailMessage, Throwable throwable) {
	    super(detailMessage, throwable);
	}
	public AnError(Throwable throwable) {
	    super(throwable);
	}
    }

    public static void throwable(String detailMessage) throws Throwable {
	throw new Throwable(detailMessage);
    }
    public static void exception(String detailMessage) throws Exception {
	throw new Exception(detailMessage);
    }
    public static void runtimeException(String detailMessage) {
	throw new RuntimeException(detailMessage);
    }
    public static void error(String detailMessage) {
	throw new Error(detailMessage);
    }

    public static void aThrowable(String detailMessage) throws Throwable {
	throw new AThrowable(detailMessage);
    }
    public static void anException(String detailMessage) throws Exception {
	throw new AnException(detailMessage);
    }
    public static void aRuntimeException(String detailMessage) {
	throw new ARuntimeException(detailMessage);
    }
    public static void anError(String detailMessage) {
	throw new AnError(detailMessage);
    }
}
