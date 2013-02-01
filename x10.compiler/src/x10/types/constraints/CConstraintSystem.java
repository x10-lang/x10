package x10.types.constraints;

import java.util.List;

import polyglot.ast.IntLit;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import x10.constraint.XConstraintSystem;
import x10.constraint.XDef;
import x10.constraint.XExpr;
import x10.constraint.XField;
import x10.constraint.XLit;
import x10.constraint.XTerm;
import x10.constraint.XUQV;
import x10.constraint.XVar;
import x10.constraint.xnative.XNativeUQV;
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
	public XVar<Type> makeSelf(Type t);
	/**
	 * Create a fresh special "this" variable with type t. 
	 * @param t type of this variable
	 * @return this variable
	 */
    public XVar<Type> makeThis(Type t);
	/**
	 * Make a local variable with its associated definition
	 * @param def the definition of the variable
	 * @return
	 */
	public CLocal makeLocal(X10LocalDef def);
	/**
	 * Make a local variable with its associated name and 
	 * definition
	 * @param def the definition of the variable
	 * @param name the name of the local variable
	 * @return
	 */
	public CLocal makeLocal(X10LocalDef def, String name);

	/**
     * Construct the XTerm corresponding to a method call. This will be represented by a 
     * field dereference followed by an function application. For example a.foo(x,y), will become 
     * (APPLY (XLabeledOp(foo) a) x y). 
     * @param md method definition
     * @param receiver 
     * @param t method arguments
     * @return 
     */
    public XExpr<Type> makeMethod(XDef<Type> md, XTerm<Type> receiver, List<? extends XTerm<Type>> t);
	
	
    /** Create the field dereference that represents the outer this variable for class t at object e. */
    public CQualifiedVar makeQualifiedVar(Type t, XTerm<Type> e); 
    
    /**
     * Construct the XTerm<Type> corresponding to the opaque definition. 
     * @param op
     * @param isatom
     * @param target
     * @param args
     * @return
     */
    public XExpr<Type> makeOpaque(Object op, boolean isAtomic, XTerm<Type> target, List<XTerm<Type>> args); 
    /**
     * Return a literal representing the zero value of the given type. 
     * @param type
     * @return
     */
    public XLit<Type, ?> makeZero(Type type); 
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
     * @param ts
     * @return
     */
    public CConstraint makeCConstraint(Type type, TypeSystem ts); 
    /**
     * Construct an empty CConstraint with the given <code>self</code> variable.
     * @param self
     * @return
     */
    public CConstraint makeCConstraint(XVar<Type> self, TypeSystem ts);

    /**
     * Construct an empty CConstraint with no <code>self</code> variable.
     * @param ts the type system
     * @return
     */
    public CConstraint makeCConstraintNoSelf(TypeSystem ts);

}
