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
import polyglot.ext.x10.types.X10ReferenceType;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.main.Report;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

/**
 * @author vj Jan 9, 2005
 * @author Chrisitan Grothoff (added GenParameterExpr)
 */
public class ParametricTypeNode_c extends TypeNode_c implements
		ParametricTypeNode {
	protected TypeNode base;
	protected DepParameterExpr parameter;
	protected GenParameterExpr typeparameter;

	/**
	 * @param pos
	 */
	public ParametricTypeNode_c(Position pos, TypeNode base,
            GenParameterExpr types,
            DepParameterExpr parameter) {
		super(pos);
		this.base = base;
		this.typeparameter = types;
		this.parameter = parameter;

	}

	public TypeNode base() {
		return base;
	}
	public ParametricTypeNode_c base( TypeNode base) {
		ParametricTypeNode_c n = (ParametricTypeNode_c) copy();
		n.base = base;
		return n;
	}
    
	public DepParameterExpr parameter() {
	    return parameter;
	}
    
	public GenParameterExpr typeParameter() {
	    return typeparameter;
	}
	public ParametricTypeNode_c expr( DepParameterExpr expr) {
	    ParametricTypeNode_c n = (ParametricTypeNode_c) copy();
	    n.parameter = expr;
	    return n;
	}
    
	public ParametricTypeNode_c expr(GenParameterExpr expr) {
	    ParametricTypeNode_c n = (ParametricTypeNode_c) copy();
	    n.typeparameter = expr;
	    return n;
	}
    
	protected ParametricTypeNode_c reconstruct( TypeNode base,
            GenParameterExpr tp,
            DepParameterExpr parameter) {
		if (base == this.base && parameter == this.parameter &&
                tp == this.typeparameter) return this;
		ParametricTypeNode_c n = (ParametricTypeNode_c) copy();
		n.base = base;
		n.parameter = parameter;
		n.typeparameter = tp;
		return n;
	  }
	
	
	public Node visitChildren( NodeVisitor v ) {
		TypeNode base = (TypeNode) visitChild( this.base, v);
		DepParameterExpr parameter = (this.parameter == null)? null : (DepParameterExpr) visitChild( this.parameter, v);
		GenParameterExpr tparam = (this.typeparameter == null)? null : (GenParameterExpr) visitChild( this.typeparameter, v);
		return reconstruct( base, tparam, parameter);
	}

	/**
	 * Disambiguate the base node. Ensure that it is unambiguous and a
	 * ReferenceType. Create a ParametricType_c and store it in
	 * this.type.
	 */
	public Node disambiguate(AmbiguityRemover sc) throws SemanticException {
		if (Report.should_report("debug", 5)) {
			Report.report(5,"[ParametricTyeNode_c] Disambiguating |" + this + "|(#" 
					+ this.hashCode() +") with base=|" + base + "|:");
		}
		
		TypeNode newType = (TypeNode) base.disambiguate( sc );
		
		if (Report.should_report("debug", 5)) {
			Report.report(5,"[ParametricTypeeNode_c] ... yields type |" + newType + "|.");
		}
		
		Type baseType = newType.type();
		if (null == baseType || ! (baseType instanceof X10ReferenceType) ) {
			throw new SemanticException("The type constructor future cannot be applied to a <null> type", 
					position());
		}
		
		DepParameterExpr newParameter = parameter == null? null : (DepParameterExpr) parameter.disambiguate( sc );
		

		GenParameterExpr newTParameter = typeparameter == null? null : (GenParameterExpr) typeparameter.disambiguate( sc );

		List typeparameters = new LinkedList();
		Iterator it = newTParameter.args().iterator();
		while (it.hasNext())
		    typeparameters.add(((TypeNode)it.next()).type());
        
                X10TypeSystem ts = (X10TypeSystem) baseType.typeSystem();
		this.type = ts.createParametricType( position(), (X10ReferenceType) baseType, 
		        typeparameters,
		        newParameter );
		
		Node result = reconstruct( newType, newTParameter, newParameter );
		
		if (Report.should_report("debug", 5)) {
			Report.report(5,"[ParametricTypeNode_c] ... returns |" + result +"|(#" 
					+ result.hashCode() +").");
		}
		
		return result; 
	}
	
	/**
	 * Typecheck the type-argument (in this.base). If it typechecks
	 * (e.g. passes visibility constraints), and the result is a
	 * ReferenceType, update type.this if necessary. Otherwise throw a
	 * semantic exception.  TODO: Ensure that visibility of this node
	 * is the same as that of the type argument.
	 */
	
	public Node typeCheck( TypeChecker tc) throws SemanticException {
		
		if (Report.should_report("debug", 5)) {
			Report.report(5,"[ParametricTypeNode_c] Type checking |" + this +"|:");
		}
		
		Node n = base.typeCheck( tc );
		
		if (Report.should_report("debug", 5)) {
			Report.report(5,"[ParametricTypeNode_c] ... yields node |" + n +"|.");
		}
		
		if (! (n instanceof TypeNode)) 
			throw new SemanticException("Argument to parametric type node does not type-check" + position());
		if (n == base )
			return this;
		TypeNode arg = (TypeNode) n;
		Type argType = arg.type();
		if ( ! (argType instanceof X10ReferenceType ))
			throw new SemanticException("Argument to parametric type node must be a reference type" 
						+ position());
		X10TypeSystem ts = (X10TypeSystem) argType.typeSystem();
		List typeparameters = new LinkedList();
		Iterator it = typeparameter.args().iterator();
		while (it.hasNext())
		    typeparameters.add(((TypeNode)it.next()).type());

        
		this.type = ts.createParametricType( position(),
		        (X10ReferenceType) argType,
		        typeparameters,
		        parameter);
		
		// TODO: vj Check that the parameter is in fact a boolean expression, and uses only the fields 
		// of the base class that are marked as parameters.
		
		if (Report.should_report("debug", 5)) {
			Report.report(5, "[ParametricTypeNode_c] ... sets type to |" + this.type + "|.");
			Report.report(5, "[ParametricTypeNode_c] ... returns |" + this + "|."); 
		}
		//TODO: Check that this is ok, and a reconstruct does not need to be used.
		return this;
		
	}
	
	// TODO: Need to build CFG for the contained exprs?
	// This would require making this node be a kind of an Expr, and a TypeNode.
	
	
	public String toString() {
		return "/*nullable*/" + base.toString() +
		((typeparameter != null) ? typeparameter.toString() :"") +
                ((parameter != null) ? parameter.toString() : "");
	}
	/**
	 * Write out Java code for this node.
	 * Hmmm.. TODO: this will need to output the BoxedType for the primitive types, 
	 * and just the reference type (unchanged) otherwise.
	 */
	public void prettyPrint(CodeWriter w, PrettyPrinter ignore) {             
		w.write( base.toString());
	} 
	
	// translate??
	// prettyPrint?
	// dump?
}
