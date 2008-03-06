/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 26, 2004
 */
package polyglot.ext.x10.ast;

import java.util.Collections;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ast.TypeNode_c;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.BindingConstraintSystem;
import polyglot.main.Report;
import polyglot.types.Context;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

/**
 * A NullableNode is an TypeNode that has been marked with a nullable
 * qualifier.  Note that the base TypeNode may be ambiguous and may need to
 * be resolved.
 * TODO: Find a better way of dealing with the construction of the Nullable
 * type. In the code below, this might be done twice. That doesnt seem right.
 *
 * @author vj
 */
public class NullableNode_c extends TypeNode_c implements NullableNode {
	
	// Recall the field this.type is defined at the supertype.
	// The Typenode representing the argument type X.
	protected TypeNode base;
	
	public NullableNode_c(Position pos, TypeNode base) {
		super(pos);
		this.base = base;
	}
	
	public TypeNode base() {
		return this.base;
	}
	
	public NullableNode_c base(TypeNode base) {
		NullableNode_c n = (NullableNode_c) copy();
		n.base = base;
		return n;
	}
	
	protected NullableNode_c reconstruct(TypeNode base) {
		return (base != this.base) ? base(base) : this;
	}
	
	public Node buildTypes(TypeBuilder tb) throws SemanticException {
	    X10TypeSystem ts = (X10TypeSystem) tb.typeSystem();
	    return typeRef(Types.<Type>ref(ts.createNullableType(position(), (Ref<? extends X10NamedType>) base.typeRef())));
	}

	public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
	    X10TypeSystem ts = (X10TypeSystem) ar.typeSystem();
	    NodeFactory nf = ar.nodeFactory();
	    if (base.type() instanceof UnknownType) {
	        return nf.CanonicalTypeNode(position(), base.type());
	    }
	    return nf.CanonicalTypeNode(position(),
	                                ts.createNullableType(position(), (Ref<? extends X10NamedType>) base.typeRef()));
	}
	
	public Node visitChildren(NodeVisitor v) {
		TypeNode base = (TypeNode) visitChild(this.base, v);
		NullableNode_c result = (NullableNode_c) reconstruct(base);
		return result;
	}
	
	public String toString() {
		return "nullable<" + base.toString() + ">";
	}
	
	/**
	 * Write out Java code for this node.
	 * Hmmm.. TODO: this will need to output the BoxedType for the primitive types,
	 * and just the reference type (unchanged) otherwise.
	 */
	public void prettyPrint(CodeWriter w, PrettyPrinter pp) {
		w.write("/*nullable*/");
		base.del().prettyPrint(w, pp);
	}
	
	// translate??
	// dump?
}

