/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
 */

package polyglot.ast;

import java.util.List;

import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import x10.errors.Errors;

/**
 * An <code>ArrayAccess</code> is an immutable representation of an
 * access of an array member.
 */
public class ArrayAccess_c extends Expr_c implements ArrayAccess
{
    protected Expr array;
    protected Expr index;

    public ArrayAccess_c(Position pos, Expr array, Expr index) {
	super(pos);
	assert(array != null && index != null);
	this.array = array;
	this.index = index;
    }

    /** Get the precedence of the expression. */
    public Precedence precedence() { 
	return Precedence.LITERAL;
    }

    /** Get the array of the expression. */
    public Expr array() {
	return this.array;
    }

    /** Set the array of the expression. */
    public ArrayAccess array(Expr array) {
	ArrayAccess_c n = (ArrayAccess_c) copy();
	n.array = array;
	return n;
    }

    /** Get the index of the expression. */
    public Expr index() {
	return this.index;
    }

    /** Set the index of the expression. */
    public ArrayAccess index(Expr index) {
	ArrayAccess_c n = (ArrayAccess_c) copy();
	n.index = index;
	return n;
    }

    /** Return the access flags of the variable. */
    public Flags flags() {
        return Flags.NONE;
    }

    /** Reconstruct the expression. */
    protected ArrayAccess_c reconstruct(Expr array, Expr index) {
	if (array != this.array || index != this.index) {
	    ArrayAccess_c n = (ArrayAccess_c) copy();
	    n.array = array;
	    n.index = index;
	    return n;
	}

	return this;
    }

    /** Visit the children of the expression. */
    public Node visitChildren(NodeVisitor v) {
	Expr array = (Expr) visitChild(this.array, v);
	Expr index = (Expr) visitChild(this.index, v);
	return reconstruct(array, index);
    }

    /** Type check the expression. */
    public Node typeCheck(ContextVisitor tc) {
        TypeSystem ts = tc.typeSystem();

	Type arrayType = array.type();
	if (! arrayType.isArray()) {
	    Errors.issue(tc.job(),
	            new SemanticException("Subscript can only follow an array type.", position()));
	    // FIXME: HACK! Fake the type
	    arrayType = new JavaArrayType_c(ts, position(), Types.ref(ts.Int()));
	}

	if (! ts.isImplicitCastValid(index.type(), ts.Int(), tc.context())) {
	    Errors.issue(tc.job(),
	            new SemanticException("Array subscript must be an integer.", position()));
	}

	return type(arrayType.toArray().base());
    }

    public String toString() {
	return array + "[" + index + "]";
    }

    /** Write the expression to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	printSubExpr(array, w, tr);
	w.write ("[");
	printBlock(index, w, tr);
	w.write ("]");
    }

    public Term firstChild() {
        return array;
    }

    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        v.visitCFG(array, index, ENTRY);
        v.visitCFG(index, this, EXIT);
        return succs;
    }

    public List<Type> throwTypes(TypeSystem ts) {
        return CollectionUtil.<Type>list(ts.OutOfBoundsException(),
                                         ts.NullPointerException());
    }
}
