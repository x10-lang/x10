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
import java.util.Iterator;
import java.util.List;

import polyglot.frontend.Source;
import polyglot.types.ClassDef;
import polyglot.types.ClassDef_c;
import polyglot.types.FieldDef;
import polyglot.types.LazyRef_c;
import polyglot.types.Name;
import polyglot.types.Package;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.Ref_c;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.FilteringList;
import polyglot.util.Predicate;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import polyglot.util.TypedList;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XName;
import x10.constraint.XNameWrapper;
import x10.constraint.XRoot;
import x10.constraint.XTerm;
import x10.constraint.XTerms;

public class X10ClassDef_c extends ClassDef_c implements X10ClassDef {
    protected List<ParameterType.Variance> variances;
    XRoot thisVar;
    
    public X10ClassDef_c(TypeSystem ts, Source fromSource) {
        super(ts, fromSource);
        this.variances = new ArrayList<ParameterType.Variance>();
        this.typeParameters = new ArrayList<ParameterType>();
        this.typeMembers = new ArrayList<TypeDef>();
        this.thisVar = null;
    }
    
    public XRoot thisVar() {
        if (thisVar == null) {
            String fullNameWithThis = fullName() + "#this";
            XName thisName = new XNameWrapper<Object>(new Object(), fullNameWithThis);
            thisVar = XTerms.makeLocal(thisName);
        }
        return this.thisVar;
    }

    public void setThisVar(XRoot thisVar) {
        this.thisVar = thisVar;
    }

    // BEGIN ANNOTATION MIXIN
    List<Ref<? extends Type>> annotations;

    public List<Ref<? extends Type>> defAnnotations() {
	if (annotations == null)
	    return Collections.EMPTY_LIST;
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
    
    // Cached realClause of the root type.
    protected SemanticException rootClauseInvalid;

    /**
     * Set the realClause for this type. The realClause is the conjunction of the
     * depClause and the baseClause for the type -- it represents all the constraints
     * that are satisfied by an instance of this type. The baseClause is the invariant for
     * the base type. If the base type C has defined properties P1 p1, ..., Pk pk, 
     * and inherits from type B, then the baseClause for C is the baseClause for B
     * conjoined with r1[self.p1/self, self/this] && ... && rk[self.pk/self, self/this]
     * where ri is the realClause for Pi.
     * 
     * @return
     */
    boolean computing = false;

    Ref<TypeConstraint> typeBounds;
    
    public Ref<TypeConstraint> typeBounds() {
        return typeBounds;
    }

    public void setTypeBounds(Ref<TypeConstraint> c) {
        this.typeBounds = c;
    }

    // Cached realClause of the root type.
    Ref<XConstraint> rootClause;

    protected Ref<XConstraint> classInvariant;

    public void setClassInvariant(Ref<XConstraint> c) {
        this.classInvariant = c;
        this.rootClause = null;
        this.rootClauseInvalid = null;
    }

    public void setRootClause(Ref<XConstraint> c) {
    	this.rootClause = c;
    	this.rootClauseInvalid = null;
    }

    public Ref<XConstraint> classInvariant() {
        return classInvariant;
    }
    
    public void checkRealClause() throws SemanticException {
	if (rootClauseInvalid != null)
	    throw rootClauseInvalid;
    }
    
    public XConstraint getRootClause() {
	    if (rootClause == null) {
		    if (computing) {
			    /*this.rootClause = Types.<XConstraint>ref(new XConstraint_c());
			    this.rootClauseInvalid = 
				    new SemanticException("The real clause of " + this + " depends upon itself.", position());
			    return rootClause.get();*/
		    	return new XConstraint_c();
		    }
		    
		    computing = true;
		    
		    try {
			    List<X10FieldDef> properties = properties();
			    
			    X10TypeSystem xts = (X10TypeSystem) ts;

			    XConstraint result = new XConstraint_c();
			    
			    XRoot oldThis = xts.xtypeTranslator().transThisWithoutTypeConstraint();
			    
			    try {
				    // Add in constraints from the supertypes.  This is
				    // no need to change self, and no occurrence of this is possible in 
				    // a type's base constraint.
			    	// vj: 08/12/09. Incorrect. this can occur in a type's base constraint.
				    {
					    Type type = Types.get(superType());
					    if (type != null) {
						XConstraint rs = X10TypeMixin.realX(type);
						if (rs != null) {
							if (rs.thisVar() != null)
								rs = rs.substitute(oldThis, (XRoot) rs.thisVar());
						    result.addIn(rs);
						}
					    }
				    }

				    // Add in constraints from the interfaces.
				    for (Iterator<Ref<? extends Type>> i = interfaces().iterator(); i.hasNext(); ) {
					    Ref<? extends Type> it = (Ref<? extends Type>) i.next();
					    XConstraint rs = X10TypeMixin.realX(it.get());
					    // no need to change self, and no occurrence of this is possible in 
					    // a type's base constraint.
					    if (rs != null) {
//						    rs = rs.substitute(rs.self(), oldThis);
						    result.addIn(rs);
					    }
				    }
				    
				    // add in the bindings from the property declarations.
				    for (X10FieldDef fi : properties) {
					    Type type = fi.asInstance().type();   // ### check for recursive call here
					    XRoot fiThis = fi.thisVar();
					    XConstraint rs = X10TypeMixin.realX(type);
					    if (rs != null) {
						    // Given: C(:c) f
						    // Add in: c[self.f/self,self/this]
						    XTerm newSelf = xts.xtypeTranslator().trans(rs, rs.self(), fi.asInstance());
						    XConstraint rs1 = rs.substitute(newSelf, rs.self());
						    XConstraint rs2;
						    if (fiThis != null)
						        rs2 = rs1.substitute(rs1.self(), fiThis);
						    else
						        rs2 = rs1;
						    result.addIn(rs2);
					    }
				    }

				    // Finally, add in the class invariant.
				    // It is important to do this last since we need avoid type-checking constraints
				    // until after the base type of the supertypes are resolved.
				    XConstraint ci = Types.get(classInvariant);
				    if (ci != null) {
					ci = ci.substitute(ci.self(), oldThis);
					result.addIn(ci);
				    }
				    
			    }
			    catch (XFailure f) {
				    result.setInconsistent();
				    this.rootClause = Types.ref(result);
				    this.rootClauseInvalid = new SemanticException("The class invariant and property constraints of " + this + " are inconsistent.", position());
			    }
			    
			    // Now, set the root clause and mark that we're no longer computing.
			    this.rootClause = Types.ref(result);
			    this.computing = false;
			    
			    // Now verify that the root clause entails the assertions of the properties.
			    // We need to set the root clause first, to avoid a spurious report of a cyclic dependency.
			    // This can happen when one of the properties is subtype of this type:
			    // class Ref(home: Place) { }
			    // class Place extends Ref { ... }
			    
			    // Disable this for now since it can cause an infinite loop.
			    // TODO: vj 08/12/09 Revisit this.
			    if (false && result.consistent()) {
				    // Verify that the realclause, as it stands, entails the assertions of the 
				    // property.
				    for (X10FieldDef fi : properties) {
					    Type ftype = fi.asInstance().type();
					    
					    XConstraint c = X10TypeMixin.realX(ftype);
					    XTerm newSelf = xts.xtypeTranslator().trans(c, c.self(), fi.asInstance());
					    c = c.substitute(newSelf, c.self());
					    
					    if (! result.entails(c, ((X10Context) ts.emptyContext()).constraintProjection(result, c))) {
						    this.rootClause = Types.ref(result);
						    this.rootClauseInvalid = 
							    new SemanticException("The real clause, " + result 
							                          + ", does not satisfy constraints from " + fi + ".", position());
					    }
				    }
			    }
		    }
		    catch (XFailure e) {
		    	XConstraint result = new XConstraint_c();
			    result.setInconsistent();
			    this.rootClause = Types.ref(result);
			    this.rootClauseInvalid = new SemanticException(e.getMessage(), position());
		    }
		    catch (SemanticException e) {
		    	XConstraint result = new XConstraint_c();
			    result.setInconsistent();
			    this.rootClause = Types.ref(result);
			    this.rootClauseInvalid = e;
		    }
		    finally {
			    computing = false;
		    }
	    }
	    
	    assert rootClause != null;
	    return rootClause.get();
    }

    public boolean isJavaType() {
        return fromJavaClassFile();
    }

    public List<X10FieldDef> properties() {
	List<X10FieldDef> x10fields = new TransformingList(fields(), new Transformation<FieldDef,X10FieldDef>() {
	    public X10FieldDef transform(FieldDef o) {
		return (X10FieldDef) o;
	    }
	});
        return new FilteringList<X10FieldDef>(x10fields, new Predicate<X10FieldDef>() {
            public boolean isTrue(X10FieldDef o) {
                return o.isProperty();
            }
        });
    }
    
    List<ParameterType> typeParameters;
    
    public List<ParameterType> typeParameters() {
	return Collections.unmodifiableList(typeParameters);
    }
    
    public List<ParameterType.Variance> variances() {
	return Collections.unmodifiableList(variances);
    }
    
    public void addTypeParameter(ParameterType p, ParameterType.Variance v) {
	typeParameters.add(p);
	variances.add(v);
    }

    List<TypeDef> typeMembers;

    // TODO:
    // Add .class property
    // Add class <: C to class invariant
    // Add code to not complain that it's not initialized; ignore when disambiguating C[T]
    public List<TypeDef> memberTypes() {
    	return Collections.unmodifiableList(typeMembers);
    }

    public void addMemberType(TypeDef t) {
    	typeMembers.add(t);
    }
    public Ref<TypeConstraint> typeGuard() {
    	return new LazyRef_c<TypeConstraint>(X10TypeMixin.parameterBounds(asType()));
    }
    public boolean isStruct() {
    	return X10Flags.toX10Flags(flags()).isStruct();
    }
    // This is overridden by the synthetic Fun_** classes created in X10TypeSystem_c.
    public boolean isFunction() {
    	return false;
    }
    public String toString() {
        Name name = name();
        
        if (kind() == null) {
            return "<unknown class " + name + ">";
        }
    
        if (kind() == ANONYMOUS) {
            if (interfaces != null && ! interfaces.isEmpty()) {
                return isFunction() ? "" + interfaces.get(0) : "<anonymous subtype of " + interfaces.get(0) + ">";
            }
            if (superType != null) {
                return "<anonymous subclass of " + superType + ">";
            }
        }
    
        if (kind() == TOP_LEVEL) {
            Package p = Types.get(package_());
            return (p != null ? p.toString() + "." : "") + name;
        }
        else if (kind() == MEMBER) {
            ClassDef outer = Types.get(outer());
            return (outer != null ? outer.toString() + "." : "") + name;
        }
        else {
            return name.toString();
        }
    }
}
