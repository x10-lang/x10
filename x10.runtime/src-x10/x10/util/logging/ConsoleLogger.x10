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
package x10.util.logging;
import x10.util.StringBuilder;

import x10.compiler.Inline;
import x10.compiler.NativeRep;
import x10.compiler.Native;


@NativeRep("java", "java.lang.Object", null, "x10.rtt.Types.ANY")
class ConsoleLogger implements Log {

    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    static val DEFAULT_LEVEL:Int = getDefaultLevel();

    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    static val DEFAULT_LEVEL_ENV:String = "X10_LOG_LEVEL";

    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    static val LEVEL_ALL:Int = 7n;
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    static val LEVEL_TRACE:Int = 6n;
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    static val LEVEL_DEBUG:Int = 5n;
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    static val LEVEL_INFO:Int = 4n;
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    static val LEVEL_WARN:Int = 3n;
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    static val LEVEL_ERROR:Int = 2n;
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    static val LEVEL_FATAL:Int = 1n;
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    static val LEVEL_NONE:Int = 0n;
    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    private static val LEVEL_STRINGS:Rail[String] = ["NONE", "FATAL", "ERROR", "WARN", "INFO", "DEBUG", "TRACE","ALL"];
    

    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    private static def getDefaultLevel():Int {
        var v:String = System.getenv(DEFAULT_LEVEL_ENV);
        if (v == null) {
            return LEVEL_INFO;
        }

        val prefix = "LEVEL_";
        val prefixLength = prefix.length();
        if (v.length() > prefixLength && prefix.equalsIgnoreCase(v.substring(0n,prefixLength))) {
            v = v.substring(prefixLength);
        }
        Console.ERR.println(v);
        for (i in LEVEL_STRINGS.range()) {
            if(LEVEL_STRINGS(i).equalsIgnoreCase(v)) {
                return i as Int;
            }
        }
        return LEVEL_INFO;
    }

    @Inline
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    private static def logLevelString(logLevel:Int):String = LEVEL_STRINGS(logLevel);
        
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    private static def getLastPartOfDotName(name:String):String {
        val index = name.lastIndexOf(".");
        if (index == -1n) {
            return name;
        } else if (index >= name.length() - 1n) {
            return name;
        } else {
            return name.substring(index + 1n);
        }
    }
    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def this(name:String) {
        this(name, DEFAULT_LEVEL);
    }

    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def this(name:String, logLevel:Int) {
        this(name, logLevel, getLastPartOfDotName(name));
    }

    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def this(name:String, logLevel:Int, outputName:String) {
        this.name = name;
        this.logLevel = logLevel;
        this.outputName = outputName;
    }

    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def isTraceEnabled():Boolean = LEVEL_TRACE <= logLevel;    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def isDebugEnabled():Boolean = LEVEL_DEBUG <= logLevel;    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def isInfoEnabled() :Boolean = LEVEL_INFO <= logLevel;
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def isWarnEnabled() :Boolean = LEVEL_WARN <= logLevel;
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def isErrorEnabled():Boolean = LEVEL_ERROR <= logLevel;
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def isFatalEnabled():Boolean = LEVEL_FATAL <= logLevel;
    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def trace(message:Any):void {
        log(LEVEL_TRACE, message);
    }
    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def trace(message:Any, t:CheckedThrowable):void {
        log(LEVEL_TRACE, message, t);
    }
    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def debug(message:Any):void {
        log(LEVEL_DEBUG, message);
    }

    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def debug(message:Any, t:CheckedThrowable):void {
        log(LEVEL_DEBUG, message, t);
    }
        
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def info(message:Any):void {
        log(LEVEL_INFO, message);
    }
    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def info(message:Any, t:CheckedThrowable):void {
        log(LEVEL_INFO, message, t);
    }
    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def warn(message:Any):void {
        log(LEVEL_WARN, message);
    }
    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def warn(message:Any, t:CheckedThrowable):void {
        log(LEVEL_WARN, message, t);
    }
    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def error(message:Any):void {
        log(LEVEL_ERROR, message);
    }
    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def error(message:Any, t:CheckedThrowable):void {
        log(LEVEL_ERROR, message, t);
    }
    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def fatal(message:Any):void {
        log(LEVEL_FATAL, message);
    }
    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def fatal(message:Any, t:CheckedThrowable):void {
        log(LEVEL_FATAL, message, t);
    }
    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def toString():String = "ConsoleLog<" + name + "@" + logLevelString(logLevel) + ">";
    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    private val name:String; 
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    private val outputName:String;
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    private var logLevel:Int;
        
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    private def setLogLevel(newLevel:Int):void {
        if (newLevel < LEVEL_NONE) {
            logLevel = LEVEL_NONE;
        } else if (newLevel > LEVEL_ALL) {
            logLevel = LEVEL_ALL;
        } else {
            logLevel = newLevel;
        }
    }
    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    private def isLogEnabled(level:Int):Boolean = level <= logLevel;    
    
    @Inline
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    private def log(level:Int, message:Any):void {
        if (!isLogEnabled(level)) {
            return;
        }
        val sb = new StringBuilder();
        sb.add(logLevelString(level));
        sb.add(" ");
        sb.add(outputName);
        sb.add(": ");
        sb.add(message);

        Console.ERR.println(sb.result());
    }

    @Inline
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    private def log(level:Int, message:Any, t:CheckedThrowable):void {
        if (!isLogEnabled(level)) {
            return;
        }
        if (t == null) {
            log(level, message);
            return;
        }
        val sb = new StringBuilder();
        sb.add(logLevelString(level));
        sb.add(" ");
        sb.add(outputName);
        sb.add(": ");
        sb.add(message);
        sb.add(", cause: ");
        sb.add(t.getCause());
        Console.ERR.println(sb.result());
        t.printStackTrace();
    }
}
