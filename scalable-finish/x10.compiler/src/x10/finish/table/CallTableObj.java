package x10.finish.table;

import java.io.Serializable;

public abstract class CallTableObj implements Serializable{
	/**
	 * automatically added for serialization
	 */
	private static final long serialVersionUID = 1L;
	public final String scope;
	public final String name;
	public final int line;
	public final int column;

	public CallTableObj(String s, String n, int l, int c) {
		scope = s;
		name = n;
		line = l;
		column = c;
	}
}
