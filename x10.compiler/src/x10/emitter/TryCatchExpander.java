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
	
	private static final boolean reduceCatches = true;
	private static final String TEMPORARY_EXCEPTION_VARIABLE_NAME = "$exc$";

	private class CatchBlock {
		private final String exClass;
		private final String exInstName;
		private final Expander bodyExpander;
		private final Catch catchBlock;

		private CatchBlock(String exClass, String exInstName,
				Expander bodyExpander) {
			this.exClass = exClass;
			this.exInstName = exInstName;
			this.bodyExpander = bodyExpander;
			this.catchBlock = null;
		}

		public CatchBlock(Catch catchBlock) {
			this.exClass = null;
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
		    String catchExClassName = (catchBlock != null) ? catchBlock.catchType().toString() : exClass;
		    int rc = 0;
		    for (String exc : x10RuntimeExceptions)
		        if (catchExClassName.equals(exc)) {
		            rc |= RUNTIME_EXCEPTION_CONVERSION;
		            break;
		        }
		    for (String exc : x10Exceptions)
		        if (catchExClassName.equals(exc)) {
		            rc |= EXCEPTION_CONVERSION;
		            break;
		        }
            for (String exc : x10Errors)
                if (catchExClassName.equals(exc)) {
                    rc |= ERROR_CONVERSION;
                    break;
                }
            for (String exc : x10Throwables)
                if (catchExClassName.equals(exc)) {
                    rc |= THROWABLE_CONVERSION;
                    break;
                }
            return rc;
		}

	}

	private final CodeWriter w;
	private final Block block;
	private final Expander child;
	private final List<CatchBlock> catches = new ArrayList<CatchBlock>();
	private final Block finalBlock;

	public TryCatchExpander(CodeWriter w, Emitter er, Block block,
			Block finalBlock) {
		super(er);
		this.w = w;
		this.block = block;
		this.child = null;
		this.finalBlock = finalBlock;
	}

	public TryCatchExpander(CodeWriter w, Emitter er,
			TryCatchExpander expander, Block finalBlock) {
		super(er);
		this.w = w;
		this.block = null;
		this.child = expander;
		this.finalBlock = finalBlock;
	}

	public void addCatchBlock(String exClass, String exInstName,
			Expander expander) {
		catches.add(new CatchBlock(exClass, exInstName, expander));
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

            w.write("catch (x10.core.Throwable " + TEMPORARY_EXCEPTION_VARIABLE_NAME + ") {");
            w.write("throw " + TEMPORARY_EXCEPTION_VARIABLE_NAME + ";");
            w.write("}");

            if (reduceCatches) {
            	
            if ((additionalTryCatchForConversion & THROWABLE_CONVERSION) != 0) {
                w.write("catch (java.lang.Throwable " + TEMPORARY_EXCEPTION_VARIABLE_NAME + ") {");
                w.write("throw x10.core.ThrowableUtilities.convertJavaThrowable(" + TEMPORARY_EXCEPTION_VARIABLE_NAME + ");");
                w.write("}");
            } else {
            	if ((additionalTryCatchForConversion & EXCEPTION_CONVERSION) != 0) {
            		w.write("catch (java.lang.Exception " + TEMPORARY_EXCEPTION_VARIABLE_NAME + ") {");
            		w.write("throw x10.core.ThrowableUtilities.convertJavaException(" + TEMPORARY_EXCEPTION_VARIABLE_NAME + ");");
            		w.write("}");
            	}
                else if ((additionalTryCatchForConversion & RUNTIME_EXCEPTION_CONVERSION) != 0) {
                	w.write("catch (java.lang.RuntimeException " + TEMPORARY_EXCEPTION_VARIABLE_NAME + ") {");
                	w.write("throw x10.core.ThrowableUtilities.convertJavaRuntimeException(" + TEMPORARY_EXCEPTION_VARIABLE_NAME + ");");
                	w.write("}");
                }
                if ((additionalTryCatchForConversion & ERROR_CONVERSION) != 0) {
                	w.write("catch (java.lang.Error " + TEMPORARY_EXCEPTION_VARIABLE_NAME + ") {");
                	w.write("throw x10.core.ThrowableUtilities.convertJavaError(" + TEMPORARY_EXCEPTION_VARIABLE_NAME + ");");
                	w.write("}");
                }
            }
            
            } else {

            if ((additionalTryCatchForConversion & RUNTIME_EXCEPTION_CONVERSION) != 0) {
                w.write("catch (java.lang.RuntimeException " + TEMPORARY_EXCEPTION_VARIABLE_NAME + ") {");
                w.write("throw x10.core.ThrowableUtilities.getCorrespondingX10Exception(" + TEMPORARY_EXCEPTION_VARIABLE_NAME + ");");
                w.write("}");
            }
            if ((additionalTryCatchForConversion & EXCEPTION_CONVERSION) != 0) {
                w.write("catch (java.lang.Exception " + TEMPORARY_EXCEPTION_VARIABLE_NAME + ") {");
                w.write("throw x10.core.ThrowableUtilities.getCorrespondingX10Exception(" + TEMPORARY_EXCEPTION_VARIABLE_NAME + ");");
                w.write("}");
            }
            if ((additionalTryCatchForConversion & ERROR_CONVERSION) != 0) {
                w.write("catch (java.lang.Error " + TEMPORARY_EXCEPTION_VARIABLE_NAME + ") {");
                w.write("throw x10.core.ThrowableUtilities.getCorrespondingX10Error(" + TEMPORARY_EXCEPTION_VARIABLE_NAME + ");");
                w.write("}");
            }
            if ((additionalTryCatchForConversion & THROWABLE_CONVERSION) != 0) {
                w.write("catch (java.lang.Throwable " + TEMPORARY_EXCEPTION_VARIABLE_NAME + ") {");
                w.write("throw x10.core.ThrowableUtilities.getCorrespondingX10Throwable(" + TEMPORARY_EXCEPTION_VARIABLE_NAME + ");");
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

    // N.B. ThrowableUtilities.x10RuntimeExceptions must be sync with TryCatchExpander.x10RuntimeExceptions
	static final String[] x10RuntimeExceptions = {
//	    "x10.array.UnboundedRegionException",	/*no corresponding Java exception*/
//	    "x10.io.IORuntimeException",			/*no corresponding Java exception*/
	    "x10.lang.ArithmeticException",
	    "x10.lang.ArrayIndexOutOfBoundsException",
		"x10.lang.StringIndexOutOfBoundsException",
//	    "x10.lang.BadPlaceException",			/*no corresponding Java exception*/
	    "x10.lang.ClassCastException",
//	    "x10.lang.ClockUseException",			/*no corresponding Java exception*/
		"x10.lang.NumberFormatException",
	    "x10.lang.IllegalArgumentException",
//	    "x10.lang.IllegalOperationException",	/*no corresponding Java exception*/
        "x10.util.NoSuchElementException",
        "x10.lang.NullPointerException",
        "x10.lang.UnsupportedOperationException",
	    // XTENLANG-2871 stop converting j.l.{Throwable,Exception,RuntimeException,Error} to x.l.{Throwable,Exception,RuntimeException,Error}
//        "x10.lang.RuntimeException",
//	    "x10.lang.Exception",
//	    "x10.lang.Throwable"
	};
    // N.B. ThrowableUtilities.x10Exceptions must be sync with TryCatchExpander.x10Exceptions
	static final String[] x10Exceptions = {
		"x10.io.FileNotFoundException",
        "x10.io.EOFException",
        "x10.io.NotSerializableException",
        "x10.io.IOException",
        "x10.lang.InterruptedException",
	    // XTENLANG-2871 stop converting j.l.{Throwable,Exception,RuntimeException,Error} to x.l.{Throwable,Exception,RuntimeException,Error}
//        "x10.lang.Exception",
//        "x10.lang.Throwable"
	};
    // N.B. ThrowableUtilities.x10Errors must be sync with TryCatchExpander.x10Errors
    static final String[] x10Errors = {
        "x10.lang.OutOfMemoryError",
        "x10.lang.StackOverflowError",
        "x10.lang.AssertionError",
	    // XTENLANG-2871 stop converting j.l.{Throwable,Exception,RuntimeException,Error} to x.l.{Throwable,Exception,RuntimeException,Error}
//        "x10.lang.Error",
//        "x10.lang.Throwable"
    };
    // N.B. ThrowableUtilities.x10Throwables must be sync with TryCatchExpander.x10Throwables
    static final String[] x10Throwables = {
	// XTENLANG-2871 stop converting j.l.{Throwable,Exception,RuntimeException,Error} to x.l.{Throwable,Exception,RuntimeException,Error}
//        "x10.lang.Throwable"
    };
    static final int NO_CONVERSION = 0;
    static final int RUNTIME_EXCEPTION_CONVERSION = 0x01;
    static final int EXCEPTION_CONVERSION = 0x02;
    static final int ERROR_CONVERSION = 0x04;
    static final int THROWABLE_CONVERSION = 0x08;
    
	private int checkConversionRequired() {
	    int rc = 0;
        for (CatchBlock catchBlock : catches) {
            rc |= catchBlock.conversionRequired();
        }
	    return rc;
	}
}