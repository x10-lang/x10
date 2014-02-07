/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10doc.doc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.Ref;
import x10.ast.X10SourceFile_c;
import x10.types.ParameterType;
import x10.types.TypeDef;
import x10.types.X10ClassDef;
import x10.types.X10ConstructorDef;
import x10.types.X10FieldDef;
import x10.types.X10MethodDef;
import polyglot.types.TypeSystem;
import x10.types.constraints.CConstraint;
import x10.types.constraints.SubtypeConstraint;
import x10.types.constraints.TypeConstraint;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.javadoc.WildcardType;

public class X10ClassDoc extends X10Doc implements ClassDoc {
    X10SourceFile_c source;
    X10ClassDef classDef;
    Flags flags;
    X10ClassDoc containingClass;
    X10ClassDoc superclass;
    Type superclassType;
    X10PackageDoc containingPackage;
    X10RootDoc rootDoc;
    Map<String, X10TypeVariable> typeParams;
    Map<String, X10FieldDoc> fields;
    Map<String, X10ConstructorDoc> constructors;
    Map<String, MethodDoc> methods;
    List<X10ClassDoc> innerClasses;
    List<X10ClassDoc> interfaces;
    List<Type> interfaceTypes;
    boolean included;

    X10FieldDoc[] includedFields;
    X10ConstructorDoc[] includedConstructors;
    MethodDoc[] includedMethods; // MethodDoc not X10MethodDoc, because
                                 // X10TypeDefDoc (which implements MethodDoc)
                                 // objects are also methods; applies to field
                                 // "methods" also
    X10ClassDoc[] includedInnerClasses;

    public X10ClassDoc(X10ClassDef classDef, X10ClassDoc containingClass, String comment) {
        // super(comment);
        this.classDef = classDef;
        flags = classDef.flags();
        if (flags.isStruct()) flags = flags.clearFinal();       // remove redundancy
        if (flags.isInterface()) flags = flags.clearAbstract(); // remove redundancy
        this.containingClass = containingClass;
        this.rootDoc = X10RootDoc.getRootDoc();
        this.fields = new LinkedHashMap<String, X10FieldDoc>();
        this.constructors = new LinkedHashMap<String, X10ConstructorDoc>();
        this.methods = new LinkedHashMap<String, MethodDoc>();
        this.innerClasses = new ArrayList<X10ClassDoc>();
        this.interfaces = new ArrayList<X10ClassDoc>();
        this.interfaceTypes = new ArrayList<Type>();
        this.included = false;
        this.includedFields = null;

        this.superclass = null;
        this.superclassType = null;

        initTypeParameters();
        super.processComment(comment);
        // addDeclTag(declString());
    }

    public MemberDoc getMemberDoc(String name) {
        for (X10FieldDoc doc : fields.values()) {
            if (doc.name().equals(name)) return doc;
        }
        String shortname = name;
        String signature = "()";
        int index = name.indexOf("(");
        if (index != -1) {
            shortname = name.substring(0, name.indexOf("("));
            signature = name.substring(index);
            signature = makeQualifiedParams(signature);
        }

        index = shortname.indexOf("[");
        if (index != -1) {
            shortname = shortname.substring(0, index);
        }

        for (X10ConstructorDoc doc : constructors.values()) {
            if (doc.name().equals(shortname) && doc.signature().equals(signature)) {
                return doc;
            }
        }
        for (MethodDoc doc : methods.values()) {
            if (doc.name().equals(shortname) && doc.signature().equals(signature)) {
                return doc;
            }
        }
        return null;
    }

    private String makeQualifiedParams(String signature) {
        String sig = "";
        String delim = "(),>";
        StringTokenizer tok = new StringTokenizer(signature, delim, true);
        while (tok.hasMoreTokens()) {
            String token = tok.nextToken();
            if (delim.contains(token)) {
                sig += token;
                if (token.equals(">")) {
                    sig += " ";
                }
            }

            else {
                token = token.trim();
                if (token.length() > 0) {
                    X10ClassDoc doc = X10RootDoc.getRootDoc().findClass(this, token.trim());
                    if (doc != null) {
                        sig += doc.qualifiedName();
                    }

                    else {
                        sig += token;
                    }
                }
            }
        }

        return sig;
    }

    public void setSuperclass(X10ClassDoc superclass) {
        this.superclass = superclass;
    }

    public void setSuperclassType(Type superclassType) {
        this.superclassType = superclassType;
    }

    void initTypeParameters() {
        List<ParameterType> params = classDef.typeParameters();
        typeParams = new LinkedHashMap<String, X10TypeVariable>(params.size());
        Ref<CConstraint> inv = classDef.classInvariant();
        // System.out.println("classInvariant: " + ((inv == null) ? "" :
        // inv.get()));
        TypeConstraint c = classDef.typeGuard().get();
        for (ParameterType p : params) {
            X10TypeVariable v = new X10TypeVariable(p, this);
            v.setTypeGuard(c);
            typeParams.put(typeParameterKey(p), v);
        }
        // the following are commented because classDef.{classInvariant,
        // typeBounds} etc. returns
        // null for x10.lang.Ref
        // System.out.println("TypeBounds: " + classDef.typeBounds().get());
        // System.out.println("TypeGuard: " + classDef.typeGuard().get());
        for (SubtypeConstraint s : classDef.typeGuard().get().terms()) {
            // System.out.println("SubtypeConstraint: " + s);
        }
    }

    // initializations that are common to specified and unspecified classes;
    // this method initializes classes, interfaces
    // and packages related to this class, e.g., super classes, implemented
    // interfaces, containing package; calls from
    // here may set off a chain of recursive calls, e.g., creation of ClassDoc
    // objects for all ancestor classes of this
    // class
    public void initializeRelatedEntities() {
        // set package of class

        String path = classDef.position().file().replace(".x10", "");
        if (classDef.name() != null) {
            path = path.replace(classDef.name().toString(), "");
        }
        this.containingPackage = rootDoc.getPackage(classDef.package_(), path);
        this.containingPackage.addClass(this);

        // obtain ClassDoc and Type objects for superclass
        Ref<? extends polyglot.types.Type> reft = classDef.superType();
        polyglot.types.Type t = ((reft == null) ? null : reft.get());
        X10ClassDef cdef = (X10ClassDef) ((t == null) ? null : t.toClass().def());
        this.superclass = rootDoc.getUnspecClass(cdef);
        this.superclassType = rootDoc.getType(t);

        // add interfaces implemented by the class
        addInterfaces();
    }

    public void addInterfaces() {
        for (Ref<? extends polyglot.types.Type> ref : classDef.interfaces()) {
            this.interfaces.add(rootDoc.getUnspecClass((X10ClassDef) ref.get().toClass().def()));
            this.interfaceTypes.add(rootDoc.getType(ref.get()));
        }

        // System.out.println("---- start interface tree ----");
        // System.out.println("X10ClassDoc{" + classDef + "}.interfaceTypes = "
        // +
        // Arrays.toString(interfaceTypes.toArray(new Type[0])));
        // for (Type y: interfaceTypes) {
        // printInterfaceTree(y);
        // }
        // System.out.println("---- end interface tree ----");
    }

    public static void printInterfaceTree(Type t) {
        if (t instanceof X10ParameterizedType) {
            X10ParameterizedType x = (X10ParameterizedType) t;
            System.out.println("X10ParameterizedType{" + x + "}.interfaceTypes = "
                    + Arrays.toString(x.interfaceTypes()));
            for (Type y : x.interfaceTypes()) {
                printInterfaceTree(y);
            }
        }
    }

    public String declString() {
        // a declaration is needed if the class has associated constraints, or
        // extends classes, implements interfaces that
        // are X10 specific (contain closures, constraints)
        Ref<CConstraint> refC = classDef.classInvariant();
        Ref<TypeConstraint> refG = classDef.typeGuard();
        boolean needsDeclForSuperOrInt = false; // signifies that the
                                                // declaration is needed because
                                                // either the super class
                                                // or an implemented interface
                                                // is X10-specific, as opposed
                                                // to it being needed
                                                // because of associated class
                                                // constraints
        if (!(X10Type.isX10Specific(this.superclassType))) {
            needsDeclForSuperOrInt = false;
            for (Type t : this.interfaceTypes) {
                if (X10Type.isX10Specific(t)) {
                    needsDeclForSuperOrInt = true;
                    break;
                }
            }
        } else {
            needsDeclForSuperOrInt = true;
        }

        if ((refC == null) && (refG == null) && !needsDeclForSuperOrInt) {
            return null;
        }

        String temp = classDef.asType().toString();
        String result = "<B>Declaration</B>: <TT>" + name();
        TypeVariable[] params = typeParameters();
        if (refG != null && (params.length > 0)) {
            result += Arrays.toString(params);
        }
        String constraint = "{";
        if (refC != null) {
            refC.get();
            refC = classDef.classInvariant(); // attempt to force lazy
                                              // initialization
            String inv = refC.get().toString();
            int len = inv.length();
            if (len > 2) {
                constraint += inv.substring(1, len - 2); // remove leading '{',
                                                         // trailing '}'
                if (refG != null) {
                    constraint += ", ";
                }
            }
        }
        if (refG != null) {
            // String typeGuard = refG.get().toString();
            // int len = typeGuard.length();
            // if (len > 2) {
            // constraint += typeGuard.substring(1, len-2); // remove leading
            // '[', trailing ']'
            // }
            boolean first = true;
            for (SubtypeConstraint st : refG.get().terms()) {
                // Type sub = rootDoc.getType(st.subtype());
                // Type sup = rootDoc.getType(st.supertype());
                if (first) {
                    first = false;
                } else {
                    constraint += ", ";
                }
                // constraint += linkTag(st.subtype()) + " <: " +
                // linkTag(st.supertype());
                constraint += st.toString();
            }
        }
        if (!constraint.equals("{")) {
            result += constraint + "}";
        } else if (!needsDeclForSuperOrInt) {
            // the declaration string is not needed for super classes or
            // implemented interfaces, and there are
            // no class constraints to display
            return null;
        }

        if (this.superclass != null) {
            result += " extends " + X10Type.toString(this.superclassType);
        }

        if (this.interfaceTypes.size() > 0) {
            result += (this.isInterface() ? " extends " : " implements ");
            boolean first = true;
            for (Type t : this.interfaceTypes) {
                if (first) {
                    first = false;
                    result += X10Type.toString(t);
                } else {
                    result += ", " + X10Type.toString(t);
                }
            }
        }

        result += ".</TT><PRE>\n</PRE>"; // the period before <TT> is required
                                         // because the declaration string is
                                         // added as a prefix to the first
                                         // sentence, and is displayed in the
                                         // "Class Summary" table, where
                                         // newlines are replaced with single
                                         // spaces;
                                         // the period separates the declaration
                                         // from the first sentence in the
                                         // "Class Summary" section
        return result;
    }

    public String linkTag(polyglot.types.Type t) {
        // if (t instanceof X10TypeVariable) {
        // return ("{@link " + name() + " " + ((X10TypeVariable) t).typeName() +
        // "}");
        // }
        // else if (t instanceof X10ClassDoc) {
        // X10ClassDoc cd = ((X10ClassDoc) t);
        // if (cd.isIncluded()) {
        // return ("{@link " + cd.qualifiedName() + " " + cd.name() + "}");
        // }
        // else {
        // return cd.name();
        // }
        // }
        // else {
        // return t.typeName();
        // }
        if (t instanceof ParameterType) {
            return ("{@link " + name() + " " + ((ParameterType) t).name().toString() + "}");
        }
        X10ClassDef classDef = (X10ClassDef) t.toClass().def();
        if (classDef.typeParameters().size() == 0) {
            X10ClassDoc cd = (X10ClassDoc) rootDoc.getClass(classDef);
            if (cd == null) {
                return classDef.fullName().toString();
            } else {
                if (cd.isIncluded()) {
                    return ("{@link " + cd.qualifiedName() + " " + cd.name() + "}");
                } else {
                    return cd.name();
                }
            }
        } else {
            return t.toString();
        }

    }

    public void addDeclsToMemberComments() {
        for (FieldDoc fd : fields.values()) {
            X10Doc d = (X10Doc) fd;
            d.addDeclTag(d.declString());
        }
        for (ConstructorDoc doc : constructors.values()) {
            X10Doc d = (X10Doc) doc;
            d.addDeclTag(d.declString());
        }
        for (MethodDoc md : methods.values()) {
            X10Doc d = (X10Doc) md;
            d.addDeclTag(d.declString());
        }
    }

    public static String fieldKey(X10FieldDef fd) {
        return fd.name().toString();
    }

    public static String methodKey(X10ConstructorDef cd) {
        return cd.signature();
    }

    public static String methodKey(X10MethodDef md) {
        // return md.name().toString() + X10MethodDoc.signature(md);
        return md.signature();
    }

    public static String methodKey(TypeDef td) {
        // return md.name().toString() + X10MethodDoc.signature(md);
        return td.signature();
    }

    public static String typeParameterKey(ParameterType p) {
        return p.name().toString();
    }

    public void setSource(X10SourceFile_c source) {
        this.source = source;
    }

    public X10FieldDoc updateField(X10FieldDef fdef, String comments) {
        X10FieldDoc fd = getField(fdef);
        if (fd == null) {
            fd = new X10FieldDoc(fdef, this, comments);
            fields.put(fieldKey(fdef), fd);
        } else {
            String existingCmnt = fd.commentText();
            assert (existingCmnt.equals("") || existingCmnt.equals(comments)) : "X10ClassDoc.updateField("
                    + fieldKey(fdef) + ",...): mismatch between existing and given comments";
            // fd.setIncluded(true);
            fd.setRawCommentText(comments);
        }
        return fd;
    }

    public X10ConstructorDoc updateConstructor(X10ConstructorDef cdef, String comments) {
        X10ConstructorDoc cd = getConstructor(cdef);
        if (cd == null) {
            cd = new X10ConstructorDoc(cdef, this, comments);
            constructors.put(methodKey(cdef), cd);
        } else {
            String existingCmnt = cd.commentText();
            assert (existingCmnt.equals("") || existingCmnt.equals(comments)) : "X10ClassDoc.updateConstructor("
                    + methodKey(cdef) + ",...): mismatch between existing and given comments";
            // cd.setIncluded(true);
            cd.setRawCommentText(comments);
        }
        return cd;
    }

    public MethodDoc updateMethod(X10MethodDef mdef, String comments) {
        MethodDoc md = getMethod(mdef);
        if (md == null) {
            md = new X10MethodDoc(mdef, this, comments);
            methods.put(methodKey(mdef), md);
        } else {
            String existingCmnt = md.commentText();
            assert (existingCmnt.equals("") || existingCmnt.equals(comments)) : "X10ClassDoc.updateMethod("
                    + methodKey(mdef) + ",...): mismatch between existing and given comments";
            // md.setIncluded(true);
            // commented to avoid duplicate addition of declaration comments
            // TODO: determine what needs to be done here or use another
            // method/method name
            md.setRawCommentText(comments);
        }
        return md;
    }

    public MethodDoc updateTypeDef(TypeDef tdef, String comments) {
        MethodDoc td = getMethod(tdef);
        if (td == null) {
            td = new X10TypeDefDoc(tdef, this, comments);
            methods.put(methodKey(tdef), td);
        } else {
            String existingCmnt = td.commentText();
            assert (existingCmnt.equals("") || existingCmnt.equals(comments)) : "X10ClassDoc.updateTypeDef("
                    + methodKey(tdef) + ",...): mismatch between existing and given comments";
            // td.setIncluded(true);
            // commented to avoid duplicate addition of declaration comments
            // TODO: determine what needs to be done here or use another
            // method/method name
            td.setRawCommentText(comments);
        }
        return td;
    }

    public void addInnerClass(X10ClassDoc cd) {
        innerClasses.add(cd);
        if (X10RootDoc.printSwitch)
            System.out.println("X10ClassDoc.addInnerClass(" + cd.name() + "); innerClasses.size() = "
                    + innerClasses.size());
    }

    public void addInterface(X10ClassDoc intClassDoc) {
        interfaces.add(intClassDoc);
    }

    // set this.included according to the access modifier filter
    public void setIncluded() {
        this.included = X10Doc.isIncluded(rootDoc.accessModFilter(), this);
    }

    public void setPackage(X10PackageDoc pkg) {
        this.containingPackage = pkg;
    }

    /**
     * @since 1.8
     * @return
     */
    public Type getElementType() {
        // TODO Auto-generated method stub
        return null;
    }

    public X10FieldDoc getField(String name) {
        return fields.get(name);
    }

    public X10FieldDoc getField(X10FieldDef fdef) {
        return fields.get(fieldKey(fdef));
    }

    public X10ConstructorDoc getConstructor(String name) {
        return constructors.get(name);
    }

    public X10ConstructorDoc getConstructor(X10ConstructorDef cdef) {
        return constructors.get(methodKey(cdef));
    }

    public MethodDoc getMethod(String name) {
        return methods.get(name);
    }

    public MethodDoc getMethod(X10MethodDef mdef) {
        // System.out.println("X10ClassDoc.getMethod: methods.keySet() = " +
        // Arrays.toString(methods.keySet().toArray(new String[0])));
        return methods.get(methodKey(mdef));
    }

    public MethodDoc getMethod(TypeDef tdef) {
        return methods.get(methodKey(tdef));
    }

    public X10TypeVariable getTypeVariable(ParameterType p) {
        return typeParams.get(typeParameterKey(p));
    }

    public AnnotationDesc[] annotations() {
        // TODO Auto-generated method stub
        return new AnnotationDesc[0];
    }

//    /**
//     * Note that AnnotatedType is introduced in 1.8.
//     * (uncomment this method to build x10doc with Java 8)
//     * @since 1.8
//     * @return
//     */
//    public com.sun.javadoc.AnnotatedType asAnnotatedType() {
//        // TODO Auto-generated method stub
//        return null;        
//    }

    // Return this type as an AnnotationTypeDoc if it represents an annotation
    // type, null otherwise.
    public AnnotationTypeDoc asAnnotationTypeDoc() {
        return null;
    }

    public ClassDoc asClassDoc() {
        return this;
    }

    public ParameterizedType asParameterizedType() {
        return null;
    }

    public TypeVariable asTypeVariable() {
        return null;
    }

    public WildcardType asWildcardType() {
        return null;
    }

    public ConstructorDoc[] constructors() {
        if (X10RootDoc.printSwitch) System.out.println("ClassDoc.constructors() called for " + name());
        // return constructors.values().toArray(new ConstructorDoc[0]);

        // HACK: it seems that the standard doclet does not call
        // X10ClassDoc.constructors(boolean) for the desired
        // set of included constructors; so, force a call to it from here
        return constructors(true);
    }

    public ConstructorDoc[] constructors(boolean arg0) {
        if (X10RootDoc.printSwitch) System.out.println("ClassDoc.constructors(boolean) called for " + name());
        if (arg0) {
            if (includedConstructors != null) {
                return includedConstructors;
            }
            int size = 0;
            Collection<X10ConstructorDoc> constrSet = constructors.values();
            for (ConstructorDoc cd : constrSet) {
                if (cd.isIncluded()) {
                    size++;
                }
            }
            includedConstructors = new X10ConstructorDoc[size];
            int i = 0;
            for (X10ConstructorDoc cd : constrSet) {
                if (cd.isIncluded()) {
                    includedConstructors[i++] = cd;
                }
            }
            return includedConstructors;
        }
        return constructors();
    }

    public ClassDoc containingClass() {
        return containingClass;
    }

    public PackageDoc containingPackage() {
        if (X10RootDoc.printSwitch) {
            System.out.print("ClassDoc.containingPackage() called for " + name());
            // new Exception().printStackTrace();
            System.out.println("; containingPackage.name() = " + containingPackage.name());
        }
        return containingPackage;
    }

    public boolean definesSerializableFields() {
        return false;
    }

    public String dimension() {
        ClassType classType = classDef.asType();
        return (classType.isArray() ? String.valueOf(classType.toArray().dims()) : "");
    }

    public FieldDoc[] enumConstants() {
        // TODO Auto-generated method stub
        return new FieldDoc[0];
    }

    public FieldDoc[] fields() {
        if (X10RootDoc.printSwitch) System.out.println("ClassDoc.fields() called. fields.size() = " + fields.size());
        return fields.values().toArray(new FieldDoc[0]);
    }

    public FieldDoc[] fields(boolean arg0) {
        if (X10RootDoc.printSwitch) System.out.println("ClassDoc.fields(boolean) called for " + name());
        if (arg0) {
            if (includedFields != null) {
                return includedFields;
            }
            int size = 0;
            Collection<X10FieldDoc> fieldsSet = fields.values();
            for (X10FieldDoc fd : fieldsSet) {
                if (fd.isIncluded()) {
                    size++;
                }
            }
            includedFields = new X10FieldDoc[size];
            int i = 0;
            for (X10FieldDoc fd : fieldsSet) {
                if (fd.isIncluded()) {
                    includedFields[i++] = fd;
                }
            }
            return includedFields;
        }
        return fields();
    }

    public ClassDoc findClass(String arg0) {
        // TODO Auto-generated method stub
        return rootDoc.classNamed(arg0);
    }

    public ClassDoc[] importedClasses() {
        // TODO Auto-generated method stub
        if (X10RootDoc.printSwitch) System.out.println("ClassDoc.importedClasses() called for " + name());

        return new ClassDoc[0];
    }

    public PackageDoc[] importedPackages() {
        // TODO Auto-generated method stub
        if (X10RootDoc.printSwitch) System.out.println("ClassDoc.importedPackages() called for " + name());
        return new PackageDoc[0];
    }

    public ClassDoc[] innerClasses() {
        if (X10RootDoc.printSwitch)
            System.out.println("" + name() + ".innerClasses() called; innerClasses.size() = " + innerClasses.size());
        return innerClasses.toArray(new ClassDoc[0]);
    }

    public ClassDoc[] innerClasses(boolean arg0) {
        if (X10RootDoc.printSwitch)
            System.out.println("" + name() + ".innerClasses() called; innerClasses.size() = " + innerClasses.size());
        // return innerClasses.toArray(new ClassDoc[0]);

        if (arg0) {
            if (includedInnerClasses != null) {
                return includedInnerClasses;
            }
            int size = 0;
            // Collection<X10ClassDoc> classes = innerClasses.values();
            for (X10ClassDoc cd : innerClasses) {
                if (cd.isIncluded()) {
                    size++;
                }
            }
            includedInnerClasses = new X10ClassDoc[size];
            int i = 0;
            for (X10ClassDoc cd : innerClasses) {
                if (cd.isIncluded()) {
                    includedInnerClasses[i++] = cd;
                }
            }
            return includedInnerClasses;
        }
        return innerClasses();
    }

    /**
     * Return interfaces implemented by this class or interfaces extended by
     * this interface. Includes only directly-declared interfaces, not inherited
     * interfaces.
     */
    public ClassDoc[] interfaces() {
        if (X10RootDoc.printSwitch) System.out.println("ClassDoc.interfaces() called for " + name());
        return interfaces.toArray(new ClassDoc[0]);
    }

    /**
     * Return interfaces implemented by this class or interfaces extended by
     * this interface. Includes only directly-declared interfaces, not inherited
     * interfaces.
     */
    public Type[] interfaceTypes() {
        if (X10RootDoc.printSwitch) System.out.println("ClassDoc.interfaceTypes() called for " + name());
        // needs to be updated to handle generic types; the result is an array
        // of ClassDoc
        // or ParametrizedType objects
        return interfaceTypes.toArray(new Type[0]);
    }

    public boolean isAbstract() {
        return flags.isAbstract();
    }

    public boolean isClass() {
        return (!isInterface());
    }

    @Override
    public boolean isAnnotationType() {
        // X10TypeSystem ts = (X10TypeSystem) classDef.typeSystem();
        // try {
        // return ts.isSubtype(classDef.asType(), (polyglot.types.Type)
        // ts.forName(QName.make("x10.lang.annotations.Annotation")),
        // ts.emptyContext());
        // } catch (SemanticException e) {
        return false;
        // }
    }

    @Override
    public boolean isEnum() {
        // TODO Auto-generated method stub
        return super.isEnum();
    }

    @Override
    public boolean isError() {
        TypeSystem ts = (TypeSystem) classDef.typeSystem();
        return ts.isSubtype(classDef.asType(), ts.Error(), ts.emptyContext());
    }

    @Override
    public boolean isException() {
        TypeSystem ts = (TypeSystem) classDef.typeSystem();
        return ts.isSubtype(classDef.asType(), ts.Exception(), ts.emptyContext());
    }

    @Override
    public boolean isIncluded() {
        return included;
    }

    @Override
    public boolean isInterface() {
        return flags.isInterface();
    }

    // the following assumes that isEnum, isError, isException have been called
    // earlier
    @Override
    public boolean isOrdinaryClass() {
        return !flags.isInterface();
    }

    public boolean isPackagePrivate() {
        return flags.isPackage();
    }

    public boolean isPrimitive() {
        // nothing in X10 is primitive
        return false;
    }

    public boolean isPrivate() {
        return flags.isPrivate();
    }

    public boolean isProtected() {
        return flags.isProtected();
    }

    public boolean isPublic() {
        return flags.isPublic();
    }

    public boolean isSerializable() {
        // X10's notion of serialization is different from that of Java
        return false;
    }

    public boolean isStatic() {
        return flags.isStatic();
    }

    public MethodDoc[] methods() {
        if (X10RootDoc.printSwitch) System.out.println("ClassDoc.methods() called for " + name());
        return methods.values().toArray(new MethodDoc[0]);
    }

    public MethodDoc[] methods(boolean arg0) {
        if (X10RootDoc.printSwitch) System.out.println("ClassDoc.methods(boolean) called for " + name());
        if (arg0) {
            if (includedMethods != null) {
                return includedMethods;
            }
            int size = 0;
            Collection<MethodDoc> methodsSet = methods.values();
            for (MethodDoc md : methodsSet) {
                if (md.isIncluded()) {
                    size++;
                }
            }
            includedMethods = new MethodDoc[size];
            int i = 0;
            for (MethodDoc md : methodsSet) {
                if (md.isIncluded()) {
                    includedMethods[i++] = (MethodDoc) md;
                }
            }
            return includedMethods;
        }
        return methods();
    }

    public String modifiers() {
        if (X10RootDoc.printSwitch) System.out.println("ClassDoc.modifiers() called for " + name());
        return flags.toString();
    }

    public int modifierSpecifier() {
        return X10Doc.flagsToModifierSpecifier(flags.flags());
    }

    public String name() {
        if (X10RootDoc.printSwitch) System.out.println("ClassDoc.name() called for " + classDef.name());
        String contClassName = ((containingClass == null) ? "" : (containingClass.name() + "."));
        return (contClassName + (classDef.isAnonymous() ? "<anonymous class>" : classDef.name().toString()));
        // return (contClassName + classDef.name().toString() +
        // classDef.classInvariant().get() + classDef.typeBounds().get());
    }

    public String qualifiedName() {
        if (X10RootDoc.printSwitch) System.out.println("ClassDoc.qualifiedName() called for " + name());
        return classDef.fullName().toString();
        // classDef.toString() also returns the fully qualified name
        // return classDef.asType().toString();
        // return "!!X10ClassDoc:qualifiedName!!";
    }

    public String qualifiedTypeName() {
        // classDef.asType().toString() =
        // classDef.asType().fullName().toString()
        // for ValRail[Place]{...}
        String result = classDef.asType().fullName().toString();
        if (X10RootDoc.printSwitch) System.out.println("ClassDoc{" + name() + "}.qualifiedTypeName() = " + result);
        return "!!X10ClassDoc:qualifiedTypeName!!";
    }

    public FieldDoc[] serializableFields() {
        // TODO Auto-generated method stub
        if (X10RootDoc.printSwitch) System.out.println("ClassDoc.serializableFields() called for " + name());
        return new FieldDoc[0];
    }

    public MethodDoc[] serializationMethods() {
        // TODO Auto-generated method stub
        if (X10RootDoc.printSwitch) System.out.println("ClassDoc.serializationMethods() called for " + name());
        return new MethodDoc[0];
    }

    public String simpleTypeName() {
        if (X10RootDoc.printSwitch) System.out.println("ClassDoc.simpleTypeName() called for " + name());
        return name();
        // return "X10ClassDoc!!simpleTypeName!!";
    }

    public boolean subclassOf(ClassDoc arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public ClassDoc superclass() {
        if (X10RootDoc.printSwitch) System.out.println("ClassDoc.superClass() called for " + name());
        if (isInterface()) {
            return null;
        }
        // HACK: Sidestep a bug in Javadoc 1.6 tool where the code in
        // com.sun.tools.doclets.internal.toolkit.util.Util
        // Util.getFirstVisibleSuperClass
        // assumes that every class is either public or has a public ancestor
        // superclass. This is not true in X10 and since it is not viable to fix
        // the bug in javadoc, we instead lie here and if all superclasses of
        // this
        // class are not public, we lie are return null (indicating that this
        // class
        // is the top of its hierarchy).
        if (superclass == null) return superclass;
        if (superclass.isPublic()) return superclass;
        X10ClassDoc sc = superclass;
        while (sc != null && !sc.isPublic()) {
            sc = sc.superclass;
        }
        return sc;
    }

    public Type superclassType() {
        if (X10RootDoc.printSwitch) System.out.println("ClassDoc.superClassType() called for " + name());
        if (isInterface()) {
            return null;
        }
        // HACK: Sidestep a bug in Javadoc 1.6 tool where the code in
        // com.sun.tools.doclets.internal.toolkit.util.Util
        // Util.getFirstVisibleSuperClass
        // assumes that every class is either public or has a public ancestor
        // superclass. This is not true in X10 and since it is not viable to fix
        // the bug in javadoc, we instead lie here and if all superclasses of
        // this
        // class are not public, we lie are return null (indicating that this
        // class
        // is the top of its hierarchy).
        if (superclassType == null) return superclassType;
        Type st = superclassType;
        while (st instanceof X10ClassDoc) {
            X10ClassDoc stdoc = (X10ClassDoc) st.asClassDoc();
            if (stdoc.isPublic()) return st;
            st = stdoc.superclassType;
        }

        return st;
    }

    public String typeName() {
        if (X10RootDoc.printSwitch) System.out.println("ClassDoc.typeName() called for " + name());
        // return classDef.asType().fullName().toString();
        return "!!X10ClassDoc:TYPENAME!!";
    }

    public TypeVariable[] typeParameters() {
        if (X10RootDoc.printSwitch) System.out.println("ClassDoc.typeParameters() called for " + name());
        return typeParams.values().toArray(new TypeVariable[0]);
    }

    public ParamTag[] typeParamTags() {
        // TODO Auto-generated method stub
        if (X10RootDoc.printSwitch) System.out.println("ClassDoc.typeParamTags() called for " + name());
        return new ParamTag[0];
    }

    public String toString() {
        return name() + classDef.classInvariant().get() + classDef.typeBounds().get();
    }

    public boolean isExternalizable() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isFinal() {
        return flags.isFinal();
    }

    /**
     * @since 1.8
     * @return
     */
    public boolean isFunctionalInterface() {
        // TODO Auto-generated method stub
        return false;
    }

    // public static String fieldKey(X10FieldDoc fd) {
    // return fd.name();
    // }
    //
    // public static String methodKey(X10ConstructorDoc cd) {
    // return methodKey(cd.getConstructorDef());
    // }
    //
    // public static String methodKey(X10MethodDoc m) {
    // return methodKey(m.getMethodDef());
    // }

    // public X10FieldDoc addField(X10FieldDoc fd) {
    // String name = fieldKey(fd);
    // X10FieldDoc existingFD = fields.get(name);
    // if (existingFD != null)
    // return existingFD;
    // else {
    // fields.put(fd.name(), fd);
    // return fd;
    // }
    // }
    //
    // public X10ConstructorDoc addConstructor(X10ConstructorDoc cd) {
    // String name = methodKey(cd);
    // X10ConstructorDoc existingCD = constructors.get(name);
    // if (existingCD != null)
    // return existingCD;
    // else {
    // constructors.put(name, cd);
    // return cd;
    // }
    // }
    //
    // public X10MethodDoc addMethod(X10MethodDoc md) {
    // String sig = methodKey(md);
    // X10MethodDoc existingMD = methods.get(sig);
    // if (existingMD != null)
    // return existingMD;
    // else {
    // methods.put(sig, md);
    // return md;
    // }
    // }

    // this method is intended to initialize fields such as includedFields,
    // includedMethods, but class cast exceptions
    // were thrown when converting ProgramElementDoc[] to X10FieldDoc[] in
    // "includedFields = (X10FieldDoc[]) includedMembers(fields.values())"
    public static ProgramElementDoc[] includedMembers(Collection<? extends ProgramElementDoc> values) {
        int size = 0;
        for (ProgramElementDoc pd : values) {
            if (pd.isIncluded()) {
                size++;
            }
        }
        ProgramElementDoc[] result = new ProgramElementDoc[size];
        int i = 0;
        for (ProgramElementDoc pd : values) {
            if (pd.isIncluded()) {
                result[i++] = pd;
            }
        }
        return result;
    }

}
