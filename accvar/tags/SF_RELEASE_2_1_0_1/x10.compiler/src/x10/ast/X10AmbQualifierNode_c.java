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

package x10.ast;

import polyglot.ast.AmbQualifierNode_c;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.Node_c;
import polyglot.ast.Prefix;
import polyglot.ast.QualifierNode;
import polyglot.frontend.SetResolverGoal;
import polyglot.types.LazyRef;
import polyglot.types.Qualifier;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.CodeWriter;
import polyglot.util.ErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.ExceptionChecker;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;
import x10.visit.X10TypeChecker;

/**
 * An <code>X10AmbQualifierNode</code> is an ambiguous AST node composed of
 * dot-separated list of identifiers that must resolve to a type qualifier.
 */
public class X10AmbQualifierNode_c extends AmbQualifierNode_c implements X10AmbQualifierNode {
	public X10AmbQualifierNode_c(Position pos, Prefix qual, Id name) {
		super(pos, qual, name);
		assert (name != null); // qual may be null
	}

	public void setResolver(Node parent, final TypeCheckPreparer v) {
		final LazyRef<Qualifier> r = (LazyRef<Qualifier>) qualifierRef();
		TypeChecker tc = new X10TypeChecker(v.job(), v.typeSystem(), v.nodeFactory(), v.getMemo());
		tc = (TypeChecker) tc.context(v.context().freeze());
		r.setResolver(new TypeCheckFragmentGoal<Qualifier>(parent, this, tc, r, false) {
		    private static final long serialVersionUID = -1753967384169577700L;
		    @Override
		    public boolean runTask() {
		        boolean result = super.runTask();
		        if (result) {
		            if (r().getCached() instanceof UnknownType) {
		                v.errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR, "Could not compute type.", n.position());
		                return false;
		            }
		        }
		        return result;
		    }
		});
	}
}
