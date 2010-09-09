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
package x10cpp.ast;

import polyglot.ast.JL;
import polyglot.ast.Node;
import polyglot.types.Context;
import polyglot.util.CodeWriter;
import polyglot.visit.Translator;
import x10.ast.X10DelFactory_c;
import x10.extension.X10Del_c;
import x10.extension.X10Ext;
import x10.visit.X10DelegatingVisitor;
import x10.util.StreamWrapper;
import x10cpp.types.X10CPPContext_c;
import x10cpp.visit.MessagePassingCodeGenerator;

/**
 * @author Christian Grothoff
 * @author Igor Peshansky
 * @author Dave Cunningham
 */
public class X10CPPDelFactory_c extends X10DelFactory_c {

	/**
	 * Used to allow subclasses to override the code generator.
	 */
    protected X10DelegatingVisitor makeCodeGenerator(CodeWriter w, Translator tr) {
        return new MessagePassingCodeGenerator((StreamWrapper)w,tr);
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
					w.write(ext.comment());
			}
			makeCodeGenerator(w, tr).visitAppropriate(jl());

		}
	};

	public JL delNodeImpl() {
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
				context.setInClosure();
				context.closureOuter = (X10CPPContext_c) c;
				return context;
			}
		};
	}

}

