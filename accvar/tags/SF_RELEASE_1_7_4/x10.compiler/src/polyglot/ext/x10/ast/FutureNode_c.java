/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 26, 2004
 *
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;


/** A FutureNode is an TypeNode that has been marked with a future
 * qualifier.  Note that the base TypeNode may be ambiguous and may need to be resolved.
 * @author vj
 *
 */
public class FutureNode_c extends X10TypeNode_c implements FutureNode {

	// Recall the field this.type is defined at the supertype.
	// The Typenode representing the argument type X.
	protected TypeNode base;

	public FutureNode_c(Position pos, TypeNode base) {
		super( pos );
		this.base = base;
	}

	public TypeNode base() {
		return this.base;
	}

	public FutureNode_c base( TypeNode base ) {
		FutureNode_c n = (FutureNode_c) copy();
		n.base = base;
		return n;
	}

	protected FutureNode_c reconstruct( TypeNode base ) {
		return (base != this.base) ? base( base ) :  this;
	}

	public Node buildTypes(TypeBuilder tb) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) tb.typeSystem();
		return type(ts.unknownType(position()));
	}

	  public Context enterChildScope(Node child, Context c) {
	        if (child == this.dep) {
	            if (type instanceof X10ParsedClassType)
	            c = ((X10Context) c).pushDepType((X10ParsedClassType) type);
	        }
	        return super.enterChildScope(child, c);
	    }
	public Node visitChildren( NodeVisitor v ) {
		TypeNode base = (TypeNode) visitChild(this.base, v);
		return ((FutureNode_c) super.visitChildren(v)).reconstruct( base );
	}

	public Node disambiguateBase(AmbiguityRemover sc) throws SemanticException {
		TypeNode newType = (TypeNode) base.disambiguate( sc );
		Type baseType =  newType.type();
		assert baseType!=null;
//		 RMF 11/2/2005 - Don't proceed further if the base type hasn't yet been disambiguated...
		if (!baseType.isCanonical())
			return this;
		FutureNode_c result = (FutureNode_c) reconstruct(newType);
		return result.propagateTypeFromBase();
	}
	public TypeNode propagateTypeFromBase() {
		X10NamedType baseType = (X10NamedType) base.type();
		assert baseType !=null;
		X10TypeSystem xts = (X10TypeSystem) baseType.typeSystem();
		X10Type resultType = xts.createFutureType(position(), baseType);
		return type(resultType);
	}

	public Node typeCheckBase( TypeChecker tc) throws SemanticException {
		// Report.report(5,"[FutureNode_c] Type checking |" + this +"|:");
		Node n = base.del().typeCheck( tc );
		// Report.report(5,"[FutureNode_c] ... yields node |" + n +"|.");
		if (! (n instanceof TypeNode))
			throw new SemanticException("Argument to future type-constructor does not type-check" + position());
		
		TypeNode newType = (TypeNode) n;
		X10NamedType baseType = (X10NamedType) newType.type();
		FutureNode_c result = (FutureNode_c) reconstruct(newType);
		return result.propagateTypeFromBase();
	}

	public String toString() {
		if (this.type == null)
			return "unknown";
		return this.type.toString();
	}

	/**
	 * Write out Java code for this node.
	 */
	public void prettyPrint(CodeWriter w, PrettyPrinter ignore) {
		w.write(this.type.toString());
	}

	// translate??
	// dump?
}
