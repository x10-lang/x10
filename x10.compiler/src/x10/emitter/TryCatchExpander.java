/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */
package x10.emitter;

import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import polyglot.ast.Block;
import polyglot.ast.Catch;
import polyglot.ast.Expr;
import polyglot.ast.Ext;
import polyglot.ast.JL;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Precedence;
import polyglot.ast.Term;
import polyglot.frontend.ExtensionInfo;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SubtypeSet;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.ExceptionChecker;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;
import x10.visit.X10PrettyPrinterVisitor;

public class TryCatchExpander extends Expander {
    
    private static final String TEMPORARY_EXCEPTION_VARIABLE_NAME = "$exc$";

    private class CatchBlock {
        private final String exClass;
        private final int convRequired;
        private final String exInstName;
        private final Expander bodyExpander;
        private final Catch catchBlock;

        private CatchBlock(String exClass, int convRequired, String exInstName, Expander bodyExpander) {
            this.exClass = exClass;
            this.convRequired = convRequired;
            this.exInstName = exInstName;
            this.bodyExpander = bodyExpander;
            this.catchBlock = null;
        }

        public CatchBlock(Catch catchBlock) {
            this.exClass = null;
            this.convRequired = NO_CONVERSION;
            this.exInstName = null;
            this.bodyExpander = null;
            this.catchBlock = catchBlock;
        }

        void prettyPrint(Translator tr) {
            if (catchBlock != null) {
                er.prettyPrint(catchBlock, tr);
            } else {
                w.write("catch (");
                w.write(exClass);
                w.write(" ");
                w.write(exInstName);
                w.write(") {");
                bodyExpander.expand(tr);
                w.write("}");
            }
        }

        int conversionRequired() {
            if (catchBlock == null) return convRequired;
            return TryCatchExpander.conversionRequired(catchBlock.catchType());
        }
    }

    // XTENLANG-2871 simplify Java to X10 exception conversion logic 
    // Use subtype checking instead of exact match of type name
    // Assume that conversion of j.l.Throwable also includes conversion of j.l.Exception etc. 
    public static int conversionRequired(Type catchType) {
        TypeSystem ts = catchType.typeSystem();
        int convRequired = 0;
//        if (ts.isSubtype(catchType, ts.RuntimeException())) {
//            convRequired |= RUNTIME_EXCEPTION_CONVERSION;                        
//        } else
        if (ts.isSubtype(catchType, ts.Exception())) {
            convRequired |= EXCEPTION_CONVERSION;
        } else if (ts.isSubtype(catchType, ts.Error())) {
            convRequired |= ERROR_CONVERSION;
        } else if (ts.isSubtype(catchType, ts.Throwable())) {
            convRequired |= THROWABLE_CONVERSION;
        }
        return convRequired;
    }

    private final CodeWriter w;
    private final Block block;
    private final Expander child;
    private final List<CatchBlock> catches = new ArrayList<CatchBlock>();
    private final Block finalBlock;

    public TryCatchExpander(CodeWriter w, Emitter er, Block block, Block finalBlock) {
        super(er);
        this.w = w;
        this.block = block;
        this.child = null;
        this.finalBlock = finalBlock;
    }

    public TryCatchExpander(CodeWriter w, Emitter er, TryCatchExpander expander, Block finalBlock) {
        super(er);
        this.w = w;
        this.block = null;
        this.child = expander;
        this.finalBlock = finalBlock;
    }

    public void addCatchBlock(String exClass, int convRequired, String exInstName, Expander expander) {
        catches.add(new CatchBlock(exClass, convRequired, exInstName, expander));
    }

    public void addCatchBlock(Catch catchBlock) {
        catches.add(new CatchBlock(catchBlock));
    }

    @Override
    public void expand(Translator tr) {
        w.write("try {");

        int additionalTryCatchForConversion = checkConversionRequired();
        if (additionalTryCatchForConversion != NO_CONVERSION) {
            // inner try-catch generation for exception type conversion
            w.write("try {");
        }

        if (block != null) {
            er.prettyPrint(block, tr);
        } else if (child != null) {
            child.expand(tr);
        }

        if (additionalTryCatchForConversion != NO_CONVERSION) {
            w.write("}");

            w.write("catch (" + X10PrettyPrinterVisitor.X10_CORE_THROWABLE + " " + TEMPORARY_EXCEPTION_VARIABLE_NAME + ") {");
            w.write("throw " + TEMPORARY_EXCEPTION_VARIABLE_NAME + ";");
            w.write("}");

            if ((additionalTryCatchForConversion & THROWABLE_CONVERSION) != 0) {
                w.write("catch (" + X10PrettyPrinterVisitor.JAVA_LANG_THROWABLE + " " + TEMPORARY_EXCEPTION_VARIABLE_NAME + ") {");
                w.write("throw " + X10PrettyPrinterVisitor.X10_CORE_THROWABLE_UTILITIES + ".convertJavaThrowable(" + TEMPORARY_EXCEPTION_VARIABLE_NAME + ");");
                w.write("}");
            } else {
                if ((additionalTryCatchForConversion & EXCEPTION_CONVERSION) != 0) {
                    w.write("catch (" + X10PrettyPrinterVisitor.JAVA_LANG_EXCEPTION + " " + TEMPORARY_EXCEPTION_VARIABLE_NAME + ") {");
                    w.write("throw " + X10PrettyPrinterVisitor.X10_CORE_THROWABLE_UTILITIES + ".convertJavaException(" + TEMPORARY_EXCEPTION_VARIABLE_NAME + ");");
                    w.write("}");
                } else if ((additionalTryCatchForConversion & RUNTIME_EXCEPTION_CONVERSION) != 0) {
                    w.write("catch (" + X10PrettyPrinterVisitor.JAVA_LANG_RUNTIME_EXCEPTION + " " + TEMPORARY_EXCEPTION_VARIABLE_NAME + ") {");
                    w.write("throw " + X10PrettyPrinterVisitor.X10_CORE_THROWABLE_UTILITIES + ".convertJavaRuntimeException(" + TEMPORARY_EXCEPTION_VARIABLE_NAME + ");");
                    w.write("}");
                }
                if ((additionalTryCatchForConversion & ERROR_CONVERSION) != 0) {
                    w.write("catch (" + X10PrettyPrinterVisitor.JAVA_LANG_ERROR + " " + TEMPORARY_EXCEPTION_VARIABLE_NAME + ") {");
                    w.write("throw " + X10PrettyPrinterVisitor.X10_CORE_THROWABLE_UTILITIES + ".convertJavaError(" + TEMPORARY_EXCEPTION_VARIABLE_NAME + ");");
                    w.write("}");
                }
            }

        }

        w.write("}");

        for (CatchBlock catchBlock : catches) {
            catchBlock.prettyPrint(tr);
        }

        if (finalBlock != null) {
            w.begin(0);
            w.write("finally {");
            er.prettyPrint(finalBlock, tr);
            w.write("}");
        }
    }

    // N.B. ThrowableUtilities.x10{RuntimeException,Exception,Error,Throwable}s must be sync with TryCatchExpander.knownJava{RuntimeException,Exception,Error,Throwable}s
    private static final Set<String> knownJavaRuntimeExceptions = new HashSet<String>(Arrays.asList("java.lang.ArithmeticException", "java.lang.ArrayIndexOutOfBoundsException", "java.lang.StringIndexOutOfBoundsException", "java.lang.ClassCastException", "java.lang.NumberFormatException", "java.lang.IllegalArgumentException", "java.util.NoSuchElementException", "java.lang.NullPointerException", "java.lang.UnsupportedOperationException"
        // XTENLANG-2871 stop converting j.l.{Throwable,Exception,RuntimeException,Error} to x.l.{Throwable,Exception,RuntimeException,Error}
//        ,"java.lang.RuntimeException"
    ));
    private static final Set<String> knownJavaExceptions = new HashSet<String>(Arrays.asList("java.io.FileNotFoundException", "java.io.EOFException", "java.io.NotSerializableException", "java.io.IOException", "java.lang.InterruptedException"
        // XTENLANG-2871 stop converting j.l.{Throwable,Exception,RuntimeException,Error} to x.l.{Throwable,Exception,RuntimeException,Error}
//        , "java.lang.Exception"
    ));
    private static final Set<String> knownJavaErrors = new HashSet<String>(Arrays.asList("java.lang.OutOfMemoryError", "java.lang.StackOverflowError", "java.lang.AssertionError"
        // XTENLANG-2871 stop converting j.l.{Throwable,Exception,RuntimeException,Error} to x.l.{Throwable,Exception,RuntimeException,Error}
//        , "java.lang.Error"
    ));
    private static final Set<String> knownJavaThrowables = Collections.<String>emptySet();
    // XTENLANG-2871 stop converting j.l.{Throwable,Exception,RuntimeException,Error} to x.l.{Throwable,Exception,RuntimeException,Error}
    public static boolean isKnownJavaThrowable(Type type) {
        String typeName = type.toString();
        return knownJavaRuntimeExceptions.contains(typeName) || knownJavaExceptions.contains(typeName) || knownJavaErrors.contains(typeName) || knownJavaThrowables.contains(typeName);
    }
    
    public static final int NO_CONVERSION = 0;
    public static final int RUNTIME_EXCEPTION_CONVERSION = 0x01;
    public static final int EXCEPTION_CONVERSION = 0x02;
    public static final int ERROR_CONVERSION = 0x04;
    public static final int THROWABLE_CONVERSION = 0x08;

    private int checkConversionRequired() {
        int rc = 0;
        for (CatchBlock catchBlock : catches) {
            rc |= catchBlock.conversionRequired();
        }
        return rc;
    }
}
