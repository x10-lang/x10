/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Stmt_c;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10ConstructorDef;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.Job;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XRef_c;
import x10.constraint.XRoot;
import x10.constraint.XSelf;
import x10.constraint.XTerm;
import x10.constraint.XVar;

/**
 * @author vj
 *
 */
public class AssignPropertyCall_c extends Stmt_c implements AssignPropertyCall {

	List<TypeNode> typeArgs;
	List<Expr> arguments;

	
	/**
	 * @param pos
	 * @param target
	 * @param name
	 * @param arguments
	 */
	public AssignPropertyCall_c(Position pos, List<TypeNode> typeArgs, List<Expr> arguments) {
		super(pos);
		this.typeArgs = TypedList.copyAndCheck(typeArgs, TypeNode.class, true);
		this.arguments = TypedList.copyAndCheck(arguments, Expr.class, true);
		
	}
	  public Term firstChild() {
		  return listChild(typeArgs,  listChild(arguments, null));
	    }

	/* (non-Javadoc)
	 * @see polyglot.ast.Term#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
	 */
	  public List acceptCFG(CFGBuilder v, List succs) {
		  if (! typeArgs.isEmpty())
			  v.visitCFGList(typeArgs, listChild(arguments, this), ENTRY);
		  v.visitCFGList(arguments, this, EXIT);
		  return succs;
	    }

	  
	  /** Return a copy of this node with this.expr equal to the given expr.
	   * @see polyglot.ext.x10.ast.Await#expr(polyglot.ast.Expr)
	   */
	  public AssignPropertyCall args( List<Expr> args ) {
		  if (args == arguments) return this;
		  AssignPropertyCall_c n = (AssignPropertyCall_c) copy();
		  n.arguments = TypedList.copyAndCheck(args, Expr.class, true);
		  return n;
	  }
	  
	  public List<Expr> args() {
		  return arguments;
	  }
	
	/** Return a copy of this node with this.expr equal to the given expr.
	 * @see polyglot.ext.x10.ast.Await#expr(polyglot.ast.Expr)
	 */
	public AssignPropertyCall typeArgs( List<TypeNode> args ) {
		if (args == typeArgs) return this;
		AssignPropertyCall_c n = (AssignPropertyCall_c) copy();
		n.typeArgs = TypedList.copyAndCheck(args, TypeNode.class, true);
		return n;
	}
	
	public List<TypeNode> typeArgs() {
	    return typeArgs;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("property");
		if (! typeArgs.isEmpty())
			sb.append(typeArgs);
		sb.append("(");
		boolean first = true;
		for (Expr e : arguments) {
			if (first) {
				first = false;
			}
			else {
				sb.append(", ");
			}
			sb.append(e);
		}
		sb.append(");");
		return sb.toString();
	}
	
	public Node typeCheck(ContextVisitor tc) throws SemanticException {
		TypeSystem ts = tc.typeSystem();
		Context ctx = tc.context();
		X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
		Position pos = position();
		Job job = tc.job();
		if (! (ctx.inCode()) || ! (ctx.currentCode() instanceof X10ConstructorDef))
			throw new SemanticException("A property statement may only occur in the body of a constructor.",
					position());
		X10ConstructorDef thisConstructor = null;
		thisConstructor = (X10ConstructorDef) ctx.currentCode();
		// Now check that the types of each actual argument are subtypes of the corresponding
		// property for the class reachable through the constructor.
		List<FieldInstance> definedProperties = 
			((X10ParsedClassType) thisConstructor.asInstance().container()).definedProperties();
		List<Type> definedTypeProperties = 
		    ((X10ParsedClassType) thisConstructor.asInstance().container()).typeProperties();
		int pSize = definedProperties.size();
		int aSize = arguments.size();
		if (aSize != pSize) {
		    throw new SemanticException("The property initializer must have the same number of arguments as properties for the class.",
		                                position());
		}
		int tpSize = definedTypeProperties.size();
		int taSize = typeArgs.size();
		if (taSize != tpSize) {
		    throw new SemanticException("The property initializer must have the same number of type arguments as type properties for the class.",
		                                position());
		}
		
		 checkAssignments(tc, pos, thisConstructor, definedProperties, definedTypeProperties);
		 
		 List<Stmt> s = new ArrayList<Stmt>(pSize);

		 for (int i=0; i < aSize; i++) {
		     //				 We fudge type checking of the generating code as follows.
		     // X10 Typechecking of the assignment statement is problematic since 	
		     // the type of the field may have references to other fields, hence may use this,
		     // But this doesn't exist yet. We will check all the properties simultaneously
		     // in AssignPropertyBody. So we do not need to check it here. 
		     Expr arg = arguments.get(i);
		     FieldAssign as = nf.FieldAssign(pos, nf.This(pos), nf.Id(pos, definedProperties.get(i).name()), Assign.ASSIGN, arg);
		     as = (FieldAssign) this.visitChild(as, tc);
		     Stmt a = (Stmt) nf.Eval(pos, as);
		     a = (Stmt) a.disambiguate(tc);
		     // a = (Stmt) a.visit(tc); Do not type-check the statement a.
		     s.add(a);
		 }

		 
		 return nf.AssignPropertyBody(pos, s, thisConstructor, definedProperties).del().typeCheck(tc);
	}

	protected void checkAssignments(ContextVisitor tc, Position pos, X10ConstructorDef thisConstructor, List<FieldInstance> definedProperties, List<Type> definedTypeProperties)
		throws SemanticException {
	    X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		    Context ctx = tc.context();
		    if (Types.get(thisConstructor.returnType()) instanceof UnknownType) {
		        throw new SemanticException();
		    }
		    
		    Type returnType = Types.get(thisConstructor.returnType());
		    
		    XConstraint result = X10TypeMixin.xclause(returnType);
		    if (result != null) {
			XConstraint known = Types.get(thisConstructor.supClause());
			known = (known==null ? new XConstraint_c() : known.copy());
			try {
		            known.addIn(Types.get(thisConstructor.guard()));
		            
		            for (int i = 0; i < arguments.size(); i++) {
		        	Expr initializer = arguments.get(i);
		        	Type initType = initializer.type();
		        	final FieldInstance fii = definedProperties.get(i);
		        	XVar prop = (XVar) ts.xtypeTranslator().trans(XSelf.Self, fii);
		        	prop.setSelfConstraint(new XRef_c<XConstraint>() {
		        	    public XConstraint compute() { return X10TypeMixin.realX(fii.type()); } });

		        	// Add in the real clause of the initializer with [self.prop/self]
		        	XConstraint c = X10TypeMixin.realX(initType);
		        	XTerm initVar = null;
		        	try {
		        	    initVar = ts.xtypeTranslator().trans(initializer);
		        	}
		        	catch (SemanticException e) {
		        	}
		        	if (c == null)
		        	    c = new XConstraint_c();
		        	else 
		        	    c = c.copy();
		        	c = c.substitute(prop, XSelf.Self);
		        	if (initVar != null)
		        	    c.addBinding(prop, initVar);
		        	known.addIn(c);
		            }

		            for (int i = 0; i < typeArgs.size(); i++) {
		        	TypeNode tn = typeArgs.get(i);
		        	Type pt = definedTypeProperties.get(i);
		        	XVar prop = (XVar) ts.xtypeTranslator().trans(pt);
		        	prop = (XVar) prop.subst(XSelf.Self, (XRoot) prop.rootVar());

		        	// Add in the real clause of the initializer with [self.prop/self]
		        	XConstraint c = new XConstraint_c();
		        	XTerm t = ts.xtypeTranslator().trans(tn);
		        	c.addBinding(prop, t);
		        	known.addIn(c);
		            }

		            if (! known.copy().entails(result)) {
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
	
	/** Visit the children of the statement. */
	
	    public Node visitChildren(NodeVisitor v) {
	        List<TypeNode> typeArgs = visitList(this.typeArgs, v);
	        List<Expr> args = visitList(this.arguments, v);
		return typeArgs(typeArgs).args(args);
	    }
	    
	    Expr expr;
	    
	   
}


