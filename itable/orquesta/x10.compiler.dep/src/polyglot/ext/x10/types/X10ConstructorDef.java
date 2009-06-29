package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ext.x10.types.constr.Constraint;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;

public interface X10ConstructorDef extends ConstructorDef, X10ProcedureDef {
    /**
     * Set the returnType associated with this constructor.
     * @param t
     */
    void setReturnType(Ref<? extends ClassType> t);
    
    /** Return type associated with the constructor. */
    Ref<? extends ClassType> returnType();
    
    /** Return the constraint on properties, if any,
     * obtained from the return type of the call
     * to super in the body of this constructor. 
     * @return
     */
    Ref<? extends Constraint> supClause();
    
    /** Set the constraint on properties obtained from
     * the return type of the call to super. Set when typechecking
     * the code in the body of the constructor for which this is the constructor instance.
     * 
     * @param c
     */
    void setSupClause(Ref<? extends Constraint> c);

//    /**
//     * Return an instance of this, specialized with (a) any references
//     * to this in the dependent type of the result replaced by
//     * selfVar of thisType or an EQV of thisType (with propagation) 
//     * (b) any references to this in the dependent
//     * type T of an argument replaced by selfVar of thisType or an EQV
//     * at T, with no propagation.
//     * @param thisType
//     * @return
//     * @throws SemanticException 
//     */
//    X10ConstructorInstance instantiateForThis(ReferenceType thisType) throws SemanticException;

}
