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

import java.util.Iterator;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Node;
import polyglot.ast.Precedence;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.types.FieldInstance;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.types.X10ClassType;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint;
import x10.errors.Errors;

/** 
 * An immutable representation of the list of elements in an X10 array constructor
 * new Array[T]{ e1,..., ek}. 
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
        for (Expr e : elements) {
            if (!e.isConstant())
                return false;
        }
        return true;
    }
    
    @Override
    public Object constantValue() {
        Object[] a = new Object[elements.size()];
        int i = 0;
        for (Expr e : elements) {
            if (!e.isConstant())
                return null;
            a[i++] = e.constantValue();
        }
        return a;
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

    public Type childExpectedType(Expr child, AscriptionVisitor av) {
	if (elements.isEmpty()) {
            return child.type();
        }

        Type t = av.toType();
        
        X10TypeSystem ts = (X10TypeSystem) av.typeSystem();
        
        if (! ts.isArray(t)) {
            return child.type();
            // Don't complain when we have implicit coercions!
//            throw new InternalCompilerError("Type of rail constructor must be a " + ts.ValRail() + ", not " + t + ".", position());
        }
        
        Type base = X10TypeMixin.getParameterType(t, 0);

        for (Expr e : elements) {
            if (e == child) {
                if (ts.numericConversionValid(base, child.type(), e.constantValue(), av.context())) {
                    return child.type();
                }
                else {
                    return base;
                }
            }
        }

        return child.type();
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
	public Node typeCheck(ContextVisitor tc) throws SemanticException {
	    X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();

	    Type type = null;

	    for (Expr e : elements) {
	    	Type eType = X10TypeMixin.baseType(e.type());
		if (type == null) {
		    type = eType;
		}
		else {
		    type = ts.leastCommonAncestor(type, eType, tc.context());
		}
	    }

	    if (type == null) {
	        type = ts.Any(); // should be bottom type, not top
	    }

	    Type resultType = X10TypeMixin.makeArrayRailOf(type, elements.size(), position());

        if (indexType!=null) {
            Type iType = indexType.type();
	        List<Expr> vals = arguments();
	        for (Expr e : vals) {
	    	  Type t = e.type();
	    	  if (! ts.isSubtype(t, iType, tc.context()))
	    		  Errors.issue(tc.job(),
	    			      new Errors.ArrayLiteralTypeMismatch(e, iType));
	        }
		    resultType = X10TypeMixin.makeArrayRailOf(iType, arguments().size(), position());
        }
	    return type(resultType);
	}

	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();
        if (indexType!=null) {
            sb.append("new Array[").append(indexType).append("]");
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
            w.write("new Array[");
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
