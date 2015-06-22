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
package x10.util.logging;
import x10.compiler.NativeRep;
import x10.compiler.Native;

/**
 * This class is based on <a href="http://commons.apache.org/proper/commons-logging/javadocs/api-release/org/apache/commons/logging/Log.html">org.apache.commons.logging.Log</a>
 */
@NativeRep("java", "org.apache.commons.logging.Log", null, "x10.rtt.NamedType.make(\"x10.util.logging.Log\", org.apache.commons.logging.Log.class)") 
public interface Log {
    /** Log a message with debug log level. */
    @Native("java", "#this.debug(#message)")
    def debug(message:Any):void;
    /** Log an error with debug log level. */
    @Native("java", "#this.debug(#message, #t)")
    def debug(message:Any, t:CheckedThrowable):void;
    /** Log a message with error log level. */
    @Native("java", "#this.error(#message)")
    def error(message:Any):void;
    /** Log an error with error log level. */
    @Native("java", "#this.error(#message, #t)")	
    def error(message:Any, t:CheckedThrowable):void;
    /** Log a message with fatal log level. */
    @Native("java", "#this.fatal(#message)")
    def fatal(message:Any):void;
    /** Log an error with fatal log level. */
    @Native("java", "#this.fatal(#message, #t)")
    def fatal(message:Any, t:CheckedThrowable):void;
    /** Log a message with info log level. */
    @Native("java", "#this.info(#message)")
    def info(message:Any):void;
    /** Log an error with info log level. */
    @Native("java", "#this.info(#message, #t)")
    def info(message:Any, t:CheckedThrowable):void;
    /** Is debug logging currently enabled? */
    @Native("java", "#this.isDebugEnabled()")
    def isDebugEnabled():Boolean;
    /** Is error logging currently enabled? */
    @Native("java", "#this.isErrorEnabled()")
    def isErrorEnabled():Boolean;
    /** Is fatal logging currently enabled? */
    @Native("java", "#this.isFatalEnabled()")
    def isFatalEnabled():Boolean;
    /** Is info logging currently enabled? */
    @Native("java", "#this.isInfoEnabled()")
    def isInfoEnabled():Boolean;
    /** Is trace logging currently enabled? */
    @Native("java", "#this.isTraceEnabled()")
    def isTraceEnabled():Boolean;
    /** Is warn logging currently enabled? */
    @Native("java", "#this.isWarnEnabled()")
    def isWarnEnabled():Boolean;
    /** Log a message with trace log level. */
    @Native("java", "#this.trace(#message)")
    def trace(message:Any):void;
    /** Log an error with trace log level. */
    @Native("java", "#this.trace(#message, #t)")
    def trace(message:Any, t:CheckedThrowable):void;
    /** Log a message with warn log level. */
    @Native("java", "#this.warn(#message)")
    def warn(message:Any):void;
    /** Log an error with warn log level. */
    @Native("java", "#this.warn(#message, #t)")
    def warn(message:Any, t:CheckedThrowable):void;
}
