/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ClassBody;
import polyglot.ast.Expr;
import polyglot.ast.Lit;
import polyglot.ast.Local_c;
import polyglot.ast.MethodDecl;
import polyglot.ast.New_c;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10ConstructorInstance;
import polyglot.ext.x10.types.X10LocalInstance;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.C_Lit;
import polyglot.ext.x10.types.constr.C_Local;
import polyglot.ext.x10.types.constr.C_Root;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Failure;
import polyglot.ext.x10.types.constr.Promise;
import polyglot.ext.x10.types.constr.TypeTranslator;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.LocalInstance;
import polyglot.types.MethodInstance;
import polyglot.types.ParsedClassType;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.TypeChecker;

/**
 * An allocation wrapper to rewrite array pointwiseOp construction.
 * @author Igor
 */
public class X10New_c extends New_c {
	public X10New_c(Position pos, Expr qualifier, TypeNode tn, List arguments, ClassBody body) {
		super(pos, qualifier, tn, arguments, body);
	}

	public Node typeCheckOverride(Node parent, TypeChecker tc) throws SemanticException {
		X10New_c n = (X10New_c) super.typeCheckOverride(parent, tc);
		return n;
	}

	/**
	 * Rewrite pointwiseOp construction to use Operator.Pointwise, otherwise
	 * leave alone.
	 */
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		X10New_c n = this;
		X10NodeFactory xnf = (X10NodeFactory) tc.nodeFactory();
		X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
		TypeNode oType = n.objectType();
		String opName = null;
		if (n.body != null && n.qualifier == null && oType instanceof CanonicalTypeNode) {
			ClassType type = oType.type().toClass();
			ClassType outer = null;
			if (type.isNested() && (outer = type.container().toClass()) != null && xts.isX10Array(outer)) {
				opName = type.name();
			}
		}
		if (opName != null) {
			// Special array operations
			Position nPos = oType.position();

			if (opName.equals("pointwiseOp"))
			{
				List members = n.body.members();
				MethodDecl apply = n.findMethod1Arg("apply", members);
				MethodInstance mi = apply.methodInstance();
				TypeNode appResultType = apply.returnType();
				List formals = apply.formals();
				assert (!formals.isEmpty());
				List l1 = TypedList.copy(formals, X10Formal.class, false);
				X10Formal firstArg = (X10Formal) formals.get(0);
				Position pos = firstArg.position();
				LocalInstance li = firstArg.localInstance();
				li = li.flags(Flags.FINAL).type(mi.returnType()).name("_");
				l1.add(xnf.Formal(pos, Flags.FINAL, appResultType, xnf.Id(pos, "_")).localInstance(li));
				ReferenceType tOperatorPointwise = xts.OperatorPointwise();
				List l2 = TypedList.copy(mi.formalTypes(), Type.class, false);
				l2.add(mi.returnType());
				mi = mi.container(tOperatorPointwise).formalTypes(l2);
				MethodDecl decl = apply.formals(l1).methodInstance(mi);
				//  new ClassOrInterfaceType ( ArgumentListopt ) ClassBodyopt
				List classDecl = new TypedList(new LinkedList(), MethodDecl.class, false);
				for (int i = 0; i < members.size(); i++) {
					Object m = members.get(i);
					if (m != apply)
						classDecl.add(m);
					else
						classDecl.add(decl);
				}
				CanonicalTypeNode t = xnf.CanonicalTypeNode(nPos, tOperatorPointwise);
				ParsedClassType anon = n.anonType();
				// FIXME: this is probably breaking some assumptions in the typesystem
				anon.superType(tOperatorPointwise);
				return n.objectType(t).body(n.body().members(classDecl)).typeCheckOverride(null, tc);
			}
			else if (opName.equals("binaryOp"))
			{
				TypeNode t = xnf.CanonicalTypeNode(nPos, xts.OperatorBinary());
				return n.objectType(t).typeCheckOverride(null, tc);
			}
			else if (opName.equals("unaryOp"))
			{
				TypeNode t = xnf.CanonicalTypeNode(nPos, xts.OperatorUnary());
				return n.objectType(t).typeCheckOverride(null, tc);
			}
		}
		X10New_c result = (X10New_c) super.typeCheck(tc);
		result = (X10New_c) result.constructorInstance((X10ConstructorInstance) result.constructorInstance().copy());
		result = result.adjustCI(tc);
		return result;
	}

	private MethodDecl findMethod1Arg(String name, List members) {
		for (Iterator i = members.iterator(); i.hasNext();) {
			MethodDecl m = (MethodDecl) i.next();
			if (m.name().equals(name))
				return m;
		}
		return null;
	}
	 /**
     * Compute the new resulting type for the method call by replacing this and 
     * any argument variables that occur in the rettype depclause with new
     * variables whose types are determined by the static type of the receiver
     * and the actual arguments to the call.
     * @param tc
     * @return
     * @throws SemanticException
     */
    private X10New_c adjustCI(TypeChecker tc) throws SemanticException {
    	/*X10ConstructorInstance xci = (X10ConstructorInstance) ci;
    	
    	if (ci == null) return this;
    	X10Type type = (X10Type) xci.returnType();
    	X10Type retType = instantiateType(type, arguments);
    	//if (retType != type)
    	//	xci.setReturnType(retType);
    	return (X10New_c) this.type(retType);*/
    	 
    	if (ci == null) return this;
    	X10ConstructorInstance xci = (X10ConstructorInstance) ci;
    	X10Type type = (X10Type) xci.returnType();
    	
    	if (body != null) {
    		// If creating an anonymous class, we need to adjust the return type
    		// to be based on anonType rather than on the supertype.
    		X10ParsedClassType anonType = (X10ParsedClassType) anonType();
    		type = anonType.makeDepVariant(type.depClause(), type.typeParameters());
    	}

    	// FIXME: if the constructor has a depclause, make sure it propagates to the instance
    	X10Type retType = instantiateType(type, arguments);
    	if (type != retType) {
    		xci.setReturnType(retType);
    	}
    	if (xci.whereClause() != null) {
    		Constraint where = X10New_c.instantiateConstraint(xci.whereClause(), null, arguments);
    		if (! where.consistent()) {
    			throw new SemanticException("Constructor invocation inconsistent with arguments.", position());
    		}
    		xci.setWhereClause(where);
    	}
    	return (X10New_c) this.type(retType);
    }
    /**
     * Invoke instantiateType(X10Type, X10Type, List<Expr>) with the given arguments
     * and with thisType==null.
     * @param formalReturnType
     * @param arguments
     * @return
     * @throws SemanticException 
     */
    public static X10Type instantiateType(X10Type formalReturnType, List<Expr> arguments) throws SemanticException {
    	return instantiateType(formalReturnType, null, arguments);
    }
    /**
     * Replace occurrences of formal parameters -- if any -- in the given formalReturnType
     * with C_Vars computed from the corresponding argument in the given list of expressions.
     * For each formal parameter, the C_Var is the actual argument a (if a is rigid), 
     * and an EQV of the same type as a if a is not rigid. 
     * @param formalReturnType
     * @param arguments
     * @return
     * @throws SemanticException 
     */
   public static X10Type instantiateType(X10Type formalReturnType, Receiver target, 
		   List<Expr> arguments) throws SemanticException {
	   //Report.report(1, "X10new_c: instantiatetype "  + formalReturnType + "args=" + arguments);
    	X10Type retType = formalReturnType;
    	Constraint rc = formalReturnType.realClause();
    	if (rc == null) return retType;
    	Constraint newRC = instantiateConstraint(rc, target, arguments);

    	if (newRC != rc) {
    		retType = formalReturnType.makeVariant(newRC, null);
    	}
	    return retType;
   }

	public static Constraint instantiateConstraint(Constraint rc,
			Receiver target, List<Expr> arguments) throws SemanticException {
    	
		if (rc == null) return null;
    	
    	// Replace each method parameter in the return type 
    	// by a parameter constructed from the actual argument.
    	Map<C_Var,Promise> m = rc.roots();
    	if (m == null) return rc;
    	
    	Set<C_Var> vars = m.keySet();
    	HashMap<C_Root, C_Var> subs = new HashMap<C_Root, C_Var>();
    	// If an actual parameter is a variable v, and the variable 
    	// occurs multiple times in the actual argument list, we need 
    	// to ensure that only one EQV is generated for v. This is 
    	// important because the multiple positions in the arg list in 
    	// which v occurs may correspond to final parameters in the formal
    	// parameter list, say x and y, and these final parameters may be used in the
    	// return type. We need to replace all these parameters in the return
    	// type with the same EQV, vv. Thus a test in the return type of the form
    	// x==y will succeed, as it should. 
    	
    	HashMap<LocalInstance, C_Var> renaming = new HashMap<LocalInstance, C_Var>();
    	for (Iterator<C_Var> it = vars.iterator(); it.hasNext();) {
    		C_Root var = (C_Root) it.next();
    		if (var.equals(C_Special.This)) {
    			//assert(target != null && target instanceof Expr);
    			if (target != null) {
    				C_Var realThis = rc.selfVar((Expr) target, false);
    				subs.put(var, realThis);
    			}
    		} else 	if (var instanceof C_Local){
    			X10LocalInstance li = ((C_Local) var).localInstance();
    			assert li != null;
    			int p = li.positionInArgList();
    			if (p >= 0) {
    				Expr arg = arguments.get(p);
    				C_Var realVar = null;
    				if (arg instanceof Local_c) {
    					Local_c actual = (Local_c) arg;
    					realVar = renaming.get(actual.localInstance());
    					if (realVar == null) {
        					realVar = rc.selfVar(arg, false); 
        					renaming.put(actual.localInstance(), realVar);
        				}
    				}
    				else if (arg instanceof Lit) {
    					try {
    						X10TypeSystem xts = (X10TypeSystem) li.typeSystem();
    						C_Lit t = (C_Lit) new TypeTranslator(xts).trans(arg);
    						realVar = t;
    					}
    					catch (SemanticException e) {
    					}
    				}
    				if (realVar == null) {
    					realVar = rc.selfVar(arg, false); 
    				}
    				
    				subs.put(var, realVar);
    			}
    		}
    	}
    	Constraint newRC = rc;
    	if (! subs.isEmpty()) {
    		try {
    			newRC = rc.substitute(subs);
    		}
    		catch (Failure f) {
    			throw new SemanticException("Constraint is possibly inconsistent with respect to the actual arguments: " + f.getMessage());
    		}
    	}
    	return newRC;
	}
}

