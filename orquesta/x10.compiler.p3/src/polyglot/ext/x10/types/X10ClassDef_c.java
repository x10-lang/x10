package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import polyglot.frontend.Source;
import polyglot.types.ArrayType;
import polyglot.types.ClassDef_c;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.FieldDef;
import polyglot.types.MethodDef;
import polyglot.types.Named;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.FilteringList;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.Predicate;
import polyglot.util.TypedList;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XRoot;
import x10.constraint.XSelf;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;

public class X10ClassDef_c extends ClassDef_c implements X10ClassDef {
    public X10ClassDef_c(TypeSystem ts, Source fromSource) {
        super(ts, fromSource);
        this.typeProperties = new ArrayList<TypeProperty>();
        this.typeMembers = new ArrayList<TypeDef>();
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
    
    // Cached realClause of the root type.
    XConstraint rootXClause;
    
    protected Ref<XConstraint> xclassInvariant;

    public void setXClassInvariant(Ref<XConstraint> c) {
        this.xclassInvariant = c;
        this.rootXClause = null;
    }

    public Ref<XConstraint> xclassInvariant() {
        return xclassInvariant;
    }
    
    public void checkRealClause() throws SemanticException {
	if (rootClauseInvalid != null)
	    throw rootClauseInvalid;
    }
    
    public XConstraint getRootXClause() {
	    if (rootXClause == null) {
		    if (computing) {
			    this.rootXClause = new XConstraint_c();
			    this.rootClauseInvalid = 
				    new SemanticException("The real clause is recursive.", position());
			    return rootXClause;
		    }
		    
		    computing = true;
		    
		    try {
			    List<? extends X10FieldDef> properties = (List<? extends X10FieldDef>) properties();
			    
			    X10TypeSystem xts = (X10TypeSystem) ts;

			    XConstraint result = new XConstraint_c();
			    
			    XRoot oldThis = xts.xtypeTranslator().transThisWithoutTypeConstraint();
			    
			    try {
				    // Add in constraints from the supertypes.  This is
				    // no need to change self, and no occurrence of this is possible in 
				    // a type's base constraint.
				    {
					    Type type = Types.get(superType());
					    if (type != null) {
						XConstraint rs = X10TypeMixin.realX(type);
						if (rs != null) {
						    rs = rs.substitute(XSelf.Self, oldThis);
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
						    rs = rs.substitute(XSelf.Self, oldThis);
						    result.addIn(rs);
					    }
				    }
				    
				    // add in the bindings from the property declarations.
				    for (X10FieldDef fi : properties) {
					    Type type = fi.asInstance().type();   // ### check for recursive call here
					    XConstraint rs = X10TypeMixin.realX(type);
					    if (rs != null) {
						    // Given: C(:c) f
						    // Add in: c[self.f/self,self/this]
						    XTerm newSelf = xts.xtypeTranslator().trans(XSelf.Self, fi.asInstance());
						    XConstraint rs1 = rs.substitute(newSelf, XSelf.Self);
						    XConstraint rs2 = rs1.substitute(XSelf.Self, oldThis);
						    result.addIn(rs2);
					    }
				    }

				    // Finally, add in the class invariant.
				    // It is important to do this last since we need avoid type-checking constraints
				    // until after the base type of the supertypes are resolved.
				    XConstraint ci = Types.get(xclassInvariant);
				    if (ci != null) {
					ci = ci.substitute(XSelf.Self, oldThis);
					result.addIn(ci);
				    }
				    
			    }
			    catch (XFailure f) {
				    result.setInconsistent();
				    this.rootXClause = result;
				    this.rootClauseInvalid = new SemanticException("The class invariant and property constraints are inconsistent.", position());
			    }
			    
			    if (result.consistent()) {
				    // Verify that the realclause, as it stands, entails the assertions of the 
				    // property.
				    for (X10FieldDef fi : properties) {
					    Type ftype = fi.asInstance().type();
					    
					    XConstraint c = X10TypeMixin.realX(ftype);
					    XTerm newSelf = xts.xtypeTranslator().trans(XSelf.Self, fi.asInstance());
					    c = c.substitute(newSelf, XSelf.Self);
					    
					    if (! result.entails(c)) {
						    this.rootXClause = result;
						    this.rootClauseInvalid = 
							    new SemanticException("The real clause, " + result 
							                          + ", does not satisfy constraints from " + fi + ".", position());
					    }
				    }
			    }
			    
			    this.rootXClause = result;
		    }
		    catch (XFailure e) {
			    this.rootXClause = new XConstraint_c();
			    this.rootXClause.setInconsistent();
			    this.rootClauseInvalid = new SemanticException(e.getMessage(), position());
		    }
		    catch (SemanticException e) {
			    this.rootXClause = new XConstraint_c();
			    this.rootXClause.setInconsistent();
			    this.rootClauseInvalid = e;
		    }
		    finally {
			    computing = false;
		    }
	    }
	    
	    return rootXClause;
    }

    public boolean isJavaType() {
        return fromJavaClassFile();
    }

    boolean propertiesInitialized = false;
    
    protected void setPropertiesFromMagicString(List<FieldDef> fields) {
        if (!propertiesInitialized) {
            propertiesInitialized = true;
            
            HashSet<String> set = new HashSet<String>();
            
            for (FieldDef fd : fields) {
                if (fd.name().equals(X10FieldInstance.MAGIC_PROPERTY_NAME)) {
                    if (fd.isConstant()) {
                        Object o = fd.constantValue();
                        if (o instanceof String) {
                            Scanner s = new Scanner((String) o);
                            while (s.hasNext()) {
                                String name = s.next();
                                set.add(name);
                            }
                        }
                    }
                }
            }
            
            for (FieldDef fd : fields) {
                if (set.contains(fd.name())) {
                    ((X10FieldDef) fd).setProperty();
                }
            }
        }
    }

    public List<FieldDef> properties() {
        return new FilteringList<FieldDef>(fields(), new Predicate<FieldDef>() {
            public boolean isTrue(FieldDef o) {
                return o instanceof X10FieldDef && ((X10FieldDef) o).isProperty();
            }
        });
    }
    
    List<TypeProperty> typeProperties;
    
    // TODO:
    // Add .class property
    // Add class <: C to class invariant
    // Add code to not complain that it's not initialized; ignore when disambiguating C[T]
    public List<TypeProperty> typeProperties() {
	return Collections.unmodifiableList(typeProperties);
    }
    
    public void addTypeProperty(TypeProperty p) {
	typeProperties.add(p);
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
    
    boolean valid = false;
    
	@Override
	public void addConstructor(ConstructorDef ci) {
		valid = false;
		super.addConstructor(ci);
	}

	@Override
	public void addField(FieldDef fi) {
		valid = false;
		super.addField(fi);
	}

	@Override
	public void addMethod(MethodDef mi) {
		valid = false;
		super.addMethod(mi);
	}

	@Override
	public List<FieldDef> fields() {
		if (! valid) {
			fixGenericAndPrimitiveTypes();
			valid = true;
		}
		List<FieldDef> fs = super.fields();
		setPropertiesFromMagicString(fs);
		return fs;
	}
	@Override
	public List<ConstructorDef> constructors() {
		if (! valid) {
			fixGenericAndPrimitiveTypes();
			valid = true;
		}
		return super.constructors();
	}

	@Override
	public List<MethodDef> methods() {
		if (! valid) {
			fixGenericAndPrimitiveTypes();
			valid = true;
		}
		return super.methods();
	}
	
	private void fixGenericAndPrimitiveTypes() {
		X10TypeSystem ts = (X10TypeSystem) this.ts;

		X10ClassType ct = (X10ClassType) asType();
		TypeProperty px = X10TypeSystem_c.getFirstTypeProperty(this);
		
		if (px == null)
			return;
		
		XVar thisPath;
		try {
			thisPath = ts.xtypeTranslator().transThis(ct);
		}
		catch (SemanticException e) {
			throw new InternalCompilerError(e);
		}
		
		Type T = PathType_c.pathBase(px.asType(), thisPath, ct);

		// Replace Parameter1 with T.
		// Replace C { Parameter1 } with C[T].

//		List<Ref<? extends Type>> newInterfaces = new ArrayList<Ref<? extends Type>>();
//		
//		for (Ref<? extends Type> tref : interfaces) {
//		    Type t = tref.getCached();
//		    if (!(t instanceof Named) || !((Named) t).name().equals("Parameter1"))
//			newInterfaces.add(tref);
//		    else
//			System.out.println("removing " + t);
//		}
//		
//		if (newInterfaces.size() != interfaces.size())
//		    interfaces = newInterfaces;
		
		for (FieldDef f : fields) {
			Ref<Type> r = (Ref<Type>) f.type();
			Type newType = fixType(r.get(), T);
			if (newType != r.get())
				r.update(newType);
		}

		for (MethodDef m : methods) {
			{
				Ref<Type> r = (Ref<Type>) m.returnType();
				Type newType = fixType(r.get(), T);
				if (newType != r.get())
					r.update(newType);
			}
			for (Ref<? extends Type> fr : m.formalTypes()) {
				Ref<Type> r = (Ref<Type>) fr;
				Type newType = fixType(r.get(), T);
				if (newType != r.get())
					r.update(newType);
			}
			for (Ref<? extends Type> fr : m.throwTypes()) {
				Ref<Type> r = (Ref<Type>) fr;
				Type newType = fixType(r.get(), T);
				if (newType != r.get())
					r.update(newType);
			}
		}

		for (ConstructorDef c : constructors) {
			X10ConstructorDef xc = (X10ConstructorDef) c;
			{
				Ref<Type> r = (Ref<Type>) xc.returnType();
				Type newType = fixType(r.get(), T);
				if (newType != r.get())
					r.update(newType);
			}
			for (Ref<? extends Type> fr : xc.formalTypes()) {
				Ref<Type> r = (Ref<Type>) fr;
				Type newType = fixType(r.get(), T);
				if (newType != r.get())
					r.update(newType);
			}
			for (Ref<? extends Type> fr : xc.throwTypes()) {
				Ref<Type> r = (Ref<Type>) fr;
				Type newType = fixType(r.get(), T);
				if (newType != r.get())
					r.update(newType);
			}
		}
	}
	
	private Map<String,Type> primitiveTypes;
	
	{
		primitiveTypes = new HashMap<String, Type>();
		primitiveTypes.put("x10.lang.Void", ts.Void());
		primitiveTypes.put("x10.lang.Boolean", ts.Boolean());
		primitiveTypes.put("x10.lang.Byte", ts.Byte());
		primitiveTypes.put("x10.lang.Short", ts.Short());
		primitiveTypes.put("x10.lang.Char", ts.Char());
		primitiveTypes.put("x10.lang.Int", ts.Int());
		primitiveTypes.put("x10.lang.Long", ts.Long());
		primitiveTypes.put("x10.lang.Float", ts.Float());
		primitiveTypes.put("x10.lang.Double", ts.Double());
	}
	
	private Type fixType(Type oldType, Type typeArg) {
		X10TypeSystem ts = (X10TypeSystem) this.ts;

		if (oldType instanceof ConstrainedType) {
			ConstrainedType ct = (ConstrainedType) oldType;
			Type t = Types.get(ct.baseType());
			Type t2 = fixType(t, typeArg);
			XConstraint c = Types.get(ct.constraint());
			// param1 should not appear in constraint; in fact, we shouldn't have any constraints
//			XConstraint c2 = c.substitute(ts.xtypeTranslator().trans(typeArg), (XRoot) ts.xtypeTranslator().trans(param1));
			return X10TypeMixin.xclause(t2, c);
		}
		if (oldType instanceof ArrayType) {
		    ArrayType at = (ArrayType) oldType;
		    Type base = fixType(at.base(), typeArg);
		    return ts.x10Array(base, false);
		}
		else if (oldType instanceof ClassType) {
			ClassType ct = (ClassType) oldType;
			
			X10ClassDef def = (X10ClassDef) ct.def();
			
			String name = def.fullName();
			
			if (name != null && primitiveTypes.containsKey(name)) {
				return primitiveTypes.get(name);
			}
		}
		
		return oldType;
	}
}
