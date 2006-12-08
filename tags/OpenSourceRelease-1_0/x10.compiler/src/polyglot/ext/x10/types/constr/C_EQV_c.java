/**
 * 
 */
package polyglot.ext.x10.types.constr;

import polyglot.types.LocalInstance;

/**
 * @author vj
 *
 */
public class C_EQV_c extends C_Local_c implements C_EQV {

	/**
	 * @param li
	 */
	public C_EQV_c(LocalInstance li) {
		super(li);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param li
	 * @param isSelfVar
	 */
	public C_EQV_c(LocalInstance li, boolean isSelfVar) {
		super(li, isSelfVar);
		// TODO Auto-generated constructor stub
	}
	
	public boolean isEQV() { return true;}

}
