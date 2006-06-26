package polyglot.ext.x10.ast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.CanonicalTypeNode_c;
import polyglot.ext.x10.types.X10ReferenceType;
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
        if (  Report.should_report("debug", 5)) {
            Report.report(5,"[X10CanonicalTypeNode_c: ] Type checking  |" + this 
                    +"| " + getClass() + "|");
        }
        Node n = typeCheckBase( tc );
        if (Report.should_report("debug", 5)) {
            Report.report(5,"[X10TypeNode static] ... yields node |" + n.getClass() 
                    +"| of type |" + ((X10TypeNode) n).type() + "|");
        }
        if (! (n instanceof X10TypeNode)) 
            throw new SemanticException("Argument to parametric type node does not type-check" + position());
       
        X10TypeNode arg = (X10TypeNode) n;
        X10Type argType = (X10Type) arg.type();
        
        X10TypeSystem ts = (X10TypeSystem) argType.typeSystem();
        List tParameters = new LinkedList();
        if (gen()!=null && gen().args() !=null) {
            Iterator it = gen().args().iterator();
            while (it.hasNext())
                tParameters.add(((TypeNode)it.next()).type());
        }
        DepParameterExpr d = dep();
        X10Type newArgType = argType.makeVariant(d, tParameters);
        /*
        parent.setType(ts.createParametricType( position(),
                (X10ReferenceType) argType,
                tParameters,
                parameter));
        */
        // TODO: vj Check that the parameter is in fact a boolean expression, and uses only the fields 
        // of the base class that are marked as parameters.
        // TODO: vj Need to add self to the context, with type parent, and now treat field references
        // in the type as automatically prefixed with "self."
        
        // splice the information into the right places.
    
        this.type = newArgType;
        if ( Report.should_report("debug", 5)) {
            Report.report(5,"[X10CanonicalTypeNode_c] .." + this + "(#" + this.hashCode()+ ").type is now set to =" + newArgType);
        }
        
        return this;
        
        
    }
   
    public String toString() {
        return super.toString() + (gen ==null ? "" : "/*T:"+gen+"*/") 
        + (dep == null ? "" : "/*(" + dep+")*/");
    }
}
