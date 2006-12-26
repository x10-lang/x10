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
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Formal;
import polyglot.types.MethodInstance_c;
import polyglot.types.TypeSystem_c;
import polyglot.ext.x10.types.constr.C_Local_c;
import polyglot.ext.x10.types.constr.C_Root;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Special_c;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.C_Var_c;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.types.constr.Promise;
import polyglot.main.Report;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;

/**
 * A representation of a MethodInstance. This implements the requirement that method
 * annotations such as sequential, local, nonblocking, safe are preserved on overriding.
 * @author vj
 *
 */
public class X10MethodInstance_c extends MethodInstance_c implements X10MethodInstance {

	List<Formal> formals;
	/**
	 * 
	 */
	public X10MethodInstance_c() {
		super();
	
	}

	/**
	 * @param ts
	 * @param pos
	 * @param container
	 * @param flags
	 * @param returnType
	 * @param name
	 * @param formalTypes
	 * @param excTypes
	 */
	public X10MethodInstance_c(TypeSystem ts, Position pos,
			ReferenceType container, Flags flags, Type returnType, String name,
			List formalTypes,   List excTypes) {
		super(ts, pos, container,flags, returnType, name, formalTypes,	excTypes);
		
	}
	
	public boolean canOverrideImpl(MethodInstance mj, boolean quiet) throws SemanticException {
		//  Report.report(1, "X10MethodInstance_c: " + this + " canOverrideImpl " + mj);
		boolean result = super.canOverrideImpl(mj, quiet);
		MethodInstance mi = this;
		X10Flags miF = X10Flags.toX10Flags(mi.flags());
		X10Flags mjF = X10Flags.toX10Flags(mj.flags());
		if (result) {
			// Report.report(1, "X10MethodInstance_c: " + this + " canOVerrideImpl " + mj);
			if (! miF.hasAllAnnotationsOf(mjF)) {
				if (Report.should_report(Report.types, 3))
					Report.report(3, mi.flags() + " is more liberal than " +
							mj.flags());
				if (quiet) return false;
				throw new SemanticException(mi.signature() + " in " + mi.container() +
						" cannot override " + 
						mj.signature() + " in " + mj.container() + 
						"; attempting to assign weaker " + 
						"behavioral annotations", 
						mi.position());
			}
		}
		return result;
	}
	public boolean isJavaMethod() {
		assert container instanceof X10ParsedClassType;
		boolean result = ((X10ParsedClassType) container).isJavaType();
		return result;
	}
	public boolean isSafe() {
		assert container instanceof X10ParsedClassType;
		boolean result = ((X10ParsedClassType) container).safe();
		if (result) return true;
		X10Flags f = X10Flags.toX10Flags(flags());
		result = f.isSafe();
		return result;
	}
	protected static String myListToString(List l) {
		StringBuffer sb = new StringBuffer();
		
		for (Iterator i = l.iterator(); i.hasNext(); ) {
			Object o = i.next();
			sb.append(o.toString());
			
			if (i.hasNext()) {
				sb.append(", ");
			}
		}
		
		return sb.toString();
	}
	
	public X10MethodInstance instantiateForThis(X10Type thisType ) {
		boolean needed = false;
		X10Type retType = (X10Type) returnType();
		X10Type newRetType = retType;
		Constraint rc = retType.realClause();
		C_Special THIS = C_Special_c.This;
		C_Var selfVar = thisType.selfVar();
		/*if (rc!=null)
			needed = rc.hasVar(THIS);
		if (needed) {
			C_Var myVar = selfVar;
			if (myVar == null) myVar = rc.genEQV(thisType, true);
			Constraint rc2 = rc.substitute(myVar, THIS, true);
			newRetType = retType.makeVariant(rc2,null);
		}*/
		for (Iterator<X10Type> it = formalTypes().iterator(); !needed && it.hasNext();) {
			X10Type type = it.next();
			rc = type.realClause();
			if (rc != null)
				needed = rc.hasVar(THIS);
		}
		if (!needed) return this;
		List<X10Type> newFormalTypes = new ArrayList(formalTypes.size());
		for (Iterator<X10Type> it = formalTypes().iterator(); it.hasNext();) {
			X10Type type = it.next();
			rc = type.realClause();
			X10Type newType = type;
			if (rc!=null && rc.hasVar(THIS)) {
				C_Var myVar = selfVar;
				if (myVar == null) myVar = rc.genEQV(type, true); // not thisType for the args.
				Constraint rc2 = rc.substitute(selfVar, THIS, false);
				newType = type.makeVariant(rc2,null);
			}
			newFormalTypes.add(newType);
		}
		X10MethodInstance result = new X10MethodInstance_c(ts, position(),
				container, flags, newRetType, name,
				 newFormalTypes,  throwTypes());
		return result;
	}
	  /** Returns true if a call can be made with the given argument types. 
	   * Specialized to deal with the possibility of dependent types in the 
	   * arguments of the method which may contain references to arguments
	   * to the method.
	   * TODO: Take into account the deptype in the parameter list of the method.
	   */
	@Override
    public boolean callValidImpl(final List argTypes) {
		return X10MethodInstance_c.callValidImpl(this, argTypes);
	}
	public static boolean callValidImpl(final X10ProcedureInstance me, final List<X10Type> args) {
    	boolean result = me.callValidImplNoClauses(args);
    	if (!result) return result;
    	final List<X10Type> formals = me.formalTypes();
    	final int n = formals.size();
    	// Check quickly if you need to test for entailment.
    	boolean typesNotDep = true;
    	for (int i=0; typesNotDep && i < n; i++) {
    		Constraint d = formals.get(i).realClause();
    		typesNotDep = d==null || d.valid();
    	}
    	if (typesNotDep) return true;
    	// There is a formal argument with a non-vacuous deptype.
    	Constraint env  = new Constraint_c();
    	C_Root[] x = new C_Root[n];
    	C_Var[] y = new C_Var[n];
    	for (int i=0; i < n; i++) {
    		X10Type type = formals.get(i);
    		X10Type aType = args.get(i);
    		Constraint yc = aType.depClause();
    		if (yc != null) {
    			y[i] = yc.selfVar();
    		}
    		if (y[i] == null) {
    			// This must mean that y[i] was not final, hence it cant occur in 
    			// the dep clauses of downstream y[i]'s.
    			y[i] = env.genEQV(aType, false, false);
    		}
    		Constraint fc = type.depClause();
    		if (fc != null) {
    			x[i] = (C_Root) fc.selfVar();
    		}
    		if (x[i]==null) 
    			x[i] = env.genEQV(type, false, false);
    		env.internRecursively(y[i]); 
    	}
    	for (int i=0; result && (i < n); i++) {
    		Constraint query = formals.get(i).realClause();
    		if (query != null && ! query.valid()) {
    			Constraint query2 = query.substitute(y,x, false);
    			Constraint query3 = query2.substitute(y[i], C_Special_c.Self, false);
    			result = env.entails(query3);
    		}
    	}
    	return result;
    }
    public boolean callValidImplNoClauses(List argTypes) {
    	return X10MethodInstance_c.callValidImplNoClauses(this, argTypes);
    }
    public static boolean callValidImplNoClauses(X10ProcedureInstance me, List<X10Type> argTypes) {
        List l1 = me.formalTypes();
        List l2 = argTypes;
        TypeSystem ts = me.typeSystem();

        Iterator i1 = l1.iterator();
        Iterator i2 = l2.iterator();

        while (i1.hasNext() && i2.hasNext()) {
            X10Type t1 = (X10Type) i1.next();
            X10Type t2 = (X10Type) i2.next();

            if (! ts.isImplicitCastValid(t2.makeNoClauseVariant(), 
            		t1.makeNoClauseVariant())) {
                return false;
            }
        }

        return ! (i1.hasNext() || i2.hasNext());
    }
	public String toString() {
		String s = designator() + " " + flags + " " + returnType + " " +
		container() + "." + signature();
		
		if (! throwTypes.isEmpty()) {
			s += " throws " + myListToString(throwTypes);
		}
		
		return s;
	}
	
}
