package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

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
import polyglot.types.FieldDef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.FilteringList;
import polyglot.util.Predicate;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import polyglot.util.TypedList;

public class X10ClassDef_c extends ClassDef_c implements X10ClassDef {
    public X10ClassDef_c(TypeSystem ts, Source fromSource) {
        super(ts, fromSource);
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
                    Constraint rs = X10TypeMixin.realClause(xType);
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
                        Constraint rs = X10TypeMixin.realClause(xType);
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
                        Constraint rs = X10TypeMixin.realClause(xType);
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

    @Override
    public List<FieldDef> fields() {
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
}
