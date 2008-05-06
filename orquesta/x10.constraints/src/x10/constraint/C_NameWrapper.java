package x10.constraint;

public class C_NameWrapper<T> implements C_Name {
	T v;

	public C_NameWrapper(T v) {
		this.v = v;
	}

	public String toString() {
		return v.toString();
	}

	public int hashCode() {
		return v.hashCode();
	}

	public boolean equals(Object o) {
		return o instanceof C_NameWrapper && v.equals(((C_NameWrapper<?>) o).v);
	}
}
