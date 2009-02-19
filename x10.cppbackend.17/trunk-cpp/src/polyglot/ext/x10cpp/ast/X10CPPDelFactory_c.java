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
package polyglot.ext.x10cpp.ast;

import polyglot.ast.Block;
import polyglot.ast.Conditional;
import polyglot.ast.ForInit;
import polyglot.ast.JL;
import polyglot.ast.Node;
import polyglot.ext.x10.ast.X10DelFactory_c;
import polyglot.ext.x10.extension.X10Del_c;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10cpp.Configuration;
import polyglot.ext.x10cpp.types.X10CPPContext_c;
import polyglot.ext.x10cpp.visit.MessagePassingCodeGenerator;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.visit.Translator;
import polyglot.visit.TypeChecker;
import x10c.util.StreamWrapper;

/**
 * @author Christian Grothoff
 */
public class X10CPPDelFactory_c extends X10DelFactory_c {

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
			new MessagePassingCodeGenerator((StreamWrapper)w,tr).visitAppropriate(jl());

		}
	};

	public JL delNodeImpl() {
//		return new X10Del_c();
		return new TD();
	}

	protected JL delIdImpl() {
		return new TD();
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
	 * For each constructor declaration, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor.
	 */
	public JL delConstructorDeclImpl() {
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
				/*
				if (t1.isNull() && t2.isNumeric())
					return c.type(ts.createNullableType(t2.position(), (X10NamedType) t2));
				if (t1.isNumeric() && t2.isNull())
					return c.type(ts.createNullableType(t1.position(), (X10NamedType) t1));
					*/
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
	 * For each import declaration, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor.
	 */
	public JL delImportImpl() {
		return new TD();
	}

	/**
	 * For each formal parameter, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor.
	 */
	public JL delFormalImpl() {
		return new TD();
	}

	/**
	 * For each local declaration, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor.
	 */
	public JL delLocalDeclImpl() {
		return new TD();
	}

	/**
	 * For each async, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor and overrides enterScope.
	 */
	public JL delAsyncImpl() {
		return new TD() {
			public Context enterScope(Context c) {
				X10CPPContext_c context = (X10CPPContext_c) super.enterScope(c);
				context.setinClosure(true);
				return context;
			}
		};
	}

	/**
	 * For each ateach loop, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor.
	 */
	public JL delAtEachImpl() {
		return new TD(){
			public Context enterScope(Context c) {
				X10CPPContext_c context = (X10CPPContext_c) super.enterScope(c);
				context.setinClosure(true);
				return context;
			}
		};
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
	 * to the X10PrettyPrinterVisitor and overrides enterScope.
	 */
	public JL delForLoopImpl() {
		return new TD() {
			public Context enterScope(Context c) {
				X10CPPContext_c context = (X10CPPContext_c) super.enterScope(c);
				return context;
			}
		};
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
	 * to the X10PrettyPrinterVisitor and overrides enterScope.
	 */
	public JL delClosureImpl() {
		return new TD() {
			public Context enterScope(Context c) {
				X10CPPContext_c context = (X10CPPContext_c) super.enterScope(c);
				context.setinClosure(true);
				return context;
			}
		};
	}

	/**
	 * For each catch block, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor and overrides enterScope.
	 */
	protected JL delCatchImpl() {
		return new TD() {
			public Context enterScope(Context c) {
				X10CPPContext_c context = (X10CPPContext_c) super.enterScope(c);
				return context;
			}
		};
	}

	/**
	 * For each for loop, add the delegate that redirects translate
	 * to the X10PrettyPrinterVisitor and overrides enterChildScope.
	 */
	protected JL delForImpl() {
		return new TD() {
			private boolean saveMain = false;
			public Context enterChildScope(Node child, Context c) {
				X10CPPContext_c context = (X10CPPContext_c) super.enterChildScope(child, c);
				if (child instanceof ForInit) {
					if (!saveMain)
						saveMain = context.isMainMethod();
					context.resetMainMethod();  // FIXME: this will be a problem if SPMD vars or Global vars are used
				} else
				if (child instanceof Block && saveMain) {
					saveMain = false;
					// FIXME: [IP] the next line is needed because otherwise the above
					// leaves the outer context in the non-main state
					((X10CPPContext_c) c).setMainMethod();
					context.setMainMethod();
				}
				return context;
			}
		};
	}
}

