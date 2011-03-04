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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import polyglot.ext.x10.ast.X10New_c;
import polyglot.ext.x10.types.constr.C_Root;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Special_c;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.types.constr.Failure;
import polyglot.main.Report;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.MethodInstance;
import polyglot.types.MethodInstance_c;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;

/**
 * A representation of a MethodInstance. This implements the requirement that method
 * annotations such as sequential, local, nonblocking, safe are preserved on overriding.
 * @author vj
 *
 */
public class X10MethodInstance_c extends MethodInstance_c implements X10MethodInstance {

	public static class NoClauseVariant implements Transformation<Type, Type> {
        public Type transform(Type o) {
            return X10TypeMixin.makeNoClauseVariant((X10Type) o);
        }
    }
    public Object copy() { 
		return super.copy();
	}
	
	public List<X10ClassType> annotations() {
	    return X10TypeObjectMixin.annotations(this);
	}
	public List<X10ClassType> annotationsMatching(Type t) {
	    return X10TypeObjectMixin.annotationsMatching(this, t);
	}
	
	public X10MethodInstance_c(TypeSystem ts, Position pos, Ref<? extends X10MethodDef> def) {
	    super(ts, pos, def);
	}
	
	/** Constraint on formal parameters. */
	protected Constraint whereClause;
	public Constraint whereClause() { return whereClause; }
	public X10MethodInstance whereClause(Constraint s) { 
	    X10MethodInstance_c n = (X10MethodInstance_c) copy();
	    n.whereClause = s; 
	    return n;
	}

	public void checkOverride(MethodInstance mj) throws SemanticException {
	    //  Report.report(1, "X10MethodInstance_c: " + this + " canOverrideImpl " + mj);
	    super.checkOverride(mj);
	    MethodInstance mi = this;
	    X10Flags miF = X10Flags.toX10Flags(mi.flags());
	    X10Flags mjF = X10Flags.toX10Flags(mj.flags());

	    // Report.report(1, "X10MethodInstance_c: " + this + " canOVerrideImpl " + mj);
	    if (! miF.hasAllAnnotationsOf(mjF)) {
	        if (Report.should_report(Report.types, 3))
	            Report.report(3, mi.flags() + " is more liberal than " +
	                          mj.flags());
	        throw new SemanticException(mi.signature() + " in " + mi.container() +
	                                    " cannot override " + 
	                                    mj.signature() + " in " + mj.container() + 
	                                    "; attempting to assign weaker " + 
	                                    "behavioral annotations", 
	                                    mi.position());
	    }
	}

	public boolean isPropertyGetter() {
		assert container instanceof X10ParsedClassType;
		if (isJavaMethod()) return false;
		if (!formalTypes.isEmpty()) return false;
		for (FieldInstance fi : container.fields()) {
		    FieldDef fd = fi.def();
		    if (fd instanceof X10FieldDef) {
		        X10FieldDef xfd = (X10FieldDef) fd;
		        if (xfd.isProperty()) {
		            return true;
		        }
		    }
		}
		return false;
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

	  /** Returns true if a call can be made with the given argument types. 
	   * Specialized to deal with the possibility of dependent types in the 
	   * arguments of the method which may contain references to arguments
	   * to the method.
	   * TODO: Take into account the deptype in the parameter list of the method.
	   */
	@Override
	public boolean callValid(List<Type> argTypes) {
		return X10MethodInstance_c.callValidImpl(this, argTypes);
	}
	
	public static boolean callValidImpl(final X10ProcedureInstance me, final List<Type> args) {
	    boolean result = me.callValidNoClauses(args);
	    if (!result) return result;
	    final List<Type> formals = me.formalTypes();
	    final int n = formals.size();
	    // Check quickly if you need to test for entailment.
	    boolean typesNotDep = true;
	    for (int i=0; typesNotDep && i < n; i++) {
	        X10Type ti = (X10Type) formals.get(i);
	        Constraint d = X10TypeMixin.realClause(ti);
	        typesNotDep = d==null || d.valid();
	    }
	    if (typesNotDep) return true;
	    
	    // There is a formal argument with a non-vacuous deptype.
	    Constraint env  = new Constraint_c((X10TypeSystem) me.typeSystem());
	    C_Root[] x = new C_Root[n];
	    C_Var[] y = new C_Var[n];
	    for (int i=0; i < n; i++) {
	        X10Type type = (X10Type) formals.get(i);
	        X10Type aType = (X10Type) args.get(i);
	        Constraint yc = X10TypeMixin.realClause(aType);
	        if (yc != null) {
	            y[i] = yc.selfVar();
	        }
	        if (y[i] == null) {
	            // This must mean that y[i] was not final, hence it cannot occur in 
	            // the dep clauses of downstream y[i]'s.
	            y[i] = env.genEQV(aType, false, false);
	        }
	        Constraint fc = X10TypeMixin.realClause(type);
	        if (fc != null && fc.selfVar() instanceof C_Root) {
	            x[i] = (C_Root) fc.selfVar();
	        }
	        if (x[i]==null) 
	            x[i] = env.genEQV(type, false, false);
	        try {
	            Constraint yc2 = yc.substitute(y[i], C_Special.Self);
	            env.addIn(yc2);
//	            env.internRecursively(y[i]); 
	        }
	        catch (Failure f) {
	            // environment is inconsistent.
	            return false;
	        }
	    }
	    for (int i=0; result && (i < n); i++) {
	        X10Type ti = (X10Type) formals.get(i);
	        Constraint query = X10TypeMixin.realClause(ti);
	        if (query != null && ! query.valid()) {
	            try {
	                Constraint query2 = query.substitute(y, x, false, new HashSet<C_Term>());
	                Constraint query3 = query2.substitute(y[i], C_Special_c.Self, false, new HashSet<C_Term>());
	                result = env.entails(query3);
	            }
	            catch (Failure f) {
	                // Substitution introduces inconsistency.
	                result = false;
	            }
	        }
	    }
	    return result;
	}

	public boolean callValidNoClauses(List<Type> argTypes) {
	    X10MethodInstance_c me = (X10MethodInstance_c) this.formalTypes(new TransformingList<Type,Type>(this.formalTypes(), new X10MethodInstance_c.NoClauseVariant()));
	    return me.superCallValid(new TransformingList<Type,Type>(argTypes, new X10MethodInstance_c.NoClauseVariant()));
	}

	protected boolean superCallValid(List<Type> argTypes) {
	    return super.callValid(argTypes);
	}


	public String signature() {
	    return name() + "(" + X10TypeSystem_c.listToString(formalTypes()) + (whereClause() != null ? ": " + whereClause().toString() : "") + ")";
	}

    public String toString() {
        String s = designator() + " " + flags() + " " + returnType() + " " + container() + "." + signature();

        if (!throwTypes().isEmpty()) {
            s += " throws " + myListToString(throwTypes());
        }

        return s;
    }

    public X10MethodDef x10Def() {
        return (X10MethodDef) def();
    }
	
}
