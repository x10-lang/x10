/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.ArrayTypeNode_c;
import polyglot.util.Position;
import polyglot.ast.Node;
import polyglot.visit.NodeVisitor;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.visit.TypeBuilder;
import polyglot.types.SemanticException;

/** An immutable AST representation of an X10 array type.
 * @author vj Dec 9, 2004
 * 
 */
public class X10ArrayTypeNode_c extends ArrayTypeNode_c implements
		X10ArrayTypeNode {

	protected boolean isValueType;
	protected Expr indexedSet;
	
	/**
	 * @param pos
	 * @param base
	 */
	public X10ArrayTypeNode_c(Position pos, TypeNode base) {
		this(pos, base, false);
	}
	
	public X10ArrayTypeNode_c(Position pos, TypeNode base, boolean isValueType) {
		this(pos, base, isValueType, null);
	}
	
	/** Create an ArrayTypeNode for the X10 construct base[ expr ].
	 * expr must be a region or a distribution.
	 * 
	 * @param pos
	 * @param base
	 * @param isValueType
	 * @param indexedSet -- the region or distribution.
	 */
	public X10ArrayTypeNode_c(Position pos, TypeNode base, boolean isValueType, 
			Expr indexedSet ) {
		super(pos, base);
		this.isValueType = isValueType;
		this.indexedSet = indexedSet;
	}
	
	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10ArrayTypeNode#isValueArray()
	 */
	public boolean isValueType() {
		return this.isValueType;
	}

	/** Returns the indexset if any associated with this type.
	 * May return null.
	 * @see polyglot.ext.x10.ast.X10ArrayTypeNode#indexSet()
	 */
	public Expr indexSet() {
		return this.indexedSet;
	}

	 protected X10ArrayTypeNode_c reconstruct(TypeNode base,  Expr indexedSet) {
        if (base != this.base || (isValueType != this.isValueType) 
        		|| (indexedSet != this.indexedSet)) {
	    X10ArrayTypeNode_c n = (X10ArrayTypeNode_c) copy();
	    n.base = base;
	    n.indexedSet = indexedSet;
	    return n;
        }

        return this;
	 }

	 public Node visitChildren(NodeVisitor v) {
		TypeNode base = (TypeNode) visitChild(this.base, v);
		Expr indexedSet = (Expr) visitChild(this.indexedSet, v);
		return reconstruct(base, indexedSet);
	}

	   public Node buildTypes(TypeBuilder tb) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) tb.typeSystem();
	        return type(indexedSet == null 
	        		? ts.arrayOf(position(), base.type())
	        				: ts.arrayOf(position(), base.type(), indexedSet)
	        		);
	    }
}
