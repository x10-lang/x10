package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import polyglot.ast.Special;
import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.types.constr.C_Field_c;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Special_c;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.types.constr.Failure;
import polyglot.frontend.Source;
import polyglot.types.ClassDef_c;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.FieldDef;
import polyglot.types.MethodDef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.FilteringList;
import polyglot.util.Position;
import polyglot.util.Predicate;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import polyglot.util.TypedList;

public class X10ClassDef_c extends ClassDef_c implements X10ClassDef {
    public X10ClassDef_c(TypeSystem ts, Source fromSource) {
        super(ts, fromSource);
        this.typeProperties = new ArrayList<TypeProperty>();
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
    
    protected Ref<? extends Constraint> classInvariant;
    
    // Cached realClause of the root type.
    protected Constraint rootClause;
    protected SemanticException rootClauseInvalid;

    public void setClassInvariant(Ref<? extends Constraint> c) {
        this.classInvariant = c;
        this.rootClause = null;
    }

    public Ref<? extends Constraint> classInvariant() {
        return classInvariant;
    }

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

    public Constraint getRootClause() {
        if (rootClause == null) {
            if (computing) {
                this.rootClause = new Constraint_c((X10TypeSystem) ts);
                this.rootClauseInvalid = 
                    new SemanticException("The real clause is recursive.", position());
                return rootClause;
            }

            computing = true;

            try {
                List<? extends X10FieldDef> properties = (List<? extends X10FieldDef>) properties();

                Constraint result = new Constraint_c((X10TypeSystem) ts);
                
                C_Special_c self = new C_Special_c(X10Special.SELF, this.asType());
				try {
                Constraint ci = Types.get(classInvariant);

                if (ci != null)
                    result.addIn(ci);

                // Add in constraints from the supertype.
                Type type = Types.get(superType());
                if (type instanceof X10Type) {
                    X10Type xType = (X10Type) type;
                    Constraint rs = xType.realClause();
                    // no need to change self, and no occurrence of this is possible in 
                    // a type's base constraint.
                    if (rs != null)
                        result.addIn(rs);
                }

                // Add in constraints from the interfaces.
                for (Iterator<Ref<? extends Type>> i = interfaces().iterator(); i.hasNext(); ) {
                    Ref<? extends Type> it = (Ref<? extends Type>) i.next();
                    if (it instanceof X10Type) {
                        X10Type xType = (X10Type) it;
                        Constraint rs = xType.realClause();
                        // no need to change self, and no occurrence of this is possible in 
                        // a type's base constraint.
                        if (rs != null)
                            result.addIn(rs);
                    } 
                }
                
                C_Var newThis = self;

                // add in the bindings from the property declarations.
                for (X10FieldDef fi : properties) {
                    type = fi.asInstance().type();   // ### check for recursive call here
                    if (type instanceof X10Type) {
                        X10Type xType = (X10Type) type;
                        Constraint rs = xType.realClause();
                        if (rs != null) {
                            C_Var newSelf = new C_Field_c(fi.asInstance(), self);
                            Constraint rs1 = rs.substitute(newSelf, C_Special.Self);
							Constraint rs2 = rs1.substitute(newThis, C_Special.This);
							rs2.setSelfVar(null);
							result.addIn(rs2);
                        }
                    }
                }
                }
                catch (Failure f) {
                    result.setInconsistent();
                    this.rootClauseInvalid = new SemanticException("The class invariant and property constraints are inconsistent.", position());
                }

                if (result.consistent()) {
                    // Verify that the realclause, as it stands entails the assertions of the 
                    // property.
                    for (X10FieldDef fi : properties) {
                        C_Var var = new C_Field_c(fi.asInstance(), self);
                        if (! result.entailsType(var)) {
                            this.rootClause = result;
                            this.rootClauseInvalid = 
                                new SemanticException("The real clause," + result 
                                                      + " does not satisfy constraints from the property declaration " 
                                                      + var.type() + " " + var + ".", position());
                        }
                    }
                }

                this.rootClause = result;
            }
            finally {
                computing = false;
            }
        }

        return rootClause;
    }

    public boolean isJavaType() {
        return fromJavaClassFile() || fullName().startsWith("java");
    }

    public List<FieldDef> fields2() {
        List<FieldDef> fields = super.fields();
        setPropertiesFromMagicString(fields);
        return fields;
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

    public List<TypeProperty> typeProperties() {
    	if (typeProperties.size() == 0) {
    		boolean isValueArray = false;
    		boolean isArray = false;
    		if (fullName().equals("x10.lang.genericArray")) {
    			isValueArray = true;
    			isArray = true;
    		}
    		if (fullName().equals("x10.lang.GenericReferenceArray")) {
    			isValueArray = false;
    			isArray = true;
    		}
    		if (isArray) {
    			X10TypeSystem ts = (X10TypeSystem) this.ts;
    			X10ClassType ct = (X10ClassType) this.asType();
    			TypeProperty p = new TypeProperty_c(ts, Position.COMPILER_GENERATED, Types.ref(ct), "T",
    					isValueArray ? TypeProperty.Variance.COVARIANT : TypeProperty.Variance.INVARIANT);
    			addTypeProperty(p);
    		}
    	}
    	
    	return Collections.unmodifiableList(typeProperties);
    }

    public void addTypeProperty(TypeProperty p) {
    	typeProperties.add(p);
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
		if (! valid && name != null && (name.equals("genericArray") || name.equals("GenericReferenceArray"))) {
			fixArrayType(name.equals("genericArray"));
			valid = true;
		}
		return fields2();
	}
	@Override
	public List<ConstructorDef> constructors() {
		if (! valid && name != null && (name.equals("genericArray") || name.equals("GenericReferenceArray"))) {
			fixArrayType(name.equals("genericArray"));
			valid = true;
		}
		return super.constructors();
	}

	@Override
	public List<MethodDef> methods() {
		if (! valid && name != null && (name.equals("genericArray") || name.equals("GenericReferenceArray"))) {
			fixArrayType(name.equals("genericArray"));
			valid = true;
		}
		return super.methods();
	}

	private void fixArrayType(boolean isValueArray) {
		X10ClassType ct = (X10ClassType) asType();
		
		if (typeProperties().size() == 1) {
			X10TypeSystem ts = (X10TypeSystem) this.ts;

			TypeProperty px = typeProperties().get(0);
			
			C_Var thisPath = new C_Special_c(Special.THIS, ct);
			PathType_c T = new PathType_c(ts, Position.COMPILER_GENERATED, thisPath, px);
			Type param1 = ts.parameter1();
			
			for (FieldDef f : fields) {
				Ref<Type> r = (Ref<Type>) f.type();
				if (r.get().typeEquals(param1)) {
					r.update(T);
				}
			}
			
			for (MethodDef m : methods) {
				{
					Ref<Type> r = (Ref<Type>) m.returnType();
					if (r.get().typeEquals(param1)) {
						r.update(T);
					}
				}
				for (Ref<? extends Type> fr : m.formalTypes()) {
					Ref<Type> r = (Ref<Type>) fr;
					if (r.get().typeEquals(param1)) {
						r.update(T);
					}
				}
				for (Ref<? extends Type> fr : m.throwTypes()) {
					Ref<Type> r = (Ref<Type>) fr;
					if (r.get().typeEquals(param1)) {
						r.update(T);
					}
				}
			}

			for (ConstructorDef c : constructors) {
				X10ConstructorDef xc = (X10ConstructorDef) c;
				{
					Ref<Type> r = (Ref<Type>) xc.returnType();
					if (r.get().typeEquals(param1)) {
						r.update(T);
					}
				}
				for (Ref<? extends Type> fr : xc.formalTypes()) {
					Ref<Type> r = (Ref<Type>) fr;
					if (r.get().typeEquals(param1)) {
						r.update(T);
					}
				}
				for (Ref<? extends Type> fr : xc.throwTypes()) {
					Ref<Type> r = (Ref<Type>) fr;
					if (r.get().typeEquals(param1)) {
						r.update(T);
					}
				}
			}
		}
	}
}
