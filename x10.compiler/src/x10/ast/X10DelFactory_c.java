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

package x10.ast;

import polyglot.ast.AbstractDelFactory_c;
import polyglot.ast.JL;
import polyglot.ast.Node;
import polyglot.util.CodeWriter;
import polyglot.util.OptimalCodeWriter;
import polyglot.visit.Translator;
import x10.X10CompilerOptions;
import x10.extension.X10Del_c;
import x10.extension.X10Ext;
import x10.visit.X10DelegatingVisitor;
import x10.visit.X10PrettyPrinterVisitor;
import x10c.visit.DebugCodeWriter;

/**
 * @author Christian Grothoff
 */
public class X10DelFactory_c extends AbstractDelFactory_c {

	/**
	 * Used to allow subclasses to override the code generator.
	 */
	protected X10DelegatingVisitor makeCodeGenerator(CodeWriter w, Translator tr) {
		return new X10PrettyPrinterVisitor(w, tr);
	}

	/**
	 * A delegate that redirects translate to the object given by makeCodeGenerator.
	 */
	public class TD extends X10Del_c {
		public void translate(CodeWriter w, Translator tr) {
			if (jl() instanceof Node) {
				Node n = (Node) jl();
				X10Ext ext = (X10Ext) n.ext();
				if (ext != null && ext.comment() != null)
					w.writeln(ext.comment());
			}
			if (((X10CompilerOptions)tr.job().extensionInfo().getOptions()).x10_config.DEBUG_CODEGEN
			    && !(jl() instanceof TypeDecl) && w.getClass() != DebugCodeWriter.class)
			    w = new DebugCodeWriter(w, tr.job());
			makeCodeGenerator(w, tr).visitAppropriate(jl());
		}
	};

	public JL delNodeImpl() {
		return new TD();
	}

	/**
	 * For each async, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor.
	 */
	public JL delAsyncImpl() {
		return delNodeImpl();
	}

	/**
	 * For each ateach loop, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor.
	 */
	public JL delAtEachImpl() {
		return delNodeImpl();
	}

	/**
	 * For each foreach loop, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor.
	 */
	public JL delForEachImpl() {
		return delNodeImpl();
	}

	/**
	 * For each x10 for loop, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor.
	 */
	public JL delForLoopImpl() {
		return delNodeImpl();
	}

	/**
	 * For each finish, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor.
	 */
	public JL delFinishImpl() {
		return delNodeImpl();
	}

	/**
	 * For each closure, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor.
	 */
	public JL delClosureImpl() {
		return delNodeImpl();
	}

	/**
	 * For each future, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor.
	 */
	public JL delFutureImpl() {
		return delNodeImpl();
	}
}

