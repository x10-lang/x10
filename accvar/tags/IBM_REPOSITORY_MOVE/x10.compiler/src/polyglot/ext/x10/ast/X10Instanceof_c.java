package polyglot.ext.x10.ast;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Instanceof;
import polyglot.ast.Node;
import polyglot.ast.Precedence;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.Cast_c;
import polyglot.ext.jl.ast.Instanceof_c;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

/**
 * Represent java instanceof operation.
 * expression instanceof TargetType
 * This class is compliant with dependent type constraint.
 * If a dynamic check is needed then some code is generated to check declared 
 * type constraint are met by the instance.
 *
 * @author vcave
 *
 */
public class X10Instanceof_c extends Instanceof_c implements X10Instanceof, X10CastInfo{

	private boolean toTypeNullable = false;
	private boolean dynamicCheckNeeded = false;
	
	public X10Instanceof_c(Position pos, Expr expr, TypeNode compareType) {
    	super(pos,expr,compareType);
	}
 
    /** Type check the expression. */
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        Instanceof n = (Instanceof) node();
        Type rtype = n.compareType().type();
        Type ltype = n.expr().type();

        if (! tc.typeSystem().isCastValid(ltype, rtype)) {
            throw new SemanticException(
                      "Left operand of \"instanceof\" must be castable to "
                      + "the right operand.");
        }
        
        // is conversion from a nullable type to a non nullable one.        
        this.toTypeNullable = ((X10TypeSystem) tc.typeSystem()).isNullable(this.compareType.type());
    	this.dynamicCheckNeeded  = ((((X10Type)rtype).depClause() != null) && ((X10Type)ltype).depClause() == null)
    								|| this.isToTypeNullable();
    	return n.type(tc.typeSystem().Boolean());
	}

    public boolean isDynamicCheckNeeded() {
    	return this.dynamicCheckNeeded;
    }
    
	public boolean isPrimitiveCast() {
		return false;
	}

	/**
	 * Always return false as if we are dealing with a non nullable
	 * then the (null instanceof T) code generated will return false.   
	 */
	public boolean notNullRequired() {
		return false;
	}

	public boolean isToTypeNullable() {
		return this.toTypeNullable;
	}

}
