package x10.types.constraints;

import java.util.List;

import polyglot.ast.Field;
import polyglot.ast.IntLit;
import polyglot.types.Def;
import polyglot.types.FieldDef;
import polyglot.types.LocalDef;
import polyglot.types.MethodDef;
import polyglot.types.Ref;
import polyglot.types.Type;
import x10.constraint.XConstraintSystem;
import x10.constraint.XDef;
import x10.constraint.XExpr;
import x10.constraint.XLit;
import x10.constraint.XLocal;
import x10.constraint.XOp;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.types.X10LocalDef;

/**
 * Factory interface that provides methods to create various XTerms that
 * have type information. 
 * @author lshadare
 *
 */
public interface CConstraintSystem extends XConstraintSystem<Type> {
	/**
	 * Create a fresh special "self" variable with type t. 
	 * @param t type of self variable
	 * @return self variable
	 */
	public CSelf makeSelf(Type t);
	/**
	 * Create a fresh special "this" variable with type t. 
	 * @param t type of this variable
	 * @return this variable
	 */
    public CThis makeThis(Type t);
    /**
     * Create a new field dereference resulting from applying the field f 
     * defined by field to receiver i.e. receiver.f
     * @param receiver the term we are dereferencing 
     * @param def definition of the field
     * @return
     */
    public CField makeField(XTerm<Type> receiver, XDef<Type> field); 
    /**
     * Create a new fake field dereference resulting from applying the field f 
     * defined by field to receiver i.e. receiver.f. This should be used for
     * compiler generated fields that are not visible to the user (such as here) 
     * @param receiver the term we are dereferencing 
     * @param def definition of the field
     * @return
     */
    //TODO: can we eliminate this?
    public CField makeFakeField(XTerm<Type> receiver, XDef<Type> field);
    /**
     * Create a new fake field dereference resulting from applying the field f 
     * defined by field to receiver i.e. receiver.f. This should be used for
     * compiler generated fields that are not visible to the user (such as here) 
     * @param receiver the term we are dereferencing 
     * @param op
     * @return
     */
    public CField makeFakeField(XTerm<Type> receiver, XOp<Type> op);
    /**
     * Create a local variable from the give local definition.
     * @param ld variable definition
     * @return
     */
    public <D extends XDef<Type>> XLocal<Type, D> makeLocal(D ld);
    /**
     * Create a local variable from the given local definition
     * using the given string name
     * @param ld variable definition
     * @param s string name
     * @return
     */
    public <D extends XDef<Type>> XLocal<Type, D> makeLocal(D ld, String s);
    /**
     * Construct an XExpr resulting from applying the method definition to the
     * given arguments. Note that the first element of the list is the recevier and 
     * the rest are the method arguments
     * @param md method definition
     * @param t receiver followed by method arguments
     * @return 
     */
    public XExpr<Type> makeApply(Def md, List<XTerm<Type>> t);
    /**
     * Construct the XTerm<Type> corresponding to the opaque definition. 
     * @param op
     * @param isatom
     * @param target
     * @param args
     * @return
     */
    public XExpr<Type> makeOpaque(Object op, boolean isatom, XTerm<Type> target, List<XTerm<Type>> args); 
    /**
     * Construct an XLit with the given value and type. Note that the correspondence
     * between the value of type and the type of val is not enforced. 
     * @param val value stored in literal
     * @param type the type of the literal
     * @return
     */
    public <V> XLit<Type, V> makeLit(V val, Type type);
    /**
     * Return a literal representing the zero value of the given type. 
     * @param type
     * @return
     */
    public XLit<Type, Object> makeZero(Type type); 
    /**
     * Construct a type literal. 
     * @param type
     * @return
     */
    public XTypeLit makeTypeLit(Type type);
    /**
     * Returns the IntLit.Kind corresponding to the given type. If type is
     * not an Integer type returns null. 
     * @param type
     * @return
     */
    public IntLit.Kind getIntLitKind(Type type);
    /**
     * Construct an empty CConstraint with fresh <code>self</code> variable and null 
     * <code>this</code> variable. The CConstraint will be over type <code>type</code> 
     * i.e. the type of the CConstraint's <code>self</code> will be <code>type</code>. 
     * @param type
     * @return
     */
    public CConstraint makeCConstraint(Type type); 
    /**
     * Construct an empty CConstraint with the given <code>self</code> variable.
     * @param self
     * @return
     */
    public CConstraint makeCConstraint(XVar<Type> self); 
    
}
