/**
 * 
 */
package x10.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import polyglot.types.SemanticException;
import polyglot.types.Type;
import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XRoot;
import x10.constraint.XTerm;

public
class Subst {
    public
        static Type subst(Type t, Type[] Y, ParameterType[] X) throws SemanticException {
            assert Y.length == X.length;
            
            for (int i = 0; i < X.length; i++) {
                if (Y[i] == null)
                    throw new SemanticException("Cannot infer type for parameter " + X[i] + ".");
                t = subst(t, Y[i], X[i]);
            }
            return t;
        }
        
    public
        static Type subst(Type t, XTerm[] y, XRoot[] x) throws SemanticException {
            assert y.length == x.length;
            
            if (t == null)
                return null;
            
            X10TypeSystem ts = (X10TypeSystem) t.typeSystem();

            t = ts.expandMacros(t);
            
            Type base = X10TypeMixin.baseType(t);
            XConstraint c = X10TypeMixin.xclause(t);
            
            if (t instanceof X10ParsedClassType) {
                X10ParsedClassType ct = (X10ParsedClassType) t;
                List<Type> newArgs = new ArrayList<Type>();
                for (Type at : ct.typeArguments()) {
                    Type at2 = subst(at, y, x);
                    newArgs.add(at2);
                }
                if (! newArgs.isEmpty())
                    return ct.typeArguments(newArgs);
            } else 
            if (c != null) {
                base = subst(base, y, x);

                try {
                    c = c.substitute(y, x);
//                  c = c.saturate();
                }
                catch (XFailure e) {
                    throw new SemanticException("Cannot instantiate formal parameters on actuals.");
                }
                
                return X10TypeMixin.xclause(base, c);
            }

            
            return t;
        }
        

        public static Type subst(Type t, XTerm[] y, XRoot[] x, Type[] Y, ParameterType[] X) throws SemanticException {
            if (t instanceof ConstrainedType) {
                Type ct = t;
                Type base = X10TypeMixin.baseType(ct);
                XConstraint c = X10TypeMixin.xclause(ct);
                Type newBase = subst(base, y, x, Y, X);
                // if (x instanceof XSelf) {
                // return X10TypeMixin.xclause(newBase, c);
                // }
                return X10TypeMixin.xclause(newBase, subst(c, y, x, Y, X));
            }
            if (t instanceof ParameterType) {
                for (int i = 0; i < X.length; i++) {
                    if (TypeParamSubst.isSameParameter((ParameterType) t, X[i]))
                        return Y[i];
                }
                return t;
            }
            if (t instanceof X10ClassType) {
                X10ClassType ct = (X10ClassType) t;
                if (ct.isIdentityInstantiation()) {
                    List<Type> args = new ArrayList<Type>();
                    boolean changed = false;
                    for (Type ti : ct.x10Def().typeParameters()) {
                        Type ti2 = subst(ti, y, x, Y, X);
                        if (ti2 != ti)
                            changed = true;
                        args.add(ti2);
                    }
                    if (changed)
                        return ct.typeArguments(args);
                    return ct;
                }
                else {
                    List<Type> args = new ArrayList<Type>();
                    boolean changed = false;
                    for (Type ti : ct.typeArguments()) {
                        Type ti2 = subst(ti, y, x, Y, X);
                        if (ti2 != ti)
                            changed = true;
                        args.add(ti2);
                    }
                    if (changed)
                        return ct.typeArguments(args);
                    return ct;
                }
            }
            if (t instanceof MacroType) {
                MacroType mt = (MacroType) t;
                return subst(mt.definedType(), y, x, Y, X);
            }
            return t;
        }
        
        public
        static Type subst(Type t, XTerm y, XRoot x) throws SemanticException {
            return subst(t, new XTerm[] { y }, new XRoot[] { x });
        }
        
        public
        static Type subst(Type t, Type Y, ParameterType X) throws SemanticException {
            return subst(t, new XTerm[] {}, new XRoot[] { }, new Type[] { Y }, new ParameterType[] { X });
        }
        
        public
        static XConstraint subst(XConstraint t, XTerm y, XRoot x) throws SemanticException {
            return subst(t, new XTerm[] { y }, new XRoot[] { x }, new Type[0], new ParameterType[0]);
        }
        
        public
        static XConstraint subst(XConstraint t, Type Y, ParameterType X) throws SemanticException {
            return t;
        }
        
        public
        static TypeConstraint subst(TypeConstraint t, XTerm y, XRoot x) throws SemanticException {
            if (t == null)
                return null;
            return t.subst(y, x);
        }
        
        static TypeConstraint subst(TypeConstraint t, Type Y, ParameterType X) throws SemanticException {
            if (t == null)
                return null;
            TypeParamSubst subst = new TypeParamSubst((X10TypeSystem) Y.typeSystem(), Arrays.asList(Y), Arrays.asList(X));
            return subst.reinstantiate(t);
        }
        
        static TypeConstraint subst(TypeConstraint t, XTerm[] y, XRoot[] x) throws SemanticException {
            assert y.length == x.length;
            
            if (t == null)
                return null;
            
            for (int i = 0; i < x.length; i++)
                t = subst(t, y[i], x[i]);
            
            return t;
        }
        
        static TypeConstraint subst(TypeConstraint t, Type[] Y, ParameterType[] X) throws SemanticException {
            assert Y.length == X.length;
            
            if (t == null)
                return null;
            
            for (int i = 0; i < X.length; i++) {
                if (Y[i] == null)
                    throw new SemanticException("Cannot infer type for parameter " + X[i] + ".");
                t = subst(t, Y[i], X[i]);
            }
            
            return t;
        }
        
        static TypeConstraint subst(TypeConstraint t, XTerm[] y, XRoot[] x, Type[] Y, ParameterType[] X) throws SemanticException {
            TypeConstraint t2 = subst(t, y, x);
            TypeConstraint t3 = subst(t2, Y, X);
            return t3;
        }
        
        static XConstraint subst(XConstraint t, XTerm[] y, XRoot[] x) throws SemanticException {
            if (t == null)
                return null;
            
            try {
                XConstraint c = t.substitute(y, x);
                return c;
            }
            catch (XFailure e) {
                throw new SemanticException("Cannot instantiate formal parameters on actuals.");
            }
        }
        static XConstraint subst(XConstraint t, Type[] Y, ParameterType[] X) throws SemanticException {
            if (t == null)
                return null;
            
            return t;
        }
        
        static XConstraint subst(XConstraint t, XTerm[] y, XRoot[] x, Type[] Y, ParameterType[] X) throws SemanticException {
            XConstraint t2 = subst(t, y, x);
            XConstraint t3 = subst(t2, Y, X);
            return t3;
        }
    }