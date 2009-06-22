/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Apr 18, 2005
 */
package polyglot.ext.x10.types;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Binary;
import polyglot.ast.Unary;
import polyglot.ast.Binary.Operator;
import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.types.constr.C_Field_c;
import polyglot.ext.x10.types.constr.C_Here_c;
import polyglot.ext.x10.types.constr.C_Lit;
import polyglot.ext.x10.types.constr.C_Lit_c;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Special_c;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.types.constr.Failure;
import polyglot.frontend.Globals;
import polyglot.types.ClassType;
import polyglot.types.DerefTransform;
import polyglot.types.FieldInstance;
import polyglot.types.PrimitiveType;
import polyglot.types.Ref;
import polyglot.types.Resolver;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.TypeSystem_c;
import polyglot.types.Type_c;
import polyglot.types.Types;
import polyglot.util.CollectionUtil;
import polyglot.util.Copy;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import polyglot.util.TypedList;
import polyglot.visit.Translator;

/** 
 * An X10 dependent type.
 * @author nystrom
 */
public class X10TypeMixin {
    public static Constraint depClause(X10Type t) {
        return Types.get(t.getDepClause());
    }

    public static List<Type> typeParameters(X10Type t) {
        if (t.getTypeParams() == null) return null;
        return new TransformingList<Ref<? extends Type>, Type>(t.getTypeParams(), new DerefTransform<Type>());
    }
    
    public static List<Ref<? extends Type>> typeParamRefs(X10Type t) {
        return t.getTypeParams();
    }
    
    public static <T extends X10Type> T depClauseDeref(T t, Constraint c) {
        return depClause(t, Types.ref(c));
    }
    
    public static <T extends X10Type> T depClause(T t, Ref<? extends Constraint> c) {
        T t2 = (T) t.copy();
        t2.setDepClause(c);
        return t2;
    }
    
    public static <T extends X10Type> T makeDepVariant(T t, Constraint d) {
        return depClauseDeref(t, d);
    }

    public static <T extends X10Type> T makeVariant(T t, Constraint c, List<Type> l) {
        return depClauseDeref(typeParamsDeref(t, l), c);
    }
    
    public static <T extends X10Type> T typeParamsDeref(T t, List<Type> l) {
        if (l == null) {
            if (t.getTypeParams() == null)
                return t;
            else
                return typeParams(t, null);
        }
        assert l != null;
        return typeParams(t, new TransformingList<Type, Ref<? extends Type>>(l, new Transformation<Type, Ref<? extends Type>>() {
            public Ref<? extends Type> transform(Type o) {
                return Types.ref(o);
            }
        }));
    }

    public static <T extends X10Type> T rootType(T t) { return makeNoClauseVariant(t); }
    
    public static <T extends X10Type> T makeNoClauseVariant(T t) {
        return makeVariant(t, new Constraint_c((X10TypeSystem) t.typeSystem()), Collections.EMPTY_LIST);
    }
    
    public static <T extends X10Type> T typeParams(T t, List<Ref<? extends Type>> l) {
        assert l != null;
        T t2 = (T) t.copy();
        t2.setTypeParams(l);
        return t2;
    }

    public static boolean isConstrained(X10Type t) {
        Ref<? extends Constraint> c = t.getDepClause();
        return c != null && Types.get(c) != null && ! Types.get(c).valid();
    }
    
    public static boolean isParametric(X10Type t) {
        List<Ref<? extends Type>> l = t.getTypeParams();
        return l != null && ! l.isEmpty();
    }
    
    /** The class invariant conjoined with the constraints on the class's properties. */
    public static Constraint realClause(X10Type t) {
        Constraint realClause = t.getRealClause();
        
        if (realClause == null) {
            // Set the real clause to something invalid to detect recursion.
            t.setRealClause(new Constraint_c((X10TypeSystem) t.typeSystem()), new SemanticException("The dependent clause is recursive.", t.position()));
            
            // Now get the root clause and join it with the dep clause.
            Constraint rootClause = t.getRootClause();
            assert rootClause != null;
            
            Constraint depClause = depClause(t);
            
            if (depClause == null) {
                realClause = rootClause;
            }
            else {
                realClause = rootClause.copy();
                
                try {
                    realClause.addIn(depClause);
                }
                catch (Failure f) {
                    realClause.setInconsistent();
                    SemanticException realClauseInvalid = new SemanticException("The dependent clause is inconsistent with respect to the class invariant and property constraints.", t.position());
                    t.setRealClause(realClause, realClauseInvalid);
                    return realClause;
                }
            }

            t.setRealClause(realClause, null);
        }

        return realClause;
    }

    public static <T extends X10Type> T addBinding(T t, C_Var t1, C_Var t2) {
        try {
            Constraint c = depClause(t);
            if (c == null) {
                X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
                c = new Constraint_c(xts);
            }
            Constraint c2 = c.addBinding(t1, t2);
            return depClauseDeref(t, c2);
        }
        catch (Failure f) {
            throw new InternalCompilerError("Cannot bind " + t1 + " to " + t2 + ".", f);
        }
    }

    public static boolean consistent(X10Type t) {
        Constraint c = depClause(t);
        if (c == null) return true;
        return c.consistent();
    }

    public static C_Var selfVar(X10Type t) {
        Constraint c = depClause(t);
        if (c == null) return null;
        return c.selfVar();
    }

    public static <T extends X10Type> T setSelfVar(T t, C_Var v) {
        Constraint c = depClause(t);
        if (c == null) {
            X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
            c = new Constraint_c(xts);
        }
        else {
            c = c.copy();
        }
        c.setSelfVar(v);
        return depClauseDeref(t, c);
    }

    /**
     * If the type constrains the given property to
     * a particular value, then return that value, otherwise 
     * return null
     * @param name -- the name of the property.
     * @return null if there is no value associated with the property in the type.
     */
    public static C_Term propVal(X10Type t, String name) {
        Constraint c = depClause(t);
        if (c == null) return null;
        return c.find(name);
    }
    
    
    // Helper functions for the various type tests
    // At the top of test foo(o), put:
    // if (Mixin.isConstrained(this) || Mixin.isParametric(this) || Mixin.isConstrained(o) || Mixin.isParametric(o))
    //     return Mixin.foo(this, o)
    // ...
    
    public static boolean eitherIsDependent(X10Type t1, X10Type t2) {
        return isConstrained(t1) || isConstrained(t2) ||  isParametric(t1) || isParametric(t2);
    }

    public static boolean equalsIgnoreClause(X10Type t1, X10Type t2) {
        return makeNoClauseVariant(t1).typeEquals(makeNoClauseVariant(t2));
    }
    
    public static boolean descendsFrom(X10Type t, X10Type o) {
        return makeNoClauseVariant(t).descendsFrom(makeNoClauseVariant(o));
    }

    public static boolean typeEquals(X10Type t, X10Type o) {
        if (t == o) 
            return true;

        if (isConstrained(t) && isConstrained(o)) {
            if (! depClause(t).equals(depClause(o)))
                return false;
        }
        
        if (isParametric(t) && isParametric(o)) {
            if (! CollectionUtil.equals(typeParameters(t), typeParameters(o), new TypeSystem_c.TypeEquals()))
                return false;
        }

        return ! isConstrained(t) && ! isConstrained(o)
            && ! isParametric(t) && ! isParametric(o);
    }

    public static boolean isCastValid(X10Type fromType, X10Type toType) {
        X10TypeSystem xts = (X10TypeSystem) fromType.typeSystem();

        if (!makeNoClauseVariant(fromType).isCastValid(makeNoClauseVariant(toType)))
            return false;

        Constraint c1 = isConstrained(fromType) ? realClause(fromType) : new Constraint_c(xts);
        Constraint c2 = isConstrained(toType) ? realClause(toType) : new Constraint_c(xts);
        
        if (fromType.isPrimitive() && toType.isPrimitive()) {
            if (!xts.primitiveClausesConsistent(c1, c2))
                return false;
        }
        else {
            if (!xts.clausesConsistent(c1, c2))
                return false;
        }

        if (isParametric(fromType) && isParametric(toType)) {
            if (! CollectionUtil.equals(typeParameters(fromType), typeParameters(toType), new TypeSystem_c.TypeEquals()))
                return false;
        }
        else if (isParametric(fromType)) {
            return false;
        }
        else if (isParametric(toType)) {
            return false;
        }

        return true;
    }
    
    public static boolean isImplicitCastValid(X10Type fromType, X10Type toType) {
        X10TypeSystem xts = (X10TypeSystem) fromType.typeSystem();
        
        if (!makeNoClauseVariant(fromType).isImplicitCastValid(makeNoClauseVariant(toType)))
            return false;

        Constraint c1 = isConstrained(fromType) ? realClause(fromType) : new Constraint_c(xts);
        Constraint c2 = isConstrained(toType) ? realClause(toType) : new Constraint_c(xts);

        if (!xts.clauseImplicitCastValid(c1, c2))
            return false;
        
        if (isParametric(fromType) && isParametric(toType)) {
            if (! CollectionUtil.equals(typeParameters(fromType), typeParameters(toType), new TypeSystem_c.TypeEquals()))
                return false;
        }
        else if (isParametric(fromType)) {
            return false;
        }
        else if (isParametric(toType)) {
            return false;
        }
        
        return true;
    }
    
    public static boolean isSubtype(X10Type fromType, X10Type toType) {
        X10TypeSystem xts = (X10TypeSystem) fromType.typeSystem();
        
        if (!makeNoClauseVariant(fromType).isSubtype(makeNoClauseVariant(toType)))
            return false;
        
        Constraint c1 = isConstrained(fromType) ? realClause(fromType) : new Constraint_c(xts);
        Constraint c2 = isConstrained(toType) ? realClause(toType) : new Constraint_c(xts);

        if (!xts.clauseImplicitCastValid(c1, c2))
            return false;
        
        if (isParametric(fromType) && isParametric(toType)) {
            if (! CollectionUtil.equals(typeParameters(fromType), typeParameters(toType), new TypeSystem_c.TypeEquals()))
                return false;
        }
        else if (isParametric(fromType)) {
            return false;
        }
        else if (isParametric(toType)) {
            return false;
        }
        
        return true;
    }

    public static String clauseToString(X10Type t) {
        StringBuffer sb = new StringBuffer();
        if (t.getTypeParams() != null) {
            String p = t.getTypeParams().toString();
            if (p.length() >= 3) {
                sb.append("<");
                sb.append(p.substring(1,p.length()-1));
                sb.append(">");
            }
        }
        if (t.getDepClause() != null)
            sb.append(t.getDepClause().toString());
        return sb.toString();
    }

    public static X10PrimitiveType promote(Unary.Operator op, X10PrimitiveType t) throws SemanticException {
        TypeSystem ts = t.typeSystem();
        X10PrimitiveType pt = (X10PrimitiveType) ts.promote(t);
        return depClauseDeref((X10PrimitiveType) pt.rootType(), promoteClause(op, depClause(t)));
    }

    public static Constraint promoteClause(polyglot.ast.Unary.Operator op, Constraint c) {
        if (c == null)
            return null;
        return c.unaryOp(op);
    }

    public static X10PrimitiveType promote(Binary.Operator op, X10PrimitiveType t1, X10PrimitiveType t2) throws SemanticException {
        TypeSystem ts = t1.typeSystem();
        X10PrimitiveType pt = (X10PrimitiveType) ts.promote(t1, t2);
        return depClauseDeref((X10PrimitiveType) pt.rootType(), promoteClause(op, depClause(t1), depClause(t2)));
    }

    public static Constraint promoteClause(Operator op, Constraint c1, Constraint c2) {
        if (c1 == null || c2 == null)
            return null;
        return c1.binaryOp(op, c2);
    }
}
