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
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ast.TypeNode_c;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
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


/** A FutureNode is an TypeNode that has been marked with a future
 * qualifier.  Note that the base TypeNode may be ambiguous and may need to be resolved.
 * @author vj
 *
 */
public class FutureNode_c extends TypeNode_c implements FutureNode {

 // Recall the field this.type is defined at the supertype.
    // The Typenode representing the argument type X.
    protected TypeNode base;
    
    public FutureNode_c(Position pos, TypeNode base) {
            super(pos);
            this.base = base;
    }
    
    public TypeNode base() {
            return this.base;
    }
    
    public FutureNode_c base(TypeNode base) {
            FutureNode_c n = (FutureNode_c) copy();
            n.base = base;
            return n;
    }
    
    protected FutureNode_c reconstruct(TypeNode base) {
            return (base != this.base) ? base(base) : this;
    }
    
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        X10TypeSystem ts = (X10TypeSystem) tb.typeSystem();
        return typeRef(Types.<Type>ref(ts.createFutureType(position(), (Ref<? extends X10NamedType>) base.typeRef())));
    }

    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        X10TypeSystem ts = (X10TypeSystem) ar.typeSystem();
        NodeFactory nf = ar.nodeFactory();
        if (base.type() instanceof UnknownType) {
            return nf.CanonicalTypeNode(position(), base.type());
        }
        return nf.CanonicalTypeNode(position(),
                                    ts.createFutureType(position(), (Ref<? extends X10NamedType>) base.typeRef()));
    }
    
    public Node visitChildren(NodeVisitor v) {
            TypeNode base = (TypeNode) visitChild(this.base, v);
            FutureNode_c result = (FutureNode_c) reconstruct(base);
            return result;
    }
    
    public String toString() {
            return "future" + base.toString() + ">";
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
