package x10.constraint.xsmt;

public class SmtBaseType extends SmtType {
	TypeKind kind; 
	String name; 
	protected SmtBaseType(TypeKind kind) {
		this.kind = kind;
		this.name = kind.toString();
	}  
	
	protected SmtBaseType(String name) {
		this.kind = TypeKind.USORT; 
		this.name = name; 
	}

	@Override
	public String toSmt2() {
		return "() " + name; 
	}

	@Override
	public int arity() {
		return 0;
	}

	@Override
	public SmtBaseType get(int i) {
		if (i != 0)
			throw new IllegalArgumentException("SmtBaseType has arity 0.");
		return this;
	}

	@Override
	public boolean isUSort() {
		return kind == TypeKind.USORT;
	}

	@Override
	public boolean isBoolean() {
		return kind == TypeKind.BOOLEAN;
	}

	public String name() {
		return name;
	}
	
	@Override
	public String toString() {
		return kind.toString(); 
	}
}
