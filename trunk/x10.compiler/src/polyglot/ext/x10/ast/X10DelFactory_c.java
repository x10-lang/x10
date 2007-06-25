/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Oct 7, 2004
 */
package polyglot.ext.x10.ast;

import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Conditional;
import polyglot.ast.JL;
import polyglot.ast.Node;
import polyglot.ast.AbstractDelFactory_c;
import polyglot.ast.JL_c;
import polyglot.ext.x10.extension.X10Del_c;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10Type_c;
import polyglot.ext.x10.visit.X10PrettyPrinterVisitor;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.visit.Translator;
import polyglot.visit.TypeChecker;

/**
 * @author Christian Grothoff
 */
public class X10DelFactory_c extends AbstractDelFactory_c {

	/**
	 * A delegate that redirects translate to the X10PrettyPrinterVisitor.
	 */
	public static class TD extends X10Del_c {
		public void translate(CodeWriter w, Translator tr) {
			if (jl() instanceof Node) {
				Node n = (Node) jl();
				X10Ext ext = (X10Ext) n.ext();
				if (ext != null && ext.comment() != null)
					w.write(ext.comment());
			}
			new X10PrettyPrinterVisitor(w,tr).visitAppropriate(jl());
		}
	};

	public JL delNodeImpl() {
		return new X10Del_c();
	}

	/**
	 * For each term, add the delegate that redirects translate to the
	 * X10PrettyPrinterVisitor.
	 */
	public JL delTermImpl() {
		return new TD();
	}

	/**
	 * For each method declaration, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor.
	 */
	public JL delMethodDeclImpl() {
		return new TD();
	}

	/**
	 * For each field declaration, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor.
	 */
	public JL delFieldDeclImpl() {
		return new TD();
	}

	/**
	 * For ternaries, also implement a separate typeCheck method.
	 */
	public JL delConditionalImpl() {
		return new TD() {
			public Node typeCheck(TypeChecker tc) throws SemanticException {
				X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
				Conditional c = (Conditional) jl();
				Type t1 = c.consequent().type();
				Type t2 = c.alternative().type();
				if (t1.isNull() && t2.isNumeric())
					return c.type(ts.createNullableType(t2.position(), (X10NamedType) t2));
				if (t1.isNumeric() && t2.isNull())
					return c.type(ts.createNullableType(t1.position(), (X10NamedType) t1));
				return c.typeCheck(tc);
			}
		};
	}

	/**
	 * For each canonical type node, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor.
	 */
	public JL delCanonicalTypeNodeImpl() {
		return new TD();
	}

	/**
	 * For each nullable type node, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor.
	 */
	public JL delNullableNodeImpl() {
		return new TD();
	}

	/**
	 * For each future type node, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor.
	 */
	public JL delFutureNodeImpl() {
		return new TD();
	}

	/**
	 * For each async, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor.
	 */
	public JL delAsyncImpl() {
		return new TD();
	}

	/**
	 * For each ateach loop, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor.
	 */
	public JL delAtEachImpl() {
		return new TD();
	}

	/**
	 * For each foreach loop, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor.
	 */
	public JL delForEachImpl() {
		return new TD();
	}

	/**
	 * For each x10 for loop, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor.
	 */
	public JL delForLoopImpl() {
		return new TD();
	}

	/**
	 * For each finish, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor.
	 */
	public JL delFinishImpl() {
		return new TD();
	}

	/**
	 * For each closure, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor.
	 */
	public JL delClosureImpl() {
		return new TD();
	}
}

