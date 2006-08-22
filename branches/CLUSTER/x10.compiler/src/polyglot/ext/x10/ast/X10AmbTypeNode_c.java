package polyglot.ext.x10.ast;

import polyglot.ast.Node;
import polyglot.ast.QualifierNode;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.AmbTypeNode_c;
import polyglot.main.Report;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;


public class X10AmbTypeNode_c extends AmbTypeNode_c implements X10TypeNode {
   
    protected DepParameterExpr dep;
    public DepParameterExpr dep() { return dep;}
    
    protected GenParameterExpr gen;
    public GenParameterExpr gen() { return gen;}
    
    public X10AmbTypeNode_c(Position pos, QualifierNode qual, String name) {
        super(pos,qual,name);
      
    }
    public X10AmbTypeNode_c(Position pos, QualifierNode qual,String name, DepParameterExpr d) {
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
        X10TypeNode_c n = (X10TypeNode_c) copy();
        n.dep = expr;
       
        return n;
        }  
    
    public X10TypeNode dep(GenParameterExpr g, DepParameterExpr d) {
        if (g == gen && d==dep) return this;
        
        X10AmbTypeNode_c n = (X10AmbTypeNode_c) copy();
        n.gen=g;
        n.dep=d;
        Report.report(1, "X10AmbType: Adding a dep |" + d + "|, g=|" + g + "| => " + n);
        return n;
    }
    
    public Node visitChildren( NodeVisitor v ) {
        GenParameterExpr gen = (GenParameterExpr) visitChild( this.gen, v);
        DepParameterExpr dep = (DepParameterExpr) visitChild( this.dep, v);
        return ((X10AmbTypeNode_c) super.visitChildren(v)).dep(gen,dep);
    }
      
    public boolean isDisambiguated() {
        return super.isDisambiguated() && dep == null && gen == null;
    }
   
    public Node disambiguate(AmbiguityRemover sc) throws SemanticException {
       // Report.report(5, "X10AmbTypeNode_c: disambiguate (#" + this.hashCode() + ")|" + this + "| type=|" + type()+"|");
        TypeNode result = (TypeNode) X10TypeNode_c.disambiguateDepClause(this, sc);
        // TypeNode result = (TypeNode) super.disambiguate(sc);
       //Report.report(5,"X10AmbTypeNode_c: (#" + this.hashCode()+")... returns |" + result + "| type=|" + result.type() + "|");
        return result;
    }
    public Node disambiguateBase(AmbiguityRemover sc) throws SemanticException {
       //Report.report(5, "X10AmbTypeNode_c: disambiguateBase (#" + this.hashCode() + ")|" + this+"| type=|" + type()+"|");
        TypeNode result = (TypeNode) super.disambiguate(sc);
        //Report.report(5,"X10AmbTypeNode_c: (#" + this.hashCode()+")... returns |" + result + "| type=|" + result.type() + "|");
        return result;
    }
    
    public Node typeCheckBase(TypeChecker tc) throws SemanticException {
        return super.typeCheck(tc);
    }
    
}
