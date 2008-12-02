package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ext.x10.types.X10ConstructorDef;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.Job;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.TypeChecker;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XRef_c;
import x10.constraint.XSelf;
import x10.constraint.XTerm;
import x10.constraint.XVar;

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
	public Node typeCheckOverride(Node parent, ContextVisitor tc) throws SemanticException {
		return this;
	}
	
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		Context ctx = tc.context();
		NodeFactory nf = tc.nodeFactory();
		Position pos = position();
		Job job = tc.job();
		AssignPropertyBody_c n = (AssignPropertyBody_c) super.typeCheck(tc);
//		n.checkReturnType(ts);
		return n;
	}
	
	protected void checkReturnType(X10TypeSystem ts) throws SemanticException {
	    if (Types.get(ci.returnType()) instanceof UnknownType) {
	        throw new SemanticException();
	    }
	    Type returnType = Types.get(ci.returnType());
	    XConstraint result = X10TypeMixin.xclause(returnType);
	    if (result != null) {
	    XConstraint known = Types.get(ci.supClause());
	    known = (known==null ? new XConstraint_c() : known.copy());
	    try {
		    known.addIn(Types.get(ci.guard()));

		    //		result = result.substitute(self, self2);

		    List<Stmt> s = statements();
		    int len = s.size();
		    for (int i=0; i < len; i++) {
			    Assign a = (Assign) ((Eval)s.get(i)).expr();
			    Expr initializer = a.right();
			    Type initType = initializer.type();
			    final FieldInstance fii = fi.get(i);
			    XVar prop = (XVar) ts.xtypeTranslator().trans(XSelf.Self, fii);
			    prop.setSelfConstraint(new XRef_c<XConstraint>() { public XConstraint compute() { return X10TypeMixin.realX(fii.type()); } });
			    
			    // Add in the real clause of the initializer with [self.prop/self]
			    XConstraint c = X10TypeMixin.realX(initType);
			    if (c==null) {
				    c=new XConstraint_c();
				    XTerm t = ts.xtypeTranslator().trans(initializer);
				    c.addBinding(prop, t);
			    }
			    else {
				    known.addIn(c.substitute(prop, XSelf.Self));
			    }
		    }
		    if (! known.entails(result)) {
			    throw new SemanticException("Instances created by this constructor satisfy " + known 
			                                + "; this is not strong enough to entail the return constraint " + result,
			                                position());
		    }
	    }
	    catch (XFailure e) {
		    throw new SemanticException(e.getMessage());
	    } 
	    }

		
	}


}
