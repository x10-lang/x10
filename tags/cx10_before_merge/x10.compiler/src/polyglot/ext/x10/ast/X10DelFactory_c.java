/*
 * Created on Oct 7, 2004
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Conditional;
import polyglot.ast.JL;
import polyglot.ast.Node;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ext.jl.ast.AbstractDelFactory_c;
import polyglot.ext.jl.ast.JL_c;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.visit.X10PrettyPrinterVisitor;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

/**
 * @author Christian Grothoff
 */
public class X10DelFactory_c extends AbstractDelFactory_c {

	/**
	 * A delegate that redirects prettyPrint to the X10PrettyPrinterVisitor.
	 */
	public static class PP extends JL_c {
		public void prettyPrint(CodeWriter w, PrettyPrinter pp) {
			new X10PrettyPrinterVisitor(w,pp).visitAppropriate(jl());
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
					return c.type(ts.createNullableType(t2.position(), (X10Type) t2));
				if (t1.isNumeric() && t2.isNull())
					return c.type(ts.createNullableType(t1.position(), (X10Type) t1));
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
				if (t.isNullable()) {
					w.write("/*nullable*/");
					printType(w, t.toNullable().base());
				} else if (t.isArray()) {
					printType(w, t.toArray().base());
					w.write("[]");
				} else
					w.write(t.toString());
			}
			public void prettyPrint(CodeWriter w, PrettyPrinter pp) {
//				System.out.println("Pretty-printing canonical type node for "+jl());
				Type t = ((CanonicalTypeNode) jl()).type();
				if (t != null)
					printType(w, t);
				else
					super.prettyPrint(w, pp);
			}
		};
	}
}

