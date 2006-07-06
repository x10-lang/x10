/*
 * Created by vj on Jan 9, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.TypeNode_c;
import polyglot.ext.jl.parse.Name;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.main.Report;
import polyglot.types.SemanticException;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

/** Implements dependent and generic type functionality by using a helper object.
 * The helper object points back to this object through the parent field.
 * Care must be taken to ensure that these two way pointers are properly updated
 * when a new version of this object or the hepler is created by modifying fields.
 * @author vj Jan 9, 2005
 * @author Chrisitan Grothoff (added GenParameterExpr)
 */
public class X10TypeNode_c extends TypeNode_c implements X10TypeNode {
// begin common code to implement DepType
	
    protected DepParameterExpr dep;
    public DepParameterExpr dep() { return dep;}
    
    protected GenParameterExpr gen;
    public GenParameterExpr gen() { return gen;}

	/**
	 * @param pos
	 */
    public X10TypeNode_c(Position pos) {
        super(pos);
        
    }
	public X10TypeNode_c(Position pos, 
            Name name,
            GenParameterExpr types,
            DepParameterExpr parameter) {
		super(pos);
        this.gen=types;
        this.dep=parameter;
	}

     public boolean isDisambiguated() {
            return super.isDisambiguated() && dep == null && gen == null;
        }
    public static Node disambiguateDepClause(X10TypeNode me, AmbiguityRemover sc) throws SemanticException {
   
      if (Report.should_report("debug", 5)) {
            Report.report(5,"[X10TypeNode_c static, (#" + me.hashCode() + ")] " + me.getClass() + "|");
        }
        Node newTypeNode1 =  me.disambiguateBase(sc);
        
        if (Report.should_report("debug", 5)) {
            Report.report(5,"[X10TypeNode_c static] " + me.hashCode() 
                    + " ... yields type |" + newTypeNode1 + "| (" + newTypeNode1.getClass()+")|"); 
        }
        
        X10TypeNode newType = (X10TypeNode) newTypeNode1;
        X10Type baseType = (X10Type) newType.type();
      
        
        if (Report.should_report("debug", 5)) {
            Report.report(5,"[X10TypeNode_c static] "+ me.hashCode() + " ... of type |" 
                    + baseType + "| " // + baseType.getClass() + "| canonical=|" +  baseType.isCanonical()
                    + "| parameter=|" + me.dep() + "| typeparamater=|" + me.gen() + "| " + me.gen().getClass());
        }
        
        if ( !baseType.isCanonical()) {
            if ( Report.should_report("debug", 5)) {
                Report.report(5,"[X10TypeNode_c static] " + me.hashCode() +" ... bailing early with |" + newType+"|(#" 
                        + newType.hashCode() +").");
            }
            return newType; // bail... canonicalization needs to be done first.
        }
       
        DepParameterExpr newParameter = me.dep() == null? null : (DepParameterExpr) me.dep().disambiguate( sc );
        
        GenParameterExpr newTParameter =  me.gen()==null ? null : (GenParameterExpr) me.gen().disambiguate( sc );
        
        if ( Report.should_report("debug", 5)) {
            Report.report(1,"[X10TypeNode_c static] newParameter="+ newParameter );
        }
        X10TypeSystem ts = (X10TypeSystem) baseType.typeSystem();
        List typeParameters = new LinkedList(); 
        if (newTParameter != null) {
            List args = newTParameter.args();
            if (args != null) {
                Iterator it = args.iterator();
                while (it.hasNext())
                    typeParameters.add(((TypeNode)it.next()).type());
            }
        }
        // At this point we have information to augment the type information stored in the parent node.
        // So there should be no need to store the DepTypeHandler. 
        X10Type newBaseType = baseType.makeVariant(newParameter, typeParameters);
       
        Node  result = ((X10TypeNode) newType.type(newBaseType)).dep(null,null);
        if ( Report.should_report("debug", 5)) {
            Report.report(5,"[X10TypeNode_c static] disambiguate (#" + me +") ... returns |" + result +"|(#" 
                    + result.hashCode() +").");
        }
        
        return result; 
     
    }
    /**
     * A delegated call from the parent.
     * @param tc
     * @return
     * @throws SemanticException
     */
    public static Node typeCheckDepClause( X10TypeNode me, TypeChecker tc) throws SemanticException {
        if ( Report.should_report("debug", 5)) {
            Report.report(5,"[X10TypeNode static] Type checking  |" + me 
                    +"| " + me.getClass() + "|");
        }
        Node n = me.typeCheckBase( tc );
        if ( Report.should_report("debug", 5)) {
            Report.report(5,"[X10TypeNode static] ... yields node |" + n.getClass() 
                    +"| of type |" + ((X10TypeNode) n).type() + "|");
        }
        if (! (n instanceof X10TypeNode)) 
            throw new SemanticException("Argument to parametric type node does not type-check" + me.position());
       
        X10TypeNode arg = (X10TypeNode) n;
        X10Type argType = (X10Type) arg.type();
        
        if (me.dep() == null && me.gen() == null) {
         //   new Exception().printStackTrace();
            if ( Report.should_report("debug", 5)) {
                Report.report(5,"[X10TypeNode_c static] ..returning with |" + arg + "|.");
            }
            return arg;
        }
        X10TypeSystem ts = (X10TypeSystem) argType.typeSystem();
        List tParameters = new LinkedList();
        if (me.gen()!=null && me.gen().args() !=null) {
            Iterator it = me.gen().args().iterator();
            while (it.hasNext())
                tParameters.add(((TypeNode)it.next()).type());
        }
        DepParameterExpr d = me.dep();
       
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
    
        TypeNode result = arg.type(newArgType);
        if (Report.should_report("debug", 5)) {
            Report.report(5,"[X10TypeNode_c static] ... done |" + result + "|.");
        }
        
        return result;
        
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
        X10TypeNode_c n = (X10TypeNode_c) copy();
        n.gen=g;
        n.dep=d;
        return n;
    }
    
    
	public Node visitChildren( NodeVisitor v ) {
        GenParameterExpr gen = (GenParameterExpr) visitChild( this.gen, v);
		DepParameterExpr dep = (DepParameterExpr) visitChild( this.dep, v);
		return dep(gen,dep);
	}

	/**
	 * Disambiguate the base node. Ensure that it is unambiguous and a
	 * ReferenceType. Create a ParametricType_c and store it in
	 * this.type.
	 */
	public Node disambiguate(AmbiguityRemover sc) throws SemanticException {
        return  X10TypeNode_c.disambiguateDepClause(this, sc);
    }
	
	/**
	 * Typecheck the type-argument (in this.base). If it typechecks
	 * (e.g. passes visibility constraints), and the result is a
	 * ReferenceType, update type.this if necessary. Otherwise throw a
	 * semantic exception.  TODO: Ensure that visibility of this node
	 * is the same as that of the type argument.
	 */
	
	public Node typeCheck( TypeChecker tc) throws SemanticException {
        return X10TypeNode_c.typeCheckDepClause(this, tc);
	}
    
    public Node typeCheckBase(TypeChecker tc) throws SemanticException {
        return  super.typeCheck(tc);
    }
	
    public Node disambiguateBase(AmbiguityRemover tc) throws SemanticException {
        return  super.disambiguate(tc);
    }
    // end common code to implement DepType
    
	// TODO: Need to build CFG for the contained exprs?
	// This would require making this node be a kind of an Expr, and a TypeNode.
	
	
	public String toString() {
		return "/*nullable*/" + super.toString() + "/*<"+gen+">(" + dep+")";
	}
	/**
	 * Write out Java code for this node.
	 * Hmmm.. TODO: this will need to output the BoxedType for the primitive types, 
	 * and just the reference type (unchanged) otherwise.
	 */
	public void prettyPrint(CodeWriter w, PrettyPrinter ignore) {             
		w.write( this.toString());
	} 
	
	// translate??
	// prettyPrint?
	// dump?
}
