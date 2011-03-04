package x10.types.constraints;

import x10.constraint.XField;
import x10.constraint.XName;
import x10.constraint.XVar;

public class CTerms {

	
	/**
	 * Make and return <code>receiver.field</code>.
	 * @param receiver
	 * @param field
	 * @return
	 */
	public static final XField makeField(XVar receiver, XName field) {
		return new XField(receiver, field);
	}
}
