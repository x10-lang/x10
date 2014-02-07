/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.types;

import java.util.*;

import polyglot.util.*;
import x10.types.MethodInstance;

/**
 * An <code>ArrayType</code> represents an array of base java types.
 */
public class JavaArrayType_c extends ReferenceType_c implements JavaArrayType
{
    private static final long serialVersionUID = 5957743833621743101L;

    protected Ref<? extends Type> base;
    protected List<FieldDef> fields;
    protected List<MethodDef> methods;
    protected List<Ref<? extends Type>> interfaces;

    /** Used for deserializing types. */
    protected JavaArrayType_c() { }

    public JavaArrayType_c(TypeSystem ts, Position pos, Ref<? extends Type> base) {
	super(ts, pos, pos);
	this.base = base;

        methods = null;
        fields = null;
        interfaces = null;
    }

    protected void init() {
        if (methods == null) {
            methods = new ArrayList<MethodDef>(1);

            // Add method public Object clone()
            MethodDef mi = ts.methodDef(position(), position(),
                                        Types.<JavaArrayType_c>ref(this),
                                        ts.Public(),
                                        Types.<Type>ref(ts.Any()),
                                        Name.make("clone"),
                                        Collections.<Ref<? extends Type>>emptyList(), Collections.<Ref<? extends Type>>emptyList());
            methods.add(mi);
        }

        if (fields == null) {
            fields = new ArrayList<FieldDef>(1);

            // Add field public final int length
            FieldDef fi = ts.fieldDef(position(),
                                        Types.<JavaArrayType_c>ref(this),
                                        ts.Public().Final(),
                                        Types.ref(ts.Int()),
                                        Name.make("length"));
            fi.setNotConstant();
            fields.add(fi);
        }

        if (interfaces == null) {
            interfaces = new ArrayList<Ref<? extends Type>>(2);
        }
    }

    public Ref<? extends Type> theBaseType() {
        return base;
    }
    
    /** Get the base type of the array. */
    public Type base() {
        return Types.get(base);
    }

    /** Set the base type of the array. */
    public JavaArrayType base(Type base) {
        return base(Types.ref(base));
    }
    
    public JavaArrayType base(Ref<? extends Type> base) {
        if (base == this.base)
            return this;
	JavaArrayType_c n = (JavaArrayType_c) copy();
	n.base = base;
	return n;
    }

    /** Get the ulitimate base type of the array. */
    public Type ultimateBase() {
        if (base().isArray()) {
            return base().toArray().ultimateBase();
        }

        return base();
    }

    public int dims() {
        return 1 + (base().isArray() ? base().toArray().dims() : 0);
    }

    public String typeToString() {
        return base.toString() + "[]";
    }

    public void print(CodeWriter w) {
	base().print(w);
	w.write("[]");
    }

    /** Translate the type. */
    public String translate(Resolver c) {
        return base().translate(c) + "[]"; 
    }
    
    public boolean isArray() { return true; }
    public JavaArrayType toArray() { return this; }

    /** Get the methods implemented by the array type. */
    public List<MethodInstance> methods() {
        init();
        return new TransformingList<MethodDef,MethodInstance>(methods, new MethodAsTypeTransform());
    }

    /** Get the fields of the array type. */
    public List<FieldInstance> fields() {
        init();
        return new TransformingList<FieldDef,FieldInstance>(fields, new FieldAsTypeTransform());
    }

    /** Get the clone() method. */
    public MethodInstance cloneMethod() {
	return methods().get(0);
    }

    /** Get a field of the type by name. */
    public FieldInstance fieldNamed(Name name) {
        FieldInstance fi = lengthField();
        return name.equals(fi.name()) ? fi : null;
    }

    /** Get the length field. */
    public FieldInstance lengthField() {
	return fields().get(0);
    }

    /** Get the super type of the array type. */
    public Type superClass() {
	return ts.Any();
    }

    /** Get the interfaces implemented by the array type. */
    public List<Type> interfaces() {
        init();
        return new TransformingList<Ref<? extends Type>,Type>(interfaces, new DerefTransform<Type>());
    }

    public int hashCode() {
	return base().hashCode() << 1;
    }

    public boolean equalsImpl(TypeObject t) {
        if (t instanceof JavaArrayType) {
            JavaArrayType a = (JavaArrayType) t;
            return ts.equals((TypeObject) base(), (TypeObject) a.base());
        }
	return false;
    }
    public boolean isX10Struct() {
    	return false;
    }
    public Type makeX10Struct() {
    	throw new InternalCompilerError("Should not have been called. Cannot make an Unknown type a struct.");
    }
}
