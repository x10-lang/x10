package x10.types;

import java.util.List;

import polyglot.types.ClassType;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeEnv;
import x10.constraint.XConstraint;
import x10.constraint.XTerm;
import x10.types.X10TypeSystem_c.Kind;
import x10.types.constraints.CConstraint;

public interface X10TypeEnv extends TypeEnv {
    public void checkOverride(ClassType thisType, MethodInstance mi, MethodInstance mj) throws SemanticException;

    /** Return true if the constraint is consistent. */
    boolean consistent(CConstraint c);

    /** Return true if constraints in the type are all consistent. */
    boolean consistent(Type t);

    /**
     * Return a lit of upper bounds for type t.
     * @param t
     * @param includeObject
     * @return
     */
    List<Type> upperBounds(Type t, boolean includeObject);

    List<Type> lowerBounds(Type t);

    List<Type> equalBounds(Type t);

    List<MacroType> findAcceptableTypeDefs(Type container, X10TypeSystem_c.TypeDefMatcher matcher) throws SemanticException;

    boolean isSubtypeWithValueInterfaces(Type t1, Type t2);

    boolean isSubtype(Type t1, Type t2);
   
    boolean entails(CConstraint c1, CConstraint c2);

    boolean hasSameClassDef(Type t1, Type t2);

    boolean equivClause(Type me, Type other);

    boolean entailsClause(Type me, Type other);

    boolean typeBaseEquals(Type type1, Type type2);

    boolean typeDeepBaseEquals(Type type1, Type type2);

    boolean equalTypeParameters(List<Type> a, List<Type> b);

    boolean primitiveClausesConsistent(CConstraint c1, CConstraint c2);

    boolean clausesConsistent(CConstraint c1, CConstraint c2);

    Kind kind(Type t);
    
    boolean numericConversionValid(Type toType, Type fromType, Object value);
    
    
    

}