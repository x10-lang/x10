/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import java.util.List;

import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.util.Enum;
import polyglot.util.Position;

/**
 * A <code>ParsedClassType</code> represents a class loaded from a source file.
 * <code>ParsedClassType</code>s are mutable.
 */
public interface ClassDef extends MemberDef
{
    ClassType asType();
    
    Name name();
    QName fullName();

    Ref<? extends Package> package_();
    
    public void inStaticContext(boolean inStaticContext);
    public boolean inStaticContext();
    
    public static class Kind extends Enum {
        public Kind(String name) {
            super(name);
        }
    }

    public static final Kind TOP_LEVEL = new Kind("top-level");
    public static final Kind MEMBER = new Kind("member");
    public static final Kind LOCAL = new Kind("local");
    public static final Kind ANONYMOUS = new Kind("anonymous");

    /** Get the class's kind. */
    Kind kind();

    boolean isMember();
    boolean isTopLevel();
    boolean isLocal();
    boolean isAnonymous();
    boolean isNested();
    boolean isInnerClass();

    /**
     * Return the type's super type.
     */
    Ref<? extends Type> superType();

    /**
     * Return the type's interfaces.
     * @return A list of <code>Type</code>.
     * @see polyglot.types.Type
     */
    List<Ref<? extends Type>> interfaces();

    /**
     * Return the type's fields.
     * @return A list of <code>FieldInstance</code>.
     * @see polyglot.types.FieldDef
     */
    List<FieldDef> fields();

    /**
     * Return the type's methods.
     * @return A list of <code>MethodInstance</code>.
     * @see polyglot.types.MethodDef
     */
    List<MethodDef> methods();
    /**
     * The class's constructors.
     * A list of <code>ConstructorInstance</code>.
     * @see polyglot.types.ConstructorDef
     */
    List<ConstructorDef> constructors();

    /**
     * The class's member classes.
     * A list of <code>ClassType</code>.
     * @see polyglot.types.ClassType
     */
    List<Ref<? extends ClassType>> memberClasses();

    /** The class's outer class if this is a nested class, or null. */
    Ref<? extends ClassDef> outer();
    
    /**
     * Position of the type's declaration.
     */
    void position(Position pos);
    
    /**
     * The <code>Source</code> that this class type
     * was loaded from. Should be <code>null</code> if it was not loaded from
     * a <code>Source</code> during this compilation. 
     */
    Source sourceFile();
    Job job();
    void setJob(Job job);
    
    boolean fromSourceFile();
    void setFromSourceFile();
    boolean fromEncodedClassFile();
    void setFromEncodedClassFile();
    boolean fromJavaClassFile();
    void setFromJavaClassFile();

    /**
     * Set the class's package.
     */
    void setPackage(Ref<? extends Package> p);

    /**
     * Set the class's super type.
     */
    void superType(Ref<? extends Type> t);

    /**
     * Add an interface to the class.
     */
    void addInterface(Ref<? extends Type> t);

    /**
     * Set the class's interfaces.
     */
    void setInterfaces(List<Ref<? extends Type>> l);
    
    /**
     * Add a field to the class.
     */
    void addField(FieldDef fi);

    /**
     * Set the class's fields.
     */
    void setFields(List<? extends FieldDef> l);
    
    /**
     * Add a method to the class.
     */
    void addMethod(MethodDef mi);

    /**
     * Set the class's methods.
     */
    void setMethods(List<? extends MethodDef> l);

    /**
     * Add a constructor to the class.
     */
    void addConstructor(ConstructorDef ci);

    /**
     * Set the class's constructors.
     */
    void setConstructors(List<? extends ConstructorDef> l);
    
    /**
     * Add a member class to the class.
     */
    void addMemberClass(Ref<? extends ClassType> t);

    /**
     * Set the class's member classes.
     */
    void setMemberClasses(List<Ref<? extends ClassType>> l);

    /**
     * Set the flags of the class.
     */
    void flags(Flags flags);

    /**
     * Set the class's outer class.
     */
    void outer(Ref<ClassDef> outer);

    /**
     * Set the name of the class.  Throws <code>InternalCompilerError</code>
     * if called on an anonymous class.
     */
    void name(Name name);

    /**
     * Set the class's kind.
     */
    void kind(Kind kind);
}
