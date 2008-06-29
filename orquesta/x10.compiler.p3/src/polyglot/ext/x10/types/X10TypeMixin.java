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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Binary;
import polyglot.ast.Unary;
import polyglot.ast.Binary.Operator;
import polyglot.frontend.Globals;
import polyglot.types.ClassType;
import polyglot.types.FieldInstance;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XEquals;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XNameWrapper;
import x10.constraint.XPromise;
import x10.constraint.XSelf;
import x10.constraint.XTerm;
import x10.constraint.XVar;

/** 
 * An X10 dependent type.
 * @author nystrom
 */
public class X10TypeMixin {
	public static XConstraint realX(Type t) {
		if (t instanceof ConstrainedType) {
			ConstrainedType	ct = (ConstrainedType) t;
			XConstraint realClause = ct.getRealXClause();

			if (realClause == null) {
				// Set the real clause to something invalid to detect recursion.
				ct.setRealXClause(new XConstraint_c(), new SemanticException("The dependent clause is recursive.", ct.position()));

				// Now get the root clause and join it with the dep clause.
				XConstraint rootClause;

				if (ct.baseType().get() instanceof X10ClassType) {
					X10ClassType xct = (X10ClassType) ct.baseType().get();
					rootClause = xct.x10Def().getRootXClause();
				}
				else {
					 rootClause = new XConstraint_c();
				}
				
				assert rootClause != null;

				XConstraint depClause = xclause(ct);

				if (depClause == null) {
					realClause = rootClause;
				}
				else {
					realClause = rootClause.copy();

					try {
						realClause.addIn(depClause);
					}
					catch (XFailure f) {
						realClause.setInconsistent();
						SemanticException realClauseInvalid = new SemanticException("The dependent clause is inconsistent with respect to the class invariant and property constraints.", ct.position());
						ct.setRealXClause(realClause, realClauseInvalid);
						return realClause;
					}
				}

				ct.setRealXClause(realClause, null);
			}

			return realClause;
		}
		else if (t instanceof X10ClassType) {
			X10ClassType ct = (X10ClassType) t;
			return ct.x10Def().getRootXClause();
		}

		return new XConstraint_c();
	}
	public static XConstraint xclause(Type t) {
		if (t instanceof ConstrainedType) {
			ConstrainedType  ct = (ConstrainedType) t;
			return Types.get(ct.constraint());
		}
		return null;
	}
	public static Type baseType(Type t) {
	    if (t instanceof ConstrainedType) {
		ConstrainedType ct = (ConstrainedType) t;
		return baseType(Types.get(ct.baseType()));
	    }
	    return t;
	}
	public static Type xclause(Type t, XConstraint c) {
	        if (c == null || c.valid()) {
	            return baseType(t);
	        }
	        return xclause(Types.ref(t), Types.ref(c));
	}
	public static Type xclause(Ref<? extends Type> t, Ref<XConstraint> c) {
	    if (c == null) {
		c = Types.<XConstraint>ref(new XConstraint_c());
	    }
	    if (t instanceof ConstrainedType) {
		ConstrainedType ct = (ConstrainedType) t;
		return xclause(ct.baseType(), c);
	    }
	    Type tx = t.getCached();
	    assert tx != null;
	    return new ConstrainedType_c((X10TypeSystem) tx.typeSystem(), tx.position(), t, c);
	}

    public static boolean isConstrained(Type t) {
	    return t instanceof ConstrainedType;
    }
    
    public static Type addBinding(Type t, XVar t1, XVar t2) {
        try {
            XConstraint c = xclause(t);
            if (c == null) {
                c = new XConstraint_c();
            }
            XConstraint c2 = c.addBinding(t1, t2);
            return xclause(t, c2);
        }
        catch (XFailure f) {
            throw new InternalCompilerError("Cannot bind " + t1 + " to " + t2 + ".", f);
        }
    }

    public static boolean consistent(X10Type t) {
	    XConstraint c = xclause(t);
        if (c == null) return true;
        return c.consistent();
    }

    public static XVar selfVar(Type thisType) {
	    XConstraint c = xclause(thisType);
	    return selfVar(c);
    }

    public static XVar selfVar(XConstraint c) {
	    if (c == null) return null;
	    return c.bindingForVar(XSelf.Self);
    }

    public static Type setSelfVar(Type t, XVar v) throws SemanticException {
        XConstraint c = xclause(t);
        if (c == null) {
            c = new XConstraint_c();
        }
        else {
            c = c.copy();
        }
        try {
		c.addSelfBinding(v);
	}
	catch (XFailure e) {
		throw new SemanticException(e.getMessage(), t.position());
	}
        return xclause(t, c);
    }

    /**
     * If the type constrains the given property to
     * a particular value, then return that value, otherwise 
     * return null
     * @param name -- the name of the property.
     * @return null if there is no value associated with the property in the type.
     */
    public static XTerm propVal(X10Type t, String name) {
        XConstraint c = xclause(t);
        if (c == null) return null;
        try {
		return c.find(new XNameWrapper<String>(name));
	}
	catch (XFailure e) {
		return null;
	}
    }
    
    
    // Helper functions for the various type tests
    // At the top of test foo(o), put:
    // if (Mixin.isConstrained(this) || Mixin.isParametric(this) || Mixin.isConstrained(o) || Mixin.isParametric(o))
    //     return Mixin.foo(this, o)
    // ...
    
    public static boolean eitherIsDependent(X10Type t1, X10Type t2) {
    	return isDependentOrDependentPath(t1) || isDependentOrDependentPath(t2);
    }
    
    public static boolean isDependentOrDependentPath(Type t) {
    	return isConstrained(t);
    }

    public static boolean equalsIgnoreClause(X10Type t1, X10Type t2) {
        return t1.typeEquals(t2);
    }
    public static X10PrimitiveType promote(Unary.Operator op, X10PrimitiveType t) throws SemanticException {
        TypeSystem ts = t.typeSystem();
        X10PrimitiveType pt = (X10PrimitiveType) ts.promote(t);
        return (X10PrimitiveType) xclause(pt, promoteClause(op, xclause(t)));
    }

    public static XConstraint promoteClause(polyglot.ast.Unary.Operator op, XConstraint c) {
        if (c == null)
            return null;
        X10TypeSystem ts = (X10TypeSystem) Globals.TS();
        return ts.xtypeTranslator().unaryOp(op, c);
    }

    public static X10PrimitiveType promote(Binary.Operator op, X10PrimitiveType t1, X10PrimitiveType t2) throws SemanticException {
        TypeSystem ts = t1.typeSystem();
        X10PrimitiveType pt = (X10PrimitiveType) ts.promote(t1, t2);
        return (X10PrimitiveType) xclause(pt, promoteClause(op, xclause(t1), xclause(t2)));
    }

    public static XConstraint promoteClause(Operator op, XConstraint c1, XConstraint c2) {
        if (c1 == null || c2 == null)
            return null;
        X10TypeSystem ts = (X10TypeSystem) Globals.TS();
        return ts.xtypeTranslator().binaryOp(op, c1, c2);
    }

	public static Type lookupTypeProperty(XConstraint c, TypeProperty p) {
		if (c == null)
			return null;
		try {
			XPromise x = c.lookup(p.asVar());
			if (x != null && x.term() instanceof XLit) return (Type) ((XLit) x.term()).val();
		}
		catch (XFailure e) {
		}
		for (XTerm t : c.constraints()) {
			if (t instanceof XEquals) {
				XEquals eq = (XEquals) t;
				XTerm v1 = eq.left();
				XTerm v2 = eq.right();
				if (v1.equals(p.asVar()) && v2 instanceof XLit) {
					return (Type) ((XLit) v2).val();
				}
				if (v2.equals(p.asVar()) && v1 instanceof XLit) {
					return (Type) ((XLit) v1).val();
				}
			}
		}
		return null;
	}
	
	public static Type getParameterType(Type theType, String prop) {
		XConstraint c = realX(theType);
		if (c == null)
			return null;
		return getParameterType(theType, c, prop);
	}

	private static Type getParameterType(Type theType, XConstraint c, String prop) {
		ClassType ct = theType.toClass();
		if (ct != null) {
			X10ClassDef def = (X10ClassDef) ct.def();
			for (TypeProperty p : def.typeProperties()) {
				if (p.name().equals(prop)) {
					Type S = X10TypeMixin.lookupTypeProperty(c, p);
					return S;
				}
			}
			Type sup = ct.superClass();
			if (sup != null)
				return getParameterType(sup, c, prop);
		}
		return null;
	}
	
	public static void checkRealClause(Type t) throws SemanticException {
		if (t instanceof ConstrainedType) {
			ConstrainedType ct = (ConstrainedType) t;
			ct.checkRealClause();
		}
	}
	public static List<FieldInstance> properties(Type t) {
		if (t instanceof ConstrainedType) {
			ConstrainedType ct = (ConstrainedType) t;
			return properties(ct.baseType().get());
		}
		if (t instanceof MacroType) {
			MacroType mt = (MacroType) t;
			return properties(mt.definedType());
		}
		if (t instanceof X10ClassType) {
			X10ClassType ct = (X10ClassType) t;
			return ct.properties();
		}
		return Collections.EMPTY_LIST;
	}
	public static List<TypeProperty> typeProperties(Type t) {
		if (t instanceof ConstrainedType) {
			ConstrainedType ct = (ConstrainedType) t;
			return typeProperties(ct.baseType().get());
		}
		if (t instanceof MacroType) {
			MacroType mt = (MacroType) t;
			return typeProperties(mt.definedType());
		}
		if (t instanceof X10ClassType) {
			X10ClassType ct = (X10ClassType) t;
			X10ClassDef def = ct.x10Def();
			Type sup = Types.get(def.superType());
			List<TypeProperty> ps = new ArrayList<TypeProperty>();
			if (sup != null) {
				ps.addAll(typeProperties(sup));
			}
			ps.addAll(ct.x10Def().typeProperties());
			return ps;
		}
		return Collections.EMPTY_LIST;
	}
}
