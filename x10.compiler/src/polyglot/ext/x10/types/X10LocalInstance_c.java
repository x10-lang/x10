/**
 * 
 */
package polyglot.ext.x10.types;

import polyglot.types.LocalInstance_c;
import polyglot.types.Flags;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;

/**
 * @author vj
 *
 */
public class X10LocalInstance_c extends LocalInstance_c implements X10LocalInstance {

	/**
	 * 
	 */
	public X10LocalInstance_c() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param ts
	 * @param pos
	 * @param flags
	 * @param type
	 * @param name
	 */
	public X10LocalInstance_c(TypeSystem ts, Position pos, Flags flags,
			Type type, String name) {
		super(ts, pos, flags, type, name);
	}
	
	protected int positionInArgList = -1;
	public int positionInArgList() {
		return positionInArgList;
	}
	public void setPositionInArgList(int i) {
		positionInArgList = i;
	}

}
