package polyglot.ext.x10.ast;

import java.util.HashMap;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ext.x10.types.X10ConstructorInstance;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.C_Field_c;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.types.constr.TypeTranslator;
import polyglot.frontend.Job;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

public class AssignPropertyBody_c extends StmtSeq_c implements AssignPropertyBody {
	final X10ConstructorInstance ci;
	final List<FieldInstance> fi;
	public AssignPropertyBody_c(Position pos, List<Stmt> statements, 
			X10ConstructorInstance ci, List<FieldInstance> fi) {
		super(pos, statements);
		this.ci = ci;
		this.fi = fi;
		
	}
	
	public X10ConstructorInstance constructorInstance() { return ci; }
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
		Constraint result = ci.constraint();
		Constraint known = ci.supClause();
		known = (known==null ? new Constraint_c(ts) : known.copy());
		{
			known.addIn(ci.whereClause());
		}

		List<Stmt> s = statements();
		int len = s.size();
		for (int i=0; i < len; i++) {
			Assign a = (Assign) ((Eval)s.get(i)).expr();
			Expr initializer = a.right();
			X10Type initType = (X10Type) initializer.type();
			C_Var prop = new C_Field_c(fi.get(i), C_Special.Self);
			Constraint c = initType.realClause();
			// Create a constraint (:) with self bound to the initializer.
			// TODO: should this live elsewhere?
			if (c==null) {
				c=new Constraint_c(ts);
				C_Term t = (C_Term) new TypeTranslator(ts).trans(initializer);
				if (t instanceof C_Var) {
					c.setSelfVar((C_Var) t);
				}
			}
			if (c != null) {
				HashMap<C_Var,C_Var> bindings = c.constraints(null, prop);
				C_Var selfVar = c.selfVar();
				if (selfVar != null) {
					known = known.addBinding(prop,selfVar);
				}
				known = known.addBindings(bindings);
			}
		}
		if (! known.entails(result)) {
			throw new SemanticException("Instances created by this constructor satisfy " + known 
					+ "; this is not strong enough to entail the return constraint " + result,
					position());
		}
		
		
	}


}
