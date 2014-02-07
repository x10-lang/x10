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

import java.util.List;

import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.util.Position;
import x10.types.ParameterType;
import x10.types.TypeDef;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10FieldDef;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;

/**
 * A <code>ParsedClassType</code> represents a class loaded from a source file.
 * <code>ParsedClassType</code>s are mutable.
 */
public interface ClassDef extends MemberDef
{    
    Name name();
    QName fullName();

    Ref<? extends Package> package_();
    
    public void inStaticContext(boolean inStaticContext);
    public boolean inStaticContext();
    
    public static enum Kind {
        TOP_LEVEL("top-level"),
        MEMBER("member"),
        LOCAL("local"),
        ANONYMOUS("anonymous");

        public final String name;
        private Kind(String name) {
            this.name = name;
        }
        @Override public String toString() {
            return name;
        }                  
    }

    public static final Kind TOP_LEVEL = Kind.TOP_LEVEL;
    public static final Kind MEMBER = Kind.MEMBER;
    public static final Kind LOCAL = Kind.LOCAL;
    public static final Kind ANONYMOUS = Kind.ANONYMOUS;

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
    Ref<? extends X10ClassDef> outer();
    
    /**
     * Position of the type's declaration.
     */
    void position(Position pos);
    
    void errorPosition(Position errorPos);
    
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
    void outer(Ref<X10ClassDef> outer);

    /**
     * Set the name of the class.  Throws <code>InternalCompilerError</code>
     * if called on an anonymous class.
     */
    void name(Name name);

    /**
     * Set the class's kind.
     */
    void kind(Kind kind);
    
    /** The root clause for a ClassDef is the conjunction of the invariant
     * for the class, with the invariants for its superclass and the 
     * interfaces it implements, and for each property f:C{c},
     * the constraint c[self.f/self,self/this]
     * 
     * <p>This will force computation of the real clause if it is not computed already.
     * @return
     */
	CConstraint getRealClause();
	/**
	 * Return the lazy ref so you can decide whether to force it or not.
	 * @return
	 */
     Ref<CConstraint>  realClause();
     
     /**
      * Return the lazy ref so you can decide whether to force it or not.
      * When forced, it will return the real clause with this substituted
      * for self.
      * @return
      */
      Ref<CConstraint>  realClauseWithThis();
    
    /**
     * Throw a SemanticException if the real clause is invalid.
     * (The real clause would have been discovered to be invalid
     * during its computation, and the reason would have been
     * recorded as an exception. That exception is thrown.)
     * @throws SemanticException
     */
    void checkRealClause() throws SemanticException;
    
    /** The class invariant. */
    Ref<CConstraint> classInvariant();
    void setClassInvariant(Ref<CConstraint> classInvariant);

    Ref<TypeConstraint> typeBounds() ;
    void setTypeBounds(Ref<TypeConstraint> c) ;

    /** Properties defined in the class.  Subset of fields(). */
    List<X10FieldDef> properties();
    
    List<ParameterType.Variance> variances();
    List<ParameterType> typeParameters();
    void addTypeParameter(ParameterType p, ParameterType.Variance v);
    void replaceTypeParameter(int i, ParameterType p, ParameterType.Variance v);
    
    /** Add a member type to the class. */
    List<TypeDef> memberTypes();
    
    /** Add a member type to the class. */
    void addMemberType(TypeDef t);
    
    /**
     * Is this the class def for an X10 struct?
     * @return
     */
    boolean isStruct();
    /**
     * Is this the class def for an X10 function?
     */
    boolean isFunction();
    X10ClassType asType();
    /**
     * Does this class def have a custom deserialization constructor defined?
     */
    boolean hasDeserializationConstructor(Context context);
    /**
     * True if the class def used to be a non-static member class (used by {@link InnerClassRemover}).
     */
    boolean wasInner();
    /**
     * Mark the class def as having been a non-static member class (used by {@link InnerClassRemover}).
     */
    void setWasInner(boolean v);
}
