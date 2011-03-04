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

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ast.TypeNode_c;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.GoalSet;
import polyglot.types.Context;
import polyglot.types.LazyRef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
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
public class X10ArrayTypeNode_c extends TypeNode_c implements
X10ArrayTypeNode {
	protected TypeNode base;
	protected boolean isValueType;
	protected DepParameterExpr dep;
	
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
	
	public DepParameterExpr dep() {
	    return this.dep;
	}
	
	public X10ArrayTypeNode dep(DepParameterExpr dep) {
	    X10ArrayTypeNode_c n = (X10ArrayTypeNode_c) copy();
	    n.dep = dep;
	    return n;
	}
	
	/** Returns the indexset if any associated with this type.
	 * May return null.
	 * @see polyglot.ext.x10.ast.X10ArrayTypeNode#indexSet()
	 */
	public Expr distribution() {
	    DepParameterExpr indexedSet = dep;
	    List<Expr> args = indexedSet==null? null : indexedSet.args();
	    Expr distribution = (args == null || args.size() < 1) ? null : (Expr) args.get(0); // pick out the distribution.
	    return distribution;
	}
	
	protected X10ArrayTypeNode_c reconstruct(TypeNode base,  DepParameterExpr indexedSet) {
		if (base != this.base || (indexedSet != this.dep)) {
			X10ArrayTypeNode_c n = (X10ArrayTypeNode_c) copy();
			n.base = base;
			n.dep = indexedSet;
			return n;
		}
		
		return this;
	}
	public Context enterChildScope(Node child, Context c) {
		if (child == this.dep) {
                    Type t = type.get(GoalSet.EMPTY);
                    c = ((X10Context) c).pushDepType(type);
		}
		return super.enterChildScope(child, c);
	}
	
	public Node visitChildren(NodeVisitor v) {
		TypeNode base = (TypeNode) visitChild(this.base, v);
		DepParameterExpr indexedSet = (DepParameterExpr) visitChild(this.dep, v);
		return reconstruct(base, indexedSet);
	}
	
	public Node buildTypes(TypeBuilder tb) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) tb.typeSystem();
		return typeRef( Types.lazyRef(ts.array( base.type(), isValueType, distribution() ), tb.goal() ));
	}
	
	    public Node typeCheckOverride(Node parent, TypeChecker tc) throws SemanticException {
	        // Override to disambiguate the base type and rebuild the node before type checking the dep clause.

	        X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
	        NodeFactory nf = tc.nodeFactory();

	        X10ArrayTypeNode_c n = this;

	        TypeChecker childtc = (TypeChecker) tc.enter(parent, n);
	        
	        TypeNode tn = (TypeNode) n.visitChild(n.base, childtc);
	        n = (X10ArrayTypeNode_c) n.base(tn);
	        
	        if (tn.type() instanceof UnknownType) {
	            // Mark the type resolved to prevent us from trying to resolve this again and again.
	            LazyRef<Type> sym = (LazyRef<Type>) n.type;
	            sym.update(ts.unknownType(position()));
	            return n;
	        }
	        
	        DepParameterExpr dep = (DepParameterExpr) n.visitChild(n.dep, childtc);
	        n = (X10ArrayTypeNode_c) n.dep(dep);
	        
	        X10Type t = (X10Type) ts.array( tn.type(), isValueType, distribution() );

	        if (dep != null) {
	            t = X10TypeMixin.depClause(t, dep.constraint());
	        }

	        LazyRef<Type> sym = (LazyRef<Type>) n.type;
	        sym.update(t);

	        return nf.CanonicalTypeNode(position(), t);
	    }
	
	public Node exceptionCheck(ExceptionChecker ec) throws SemanticException {
		throw new InternalCompilerError(position(),
				"Cannot type check X10ArrayType Node " + this + " should have been converted to X10CanonicalTypeNode.");
	}
	
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		print(base, w, tr);
		w.write( (isValueType ? " value " : "") + "[." 
				+ (distribution() == null ? "" : distribution().toString()) + "]");
	}
	
	public void translate(CodeWriter w, Translator tr) {
		throw new InternalCompilerError(position(),
				"Cannot translate ambiguous node "
				+ this + ".");
	}
	
	public String toString() {
		return base.toString() 
		+ (isValueType ? " value " : "")
		+ "[" + (dep == null ? "." + (distribution()==null ? "" : distribution().toString()) : dep.toString()) + "]";
		
	}
	
}
