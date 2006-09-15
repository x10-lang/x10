package polyglot.ext.x10.ast;

import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.CanonicalTypeNode_c;
import polyglot.ext.jl.ast.TypeNode_c;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.main.Report;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;

public class X10CanonicalTypeNode_c extends CanonicalTypeNode_c implements
        X10TypeNode {

    protected DepParameterExpr dep;
    public DepParameterExpr dep() { return dep;}
    
    protected GenParameterExpr gen;
    public GenParameterExpr gen() { return gen;}
   
    public X10CanonicalTypeNode_c(Position pos, Type type) {
        super(pos, type);
       // Report.report(1, "X10CanonicalTypeNode: created with type |" + type + "|");
    }
    public X10CanonicalTypeNode_c(Position pos, Type type, GenParameterExpr gen, DepParameterExpr dep) {
        super(pos, type);
        //Report.report(1, "X10CanonicalTypeNode: created with type |" + type + "| dep=|" + dep+"|");
        this.gen=gen;
        this.dep=dep;
    }

    public X10TypeNode gen(GenParameterExpr expr)  {
        if (expr == this.gen)  return this;
        X10TypeNode_c n = (X10TypeNode_c) copy();
        n.gen = expr;
        return n;
        } 
    
    public X10TypeNode dep( DepParameterExpr expr) {
        if (expr == this.dep)  return this;
        X10TypeNode_c n = (X10TypeNode_c) copy();
        n.dep = expr;
        return n;
        }  
    public X10TypeNode dep(GenParameterExpr g, DepParameterExpr d) {
        if (g == gen && d==dep) return this;
        X10CanonicalTypeNode_c n = (X10CanonicalTypeNode_c) copy();
        n.gen=g;
        n.dep=d;
        return n;
    }
    
    public Context enterChildScope(Node child, Context c) {
    	if (child == this.dep) {
    		TypeSystem ts = c.typeSystem();
    		if (lookaheadType instanceof X10ParsedClassType) {
    			c = ((X10Context) c).pushDepType((X10ParsedClassType) lookaheadType);
    		}
    	}
    	Context cc = super.enterChildScope(child, c);
    	return cc;
    }
    
    Type lookaheadType = null;
    public AmbiguityRemover disambiguateEnter(AmbiguityRemover sc) throws SemanticException {
    	lookaheadType = ((TypeNode_c) super.disambiguate(sc)).type();
    	return sc;
    }
    public TypeChecker typeCheckEnter(TypeChecker tc) throws SemanticException {
    	//Report.report(1, "X10CanonicalType: typecheckEnter " + this + " dep=|" + this.dep + "|");
    	lookaheadType = ((TypeNode_c) super.typeCheck(tc)).type();
    	return tc;
    }
    public Node visitChildren( NodeVisitor v ) {
        GenParameterExpr gen = (GenParameterExpr) visitChild( this.gen, v);
      //  if (dep != null)
        //	Report.report(1, "X10CanonicalTypeNode_c: Visiting children |" + this.dep + "| with " + v);
        DepParameterExpr dep = (DepParameterExpr) visitChild( this.dep, v);
        return dep(gen,dep);
    }
    
    public Node disambiguateBase(AmbiguityRemover sc) throws SemanticException {
        return super.disambiguate(sc);
    }
    public Node typeCheckBase(TypeChecker sc) throws SemanticException {
        return super.typeCheck(sc);
    }
    public boolean isTypeChecked() {
    	boolean result = 
    		type != null && type.typeSystem().isCanonical(type) 
    		&& dep == null;
    	//if (type.toString().startsWith("Test"))
    	//Report.report(1, "X10CanonicalTypeNode_c: isTypeChecked " + this + " = " + result + " type=" + type.getClass());
    	return result;
    }
    public boolean isDisambiguated() {
        return super.isDisambiguated() && (dep == null || dep.isDisambiguated()) 
        && (gen==null || gen.isDisambiguated());
    }
    public Node disambiguate(AmbiguityRemover sc) throws SemanticException {
    	boolean val = (dep != null && ! dep.isDisambiguated()) ||
    	(gen != null && ! gen.isDisambiguated());
    	if (val) return this;
      //   TypeNode result = (TypeNode) X10TypeNode_c.disambiguateDepClause(this, sc);
         X10TypeNode result = (X10TypeNode) super.disambiguate(sc);
         return result.dep(gen,dep);
     }
    public Node typeCheck(TypeChecker tc) throws SemanticException {
    //	if (type.toString().startsWith("Test"))
    	//Report.report(1, "X10CanonicalType: typechecking: " + this + " dep=|" + this.dep + "|");
        return X10TypeNode_c.typeCheckDepClause(this, tc);
    }
   
    public String toString() {
        return super.toString() + (gen ==null ? "" : "/*T:"+gen+"*/") 
        + (dep == null ? "" : "/*(" + dep+")*/");
    }
}
