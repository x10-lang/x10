/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.TypeNode_c;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.ast.Node;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.ExceptionChecker;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import polyglot.visit.TypeChecker;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.visit.TypeBuilder;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;

/** An immutable AST representation of an X10 array type.
 * @author vj Dec 9, 2004
 * 
 */
public class X10ArrayTypeNode_c extends TypeNode_c implements
		X10ArrayTypeNode {
    protected TypeNode base;
	protected boolean isValueType;
	protected DepParameterExpr distribution;
	
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
			DepParameterExpr indexedSet ) {
		super(pos);
		this.base = base;
		this.isValueType = isValueType;
		this.distribution = indexedSet;
	}
	
	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10ArrayTypeNode#isValueType()
	 */
	public boolean isValueType() {
		return this.isValueType;
	}

	public TypeNode base() {
		return this.base;
	}
	public X10ArrayTypeNode base(TypeNode base ) {
		X10ArrayTypeNode_c n = (X10ArrayTypeNode_c) copy();
		n.base = base;
		return n;
	}
	/** Returns the indexset if any associated with this type.
	 * May return null.
	 * @see polyglot.ext.x10.ast.X10ArrayTypeNode#indexSet()
	 */
	public Expr distribution() {
		return this.distribution;
	}

	 protected X10ArrayTypeNode_c reconstruct(TypeNode base,  DepParameterExpr indexedSet) {
        if (base != this.base || (isValueType != this.isValueType) 
        		|| (indexedSet != this.distribution)) {
	    X10ArrayTypeNode_c n = (X10ArrayTypeNode_c) copy();
	    n.base = base;
	    n.distribution = indexedSet;
	    return n;
        }

        return this;
	 }

	 public Node visitChildren(NodeVisitor v) {
		TypeNode base = (TypeNode) visitChild(this.base, v);
		DepParameterExpr indexedSet = (DepParameterExpr) visitChild(this.distribution, v);
		return reconstruct(base, indexedSet);
	}

	   public Node buildTypes(TypeBuilder tb) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) tb.typeSystem();
		return type( ts.array( base.type(), isValueType, distribution ));
	    }
	  

    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
    	X10TypeSystem ts = (X10TypeSystem) ar.typeSystem();
    	NodeFactory nf = ar.nodeFactory();
    	
    	Type baseType = base.type();
    	
    	if (! baseType.isCanonical())
    	    return this;
//    	{
//    		throw new SemanticException(
//    				"Base type " + baseType + " of array could not be resolved.",
//					base.position());
//    	}
    	// Now the base type is known. Simply ask the type system to load the corresponding
    	// class and return the type you thus get back. No need for X10ArrayType and X10ArrayType_c.
    	return nf.CanonicalTypeNode(position(), ts.array(baseType, isValueType, distribution));
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
	throw new InternalCompilerError(position(),
	    "Cannot type check ambiguous node " + this + ".");
    }

    public Node exceptionCheck(ExceptionChecker ec) throws SemanticException {
	throw new InternalCompilerError(position(),
	    "Cannot exception check ambiguous node " + this + ".");
    }

    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
    	System.out.println("X10ArrayTypeNode:  base=" + base);
        print(base, w, tr);
        w.write( (isValueType ? " value " : "") + "[." 
        		+ (distribution == null ? "" : distribution.toString()) + "]");
    }

    public void translate(CodeWriter w, Translator tr) {
      throw new InternalCompilerError(position(),
                                      "Cannot translate ambiguous node "
                                      + this + ".");
    }

    public String toString() {
        return base.toString() 
		+ (isValueType ? " value " : "")
		+ "[." + (distribution == null ? "" : distribution.toString()) + "]";
    }

}
