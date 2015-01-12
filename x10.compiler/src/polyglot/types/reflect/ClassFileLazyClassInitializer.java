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
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.types.reflect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import polyglot.main.Reporter;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.reflect.InnerClasses.Info;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.StringUtil;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.constants.ConstantValue;
import x10.util.CollectionFactory;

/**
 * A lazy initializer for classes loaded from a .class file.
 * 
 * @author nystrom
 * 
 * IMPORTANT: to avoid infinite loops, this code should not call any code that
 * might load a class (in particular, this one). ts.Object() should not be
 * called.
 */
public class ClassFileLazyClassInitializer {
    protected ClassFile clazz;
    protected TypeSystem ts;
    protected Reporter reporter;
    protected ClassDef ct;

    protected boolean init;
    protected boolean constructorsInitialized;
    protected boolean fieldsInitialized;
    protected boolean interfacesInitialized;
    protected boolean memberClassesInitialized;
    protected boolean methodsInitialized;
    protected boolean superclassInitialized;

    public ClassFileLazyClassInitializer(ClassFile file, TypeSystem ts) {
        this.clazz = file;
        this.ts = ts;
        this.reporter = ts.extensionInfo().getOptions().reporter;
    }
    
    public void setClass(ClassDef ct) {
        this.ct = ct;
    }

    public boolean fromClassFile() {
        return true;
    }

    /**
     * Create a position for the class file.
     */
    public Position position() {
    	String path = clazz.classFileSource().getAbsolutePath();
    	if (path.endsWith(".jar")){
    		path += ":" + clazz.name() + ".class";
    	}
        return new Position(null, path);
    }

    /**
     * Create the type for this class file.
     */
    protected ClassDef createType() throws SemanticException {
        // The name is of the form "p.q.C$I$J".
        String name = clazz.classNameCP(clazz.getThisClass());

        if (reporter.should_report(Reporter.loader, 2))
            reporter.report(2, "creating ClassType for " + name);

        // Create the ClassType.
        ClassDef ct = ts.createClassDef();
        this.setClass(ct);

        ct.setFromJavaClassFile();

        ct.flags(ts.flagsForBits(clazz.getModifiers()));
        ct.position(position());

        // This is the "p.q" part.
        String packageName = StringUtil.getPackageComponent(name);

        // Set the ClassType's package.
        if (!packageName.equals("")) {
            ct.setPackage(Types.ref(ts.packageForName(QName.make(packageName))));
        }

        // This is the "C$I$J" part.
        String className = StringUtil.getShortNameComponent(name);

        InnerClasses.Info innerClassInfo = clazz.getInnerClassInfo();
        String outerName; // This will be "p.q.C$I"
        String innerName; // This will be "J"

        outerName = name;
        innerName = null;

        //int dollar = outerName.lastIndexOf('$');
        //
        //if (dollar >= 0) {
        //    outerName = name.substring(0, dollar);
        //    innerName = name.substring(dollar + 1);
        //
        //    // Lazily load the outer class.
        //    ct.outer(defForName(outerName));
        //}
        //else {
        //    outerName = name;
        //    innerName = null;
        //}

        if (innerClassInfo != null) {
            outerName = clazz.classNameCP(innerClassInfo.outerClassIndex);
            innerName = clazz.className(innerClassInfo.nameIndex);
            // Lazily load the outer class.
            ct.outer(defForName(outerName));
        }

        ClassDef.Kind kind = ClassDef.TOP_LEVEL;

        if (innerName != null) {
            // A nested class. Parse the class name to determine what kind.
            StringTokenizer st = new StringTokenizer(className, "$");

            while (st.hasMoreTokens()) {
                String s = st.nextToken();

                if (Character.isDigit(s.charAt(0))) {
                    // Example: C$1
                    kind = ClassDef.ANONYMOUS;
                }
                else if (kind == ClassDef.ANONYMOUS) {
                    // Example: C$1$D
                    kind = ClassDef.LOCAL;
                }
                else {
                    // Example: C$D
                    kind = ClassDef.MEMBER;
                }
            }
        }

        if (reporter.should_report(Reporter.loader, 3))
            reporter.report(3, name + " is " + kind);

        ct.kind(kind);

        if (ct.isTopLevel()) {
            ct.name(Name.make(className));
        }
        else if (ct.isMember() || ct.isLocal()) {
            ct.name(Name.make(innerName));
        }
        
        initSuperclass();
        initInterfaces();

        initMemberClasses();

        initFields();
        initMethods();
        initConstructors();

        return ct;
    }

    /**
     * Create the type for this class file.
     */
    public ClassDef type() throws SemanticException {
        if (ct == null) {
            ct = createType();
        }
        return ct;
    }

    /**
     * Return an array type.
     * @param t The array base type.
     * @param dims The number of dimensions of the array.
     * @return An array type.
     */
    protected Ref<? extends Type> arrayOf(Type t, int dims) {
        return arrayOf(Types.<Type>ref(t), dims);
    }
    
    /**
     * Return an array type.
     * @param t The array base type.
     * @param dims The number of dimensions of the array.
     * @return An array type.
     */
    protected Ref<? extends Type> arrayOf(Ref<? extends Type> t, int dims) {
        if (dims == 0) {
            return t;
        }
        else {
            return Types.<Type>ref(ts.arrayOf(t, dims));
        }
    }

    /**
     * Return a list of types based on a JVM type descriptor string.
     * @param str The type descriptor.
     * @param bounds Generic type bounds in scope (may be null).
     * @return The corresponding list of types.
     */
    protected List<Ref<? extends Type>> typeListForString(String str, Map<String,Ref<? extends Type>> bounds) {
        List<Ref<? extends Type>> types = new ArrayList<Ref<? extends Type>>(1);

        updateTypeListFromString(types, str, 0, bounds);

        if (reporter.should_report(Reporter.loader, 4))
            reporter.report(4, "parsed \"" + str + "\" -> " + types);

        return types;
    }
    private int updateTypeListFromString(List<Ref<? extends Type>> types, String str, int begin, Map<String,Ref<? extends Type>> bounds) {
        int i = begin;
        while (i < str.length()) {
            if (str.charAt(i) == '>') return i;
            i = updateTypeFromString(types, str, i, bounds);
            i++;
        }
        return i;
    }
    private int updateTypeFromString(List<Ref<? extends Type>> types, String str, int begin, Map<String,Ref<? extends Type>> bounds) {
        int i = begin;

        int dims = 0;
        while (str.charAt(i) == '[') {
            dims++;
            i++;
        }

        switch (str.charAt(i)) {
            case 'Z':
                types.add(arrayOf(ts.Boolean(), dims));
                break;
            case 'B':
                types.add(arrayOf(ts.Byte(), dims));
                break;
            case 'S':
                types.add(arrayOf(ts.Short(), dims));
                break;
            case 'C':
                types.add(arrayOf(ts.Char(), dims));
                break;
            case 'I':
                types.add(arrayOf(ts.Int(), dims));
                break;
            case 'J':
                types.add(arrayOf(ts.Long(), dims));
                break;
            case 'F':
                types.add(arrayOf(ts.Float(), dims));
                break;
            case 'D':
                types.add(arrayOf(ts.Double(), dims));
                break;
            case 'V':
                types.add(arrayOf(ts.Void(), dims));
                break;
            case 'T': { // Type parameter - use the erased bound
                int start = ++i;
                while (i < str.length()) {
                    if (str.charAt(i) == ';') {
                        break;
                    }
                    i++;
                }
                Ref<? extends Type> t = bounds.get(str.substring(start, i));
                if (t == null) {
                    t = typeForName("x10.lang.Any");
                }
                types.add(arrayOf(t, dims));
                break;
            }
            case 'L': {
                Ref<? extends Type> t = null;
                String parent = null;
                do {
                int start = ++i;
                while (i < str.length()) {
                    char c = str.charAt(i);
                    if (c == ';' || c == '<') {
                        String s = str.substring(start, i);
                        s = s.replace('/', '.');
                        if (t == null) {
                            t = typeForName(s);
                            parent = s;
                        } else {
                            String fullName = parent + "$" + s;
                            parent = fullName;
                            t = typeForName(fullName); // FIXME: forgets generic info on container -- should look up member class instead
                        }
                        break;
                    }
                    i++;
                }
                if (str.charAt(i) == '<') {
                    ++i;
                    assert (t != null);
                    List<Ref<? extends Type>> targs = new ArrayList<Ref<? extends Type>>();
                    i = updateTypeListFromString(targs, str, i, bounds);
                    assert (i < str.length() && str.charAt(i) == '>');
                    i++;
                    X10ClassType cType = t.get().toClass();
                    List<Type> typeArgs = new ArrayList<Type>(targs.size());
                    for (Ref<? extends Type> a : targs) {
                        typeArgs.add(a.get());
                    }
                    // [DC] hacky way of turning off type arguments here -- we don't want them in X10
                    //t = Types.ref(cType.typeArguments(typeArgs));
                }
                } while (str.charAt(i) == '.'); // Some signatures have generic info for containers
                assert (i < str.length() && str.charAt(i) == ';');
                types.add(arrayOf(t, dims));
                break;
            }
            case '*': { // wildcard
                types.add(arrayOf(Types.ref(ts.Any()), dims));
                break;
            }
            case '+': { // wildcard extends
                List<Ref<? extends Type>> t = new ArrayList<Ref<? extends Type>>();
                i = updateTypeFromString(t, str, i+1, bounds);
                assert (t.size() == 1);
                types.add(arrayOf(t.get(0), dims));
                break;
            }
            case '-': { // wildcard super
                List<Ref<? extends Type>> t = new ArrayList<Ref<? extends Type>>();
                i = updateTypeFromString(t, str, i+1, bounds);
                assert (t.size() == 1);
                types.add(arrayOf(Types.ref(ts.Any()), dims));
                break;
            }
            default:
                return i;
        }
        return i;
    }

    /**
     * Return a type based on a JVM type descriptor string. 
     * @param str The type descriptor.
     * @param bounds Generic type bounds in scope (may be null).
     * @return The corresponding type.
     */
    protected Ref<? extends Type> typeForString(String str, Map<String,Ref<? extends Type>> bounds) {
        List<Ref<? extends Type>> l = typeListForString(str, bounds);

        if (l.size() == 1) {
            return l.get(0);
        }

        throw new InternalCompilerError("Bad type string: \"" + str + "\"");
    }

    /**
     * Looks up a class by name, assuming the class exists.
     * @param name Name of the class to find.
     * @return A ClassType with the given name.
     * @throws InternalCompilerError if the class does not exist.
     */
    protected Ref<X10ClassDef> defForName(String name) {
        return defForName(name, null);
    }
    
    protected Ref<X10ClassDef> defForName(String name, Flags flags) {
        if (reporter.should_report(Reporter.loader, 2))
            reporter.report(2, "resolving " + name);
        
        LazyRef<X10ClassDef> sym = Types.lazyRef(ts.unknownClassDef(), null);
        
        if (flags == null) {
            sym.setResolver(ts.extensionInfo().scheduler().LookupGlobalTypeDef(sym, QName.make(name)));
        }
        else {
            sym.setResolver(ts.extensionInfo().scheduler().LookupGlobalTypeDefAndSetFlags(sym, QName.make(name), flags));
        }
        return sym;
    }
    
    /**
     * Looks up a class by name, assuming the class exists.
     * @param name Name of the class to find.
     * @return A Ref to a Type with the given name.
     */
    protected Ref<? extends Type> typeForName(String name) {
        return typeForName(name, null);
    }

    private static final HashMap<String,String> boxedPrimitives = new HashMap<String,String>();
    static {
        boxedPrimitives.put("x10.core.Boolean", "x10.lang.Boolean");
        boxedPrimitives.put("x10.core.Char", "x10.lang.Char");
        boxedPrimitives.put("x10.core.Byte", "x10.lang.Byte");
        boxedPrimitives.put("x10.core.Short", "x10.lang.Short");
        boxedPrimitives.put("x10.core.Int", "x10.lang.Int");
        boxedPrimitives.put("x10.core.Long", "x10.lang.Long");
        boxedPrimitives.put("x10.core.Float", "x10.lang.Float");
        boxedPrimitives.put("x10.core.Double", "x10.lang.Double");
        boxedPrimitives.put("x10.core.UByte", "x10.lang.UByte");
        boxedPrimitives.put("x10.core.UShort", "x10.lang.UShort");
        boxedPrimitives.put("x10.core.UInt", "x10.lang.UInt");
        boxedPrimitives.put("x10.core.ULong", "x10.lang.ULong");
    }
    protected Ref<? extends Type> typeForName(String name, Flags flags) {
        String unboxed = boxedPrimitives.get(name);
        if (unboxed != null) {
            name = unboxed;
        }
        //name = name.replace('$', '.'); // keep the name with the '$' to make sure the system finds the class
        Name shortName = Name.make(name.substring(name.lastIndexOf('.')+1));
        return Types.<Type>ref(ts.createClassType(position(), position(), defForName(name, flags)).name(shortName));
    }

    public void initTypeObject() {
        this.init = true;
    }

    public boolean isTypeObjectInitialized() {
        return this.init;
    }

    public void initSuperclass() {
        if (superclassInitialized) {
            return;
        }

        if (clazz.name().equals("java/lang/Object")) {
            ct.superType(null);
        }
        else {
            String superName = clazz.classNameCP(clazz.getSuperClass());

            if (superName != null) {
                ct.superType(typeForName(superName));
            }
            else {
                ct.superType(typeForName("java.lang.Object"));
            }
        }

        superclassInitialized = true;

        if (initialized()) {
            clazz = null;
        }
    }

    public void initInterfaces() {
        if (interfacesInitialized) {
            return;
        }

        int[] interfaces = clazz.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            String name = clazz.classNameCP(interfaces[i]);
            ct.addInterface(typeForName(name));
            // ### should be a lazy ref that rather than eager
        }
        
        interfacesInitialized = true;

        if (initialized()) {
            clazz = null;
        }
    }

    public void initMemberClasses() {
        if (memberClassesInitialized) {
            return;
        }

        InnerClasses innerClasses = clazz.getInnerClasses();
        
        if (innerClasses != null) {
            for (int i = 0; i < innerClasses.getClasses().length; i++) {
                Info c = innerClasses.getClasses()[i];

                if (c.outerClassIndex == clazz.getThisClass() && c.classIndex != 0) {
                    String name = clazz.classNameCP(c.classIndex);

                    int index = name.lastIndexOf('$');

                    // Skip local and anonymous classes.
                    if (index >= 0 && Character.isDigit(name.charAt(index + 1))) {
                        continue;
                    }

                    // A member class of this class
                    Ref<? extends Type> t = typeForName(name, ts.flagsForBits(c.modifiers));

                    if (reporter.should_report(Reporter.loader, 3))
                        reporter.report(3, "adding member " + t + " to " + ct);

                    ct.addMemberClass((Ref<? extends ClassType>) t);
                }
            }
        }

        memberClassesInitialized = true;

        if (initialized()) {
            clazz = null;
        }
    }

    public void canonicalFields() {
        initFields();
    }
    
    public void canonicalMethods() {
        initMethods();
    }
    
    public void canonicalConstructors() {
        initConstructors();
    }
    
    public void initFields() {
        if (fieldsInitialized) {
            return;
        }

        Field[] fields = clazz.getFields();
        for (int i = 0; i < fields.length; i++) {
            if (!fields[i].name().startsWith("jlc$")
                    && !fields[i].isSynthetic()) {
                FieldDef fi = this.fieldInstance(fields[i], ct);
                if (reporter.should_report(Reporter.loader, 3))
                    reporter.report(3, "adding " + fi + " to " + ct);
                ct.addField(fi);
            }
        }

        fieldsInitialized = true;

        if (initialized()) {
            clazz = null;
        }
    }

    public void initMethods() {
        if (methodsInitialized) {
            return;
        }
        
        Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (!methods[i].name().equals("<init>")
                    && !methods[i].name().equals("<clinit>")
                    && !methods[i].isSynthetic()) {
                MethodDef mi = this.methodInstance(methods[i], ct);
                if (reporter.should_report(Reporter.loader, 3))
                    reporter.report(3, "adding " + mi + " to " + ct);
                ct.addMethod(mi);
            }
        }

        methodsInitialized = true;

        if (initialized()) {
            clazz = null;
        }
    }

    public void initConstructors() {
        if (constructorsInitialized) {
            return;
        }

        Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].name().equals("<init>")
                    && !methods[i].isSynthetic()) {
                ConstructorDef ci = this.constructorInstance(methods[i],
                                                                  ct,
                                                                  clazz.getFields());
                if (reporter.should_report(Reporter.loader, 3))
                    reporter.report(3, "adding " + ci + " to " + ct);
                ct.addConstructor(ci);
            }
        }

        constructorsInitialized = true;
        
        if (initialized()) {
            clazz = null;
        }
    }

    protected boolean initialized() {
        return superclassInitialized && interfacesInitialized
                && memberClassesInitialized && methodsInitialized
                && fieldsInitialized && constructorsInitialized;
    }

    private int parseBounds(String str, Map<String,Ref<? extends Type>> bounds) {
        if (str.charAt(0) != '<') return 0;
        int i = 1;
        while (i < str.length()) {
            if (str.charAt(i) == '>') return i+1;
            int start = i;
            int colon = str.indexOf(':', start);
            String name = str.substring(start, colon);
            while (str.charAt(colon+1) == ':') colon++; // Sometimes the separator is two colons
            List<Ref<? extends Type>> types = new ArrayList<Ref<? extends Type>>(1);
            i = updateTypeFromString(types, str, colon+1, bounds);
            assert (types.size() == 1);
            bounds.put(name, types.get(0));
            i++;
        }
        assert (str.charAt(i) == '>');
        return i;
    }

    private static String stripThrowsSignature(String returnType) {
        int end = returnType.indexOf('^');
        if (end > 0) returnType = returnType.substring(0, end); // [MT] strip ThrowsSignature (^xxx;)
        return returnType;
    }

    /**
     * Create a MethodInstance.
     * @param method The JVM Method data structure.
     * @param ct The class containing the method.
     */
    protected MethodDef methodInstance(Method method, ClassDef ct) {
        Constant[] constants = clazz.getConstants();
        Map<String,Ref<? extends Type>> bounds = CollectionFactory.<String,Ref<? extends Type>>newHashMap();
        if (clazz.getSignature() != null) {
            parseBounds((String) constants[clazz.getSignature().getSignature()].value(), bounds);
            // TODO: [IP] also find and process bounds of the outer classes
        }
        String name = (String) constants[method.getName()].value();
        int sig = method.getSignature() != null ? method.getSignature().getSignature() : method.getType();
        String type = (String) constants[sig].value();
        int start = parseBounds(type, bounds);

        if (type.charAt(start) != '(') {
            throw new ClassFormatError("Bad method type descriptor.");
        }
    
        int index = type.indexOf(')', start+1);
        List<Ref<? extends Type>> argTypes = typeListForString(type.substring(start+1, index), bounds);
        Ref<? extends Type> returnType = typeForString(stripThrowsSignature(type.substring(index+1)), bounds);
    
        List<Ref<? extends Type>> excTypes = new ArrayList<Ref<? extends Type>>();
        Exceptions exceptions = method.getExceptions();
        if (exceptions != null) {
            int[] throwTypes = exceptions.getThrowTypes();
            for (int i = 0; i < throwTypes.length; i++) {
                String s = clazz.classNameCP(throwTypes[i]);
                excTypes.add(typeForName(s));
            }
        }

                
        return ts.methodDef(ct.position(), ct.errorPosition(), Types.ref(ct.asType()),
                                 ts.flagsForBits(method.getModifiers()),
                                 returnType, Name.make(name), argTypes, excTypes);
    }

    /**
     * Create a ConstructorInstance.
     * @param method The JVM Method data structure for the constructor.
     * @param ct The class containing the method.
     * @param fields The constructor's fields, needed to remove parameters
     * passed to initialize synthetic fields.
     */
    protected ConstructorDef constructorInstance(Method method, ClassDef ct, Field[] fields) {
        // Get a method instance for the <init> method.
        MethodDef mi = methodInstance(method, ct);
    
        List<Ref<? extends Type>> formals = mi.formalTypes();
    
        if (ct.isInnerClass()) {
            // If an inner class, the first argument may be a reference to an
            // enclosing class used to initialize a synthetic field.
    
            // Count the number of synthetic fields.
            int numSynthetic = 0;
    
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].isSynthetic()) {
                    numSynthetic++;
                }
            }
    
            // Ignore a number of parameters equal to the number of synthetic
            // fields.
            if (numSynthetic <= formals.size()) {
                formals = formals.subList(numSynthetic, formals.size());
            }
        }
        
        return ts.constructorDef(mi.position(), mi.errorPosition(), Types.ref(ct.asType()), mi.flags(),
                                      formals, mi.throwTypes());
    }

    /**
     * Create a FieldInstance.
     * @param field The JVM Field data structure for the field.
     * @param ct The class containing the field.
     */
    protected FieldDef fieldInstance(Field field, ClassDef ct) {
      Constant[] constants = clazz.getConstants();
      Map<String,Ref<? extends Type>> bounds = CollectionFactory.<String,Ref<? extends Type>>newHashMap();
      if (clazz.getSignature() != null) {
        parseBounds((String) constants[clazz.getSignature().getSignature()].value(), bounds);
        // TODO: [IP] also find and process bounds of the outer classes
      }
      String name = (String) constants[field.getName()].value();
      int sig = field.getSignature() != null ? field.getSignature().getSignature() : field.getType();
      String type = (String) constants[sig].value();
      int start = parseBounds(type, bounds);
    
      FieldDef fi = ts.fieldDef(ct.position(), Types.ref(ct.asType()),
                                          ts.flagsForBits(field.getModifiers()),
                                          typeForString(type.substring(start), bounds), Name.make(name));
    
      if (field.isConstant()) {
        Constant c = field.constantValue();
    
        ConstantValue o = null;
    
        try {
          switch (c.tag()) {
            case Constant.STRING: o = ConstantValue.makeString(field.getString()); break;
            case Constant.INTEGER: o = ConstantValue.makeInt(field.getInt()); break;
            case Constant.LONG: o = ConstantValue.makeLong(field.getLong()); break;
            case Constant.FLOAT: o = ConstantValue.makeFloat(field.getFloat()); break;
            case Constant.DOUBLE: o = ConstantValue.makeDouble(field.getDouble()); break;
          }
        }
        catch (SemanticException e) {
          throw new ClassFormatError("Unexpected constant pool entry.");
        }
    
        fi.setConstantValue(o);
        return fi;
      }
      else {
        fi.setNotConstant();
      }
    
      return fi;
    }

}
