/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.types;

import java.util.*;

import polyglot.types.TypeSystem_c.TypeEquals;
import polyglot.util.*;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;
import x10.types.ClosureInstance;
import x10.types.X10ProcedureInstance;
import x10.types.MethodInstance;

public abstract class ProcedureInstance_c<T extends ProcedureDef> extends Use_c<T> implements X10ProcedureInstance<T> {
    private static final long serialVersionUID = -5028005051545234620L;

    protected ProcedureInstance_c(TypeSystem ts, Position pos, Position errorPos, Ref<? extends T> def) {
        super(ts, pos,errorPos,def);
    }

    
    protected CConstraint guard;
    public CConstraint guard() {
        if (guard == null)
            return Types.get(def().guard());
        return guard;
    }

    public ProcedureInstance_c guard(CConstraint guard) {
        if (guard == this.guard) return this;
        ProcedureInstance_c n = (ProcedureInstance_c) copy();
        n.guard = guard;
        return n;
    }

    protected boolean checkGuardAtRuntime = false;
    public boolean checkConstraintsAtRuntime() { return checkGuardAtRuntime; }
    public ProcedureInstance_c checkConstraintsAtRuntime(boolean check) {
        if (check==checkGuardAtRuntime) return this;
        ProcedureInstance_c n = (ProcedureInstance_c) copy();
        n.checkGuardAtRuntime = check;
        return n;
    }

    /** Constraint on type parameters. */
    protected TypeConstraint typeGuard;
    public TypeConstraint typeGuard() {
        if (typeGuard == null)
            return Types.get(def().typeGuard());
        return typeGuard;
    }
    public ProcedureInstance_c typeGuard(TypeConstraint s) {
        if (s == this.typeGuard) return this;
        ProcedureInstance_c n = (ProcedureInstance_c) copy();
        n.typeGuard = s;
        return n;
    }


    protected List<Type> formalTypes;
    protected List<Type> throwTypes;
    
    public ProcedureInstance<T> formalTypes(List<Type> formalTypes) {
        ProcedureInstance_c<T> p = this.<ProcedureInstance_c<T>>copyGeneric();
        p.formalTypes = formalTypes;
        return p;
    }
    
    public ProcedureInstance<T> throwTypes(List<Type> throwTypes) {
        ProcedureInstance_c<T> p = this.<ProcedureInstance_c<T>>copyGeneric();
        p.throwTypes = throwTypes;
        return p;
    }
    
    public List<Type> formalTypes() {
        if (this.formalTypes == null) {
            return new TransformingList<Ref<? extends Type>, Type>(def().formalTypes(), new DerefTransform<Type>());
        }
        return this.formalTypes;
    }

   
    /**
     * Returns whether <code>this</code> is <i>more specific</i> than
     * <code>p</code>, where <i>more specific</i> is defined as JLS
     * 15.12.2.2.
     *<p>
     * <b>Note:</b> There is a fair amount of guess work since the JLS
     * does not include any info regarding Java 1.2, so all inner class
     * rules are found empirically using jikes and javac.
     */
    public boolean moreSpecific(Type container, ProcedureInstance<T> p, Context context) {
    	
    	// vj: Should never be invoked
    	assert false;
    	
        ProcedureInstance<T> p1 = this;
        ProcedureInstance<T> p2 = p;

        // rule 1:
        Type t1 = null;
        Type t2 = null;
        
        if (p1 instanceof MemberInstance<?>) {
            t1 = ((MemberInstance<?>) p1).container();
        }
        if (p2 instanceof MemberInstance<?>) {
            t2 = ((MemberInstance<?>) p2).container();
        }
        
        if (t1 != null && t2 != null) {
            if (t1.isClass() && t2.isClass()) {
                if (! t1.isSubtype(t2, context) &&
                        ! t1.toClass().isEnclosed(t2.toClass())) {
                    return false;
                }
            }
            else {
                if (! t1.isSubtype(t2, context)) {
                    return false;
                }
            }
        }

        // rule 2:
        // if the formal params of p1 can be used to call p2, p1 is more specific
        return p2.callValid(t1, p1.formalTypes(), context);
    }
    
    /** Returns true if the procedure has the given formal parameter types. */
    public boolean hasFormals(List<Type> formalTypes, Context context) {
        return CollectionUtil.allElementwise(this.formalTypes(), formalTypes, new TypeEquals(context));
    }


    /** Returns true if a call can be made with the given argument types. */
    public boolean callValid(Type thisType, List<Type> argTypes, Context context) {
        List<Type> l1 = this.formalTypes();
        List<Type> l2 = argTypes;

        Iterator<Type> i1 = l1.iterator();
        Iterator<Type> i2 = l2.iterator();

        while (i1.hasNext() && i2.hasNext()) {
            Type t1 = (Type) i1.next();
            Type t2 = (Type) i2.next();

            if (! ts.isImplicitCastValid(t2, t1, context)) {
                return false;
            }
        }

        return ! (i1.hasNext() || i2.hasNext());
    }

    public String designator() {
        return def().designator();
    }
    
    public String signature() {
        StringBuilder sb = new StringBuilder();
        List<String> formals = new ArrayList<String>();
        if (formalTypes != null) {
            for (int i = 0; i < formalTypes.size(); i++) {
                String s = formalTypes.get(i).toString();
                formals.add(s);
            }
        }
        else {
            for (int i = 0; i < def().formalTypes().size(); i++) {
                formals.add(def().formalTypes().get(i).toString());
            }
        }
        sb.append("(");
        sb.append(CollectionUtil.listToString(formals));
        sb.append(")");
        return sb.toString();
    }

    public List<Type> throwTypes() {
        if (this.throwTypes == null) {
            return new TransformingList<Ref<? extends Type>, Type>(def().throwTypes(), new DerefTransform<Type>());
        }
        return this.throwTypes;
    }

    /** Returns true iff <code>this</code> throws fewer exceptions than
     * <code>p</code>. */
    public boolean throwsSubset(ProcedureInstance<T> p) {

        SubtypeSet s1 = new SubtypeSet(ts.CheckedThrowable());
        SubtypeSet s2 = new SubtypeSet(ts.CheckedThrowable());

        s1.addAll(this.throwTypes());
        s2.addAll(p.throwTypes());

        for (Iterator<Type> i = s1.iterator(); i.hasNext(); ) {
            Type t = (Type) i.next();
            if (! ts.isUncheckedException(t) && ! s2.contains(t)) {
                return false;
            }
        }

        return true;
    }

   
}
