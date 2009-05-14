package x10.constraint;

import java.util.List;

/**
 * A representation of a(i).
 * TODO: Implement multidimensional access?
 * @author vj
 *
 */
public class XArrayElement_c extends XFormula_c  implements XArrayElement {

	public XArrayElement_c(XTerm array, XTerm index) {
		super(XTerms.arrayAccessName, array, index);
		// TODO Auto-generated constructor stub
	}
	
	public XTerm array() { return arguments.get(0);}
	public XTerm index() { return arguments.get(1);}
	public XVar rootVar() { return this;}
	public XVar[] vars() { return new XVar[] { this};}

}
