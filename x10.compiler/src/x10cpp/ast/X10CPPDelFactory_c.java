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
	 * For each closure, add the delegate that redirects translate
	 * to the code generator and overrides enterScope.
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

