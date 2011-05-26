/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
 */

package polyglot.ast;

import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;

/**
 * A <code>TypeNode</code> represents the syntactic representation of a
 * <code>Type</code> within the abstract syntax tree.
 */
public class ArrayTypeNode_c extends TypeNode_c implements ArrayTypeNode
{
    protected TypeNode base;

    public ArrayTypeNode_c(Position pos, TypeNode base) {
	super(pos);
	assert(base != null);
	this.base = base;
    }

    public TypeNode base() {
        return base;
    }

    public ArrayTypeNode base(TypeNode base) {
        ArrayTypeNode_c n = (ArrayTypeNode_c) copy();
	n.base = base;
	return n;
    }

    protected ArrayTypeNode_c reconstruct(TypeNode base) {
        if (base != this.base) {
	    ArrayTypeNode_c n = (ArrayTypeNode_c) copy();
	    n.base = base;
	    return n;
	}

	return this;
    }
    
    public Node visitChildren(NodeVisitor v) {
        TypeNode base = (TypeNode) visitChild(this.base, v);
	return reconstruct(base);
    }

    public Node buildTypes(TypeBuilder tb) {
    	return typeRef(Types.<Type>ref(tb.typeSystem().arrayOf(position(), base.typeRef())));
    }

    public Node disambiguate(ContextVisitor ar) {
	TypeSystem ts = ar.typeSystem();
	NodeFactory nf = ar.nodeFactory();
        return nf.CanonicalTypeNode(position(),
		                    ts.arrayOf(position(), base.typeRef()));
    }

    public Node typeCheck(ContextVisitor tc) {
	throw new InternalCompilerError(position(),
	    "Cannot type check ambiguous node " + this + ".");
    }

    public Node exceptionCheck(ExceptionChecker ec) {
	throw new InternalCompilerError(position(),
	    "Cannot exception check ambiguous node " + this + ".");
    }

    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        print(base, w, tr);
        w.write("[]");
    }

    public String toString() {
        return base.toString() + "[]";
    }
}
