package x10.constraint.xsmt;

import java.util.List;

public abstract class SmtType {

	public static enum TypeKind {
		BOOLEAN,
		UNKNOWN,
		USORT, 
		INT, 
		REAL;
		
		public String toString() {
			switch(this) {
			case BOOLEAN: return "Bool"; 
			case INT: return "Int"; 
			case REAL: return "Real"; 
			case UNKNOWN: return "Unknown"; 
			case USORT: return "USort"; 
			default:
				throw new UnsupportedOperationException(); 
			}
		}
	}

	/**
	 * Some useful types. 
	 */
	private static final SmtBaseType BOOL = new SmtBaseType(TypeKind.BOOLEAN);
	private static final SmtBaseType UNKNOWN = new SmtBaseType(TypeKind.UNKNOWN);
	private static final SmtBaseType DEFAULT_USORT = new SmtBaseType("USort");
	private static final SmtBaseType INT = new SmtBaseType(TypeKind.INT);
	private static final SmtBaseType REAL = new SmtBaseType(TypeKind.REAL);

	/**
	 * Factory methods for constructing various SmtTypes
	 * @return
	 */
	public static SmtBaseType BoolType() {return BOOL; } 
	public static SmtBaseType IntType() {return INT; }
	public static SmtBaseType RealType() {return REAL; }
	public static SmtBaseType UnknownType() {return UNKNOWN; }
	
	public static SmtBaseType USort() {return DEFAULT_USORT; }
	public static SmtBaseType USort(String name) {return new SmtBaseType(name); }
	public static SmtType makeType(SmtBaseType t1, SmtBaseType t2) { return new SmtFuncType(t1, t2); }
	public static SmtType makeType(List<SmtBaseType> args, SmtBaseType ret) {return new SmtFuncType (args, ret); }
	
	/**
	 * SmtType interface
	 */
	public abstract String toSmt2(); 
	public abstract int arity(); 
	public abstract SmtBaseType get(int i);
	public abstract boolean isUSort(); 
	public abstract boolean isBoolean();
	public abstract String toString(); 
}