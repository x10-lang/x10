package x10.constraint.redesign;
/**
 * Representation of an expression operator for XExpr occurring in constraints. 
 * 
 * @author lshadare
 *
 */
public abstract class XOp<T extends XType> {
	/**
	 * An XKind represents the kind of the operator.  
	 * @author lshadare
	 *
	 */
	public enum Kind {
		APPLY, 
		EQ,	
		NOT,
		AND,
		OR,
		IMPL
	}
	
	Kind kind; 
	
	/**
	 * Returns the kind of the operator. 
	 * @return
	 */
	public Kind getKind() {
		return kind; 
	}
	
	/**
	 * Returns the type of the term resulting from applying this operator
	 * e.g. for EQ the returned type would be Boolean. 
	 * @return
	 */
	public abstract T type(); 
	
	/**
	 * Some useful operators. 
	 */
	public static XSimpleOp EQ   = new XSimpleOp(Kind.EQ);
	public static XSimpleOp NOT  = new XSimpleOp(Kind.NOT);
	public static XSimpleOp AND  = new XSimpleOp(Kind.AND);
	public static XSimpleOp OR   = new XSimpleOp(Kind.OR);
	public static XSimpleOp IMPL = new XSimpleOp(Kind.IMPL);
	
	/**
	 * Constructs a labeled operator that represents the application 
	 * of the field/method corresponding to the field/method definition
	 * def. 
	 * 
	 * @param def
	 * @return 
	 */
	public static <T extends XType, D extends XDef<T>> XLabeledOp<T,D> makeLabelOp(D def) {
		return new XLabeledOp<T,D>(def);
	}
}
