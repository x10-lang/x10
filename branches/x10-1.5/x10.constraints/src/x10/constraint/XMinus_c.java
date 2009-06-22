package x10.constraint;

import java.util.ArrayList;

/**
 * Represents t1 - t2
 * 
 * Intended as placeholder for special treatment for propagation rules and simplification rules
 * for -.
 * @author vj
 *
 */
public class XMinus_c extends XFormula_c {

	public XMinus_c(XTerm... args) {
		super(XTerms.minusName, args);
	}
	public String toString() {
		return arguments.get(0) + "-" + arguments.get(1);
	}
}
