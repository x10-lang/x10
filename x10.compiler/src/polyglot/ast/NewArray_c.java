/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.ast;

import java.util.*;

import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import x10.errors.Errors;

/**
 * A <code>NewArray</code> represents a new array expression such as <code>new
 * File[8][] { null }</code>.  It consists of an element type (e.g.,
 * <code>File</code>), a list of dimension expressions (e.g., 8), 0 or more
 * additional dimensions (e.g., 1 for []), and an array initializer.  The
 * dimensions of the array initializer must equal the number of additional "[]"
 * dimensions.
 */
public class NewArray_c extends Expr_c implements NewArray
{
    protected TypeNode baseType;
    protected List<Expr> dims;
    protected int addDims;
    protected ArrayInit init;

    public NewArray_c(Position pos, TypeNode baseType, List<Expr> dims, int addDims, ArrayInit init) {
	super(pos);
	assert(baseType != null && dims != null); // init may be null
	assert(addDims >= 0);
	assert(! dims.isEmpty() || init != null); // dims may be empty only if there is an initializer
	assert(addDims > 0 || init == null); // init may be non-null only if addDims > 0
	assert(dims.size() + addDims > 0); // must allocate something
	
	this.baseType = baseType;
	this.dims = TypedList.copyAndCheck(dims, Expr.class, true);
	this.addDims = addDims;
	this.init = init;
    }

    /** Get the base type node of the expression. */
    public TypeNode baseType() {
	return this.baseType;
    }

    /** Set the base type node of the expression. */
    public NewArray baseType(TypeNode baseType) {
	NewArray_c n = (NewArray_c) copy();
	n.baseType = baseType;
	return n;
    }

    /** Get the dimension expressions of the expression. */
    public List<Expr> dims() {
	return Collections.unmodifiableList(this.dims);
    }

    /** Set the dimension expressions of the expression. */
    public NewArray dims(List<Expr> dims) {
	NewArray_c n = (NewArray_c) copy();
	n.dims = TypedList.copyAndCheck(dims, Expr.class, true);
	return n;
    }

    /** Get the number of dimensions of the expression. */
    public int numDims() {
        return dims.size() + addDims;
    }

    /** Get the number of additional dimensions of the expression. */
    public int additionalDims() {
	return this.addDims;
    }

    /** Set the number of additional dimensions of the expression. */
    public NewArray additionalDims(int addDims) {
	NewArray_c n = (NewArray_c) copy();
	n.addDims = addDims;
	return n;
    }

    /** Get the initializer of the expression. */
    public ArrayInit init() {
	return this.init;
    }

    /** Set the initializer of the expression. */
    public NewArray init(ArrayInit init) {
	NewArray_c n = (NewArray_c) copy();
	n.init = init;
	return n;
    }

    /** Reconstruct the expression. */
    protected NewArray_c reconstruct(TypeNode baseType, List<Expr> dims, ArrayInit init) {
	if (baseType != this.baseType || ! CollectionUtil.allEqual(dims, this.dims) || init != this.init) {
	    NewArray_c n = (NewArray_c) copy();
	    n.baseType = baseType;
	    n.dims = TypedList.copyAndCheck(dims, Expr.class, true);
	    n.init = init;
	    return n;
	}

	return this;
    }

    /** Visit the children of the expression. */
    public Node visitChildren(NodeVisitor v) {
	TypeNode baseType = (TypeNode) visitChild(this.baseType, v);
	List<Expr> dims = visitList(this.dims, v);
	ArrayInit init = (ArrayInit) visitChild(this.init, v);
	return reconstruct(baseType, dims, init);
    }

    /** Type check the expression. */
    public Node typeCheck(ContextVisitor tc) {
        TypeSystem ts = tc.typeSystem();

        for (Expr expr : dims) {
            if (! ts.isImplicitCastValid(expr.type(), ts.Int(), tc.context())) {
                Errors.issue(tc.job(),
                        new SemanticException("Array dimension must be an integer.", expr.position()),
                        this);
            }
        }

        Type type = ts.arrayOf(baseType.type(), dims.size() + addDims);

	if (init != null) {
            init.typeCheckElements(tc, type);
	}

	return type(type);
    }

    public String toString() {
	return "new " + baseType + "[...]";
    }

    /** Write the expression to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	w.write("new ");
	print(baseType, w, tr);

	for (Expr e : dims) {
	    w.write("[");
	    printBlock(e, w, tr);
	    w.write("]");
	}

	for (int i = 0; i < addDims; i++) {
	    w.write("[]");
	}

	if (init != null) {
	    w.write(" ");
	    print(init, w, tr);
	}
    }

    public Term firstChild() {
        return baseType;
    }

    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        if (init != null) {
            v.visitCFG(baseType, listChild(dims, init), ENTRY);
            v.visitCFGList(dims, init, ENTRY);
            v.visitCFG(init, this, EXIT);
        } else {
            v.visitCFG(baseType, listChild(dims, null), ENTRY);
            v.visitCFGList(dims, this, EXIT);
        }
        
        return succs;
    }
    
    public List<Type> throwTypes(TypeSystem ts) {
        if (dims != null && !dims.isEmpty()) {
            // if dimension expressions are given, then
            // a NegativeArraySizeException may be thrown.
            try {
                return CollectionUtil.list(ts.forName(QName.make("java.lang.NegativeArraySizeException")));
            }
            catch (SemanticException e) {
                throw new InternalCompilerError("Cannot find class java.lang.NegativeArraySizeException", e);
            }
        }
        return Collections.<Type>emptyList();
    }
    

}
