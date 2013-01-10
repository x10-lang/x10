package x10.constraint;


/** An implementation of XDef that can be used for small test cases or any case where a full FieldDef is not available.
 * @author dcunnin
 *
 * @param <T> The java type of the x10 type object.
 */
public class XStringDef<T extends XType> implements XDef<T> {

	private String name;
	private T type;
	
	public XStringDef (String name, T type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public T resultType() {
		return type;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (o==this) return true;
		if (!(o instanceof XStringDef<?>)) return false;
		return ((XStringDef<?>)o).name.equals(this.name);
	}

	@Override
	public int hashCode() { return name.hashCode(); }

	@Override
	public String toString() { return name; }
}
