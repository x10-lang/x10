/*
 * Created on Mar 1, 2007
 */
/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ClassType_c;
import polyglot.types.ConstructorInstance;
import polyglot.types.DerefTransform;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.LocalInstance;
import polyglot.types.Matcher;
import polyglot.types.MethodAsTypeTransform;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Named;
import polyglot.types.Package;
import polyglot.types.Ref;
import polyglot.types.Resolver;
import polyglot.types.Name;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.Types;
import polyglot.types.ClassDef.Kind;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import x10.constraint.XConstraint;

public class ClosureType_c extends X10ParsedClassType_c implements FunctionType {
    private static final long serialVersionUID = 2768150875334536668L;

//    protected ClosureInstance ci;

    public ClosureType_c(final X10TypeSystem ts, Position pos, final X10ClassDef def) {
	super(ts, pos, Types.ref(def));
    }
    
    public X10MethodInstance applyMethod() {
        return (X10MethodInstance) methods().get(0);
    }
    
    public Type returnType() {
        return applyMethod().returnType();
    }

    public XConstraint guard() {
        return applyMethod().guard();
    }

    public List<Type> typeParameters() {
        return applyMethod().typeParameters();
    }

    public List<LocalInstance> formalNames() {
        return applyMethod().formalNames();
    }

    public List<Type> argumentTypes() {
        return applyMethod().formalTypes();
    }

    public List<Type> throwTypes() {
        return applyMethod().throwTypes();
    }
    
    @Override
    public String toString() {
        X10MethodInstance mi = applyMethod();
        StringBuilder sb = new StringBuilder();
        for (Type t : mi.formalTypes()) {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(t);
        }
        XConstraint guard = guard();
        return "(" + sb.toString() + ")" + (guard==null? "" : guard) + "=> " + mi.returnType();
    }

	@Override
	public boolean equalsImpl(TypeObject t) {
		return super.equalsImpl(t);
	}

	@Override
	public int hashCode() {
		return def.get().hashCode();
	}
    
    
}
