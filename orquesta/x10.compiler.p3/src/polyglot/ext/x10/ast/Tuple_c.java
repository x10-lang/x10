/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Jan 19, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.Iterator;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
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

/** 
 * An immutable representation of the X10 rail constructor [e1, ..., ek]. 
 * This behaves like a Java array initializer except that the type is NativeValRail[T], not T[].
 */
public class Tuple_c extends Expr_c implements Tuple {
    
    protected List<Expr> elements;

    public Tuple_c(Position pos, List<Expr> elements) {
	super(pos);
	assert(elements != null);
	this.elements = TypedList.copyAndCheck(elements, Expr.class, true);
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
    protected Tuple_c reconstruct(List<Expr> elements) {
	if (! CollectionUtil.allEqual(elements, this.elements)) {
	    Tuple_c n = (Tuple_c) copy();
	    n.elements = TypedList.copyAndCheck(elements, Expr.class, true);
	    return n;
	}

	return this;
    }

    /** Visit the children of the initializer. */
    public Node visitChildren(NodeVisitor v) {
	List<Expr> elements = visitList(this.elements, v);
	return reconstruct(elements);
    }

    public Type childExpectedType(Expr child, AscriptionVisitor av) {
	if (elements.isEmpty()) {
            return child.type();
        }

        Type t = av.toType();
        
        X10TypeSystem ts = (X10TypeSystem) av.typeSystem();
        
        if (! ts.isValRail(t)) {
            return child.type();
            // Don't complain when we have implicit coercions!
//            throw new InternalCompilerError("Type of rail constructor must be a " + ts.ValRail() + ", not " + t + ".", position());
        }
        
        Type base = X10TypeMixin.getParameterType(t, 0);

        for (Expr e : elements) {
            if (e == child) {
                if (ts.numericConversionValid(base, e.constantValue())) {
                    return child.type();
                }
                else {
                    return base;
                }
            }
        }

        return child.type();
    }

    public Term firstChild() {
        return listChild(elements, null);
    }

    public List<Term> acceptCFG(CFGBuilder v, List<Term> succs) {
        v.visitCFGList(elements, this, EXIT);
        return succs;
    }

	/** Type check the initializer. */
	public Node typeCheck(ContextVisitor tc) throws SemanticException {
	    X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();

	    Type type = null;

	    for (Expr e : elements) {
		if (type == null) {
		    type = X10TypeMixin.baseType(e.type());
		}
		else {
		    type = ts.leastCommonAncestor(type, X10TypeMixin.baseType(e.type()));
		}
	    }

	    if (type == null) {
		return type(ts.Null());
	    }
	    else {
		Type r = ts.ValRail();
		Type t = X10TypeMixin.instantiate(r, type);
		return type(t);
	    }
	}

	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();
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
	    w.write("[");

	    for (Iterator<Expr> i = elements.iterator(); i.hasNext(); ) {
		Expr e = i.next();

		print(e, w, tr);

		if (i.hasNext()) {
		    w.write(",");
		    w.allowBreak(0, " ");
		}
	    }

	    w.write("]");
	}
	
	@Override
	public void translate(CodeWriter w, Translator tr) {
	    super.prettyPrint(w, tr);
	}
}
