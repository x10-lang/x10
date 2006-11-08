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
public class X10Instanceof_c extends Instanceof_c {

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

        return n.type(tc.typeSystem().Boolean());
	}

    /** Write the expression to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
    	boolean dynamicCheckNeeded = false;
    	if(((X10Type)this.compareType.type()).depClause() != null)  {
		   // cast is valid if toType or fromType have constraints, checks them at runtime
    		System.out.println("Instanceof" + this.expr.type() + " to " + this.compareType.type() + " will require a runtime check");
    		dynamicCheckNeeded = true;
    	}

    	if (dynamicCheckNeeded) {
    		X10Cast_c.X10CastHelper.prettyPrintInstanceOf(w,tr, (X10Type)this.compareType.type(),this.expr,this);
    	}
    	else {
    		super.prettyPrint(w,tr);
    	}
    }

}
