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
	 * A delegate that redirects prettyPrint to the X10PrettyPrinterVisitor.
	 */
	public static class PP extends JL_c {
		public void translate(CodeWriter w, Translator tr) {
			new X10PrettyPrinterVisitor(w,tr).visitAppropriate(jl());
		}
	};

	/**
	 * For each term, add the delegate that redirects prettyPrint to the
	 * X10PrettyPrinterVisitor.
	 */
	public JL delTermImpl() {
		return new PP();
	}

	/**
	 * For each method declaration, add the delegate that redirects prettyPrint
	 * to the X10PrettyPrinterVisitor.
	 */
	public JL delMethodDeclImpl() {
		return new PP();
	}

	/**
	 * For ternaries, also implement a separate typeCheck method.
	 */
	public JL delConditionalImpl() {
		return new PP() {
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
	 * For ternaries, also implement a separate typeCheck method.
	 */
	public JL delCanonicalTypeNodeImpl() {
		return new JL_c() {
			private void printType(CodeWriter w, Type type) {
				X10Type t = (X10Type) type;
                X10TypeSystem ts = (X10TypeSystem) t.typeSystem();
				if (ts.isNullable(t)) {
					w.write("/*nullable*/");
					printType(w, X10Type_c.toNullable(t).base());
				} else if (t.isArray()) {
					printType(w, t.toArray().base());
					w.write("[]");
				} else
					w.write(t.toString());
			}
			public void translate(CodeWriter w, Translator tr) {
//				System.out.println("Pretty-printing canonical type node for "+jl());
				Type t = ((CanonicalTypeNode) jl()).type();
				if (t != null)
					printType(w, t);
				else
					super.translate(w, tr);
			}
		};
	}
}

