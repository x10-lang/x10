/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types.reflect;

import java.util.*;

import polyglot.frontend.Globals;
import polyglot.main.Report;
import polyglot.types.*;
import polyglot.types.reflect.InnerClasses.Info;
import polyglot.util.*;

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
    protected ClassDef ct;

    protected boolean init;
    protected boolean constructorsInitialized;
    protected boolean fieldsInitialized;
    protected boolean interfacesInitialized;
    protected boolean memberClassesInitialized;
    protected boolean methodsInitialized;
    protected boolean superclassInitialized;

    protected static Collection verbose = ClassFileLoader.verbose;

    public ClassFileLazyClassInitializer(ClassFile file, TypeSystem ts) {
        this.clazz = file;
        this.ts = ts;
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
        return new Position(null, clazz.name() + ".class");
    }

    /**
     * Create the type for this class file.
     */
    protected ClassDef createType() throws SemanticException {
        // The name is of the form "p.q.C$I$J".
        String name = clazz.classNameCP(clazz.getThisClass());

        if (Report.should_report(verbose, 2))
            Report.report(2, "creating ClassType for " + name);

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

        String outerName; // This will be "p.q.C$I"
        String innerName; // This will be "J"

        outerName = name;
        innerName = null;

        int dollar = outerName.lastIndexOf('$');

        if (dollar >= 0) {
            outerName = name.substring(0, dollar);
            innerName = name.substring(dollar + 1);

            // Lazily load the outer class.
            ct.outer(defForName(outerName));
        }
        else {
            outerName = name;
            innerName = null;
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

        if (Report.should_report(verbose, 3))
            Report.report(3, name + " is " + kind);

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
     * @return The corresponding list of types.
     */
    protected List<Ref<? extends Type>> typeListForString(String str) {
        List<Ref<? extends Type>> types = new ArrayList<Ref<? extends Type>>();

        for (int i = 0; i < str.length(); i++) {
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
                case 'L': {
                    int start = ++i;
                    while (i < str.length()) {
                        if (str.charAt(i) == ';') {
                            String s = str.substring(start, i);
                            s = s.replace('/', '.');
                            types.add(arrayOf(this.typeForName(s), dims));
                            break;
                        }

                        i++;
                    }
                }
            }
        }

        if (Report.should_report(verbose, 4))
            Report.report(4, "parsed \"" + str + "\" -> " + types);

        return types;
    }

    /**
     * Return a type based on a JVM type descriptor string. 
     * @param str The type descriptor.
     * @return The corresponding type.
     */
    protected Ref<? extends Type> typeForString(String str) {
        List<Ref<? extends Type>> l = typeListForString(str);

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
    protected Ref<ClassDef> defForName(String name) {
        return defForName(name, null);
    }
    
    protected Ref<ClassDef> defForName(String name, Flags flags) {
        if (Report.should_report(verbose, 2))
            Report.report(2, "resolving " + name);
        
        LazyRef<ClassDef> sym = Types.lazyRef(ts.unknownClassDef(), null);
        
        if (flags == null) {
            sym.setResolver(Globals.Scheduler().LookupGlobalTypeDef(sym, QName.make(name)));
        }
        else {
            sym.setResolver(Globals.Scheduler().LookupGlobalTypeDefAndSetFlags(sym, QName.make(name), flags));
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

    protected Ref<? extends Type> typeForName(String name, Flags flags) {
        return Types.<Type>ref(ts.createClassType(position(), defForName(name, flags)));
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

                    if (Report.should_report(verbose, 3))
                        Report.report(3, "adding member " + t + " to " + ct);

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
                if (Report.should_report(verbose, 3))
                    Report.report(3, "adding " + fi + " to " + ct);
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
                if (Report.should_report(verbose, 3))
                    Report.report(3, "adding " + mi + " to " + ct);
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
                if (Report.should_report(verbose, 3))
                    Report.report(3, "adding " + ci + " to " + ct);
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

    /**
     * Create a MethodInstance.
     * @param method The JVM Method data structure.
     * @param ct The class containing the method.
     */
    protected MethodDef methodInstance(Method method, ClassDef ct) {
        Constant[] constants = clazz.getConstants();
        String name = (String) constants[method.getName()].value();
        String type = (String) constants[method.getType()].value();
    
        if (type.charAt(0) != '(') {
            throw new ClassFormatError("Bad method type descriptor.");
        }
    
        int index = type.indexOf(')', 1);
        List<Ref<? extends Type>> argTypes = typeListForString(type.substring(1, index));
        Ref<? extends Type> returnType = typeForString(type.substring(index+1));
    
        List<Ref<? extends Type>> excTypes = new ArrayList<Ref<? extends Type>>();
    
        Exceptions exceptions = method.getExceptions();
        if (exceptions != null) {
            int[] throwTypes = exceptions.getThrowTypes();
            for (int i = 0; i < throwTypes.length; i++) {
                String s = clazz.classNameCP(throwTypes[i]);
                excTypes.add(typeForName(s));
            }
        }
    
        return ts.methodDef(ct.position(), Types.ref(ct.asType()),
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
        
        return ts.constructorDef(mi.position(), Types.ref(ct.asType()), mi.flags(),
                                      formals, mi.throwTypes());
    }

    /**
     * Create a FieldInstance.
     * @param field The JVM Field data structure for the field.
     * @param ct The class containing the field.
     */
    protected FieldDef fieldInstance(Field field, ClassDef ct) {
      Constant[] constants = clazz.getConstants();
      String name = (String) constants[field.getName()].value();
      String type = (String) constants[field.getType()].value();
    
      FieldDef fi = ts.fieldDef(ct.position(), Types.ref(ct.asType()),
                                          ts.flagsForBits(field.getModifiers()),
                                          typeForString(type), Name.make(name));
    
      if (field.isConstant()) {
        Constant c = field.constantValue();
    
        Object o = null;
    
        try {
          switch (c.tag()) {
            case Constant.STRING: o = field.getString(); break;
            case Constant.INTEGER: o = Integer.valueOf(field.getInt()); break;
            case Constant.LONG: o = Long.valueOf(field.getLong()); break;
            case Constant.FLOAT: o = Float.valueOf(field.getFloat()); break;
            case Constant.DOUBLE: o = Double.valueOf(field.getDouble()); break;
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
