package x10.constraint;

import java.util.List;


/**
 * Represents t1 + t2
 * 
 * Intended as placeholder for special treatment for propagation rules and simplification rules
 * for +.
 * @author vj
 *
 */
public class XMod_c extends XFormula_c {


	public XMod_c(XTerm... args) {
		super(XTerms.modName, args);
	}
	public String toString() {
		return arguments.get(0) + "%" + arguments.get(1);
	}
}
