package x10.types.constraints;

import polyglot.types.Name;
import x10.constraint.XField;
import x10.constraint.XName;
import x10.constraint.XVar;

/**
 * Analogous to x10.constraint.XTerm. Provides static methods
 * for generating various kinds of XTerms that know about type
 * information.
 * @author vijay
 *
 */
public class CTerms {

    public static final String SELF_VAR_PREFIX="self";
    public static final String THIS_VAR_PREFIX="this";
    public static final CThis THIS_THIS = new CThis(0);
	
	/**
	 * Make and return <code>receiver.field</code>.
	 * @param receiver
	 * @param field
	 * @return
	 */
	public static final XField makeField(XVar receiver, XName field) {
		return new XField(receiver, field);
	}
	
	static int selfId=0;
	public static final CSelf makeSelf() {
	    return new CSelf(selfId++);
	}
	static int thisId=1;
    public static final CThis makeThis() {
        return new CThis(thisId++);
    }
    public static final CThis makeThis(String name) {
        return new CThis(name, thisId++);
    }
}
