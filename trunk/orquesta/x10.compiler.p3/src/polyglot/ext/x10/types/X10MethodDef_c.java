/**
 * 
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.types.Flags;
import polyglot.types.MethodDef_c;
import polyglot.types.MethodInstance;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
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
import x10.constraint.XVar;

/**
 * An X10ConstructorInstance_c varies from a ConstructorInstance_c only in that it
 * maintains a returnType. If an explicit returnType is not declared in the constructor
 * then the returnType is simply a noClause variant of the container.
 * @author vj
 *
 */
public class X10MethodDef_c extends MethodDef_c implements X10MethodDef {
    protected Ref<? extends XConstraint> whereClause;
    List<Ref<? extends Type>> typeParameters;

    public X10MethodDef_c(TypeSystem ts, Position pos,
            Ref<? extends ReferenceType> container,
            Flags flags, 
            Ref<? extends Type> returnType,
            String name,
            List<Ref<? extends Type>> typeParams,
            List<Ref<? extends Type>> formalTypes,
            Ref<? extends XConstraint> whereClause,
            List<Ref<? extends Type>> excTypes) {
        super(ts, pos, container, flags, returnType, name, formalTypes, excTypes);
        this.typeParameters = TypedList.copyAndCheck(typeParams, Ref.class, true);
        this.whereClause = whereClause;
    }

    // BEGIN ANNOTATION MIXIN
    List<Ref<? extends X10ClassType>> annotations;

    public List<Ref<? extends X10ClassType>> defAnnotations() {
        return Collections.unmodifiableList(annotations);
    }
    
    public void setDefAnnotations(List<Ref<? extends X10ClassType>> annotations) {
        this.annotations = TypedList.<Ref<? extends X10ClassType>>copyAndCheck(annotations, Ref.class, true);
    }
    
    public List<X10ClassType> annotations() {
        return X10TypeObjectMixin.annotations(this);
    }
    
    public List<X10ClassType> annotationsMatching(Type t) {
        return X10TypeObjectMixin.annotationsMatching(this, t);
    }
    
    public List<X10ClassType> annotationsNamed(String fullName) {
        return X10TypeObjectMixin.annotationsNamed(this, fullName);
    }
    // END ANNOTATION MIXIN
    
    /** Constraint on formal parameters. */
    public Ref<? extends XConstraint> whereClause() {
        return whereClause;
    }

    public void setWhereClause(Ref<? extends XConstraint> s) {
        this.whereClause = s;
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
		    for (Type t : mt.typeParams()) {
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
		String s = designator() + " " + flags().translate() + container() + "." + signature() + (whereClause() != null ? whereClause() : "") + ": " + returnType();

		if (!throwTypes().isEmpty()) {
			s += " throws " + CollectionUtil.listToString(throwTypes());
		}

		return s;
	}

}
