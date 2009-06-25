/**
 * 
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.ConstructorInstance_c;
import polyglot.types.DerefTransform;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MethodInstance;
import polyglot.types.ProcedureInstance;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import x10.constraint.XConstraint;

/**
 * An X10ConstructorInstance_c varies from a ConstructorInstance_c only in that it
 * maintains a returnType. If an explicit returnType is not declared in the constructor
 * then the returnType is simply a noClause variant of the container.
 * @author vj
 *
 */
public class X10ConstructorInstance_c extends ConstructorInstance_c implements X10ConstructorInstance {

    public X10ConstructorInstance_c(TypeSystem ts, Position pos, Ref<? extends X10ConstructorDef> def) {
        super(ts, pos, def);
    }
    
    public X10ConstructorDef x10Def() {
        return (X10ConstructorDef) def();
    }
    
    public List<Type> annotations() {
        return X10TypeObjectMixin.annotations(this);
    }

    public List<Type> annotationsMatching(Type t) {
        return X10TypeObjectMixin.annotationsMatching(this, t);
    }

    /* (non-Javadoc)
     * @see polyglot.ext.x10.types.X10ConstructorInstance#depClause()
     */
    public XConstraint constraint() { return X10TypeMixin.realX(returnType()); }

    public Type returnType;
    
    public Type returnType() { 
        if (returnType == null) {
            returnType = x10Def().returnType().get();
        }
	return returnType;
    }
    
    public X10ConstructorInstance returnType(Type retType) {
        X10ConstructorInstance_c n = (X10ConstructorInstance_c) copy();
        n.returnType = retType;
        return n;
    }

    /** Constraint on superclass constructor call return type. */
    public XConstraint supClause() { 
        return Types.get(x10Def().supClause());
        }

    XConstraint guard;
    
    /** Constraint on formal parameters. */
    public XConstraint guard() {
        if (guard == null) 
            guard = Types.get(x10Def().guard());
        return guard;
    }

    public X10ConstructorInstance guard(XConstraint c) {
        X10ConstructorInstance_c n = (X10ConstructorInstance_c) copy();
        n.guard = c;
        return n;
    }

    public boolean callValidNoClauses(Type thisType, List<Type> argTypes) {
        X10ConstructorInstance_c me = (X10ConstructorInstance_c) this.formalTypes(new TransformingList<Type,Type>(this.formalTypes(), new X10MethodInstance_c.NoClauseVariant()));
        return me.superCallValid(thisType, new TransformingList<Type,Type>(argTypes, new X10MethodInstance_c.NoClauseVariant()));
    }
    
    protected boolean superCallValid(Type thisType, List<Type> argTypes) {
        return super.callValid(thisType, argTypes);
    }
    
    @Override
    public boolean callValid(Type thisType, List<Type> argTypes) {
        return X10MethodInstance_c.callValidImpl(this, thisType, argTypes);
    }
    
    public List<Type> typeParameters;

    public List<Type> typeParameters() {
	    if (this.typeParameters == null) {
		    this.typeParameters = new TransformingList<Ref<? extends Type>, Type>(x10Def().typeParameters(), new DerefTransform<Type>());
	    }

	    return typeParameters;
    }

    public X10ConstructorInstance typeParameters(List<Type> typeParameters) {
	    X10ConstructorInstance_c n = (X10ConstructorInstance_c) copy();
	    n.typeParameters = typeParameters;
	    return n;
    }

    public List<LocalInstance> formalNames;
    
    public List<LocalInstance> formalNames() {
	if (this.formalNames == null) {
	    this.formalNames = new TransformingList(x10Def().formalNames(), new Transformation<LocalDef,LocalInstance>() {
		public LocalInstance transform(LocalDef o) {
		    return o.asInstance();
		}
	    });
	}
	
	return formalNames;
    }
    
    public X10ConstructorInstance formalNames(List<LocalInstance> formalNames) {
	X10ConstructorInstance_c n = (X10ConstructorInstance_c) copy();
	n.formalNames = formalNames;
	return n;
    }

    public String toString() {
	    String s = designator() + " " + X10Flags.toX10Flags(flags()).prettyPrint() + container() + "." + signature() + (guard() != null ? guard() : "") + ": " + returnType();
	
	    if (! throwTypes().isEmpty()) {
		    s += " throws " + CollectionUtil.listToString(throwTypes());
	    }
	
	    return s;
    }
    
    public String signature() {
	StringBuilder sb = new StringBuilder();
	sb.append("this");
	List<String> params = new ArrayList<String>();
	if (typeParameters != null) {
	    for (int i = 0; i < typeParameters.size(); i++) {
		params.add(typeParameters.get(i).toString());
	    }
	}
	else {
	    for (int i = 0; i < x10Def().typeParameters().size(); i++) {
		params.add(x10Def().typeParameters().get(i).toString());
	    }
	}
	if (params.size() > 0) {
	    sb.append("[");
	    sb.append(CollectionUtil.listToString(params));
	    sb.append("]");
	}
	List<String> formals = new ArrayList<String>();
	if (formalTypes != null) {
	    for (int i = 0; i < formalTypes.size(); i++) {
		String s = "";
		String t = formalTypes.get(i).toString();
		if (formalNames != null && i < formalNames.size()) {
		    LocalInstance a = formalNames.get(i);
		    if (a != null && ! a.name().toString().equals(""))
			s = a.name() + ": " + t; 
		    else
			s = t;
		}
		else {
		    s = t;
		}
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
	if (guard != null)
	    sb.append(guard);
	return sb.toString();
    }
}
