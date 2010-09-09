/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package x10.types;

import java.util.List;

import polyglot.types.Flags;
import polyglot.types.LocalInstance_c;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XLocal;
import x10.constraint.XTerm;

/**
 * @author vj
 *
 */
public class X10LocalInstance_c extends LocalInstance_c implements X10LocalInstance {

    public X10LocalInstance_c(TypeSystem ts, Position pos, Ref<? extends X10LocalDef> def) {
        super(ts, pos, def);
    }
    
    public X10LocalDef x10Def() {
        return (X10LocalDef) def();
    }
    
    public List<Type> annotations() {
        return X10TypeObjectMixin.annotations(this);
    }
    public List<Type> annotationsMatching(Type t) {
        return X10TypeObjectMixin.annotationsMatching(this, t);
    }
    
    public X10LocalInstance type(Type t) {
        X10LocalInstance_c n = (X10LocalInstance_c) super.type(t);
        assert n != this;
        // clear right type so it is recomputed from type
        n.rightType = null;
        return n;
    }
    
    Type rightType;

    public Type rightType() {
        if (rightType == null) {
            Type t = type();
            
            // If the local variable is final, replace T by T{self==t}, 
            // do this even if depclause==null
            Flags flags = flags();

            if (flags.isFinal()) {
                if (t instanceof UnknownType) {
                    rightType = t;
                }
                else {
                    try {
                        XConstraint c = X10TypeMixin.xclause(t);
                        if (c == null)
                            c = new XConstraint_c();
                        else
                            c = c.copy();
                        X10TypeSystem xts = (X10TypeSystem) ts;
                        XLocal var = xts.xtypeTranslator().trans(this, t);
                        c.addSelfBinding(var);
                        rightType = X10TypeMixin.xclause(X10TypeMixin.baseType(t), c);
                    }
                    catch (SemanticException f) {
                        throw new InternalCompilerError("Could not add self binding.", f);
                    }
                    catch (XFailure f) {
                        throw new InternalCompilerError("Could not add self binding.", f);
                    }
                }
            }
            else {
                rightType = t;
            }

            assert rightType != null;
        }

        return rightType;
    }

    public String toString() {
	String s = "local " + X10Flags.toX10Flags(flags()).prettyPrint() + name() + ": " + type();
	return s;
    }

}
