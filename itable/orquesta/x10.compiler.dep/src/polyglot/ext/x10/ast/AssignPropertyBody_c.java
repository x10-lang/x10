package polyglot.ext.x10.ast;

import java.util.HashMap;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10ConstructorDef;
import polyglot.ext.x10.types.X10ConstructorInstance;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.C_Field_c;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Special_c;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.types.constr.BindingConstraintSystem;
import polyglot.frontend.Job;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.SemanticException;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

public class AssignPropertyBody_c extends StmtSeq_c implements AssignPropertyBody {
	final X10ConstructorDef ci;
	final List<FieldInstance> fi;
	public AssignPropertyBody_c(Position pos, List<Stmt> statements, 
			X10ConstructorDef ci, List<FieldInstance> fi) {
		super(pos, statements);
		this.ci = ci;
		this.fi = fi;
		
	}
	
	public X10ConstructorDef constructorInstance() { return ci; }
	public List<FieldInstance> fieldInstances() { return fi; }

	@Override
	public Node typeCheckOverride(Node parent, TypeChecker tc) throws SemanticException {
		return this;
	}
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		Context ctx = tc.context();
		NodeFactory nf = tc.nodeFactory();
		Position pos = position();
		Job job = tc.job();
		AssignPropertyBody_c n = (AssignPropertyBody_c) super.typeCheck(tc);
		n.checkReturnType(ts);
		return n;
	}
	protected void checkReturnType(X10TypeSystem ts) throws SemanticException {
	    if (Types.get(ci.returnType()) instanceof UnknownType) {
	        throw new SemanticException();
	    }
	        X10ClassType returnType = (X10ClassType) Types.get(ci.returnType());
		Constraint result = returnType.depClause();
		Constraint known = Types.get(ci.supClause());
		known = (known==null ? new Constraint_c(ts) : known.copy());
		known.addIn(Types.get(ci.whereClause()));
		
		C_Special self = new C_Special_c(X10Special.SELF, returnType);
//		result = result.substitute(self, self2);

		List<Stmt> s = statements();
		int len = s.size();
		for (int i=0; i < len; i++) {
			Assign a = (Assign) ((Eval)s.get(i)).expr();
			Expr initializer = a.right();
			X10Type initType = (X10Type) initializer.type();
			C_Var prop = new C_Field_c(fi.get(i), self);
			Constraint c = initType.realClause();
			// Create a constraint (:) with self bound to the initializer.
			// TODO: should this live elsewhere?
			if (c == null) {
				c = new Constraint_c(ts);
				C_Term t = (C_Term) ts.typeTranslator().trans(initializer);
				if (t instanceof C_Var) {
				    c.addSelfBinding((C_Var) t);
				}
			}
			if (c != null) {
			    Constraint c2 = c.substitute(prop, self);
			    known.addIn(c2);
			}
		}
		if (! known.entails(result)) {
			throw new SemanticException("Instances created by this constructor satisfy " + known 
					+ "; this is not strong enough to entail the return constraint " + result,
					position());
		}
		
		
	}


}
