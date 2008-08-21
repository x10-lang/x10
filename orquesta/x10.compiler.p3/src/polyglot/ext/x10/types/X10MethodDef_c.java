/**
 * 
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.MethodDef_c;
import polyglot.types.MethodInstance;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.util.TypedList;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XRoot;
import x10.constraint.XTerm;
import x10.constraint.XVar;

/**
 * An X10ConstructorInstance_c varies from a ConstructorInstance_c only in that it
 * maintains a returnType. If an explicit returnType is not declared in the constructor
 * then the returnType is simply a noClause variant of the container.
 * @author vj
 *
 */
public class X10MethodDef_c extends MethodDef_c implements X10MethodDef {
    Ref<XConstraint> guard;
    List<Ref<? extends Type>> typeParameters;
    List<LocalDef> formalNames;
    Ref<XTerm> body;

    public X10MethodDef_c(TypeSystem ts, Position pos,
            Ref<? extends StructType> container,
            Flags flags, 
            Ref<? extends Type> returnType,
            Name name,
            List<Ref<? extends Type>> typeParams,
            List<Ref<? extends Type>> formalTypes,
            List<LocalDef> formalNames,
            Ref<XConstraint> guard, List<Ref<? extends Type>> excTypes, Ref<XTerm> body) {
        super(ts, pos, container, flags, returnType, name, formalTypes, excTypes);
        this.typeParameters = TypedList.copyAndCheck(typeParams, Ref.class, true);
        this.formalNames = TypedList.copyAndCheck(formalNames, LocalDef.class, true);
        this.guard = guard;
        this.body = body;
    }

    public List<LocalDef> formalNames() {
	return Collections.unmodifiableList(formalNames);
    }

    public void setFormalNames(List<LocalDef> formalNames) {
	this.formalNames = TypedList.copyAndCheck(formalNames, LocalDef.class, true);
    }

    public Ref<XTerm> body() {
        return body;
    }
    
    public void body(Ref<XTerm> body) {
	this.body = body;
    }

    protected boolean inferReturnType;
    public boolean inferReturnType() { return inferReturnType; }
    public void inferReturnType(boolean r) { this.inferReturnType = r; }

    // BEGIN ANNOTATION MIXIN
    List<Ref<? extends Type>> annotations;

    public List<Ref<? extends Type>> defAnnotations() {
	if (annotations == null) return Collections.EMPTY_LIST;
        return Collections.unmodifiableList(annotations);
    }
    
    public void setDefAnnotations(List<Ref<? extends Type>> annotations) {
        this.annotations = TypedList.<Ref<? extends Type>>copyAndCheck(annotations, Ref.class, true);
    }
    
    public List<Type> annotations() {
        return X10TypeObjectMixin.annotations(this);
    }
    
    public List<Type> annotationsMatching(Type t) {
        return X10TypeObjectMixin.annotationsMatching(this, t);
    }
    
    public List<Type> annotationsNamed(QName fullName) {
        return X10TypeObjectMixin.annotationsNamed(this, fullName);
    }
    // END ANNOTATION MIXIN
    
    /** Constraint on formal parameters. */
    public Ref<XConstraint> guard() {
        return guard;
    }

    public void setGuard(Ref<XConstraint> s) {
        this.guard = s;
    }
    
    public List<Ref<? extends Type>> typeParameters() {
	        return Collections.unmodifiableList(typeParameters);
    }

    public void setTypeParameters(List<Ref<? extends Type>> typeParameters) {
	    this.typeParameters = TypedList.copyAndCheck(typeParameters, Ref.class, true);
    }
	
    public String signature() {
        return name + (typeParameters.isEmpty() ? "" : typeParameters.toString()) + "(" + CollectionUtil.listToString(formalTypes) + ")";
    }

    @Override
    public MethodInstance asInstance() {
        if (asInstance == null) {
            asInstance = new X10MethodInstance_c(ts, position(), Types.<X10MethodDef>ref(this));
        }
        return asInstance;
    }
    
    public static boolean hasVar(Type type, XRoot var) {
	    if (type instanceof ConstrainedType) {
		    XConstraint rc = X10TypeMixin.realX(type);
		    if (rc != null && rc.hasVar(var))
			    return true;
		    ConstrainedType ct = (ConstrainedType) type;
		    if (hasVar(Types.get(ct.baseType()), var))
			    return true;
	    }
	    if (type instanceof ParametrizedType) {
		    ParametrizedType mt = (ParametrizedType) type;
		    for (Type t : mt.typeParameters()) {
			    if (hasVar(t, var))
				    return true;
		    }
		    for (XVar v : mt.formals()) {
			    if (v.hasVar(var))
				    return true;
		    }
	    }
	    return false;
    }
    
	public String toString() {
		String s = designator() + " " + X10Flags.toX10Flags(flags()).prettyPrint() + container() + "." + signature() + (guard() != null ? guard() : "") + ": " + returnType();

		if (!throwTypes().isEmpty()) {
			s += " throws " + CollectionUtil.listToString(throwTypes());
		}
		
		if (body != null && body.getCached() != null)
		    s += " = " + body;

		return s;
	}

}
