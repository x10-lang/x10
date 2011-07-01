/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
 */

package polyglot.ast;

import java.util.*;

import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;

/**
 * A <code>ConstructorCall_c</code> represents a direct call to a constructor.
 * For instance, <code>super(...)</code> or <code>this(...)</code>.
 */
public abstract class ConstructorCall_c extends Stmt_c implements ConstructorCall
{
    protected Kind kind;
    protected Expr qualifier;
    protected List<Expr> arguments;
    protected ConstructorInstance ci;
    protected Expr target;

    public ConstructorCall_c(Position pos, Kind kind, Expr qualifier, List<Expr> arguments) {
	super(pos);
	assert(kind != null && arguments != null); // qualifier may be null
	this.kind = kind;
	this.qualifier = qualifier;
	this.arguments = TypedList.copyAndCheck(arguments, Expr.class, true);
    }
    
    /** Get the qualifier of the constructor call. */
    public Expr qualifier() {
	return this.qualifier;
    }

    /** Set the qualifier of the constructor call. */
    public ConstructorCall qualifier(Expr qualifier) {
	ConstructorCall_c n = (ConstructorCall_c) copy();
	n.qualifier = qualifier;
	return n;
    }

    /** Get the kind of the constructor call. */
    public Kind kind() {
	return this.kind;
    }

    /** Set the kind of the constructor call. */
    public ConstructorCall kind(Kind kind) {
	ConstructorCall_c n = (ConstructorCall_c) copy();
	n.kind = kind;
	return n;
    }

    /** Get the actual arguments of the constructor call. */
    public List<Expr> arguments() {
	return Collections.unmodifiableList(this.arguments);
    }

    /** Set the actual arguments of the constructor call. */
    public ConstructorCall arguments(List<Expr> arguments) {
	ConstructorCall_c n = (ConstructorCall_c) copy();
	n.arguments = TypedList.copyAndCheck(arguments, Expr.class, true);
	return n;
    }

    public ConstructorInstance procedureInstance() {
	return constructorInstance();
    }

    /** Get the constructor we are calling. */
    public ConstructorInstance constructorInstance() {
        return ci;
    }

    public ConstructorCall procedureInstance(ProcedureInstance<? extends ProcedureDef> pi) {
        return constructorInstance((ConstructorInstance) pi);
    }

    /** Set the constructor we are calling. */
    public ConstructorCall constructorInstance(ConstructorInstance ci) {
        if (ci == this.ci) return this;
	ConstructorCall_c n = (ConstructorCall_c) copy();
	n.ci = ci;
	return n;
    }

    /** Reconstruct the constructor call. */
    protected ConstructorCall_c reconstruct(Expr qualifier, Expr target, List<Expr> arguments) {
	if (qualifier != this.qualifier || target != this.target || ! CollectionUtil.allEqual(arguments, this.arguments)) {
	    ConstructorCall_c n = (ConstructorCall_c) copy();
	    n.qualifier = qualifier;
	    n.target = target;
	    n.arguments = TypedList.copyAndCheck(arguments, Expr.class, true);
	    return n;
	}

	return this;
    }

    /** Visit the children of the call. */
    public abstract Node visitChildren(NodeVisitor v);

    public abstract Node buildTypes(TypeBuilder tb);

    /** Type check the call. */
    public abstract Node typeCheck(ContextVisitor tc);

    public abstract String toString();

    /** Write the call to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	if (qualifier != null) {
	    print(qualifier, w, tr);
	    w.write(".");
	} 

	w.write(kind + "(");

	w.begin(0);

	for (Iterator<Expr> i = arguments.iterator(); i.hasNext(); ) {
	    Expr e = i.next();
	    print(e, w, tr);

	    if (i.hasNext()) {
		w.write(",");
		w.allowBreak(0);
	    }
	}

	w.end();

	w.write(");");
    }

    public Term firstChild() {
        if (qualifier != null) {
            return qualifier;
        } else {
            return listChild(arguments, null);
        }
    }

    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        if (qualifier != null) {
            assert (target == null);
            if (!arguments.isEmpty()) {
                v.visitCFG(qualifier, listChild(arguments, null), ENTRY);
                v.visitCFGList(arguments, this, EXIT);
            } else {
                v.visitCFG(qualifier, this, EXIT);
            }
        } else {
            if (target != null) {
                if (!arguments.isEmpty()) {
                    v.visitCFG(target, listChild(arguments, null), ENTRY);
                    v.visitCFGList(arguments, this, EXIT);
                } else {
                    v.visitCFG(target, this, EXIT);
                }
            } else {
                if (!arguments.isEmpty()) {
                    v.visitCFGList(arguments, this, EXIT);
                }
            }
        }

        return succs;
    }

    /* (non-Javadoc)
     * @see polyglot.ast.ConstructorCall#target()
     */
    public Expr target() {
        return target;
    }

    /* (non-Javadoc)
     * @see polyglot.ast.ConstructorCall#target(polyglot.ast.Expr)
     */
    public ConstructorCall target(Expr target) {
        assert this.qualifier == null;
        if (target != this.target) {
            ConstructorCall_c n = (ConstructorCall_c) copy();
            n.target = target;
            return n;
        }
        return this;
    }
}
