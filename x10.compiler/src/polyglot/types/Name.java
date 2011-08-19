package polyglot.types;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import polyglot.util.StringUtil;
import polyglot.util.UniqueID;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;

public class Name  implements Serializable {
    private static final long serialVersionUID = -899548430558751316L;

    String name;
    int hash;

    private Name(String name, int hash) {
        this.name = name;
        this.hash = hash;
    }

    static Map<String,Name> internCache = CollectionFactory.newHashMap();
    static int count = 0;
    
    public static Name makeFresh() {
        return makeFresh("id");
    }
    
    public static Name makeFresh(String prefix) {
        return make(prefix + (count++));
    }

    public static Name makeFresh(Name prefix) {
	if (prefix == null)
	    return makeFresh();
	return makeFresh(prefix.toString());
    }
    
    public static Name make(String name) {
        if (! StringUtil.isNameShort(name))
	assert StringUtil.isNameShort(name);
	return makeUnchecked(name);
    }

    public static Name makeUnchecked(String name) {
	synchronized (internCache) {
	    Name n = internCache.get(name);

	    if (n == null) {
		n = new Name(name, name.hashCode());
		internCache.put(name, n);
	    }

	    return n;
	}
    }
    
    public final void equals(final String o) { }
    public final void equals(final QName o) { }

    public boolean equals(Object o) {
        if (o == null)
            return false;
        return this == o;
    }

    public int hashCode() {
        return hash;
    }

    public String toString() {
        return name;
    }
}
