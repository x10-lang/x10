/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.main.Report;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.ExceptionChecker;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

/** An immutable AST representation of an X10 array type.
 * @author vj Dec 9, 2004
 * 
 */
public class X10ArrayTypeNode_c extends X10TypeNode_c implements
X10ArrayTypeNode {
	protected TypeNode base;
	protected boolean isValueType;
	protected Expr distribution;
	// protected DepParameterExpr distribution;
	
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
	
	public DepParameterExpr indexedSet() {
		return dep;
	}
	
	/** Create an ArrayTypeNode for the X10 construct base[ depClause].
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
		List args = indexedSet==null? null : indexedSet.args();
		
		this.distribution = (args == null || args.size() < 1) ? null : (Expr) args.get(0); // pick out the distribution.
		this.dep = indexedSet;
		//Report.report(1, "X10ArrayTypeNode *** dep=" + indexedSet + "dist=" + this.distribution);
	}
	
	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10ArrayTypeNode#isValueArray()
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
	
	public X10ArrayTypeNode reconstruct(TypeNode base,  Expr indexedSet) {
		if (base != this.base || (isValueType != this.isValueType) 
				|| (indexedSet != this.distribution)) {
			X10ArrayTypeNode_c n = (X10ArrayTypeNode_c) copy();
			n.base = base;
			this.distribution = indexedSet;
			return n;
		}
		
		return this;
	}
	
	protected X10ArrayTypeNode_c reconstruct(TypeNode base,  DepParameterExpr indexedSet) {
		if (base != this.base || (isValueType != this.isValueType) 
				|| (indexedSet != this.distribution)) {
			X10ArrayTypeNode_c n = (X10ArrayTypeNode_c) copy();
			n.base = base;
			List args = indexedSet.args();
			this.distribution = (args == null ? null : (Expr) args.get(0)); // pick out the distribution.
			this.dep = indexedSet;
			return n;
		}
		
		return this;
	}
	public Context enterChildScope(Node child, Context c) {
		if (child == this.dep) {
			if (type instanceof X10ParsedClassType)
				c = ((X10Context) c).pushDepType((X10ParsedClassType) type);
		}
		return super.enterChildScope(child, c);
	}
	
	public Node visitChildren(NodeVisitor v) {
		X10ArrayTypeNode n = (X10ArrayTypeNode) super.visitChildren(v);
		TypeNode base = (TypeNode) n.visitChild(this.base, v);
		Expr indexedSet = (Expr) n.visitChild(this.distribution, v);
		return n.reconstruct(base, indexedSet);
	}
	
	public Node buildTypes(TypeBuilder tb) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) tb.typeSystem();
		return type( ts.array( base.type(), isValueType, distribution ));
	}
	
	
	public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
		//Report.report(1, "X10ArrayTypeNode entering disambiguate of " + this);
		X10TypeSystem ts = (X10TypeSystem) ar.typeSystem();
		NodeFactory nf = ar.nodeFactory();
		
		Type baseType = base.type();
		
		if (! baseType.isCanonical())
			return this;
//		{
//		throw new SemanticException(
//		"Base type " + baseType + " of array could not be resolved.",
//		base.position());
//		}
		// Now the base type is known. Simply ask the type system to load the corresponding
		// class and return the type you thus get back. No need for X10ArrayType and X10ArrayType_c.
		Node n = ((X10TypeNode) nf.CanonicalTypeNode(position(), ts.array(baseType, isValueType, 
				distribution))).dep(dep);
		//Report.report(1, "X10ArrayTypeNode returning " + n);
		return n;
	}
	
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		throw new InternalCompilerError(position(),
				"Cannot type check X10ArrayType Node " + this + " should have been converted to X10CanonicalTypeNode.");
	}
	
	public Node exceptionCheck(ExceptionChecker ec) throws SemanticException {
		throw new InternalCompilerError(position(),
				"Cannot type check X10ArrayType Node " + this + " should have been converted to X10CanonicalTypeNode.");
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
		+ "[" + (dep == null ? "." + (distribution==null ? "" : distribution.toString()) : dep.toString()) + "]";
		
	}
	
}
