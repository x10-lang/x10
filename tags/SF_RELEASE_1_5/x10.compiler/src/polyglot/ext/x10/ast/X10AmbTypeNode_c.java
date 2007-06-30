/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.QualifierNode;
import polyglot.ast.TypeNode;
import polyglot.ast.AmbTypeNode_c;
import polyglot.ast.TypeNode_c;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;


public class X10AmbTypeNode_c extends AmbTypeNode_c implements X10TypeNode {
   
    protected DepParameterExpr dep;
    public DepParameterExpr dep() { return dep;}
    
    protected GenParameterExpr gen;
    public GenParameterExpr gen() { return gen;}
    
    public X10AmbTypeNode_c(Position pos, QualifierNode qual, Id name) {
        super(pos,qual,name);
      
    }
    public X10AmbTypeNode_c(Position pos, QualifierNode qual,Id name, DepParameterExpr d) {
        super(pos,qual,name);
        dep = d;
    }

    public X10TypeNode gen(GenParameterExpr expr)  {
        if (expr == this.gen)  return this;
        X10TypeNode_c n = (X10TypeNode_c) copy();
        n.gen = expr;
        return n;
        } 
    
    public X10TypeNode dep( DepParameterExpr expr) {
        if (expr == this.dep)  return this;
        X10AmbTypeNode_c n = (X10AmbTypeNode_c) copy();
        n.dep = expr;
       
        return n;
        }  
    
    public X10TypeNode dep(GenParameterExpr g, DepParameterExpr d) {
        if (g == gen && d==dep) return this;
        X10AmbTypeNode_c n = (X10AmbTypeNode_c) copy();
        n.gen=g;
        n.dep=d;
        return n;
    }
    
    public Context enterChildScope(Node child, Context c) {
    	if (child == this.dep) {
    		TypeSystem ts = c.typeSystem();
    		if (lookaheadType instanceof X10NamedType)
    			c = ((X10Context) c).pushDepType((X10NamedType) lookaheadType);
    	}
        Context cc = super.enterChildScope(child, c);
        return cc;
    }
    public Node visitChildren( NodeVisitor v ) {
        GenParameterExpr gen = (GenParameterExpr) visitChild( this.gen, v);
        DepParameterExpr dep = (DepParameterExpr) visitChild( this.dep, v);
        return ((X10AmbTypeNode_c) super.visitChildren(v)).dep(gen,dep);
    }
      
    public boolean isDisambiguated() {
        boolean val = super.isDisambiguated() 
        && (dep == null || dep.isDisambiguated()) 
        && (gen == null || gen.isDisambiguated());
        return val;
    }
   
    Type lookaheadType = null;
    public NodeVisitor disambiguateEnter(AmbiguityRemover sc) throws SemanticException {
    	lookaheadType = ((TypeNode_c) super.disambiguate(sc)).type();
    	return sc;
    }
    public Node disambiguate(AmbiguityRemover sc) throws SemanticException {
    	boolean val = (dep != null && ! dep.isDisambiguated())
    	|| (gen !=null && ! gen.isDisambiguated());
    	if (val) return this;
    	assert (dep == null || dep.isDisambiguated());
    	Node result = disambiguateBase(sc);
        return ((X10TypeNode) result).dep(gen, dep);
    }
    public Node disambiguateBase(AmbiguityRemover sc) throws SemanticException {
        TypeNode result = (TypeNode) super.disambiguate(sc);
      //  Report.report(1, "X10AmbTypeNode_c returns " + result.getClass());
        return result;
    }
    
    public Node typeCheckBase(TypeChecker tc) throws SemanticException {
        return super.typeCheck(tc);
    }
    
}
