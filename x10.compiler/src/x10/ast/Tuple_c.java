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

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Node;
import polyglot.ast.Precedence;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.ast.NodeFactory;
import polyglot.types.FieldInstance;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.types.constraints.ConstraintManager;
import x10.constraint.XVar;
import x10.types.ConstrainedType;
import x10.types.X10ClassType;
import x10.types.checker.Converter;
import polyglot.types.TypeSystem;
import x10.types.constants.ConstantValue;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint;
import x10.errors.Errors;

/** 
 * An immutable representation of the list of elements in an X10 rail constructor
 * new Rail[T]{ e1,..., ek}. 
 * 
 * There is no surface syntax for a Tuple_c. Rather it is access
 */
public class Tuple_c extends Expr_c implements Tuple {
    
    protected List<Expr> elements;
    protected TypeNode indexType;

    public Tuple_c(Position pos, List<Expr> elements, TypeNode indexType) {
	super(pos);
        this.indexType = indexType;
        assert(elements != null);
	this.elements = TypedList.copyAndCheck(elements, Expr.class, true);
    }
    
    @Override
    public boolean isConstant() {
        return false;
    }
    
    @Override
    public ConstantValue constantValue() {
    	return null;
    }
    
    @Override
    public Precedence precedence() {
        return Precedence.LITERAL;
    }

    /** Get the elements of the initializer. */
    public List<Expr> arguments() {
	return this.elements;
    }

    /** Set the elements of the initializer. */
    public Tuple arguments(List<Expr> elements) {
	Tuple_c n = (Tuple_c) copy();
	n.elements = TypedList.copyAndCheck(elements, Expr.class, true);
	return n;
    }

    /** Reconstruct the initializer. */
    protected Tuple_c reconstruct(TypeNode tn, List<Expr> elements) {
	if (tn!=indexType || ! CollectionUtil.allEqual(elements, this.elements)) {
	    Tuple_c n = (Tuple_c) copy();
        n.indexType = tn;
	    n.elements = TypedList.copyAndCheck(elements, Expr.class, true);
	    return n;
	}

	return this;
    }

    /** Visit the children of the initializer. */
    public Node visitChildren(NodeVisitor v) {
    TypeNode tn = null;
    if (indexType!=null) tn = (TypeNode) visitChild( this.indexType, v );
	List<Expr> elements = visitList(this.elements, v);
	return reconstruct(tn,elements);
    }

	public TypeNode indexType() {
		return indexType;
	}
    public Term firstChild() {
        return indexType!=null ? indexType : listChild(elements, null);
    }

    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        if (indexType!=null) {
            if (elements.size()>0)
                v.visitCFG(indexType, elements.get(0), ENTRY);
            else
                v.visitCFG(indexType, this, EXIT);
        }
        v.visitCFGList(elements, this, EXIT);
        return succs;
    }

	/** Type check the initializer. */
	public Node typeCheck(ContextVisitor tc) {
	    TypeSystem ts = (TypeSystem) tc.typeSystem();

        Type type;
        Expr me = this;
        if (indexType == null) {
            type = null;

            for (Expr e : elements) {
                Type eType = e.type(); // Types.baseType(e.type());
                if (type == null) {
                    type = eType;
                }
                else {
                    try {
                        type = ts.leastCommonAncestor(type, eType, tc.context());
                    } catch (SemanticException z) {
                        Errors.issue(tc.job(), z, this);
                        type = ts.Any();
                    }
                }
            }

            if (type == null) {
                type = ts.Any(); // should be bottom type, not top
            }
        } else {
            type = indexType.type();
	        List<Expr> vals = arguments();
            ArrayList<Expr> newChildren = new ArrayList<Expr>();
	        for (Expr e : vals) {
                Expr newE = Converter.attemptCoercion(tc, e, type);
                if (newE==null) {
                    newE = e;
                    Errors.issue(tc.job(),
                        new Errors.RailLiteralTypeMismatch(e, type));
                }
                newChildren.add(newE);
	        }
            me = this.reconstruct(indexType,newChildren);
        }
        ConstrainedType resultType = Types.toConstrainedType(Types.makeRailOf(type, elements.size(), position()));
        resultType = resultType.addNonNull();

	    if (! Types.consistent(resultType))
	        Errors.issue(tc.job(), new Errors.InconsistentType(resultType, position()));
	    return me.type(resultType);
	}

	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();
        if (indexType!=null) {
            sb.append("new Rail[").append(indexType).append("]");
        }
	    sb.append("[");
	    for (Iterator<Expr> i = elements.iterator(); i.hasNext(); ) {
		Expr e = i.next();
		sb.append(e.toString());
		if (i.hasNext()) {
		    sb.append(", ");
		}
	    }
	    sb.append("]");
	    return sb.toString();
	}
	
	
	
	@Override
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        if (indexType!=null) {
            w.write("new Rail[");
	    	printBlock(indexType, w, tr);
	    	w.write("]");
        }
	    w.write("{");

	    for (Iterator<Expr> i = elements.iterator(); i.hasNext(); ) {
		Expr e = i.next();

		print(e, w, tr);

		if (i.hasNext()) {
		    w.write(",");
		    w.allowBreak(0, " ");
		}
	    }

	    w.write("}");
	}
	
	@Override
	public void translate(CodeWriter w, Translator tr) {
	    super.prettyPrint(w, tr);
	}
}
