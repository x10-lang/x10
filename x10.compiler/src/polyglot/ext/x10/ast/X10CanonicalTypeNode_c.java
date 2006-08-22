package polyglot.ext.x10.ast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.CanonicalTypeNode_c;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.main.Report;
import polyglot.types.SemanticException;
import polyglot.types.Type;
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
    }
    public X10CanonicalTypeNode_c(Position pos, Type type, GenParameterExpr gen, DepParameterExpr dep) {
        super(pos, type);
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
        //Report.report(1, "X10CanonicalType: Adding a dep |" + d + "|, g=|" + g + "| => " + n);
        return n;
    }
    
    //TODO: Check if this is the right definition.
    public Node visitChildren( NodeVisitor v ) {
        GenParameterExpr gen = (GenParameterExpr) visitChild( this.gen, v);
        DepParameterExpr dep = (DepParameterExpr) visitChild( this.dep, v);
        return dep(gen,dep);
    }
    
    public Node disambiguateBase(AmbiguityRemover sc) throws SemanticException {
        return super.disambiguate(sc);
    }
    public Node typeCheckBase(TypeChecker sc) throws SemanticException {
        return super.typeCheck(sc);
    }
    public boolean isDisambiguated() {
        return super.isDisambiguated() && dep == null && gen == null;
    }
    public Node disambiguate(AmbiguityRemover sc) throws SemanticException {
         TypeNode result = (TypeNode) X10TypeNode_c.disambiguateDepClause(this, sc);
         if (  Report.should_report("debug", 5))
             Report.report(5,"X10CanonicalypeNode_c: (#" + this.hashCode()+")... returns |" + 
                     result + "| type=|" + result.type() + "|");
         return result;
     }
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        return X10TypeNode_c.typeCheckDepClause(this, tc);
    }
   
    public String toString() {
        return super.toString() + (gen ==null ? "" : "/*T:"+gen+"*/") 
        + (dep == null ? "" : "/*(" + dep+")*/");
    }
}
