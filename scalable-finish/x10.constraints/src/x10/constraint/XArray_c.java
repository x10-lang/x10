package x10.constraint;

/**
 * A variable that represents an array. The constraint
 * @author vj
 *
 */
public class XArray_c extends XLocal_c implements XArray {

	public XArray_c(XName name) {
		super(name);
		
	}
	public XTermKind kind() { return XTermKind.ARRAY;}

}
