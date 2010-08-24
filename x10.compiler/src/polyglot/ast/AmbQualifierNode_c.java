/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
 */

package polyglot.ast;


import polyglot.frontend.*;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;

/**
 * An <code>AmbQualifierNode</code> is an ambiguous AST node composed of
 * dot-separated list of identifiers that must resolve to a type qualifier.
 */
public class AmbQualifierNode_c extends Node_c implements AmbQualifierNode
{
    protected LazyRef<Qualifier> qualifier;
    protected Prefix qual;
    protected Id name;

    public AmbQualifierNode_c(Position pos, Prefix qual, Id name) {
	super(pos);
	assert(name != null); // qual may be null

	this.qual = qual;
	this.name = name;
    }
    
    public LazyRef<? extends Qualifier> qualifierRef() {
	return this.qualifier;
    }
    
    public Id name() {
        return this.name;
    }
    
    public AmbQualifierNode name(Id name) {
        AmbQualifierNode_c n = (AmbQualifierNode_c) copy();
        n.name = name;
        return n;
    }

    public Prefix prefix() {
	return this.qual;
    }

    public AmbQualifierNode qual(Prefix qual) {
	AmbQualifierNode_c n = (AmbQualifierNode_c) copy();
	n.qual = qual;
	return n;
    }

    public AmbQualifierNode qualifier(LazyRef<Qualifier> qualifier) {
	AmbQualifierNode_c n = (AmbQualifierNode_c) copy();
	n.qualifier = qualifier;
	return n;
    }

    protected AmbQualifierNode_c reconstruct(Prefix qual, Id name) {
	if (qual != this.qual || name != this.name) {
	    AmbQualifierNode_c n = (AmbQualifierNode_c) copy();
	    n.qual = qual;
	    n.name = name;
	    return n;
	}

	return this;
    }

    public Node visitChildren(NodeVisitor v) {
        Id name = (Id) visitChild(this.name, v);
        Prefix qual = (Prefix) visitChild(this.qual, v);
	return reconstruct(qual, name);
    }

    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        TypeSystem ts = tb.typeSystem();
        LazyRef<Qualifier> sym = Types.<Qualifier>lazyRef(ts.unknownQualifier(position()), new SetResolverGoal(tb.job()));
        return qualifier(sym);
    }
    
	public Qualifier qualifier() {
		return qualifierRef().get();
	}
	
	public void setResolver(Node parent, final TypeCheckPreparer v) {
		final LazyRef<Qualifier> r = (LazyRef<Qualifier>) qualifierRef();
		TypeChecker tc = new TypeChecker(v.job(), v.typeSystem(), v.nodeFactory(), v.getMemo());
		tc = (TypeChecker) tc.context(v.context().freeze());
		r.setResolver(new TypeCheckTypeGoal(parent, this, tc, r, false));
	}

	public Node disambiguate(ContextVisitor ar) throws SemanticException {
		SemanticException ex;
		
		try {
			Node n = ar.nodeFactory().disamb().disambiguate(this, ar, position(), qual, name);

			if (n instanceof QualifierNode) {
				QualifierNode qn = (QualifierNode) n;
				Qualifier q = qn.qualifierRef().get();
				qualifier.update(q);
				return n;
			}

			ex = new SemanticException("Could not find type or package \"" +
					(qual == null ? name.toString() : qual.toString() + "." + name.toString()) +
					"\".", position());
		}
		catch (SemanticException e) {
			ex = e;
		}

		// Mark the type as an error, so we don't try looking it up again.
		LazyRef<Qualifier> sym = (LazyRef<Qualifier>) qualifier;
		sym.update(ar.typeSystem().unknownQualifier(position()));

		throw ex;
	}

	public Node exceptionCheck(ExceptionChecker ec) throws SemanticException {
	throw new InternalCompilerError(position(),
	    "Cannot exception check ambiguous node " + this + ".");
    } 

    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	if (qual != null) {
            print(qual, w, tr);
            w.write(".");
	    w.allowBreak(2, 3, "", 0);
        }
             
        tr.print(this, name, w);
    }

    public String toString() {
	return (qual == null
		? name.toString()
		: qual.toString() + "." + name.toString()) + "{amb}";
    }
}
