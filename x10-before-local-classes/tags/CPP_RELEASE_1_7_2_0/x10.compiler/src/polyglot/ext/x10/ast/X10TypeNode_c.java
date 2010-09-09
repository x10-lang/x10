/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Jan 9, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ast.TypeNode_c;
import polyglot.parse.Name;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.TypeTranslator;
import polyglot.ext.x10.visit.TypeElaborator;
import polyglot.main.Report;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

/** Implements dependent and generic type functionality.
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
            return (dep == null || dep.isDisambiguated()) && 
            (gen == null || gen.isDisambiguated()) && super.isDisambiguated() ;
        }
     /**
      * This method may throw MissingDependencyExceptions, and may therefore need to be retried.
      * @param me
      * @param sc
      * @return
      * @throws SemanticException
      */
    public static Node disambiguateDepClause(X10TypeNode me, AmbiguityRemover sc) throws SemanticException {
    	  Node newTypeNode1 =  me.disambiguateBase(sc);
        X10TypeNode newType = (X10TypeNode) newTypeNode1;
        X10Type baseType = (X10Type) newType.type();
        if ( !baseType.isCanonical()) {
            return newType; // bail... canonicalization needs to be done first.
        }
        assert me.dep() == null || me.dep().isDisambiguated();
        X10TypeSystem xt = (X10TypeSystem) me.type().typeSystem();
        
        GenParameterExpr newTParameter =  me.gen()==null ? null : (GenParameterExpr) me.gen().disambiguate( sc );
    	List<Type> typeParameters = new LinkedList<Type>(); 
    	if (newTParameter != null) {
    		List args = newTParameter.args();
    		if (args != null) {
    			Iterator it = args.iterator();
    			while (it.hasNext())
    				typeParameters.add(((TypeNode)it.next()).type());
    		}
    	}
    	
        TypeTranslator eval = xt.typeTranslator();
        DepParameterExpr dep = me.dep();
        
        boolean inAnnotation = ((X10Context) sc.context()).inAnnotation();
    	
        if (inAnnotation) {
        	// If in an annotation, we allow arbitrary expressions.
        	baseType.setDepGen(null, null);
        	X10Type newBaseType = baseType.dep(dep);
        	Node result = ((X10TypeNode) newType.type(newBaseType)).dep(null,null);
        	return result;
        }
        else {
        	// If we're not in an annotation, the expression is sugar for a @where annotation.
        	Constraint newParameter = dep == null ? null : eval.constraint(me.dep().condition());
        	// TODO: Fold in the args as well.
        	
        	baseType.setDepGen(newParameter, null);
        	X10Type newBaseType = baseType.makeVariant(null, typeParameters); // baseType.makeVariant(newParameter, typeParameters);
        	Node result = ((X10TypeNode) newType.type(newBaseType)).dep(null,null);
        	return result;
        }
    }
    /**
     * A delegated call from the parent.
     * @param tc
     * @return
     * @throws SemanticException
     */
    public static Node typeCheckDepClause( X10TypeNode me, TypeChecker tc) 
    throws SemanticException {
    	  if (  Report.should_report("debug", 5)) {
              Report.report(1,"[X10TypeNode_c static] typeCheckDepClause... entering|" 
            		  + me + " " + me.getClass() + "|.");
          }
        if (! (me instanceof X10TypeNode)) 
            throw new SemanticException("Argument to parametric type node does not type-check" 
                    + me.position());
       
        X10TypeNode arg = (X10TypeNode) me;
        X10Type argType = (X10Type) arg.type();
        X10TypeSystem ts = (X10TypeSystem) argType.typeSystem();
        
        if (arg.dep() == null && arg.gen() == null) return arg;
        
        List<Type> tParameters = new LinkedList<Type>();
        if (me.gen()!=null && me.gen().args() !=null) {
            Iterator it = me.gen().args().iterator();
            while (it.hasNext())
                tParameters.add(((TypeNode)it.next()).type());
        }
        DepParameterExpr d = arg.dep();
        
        boolean inAnnotation = ((X10Context) tc.context()).inAnnotation();
    	
        if (inAnnotation) {
        	// If in an annotation, we allow arbitrary expressions.

           //assert argType.isRootType();
            X10Type newArgType = argType;
            if (! tParameters.isEmpty()) {
            	newArgType = tc instanceof TypeElaborator  ?
            			argType.makeDepVariant(null, tParameters)
            			: argType.makeVariant(null, tParameters);
            }
            newArgType = newArgType.dep(d);
            X10TypeNode result = (X10TypeNode) arg.type(newArgType);
            result = result.dep(null,null);
            if (  Report.should_report("debug", 5)) {
                Report.report(1,"[X10TypeNode_c static] typeCheckDepClause... returning |" + result + "|.");
            }
            return result;
        }
        else {
        	// If we're not in an annotation, the expression is sugar for a @where annotation.

            // splice the information into the right places.
           
            TypeTranslator eval = ts.typeTranslator();
            Constraint term = eval.constraint(d.condition());
           //assert argType.isRootType();
            X10Type newArgType = tc instanceof TypeElaborator  ? 
            		argType.makeDepVariant(term, tParameters)
            		: argType.makeVariant(term, tParameters);
            
            X10TypeNode result = (X10TypeNode) arg.type(newArgType);
            result = result.dep(null,null);
            if (  Report.should_report("debug", 5)) {
                Report.report(1,"[X10TypeNode_c static] typeCheckDepClause... returning |" + result + "|.");
            }
            return result;
        }
        
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
		 X10TypeNode n = (X10TypeNode) typeCheckBase( tc );
        return X10TypeNode_c.typeCheckDepClause(n, tc);
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
		return "/*nullable*/" + super.toString() + "/*<"+gen+">("+dep+")*/";
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
